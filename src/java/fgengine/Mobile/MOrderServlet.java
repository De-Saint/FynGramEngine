/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Mobile;

import com.google.gson.Gson;
import fgengine.Managers.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author mac
 */
public class MOrderServlet extends HttpServlet {

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
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
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
        processRequest(request, response);
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
            HttpSession session = request.getSession(true);
            String ans = "";
            String json = "";
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader br = request.getReader();
                String str = null;
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONParser parser = new JSONParser();
            JSONObject jsonParameter = null;
            try {
                jsonParameter = (JSONObject) parser.parse(sb.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String type = (String) jsonParameter.get("type");
            String json1 = "";
            String json2 = "";
            String json3 = "";
            String result = "";
            switch (type) {
                case "GetOrders": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    ArrayList<Integer> IDS = EngineOrderManager.GetOrderIDs(UserID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> Details = EngineOrderManager.GetMobileOrderData(id);
                            if (!Details.isEmpty()) {
                                list.add(Details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "Orders found.");

                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Orders found.");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetOrderDetails": {
                    String orderid = (String) jsonParameter.get("orderid");
                    int OrderID = Integer.parseInt(orderid);
                    HashMap<String, String> Details = EngineOrderManager.GetMobileOrderFullData(OrderID);
                    JSONObject datares = new JSONObject();
                    if (!Details.isEmpty()) {
                        datares.put("code", 200);
                        datares.put("msg", "Order Details Found.");
                        datares.put("data", Details);
                    } else {
                        datares.put("msg", "Something went wrong or your cart is empty. Please try again.");
                        datares.put("code", 400);
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "ReviewProduct": {
                    String productid = (String) jsonParameter.get("productid");
                    int ProductID = Integer.parseInt(productid);
                    String ratevalue = (String) jsonParameter.get("ratevalue");
                    double RateValue = Double.parseDouble(ratevalue);
                    String sessionid = (String) jsonParameter.get("sid");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    String Comment = (String) jsonParameter.get("comment");
                    result = EngineReviewManager.CreateReview(UserID, RateValue, ProductID, "Product", Comment);
                    JSONObject datares = new JSONObject();
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        datares.put("msg", "Your review has been logged successfully.");
                    } else {
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong. Please try again.");
                        }
                        datares.put("code", 400);
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "UpdateOrderStatus": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String orderID = (String) jsonParameter.get("orderid");
                    String StatusID = (String) jsonParameter.get("statusid");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    result = EngineOrderManager.ComputeOrderStatus(SessionID, orderID, StatusID);
                    JSONObject datares = new JSONObject();
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        datares.put("msg", "The order has been updated successfully.");
                    } else {
                        datares.put("code", 200);
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong. Please try again.");
                        }
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        } catch (Exception ex) {

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
