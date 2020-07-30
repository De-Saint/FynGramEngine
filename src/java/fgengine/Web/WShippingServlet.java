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
public class WShippingServlet extends HttpServlet {

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
            throws ServletException, IOException, ClassNotFoundException, SQLException {
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
                case "NewShippingFees": {
                    String[] data = request.getParameterValues("data[]");
                    String shippingamt = data[0];
                    double ShippingAmt = Double.parseDouble(shippingamt);
                    String shippingcartmax = data[1];
                    double ShippingMaxCartAmt = Double.parseDouble(shippingcartmax);
                    String shippingcartmin = data[2];
                    double ShippingMinCartAmt = Double.parseDouble(shippingcartmin);
                    String option = data[3];
                    String action = data[5];
                    int Action = Integer.parseInt(action);
                    if (option.equals("add")) {
                        result = EngineShippingManager.CreateShippingFees(ShippingAmt, ShippingMaxCartAmt, ShippingMinCartAmt, Action);
                    } else if (option.equals("edit")) {
                        String shippingfeesid = data[4];
                        int ShippingFeesID = Integer.parseInt(shippingfeesid);
                        result = EngineShippingManager.EditShippingFees(ShippingFeesID, ShippingAmt);
                    }
                    HashMap<Integer, HashMap<String, String>> DetailsList = new HashMap<>();
                    JsonObject returninfo = new JsonObject();
                    ArrayList<Integer> IDs = EngineShippingManager.GetShippingFeesIDs();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The shipping fees has been added successfully.");
                        if (!IDs.isEmpty()) {
                            for (int ID : IDs) {
                                HashMap<String, String> details = EngineShippingManager.GetShippingFeesData(ID);
                                if (!details.isEmpty()) {
                                    DetailsList.put(ID, details);
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
                    json1 = new Gson().toJson(IDs);
                    json2 = new Gson().toJson(DetailsList);
                    json3 = new Gson().toJson(returninfo);
                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    break;
                }
                case "NewShippingAddress": {
                    String[] data = request.getParameterValues("data[]");
                    String Name = data[0];
                    String Interval = data[1];
                    String admin_percent = data[2];
                    String shipping_method_percent = data[3];
                    String shippingoption = data[4];
                    int AdminPercent = Integer.parseInt(admin_percent);
                    int ShipMethodPercent = Integer.parseInt(shipping_method_percent);
                    if (shippingoption.equals("add")) {
                        result = EngineShippingManager.CreateShipping(Name, Interval, AdminPercent, ShipMethodPercent);

                    } else if (shippingoption.equals("edit")) {
                        String shippingid = data[5];
                        int ShippingID = Integer.parseInt(shippingid);
                        result = EngineShippingManager.EditShipping(ShippingID, Name, Interval, AdminPercent, ShipMethodPercent);
                    }
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "New Shipping Method has been added successfully");
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "GetShippings": {
                    ArrayList<Integer> IDs = EngineShippingManager.GetShippingIDs();
                    HashMap<Integer, HashMap<String, String>> DetailsList = new HashMap<>();
                    HashMap<String, String> details = new HashMap<>();
                    if (!IDs.isEmpty()) {
                        for (int ID : IDs) {
                            details = EngineShippingManager.GetShippingData(ID);
                            DetailsList.put(ID, details);
                        }
                        json1 = new Gson().toJson(IDs);
                        json2 = new Gson().toJson(DetailsList);
                        json3 = new Gson().toJson(IDs.size());
                        json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetShippingFees": {
                    ArrayList<Integer> IDs = EngineShippingManager.GetShippingFeesIDs();
                    HashMap<Integer, HashMap<String, String>> DetailsList = new HashMap<>();
                    if (!IDs.isEmpty()) {
                        for (int ID : IDs) {
                            HashMap<String, String> details = EngineShippingManager.GetShippingFeesData(ID);
                            DetailsList.put(ID, details);
                        }
                        json1 = new Gson().toJson(IDs);
                        json2 = new Gson().toJson(DetailsList);
                        json3 = new Gson().toJson(IDs.size());
                        json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "DeleteShipping": {
                    String shippingid = request.getParameter("data");
                    int ShippingID = Integer.parseInt(shippingid);
                    result = EngineShippingManager.DeleteShipping(ShippingID);
                    HashMap<Integer, HashMap<String, String>> DetailsList = new HashMap<>();
                    JsonObject returninfo = new JsonObject();
                    ArrayList<Integer> IDs = EngineShippingManager.GetShippingIDs();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The shipping method has been deleted successfully.");
                        if (!IDs.isEmpty()) {
                            for (int ID : IDs) {
                                HashMap<String, String> details = EngineShippingManager.GetShippingData(ID);
                                if (!details.isEmpty()) {
                                    DetailsList.put(ID, details);
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
                    json1 = new Gson().toJson(IDs);
                    json2 = new Gson().toJson(DetailsList);
                    json3 = new Gson().toJson(returninfo);
                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    break;
                }
                case "DeleteShippingFees": {
                    String shippingfeesid = request.getParameter("data");
                    int ShippingFeesID = Integer.parseInt(shippingfeesid);
                    result = EngineShippingManager.DeleteShippingFees(ShippingFeesID);
                    HashMap<Integer, HashMap<String, String>> DetailsList = new HashMap<>();
                    JsonObject returninfo = new JsonObject();
                    ArrayList<Integer> IDs = EngineShippingManager.GetShippingFeesIDs();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The shipping fees has been deleted successfully.");
                        if (!IDs.isEmpty()) {
                            for (int ID : IDs) {
                                HashMap<String, String> details = EngineShippingManager.GetShippingFeesData(ID);
                                if (!details.isEmpty()) {
                                    DetailsList.put(ID, details);
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
                    json1 = new Gson().toJson(IDs);
                    json2 = new Gson().toJson(DetailsList);
                    json3 = new Gson().toJson(returninfo);
                    json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    break;
                }
//                case "GetShippingTypes": {
//                    ArrayList<Integer> IDs = EngineShippingManager.GetShippingTypes();
//                    HashMap<Integer, HashMap<String, String>> DetailsList = new HashMap<>();
//                    HashMap<String, String> details = new HashMap<>();
//                    if (!IDs.isEmpty()) {
//                        for (int ID : IDs) {
//                            details = EngineShippingManager.GetShippingTypeData(ID);
//                            DetailsList.put(ID, details);
//                        }
//                        json1 = new Gson().toJson(IDs);
//                        json2 = new Gson().toJson(DetailsList);
//                        json3 = new Gson().toJson(IDs.size());
//                        json = "[" + json1 + "," + json2 + "," + json3 + "]";
//                    } else {
//                        json = new Gson().toJson(empty);
//                    }
//                    break;
//                }
                case "GetPickUpStates": {

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
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(WShippingServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            processRequest(request, response);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(WShippingServlet.class.getName()).log(Level.SEVERE, null, ex);
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
