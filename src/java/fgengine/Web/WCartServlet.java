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
 * @author mac
 */
public class WCartServlet extends HttpServlet {

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
            throws ServletException, IOException, ClassNotFoundException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String json = "";
            String json1 = "";
            String json2 = "";
            String json3 = "";
            String type = request.getParameter("type").trim();
            String empty = "none";
            String result = "";
            switch (type) {
                case "AddOptions": {//[idmin, idmax, sessionid];
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[0];
                    String UserID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    String Option = data[1];
                    String productid = data[2];
                    int ProductID = Integer.parseInt(productid);
                    String price = data[3];
                    double Price = Double.parseDouble(price);
                    double ProductPrice = 0.0;
                    String quantity = data[4];
                    int ProductQuantity = Integer.parseInt(quantity);
                    if (ProductQuantity > 1) {
                        ProductPrice = Price * ProductQuantity;
                    } else {
                        ProductPrice = Price;
                    }
                    String Action = data[5];
                    HashMap<String, String> CartDetails = new HashMap<>();
                    JSONObject OptionDetails = new JSONObject();
                    JsonObject returninfo = new JsonObject();
                    if (Option.equals("Cart")) {
                        result = EngineCartManager.ComputeCart(UserID, ProductID, ProductPrice, ProductQuantity, Action);
                        HashMap<Integer, HashMap<String, String>> PropList = EngineProductManager.GetProductPropertyList(ProductID);
                        String productName = EngineProductManager.GetProductNameByProductID(ProductID);
                        int imageid = EngineImageManager.GetFirstImageID(ProductID, "Product");
                        String ImageText = EngineImageManager.GetImageTextByImageID(imageid);
                        CartDetails = EngineCartManager.GetCartDataByUserID(UserID);
                        OptionDetails.put("CartDetails", CartDetails);
                        CartDetails.put("productName", productName);
                        CartDetails.put("productPrice", price);
                        CartDetails.put("ImageText", ImageText);
                        OptionDetails.put("PropertyDetails", PropList);
                    } else if (Option.equals("SavedItems")) {
                        result = EngineCartManager.ComputeWishList(UserID, ProductID, ProductPrice, ProductQuantity, Action);
                        returninfo.addProperty("msg", "Product has been successfully added to your Saved Items.");
                    }
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    returninfo.addProperty("option", Option);
                    OptionDetails.put("result", returninfo);
                    json = new Gson().toJson(OptionDetails);

                    break;
                }
                case "GetShopCart": {
                    String sessionid = request.getParameter("data");
                    String UserID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    HashMap<String, String> CartDetails = EngineCartManager.GetFullCartDataByUserID(UserID);
                    json = new Gson().toJson(CartDetails);
                    break;
                }
                case "GetAllShopCarts": {
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = new ArrayList<>();
                    IDS = EngineCartManager.GetCartIDs();
                    if (!IDS.isEmpty()) {
                        for (int id : IDS) {
                            HashMap<String, String> CartDetails = EngineCartManager.GetFullCartDataByCartID(id);
                            if (!CartDetails.isEmpty()) {
                                List.put(id, CartDetails);
                            }
                        }
                        json1 = new Gson().toJson(IDS);
                        json2 = new Gson().toJson(List);
                        json3 = new Gson().toJson(IDS.size());
                        json = "[" + json1 + "," + json2 + "," + json3 + "]";
                    } else {
                        json = new Gson().toJson(empty);
                    }
                    break;
                }

                case "GetCartProductDetails": {
                    String cartid = request.getParameter("data");
                    int CartID = Integer.parseInt(cartid);
                    HashMap<Integer, HashMap<String, String>> CartProdDetList = EngineCartManager.GetCartProductDetailsList(CartID);
                    JSONObject CartProductDet = new JSONObject();
                    if (!CartProdDetList.isEmpty()) {
                        CartProductDet.put("CartProductDetails", CartProdDetList);
                    }
                    json = new Gson().toJson(CartProductDet);
                    break;
                }
                case "GetShopSavedItems": {
                    String sessionid = request.getParameter("data");
                    String UserID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    HashMap<String, String> WishListDetails = EngineCartManager.GetFullWishListDataByUseID(UserID);
                    json = new Gson().toJson(WishListDetails);
                    break;
                }
                case "UpdateOptions": {
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[0];
                    String UserID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    String Option = data[1];
                    String productid = data[2];
                    int ProductID = Integer.parseInt(productid);
                    String price = data[3];
                    double Price = Double.parseDouble(price);
                    double ProductPrice = 0.0;
                    String quantity = data[4];
                    int ProductQuantity = Integer.parseInt(quantity);
                    if (ProductQuantity > 1) {
                        ProductPrice = Price * ProductQuantity;
                    } else {
                        ProductPrice = Price;
                    }
                    String Action = data[5];
                    if (Option.equals("Cart")) {
                        result = EngineCartManager.ComputeCart(UserID, ProductID, ProductPrice, ProductQuantity, Action);
                    } else if (Option.equals("SavedItems")) {
                        result = EngineCartManager.ComputeWishList(UserID, ProductID, ProductPrice, ProductQuantity, Action);
                    }
                    HashMap<String, String> CartDetails = EngineCartManager.GetFullCartDataByUserID(UserID);
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "Product successfully updated in your shopping cart.");
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json1 = new Gson().toJson((JsonElement) returninfo);
                    json2 = new Gson().toJson(CartDetails);
                    json = "[" + json1 + "," + json2 + "]";

                    break;
                }

