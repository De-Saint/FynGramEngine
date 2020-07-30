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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author mac
 */
@WebServlet(name = "WImageServlet", urlPatterns = {"/WImageServlet"})
@MultipartConfig(maxFileSize = 16177215)    // upload file's size up to 16MB
public class WImageServlet extends HttpServlet {

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
            String json = "";
            String result = "";
            String ImageString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            JsonObject returninfo = new JsonObject();

            if (!ImageString.equals("")) {
                String objectid = ImageString.split("-")[0];
                String ObjectType = ImageString.split("-")[1];
                String ImageText = ImageString.split("-")[2];
                int ObjectID = 0;
                if (ObjectType.equals("Profile")) {
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(objectid);
                    ObjectID = Integer.parseInt(SessionID);
                } else {
                    ObjectID = Integer.parseInt(objectid);
                }
                result = EngineImageManager.CreateImage(ObjectID, ObjectType, ImageText);
                if (result.equals("success")) {
                    returninfo.addProperty("status", "success");
                    returninfo.addProperty("msg", "Image Upload was successful.");
                } else {
                    returninfo.addProperty("status", "error");
                    returninfo.addProperty("msg", "Image Upload could not be completed.");
                }
            } else {
                returninfo.addProperty("status", "error");
                returninfo.addProperty("msg", "Image Upload could not be completed.");
            }
            json = new Gson().toJson((JsonElement) returninfo);

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
            Logger.getLogger(WImageServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(WImageServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(WImageServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(WImageServlet.class.getName()).log(Level.SEVERE, null, ex);
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
