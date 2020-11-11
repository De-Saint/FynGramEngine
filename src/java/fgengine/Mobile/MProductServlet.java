/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Mobile;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
public class MProductServlet extends HttpServlet {

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
                case "GetRootCategories": {
                    ArrayList<Integer> IDS = EngineCategoryManager.GetRootCategoryIDs();
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineCategoryManager.GetCategoryData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "Categories found.");

                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No categories found");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetProducts": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductIDs(UserID);
                    JSONObject datares = new JSONObject();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetMobileMiniProductData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "Products found.");

                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Products found");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetProductsByName": {
                    String searchvalue = (String) jsonParameter.get("searchvalue");
                    String sessionid = (String) jsonParameter.get("sid");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductIDsByName(UserID, searchvalue);
                    JSONObject datares = new JSONObject();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetMobileMiniProductData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "Products found.");

                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Products found");
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