                case "DeleteOptions": {
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[0];
                    String UserID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    String Option = data[1];//CART OR WISHLIST
                    String optionid = data[2];
                    int OptionID = Integer.parseInt(optionid);
                    String productid = data[3];
                    int ProductID = Integer.parseInt(productid);
                    HashMap<String, String> Details = new HashMap<>();
                    if (Option.equals("Cart")) {
                        result = EngineCartManager.DeleteCartProduct(OptionID, ProductID);
                        Details = EngineCartManager.GetFullCartDataByUserID(UserID);
                    } else if (Option.equals("SavedItems")) {
                        result = EngineCartManager.DeleteWishListProduct(OptionID, ProductID);
                        Details = EngineCartManager.GetFullWishListDataByUseID(UserID);
                    }
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "Product successfully deleted.");
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    returninfo.addProperty("option", Option);
                    json1 = new Gson().toJson((JsonElement) returninfo);
                    json2 = new Gson().toJson(Details);
                    json = "[" + json1 + "," + json2 + "]";

                    break;
                }
                case "EmptyOptions": {
                    String[] data = request.getParameterValues("data[]");
                    String optionID = data[0];
                    String Option = data[1];
                    int OptionID = Integer.parseInt(optionID);
                    String OptionText = "";
                    if (Option.equals("Cart")) {
                        result = EngineCartManager.DeleteOrEmtpyCart(OptionID);
                        OptionText = " Your shopping cart is empty!";
                    } else if (Option.equals("SavedItems")) {
                        result = EngineCartManager.DeleteOrEmtpyWishList(OptionID);
                        OptionText = " Your Saved-Items List is empty!";
                    }
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", OptionText);
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    returninfo.addProperty("option", Option);
                    json = new Gson().toJson((JsonElement) returninfo);

                    break;
                }
                case "AddWishListProductToCart": {
                    String[] data = request.getParameterValues("data[]");
                    String sessionid = data[0];
                    String wishlistid = data[1];
                    String productid = data[2];
                    int WishListID = Integer.parseInt(wishlistid);
                    int ProductID = Integer.parseInt(productid);
                    String UserID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    result = EngineCartManager.AddWishListProductToCart(UserID, WishListID, ProductID);
                    HashMap<String, String> CartDetails = new HashMap<>();
                    JSONObject OptionDetails = new JSONObject();
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        HashMap<Integer, HashMap<String, String>> PropList = EngineProductManager.GetProductPropertyList(ProductID);
                        String productName = EngineProductManager.GetProductNameByProductID(ProductID);
                        int productPrice = EngineProductManager.GetProductSellingPrice(ProductID);
                        int imageid = EngineImageManager.GetFirstImageID(ProductID, "Product");
                        String ImageText = EngineImageManager.GetImageTextByImageID(imageid);
                        CartDetails = EngineCartManager.GetCartDataByUserID(UserID);
                        OptionDetails.put("CartDetails", CartDetails);
                        CartDetails.put("productName", productName);
                        CartDetails.put("productPrice", "" + productPrice);
                        CartDetails.put("ImageText", ImageText);
                        OptionDetails.put("PropertyDetails", PropList);
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    returninfo.addProperty("option", "Cart");
                    OptionDetails.put("result", returninfo);
                    json = new Gson().toJson(OptionDetails);
                    break;
                }
                case "BuyAllSavedItems": {
                    String sessionid = request.getParameter("data");
                    String UserID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    result = EngineCartManager.AddAllWishListProductsToCart(UserID);
                    JsonObject returninfo = new JsonObject();
                    HashMap<String, String> CartDetails = new HashMap<>();
                    JSONObject OptionDetails = new JSONObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "The product(s) has/have been successfully added to your shopping cart. Visit your cart page to view the product(s).");
                        CartDetails = EngineCartManager.GetCartDataByUserID(UserID);
                        OptionDetails.put("CartDetails", CartDetails);
                    } else {
                        returninfo.addProperty("status", "error");
                        returninfo.addProperty("msg", "Something went wrong. Please try again.");
                    }
                    OptionDetails.put("result", returninfo);
                    json = new Gson().toJson(OptionDetails);
                    break;
                }
                case "GetShopCartCount": {
                    String sessionid = request.getParameter("data");
                    String SessionID = EngineUserManager.GetLoginIDBySessionID(sessionid);
                    int count = EngineCartManager.GetCartProductCountByUserID(SessionID);
                    json = new Gson().toJson(count);
                    break;
                }
                case "DeleteCart": {
                    String cartid = request.getParameter("data");
                    int CartID = Integer.parseInt(cartid);
                    result = EngineCartManager.DeleteOrEmtpyCart(CartID);
                    HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
                    ArrayList<Integer> IDS = new ArrayList<>();
                    JsonObject returninfo = new JsonObject();
                    if (result.equals("success")) {
                        returninfo.addProperty("status", "success");
                        returninfo.addProperty("msg", "Cart successfully deleted.");

                        IDS = EngineCartManager.GetCartIDs();
                        if (!IDS.isEmpty()) {
                            for (int id : IDS) {
                                HashMap<String, String> CartDetails = EngineCartManager.GetFullCartDataByCartID(id);
                                if (!CartDetails.isEmpty()) {
                                    List.put(id, CartDetails);
                                }
                            }
                        }
                    } else {
                        if (!result.equals("failed")) {
                            returninfo.addProperty("msg", result);
                        } else {
                            returninfo.addProperty("msg", "Something went wrong. Please try again.");
                        }
                        returninfo.addProperty("status", "error");
                    }
                    json1 = new Gson().toJson(IDS);
                    json2 = new Gson().toJson(List);
                    json3 = new Gson().toJson(IDS.size());
                    String json4 = new Gson().toJson(returninfo);
                    json = "[" + json1 + "," + json2 + "," + json3 + "," + json4 + "]";

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
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(WCartServlet.class.getName()).log(Level.SEVERE, null, ex);
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
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(WCartServlet.class.getName()).log(Level.SEVERE, null, ex);
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
