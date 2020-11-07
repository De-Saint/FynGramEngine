/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Mobile;

import com.google.gson.Gson;
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
public class MShopServlet extends HttpServlet {

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
                case "GetShopProductsByCategoryID": {
                    String catid = (String) jsonParameter.get("catid");
                    int CatID = Integer.parseInt(catid);
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    ArrayList<Integer> IDS = EngineProductManager.GetProductsByCategoryID(CatID);
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
                case "GetTopSellingProducts": {//[idmin, idmax, sessionid];
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    ArrayList<Integer> IDS = EngineProductManager.GetTopSellingProducts();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetMobileMiniProductData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "Top Products found.");
                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Top Products found");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetRecentlyAddedProducts": {//[idmin, idmax, sessionid];
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    ArrayList<Integer> IDS = EngineProductManager.GetRecentlyAddedProducts(6);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetMobileMiniProductData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "Recently Added Products found.");
                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Recently Added Products found");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetFeaturedProducts": {//[idmin, idmax, sessionid];
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    ArrayList<Integer> IDS = EngineProductManager.GetFeaturedProducts();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetMobileMiniProductData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "Featured Products found.");
                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Featured Products found");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetBestSellersProducts": {//[idmin, idmax, sessionid];
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    ArrayList<Integer> IDS = EngineProductManager.GetBestSellersProducts();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetMobileMiniProductData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "BestSellers Products found.");
                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No BestSellers Products found");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetRelatedProducts": {//[idmin, idmax, sessionid];
                    String productid = (String) jsonParameter.get("productid");
                    int ProductID = Integer.parseInt(productid);
                    ArrayList<HashMap<String, String>> list = new ArrayList<>();
                    JSONObject datares = new JSONObject();
                    ArrayList<Integer> IDS = EngineProductManager.GetRelatedProductsByCategoryID(ProductID);
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> details = EngineProductManager.GetMobileMiniProductData(id);
                            if (!details.isEmpty()) {
                                list.add(details);
                            }
                        }
                        datares.put("code", 200);
                        datares.put("data", list);
                        datares.put("msg", "Related Products found.");
                    } else {
                        datares.put("code", 400);
                        datares.put("msg", "No Related Products found");
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "AddOption": {
                    String productid = (String) jsonParameter.get("productid");
                    int ProductID = Integer.parseInt(productid);
                    String price = (String) jsonParameter.get("price");
                    double Price = Double.parseDouble(price);
                    double ProductPrice = 0.0;
                    String quantity = (String) jsonParameter.get("quantity");
                    int ProductQuantity = Integer.parseInt(quantity);
                    if (ProductQuantity > 1) {
                        ProductPrice = Price * ProductQuantity;
                    } else {
                        ProductPrice = Price;
                    }
                    String Action = (String) jsonParameter.get("action");
                    String sessionid = (String) jsonParameter.get("sid");
                    String UserID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    String Option = (String) jsonParameter.get("option");
                    JSONObject datares = new JSONObject();
                    if (Option.equals("Cart")) {
                        result = EngineCartManager.ComputeCart(UserID, ProductID, ProductPrice, ProductQuantity, Action);
                    } else if (Option.equals("SavedItems")) {
                        result = EngineCartManager.ComputeWishList(UserID, ProductID, ProductPrice, ProductQuantity, Action);
                    }
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        if (Option.equals("Cart")) {
                            datares.put("msg", "Product has been successfully added to your Cart.");
                        } else if (Option.equals("SavedItems")) {
                            datares.put("msg", "Product has been successfully added to your Saved Items.");
                        }
                    } else {
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong. Please try again.");
                        }
                        datares.put("code", 400);
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "UpdateOptions": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String UserID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    String Option = (String) jsonParameter.get("option");
                    String productid = (String) jsonParameter.get("productid");
                    int ProductID = Integer.parseInt(productid);
                    String price = (String) jsonParameter.get("price");
                    double Price = Double.parseDouble(price);
                    double ProductPrice = 0.0;
                    String quantity = (String) jsonParameter.get("quantity");
                    int ProductQuantity = Integer.parseInt(quantity);
                    if (ProductQuantity > 1) {
                        ProductPrice = Price * ProductQuantity;
                    } else {
                        ProductPrice = Price;
                    }
                    String Action = (String) jsonParameter.get("action");
                    if (Option.equals("Cart")) {
                        result = EngineCartManager.ComputeCart(UserID, ProductID, ProductPrice, ProductQuantity, Action);
                    } else if (Option.equals("SavedItems")) {
                        result = EngineCartManager.ComputeWishList(UserID, ProductID, ProductPrice, ProductQuantity, Action);
                    }
                    JSONObject datares = new JSONObject();
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        if (Option.equals("Cart")) {
                            datares.put("msg", "Product has been successfully updated in your Cart.");
                        } else if (Option.equals("SavedItems")) {
                            datares.put("msg", "Product has been successfully updated in your Saved Items.");
                        }
                    } else {
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong. Please try again.");
                        }
                        datares.put("code", 400);
                    }
                    json = new Gson().toJson(datares);
                    break;
                }

                case "GetShopCart": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String UserID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    HashMap<String, String> CartDetails = EngineCartManager.GetMobileCartDataByUserID(UserID);
                    JSONObject datares = new JSONObject();
                    if (!CartDetails.isEmpty()) {
                        datares.put("code", 200);
                        datares.put("msg", "Cart Found.");
                        datares.put("data", CartDetails);
                    } else {
                        datares.put("msg", "Something went wrong or your cart is empty. Please try again.");
                        datares.put("code", 400);
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "DeleteOptions": {
                    String Option = (String) jsonParameter.get("option");//CART OR WISHLIST
                    String optionid = (String) jsonParameter.get("optionid");
                    int OptionID = Integer.parseInt(optionid);
                    String productid = (String) jsonParameter.get("productid");
                    int ProductID = Integer.parseInt(productid);
                    if (Option.equals("Cart")) {
                        result = EngineCartManager.DeleteCartProduct(OptionID, ProductID);
                    } else if (Option.equals("SavedItems")) {
                        result = EngineCartManager.DeleteWishListProduct(OptionID, ProductID);
                    }
                    JSONObject datares = new JSONObject();
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        if (Option.equals("Cart")) {
                            datares.put("msg", "Product has been successfully deleted from your Cart.");
                        } else if (Option.equals("SavedItems")) {
                            datares.put("msg", "Product has been successfully deleted from Saved Items.");
                        }
                    } else {
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong. Please try again.");
                        }
                        datares.put("code", 400);
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "CartDiscountCode": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String UserID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    String DiscountCode = (String) jsonParameter.get("code");;
                    result = EngineCartManager.ComputeCartDiscountCode(UserID, DiscountCode);
                    JSONObject datares = new JSONObject();
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        datares.put("msg", "Discount Code has been added and your cart has been updated.");
                    } else {
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong. Please try again.");
                        }
                        datares.put("code", 400);
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetCartDefaultAddress": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    int addressid = EngineAddressManager.GetDefaultAddressDetailsIDByUserID("" + UserID);
                    HashMap<String, String> data = EngineAddressManager.GetAddressData(addressid);
                    JSONObject datares = new JSONObject();
                    if (!data.isEmpty()) {
                        datares.put("code", 200);
                        datares.put("msg", "Delivery address found.");
                        datares.put("data", data);
                    } else {
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong. Please try again.");
                        }
                        datares.put("code", 400);
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "PlaceOrder": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String PaymentType = (String) jsonParameter.get("paytype");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    String WalletNumber = EngineWalletManager.GetUserWalletNumber(UserID);
                    int CartID = EngineCartManager.GetCartIDByUserID("" + UserID);
                    String shippingtypeid = (String) jsonParameter.get("shiptypeid");
                    int ShippingTypeID = Integer.parseInt(shippingtypeid);
                    String shippingaddressid = (String) jsonParameter.get("addressid");
                    int ShippingAddressID = Integer.parseInt(shippingaddressid);
                    EngineCartManager.UpdateCartShippingAddressIDByCartID(CartID, ShippingAddressID);
                    EngineCartManager.UpdateCartShippingTypeIDByCartID(CartID, ShippingTypeID);
                    result = EngineOrderManager.ComputePlaceOrder(UserID, PaymentType, "", WalletNumber);
                    JSONObject datares = new JSONObject();
                    if (result.equals("success")) {
                        datares.put("code", 200);
                        datares.put("msg", "Your Payment was Successful and your order has been placed.");

                    } else {
                        if (!result.equals("failed")) {
                            datares.put("msg", result);
                        } else {
                            datares.put("msg", "Something went wrong. Please try again.");
                        }
                        datares.put("code", 400);
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "ValidatePaystackPayment": {
                    String sessionid = (String) jsonParameter.get("sid");
                    String actualamount = (String) jsonParameter.get("amount");
                    String RefereceCode = (String) jsonParameter.get("refcode");
                    String TransCode = (String) jsonParameter.get("tcode");
                    String PaymentType = (String) jsonParameter.get("paytype");

                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int UserID = Integer.parseInt(SessionID);
                    double Amount = Double.parseDouble(actualamount);
                    String message = "";

                    JSONObject datares = new JSONObject();
                    JSONObject jsonParseParameter = null;
                    String payresult = EnginePaystackManager.getInstance().PayStackPay(RefereceCode, 2);
                    try {
                        jsonParseParameter = (JSONObject) parser.parse(payresult);
                    } catch (Exception e) {
                        message = "Your payment validation was not successful, Please contact the support team if your bank account was debited and send the debit advise!";
                        datares.put("code", 400);
                        datares.put("msg", message);
                        e.printStackTrace();
                    }
                    String Status = jsonParseParameter.get("status").toString();
                    if (Status.equals("false")) {
                        message = "Your payment validation was not successful, Please contact the support team if your bank account was debited and send the debit advise!";
                        datares.put("code", 400);
                        datares.put("msg", message);
                    } else if (Status.equals("true")) {
                        if (PaymentType.equals("CheckOut Payment")) {
                            result = EnginePaymentManager.ComputePaymentWithCash(UserID, Amount, TransCode, RefereceCode, PaymentType);
                            if (result.equals("success")) {
                                int CartID = EngineCartManager.GetCartIDByUserID("" + UserID);
                                String shippingtypeid = (String) jsonParameter.get("shiptypeid");
                                int ShippingTypeID = Integer.parseInt(shippingtypeid);
                                String shippingaddressid = (String) jsonParameter.get("addressid");
                                int ShippingAddressID = Integer.parseInt(shippingaddressid);
                                EngineCartManager.UpdateCartShippingAddressIDByCartID(CartID, ShippingAddressID);
                                EngineCartManager.UpdateCartShippingTypeIDByCartID(CartID, ShippingTypeID);
                                result = EngineOrderManager.ComputePlaceOrder(UserID, "PayStack", "", RefereceCode);
                                if (result.equals("success")) {
                                    datares.put("code", 200);
                                    datares.put("msg", "Your Payment was Successful and your order has been placed.");

                                } else {
                                    if (!result.equals("failed")) {
                                        datares.put("msg", result);
                                    } else {
                                        datares.put("msg", "Something went wrong. Please try again or contact the support team for assistance");
                                    }
                                    datares.put("code", 400);
                                }
                            } else {
                                message = "An error occured while updating your FynPay Account, Please contact the support team if your bank account was debited and send the debit advise!";
                                datares.put("code", 400);
                                datares.put("msg", message);
                            }
                        } else if (PaymentType.equals("Fund Wallet")) {
                            result = EnginePaymentManager.ComputePaymentWithCash(UserID, Amount, TransCode, RefereceCode, PaymentType);
                            if (result.equals("success")) {
                                datares.put("code", 200);
                                datares.put("msg", "Your Payment was Successful. Please check your FynPay Account.");
                            } else {
                                if (!result.equals("failed")) {
                                    datares.put("msg", result);
                                } else {
                                    datares.put("msg", "Something went wrong. Please try again or contact the support team for assistance");
                                }
                                datares.put("code", 400);
                            }
                        }
                    }
                    json = new Gson().toJson(datares);
                    break;
                }
                case "GetPaystackKey": {
                    String key = EnginePaystackManager.GetPaystackPublicKey(2);
                    JSONObject datares = new JSONObject();
                    if (!key.equals("")) {
                        datares.put("code", 200);
                        datares.put("msg", "Key found.");
                        datares.put("data", key);
                    } else {
                        datares.put("msg", "Something went wrong. Please try again.");
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
