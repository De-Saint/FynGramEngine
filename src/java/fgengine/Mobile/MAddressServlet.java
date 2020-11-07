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
public class MAddressServlet extends HttpServlet {

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
                case "GetUserAddresses": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    ArrayList<Integer> IDS = EngineAddressManager.GetUserAddressIDs(UserID);
                    JSONObject datares = new JSONObject();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetAddressData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "Addresses found.");

                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Addresses found.");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetAddressTypes": {
                    ArrayList<Integer> IDS = EngineAddressManager.GetAddressTypeIDs();
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetAddressTypeData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "Address types found.");

                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Address types found.");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetStates": {
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    ArrayList<Integer> IDS = EngineAddressManager.GetStateIDs(157);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetStateNameData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "States found.");

                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No States found.");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetLGAs": {
                    String stateid = (String) jsonParameter.get("stateid");
                    int StateID = Integer.parseInt(stateid);
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    ArrayList<Integer> IDS = EngineAddressManager.GetLGAIDs(StateID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetLGANameData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "LGAs found.");

                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No LGAs found.");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetTowns": {
                    String lgaid = (String) jsonParameter.get("lgaid");
                    int LgaID = Integer.parseInt(lgaid);
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    ArrayList<Integer> IDS = EngineAddressManager.GetTownIDs(LgaID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetTownNameData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "Towns found.");

                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Towns found.");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetBusStops": {
                    String townid = (String) jsonParameter.get("townid");
                    int TownID = Integer.parseInt(townid);
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    ArrayList<Integer> IDS = EngineAddressManager.GetBusStopIDs(TownID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetBustopNameData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "BusStops found.");

                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No BusStops found.");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetStreets": {
                    String busstopid = (String) jsonParameter.get("busstopid");
                    int BusStopID = Integer.parseInt(busstopid);
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    ArrayList<Integer> IDS = EngineAddressManager.GetStreetIDs(BusStopID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineAddressManager.GetStreetNameData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "Streets found.");

                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Streets found.");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "DeleteMessage": {
                    String messageid = (String) jsonParameter.get("messageid");
                    int msgid = Integer.parseInt(messageid);
                    result = EngineMessageManager.DeleteMessage(msgid);
                    JSONObject datares = new JSONObject();
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        datares.put("msg", "The message has been deleted");
                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "Something went wrong! Please, try again!.");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "AddNewAddress": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    String addresstype = (String) jsonParameter.get("addresstype");
                    int AddressTypeID = Integer.parseInt(addresstype);
                    int CountryID = 157;
                    String stateid = (String) jsonParameter.get("state");
                    int StateID = Integer.parseInt(stateid);
                    String lgaid = (String) jsonParameter.get("lga");
                    int LgaID = Integer.parseInt(lgaid);
                    String townid = (String) jsonParameter.get("town");
                    int TownID = Integer.parseInt(townid);
                    int BusStopID = 0;
                    String BusStop = (String) jsonParameter.get("busstop");
                    if (EngineAddressManager.isNumeric(BusStop)) {
                        BusStopID = Integer.parseInt(BusStop);
                    } else {
                        BusStopID = EngineAddressManager.CreateBusStop(StateID, LgaID, TownID, BusStop);
                    }
                    String Street = (String) jsonParameter.get("street");
                    int StreetID = 0;
                    if (EngineAddressManager.isNumeric(Street)) {
                        StreetID = Integer.parseInt(Street);
                    } else {
                        StreetID = EngineAddressManager.CreateStreet(StateID, LgaID, TownID, BusStopID, Street);
                    }

                    String CloseTo = (String) jsonParameter.get("closeto");
                    String PostalCode = (String) jsonParameter.get("postal");
                    String AddressLine = (String) jsonParameter.get("housenumber");
                    String makedefault = (String) jsonParameter.get("defaultadd");
                    String Phone = (String) jsonParameter.get("phone");
                    int MakeDefault = Integer.parseInt(makedefault);
                    result = EngineAddressManager.ComputeAddress(UserID, AddressTypeID, CountryID, StateID, LgaID, TownID, BusStopID, StreetID, PostalCode, CloseTo, AddressLine, MakeDefault, Phone);

                    JSONObject datares = new JSONObject();
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        datares.put("msg", "You have successfully added a new address");
                    } else {
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong! Please, try again!.");
                        }
                        datares.put("code", 400);

                    }
                    json = new Gson().toJson(datares);

                    break;
                }
                case "DeleteUserAddresses": {
                    String addressid = (String) jsonParameter.get("addressid");
                    int AddressID = Integer.parseInt(addressid);
                    result = EngineAddressManager.DeleteAddressDetailByID(AddressID);
                    JSONObject datares = new JSONObject();
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        datares.put("msg", "You have successfully deleted the address.");
                    } else {
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong! Please, try again!.");
                        }
                        datares.put("code", 400);

                    }
                    json = new Gson().toJson(datares);
                    break;
                }

                case "MakeUserAddressDefault": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String UserID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    String addressid = (String) jsonParameter.get("addressid");
                    int AddressID = Integer.parseInt(addressid);
                    result = EngineAddressManager.SetDefaultAddress(UserID, AddressID);
                    JSONObject datares = new JSONObject();
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        datares.put("msg", "You have successfully deleted the address.");
                    } else {
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong! Please, try again!.");
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
