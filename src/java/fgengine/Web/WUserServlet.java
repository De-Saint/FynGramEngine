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
 * @author mac
 */
public class WUserServlet extends HttpServlet {

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
            String json = "";
            String json1 = "";
            String json2 = "";
            String json3 = "";
            String type = request.getParameter("type").trim();
            String empty = "none";
            String result = "";
            switch (type) {
                case "Login": {
                    String[] data = request.getParameterValues("data[]");
                    String EmailAddress = data[0].trim();
                    String Password = data[1].trim();
                    String OldSessionID = data[2].trim();
                    String App = data[3].trim();
                    JsonObject returninfo = new JsonObject();
                    int UserID = 0;
                    if (EngineUserManager.checkEmailAddressOrPhoneNumberExist(EmailAddress)) {
                        UserID = EngineUserManager.checkPasswordEmailMatch(Password, EmailAddress);
                        if (UserID != 0) {
                            String NewSessionID = "";
                            int usertypeid = EngineUserManager.GetUserTypeIDByUserID("" + UserID);
                            String usertype = "";
                            session.invalidate();
                            session = request.getSession(true);
                            switch (usertypeid) {
                                case 1:
                                    NewSessionID = session.getId() + "#A";
                                    usertype = "Admin";
                                    break;
                                case 2:
                                    NewSessionID = session.getId() + "#S";
                                    usertype = "Seller";
                                    break;
                                case 3:
                                    NewSessionID = session.getId() + "#C";
                                    usertype = "Customer";
                                    break;
                                default:
                                    break;
                            }
                            EngineUserManager.UpdateUserSessionDetails(OldSessionID, NewSessionID, "" + UserID, App);
                            JsonObject dataobject = new JsonObject();
                            dataobject.addProperty("sessionid", NewSessionID);
                            dataobject.addProperty("sessiontype", usertype);
                            returninfo.add("data", dataobject);
                            returninfo.addProperty("status", "success");
                            returninfo.addProperty("msg", "Successful Login");
                        } else {
                            returninfo.addProperty("status", "error");
                            returninfo.addProperty("msg", "Incorrect Login Details.");
                        }
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Email or Phone Number Entered Doesn't Exist.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "GetUserDetails": {
                    String sessionid = request.getParameter("data");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<String, Object> data = EngineUserManager.GetUserDetails(UserID);
                    JSONObject datares = new JSONObject();
                    datares.putAll(data);
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetSearchUserDetails": {
                    String UserInput = request.getParameter("data");
                    HashMap<String, String> details = EngineUserManager.GetSearchResult(UserInput, 0);
                    JSONObject datares = new JSONObject();
                    datares.putAll(details);
                    json = new Gson().toJson(datares);
                    break;
                }
                case "SaveGuest": {
                    String[] data = request.getParameterValues("data[]");
                    String IPaddress = data[0].trim();
                    String Location = data[1].trim();
                    String sessionid = session.getId() + "#G";
                    EngineUserManager.ComputeGuest(sessionid, Location, IPaddress);
                    json = new Gson().toJson(sessionid);
                    break;
                }
                case "SubcribeNewletter": {
                    String[] data = request.getParameterValues("data[]");
                    String Email = data[0].trim();
                    String sessionid = data[1].trim();
                    if (sessionid.equals("")) {
                        sessionid = "" + session.getAttribute("sessionid");
                    }
                    String Option = sessionid.split("#")[1];
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int LoginID = Integer.parseInt(SessionID);
                    result = EngineUserManager.UpdateGuestEmail(LoginID, Email, Option);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "Your Email address has been added to the list. You will receive an Email from us shortly. Thank you for subscribing to our Newsletter...");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! It's our problem not yours. Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "RegisterCustomer": {
                    String[] data = request.getParameterValues("data[]");
                    String Gender = data[0].trim();
                    String Frstname = data[1].trim();
                    String Lastname = data[2].trim();
                    String EmailAddress = data[3].trim();
                    String PhoneNumber = data[4].trim();
                    String Password = data[5].trim();
                    String newsletter = data[6].trim();
                    String OldSessionID = data[7].trim();
                    int NewsLetter = Integer.parseInt(newsletter);
                    int CustomerUserID = 0;
                    JsonObject returninfo = new JsonObject();
                    if (!EngineUserManager.checkEmailAddressOrPhoneNumberExist(EmailAddress)) {
                        if (!EngineUserManager.checkEmailAddressOrPhoneNumberExist(PhoneNumber)) {
                            CustomerUserID = EngineUserManager.CreateUser(EmailAddress, PhoneNumber, Password, 3, NewsLetter, Gender, "");
                            if (CustomerUserID != 0) {
                                result = EngineUserManager.CreateCustomer(CustomerUserID, Frstname, Lastname);
                                if (result.equals("success")) {
                                    result = EngineWalletManager.CreateWallet(CustomerUserID);
                                    if (result.equals("success")) {
                                        String msgbdy = "Congratulations!!! \nYou have been successfully registered as a member of FynGram Online Store.";
                                        EngineMessageManager.sendMessage(1, msgbdy, "Customer Account Created", CustomerUserID);
//                                        EngineEmailManager.SendEmail(EmailAddress, msgbdy, "Customer Account Created");
                                        session.invalidate();
                                        session = request.getSession(true);
                                        String LoginID = EngineUserManager.GetLoginIDBySessionID(OldSessionID);
                                        String NewSessionID = session.getId() + "#C";
                                        EngineUserManager.CreateOrUpdateSessionID(OldSessionID, NewSessionID, LoginID, "" + CustomerUserID);
                                        EngineCartManager.UpdateCartUserID(LoginID, "" + CustomerUserID);
                                        returninfo.addProperty("status", "success");
                                        returninfo.addProperty("msg", msgbdy);
                                        JsonObject dataobject = new JsonObject();
                                        dataobject.addProperty("sessionid", NewSessionID);
                                        String usertype = "Customer";
                                        dataobject.addProperty("sessiontype", usertype);
                                        returninfo.add("data", dataobject);
                                        returninfo.addProperty("status", "success");
                                        returninfo.addProperty("msg", msgbdy);
                                    } else {
                                        returninfo.addProperty("status", "error");
                                        returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");
                                    }
                                } else {
                                    returninfo.addProperty("status", "error");
                                    returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");

                                }
                            } else {
                                returninfo.addProperty("status", "error");
                                returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");

                            }
                        } else {
                            returninfo.addProperty("status", "error");
                            returninfo.addProperty("msg", "Oh No! An account with the same Phone Number already Exists. Please use another Phone Number.");

                        }
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! An account with the same Email already Exists. Please use another Email.");

                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "GetAllCustomers": {
                    HashMap<Integer, HashMap<String, Object>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineUserManager.GetAllCustomerUsers();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, Object> details = EngineUserManager.GetUserDetails(id);
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
                case "SearchCustomers": {
                    String data = request.getParameter("data");
                    HashMap<Integer, HashMap<String, Object>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineUserManager.SearchCustomerUsers(data);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, Object> details = EngineUserManager.GetUserDetails(id);
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
                case "GetCustomerDetails": {
                    String customeruserid = request.getParameter("data");
                    int CustomerUserID = Integer.parseInt(customeruserid);
                    HashMap<String, Object> details = EngineUserManager.GetUserDetails(CustomerUserID);
                    JSONObject datares = new JSONObject();
                    datares.putAll(details);
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetAllSellers": {
                    HashMap<Integer, HashMap<String, Object>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineUserManager.GetAllSellerUsers();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, Object> details = EngineUserManager.GetUserDetails(id);
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
                case "SearchSellers": {
                    String data = request.getParameter("data");
                    HashMap<Integer, HashMap<String, Object>> List = new HashMap<>();
                    ArrayList<Integer> IDS = EngineUserManager.SearchSellerUsers(data);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, Object> details = EngineUserManager.GetUserDetails(id);
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
                case "GetSellerDetails": {
                    String selleruserid = request.getParameter("data");
                    int SellerUserID = Integer.parseInt(selleruserid);
                    HashMap<String, Object> details = EngineUserManager.GetUserDetails(SellerUserID);
                    JSONObject datares = new JSONObject();
                    datares.putAll(details);
                    json = new Gson().toJson(datares);
                    break;
                }
                case "ActivateSellerSubscrition": {
                    String selleruserid = request.getParameter("data");
                    int SellerUserID = Integer.parseInt(selleruserid);
                    result = EngineSubscriptionManager.ActivateSellerSubscription(SellerUserID);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "Your Email address has been added to the list. You will receive an Email from us shortly. Thank you for subscribing to our Newsletter...");
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! It's our problem not yours. Please try again.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "RegisterSeller": {
                    String[] data = request.getParameterValues("data[]");
                    String EmailAddress = data[0].trim();
                    String PhoneNumber = data[1].trim();
                    String Password = data[2].trim();
                    String Gender = data[3].trim();
                    String Frstname = data[4].trim();
                    String Lastname = data[5].trim();
                    String sellertypid = data[6].trim();
                    String subscriptiontypeid = data[7].trim();
                    String BizName = data[8].trim();
                    String BizEmail = data[9].trim();
                    String BizPhone = data[10].trim();
                    String minshippingdays = data[11].trim();
                    String maxshippingdays = data[12].trim();

                    int SellerUserID = 0;
                    JsonObject returninfo = new JsonObject();
                    if (!EngineUserManager.checkEmailAddressOrPhoneNumberExist(EmailAddress) || !EngineUserManager.checkEmailAddressOrPhoneNumberExist(BizName)) {
                        if (!EngineUserManager.checkEmailAddressOrPhoneNumberExist(PhoneNumber) || !EngineUserManager.checkEmailAddressOrPhoneNumberExist(BizPhone)) {
                            SellerUserID = EngineUserManager.CreateUser(EmailAddress, PhoneNumber, Password, 2, 1, Gender, "");
                            if (SellerUserID != 0) {
                                int SubscriptionTypeID = Integer.parseInt(subscriptiontypeid);
                                int SellerTypeID = Integer.parseInt(sellertypid);
                                result = EngineUserManager.CreateSeller(SellerUserID, Frstname, Lastname, SellerTypeID, SubscriptionTypeID);
                                if (result.equals("success")) {
                                    result = EngineWalletManager.CreateWallet(SellerUserID);
                                    if (result.equals("success")) {
                                        int MinShippingDays = Integer.parseInt(minshippingdays);
                                        int MaxShippingDays = Integer.parseInt(maxshippingdays);
                                        result = EngineUserManager.CreateSellerInformation(SellerUserID, BizName, BizEmail, BizPhone, MinShippingDays, MaxShippingDays);
                                        if (result.equals("success")) {
                                            String msgbdy = "Congratulations!!! \nYou have been successfully registered as a Seller on FynGram Online Store.";
                                            EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), msgbdy, "Seller Account Created", SellerUserID);
//                                              EngineEmailManager.SendEmail(EmailAddress, msgbdy, "Admin Account Created");

                                            String sessionid = session.getId() + "#S";
                                            String usertype = "Seller";
                                            EngineUserManager.CreateOrUpdateSessionID(sessionid, sessionid, "" + SellerUserID, "" + SellerUserID);
                                            JsonObject dataobject = new JsonObject();
                                            dataobject.addProperty("sessionid", sessionid);
                                            dataobject.addProperty("sessiontype", usertype);
                                            returninfo.add("data", dataobject);
                                            returninfo.addProperty("status", "success");
                                            returninfo.addProperty("msg", msgbdy);
                                        } else {
                                            returninfo.addProperty("status", "error");
                                            returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");
                                        }
                                    } else {
                                        returninfo.addProperty("status", "error");
                                        returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");
                                    }
                                } else {
                                    returninfo.addProperty("status", "error");
                                    returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");
                                }
                            } else {
                                returninfo.addProperty("status", "error");
                                returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");

                            }
                        } else {
                            returninfo.addProperty("status", "error");
                            returninfo.addProperty("msg", "Oh No! An account with the same Phone Number already Exists. Please use another Phone Number.");

                        }
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Oh No! An account with the same Email already Exists. Please use another Email.");

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
            Logger.getLogger(WUserServlet.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(WUserServlet.class.getName()).log(Level.SEVERE, null, ex);
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
