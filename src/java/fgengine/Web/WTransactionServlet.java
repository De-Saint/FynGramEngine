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
public class WTransactionServlet extends HttpServlet {

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
                case "GetRecentTransactions": {
                    String sessionid = request.getParameter("data");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    ArrayList<Integer> TransIDs = EngineTransactionManager.GetTransactionIDs(UserID);
                    HashMap<Integer, HashMap<String, String>> transactionDetailsList = new HashMap<>();
                    HashMap<String, String> transactiondetails = new HashMap<>();
                    if (!TransIDs.isEmpty()) {
                        for (int tID : TransIDs) {
                            transactiondetails = EngineTransactionManager.GetTransactionDetails(tID, UserID);
                            transactionDetailsList.put(tID, transactiondetails);
                        }
                        json1 = new Gson().toJson(TransIDs);
                        json2 = new Gson().toJson(transactionDetailsList);
                        json3 = new Gson().toJson(TransIDs.size());
                        json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }

                    break;
                }
                case "GetTransactionTypes": {
                    HashMap<Integer, HashMap<String, String>> DefinitionList = new HashMap<>();
                    ArrayList<Integer> DefIDS = new ArrayList<>();
                    DefIDS = EngineTransactionManager.GetTransactionTypeIDs();
                    if (!DefIDS.isEmpty()) {
                        for (int id : DefIDS) {
                            HashMap<String, String> defdetails = EngineTransactionManager.GetTransactionTypeData(id);
                            DefinitionList.put(id, defdetails);
                        }
                        json = new Gson().toJson(DefinitionList);
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }
                case "DeleteTransaction": {
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[0].trim();
                    String transactionid = data[1].trim();
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    int TransactionID = Integer.parseInt(transactionid);
                    result = EngineTransactionManager.DeleteTransaction(TransactionID);
                    ArrayList<Integer> IDs = EngineTransactionManager.GetTransactionIDs(UserID);
                    HashMap<Integer, HashMap<String, String>> DetailsList = new HashMap<>();
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The transaction has been deleted successfully.");
                        if (!IDs.isEmpty()) {
                            for (int ID : IDs) {
                                HashMap<String, String> details = EngineTransactionManager.GetTransactionDetails(ID, UserID);
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
            Logger.getLogger(WTransactionServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(WTransactionServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(WTransactionServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(WTransactionServlet.class.getName()).log(Level.SEVERE, null, ex);
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
