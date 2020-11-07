/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Mobile;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
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
public class MUserServlet extends HttpServlet {

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
                case "Login": {
                    String Email_PhoneNumber = (String) jsonParameter.get("emailphone");
                    String Password = (String) jsonParameter.get("password");
                    int UserID = 0;
                    String OldSessionID = (String) jsonParameter.get("oldsid");

                    JsonObject returninfo = new JsonObject();
                    if (EngineUserManager.checkEmailAddressOrPhoneNumberExist(Email_PhoneNumber)) {
                        UserID = EngineUserManager.checkPasswordEmailMatch(Password, Email_PhoneNumber);
                        if (UserID != 0) {
                            String NewSessionID = "";
                            int usertypeid = EngineUserManager.GetUserTypeIDByUserID("" + UserID);
                            String usertype = "";
                            switch (usertypeid) {
                                case 1:
                                    NewSessionID = session.getId() + "#A";
                                    usertype = "Admin";
                                    session.invalidate();
                                    break;
                                case 2:
                                    NewSessionID = session.getId() + "#S";
                                    usertype = "Seller";
                                    session.invalidate();
                                    break;
                                case 3:
                                    NewSessionID = session.getId() + "#C";
                                    usertype = "Customer";
                                    session.invalidate();
                                    break;
                                default:
                                    break;
                            }
                            EngineUserManager.UpdateUserSessionDetails(OldSessionID, NewSessionID, "" + UserID, "FynGramShop");
                            JsonObject dataobject = new JsonObject();
                            dataobject.addProperty("sid", NewSessionID);
                            dataobject.addProperty("usertype", usertype);
                            dataobject.addProperty("name", EngineUserManager.GetUserName(UserID));
                            dataobject.addProperty("email", EngineUserManager.GetUserEmail(UserID));
                            returninfo.add("data", dataobject);
                            returninfo.addProperty("code", 200);
                            returninfo.addProperty("msg", "Successful Login");
                        } else {
                            returninfo.addProperty("code", 400);
                            returninfo.addProperty("msg", "Incorrect Login Details.");
                        }
                    } else {
                        returninfo.addProperty("code", 400);
                        returninfo.addProperty("msg", "Email or Phone Number Entered Doesn't Exist.");
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "SaveGuest": {
                    String IPaddress = (String) jsonParameter.get("ipaddress");
                    String Location = (String) jsonParameter.get("location");
                    String sessionid = session.getId() + "#G";
                    EngineUserManager.ComputeGuest(sessionid, Location, IPaddress);
                    JsonObject returninfo = new JsonObject();
                    JsonObject dataobject = new JsonObject();
                    dataobject.addProperty("sid", sessionid);
                    dataobject.addProperty("usertype", "Guest");
                    dataobject.addProperty("name", "Guest");
                    returninfo.add("data", dataobject);
                    returninfo.addProperty("code", 200);
                    returninfo.addProperty("msg", "Guest User");
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "ResetPassword": {
                    String EmailAddress = (String) jsonParameter.get("email");
                    if (EngineUserManager.checkEmailAddressOrPhoneNumberExist(EmailAddress)) {
                        result = EngineUserManager.ComputeResetPassword(EmailAddress);
                    } else {
                        result = "The email provided does not exist. Please, try again.";
                    }
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("code", 200);
                        returninfo.addProperty("msg", "Please, check the email provided for verification code.");
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                        }
                        returninfo.addProperty("code", 400);
                    }

                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }

                case "PasswordRecovery": {
                    String RecoveryCode = (String) jsonParameter.get("code");
                    String NewPassword = (String) jsonParameter.get("password");
                    result = EngineUserManager.UpdateRecoveryPassword(RecoveryCode, NewPassword);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("code", 200);
                        returninfo.addProperty("msg", "Your Password reset was successful. Please try logging in with the new password.");
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                        }
                        returninfo.addProperty("code", 400);
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "RegisterCustomer": {
                    String Gender = (String) jsonParameter.get("gender");
                    String Frstname = (String) jsonParameter.get("firstname");
                    String Lastname = (String) jsonParameter.get("lastname");
                    String EmailAddress = (String) jsonParameter.get("email");
                    String PhoneNumber = (String) jsonParameter.get("phone");
                    String Password = (String) jsonParameter.get("password");
                    String title = (String) jsonParameter.get("title");
                    int CustomerUserID = 0;
                    JsonObject returninfo = new JsonObject();
                    if (!EngineUserManager.checkEmailAddressOrPhoneNumberExist(EmailAddress)) {
                        if (!EngineUserManager.checkEmailAddressOrPhoneNumberExist(PhoneNumber)) {
                            CustomerUserID = EngineUserManager.CreateUser(EmailAddress, PhoneNumber, Password, 3, 1, Gender, "", title);
                            if (CustomerUserID != 0) {
                                result = EngineUserManager.CreateCustomer(CustomerUserID, Frstname, Lastname);
                                if (result.equals("success")) {
                                    result = EngineWalletManager.CreateWallet(CustomerUserID);
                                    if (result.equals("success")) {
                                        String msgbdy = "Congratulations!!! \n\nYou have been successfully registered as a customer on Fyngram.";
                                        EngineMessageManager.sendMessage(1, msgbdy, "Customer Account Created", CustomerUserID);
                                        String Code = "FG-" + UtilityManager.randomAlphaNumeric(7) + "#C";
                                        EngineUserManager.CreateRecovery(CustomerUserID, EmailAddress, Code);
                                        EngineEmailManager.SendingEmailOption(EmailAddress, "Customer Account Created", Code, EngineUserManager.GetUserName(CustomerUserID), "Registration", "Customer");

                                        returninfo.addProperty("code", 200);
                                        returninfo.addProperty("msg", msgbdy);
                                    } else {
                                        returninfo.addProperty("code", 400);
                                        returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");
                                    }
                                } else {
                                    returninfo.addProperty("code", 400);
                                    returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");

                                }
                            } else {
                                returninfo.addProperty("code", 400);
                                returninfo.addProperty("msg", "Oh No! Something went wrong while creating User Account. Please try again.");

                            }
                        } else {
                            returninfo.addProperty("code", 400);
                            returninfo.addProperty("msg", "Oh No! An account with the same Phone Number already Exists. Please use another Phone Number.");

                        }
                    } else {
                        returninfo.addProperty("code", 400);
                        returninfo.addProperty("msg", "Oh No! An account with the same Email already Exists. Please use another Email.");

                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }
                case "ValidateAccount": {
                    String RecoveryCode = (String) jsonParameter.get("code");
                    String res = EngineUserManager.ConfirmAccount(RecoveryCode);
                    JsonObject returninfo = new JsonObject();
                    result = res.split("#")[0];
                    int UserID = Integer.parseInt(res.split("#")[1]);
                    if (result.equals("success")) {
                        returninfo.addProperty("code", 200);
                        JsonObject dataobject = new JsonObject();
                        dataobject.addProperty("email", EngineUserManager.GetUserEmail(UserID));
                        dataobject.addProperty("password", EngineUserManager.GetUserPasswordl(UserID));
                        returninfo.add("data", dataobject);
                        returninfo.addProperty("msg", "Your account has been confirmed. Thank you for being part of Fyngram.");
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong! Please, try again!");
                        }
                        returninfo.addProperty("code", 400);
                    }
                    json = new Gson().toJson((JsonElement) returninfo);
                    break;
                }

                case "GetBanks": {
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    ArrayList<Integer> IDS = EngineCashoutManager.GetBankIDs();
                    JSONObject datares = new JSONObject();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineCashoutManager.GetBankData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "Banks found.");

                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Banks found.");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "CreateBankDetails": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String bankid = (String) jsonParameter.get("bankid");
                    int BankID = Integer.parseInt(bankid);
                    String AccounType = (String) jsonParameter.get("accounttype");
                    String AccountNumber = (String) jsonParameter.get("accountnumber");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    result = EngineCashoutManager.CreateBankDetails(UserID, BankID, AccountNumber, AccounType);
                    JSONObject datares = new JSONObject();
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        datares.put("msg", "The New Bank Details has been added successfully.");
                    } else {
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong! Please, try again!");
                        }
                        datares.put("code", 400);
                        datares.put("msg", "No Banks found.");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetBankDetails": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    int BankDetailID = EngineCashoutManager.GetBankDetailsIDByUserID(UserID);
                    HashMap<String, String> details = EngineCashoutManager.GetBankDetailsData(BankDetailID);
                    JSONObject datares = new JSONObject();
                    if (!details.isEmpty()) {
                        datares.put("code", 200);
                        datares.put("msg", "Bank Details Found");
                        datares.put("data", details);

                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Bank Details found.");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }

                case "DeleteBankDetails": {
                    String bankdetid = (String) jsonParameter.get("bankdetid");
                    int BankdetailID = Integer.parseInt(bankdetid);
                    result = EngineCashoutManager.DeleteBankDetails(BankdetailID);
                    JSONObject datares = new JSONObject();
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        datares.put("msg", "The Bank Detail has been deleted successfully.");

                    } else {
                        datares.put("code", 400);
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong! Please, try again!");
                        }
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetWalletDetails": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    HashMap<String, String> data = EngineWalletManager.ComputeWalletDetails(UserID);
                    JSONObject datares = new JSONObject();
                    if (!data.isEmpty()) {
                        datares.put("code", 200);
                        datares.put("msg", "Account Details Found");
                        datares.put("data", data);

                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Account Details found.");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "NewCashoutRequest": {
                    String amount = (String) jsonParameter.get("amount");
                    String pin = (String) jsonParameter.get("pin");
                    String sessionid = (String) jsonParameter.get("sid");
                    double Amount = Double.parseDouble(amount);
                    int Pin = Integer.parseInt(pin);
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    int WalletPin = EngineWalletManager.GetUserWalletPIN(UserID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    if (Pin == WalletPin) {
                        result = EngineCashoutManager.ComputeCashOut(UserID, Amount);
                        if (result.equals("success")) {
                            datares.put("code", 200);
                            datares.put("msg", "The New Cashout Request has been logged and is pending approval.");

                        } else {
                            if (!result.equals("failed")) {
                                datares.put("msg", result);
                            } else {
                                datares.put("msg", "Something went wrong! Please, try again!");
                            }
                            datares.put("code", 400);
                        }
                    } else {
                        datares.put("msg", "Invalid FynPay Account Pin.");
                        datares.put("code", 400);
                    }

                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetCashoutRequests": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    ArrayList<Integer> IDS = EngineCashoutManager.GetCashOutIDs(UserID);
                    JSONObject datares = new JSONObject();
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineCashoutManager.GetCashOutData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("msg", "Requests Found");
                        datares.put("data", list);
                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Requests Found");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "ProcessCashOut": {
                    String Option = (String) jsonParameter.get("option");
                    String cashoutid = (String) jsonParameter.get("cashoutid");
                    int CashoutID = Integer.parseInt(cashoutid);
                    JSONObject datares = new JSONObject();
                    result = EngineCashoutManager.ProcessCashOut(CashoutID, Option);
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        datares.put("msg", "The cashout request has been " + Option);

                    } else {
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong! Please, try again!");
                        }
                        datares.put("code", 400);
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetCustomerDiscountCodes": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    JSONObject datares = new JSONObject();
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    ArrayList<Integer> IDS = EngineDiscountManager.GetCustomerDiscountCodeIDs(UserID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineDiscountManager.GetCustomerDiscountCodeData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("msg", "Discount Codes Found");
                        datares.put("data", list);
                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Discount Codes Found");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetDiscountCodes": {
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    ArrayList<Integer> IDS = EngineDiscountManager.GetDiscountCodeIDs();
                    JSONObject datares = new JSONObject();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineDiscountManager.GetDiscountCodeData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("msg", "Discount Codes Found");
                        datares.put("data", list);
                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Discount Codes Found");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "ProcessDiscount": {
                    String Option = (String) jsonParameter.get("option");
                    String discountid = (String) jsonParameter.get("discountid");
                    int DiscountCodeID = Integer.parseInt(discountid);
                    result = EngineDiscountManager.ProcessDiscountCode(Option, DiscountCodeID);
                    JSONObject datares = new JSONObject();
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        datares.put("msg", "The Discount Code record has been " + Option);

                    } else {
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong! Please, try again!");
                        }
                        datares.put("code", 400);
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
