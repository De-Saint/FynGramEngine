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
import org.json.simple.JSONObject;

/**
 *
 * @author Pinky
 */
public class WSubscriptionServlet extends HttpServlet {

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
     * @throws java.text.ParseException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, ClassNotFoundException, SQLException, ParseException {
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
                case "GetSubscriptionTypes": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineSubscriptionManager.GetSubscriptionTypeIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineSubscriptionManager.GetSubscriptionTypeData(id);
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
                case "GetSubscriptionAmount": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineSubscriptionManager.GetSubscriptionAmountIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineSubscriptionManager.GetSubscriptionAmountData(id);
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
                case "GetSubscriptions": {
                    String sessionid = request.getParameter("data");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineSubscriptionManager.GetAllSubscriptionIDs(UserID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineSubscriptionManager.GetSubscriptionData(id);
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
                case "GetSellerTypes": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineSubscriptionManager.GetSellerTypeIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineSubscriptionManager.GetSellerTypeData(id);
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
                case "EditSubscriptionAmount": {
                    String[] data = request.getParameterValues("data[]");
                    String subamtid = data[0].trim();
                    String subnewamt = data[1].trim();
                    int SubAmtID = Integer.parseInt(subamtid);
                    int SubNewAmount = Integer.parseInt(subnewamt);
                    result = EngineSubscriptionManager.UpdateSellerSubscriptionAmount(SubAmtID, SubNewAmount);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The amount has been successfully updated..");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Error, something went wrong.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "NewSubscriptionType": {
                    String[] data = request.getParameterValues("data[]");
                    String Name = data[0].trim();
                    String Description = data[1].trim();
                    String duration = data[2].trim();
                    int Duration = Integer.parseInt(duration);
                    result = EngineSubscriptionManager.CreateSubscriptionType(Name, Description, Duration);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The subscription type has been successfully created..");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Error, something went wrong.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "NewSubscriptionAmount": {
                    String[] data = request.getParameterValues("data[]");
                    String sellertypeid = data[1].trim();
                    String subtypeid = data[2].trim();
                    String amount = data[0].trim();
                    int SellerTypeID = Integer.parseInt(sellertypeid);
                    int SubTpeID = Integer.parseInt(subtypeid);
                    double Amount = Double.parseDouble(amount);
                    result = EngineSubscriptionManager.CreateSubscriptionAmount(SellerTypeID, SubTpeID, Amount);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The subscription Amount has been successfully created..");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Error, something went wrong.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "DeleteSubscriptionAmount": {
                    String subamountid = request.getParameter("data");
                    int SubAmountID = Integer.parseInt(subamountid);
                    result = EngineSubscriptionManager.DeleteSubscriptionAmount(SubAmountID);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The subscription Amount has been successfully deleted..");
                    } else {
                        returninfo.addProperty("status", "error");
                        if (result.equals("failed")) {
                            returninfo.addProperty("msg", "Error, something went wrong.");
                        } else {
                            returninfo.addProperty("msg", result);
                        }
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "EnableOrDisableSubscriptionType": {
                    String[] data = request.getParameterValues("data[]");
                    String subtypeid = data[0].trim();
                    String Option = data[1].trim();
                    int SubTpeID = Integer.parseInt(subtypeid);
                    result = EngineSubscriptionManager.EnableOrDisableSubscriptionType(SubTpeID, Option);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The subscription Amount has been successfully " + Option + "..");
                    } else {
                        returninfo.addProperty("status", "error");
                        if (result.equals("failed")) {
                            returninfo.addProperty("msg", "Error, something went wrong.");
                        } else {
                            returninfo.addProperty("msg", result);
                        }
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "CreateSellerType": {
                    String[] data = request.getParameterValues("data[]");
                    String name = data[0].trim();
                    String adminpercentage = data[1].trim();
                    int AdminPercent = Integer.parseInt(adminpercentage);
                    result = EngineUserManager.CreateSellerType(name, AdminPercent);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The new Seller Type has been created successfully.");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! Something went wrong. Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "EditSellerType": {
                    String[] data = request.getParameterValues("data[]");
                    String sellertypeid = data[0].trim();
                    String name = data[1].trim();
                    String percent = data[2].trim();
                    int SellerTypeID = Integer.parseInt(sellertypeid);
                    int AdminPercent = Integer.parseInt(percent);
                    result = EngineUserManager.EditSellerType(SellerTypeID, name, AdminPercent);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The new Seller Type has been editted and saved successfully.");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! Something went wrong. Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "DeleteSellerType": {
                    String data = request.getParameter("data");
                    int STypeID = Integer.parseInt(data);
                    result = EngineUserManager.DeleteSellerType(STypeID);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The new Seller Type has been deleted successfully.");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! Something went wrong. Please try again.");
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
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            try {
                processRequest(request, response);
            } catch (ParseException ex) {
                Logger.getLogger(WSubscriptionServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(WSubscriptionServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            try {
                processRequest(request, response);
            } catch (ParseException ex) {
                Logger.getLogger(WSubscriptionServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(WSubscriptionServlet.class.getName()).log(Level.SEVERE, null, ex);
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
