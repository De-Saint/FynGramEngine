/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Web;

import com.google.gson.Gson;
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
import org.json.simple.JSONObject;

/**
 *
 * @author Pinky
 */
public class WOrderServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
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
                case "PlaceOrder": {
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[0].trim();
                    String PaymentType = data[1].trim();
                    String Note = data[2].trim();
                    JsonObject returninfo = new JsonObject();
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    String WalletNumber = EngineWalletManager.GetUserWalletNumber(UserID);
                    result = EngineOrderManager.ComputePlaceOrder(UserID, PaymentType, Note, WalletNumber);
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "Your Payment was Successful and your order has been placed.");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                    }
                    json = new Gson().toJson(returninfo);
                    break;
                }
                case "GetOrderDetails": {
                    String orderid = request.getParameter("data");
                    int OrderID = Integer.parseInt(orderid);
                    HashMap<String, String> Details = EngineOrderManager.GetOrderFullData(OrderID);
                    JSONObject OrderDetails = new JSONObject();
                    if (!Details.isEmpty()) {
                        OrderDetails.put("OrderDetails", Details);
                    }
                    json = new Gson().toJson(OrderDetails);
                    break;
                }
                case "GetOrderCancelRule": {
                    HashMap<String, String> Details = EngineOrderManager.GetCancelOrderRuleData();
                    JSONObject OrderDetails = new JSONObject();
                    OrderDetails.put("rules", Details);
                    json = new Gson().toJson(OrderDetails);
                    break;
                }
                case "GetOrders": {//admin getting all carts
                    String sessionid = request.getParameter("data");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = new ArrayList<>();
                    IDS = EngineOrderManager.GetOrderIDs(UserID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> Details = EngineOrderManager.GetOrderData(id);
                            if (!Details.isEmpty()) {
                                List.put(id, Details);
                            }
                        }
                        json1 = new Gson().toJson(IDS);
                        json2 = new Gson().toJson(List);
                        json3 = new Gson().toJson(IDS.size());
                        json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetOrderStatus": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = new ArrayList<>();
                    IDS = EngineOrderManager.GetOrderStatusIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> Details = EngineOrderManager.GetOrderStatusData(id);
                            if (!Details.isEmpty()) {
                                List.put(id, Details);
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
                case "GetOrderStatusHistory": {
                    String sessionid = request.getParameter("data");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = new ArrayList<>();
                    IDS = EngineOrderManager.GetOrderIDs(UserID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> Details = EngineOrderManager.GetOrderData(id);
                            if (!Details.isEmpty()) {
                                List.put(id, Details);
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
                case "AssignShippingMethod": {
                    String[] data = request.getParameterValues("data[]");
                    String orderid = data[0].trim();
                    String shippingmethodid = data[1].trim();
                    int OrderID = Integer.parseInt(orderid);
                    int ShippingMethodId = Integer.parseInt(shippingmethodid);
                    result = EngineOrderManager.ComputeAssignShippingMethodToOrder(OrderID, ShippingMethodId);
                    JsonObject returninfo = new JsonObject();
                    HashMap<String, String> Details = new HashMap<>();
                    JSONObject OrderDetails = new JSONObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The shipping method has been successfully assigned to the order. You can proceed to ship the order.");
                        Details = EngineOrderManager.GetOrderFullData(OrderID);
                        OrderDetails.put("OrderDetails", Details);
                    } else {
                        returninfo.addProperty("status", "error");
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                    }
                    OrderDetails.put("result", returninfo);
                    json = new Gson().toJson(OrderDetails);
                    break;
                }
                case "UpdateEnforceCancelFees": {
                    String[] data = request.getParameterValues("data[]");
                    String amount = data[0].trim();
                    String rule = data[1].trim();
                    double Amount = Double.parseDouble(amount);
                    int Rule = Integer.parseInt(rule);
                    result = EngineOrderManager.UpdateEnforceCancelFees(Amount, Rule);
                    JsonObject returninfo = new JsonObject();
                    HashMap<String, String> Details = new HashMap<>();
                    JSONObject OrderDetails = new JSONObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The cancellation rule has been updated successfully.");
                        Details = EngineOrderManager.GetCancelOrderRuleData();
                        OrderDetails.put("rules", Details);
                    } else {
                        returninfo.addProperty("status", "error");
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                    }
                    OrderDetails.put("result", returninfo);
                    json = new Gson().toJson(OrderDetails);
                    break;
                }
                case "UpdateOrderStatus": {
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[2].trim();
                    String orderID = data[0].trim();
                    String StatusID = data[1].trim();
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    result = EngineOrderManager.ComputeOrderStatus(SessionID, orderID, StatusID);
                    JsonObject returninfo = new JsonObject();
                    HashMap<String, String> Details = new HashMap<>();
                    JSONObject OrderDetails = new JSONObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The order has been updated successfully.");
                        int OrderID = Integer.parseInt(orderID);
                        Details = EngineOrderManager.GetOrderFullData(OrderID);
                        OrderDetails.put("OrderDetails", Details);
                    } else {
                        returninfo.addProperty("status", "error");
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                    }
                    OrderDetails.put("result", returninfo);
                    json = new Gson().toJson(OrderDetails);
                    break;
                }
                case "UpdateSellerPayment": {
                    result = EngineOrderManager.UpdateSellerPayment();
                    json = new Gson().toJson(result);
                    break;
                }
                case "TrackOrder": {
                    String reference = request.getParameter("data");
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineOrderManager.GetOrderIDsByReferenceNumber(reference);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> Details = EngineOrderManager.GetOrderData(id);
                            if (!Details.isEmpty()) {
                                List.put(id, Details);
                            }
                        }
                         
                        json1 = new Gson().toJson(IDS);
                        json2 = new Gson().toJson(List);
                        json3 = new Gson().toJson(IDS.size());
                        json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                 case "GetDashBoardOrders": {
                    String sessionid = request.getParameter("data");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = new ArrayList<>();
                    IDS = EngineOrderManager.GetDashBoardOrders(UserID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> Details = EngineOrderManager.GetOrderData(id);
                            if (!Details.isEmpty()) {
                                List.put(id, Details);
                            }
                        }
                        json1 = new Gson().toJson(IDS);
                        json2 = new Gson().toJson(List);
                        json3 = new Gson().toJson(IDS.size());
                        json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
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
            processRequest(request, response);
        } catch (ClassNotFoundException | SQLException | UnsupportedEncodingException | ParseException ex) {
            Logger.getLogger(WOrderServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(WOrderServlet.class.getName()).log(Level.SEVERE, null, ex);
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
