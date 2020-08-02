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
public class WDiscountCodeServlet extends HttpServlet {

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
            switch (type) {
                case "GetDiscountTypes": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineDiscountManager.GetDiscountTypeIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineDiscountManager.GetDiscountTypeData(id);
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
                case "GetDeductionTypes": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineDiscountManager.GetDeductionTypeIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineDiscountManager.GetDeductionTypeData(id);
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
                case "GetDiscountObject": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineDiscountManager.GetDiscountObjectIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineDiscountManager.GetDiscountObjectData(id);
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
                case "GetDiscountCodes": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineDiscountManager.GetDiscountCodeIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineDiscountManager.GetDiscountCodeData(id);
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
                case "GetCustomerDiscountCodes": {
                    String sessionid = request.getParameter("data");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineDiscountManager.GetCustomerDiscountCodeIDs(UserID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineDiscountManager.GetCustomerDiscountCodeData(id);
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
                case "NewDiscountCode": {
                    String[] data = request.getParameterValues("data[]");
                    String Name = data[0].trim();
                    String Description = data[1].trim();
                    String DeductionType = data[2].trim();
                    int DeductionTypeID = Integer.parseInt(DeductionType);
                    String Deductionvalue = data[3].trim();
                    int DeductionValue = Integer.parseInt(Deductionvalue);
                    String Object = data[4].trim();
                    int ObjectID = Integer.parseInt(Object);
                    String StartDate = data[5].trim();
                    String ExpiryDate = data[6].trim();
                    String Type = data[7].trim();
                    int TypeID = Integer.parseInt(Type);
                    String CustomerID = data[8].trim();
                    int CustomerUserID = Integer.parseInt(CustomerID);
                    String totalPerCustomer = data[9];
                    int TotalPerCustomer = Integer.parseInt(totalPerCustomer);
                    String splitDeductionValue = data[9];
                    int SplitDeductionValue = Integer.parseInt(splitDeductionValue);
                    
                    result = EngineDiscountManager.ComputeDiscountCode(Name, Description, TypeID, ObjectID, DeductionTypeID, DeductionValue, StartDate, ExpiryDate, CustomerUserID, TotalPerCustomer, SplitDeductionValue);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The new discount code has been created successfully.");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "ProcessDiscount": {
                    String[] data = request.getParameterValues("data[]");
                    String Option = data[0].trim();
                    String discountid = data[1].trim();
                    int DiscountCodeID = Integer.parseInt(discountid);
                    result = EngineDiscountManager.ProcessDiscountCode(Option, DiscountCodeID);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The Discount Code record has been " + Option);
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "CreateDiscountCodeDeductionType": {
                    String[] data = request.getParameterValues("data[]");
                    String name = data[0].trim();
                    String description = data[1].trim();
                    result = EngineDiscountManager.CreateDiscountCodeDeductionType(name, description);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The Discount Code record has been created succesfully");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "CreateDiscountCodeType": {
                    String name = request.getParameter("data");
                    result = EngineDiscountManager.CreateDiscountCodeType(name);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The Discount Code Type has been created succesfully");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "DeleteDeductionType": {
                    String id = request.getParameter("data");
                    int DeductionTypeID = Integer.parseInt(id);
                    result = EngineDiscountManager.DeleteDeductionType(DeductionTypeID);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The Discount Code Type has been created succesfully");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "DeleteDiscountCodeType": {
                    String id = request.getParameter("data");
                    int DiscountCodeTypeID = Integer.parseInt(id);
                    result = EngineDiscountManager.DeleteDiscountCodeType(DiscountCodeTypeID);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The Discount Code Type has been created succesfully");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Something went wrong! Please, try again!");
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
            Logger.getLogger(WDiscountCodeServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(WDiscountCodeServlet.class.getName()).log(Level.SEVERE, null, ex);
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
