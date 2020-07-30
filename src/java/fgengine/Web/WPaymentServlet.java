/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Web;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fgengine.Managers.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Pinky
 */
public class WPaymentServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     * @throws java.io.UnsupportedEncodingException
     * @throws java.text.ParseException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            HttpSession session = request.getSession(true);
            String temp = "" + session.getAttribute("Id");
            String json = "";
            String json1 = "";
            String json2 = "";
            String json3 = "";
            String type = request.getParameter("type").trim();
            String empty = "none";
            String result = "";
            switch (type) {// [sessionid, actualamount, response.reference, response.trans, PaymentType];
                case "ValidatePaystackPayment": {
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[0].trim();
                    String actualamount = data[1].trim();
                    String RefereceCode = data[2].trim();
                    String TransCode = data[3].trim();
                    String PaymentType = data[4].trim();

                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    int Amount = Integer.parseInt(actualamount);
                    String message = "";
                    String payresult = EnginePaystackManager.getInstance().PayStackPay(RefereceCode);
                    JSONParser parser = new JSONParser();
                    JSONObject jsonParameter = null;
                    JsonObject returninfo = new JsonObject();
                    try {
                        jsonParameter = (JSONObject) parser.parse(payresult);
                    } catch (Exception e) {
                        message = "Your payment validation was not successful, Please contact the admin if your account was debited and send prove of payment!";
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", message);
                        json = new Gson().toJson((JsonElement) returninfo);
                        e.printStackTrace();
                    }
                    String Status = jsonParameter.get("status").toString();
                    if (Status.equals("false")) {
                        message = "Your payment validation was not successful, Please contact the admin if your account was debited and send prove of payment!";
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", message);
                        json = new Gson().toJson((JsonElement) returninfo);
                    } else if (Status.equals("true")) {
                        JSONObject datares = new JSONObject();
                        if (PaymentType.equals("Fund Wallet")) {
                            result = EnginePaymentManager.ComputePaymentWithCash(UserID, Amount, TransCode, RefereceCode, PaymentType);
                            if (result.equals("success")) {
                                returninfo.addProperty("status", "success");
                                returninfo.addProperty("msg", "Your Payment was Successful. Please check your wallet.");
                                HashMap<String, String> paymentdata = EngineWalletManager.ComputeWalletDetails(UserID);
                                datares.put("paymentdata", paymentdata);
                            } else {
                                returninfo.addProperty("status", "error");
                                returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                            }
                            datares.put("result", returninfo);
                            json = new Gson().toJson(datares);
                        } else if (PaymentType.equals("Subscription Fees")) {
                            result = EnginePaymentManager.ComputeSubscriptionFees(UserID, PaymentType, Amount, TransCode, RefereceCode);
                            if (result.equals("success")) {
                                returninfo.addProperty("status", "success");
                                returninfo.addProperty("msg", "Your Payment was Successful. Please check your wallet.");
                                HashMap<String, Object> userdata = EngineUserManager.GetUserDetails(UserID);
                                datares.put("userdata", userdata);
                            } else {
                                if (!result.equals("failed")) {
                                    returninfo.addProperty("msg", result);
                                } else {
                                    returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                                }
                                returninfo.addProperty("status", "error");
                            }
                            datares.put("result", returninfo);

                            json = new Gson().toJson(datares);
                        }
                    }
                    break;
                }
                case "GetPayments": {
                    String sessionid = request.getParameter("data");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    ArrayList<Integer> PayIDs = EnginePaymentManager.GetPaymentIDs(UserID);
                    HashMap<Integer, HashMap<String, String>> DetailsList = new HashMap<>();
                    HashMap<String, String> paydetails = new HashMap<>();
                    if (!PayIDs.isEmpty()) {
                        for (int payID : PayIDs) {
                            paydetails = EnginePaymentManager.GetPaymentsData(payID);
                            if (!paydetails.isEmpty()) {
                                DetailsList.put(payID, paydetails);
                            }
                        }
                        json1 = new Gson().toJson(PayIDs);
                        json2 = new Gson().toJson(DetailsList);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "DeletePayment": {
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[0].trim();
                    String paymentid = data[1].trim();
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    int PaymentID = Integer.parseInt(paymentid);
                    result = EnginePaymentManager.DeletePayment(PaymentID);
                    ArrayList<Integer> PayIDs = EnginePaymentManager.GetPaymentIDs(UserID);
                    HashMap<Integer, HashMap<String, String>> DetailsList = new HashMap<>();
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The payment has been deleted successfully.");
                        if (!PayIDs.isEmpty()) {
                            for (int payID : PayIDs) {
                                HashMap<String, String> paydetails = EnginePaymentManager.GetPaymentsData(payID);
                                if (!paydetails.isEmpty()) {
                                    DetailsList.put(payID, paydetails);
                                }
                            }
                        }
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json1 = new Gson().toJson(PayIDs);
                    json2 = new Gson().toJson(DetailsList);
                    json3 = new Gson().toJson(returninfo);
                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    break;
                }
            }

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.io.UnsupportedEncodingException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, UnsupportedEncodingException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException | SQLException | ParseException ex) {
            Logger.getLogger(WPaymentServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.io.UnsupportedEncodingException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, UnsupportedEncodingException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException | SQLException | ParseException ex) {
            Logger.getLogger(WPaymentServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
