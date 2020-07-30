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
import org.json.simple.JSONObject;

/**
 *
 * @author Pinky
 */
public class WProductServlet extends HttpServlet {

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
                case "GetProductCondtions": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductCondtions();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetProductCondtionData(id);
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
                case "GetProperties": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineCategoryManager.GetPropertyIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineCategoryManager.GetPropertyData(id);
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
                case "GetStockNotifications": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetStockNotificationIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetStockNotificationData(id);
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
                case "GetUnits": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetUnitIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetUnitData(id);
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

                case "CreateProduct": {
                    String[] data = request.getParameterValues("data[]");
                    String option = data[0];
                    String sessionid = data[1];
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int SellerUserID = Integer.parseInt(SessionID);
                    String ProductName = data[2];
                    String Description = data[3];
                    String ReferenceCode = data[4];
                    String UPCBarcode = data[5];
                    String Condition = data[6];
                    int ProductConditionID = Integer.parseInt(Condition);
                    String sellingprice = data[7];
                    int SellingPrice = Integer.parseInt(sellingprice);
                    String costprice = data[8];
                    int CostPrice = Integer.parseInt(costprice);
                    String quantity = data[9];
                    int TotalQuantity = Integer.parseInt(quantity);
                    String quantitymin = data[10];
                    int MimimumQuantity = Integer.parseInt(quantitymin);
                    String selectedcatids = data[11];
                    String CategoryIDs = "";
                    if (!selectedcatids.isEmpty()) {
                        CategoryIDs = selectedcatids.replaceAll(":", ",");
                    }

                    String selectedproperties = data[12];
                    String PropertyIDs = "";
                    if (!selectedproperties.isEmpty()) {
                        PropertyIDs = selectedproperties.replaceAll(":", ",");
                    }
                    String unit = data[13];
                    int ProductUnitID = Integer.parseInt(unit);
                    String ProductUnitValue = data[14];
                    String tags = data[15];
                    String ProductTags = "";
                    if (!tags.isEmpty()) {
                        ProductTags = tags.replaceAll(":", ",");
                    }

                    String stocknotification = data[16];
                    int NotificationTypeID = Integer.parseInt(stocknotification);
                    String stockmin = data[17];
                    int MinimumStockLevel = Integer.parseInt(stockmin);
                    String shippingheight = data[18];
                    int PackageHeight = Integer.parseInt(shippingheight);
                    String shippingdepth = data[19];
                    int PackageDepth = Integer.parseInt(shippingdepth);
                    String shippingwidth = data[20];
                    int PackageWidth = Integer.parseInt(shippingwidth);
                    String optionText = "";
                    int productid = 0;
                    if (option.equals("addproduct")) {
                        productid = EngineProductManager.ComputeProduct(SellerUserID, ProductName, ProductConditionID, ProductUnitID, ReferenceCode,
                                UPCBarcode, Description, CategoryIDs, PropertyIDs, CostPrice, SellingPrice,
                                MimimumQuantity, TotalQuantity, PackageHeight, PackageWidth, PackageDepth,
                                MinimumStockLevel, NotificationTypeID, ProductUnitValue, ProductTags);
                        optionText = "Created";
                    } else if (option.equals("editproduct")) {
                        String prodid = data[20];
                        int ProductID = Integer.parseInt(prodid);
                        productid = EngineProductManager.EditProduct(SellerUserID, ProductName, ProductConditionID, ProductUnitID, ReferenceCode,
                                UPCBarcode, Description, CategoryIDs, PropertyIDs, CostPrice, SellingPrice,
                                MimimumQuantity, TotalQuantity, PackageHeight, PackageWidth, PackageDepth,
                                MinimumStockLevel, NotificationTypeID, ProductUnitValue, ProductTags, ProductID);
                        optionText = "Edited";
                    }

                    JsonObject returninfo = new JsonObject();
                    if (productid != 0) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The new product has been " + optionText + " successfully.");
                        returninfo.addProperty("newcreatedprodid", productid);
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Something went wrong. Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "GetProducts": {
                    String sessionid = request.getParameter("data");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductIDs(UserID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetProductData(id);
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
                case "GetProductDetails": {
                    String productid = request.getParameter("data");
                    int ProductID = Integer.parseInt(productid);
                    HashMap<String, String> details = EngineProductManager.GetProductData(ProductID);
                    JSONObject datares = new JSONObject();
                    datares.putAll(details);
                    json = new Gson().toJson(datares);
                    break;
                }
                case "ProcessProductStatus": {
                    String[] data = request.getParameterValues("data[]");
                    String productid = data[0];
                    int ProductID = Integer.parseInt(productid);
                    String Status = data[1]; //approved deact reject
                    String Option = data[3]; //bulk or single
                    String productids = data[4];
                    String Notes = data[2];
                    String ProductIDs = "";
                    String More = "";
                    if (!productids.isEmpty()) {
                        ProductIDs = productids.replaceAll(":", ",");
                        More = "The selected products have been ";
                    } else {
                        More = "The product has been";
                    }
                    JsonObject returninfo = new JsonObject();
                    result = EngineProductManager.ProcessProductStatus(ProductID, Status, Notes, Option, ProductIDs);
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", More + Status + " successfully.");
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
                case "ProcessProductUnit": {
                    String[] data = request.getParameterValues("data[]");
                    String unitid = data[0];
                    int UnitID = Integer.parseInt(unitid);
                    String Option = data[1]; //create edit delete
                    String Name = data[2];
                    String Description = data[3];
                    result = EngineProductManager.ProcessProductUnit(UnitID, Option, Name, Description);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "This product unit has been " + Option + " successfully.");
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
                case "ProcessProductCondition": {
                    String[] data = request.getParameterValues("data[]");
                    String unitid = data[0];
                    int UnitID = Integer.parseInt(unitid);
                    String Option = data[1]; //create edit delete
                    String Name = data[2];
                    result = EngineProductManager.ProcessProductCondition(UnitID, Option, Name);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The product condition has been " + Option + " successfully.");
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
                case "GetProductsByActive": {
                    String[] data = request.getParameterValues("data[]");
                    String value = data[0];
                    int ValueID = Integer.parseInt(value);
                    String sessionid = data[1];
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductIDsByActiveValue(UserID, ValueID, "Active");
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetProductData(id);
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
                case "GetProductsByIDs": {//[idmin, idmax, sessionid];
                    String[] data = request.getParameterValues("data[]");
                    String minid = data[0];
                    int MinID = Integer.parseInt(minid);
                    String maxid = data[1];
                    int MaxID = Integer.parseInt(maxid);
                    String sessionid = data[2];
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductIDsByIDs(UserID, MinID, MaxID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetProductData(id);
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
                case "GetShopProductsByCategoryID": {//[idmin, idmax, sessionid];
                    String catid = request.getParameter("data");
                    int CatID = Integer.parseInt(catid);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductsByCategoryID(CatID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetProductData(id);
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
                case "GetProductsByPrices": {//[idmin, idmax, sessionid];
                    String[] data = request.getParameterValues("data[]");
                    String minprice = data[0];
                    int MinPrice = Integer.parseInt(minprice);
                    String maxprice = data[1];
                    int MaxPrice = Integer.parseInt(maxprice);
                    String sessionid = data[2];
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductIDsByPrice(UserID, MinPrice, MaxPrice);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetProductData(id);
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
                case "GetProductsByQuantity": {//[idmin, idmax, sessionid];
                    String[] data = request.getParameterValues("data[]");
                    String minqty = data[0];
                    int MinQty = Integer.parseInt(minqty);
                    String maxqty = data[1];
                    int MaxQty = Integer.parseInt(maxqty);
                    String sessionid = data[2];
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductIDsByQuantity(UserID, MinQty, MaxQty);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetProductData(id);
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
                case "GetProductsByName": {//[idmin, idmax, sessionid];
                    String[] data = request.getParameterValues("data[]");
                    String Name = data[0];
                    String sessionid = data[1];
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductIDsByName(UserID, Name);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetProductData(id);
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
                case "GetProductsByCategoryID": {//[idmin, idmax, sessionid];
                    String[] data = request.getParameterValues("data[]");
                    String CatName = data[0];
                    String sessionid = data[1];
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductIDsByCategoryID(UserID, CatName);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetProductData(id);
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
                case "GetProductsByStatus": {//[idmin, idmax, sessionid];
                    String[] data = request.getParameterValues("data[]");
                    String Status = data[0];
                    String sessionid = data[1];
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductIDsByStatus(UserID, Status);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetProductData(id);
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
                case "GetProductsBySeller": {//[idmin, idmax, sessionid];
                    String[] data = request.getParameterValues("data[]");
                    String SellerName = data[0];
                    String sessionid = data[1];
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductIDsBySellerName(UserID, SellerName);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetProductData(id);
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
                case "GetShopCategoryPricesByCategoryID": {//[idmin, idmax, sessionid];
                    String catid = request.getParameter("data");
                    int CatID = Integer.parseInt(catid);
                    result = EngineProductManager.GetCategoryMinAndMaxPrice(CatID);
                    json = new Gson().toJson(result);
                    break;
                }
                case "GetShopProductsByPricesAndCategoryID": {//[idmin, idmax, sessionid];
                    String[] data = request.getParameterValues("data[]");
                    String minprice = data[0];
                    int MinPrice = Integer.parseInt(minprice);
                    String maxprice = data[1];
                    int MaxPrice = Integer.parseInt(maxprice);
                    String catid = data[2];
                    int CatID = Integer.parseInt(catid);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductsByPriceAndCatID(CatID, MinPrice, MaxPrice);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetProductData(id);
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
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(WProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(WProductServlet.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(WProductServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(WProductServlet.class.getName()).log(Level.SEVERE, null, ex);
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
