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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Pinky
 */
public class WCashOutServlet extends HttpServlet {

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
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
            switch (type) {
                case "NewCashoutRequest": {
                    String[] data = request.getParameterValues("data[]");
                    String amount = data[0].trim();
                    String pin = data[1].trim();
                    String sessionid = data[2].trim();
                    double Amount = Double.parseDouble(amount);
                    int Pin = Integer.parseInt(pin);
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    int WalletPin = EngineWalletManager.GetUserWalletPIN(UserID);
                    JsonObject returninfo = new JsonObject();
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = new ArrayList<>();
                    if (Pin == WalletPin) {
                        result = EngineCashoutManager.ComputeCashOut(UserID, Amount);
                        if (result.equals("success")) {
                            returninfo.addProperty("status", "success");
                            returninfo.addProperty("msg", "The New Cashout Request has been logged and is pending approval.");
                            IDS = EngineCashoutManager.GetCashOutIDs(UserID);
                            if (!IDS.isEmpty()) {
                                for (int id : IDS) {
                                    HashMap<String, String> details = EngineCashoutManager.GetCashOutData(id);
                                    if (!details.isEmpty()) {
                                        List.put(id, details);
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
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Invalid FynPay Account Pin!");
                    }

                    json1 = new Gson().toJson(IDS);
                    json2 = new Gson().toJson(List);
                    json3 = new Gson().toJson(returninfo);
                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    break;
                }
                case "GetBanks": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineCashoutManager.GetBankIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineCashoutManager.GetBankData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        json = new Gson().toJson(List);
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetBankDetails": {
                    String sessionid = request.getParameter("data");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    int BankDetailID = EngineCashoutManager.GetBankDetailsIDByUserID(UserID);
                    HashMap<String, String> details = EngineCashoutManager.GetBankDetailsData(BankDetailID);
                    if (!details.isEmpty()) {
                        json = new Gson().toJson(details);
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "CreateBankDetails": {
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[0].trim();
                    String bankid = data[1].trim();
                    int BankID = Integer.parseInt(bankid);
                    String AccounType = data[2].trim();
                    String AccountNumber = data[3].trim();
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    result = EngineCashoutManager.CreateBankDetails(UserID, BankID, AccountNumber, AccounType);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The New Bank Details has been added successfully.");
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "EditBankDetails": {
                    String[] data = request.getParameterValues("data[]");
                    String bankid = data[0].trim();
                    int BankID = Integer.parseInt(bankid);
                    String AccounType = data[1].trim();
                    String AccountNumber = data[2].trim();
                    String bankdetid = data[3].trim();
                    int BankDetailID = Integer.parseInt(bankdetid);
                    result = EngineCashoutManager.EditBankDetails(BankDetailID, BankID, AccountNumber, AccounType);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The Bank Details has been updated successfully.");
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "GetCashoutRequests": {
                    String sessionid = request.getParameter("data");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineCashoutManager.GetCashOutIDs(UserID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineCashoutManager.GetCashOutData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        json1 = new Gson().toJson(IDS);
                        json2 = new Gson().toJson(List);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "DeleteBankDetails": {
                    String bankdetid = request.getParameter("data");
                    int BankdetailID = Integer.parseInt(bankdetid);
                    result = EngineCashoutManager.DeleteBankDetails(BankdetailID);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The Bank Detail has been deleted successfully.");
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "ProcessCashOut": {
                    String[] data = request.getParameterValues("data[]");
                    String Option = data[0].trim();
                    String cashoutid = data[1].trim();
                    int CashoutID = Integer.parseInt(cashoutid);
                    String sessionid = data[2].trim();
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    result = EngineCashoutManager.ProcessCashOut(CashoutID, Option);
                    JsonObject returninfo = new JsonObject();
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = new ArrayList<>();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The cashout request has been " + Option);
                        IDS = EngineCashoutManager.GetCashOutIDs(UserID);
                        if (!IDS.isEmpty()) {
                            for (int id : IDS) {
                                HashMap<String, String> details = EngineCashoutManager.GetCashOutData(id);
                                if (!details.isEmpty()) {
                                    List.put(id, details);
                                }
                            }
                        }
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                    }
                    json1 = new Gson().toJson(IDS);
                    json2 = new Gson().toJson(List);
                    json3 = new Gson().toJson(returninfo);
                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    break;
                }
                case "AddBank": {
                    String bankname = request.getParameter("data");
                    result = EngineCashoutManager.CreateBank(bankname);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The new bank  has been added successfully");
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "DeleteBank": {
                    String bankid = request.getParameter("data");
                    int BankID = Integer.parseInt(bankid);
                    result = EngineCashoutManager.DeleteBank(BankID);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The bank  has been deleted successfully");
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
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
            Logger.getLogger(WCashOutServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(WCashOutServlet.class.getName()).log(Level.SEVERE, null, ex);
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
