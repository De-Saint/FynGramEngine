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
                    result = EngineOrderManager.ComputePlaceOrder(UserID, PaymentType, Note,WalletNumber);
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
                case "EditNewUserAddress": {

                    break;
                }
                case "DeleteUserAddress": {

                    break;
                }
                case "GetPickUpCentres": {

                    break;
                }
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
