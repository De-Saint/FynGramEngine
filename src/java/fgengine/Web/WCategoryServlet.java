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
import org.json.simple.JSONObject;

/**
 *
 * @author Pinky
 */
public class WCategoryServlet extends HttpServlet {

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
                case "GetAllLevelCategories": {
                    ArrayList<Integer> topcatids = EngineCategoryManager.GetRootCategoryIDs();
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    HashMap<Integer, ArrayList<HashMap<String, String>>> Categories = new HashMap<>();
                    HashMap<Integer, ArrayList<HashMap<String, String>>> SubCategories = new HashMap<>();
                    if (!topcatids.isEmpty()) {
                        for (int topcatid : topcatids) {
                            HashMap<String, String> topcatdetails = EngineCategoryManager.GetCategoryData(topcatid);
                            if (!topcatdetails.isEmpty()) {
                                ArrayList<HashMap<String, String>> CatIds = new ArrayList<>();
                                List.put(topcatid, topcatdetails);
                                ArrayList<Integer> catids = EngineCategoryManager.GetParentCategoryIDs(topcatid);
                                if (catids.isEmpty()) {
                                    continue;
                                }
                                for (int catid : catids) {
                                    HashMap<String, String> catdetails = EngineCategoryManager.GetCategoryData(catid);
                                    if (!catdetails.isEmpty()) {
                                        CatIds.add(catdetails);
                                        ArrayList<HashMap<String, String>> SubCatIds = new ArrayList<>();
                                        ArrayList<Integer> subids = EngineCategoryManager.GetParentCategoryIDs(catid);
                                        if (subids.isEmpty()) {
                                            continue;
                                        }
                                        for (int subid : subids) {
                                            HashMap<String, String> subdetails = EngineCategoryManager.GetCategoryData(subid);
                                            if (!subdetails.isEmpty()) {
                                                SubCatIds.add(subdetails);
                                            }
                                        }
                                        SubCategories.put(catid, SubCatIds);
                                    }
                                }
                                Categories.put(topcatid, CatIds);
                            }
                        }
                        json1 = new Gson().toJson(List);
                        json2 = new Gson().toJson(Categories);
                        json3 = new Gson().toJson(SubCategories);
                        json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetTwoLevelsCategories": {
                    ArrayList<Integer> rootcatids = EngineCategoryManager.GetRootCategoryIDs();
                    HashMap<String, HashMap<String, String>> catList = new HashMap<>();
                    HashMap<Integer, Object> RootCatParIDs = new HashMap<>();
                    if (!rootcatids.isEmpty()) {
                        for (int rootcatid : rootcatids) {
                            HashMap<String, String> topcatdetails = EngineCategoryManager.GetCategoryData(rootcatid);
                            String Topcatid = "root" + rootcatid;
                            catList.put(Topcatid, topcatdetails);
                            ArrayList<Integer> parcatids = new ArrayList<>();
                            parcatids = EngineCategoryManager.GetParentCategoryIDs(rootcatid);
                            if (!parcatids.isEmpty()) {
                                for (int parcatid : parcatids) {
                                    HashMap<String, String> parcatdetails = EngineCategoryManager.GetCategoryData(parcatid);
                                    if (!parcatdetails.isEmpty()) {
                                        String ParCatid = "par" + parcatid;
                                        catList.put(ParCatid, parcatdetails);
                                    }
                                }
                            }
                            RootCatParIDs.put(rootcatid, parcatids);
                        }
                        json1 = new Gson().toJson(catList);
                        json2 = new Gson().toJson(RootCatParIDs);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetRootCategories": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineCategoryManager.GetRootCategoryIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineCategoryManager.GetCategoryData(id);
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
                case "GetParentCategories": {
                    String rootcatid = request.getParameter("data");
                    int RootCatID = Integer.parseInt(rootcatid);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineCategoryManager.GetParentCategoryIDs(RootCatID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineCategoryManager.GetCategoryData(id);
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
                case "GetAllCategories": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineCategoryManager.GetCategoryIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineCategoryManager.GetCategoryData(id);
                            if (!details.isEmpty()) {
                                List.put(id, details);
                            }
                        }
                        int TotalCatCount = EngineCategoryManager.GetTotalCategoryCount();
                        int TotalRootCatCount = EngineCategoryManager.GetRootCategoryCount();
                        int TotalParCatCount = EngineCategoryManager.GetParentCategoryCount();
                        json1 = new Gson().toJson(IDS);
                        json2 = new Gson().toJson(List);
                        json3 = new Gson().toJson(TotalCatCount);
                        String json4 = new Gson().toJson(TotalRootCatCount);
                        String json5 = new Gson().toJson(TotalParCatCount);
                        json = "[" + json1 + "," + json2 + "," + json3 + "," + json4 + "," + json5 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetProperties": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineCategoryManager.GetPropertyRootIDs();
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
                case "GetParentProperties": {
                    String rootpropid = request.getParameter("data");
                    int RootPropID = Integer.parseInt(rootpropid);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineCategoryManager.GetParentPropertyIDs(RootPropID);
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
                case "CreateCategory": {
                    String[] data = request.getParameterValues("data[]");
                    String name = data[0];
                    String isrootcat = data[1];
                    String selectedcatid = data[2];
                    String desc = data[3];
                    String cattags = data[4];
                    String Propertiesids = data[5];
                    String PropertiesIDs = "";
                    if (!Propertiesids.isEmpty()) {
                        PropertiesIDs = Propertiesids.replaceAll(":", ",");
                    }

                    int ParentID = Integer.parseInt(selectedcatid);
                    int IsRootID = Integer.parseInt(isrootcat);
                    String tags = cattags.replaceAll(":", ",");
                    String option = data[6];
                    int catid = 0;
                    String optionText = "";
                    if (option.equals("add")) {
                        catid = EngineCategoryManager.ComputeCategory(name, desc, ParentID, IsRootID, tags, PropertiesIDs);
                        optionText = "created";
                    } else if (option.equals("edit")) {
                        String cid = data[7];
                        int CatID = Integer.parseInt(cid);
                        catid = EngineCategoryManager.EditCategory(CatID, name, desc, ParentID, IsRootID, tags, PropertiesIDs);
                        optionText = "edited";
                    }
                    JsonObject returninfo = new JsonObject();
                    if (catid != 0) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The category has been " + optionText + " successfully.");
                        returninfo.addProperty("newcreatedcatid", catid);
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Something went wrong. Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "DeleteCategory": {
                    String data = request.getParameter("data");
                    int CatID = Integer.parseInt(data);
                    result = EngineCategoryManager.DeleteCategory(CatID);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The category has been created successfully.");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Something went wrong. Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "CreateProperty": {
                    String[] data = request.getParameterValues("data[]");
                    String name = data[0];
                    String isrootprop = data[1];
                    String selectedpropid = data[2];
                    int ParentID = Integer.parseInt(selectedpropid);
                    int IsRootID = Integer.parseInt(isrootprop);
                    String option = data[3];
                    String optionText = "";
                    if (option.equals("add")) {
                        result = EngineCategoryManager.CreateProperties(name, ParentID, IsRootID);
                        optionText = "created";
                    } else if (option.equals("edit")) {
                        String propid = data[4];
                        int PropID = Integer.parseInt(propid);
                        result = EngineCategoryManager.EditProperties(PropID, name, ParentID, IsRootID);
                        optionText = "edited";
                    }
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The property has been " + optionText + " successfully.");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Something went wrong. Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "DeleteProperties": {
                    String data = request.getParameter("data");
                    int PropID = Integer.parseInt(data);
                    result = EngineCategoryManager.DeleteProperties(PropID);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The property has been created successfully.");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Something went wrong. Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "GetAllProperties": {
                    ArrayList<Integer> rootpropids = EngineCategoryManager.GetPropertyRootIDs();
                    HashMap<String, HashMap<String, String>> propList = new HashMap<>();
                    HashMap<Integer, Object> RootPropParIDs = new HashMap<>();
                    if (!rootpropids.isEmpty()) {
                        for (int rootpropid : rootpropids) {
                            HashMap<String, String> topcatdetails = EngineCategoryManager.GetPropertyData(rootpropid);
                            String Toppropid = "root" + rootpropid;
                            propList.put(Toppropid, topcatdetails);
                            ArrayList<Integer> parcatids = new ArrayList<>();
                            parcatids = EngineCategoryManager.GetParentPropertyIDs(rootpropid);
                            if (!parcatids.isEmpty()) {
                                for (int parcatid : parcatids) {
                                    HashMap<String, String> parcatdetails = EngineCategoryManager.GetPropertyData(parcatid);
                                    if (!parcatdetails.isEmpty()) {
                                        String ParCatid = "par" + parcatid;
                                        propList.put(ParCatid, parcatdetails);
                                    }
                                }
                            }
                            RootPropParIDs.put(rootpropid, parcatids);
                        }
                        json1 = new Gson().toJson(propList);
                        json2 = new Gson().toJson(RootPropParIDs);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetAllProductCategories": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineCategoryManager.GetCategoryIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineCategoryManager.GetCategoryData(id);
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
                case "GetShopAllLevelCategories": {
                    ArrayList<Integer> topcatids = EngineCategoryManager.GetRootCategoryIDsWithLimit(0, 8);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    HashMap<Integer, ArrayList<HashMap<String, String>>> Categories = new HashMap<>();
                    HashMap<Integer, ArrayList<HashMap<String, String>>> SubCategories = new HashMap<>();
                    if (!topcatids.isEmpty()) {
                        for (int topcatid : topcatids) {
                            HashMap<String, String> topcatdetails = EngineCategoryManager.GetCategoryData(topcatid);
                            if (!topcatdetails.isEmpty()) {
                                ArrayList<HashMap<String, String>> CatIds = new ArrayList<>();
                                List.put(topcatid, topcatdetails);
                                ArrayList<Integer> catids = EngineCategoryManager.GetParentCategoryIDsWithLimit(topcatid, 0, 3);
                                if (catids.isEmpty()) {
                                    continue;
                                }
                                for (int catid : catids) {
                                    HashMap<String, String> catdetails = EngineCategoryManager.GetCategoryData(catid);
                                    if (!catdetails.isEmpty()) {
                                        CatIds.add(catdetails);
                                        ArrayList<HashMap<String, String>> SubCatIds = new ArrayList<>();
                                        ArrayList<Integer> subids = EngineCategoryManager.GetParentCategoryIDsWithLimit(catid, 0, 3);
                                        if (subids.isEmpty()) {
                                            continue;
                                        }
                                        for (int subid : subids) {
                                            HashMap<String, String> subdetails = EngineCategoryManager.GetCategoryData(subid);
                                            if (!subdetails.isEmpty()) {
                                                SubCatIds.add(subdetails);
                                            }
                                        }
                                        SubCategories.put(catid, SubCatIds);
                                    }
                                }
                                Categories.put(topcatid, CatIds);
                            }
                        }
                        json1 = new Gson().toJson(List);
                        json2 = new Gson().toJson(Categories);
                        json3 = new Gson().toJson(SubCategories);
                        json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "GetShopCategoriesByCategoryID": {
                    String catid = request.getParameter("data");
                    int CatID = Integer.parseInt(catid);
                    String CatName = EngineCategoryManager.GetCategoryName(CatID);
                    int RootCatID = EngineCategoryManager.GetCategoryRootIDByCategoryID(CatID);
                    String RootCatName = EngineCategoryManager.GetCategoryName(RootCatID);
                    int ParentID = EngineCategoryManager.GetParentID(CatID);
                    String ParCatName = EngineCategoryManager.GetCategoryName(ParentID);
                    ArrayList<Integer> rootcatids = EngineCategoryManager.GetParentCategoryIDs(CatID);
                    HashMap<String, HashMap<String, String>> catList = new HashMap<>();
                    HashMap<Integer, Object> RootCatParIDs = new HashMap<>();
                    if (!rootcatids.isEmpty()) {
                        for (int rootcatid : rootcatids) {
                            HashMap<String, String> topcatdetails = EngineCategoryManager.GetCategoryData(rootcatid);
                            String Topcatid = "root" + rootcatid;
                            catList.put(Topcatid, topcatdetails);
                            ArrayList<Integer> parcatids = new ArrayList<>();
                            parcatids = EngineCategoryManager.GetParentCategoryIDs(rootcatid);
                            if (!parcatids.isEmpty()) {
                                for (int parcatid : parcatids) {
                                    HashMap<String, String> parcatdetails = EngineCategoryManager.GetCategoryData(parcatid);
                                    if (!parcatdetails.isEmpty()) {
                                        String ParCatid = "par" + parcatid;
                                        catList.put(ParCatid, parcatdetails);
                                    }
                                }
                            }
                            RootCatParIDs.put(rootcatid, parcatids);
                        }
                        json1 = new Gson().toJson(catList);
                        json2 = new Gson().toJson(RootCatParIDs);
                        json3 = new Gson().toJson(CatName + "=" + CatID);
                        String json4 = new Gson().toJson(ParCatName + "=" + ParentID);
                        String json5 = new Gson().toJson(RootCatName + "=" + RootCatID);
                        json = "[" + json1 + "," + json2 + "," + json3 + "," + json4 + "," + json5 + "]";
                    } else {
                        json1 = new Gson().toJson(empty);
                        json2 = new Gson().toJson(empty);
                        json3 = new Gson().toJson(CatName + "=" + CatID);
                        String json4 = new Gson().toJson(ParCatName + "=" + ParentID);
                        String json5 = new Gson().toJson(RootCatName + "=" + RootCatID);
                        json = "[" + json1 + "," + json2 + "," + json3 + "," + json4 + "," + json5 + "]";
                    }
                    break;
                }
                case "GetShopMobileRootCategories": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineCategoryManager.GetRootCategoryIDsWithLimit(0, 8);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineCategoryManager.GetCategoryData(id);
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
                case "GetCategoryDetails": {
                    String categoryid = request.getParameter("data");
                    int CategoryID = Integer.parseInt(categoryid);
                    HashMap<String, String> details = EngineCategoryManager.GetCategoryData(CategoryID);
                    JSONObject datares = new JSONObject();
                    datares.putAll(details);
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetPropertyDetails": {
                    String propertyid = request.getParameter("data");
                    int PropertyID = Integer.parseInt(propertyid);
                    HashMap<String, String> details = EngineCategoryManager.GetPropertyData(PropertyID);
                    JSONObject datares = new JSONObject();
                    datares.putAll(details);
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetShopPropertiesByCategoryID": {
                    String catid = request.getParameter("data");
                    int CatID = Integer.parseInt(catid);
                    ArrayList<Integer> rootcatids = EngineCategoryManager.GetRootPropertyIDsByCategoryID(CatID);
                    HashMap<String, HashMap<String, String>> catList = new HashMap<>();
                    HashMap<Integer, Object> RootCatParIDs = new HashMap<>();
                    if (!rootcatids.isEmpty()) {
                        for (int rootcatid : rootcatids) {
                            HashMap<String, String> topcatdetails = EngineCategoryManager.GetPropertyData(rootcatid);
                            String Topcatid = "root" + rootcatid;
                            catList.put(Topcatid, topcatdetails);
                            ArrayList<Integer> parcatids = EngineCategoryManager.GetPropertyIDsByRootPropertyID(CatID, rootcatid);
                            if (!parcatids.isEmpty()) {
                                for (int parcatid : parcatids) {
                                    HashMap<String, String> parcatdetails = EngineCategoryManager.GetPropertyData(parcatid);
                                    if (!parcatdetails.isEmpty()) {
                                        String ParCatid = "par" + parcatid;
                                        catList.put(ParCatid, parcatdetails);
                                    }
                                }
                            }
                            RootCatParIDs.put(rootcatid, parcatids);
                        }
                        json1 = new Gson().toJson(catList);
                        json2 = new Gson().toJson(RootCatParIDs);
                        json = "[" + json1 + "," + json2 + "]";
                    } else {
                        json1 = new Gson().toJson(empty);
                        json2 = new Gson().toJson(empty);
                        json = "[" + json1 + "," + json2 + "]";
                    }
                    break;
                }
                case "GetShopTagsByCategoryID": {
                    String catid = request.getParameter("data");
                    int CatID = Integer.parseInt(catid);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineCategoryManager.GetTags(CatID, "Category");
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineCategoryManager.GetTagData(id);
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
     * @throws java.io.UnsupportedEncodingException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, UnsupportedEncodingException {
        try {
            processRequest(request, response);
        } catch (ClassNotFoundException | SQLException | ParseException ex) {
            Logger.getLogger(WCategoryServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(WCategoryServlet.class.getName()).log(Level.SEVERE, null, ex);
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
