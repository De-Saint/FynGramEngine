/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import fgengine.Tables.Tables;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 *
 * @author mac
 */
public class EngineOrderManager {

    /**
     *
     * @param UserID
     * @param CartID
     * @param PaymentMethod
     * @param Message
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputePlaceOrder(int UserID, String PaymentMethod, String Message, String PaymentReference) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        int CartID = EngineCartManager.GetCartIDByUserID("" + UserID);
        HashMap<String, String> CartData = EngineCartManager.GetCartDataByUserID("" + UserID);
        if (!CartData.isEmpty()) {
            try {
                String Shippingtypeid = CartData.get(Tables.OrdersTable.ShippingTypeID);
                int ShippingTypeID = Integer.parseInt(Shippingtypeid);
                double OrderAmount = Double.parseDouble(CartData.get(Tables.CartTable.Amount));
                double TotalAmount = Double.parseDouble(CartData.get(Tables.CartTable.TotalAmount));
                String shippingAddressid = CartData.get(Tables.CartTable.ShippingAddressID);
                int ShippingAddressID = Integer.parseInt(shippingAddressid);
                double DeliveryFess = Double.parseDouble(CartData.get(Tables.CartTable.Fees));
                String discountcodeid = CartData.get(Tables.CartTable.DiscountCodeID);
                int DiscountCodeID = Integer.parseInt(discountcodeid);
                double DiscountAmount = Double.parseDouble(CartData.get(Tables.CartTable.DiscountAmount));
                double DiscountedAmount = Double.parseDouble(CartData.get(Tables.CartTable.DiscountedAmount));

                double UserAcctBalance = EngineWalletManager.GetUserBalance(UserID, EngineWalletManager.GetMainWalletID());
                if (UserAcctBalance >= TotalAmount) {
                    String Reference = ComputeOrderReferenceNumber();

                    int PaymentStatusID = GetOrderPaymentStatusID("Awaiting Confirmation");

                    result = EngineWalletManager.ComputeWalletRecord(UserID, UserID, EngineWalletManager.GetMainWalletID(), EngineWalletManager.GetPendingWalletID(), TotalAmount, "Move Fund", "For placing an Order.");
                    if (result.equals("success")) {
                        ArrayList<String> ProductSellerDetails = ComputeSellerAmounts(CartID);
                        int OrderID = 0;
                        String allSellers = ExtractSellerPaymentDetails(ProductSellerDetails);
                        List<String> allSellersArray = new ArrayList<>(Arrays.asList(allSellers.split(",")));
                        String OrderRef = "";
                        for (String seller : allSellersArray) {
                            int SellerUserID = Integer.parseInt(seller.split("-")[0]);
                            double SellerAmount = Double.parseDouble(seller.split("-")[1]);

                            OrderID = CreateOrder(Reference, CartID, UserID, SellerUserID, SellerAmount, ShippingTypeID, ShippingAddressID, OrderAmount, TotalAmount, DeliveryFess, DiscountCodeID, DiscountedAmount, DiscountAmount, PaymentStatusID, Message);
                            if (OrderID != 0) {
                                if (allSellersArray.size() > 1) {
                                    if (OrderRef.isEmpty()) {
                                        OrderRef = GetOrderReferenceNumber(OrderID);
                                    }
                                    UpdateOrderReferenceNumber(OrderID, Reference);
                                }
                                String InvoiceNumber = ComputeInvoiceNumber();
                                result = CreateOrderInvoices(OrderID, InvoiceNumber, TotalAmount, ShippingTypeID, ShippingAddressID);
                                if (result.equals("success")) {
                                    result = ComputeOrderHistory(OrderID, CartID, SellerUserID);
                                    if (result.equals("success")) {
                                        CreateOrderStatusHistory(OrderID, PaymentStatusID);
                                    }
                                } else {
                                    result = "Order Invoice could not be completed.";
                                }
                            } else {
                                result = "Order Payment could not be completed.";
                            }
                        }
                        if (result.equals("success")) {
                            result = CreateOrderPayment(Reference, TotalAmount, PaymentMethod, PaymentReference);
                            if (result.equals("success")) {
                                result = EngineCartManager.UpdateCartStatusByCartID(CartID, "Completed");
                                if (result.equals("success")) {
                                    result = EngineCartManager.DeleteOrEmtpyCart(CartID);
                                    if (result.equals("success")) {

                                    } else {
                                        result = "Deleting Cart Details after placing order could not be completed.";
                                    }
                                } else {
                                    result = "Order Payment could not be completed.";
                                }
                            } else {
                                result = "Order Payment could not be completed.";
                            }
                        } else {
                            result = "Order  could not be completed.";
                        }
                    }
                    String body = "Hi " + EngineUserManager.GetUserName(UserID) + ",\n\nThe Order with the Reference Number " + Reference + " has been placed and it is awaiting confirmation. \n\nYou will be notified shortly.\n\nCheers \nFyngram";
                    EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), body, "Placed Order", UserID);
                    EngineEmailManager.SendEmail(EngineUserManager.GetUserEmail(UserID), body, "Placed Order - Fyngram");
                } else {
                    result = "Insufficient fund.";
                }
            } catch (Exception ex) {
                String res = ex.getMessage();
                ex.printStackTrace();
                result = "Failed to retrive cart data";
            }
        } else {
            result = "Please check your cart";
        }

        return result;
    }

    /**
     *
     * @param CartID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<String> ComputeSellerAmounts(int CartID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<String> ProductSellerDetails = new ArrayList<>();
        HashMap<String, String> CartProductDetailData = new HashMap<>();
        try {
            ArrayList<Integer> CartProductDetailIDs = EngineCartManager.GetCartProductDetailsIDsByCartID(CartID);
            if (!CartProductDetailIDs.isEmpty()) {
                for (int CartProductDetailsID : CartProductDetailIDs) {
                    CartProductDetailData = EngineCartManager.GetCartProductDetailsDataByID(CartProductDetailsID);
                    int ProductID = Integer.parseInt(CartProductDetailData.get(Tables.CartProductDetailsTable.ProductID));
                    int SellerUserID = EngineProductManager.GetProductSellerUserIDByProductID(ProductID);
                    double Amount = Double.parseDouble(CartProductDetailData.get(Tables.CartProductDetailsTable.ProductPrice));
                    String SellerDetails = SellerUserID + "-" + Amount;
                    ProductSellerDetails.add(SellerDetails);
                }
            }
        } catch (Exception ex) {
        }
        return ProductSellerDetails;
    }

    /**
     *
     * @param OrderID
     * @param CartID
     * @param OrderSellerUserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputeOrderHistory(int OrderID, int CartID, int OrderSellerUserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, String> CartProductDetailData = new HashMap<>();
        ArrayList<Integer> CartProductDetailIDs = EngineCartManager.GetCartProductDetailsIDsByCartID(CartID);
        int count = 0;
        if (!CartProductDetailIDs.isEmpty()) {
            for (int CartProductDetailsID : CartProductDetailIDs) {
                CartProductDetailData = EngineCartManager.GetCartProductDetailsDataByID(CartProductDetailsID);
                if (!CartProductDetailData.isEmpty()) {
                    int ProductID = Integer.parseInt(CartProductDetailData.get(Tables.CartProductDetailsTable.ProductID));
                    double ProductPrice = Double.parseDouble(CartProductDetailData.get(Tables.CartProductDetailsTable.ProductPrice));
                    int ProductQuantity = Integer.parseInt(CartProductDetailData.get(Tables.CartProductDetailsTable.ProductQuantity));
                    int ProductSellerUserID = EngineProductManager.GetProductSellerUserIDByProductID(ProductID);
                    if (ProductSellerUserID == OrderSellerUserID) {
                        String TrackingNumber = EngineDiscountManager.GenerateDiscountCode(ProductID, ProductQuantity, ProductQuantity);
                        result = CreateOrderHistory(OrderID, ProductID, ProductPrice, ProductQuantity, ProductSellerUserID, TrackingNumber);
                    }
                }
            }
        }
        return result;
    }

    /**
     *
     * @param ProductSellerDetails
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws ParseException
     * @throws UnsupportedEncodingException
     */
    public static String ExtractSellerPaymentDetails(ArrayList ProductSellerDetails) throws ClassNotFoundException, SQLException, ParseException, UnsupportedEncodingException {
        String allSellers = "";
        //Extra Seller's Details "7-40,29-50,7-35,29-35,29-35" => 7-40+35 (7-75)  & 29-50+35+35 (29-120)
        String ProductSellerDetailsString = UtilityManager.ConvertStringArrayListToString(ProductSellerDetails);
        List<String> SellerDetArray = new ArrayList<>(Arrays.asList(ProductSellerDetailsString.split(",")));
        ArrayList<HashMap<String, String>> Sellerdetails = new ArrayList();
        double totalamt = 0.0;
        for (String Sdetails : SellerDetArray) {
            HashMap<String, String> Det = new HashMap();
            String suserid = Sdetails.split("-")[0];
            int selleruserid = Integer.parseInt(suserid);
            String amt = Sdetails.split("-")[1];
            double amount = Double.parseDouble(amt);
            Det.put("selleruserid", "" + selleruserid);
            Det.put("amount", "" + amount);

            if (!Sellerdetails.isEmpty()) {
                for (Map arr : Sellerdetails) {
                    if (!arr.isEmpty()) {
                        Object olduserid = arr.get("selleruserid");
                        int OldUserID = Integer.parseInt("" + olduserid);
                        if (OldUserID == selleruserid) {
                            Object oldamt = arr.get("amount");
                            double OldAmount = Double.parseDouble("" + oldamt);
                            totalamt = amount + OldAmount;
                            arr.values().remove(oldamt);
                            arr.values().remove(olduserid);
                            Det.replace("amount", "" + totalamt);
                        }
                    }
                }
            }
            Sellerdetails.add(Det);
        }
        if (!Sellerdetails.isEmpty()) {
            for (Map details : Sellerdetails) {
                if (!details.isEmpty()) {
                    Object uid = details.get("selleruserid");
                    int userid = Integer.parseInt("" + uid);
                    Object amt = details.get("amount");
                    double amount = Double.parseDouble("" + amt);
                    String seller = userid + "-" + amount + ",";
                    allSellers = allSellers + seller;
                }
            }
        }
        return allSellers;
    }

    /**
     *
     * @param Reference
     * @param CartID
     * @param CustomerUserID
     * @param SellerUserID
     * @param SellerAmount
     * @param ShippingTypeID
     * @param ShippingAddressID
     * @param OrderAmount
     * @param TotalPaid
     * @param DeliveryFees
     * @param DiscountCodeID
     * @param DiscountedAmount
     * @param DiscountAmount
     * @param PaymentStatusID
     * @param Message
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int CreateOrder(String Reference, int CartID, int CustomerUserID, int SellerUserID, double SellerAmount, int ShippingTypeID, int ShippingAddressID, double OrderAmount, double TotalPaid, double DeliveryFees,
            int DiscountCodeID, double DiscountedAmount, double DiscountAmount, int PaymentStatusID, String Message) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.OrdersTable.Reference, Reference);
        tableData.put(Tables.OrdersTable.CartID, CartID);
        tableData.put(Tables.OrdersTable.CustomerUserID, CustomerUserID);
        tableData.put(Tables.OrdersTable.SellerUserID, SellerUserID);
        tableData.put(Tables.OrdersTable.SellerAmount, SellerAmount);
        tableData.put(Tables.OrdersTable.OrderAmount, OrderAmount);
        tableData.put(Tables.OrdersTable.TotalPaid, TotalPaid);
        tableData.put(Tables.OrdersTable.ShippingTypeID, ShippingTypeID);
        tableData.put(Tables.OrdersTable.ShippingAddressID, ShippingAddressID);
        tableData.put(Tables.OrdersTable.DeliveryFees, DeliveryFees);
        tableData.put(Tables.OrdersTable.DiscountCodeID, DiscountCodeID);
        tableData.put(Tables.OrdersTable.DiscountedAmount, DiscountedAmount);
        tableData.put(Tables.OrdersTable.DiscountAmount, DiscountAmount);
        tableData.put(Tables.OrdersTable.PaymentStatusID, PaymentStatusID);
        tableData.put(Tables.OrdersTable.Message, Message);
        int OrderID = DBManager.insertTableDataReturnID(Tables.OrdersTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.OrdersTable.Table, Tables.OrdersTable.BookingDate, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        DBManager.UpdateCurrentTime(Tables.OrdersTable.Table, Tables.OrdersTable.BookingTime, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return OrderID;
    }

    /**
     *
     * @param OrderReference
     * @param Amount
     * @param PaymentMethod
     * @param ReferenceCode
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateOrderPayment(String OrderReference, double Amount, String PaymentMethod, String ReferenceCode) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.OrderPaymentsTable.OrderReference, OrderReference);
        tableData.put(Tables.OrderPaymentsTable.Amount, Amount);
        tableData.put(Tables.OrderPaymentsTable.PaymentMethod, PaymentMethod);
        tableData.put(Tables.OrderPaymentsTable.ReferenceCode, ReferenceCode);
        String result = DBManager.insertTableData(Tables.OrderPaymentsTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param OrderID
     * @param StatusID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateOrderStatusHistory(int OrderID, int StatusID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.OrderStatusHistoryTable.OrderID, OrderID);
        tableData.put(Tables.OrderStatusHistoryTable.StatusID, StatusID);
        int id = DBManager.insertTableDataReturnID(Tables.OrderStatusHistoryTable.Table, tableData, "");
        String result = DBManager.UpdateCurrentDate(Tables.OrderStatusHistoryTable.Table, Tables.OrderStatusHistoryTable.Date, "where " + Tables.OrderStatusHistoryTable.ID + " = " + id);
        DBManager.UpdateCurrentTime(Tables.OrderStatusHistoryTable.Table, Tables.OrderStatusHistoryTable.Time, "where " + Tables.OrderStatusHistoryTable.ID + " = " + id);

        return result;
    }

    /**
     *
     * @param OrderID
     * @param InvoiceNumber
     * @param Amount
     * @param ShippingTypeID
     * @param ShippingAddressID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateOrderInvoices(int OrderID, String InvoiceNumber, double Amount, int ShippingTypeID, int ShippingAddressID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.OrderInvoicesTable.OrderID, OrderID);
        tableData.put(Tables.OrderInvoicesTable.InvoiceNumber, InvoiceNumber);
        tableData.put(Tables.OrderInvoicesTable.Amount, Amount);
        tableData.put(Tables.OrderInvoicesTable.ShippingTypeID, ShippingTypeID);
        tableData.put(Tables.OrderInvoicesTable.ShippingAddressID, ShippingAddressID);
        int InvoiceID = DBManager.insertTableDataReturnID(Tables.OrderInvoicesTable.Table, tableData, "");
        String result = DBManager.UpdateCurrentDate(Tables.OrderInvoicesTable.Table, Tables.OrderInvoicesTable.Date, "where " + Tables.OrderInvoicesTable.ID + " = " + InvoiceID);
        DBManager.UpdateCurrentTime(Tables.OrderInvoicesTable.Table, Tables.OrderInvoicesTable.Time, "where " + Tables.OrderInvoicesTable.ID + " = " + InvoiceID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @param ProductID
     * @param ProductPrice
     * @param ProductQuantity
     * @param SellerUserID
     * @param TrackingNumber
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateOrderHistory(int OrderID, int ProductID, double ProductPrice, int ProductQuantity, int SellerUserID, String TrackingNumber) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.OrderHistoryTable.OrderID, OrderID);
        tableData.put(Tables.OrderHistoryTable.ProductID, ProductID);
        tableData.put(Tables.OrderHistoryTable.Price, ProductPrice);
        tableData.put(Tables.OrderHistoryTable.Quantity, ProductQuantity);
        tableData.put(Tables.OrderHistoryTable.SellerUserID, SellerUserID);
        tableData.put(Tables.OrderHistoryTable.TrackingNumber, TrackingNumber);
        int HistoryID = DBManager.insertTableDataReturnID(Tables.OrderHistoryTable.Table, tableData, "");
        String result = DBManager.UpdateCurrentDate(Tables.OrderHistoryTable.Table, Tables.OrderHistoryTable.Date, "where " + Tables.OrderHistoryTable.ID + " = " + HistoryID);
        DBManager.UpdateCurrentTime(Tables.OrderHistoryTable.Table, Tables.OrderHistoryTable.Time, "where " + Tables.OrderHistoryTable.ID + " = " + HistoryID);
        return result;
    }

    /**
     *
     * @return
     */
    public static String ComputeOrderReferenceNumber() {
        String rand = UtilityManager.randomAlphaNumeric(3);
        String timeNow = "" + UtilityManager.CurrentTime();
        timeNow = timeNow.replace(":", "");
        String rand2 = UtilityManager.randomAlphaNumeric(3);
        String ref = "FG-" + rand2 + timeNow + rand;
        return ref;
    }

    /**
     *
     * @param Status
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetOrderPaymentStatusID(String Status) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.OrderStatusTable.ID, Tables.OrderStatusTable.Table, "where " + Tables.OrderStatusTable.Name + " = '" + Status + "'");
        return result;
    }

    /**
     *
     * @return
     */
    public static String ComputeInvoiceNumber() {
        String rand = UtilityManager.randomAlphaNumeric(3);
        String timeNow = "" + UtilityManager.CurrentTime();
        timeNow = timeNow.replace(":", "");
        String rand2 = UtilityManager.randomAlphaNumeric(3);
        String result = "IN-" + rand2 + timeNow + rand;
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetOrderReferenceNumber(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.OrdersTable.Reference, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetOrderPaymentStatusID(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.OrdersTable.PaymentStatusID, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static double GetOrderTotalAmount(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double result = DBManager.GetDouble(Tables.OrdersTable.TotalPaid, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetOrderCustomerUserID(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.OrdersTable.CustomerUserID, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetOrderShippingTypeID(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.OrdersTable.ShippingTypeID, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetOrderDiscountCodeID(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.OrdersTable.DiscountCodeID, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static double GetOrderShippingFees(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double result = DBManager.GetDouble(Tables.OrdersTable.DeliveryFees, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetOrderShippingAddressID(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.OrdersTable.ShippingAddressID, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static double GetOrderDiscountAmount(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double result = DBManager.GetDouble(Tables.OrdersTable.DiscountAmount, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetOrderSellerUserID(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.OrdersTable.SellerUserID, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static double GetOrderSellerAmount(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double result = DBManager.GetDouble(Tables.OrdersTable.SellerAmount, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @param OrderReference
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateOrderReferenceNumber(int OrderID, String OrderReference) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateStringData(Tables.OrdersTable.Table, Tables.OrdersTable.Reference, OrderReference, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @param StatusID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateOrderPaymentStatusID(int OrderID, int StatusID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateIntData(Tables.OrdersTable.PaymentStatusID, StatusID, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @param AdminUserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateOrderApprovedUserID(int OrderID, int AdminUserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateIntData(Tables.OrdersTable.ApprovedByUserid, AdminUserID, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateOrderDeliveryDate(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateCurrentDate(Tables.OrdersTable.Table, Tables.OrdersTable.DeliveryDate, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateOrderDeliveryTime(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateCurrentTime(Tables.OrdersTable.Table, Tables.OrdersTable.DeliveryTime, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetOrderHistoryIDs(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        IDs = DBManager.GetIntArrayList(Tables.OrderHistoryTable.ID, Tables.OrderHistoryTable.Table, "where " + Tables.OrderHistoryTable.OrderID + " = " + OrderID);
        return IDs;
    }

    /**
     *
     * @param OrderReferenceNumber
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetOrderIDsByReferenceNumber(String OrderReferenceNumber) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        IDs = DBManager.GetIntArrayList(Tables.OrdersTable.ID, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.Reference + " = '" + OrderReferenceNumber + "'");
        return IDs;
    }

    /**
     *
     * @param ID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetOrderHistoryData(int ID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> data = DBManager.GetTableData(Tables.OrderHistoryTable.Table, "where " + Tables.OrderHistoryTable.ID + " = " + ID);
        return data;
    }
//---------------------------------------------------------  Cancel Order ---------------------------------------------------//

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputeCancelOrder(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException, IOException {
        String result = "failed";
        int CurrentOrderStatus = GetOrderPaymentStatusID(OrderID);
        int PaymentStatusID = GetOrderPaymentStatusID("Cancelled");
        if (CurrentOrderStatus != PaymentStatusID) {//3 Cancelled

//            if the status is awaiting confirmation => the cash is in the customer pending wallet
//            if the status is confirmed => the fund is in the customer pending wallet
//            if the status is shipped => the fund is in the customer pending wallet and the order quantity has been calculated and deducted and a shipping method has been added
//            if the status is delivered => the fund has been shared between the seller (pending wallet then after 24 hrs moved to pending wallet if no issues), admin (main wallet), shipping method (earnings and total delivery)
//                  if you cancel and the status is awaiting confirmation => withdraw the fund from the pending wallet, remove the admin cancellation fees if set, then move to the main wallet and inform everyone
//                  if you cancel and the status is confirmed => withdraw the fund from the pending wallet, remove the admin cancellation fees if set, then move to the main wallet and inform everyone
//                  if you cancel and the status is cancel => do nothing
//                  if you cancel and the status is shipped = withdraw the fund from the pending wallet, remove the admin cancellation fees if set, and move to the main wallet, recalculate and increment the order quantity and inform everyone 
//                  if you cancel and the status is delivered and it is  within the 24hrs delay time =>
//                                              withdraw the seller share from the seller (pending wallet), 
//                                              withdraw the admin share from the admin (pending wallet)
//                                              recalculate and increment the order quantity and inform everyone
//                                              withdraw the shipping method share and decrese the  total earning and number of deliveries.
//                                              remove the admin cancellation fees if set,
//                                               then move to the customer main wallet and inform everyone
            if (CurrentOrderStatus == 4) {//Shipped or delivered
                EngineStockManager.ComputeStockMovement(OrderID, "Increase");
                result = ProcessCancelOrder(OrderID);
            } else if (CurrentOrderStatus == 5) {//Delivered
                EngineStockManager.ComputeStockMovement(OrderID, "Increase");
                result = ProcessCancelDeliveredOrder(OrderID, PaymentStatusID);
            } else {
                result = ProcessCancelOrder(OrderID);
            }

        } else {
            result = "The Order has already been cancelled.";
        }
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ProcessCancelOrder(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        int PaymentStatusID = GetOrderPaymentStatusID("Cancelled");
        String OrderRef = GetOrderReferenceNumber(OrderID);

        int CustomerUserID = GetOrderCustomerUserID(OrderID);
        String CustomerUserName = EngineUserManager.GetUserName(CustomerUserID);
        String body = "Hi " + CustomerUserName + "," + "\nThe Order with the Ourder Reference Number " + OrderRef + " has been cancelled and your fund had also been refunded into your Main Wallet.";

        ArrayList<Integer> OrderIdsByRef = GetOrderIDsByReferenceNumber(OrderRef);
        double SellerTransactionAmount = GetOrderSellerAmount(OrderID);
        double DeliveryFees = GetOrderShippingFees(OrderID);
        double DiscountAmount = GetOrderDiscountAmount(OrderID);
        double DeliveryFeesRefundAmount = (DeliveryFees / OrderIdsByRef.size());
        String UserType = EngineUserManager.GetUserTypeNameByUserID("" + CustomerUserID);
        boolean enforceCancelFees;
        double CancelFeesPecentage = 0.0;
        if (UserType.equals("Admin")) { //if the admin is cancelling the order then the enforce cancel fees does not apply
            enforceCancelFees = false;
        } else { //if the customer is cancelling the order then the enforce cancel fees is checked and applied if set
            enforceCancelFees = GetEnforceCancelFees();
            CancelFeesPecentage = GetEnforceCancelFeesPercentage();
        }

        CreateOrderStatusHistory(OrderID, PaymentStatusID);
        double CustomerRefundableAmount = SellerTransactionAmount + DeliveryFeesRefundAmount; //e.g 2200+200 = 2600
        if (DiscountAmount == 0) { //0 2600 - 0 = 2600
            if (enforceCancelFees) { //e.g 2600 - 100 = 2500
                double AdminAmount = EngineDiscountManager.ComputePercentageAmount(CancelFeesPecentage, CustomerRefundableAmount);
                double CustomerAmount = CustomerRefundableAmount - AdminAmount;
                result = EngineWalletManager.ComputeWalletRecord(CustomerUserID, CustomerUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), CustomerAmount, "Move Fund", "For s cancelled Order.");
                EngineWalletManager.ComputeWalletRecord(CustomerUserID, EngineUserManager.GetAdminUserID(), EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), AdminAmount, "Move Fund", "For s cancelled Order.");
            } else { //e.g 2600
                result = EngineWalletManager.ComputeWalletRecord(CustomerUserID, CustomerUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), CustomerRefundableAmount, "Move Fund", "For s cancelled Order.");
            }
        } else { //100
            double withDiscountAmount = DiscountAmount / OrderIdsByRef.size();
            double withDiscountCustomerRefundableAmount = CustomerRefundableAmount - withDiscountAmount; //e,g 2600 - 100 = 2500
            if (enforceCancelFees) { //e.g 2500 -100 = 2400
                double AdminAmount = EngineDiscountManager.ComputePercentageAmount(CancelFeesPecentage, withDiscountCustomerRefundableAmount);
                double CustomerAmount = withDiscountCustomerRefundableAmount - AdminAmount;
                result = EngineWalletManager.ComputeWalletRecord(CustomerUserID, CustomerUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), CustomerAmount, "Move Fund", "For s cancelled Order.");
                EngineWalletManager.ComputeWalletRecord(CustomerUserID, EngineUserManager.GetAdminUserID(), EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), AdminAmount, "Move Fund", "For s cancelled Order.");
            } else {//e.g 2500
                result = EngineWalletManager.ComputeWalletRecord(CustomerUserID, CustomerUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), withDiscountCustomerRefundableAmount, "Move Fund", "For a cancelled Order.");
            }
        }
        if (result.equals("success")) {
            result = UpdateOrderPaymentStatusID(OrderID, PaymentStatusID);
            EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), body, "Order Cancelled", CustomerUserID);

            int SellerUserID = GetOrderSellerUserID(OrderID);
            String SellerUserName = EngineUserManager.GetUserName(SellerUserID);
            body = "Hi " + SellerUserName + ",\n\nThe Order with the Reference Number " + OrderRef + " that involves your product(s) has been cancelled.\n\nCheers \nFyngram";
            EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), body, "Order Cancelled", SellerUserID);
            try {
                String CustomerUserEmail = EngineUserManager.GetUserEmail(CustomerUserID);
                EngineEmailManager.SendEmail(CustomerUserEmail, body, "Fyngram Order Cancelled");

                String SellerUserEmail = EngineUserManager.GetUserEmail(SellerUserID);
                EngineEmailManager.SendEmail(SellerUserEmail, body, "Fyngram Order Cancelled");
            } catch (Exception ex) {
            }
        } else {
            result = "The cancelling of the order could not be completed.";
        }
        return result;
    }

    /**
     *
     * @param OrderID
     * @param PaymentStatusID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ProcessCancelDeliveredOrder(int OrderID, int PaymentStatusID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        String OrderRef = GetOrderReferenceNumber(OrderID);
        int CustomerUserID = GetOrderCustomerUserID(OrderID);
        String CustomerUserName = EngineUserManager.GetUserName(CustomerUserID);
        String body = "Hi " + CustomerUserName + "," + "\n\nThe Order with the Ourder Reference Number " + OrderRef + " has been cancelled and your fund had also been refunded into your Main Wallet.\n\nCheers\nFyngram";

        ArrayList<Integer> OrderIdsByRef = GetOrderIDsByReferenceNumber(OrderRef);
        double SellerTransactionAmount = GetOrderSellerAmount(OrderID);// 2200
        double refundableShippingFees = UpdateShippingMethodCancelOrder(OrderID, OrderRef); // 400
        int SellerUserID = GetOrderSellerUserID(OrderID);
        double DiscountAmount = GetOrderDiscountAmount(OrderID); // 100
        String UserType = EngineUserManager.GetUserTypeNameByUserID("" + CustomerUserID);
        boolean enforceCancelFees;
        double CancelFeesPecentage = 0.0;
        if (UserType.equals("Admin")) { //if the admin is cancelling the order then the enforce cancel fees does not apply
            enforceCancelFees = false;
        } else { //if the customer is cancelling the order then the enforce cancel fees is checked and applied if set
            enforceCancelFees = GetEnforceCancelFees();
            CancelFeesPecentage = GetEnforceCancelFeesPercentage();
        }
        int SellerTypeID = EngineSubscriptionManager.GetSellerTypeIDBySellerUserID(SellerUserID);
        double AdminTransactionPercent = EngineTransactionManager.GetAdminTransactionPercentageBySellerType(SellerTypeID);
        double AdminTransactionShare = EngineDiscountManager.ComputePercentageAmount(AdminTransactionPercent, SellerTransactionAmount);//200
        double SellerTransactionShare = SellerTransactionAmount - AdminTransactionShare; //2000
        CreateOrderStatusHistory(OrderID, PaymentStatusID);

        if (DiscountAmount == 0) {// 2600
            //withdraw the seller amount and send to the admin
            result = EngineWalletManager.ComputeWalletRecord(SellerUserID, EngineUserManager.GetAdminUserID(), EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetPendingWalletID(), SellerTransactionShare, "Move Fund", "For a cancelled Order.");
            double FinalRefundableAmount = SellerTransactionAmount + refundableShippingFees; //2200 + 400 = 2400
            if (enforceCancelFees) {
                double AdminAmount = EngineDiscountManager.ComputePercentageAmount(CancelFeesPecentage, FinalRefundableAmount); //
                double CustomerAmount = FinalRefundableAmount - AdminAmount;
                result = EngineWalletManager.ComputeWalletRecord(EngineUserManager.GetAdminUserID(), CustomerUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), CustomerAmount, "Move Fund", "For a cancelled Order.");
            } else { //e.g 2600
                result = EngineWalletManager.ComputeWalletRecord(EngineUserManager.GetAdminUserID(), CustomerUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), FinalRefundableAmount, "Move Fund", "For a cancelled Order.");
            }
        } else { //100 => (2200 + 400) - 100 = 2500
            //withdraw the seller amount and send to the admin
            result = EngineWalletManager.ComputeWalletRecord(SellerUserID, EngineUserManager.GetAdminUserID(), EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetPendingWalletID(), SellerTransactionShare, "Move Fund", "For a cancelled Order.");
            double withDiscountAmount = DiscountAmount / OrderIdsByRef.size();
            double FinalRefundableAmount = (SellerTransactionAmount + refundableShippingFees) - withDiscountAmount; //2200 + 400 - 100 = 2500
            if (enforceCancelFees) {
//                    enforce cancellation fees
                double AdminAmount = EngineDiscountManager.ComputePercentageAmount(CancelFeesPecentage, FinalRefundableAmount); //
                double CustomerAmount = FinalRefundableAmount - AdminAmount;
                result = EngineWalletManager.ComputeWalletRecord(EngineUserManager.GetAdminUserID(), CustomerUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), CustomerAmount, "Move Fund", "For a cancelled Order.");
            } else { //e.g 2600
//                     do not enforce cancellation fees
                result = EngineWalletManager.ComputeWalletRecord(EngineUserManager.GetAdminUserID(), CustomerUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), FinalRefundableAmount, "Move Fund", "For a cancelled Order.");
            }
        }
        if (result.equals("success")) {
            result = UpdateOrderPaymentStatusID(OrderID, PaymentStatusID);
            EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), body, "Order Cancelled", CustomerUserID);

            String SellerUserName = EngineUserManager.GetUserName(SellerUserID);
            body = "Hi " + SellerUserName + ",\n\nThe Order with the Reference Number " + OrderRef + " that involves your product(s) has been cancelled.\n\nCheers \nFyngram";
            EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), body, "Order Cancelled", SellerUserID);
            try {
                String CustomerUserEmail = EngineUserManager.GetUserEmail(CustomerUserID);
                EngineEmailManager.SendEmail(CustomerUserEmail, body, "Fyngram Order Cancelled");

                String SellerUserEmail = EngineUserManager.GetUserEmail(SellerUserID);
                EngineEmailManager.SendEmail(SellerUserEmail, body, "Fyngram Order Cancelled");
            } catch (Exception ex) {
            }
        } else {
            result = "The cancelling of the order could not be completed.";
        }
        return result;
    }

    public static double UpdateShippingMethodCancelOrder(int OrderID, String OrderRef) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double refundableShippingFees = 0.0;
        int ShippingMethodID = GetOrderShippingMethodByOrderID(OrderID);
        int ShippingTypeID = GetOrderShippingTypeID(OrderID);
        double AdminShippingPercentage = 0.0;

        if (ShippingTypeID == 1) {//Use my address
            AdminShippingPercentage = EngineShippingManager.GetAdminShippingPercentage(ShippingMethodID);
        } else if (ShippingTypeID == 2) {//Use Pickup address
            int ShippingAddressID = GetOrderShippingAddressID(OrderID);
            AdminShippingPercentage = EngineAddressManager.GetAdminPickupPercentage(ShippingAddressID);
        }
        ArrayList<Integer> OrderIdsByRef = GetOrderIDsByReferenceNumber(OrderRef);
        double DeliveryFees = GetOrderShippingFees(OrderID);
        double DeliveryFeesAmount = (DeliveryFees / OrderIdsByRef.size());
        double AdminShippingAmount = EngineDiscountManager.ComputePercentageAmount(AdminShippingPercentage, DeliveryFeesAmount);
        double ShippingMethodShippingAmount = DeliveryFeesAmount - AdminShippingAmount;
        String ShippingMethodName = EngineShippingManager.GetShippingMethodName(ShippingMethodID);
        refundableShippingFees = AdminShippingAmount + ShippingMethodShippingAmount;
        String result = EngineShippingManager.UpdateShippingMethodEarnings(ShippingMethodID, ShippingMethodShippingAmount, "Subtract");
        result = EngineShippingManager.UpdateShippingMethodNumberOfDelivery(ShippingMethodID, "Subtract");

        String body = "Hi " + ShippingMethodName + ",\n\nThe Order with the Reference Number " + OrderRef + " has been cancelled.\n\nCheers \nFyngram";
        try {
            String ShippingMethodEmail = EngineShippingManager.GetShippingMethodEmail(ShippingMethodID);
            EngineEmailManager.SendEmail(ShippingMethodEmail, body, "Fyngram Order Delivered");
        } catch (Exception ex) {

        }

        return refundableShippingFees;
    }

    /**
     *
     * @param OrderID
     * @param ShippingMethodID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static boolean GetEnforceCancelFees() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        boolean result = false;
        int enforce = DBManager.GetInt(Tables.OrderCancelRulesTable.EnforceRule, Tables.OrderCancelRulesTable.Table, "where " + Tables.OrderCancelRulesTable.ID + " = " + 1);
        if (enforce == 1) {
            result = true;
        }
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static double GetEnforceCancelFeesPercentage() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double result = DBManager.GetDouble(Tables.OrderCancelRulesTable.Percent, Tables.OrderCancelRulesTable.Table, "");
        return result;
    }

    /**
     *
     * @param Amount
     * @param Rule
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateEnforceCancelFees(double Amount, int Rule) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateDoubleData(Tables.OrderCancelRulesTable.Table, Tables.OrderCancelRulesTable.Percent, Amount, "where " + Tables.OrderCancelRulesTable.ID + " = " + 1);
        DBManager.UpdateIntData(Tables.OrderCancelRulesTable.EnforceRule, Rule, Tables.OrderCancelRulesTable.Table, "where " + Tables.OrderCancelRulesTable.ID + " = " + 1);
        return result;
    }

    public static HashMap<String, String> GetCancelOrderRuleData() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> result = new HashMap<>();
        result = DBManager.GetTableData(Tables.OrderCancelRulesTable.Table, "where " + Tables.OrderCancelRulesTable.ID + " = " + 1);
        return result;
    }

    //---------------------------------------------------------  Confirm Order ---------------------------------------------------//
    /**
     *
     * @param OrderID
     * @param ShippingMethodID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputeAssignShippingMethodToOrder(int OrderID, int ShippingMethodID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        String OrderRef = GetOrderReferenceNumber(OrderID);
        String body = "";
        int ExistingShipMeth = DBManager.GetInt(Tables.OrderShippingMethodTable.ID, Tables.OrderShippingMethodTable.Table, "where " + Tables.OrderShippingMethodTable.OrderID + " = " + OrderID);
        if (ExistingShipMeth == 0) {
            result = CreateOrderShippingMethod(OrderID, ShippingMethodID);
        } else {
            result = UpdateOrderShippingMethod(OrderID, ShippingMethodID);
        }
        if (result.equals("success")) {
            try {
                String ShippingMethodEmail = EngineShippingManager.GetShippingMethodEmail(ShippingMethodID);
                String ShippingMethodName = EngineShippingManager.GetShippingMethodName(ShippingMethodID);
                body = "Hi " + ShippingMethodName + ",\n\nThe Order with the Order Reference Number " + OrderRef + " has been confirmed and it's pending delivery. \nPlease, contact Fyngram Sales Team For Shipping/Delivery Schedules.";
                EngineEmailManager.SendEmail(ShippingMethodEmail, body, "Fyngram Order Confirmation");
            } catch (Exception ex) {
            }
        } else {
            result = "Updating the shipping method was not successful. Please try again.";
        }

        return result;
    }

    /**
     *
     * @param SessionID
     * @param orderID
     * @param statusID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputeOrderStatus(String SessionID, String orderID, String statusID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException, IOException {
        String result = "failed";
        int StatusID = Integer.parseInt(statusID);
        int OrderID = Integer.parseInt(orderID);
        switch (StatusID) {
            case 1:
                //CONFIRMATION
                int AdminUserID = Integer.parseInt(SessionID);
                result = ComputeConfirmOrder(AdminUserID, OrderID);
                break;
            case 3:
                //CANCELLED
                result = ComputeCancelOrder(OrderID);
                break;
            case 4:
                //SHIPPED
                result = ComputeShippedOrder(OrderID);
                break;
            case 5:
                //DELIVERED
                result = ComputeDeliveredOrder(OrderID);
                break;
            case 6:
                //DELIVERED
                result = ComputeDisputeOrder(OrderID);
                break;
            case 7:
                //DELIVERED
                result = ComputeSettledOrder(OrderID);
                break;
            default:
                break;
        }
        return result;
    }

    /**
     *
     * @param AdminUserID
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputeConfirmOrder(int AdminUserID, int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        int OrderStatus = GetOrderPaymentStatusID(OrderID);
        int PaymentStatusID = GetOrderPaymentStatusID("Confirmed");
        if (OrderStatus != PaymentStatusID) {
            String OrderRef = GetOrderReferenceNumber(OrderID);
            String body = "";
            result = UpdateOrderPaymentStatusID(OrderID, PaymentStatusID);
            if (result.equals("success")) {
                UpdateOrderApprovedUserID(OrderID, AdminUserID);

                int CustomerUserID = GetOrderCustomerUserID(OrderID);
                String CustomerUserName = EngineUserManager.GetUserName(CustomerUserID);
                body = "Hi " + CustomerUserName + ",\n\nThe Order with the Reference Number " + OrderRef + " has been confirmed and payment has been recieved and is BEING PROCESSED. \n\nYou will receive shipping/delivery message shortly.\n\nCheers \nFyngram";
                EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), body, "Order Confirmation", CustomerUserID);

                int SellerUserID = GetOrderSellerUserID(OrderID);
                String SellerUserName = EngineUserManager.GetUserName(SellerUserID);
                body = "Hi " + SellerUserName + ",\n\nThe Order with the Reference Number " + OrderRef + " that involves your product(s) has been confirmed. \n\nPlease, contact Fyngram Sales Team For Shipping/Delivery Schedules.\n\nCheers \nFyngram";
                EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), body, "Order Confirmation", SellerUserID);
                CreateOrderStatusHistory(OrderID, PaymentStatusID);
                try {
                    String UserEmail = EngineUserManager.GetUserEmail(CustomerUserID);
                    EngineEmailManager.SendEmail(UserEmail, body, "Fyngram Order Confirmation");

                    String SellerUserEmail = EngineUserManager.GetUserEmail(SellerUserID);
                    EngineEmailManager.SendEmail(SellerUserEmail, body, "Fyngram Order Confirmation");
                } catch (Exception ex) {
                }
            } else {
                result = "The cancelling of the order could not be completed.";
            }
        } else {
            result = "The Order has already been confirmed.";
        }

        return result;
    }

    /**
     *
     * @param AdminUserID
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputeDisputeOrder(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        int CurrentOrderStatus = GetOrderPaymentStatusID(OrderID);
        int NewStatusID = GetOrderPaymentStatusID("Dispute");
        if (CurrentOrderStatus != NewStatusID) {
            String OrderRef = GetOrderReferenceNumber(OrderID);
            result = UpdateOrderPaymentStatusID(OrderID, NewStatusID);
            if (result.equals("success")) {

                int CustomerUserID = GetOrderCustomerUserID(OrderID);
                String CustomerUserName = EngineUserManager.GetUserName(CustomerUserID);
                String body = "Hi " + CustomerUserName + ",\n\nThe Order with the Reference Number " + OrderRef + " has been reported to have an issue. \n\nFyngram Sales team would look into the issue and you will also be notfied immediately the issue is resolved.\n\nCheers \nFyngram";
                EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), body, "Order Dispute", CustomerUserID);

                int SellerUserID = GetOrderSellerUserID(OrderID);
                String SellerUserName = EngineUserManager.GetUserName(SellerUserID);
                String msg = "Hi " + SellerUserName + ",\n\nThe Order with the Reference Number " + OrderRef + " that involves your product(s) has been reported to have an issue. \n\nFyngram Sales team would look into the issue and you will also be notified immediately the issues is resolved.\n\nPlease, bear with us as this process is going to delay your payment pending the resolution of the issue.\n\nCheers \nFyngram";
                EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), msg, "Order Dispute", SellerUserID);
                CreateOrderStatusHistory(OrderID, NewStatusID);
                try {
                    String UserEmail = EngineUserManager.GetUserEmail(CustomerUserID);
                    EngineEmailManager.SendEmail(UserEmail, body, "Fyngram Order Dispute");

                    String SellerUserEmail = EngineUserManager.GetUserEmail(SellerUserID);
                    EngineEmailManager.SendEmail(SellerUserEmail, msg, "Fyngram Order Dispute");
                } catch (Exception ex) {
                }
            } else {
                result = "The cancelling of the order could not be completed.";
            }
        } else {
            result = "The Order has already been confirmed.";
        }

        return result;
    }

    /**
     *
     * @param OrderID
     * @param ShippingMethodID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateOrderShippingMethod(int OrderID, int ShippingMethodID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.OrderShippingMethodTable.OrderID, OrderID);
        tableData.put(Tables.OrderShippingMethodTable.ShippingMethodID, ShippingMethodID);
        result = DBManager.insertTableData(Tables.OrderShippingMethodTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param OrderID
     * @param ShippingMethodID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateOrderShippingMethod(int OrderID, int ShippingMethodID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateIntData(Tables.OrderShippingMethodTable.ShippingMethodID, ShippingMethodID, Tables.OrderShippingMethodTable.Table, "where " + Tables.OrderShippingMethodTable.OrderID + " = " + OrderID);
        return result;
    }

    //---------------------------------------------------------  Shipped Order ---------------------------------------------------//
    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputeShippedOrder(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException, IOException {
        String result = "failed";
        int CurrentOrderStatus = GetOrderPaymentStatusID(OrderID);
        int PaymentStatusID = GetOrderPaymentStatusID("Shipped");
        if (CurrentOrderStatus != PaymentStatusID) {
            String OrderRef = GetOrderReferenceNumber(OrderID);
            int CustomerUserID = GetOrderCustomerUserID(OrderID);
            String CustomerUserName = EngineUserManager.GetUserName(CustomerUserID);
            String CustomerPhone = EngineUserManager.GetUserPhone(CustomerUserID);
            int SellerUserID = GetOrderSellerUserID(OrderID);
            String SellerUserName = EngineUserManager.GetUserName(SellerUserID);
            String body = "Hi " + CustomerUserName + ",\n\nThe Order with the Reference Number " + OrderRef + " has been processed and shipped . \n\nYou will be contacted by The Delivery Agent.\n\nCheers \nFyngram";
            String sbody = "Hi " + SellerUserName + ",\n\nThe Order with the Reference Number " + OrderRef + " has been processed and shipped/in-delivery . \n\nPlease, contact the customer on phone: " + CustomerPhone + "\n\nCheers \nFyngram";

            result = UpdateOrderPaymentStatusID(OrderID, PaymentStatusID);
            if (result.equals("success")) {
                CreateOrderStatusHistory(OrderID, PaymentStatusID);
                result = EngineStockManager.ComputeStockMovement(OrderID, "Decrease");
                if (result.equals("success")) {
                    EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), body, "Order Shipped", CustomerUserID);
                    try {
                        String UserEmail = EngineUserManager.GetUserEmail(CustomerUserID);
                        EngineEmailManager.SendEmail(UserEmail, body, "Fyngram Order Shipped");
                        String SellerUserEmail = EngineUserManager.GetUserEmail(SellerUserID);
                        EngineEmailManager.SendEmail(SellerUserEmail, sbody, "Fyngram Order Shipped");
                    } catch (Exception ex) {
                    }
                }
            } else {
                result = "The shipping of the order could not be completed.";
            }
        } else {
            result = "The Order has already been shipped.";
        }
        return result;
    }

    //---------------------------------------------------------  Delivered Order ---------------------------------------------------//
    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputeDeliveredOrder(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        int OrderStatus = GetOrderPaymentStatusID(OrderID);
        int PaymentStatusID = GetOrderPaymentStatusID("Delivered");
        if (OrderStatus != PaymentStatusID && OrderStatus == 4) {
            String OrderRef = GetOrderReferenceNumber(OrderID);
            int CustomerUserID = GetOrderCustomerUserID(OrderID);
            int SellerUserID = GetOrderSellerUserID(OrderID);
            int AdminUserID = EngineUserManager.GetAdminUserID();

            double SellerAmount = GetOrderSellerAmount(OrderID);
            double DeliveryFees = GetOrderShippingFees(OrderID);
            double DiscountFees = GetOrderDiscountAmount(OrderID);
            int ShippingTypeID = GetOrderShippingTypeID(OrderID);
            int DiscountCodeID = GetOrderDiscountCodeID(OrderID);
            int SellerTypeID = EngineSubscriptionManager.GetSellerTypeIDBySellerUserID(SellerUserID);
            double AdminTransactionPercent = EngineTransactionManager.GetAdminTransactionPercentageBySellerType(SellerTypeID);
            double AdminShippingPercentage = 0.0;
            ArrayList<Integer> OrderIdsByRef = GetOrderIDsByReferenceNumber(OrderRef);

            if (ShippingTypeID == 1) {//Use my address
                int ShippingMethodID = GetOrderShippingMethodByOrderID(OrderID);
                AdminShippingPercentage = EngineShippingManager.GetAdminShippingPercentage(ShippingMethodID);
            } else if (ShippingTypeID == 2) {//Use Pickup address
                int ShippingAddressID = GetOrderShippingAddressID(OrderID);
                AdminShippingPercentage = EngineAddressManager.GetAdminPickupPercentage(ShippingAddressID);
            }

            double AdminTransactionShare = EngineDiscountManager.ComputePercentageAmount(AdminTransactionPercent, SellerAmount);
            double SellerTransactionShare = SellerAmount - AdminTransactionShare;

            double DeliveryFeesAmount = (DeliveryFees / OrderIdsByRef.size());
            double AdminShippingAmount = EngineDiscountManager.ComputePercentageAmount(AdminShippingPercentage, DeliveryFeesAmount);
            double ShippingMethodShippingAmount = DeliveryFeesAmount - AdminShippingAmount;

            //admin actual balance in the transactions. 
//            double AdminBalance = AdminTransactionShare + AdminShippingAmount;
            //but since the shipping company does not have account on the system
            //all the amount would be given to the admin the admin would settle the delivery company off the system.
            double AdminBalanceAndShippingMethodBalance = AdminTransactionShare + AdminShippingAmount + ShippingMethodShippingAmount;

            double SellerBalance = SellerTransactionShare;
            int SplitDiscountDeductionValue = EngineDiscountManager.GetDiscountSplitDeductionValue(DiscountCodeID);

            int ShippingMethodID = GetOrderShippingMethodByOrderID(OrderID);
            result = EngineShippingManager.UpdateShippingMethodEarnings(ShippingMethodID, ShippingMethodShippingAmount, "Add");
            result = EngineShippingManager.UpdateShippingMethodNumberOfDelivery(ShippingMethodID, "Add");
            if (DiscountFees == 0) {//no discounts
                result = EngineWalletManager.ComputeWalletRecord(CustomerUserID, AdminUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), AdminBalanceAndShippingMethodBalance, "Move Fund", "For delivered Order.");
//                result = EngineWalletManager.ComputeWalletRecord(CustomerUserID, AdminUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetPendingWalletID(), AdminBalanceAndShippingMethodBalance, "Move Fund", "For delivered Order.");
                if (result.equals("success")) {
                    result = EngineWalletManager.ComputeWalletRecord(CustomerUserID, SellerUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetPendingWalletID(), SellerBalance, "Move Fund", "For delivered Order.");
                }
            } else {//with discount
                double withDiscountAmount = (DiscountFees / OrderIdsByRef.size());
                if (SplitDiscountDeductionValue == 0) {
//                     do not share discount amount
//                    double AdminAmountAfterDiscount = AdminBalance - withDiscountAmount;
                    double AdminAmountAfterDiscountwithShippingMethodShare = AdminBalanceAndShippingMethodBalance - withDiscountAmount;
                    result = EngineWalletManager.ComputeWalletRecord(CustomerUserID, AdminUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), AdminAmountAfterDiscountwithShippingMethodShare, "Move Fund", "For delivered Order.");
//                    result = EngineWalletManager.ComputeWalletRecord(CustomerUserID, AdminUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetPendingWalletID(), AdminAmountAfterDiscountwithShippingMethodShare, "Move Fund", "For delivered Order.");
                    if (result.equals("success")) {
                        result = EngineWalletManager.ComputeWalletRecord(CustomerUserID, SellerUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetPendingWalletID(), SellerBalance, "Move Fund", "For delivered Order.");
                    }
                } else {
                    //share the discount amount
                    double DiscountAmountToRefund = (withDiscountAmount / 2);
                    double SellerAmountAfterDiscount = SellerBalance - DiscountAmountToRefund;
                    result = EngineWalletManager.ComputeWalletRecord(CustomerUserID, SellerUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetPendingWalletID(), SellerAmountAfterDiscount, "Move Fund", "For delivered Order.");
                    if (result.equals("success")) {
//                        double AdminAmountAfterDiscount = AdminBalance - DiscountAmountToRefund;
                        double AdminAmountAfterDiscountwithShippingMethodBalance = AdminBalanceAndShippingMethodBalance - DiscountAmountToRefund;
                        result = EngineWalletManager.ComputeWalletRecord(CustomerUserID, AdminUserID, EngineWalletManager.GetMainWalletID(), EngineWalletManager.GetPendingWalletID(), AdminAmountAfterDiscountwithShippingMethodBalance, "Move Fund", "For delivered Order.");
//                        result = EngineWalletManager.ComputeWalletRecord(CustomerUserID, AdminUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetPendingWalletID(), AdminAmountAfterDiscountwithShippingMethodBalance, "Move Fund", "For delivered Order.");
                    }
                }

            }

            if (result.equals("success")) {
                result = UpdateOrderPaymentStatusID(OrderID, PaymentStatusID);
                CreateOrderStatusHistory(OrderID, PaymentStatusID);
                if (result.equals("success")) {
                    String CustomerUserName = EngineUserManager.GetUserName(CustomerUserID);
                    String body = "Hi " + CustomerUserName + ",\n\nThe Order with the Reference Number " + OrderRef + " has been delivered . \n\nCheers \nFyngram";
                    EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), body, "Order Cancelled", CustomerUserID);

                    String SellerUserName = EngineUserManager.GetUserName(SellerUserID);
                    body = "Hi " + SellerUserName + ",\n\nThe Order with the Reference Number " + OrderRef + " that involves your product(s) has been delivered. \n\nCheers \nFyngram";
                    EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), body, "Order Cancelled", SellerUserID);

                    String ShippingMethodName = EngineShippingManager.GetShippingMethodName(ShippingMethodID);
                    body = "Hi " + ShippingMethodName + ",\n\nThe Order with the Reference Number " + OrderRef + " has been delivered. \n\nCheers \nFyngram";

                    try {
                        String CustomerEmail = EngineUserManager.GetUserEmail(CustomerUserID);
                        EngineEmailManager.SendEmail(CustomerEmail, body, "Fyngram Order Delivered");

                        String SellerUserEmail = EngineUserManager.GetUserEmail(SellerUserID);
                        EngineEmailManager.SendEmail(SellerUserEmail, body, "Fyngram Order Delivered");

                        String ShippingMethodEmail = EngineShippingManager.GetShippingMethodEmail(ShippingMethodID);
                        EngineEmailManager.SendEmail(ShippingMethodEmail, body, "Fyngram Order Delivered");
                    } catch (Exception ex) {
                    }
                }
                //update delivery date
                UpdateOrderDeliveryDate(OrderID);
                //update delivery time
                UpdateOrderDeliveryTime(OrderID);
            } else {
                result = "Setting the order status to delivered  could not be completed.";
            }
        } else {
            result = "The Order has already been delivered. or has not been shipped";
        }

        return result;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetOrderIDsByCustomerUserID(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.OrdersTable.ID, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.CustomerUserID + " = " + UserID);
        return IDs;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetOrderIDsBySellerUserID(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.OrdersTable.ID, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.SellerUserID + " = " + UserID);
        return IDs;
    }

    /**
     *
     * @param UserID
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetOrderIDs(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        String UserType = EngineUserManager.GetUserTypeNameByUserID("" + UserID);
        if (UserType.equals("Admin")) {
            IDs = DBManager.GetIntArrayList(Tables.OrdersTable.ID, Tables.OrdersTable.Table, "");
        } else if (UserType.equals("Seller")) {
            IDs = DBManager.GetIntArrayList(Tables.OrdersTable.ID, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.SellerUserID + " = " + UserID);
        } else if (UserType.equals("Customer")) {
            IDs = DBManager.GetIntArrayList(Tables.OrdersTable.ID, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.CustomerUserID + " = " + UserID);
        }
        return IDs;
    }

    /**
     *
     * @param UserID
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetOrderStatusIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        IDs = DBManager.GetIntArrayListDescending(Tables.OrderStatusTable.ID, Tables.OrderStatusTable.Table, "order by " + Tables.OrderStatusTable.Name);
        return IDs;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetOrderShippingMethodByOrderID(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.OrderShippingMethodTable.ShippingMethodID, Tables.OrderShippingMethodTable.Table, "where " + Tables.OrderShippingMethodTable.OrderID + " = " + OrderID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetOrderData(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> result = new HashMap<>();
        result = DBManager.GetTableData(Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        if (!result.isEmpty()) {
            result.put("OrderID", "" + OrderID);
            String customerid = result.get(Tables.OrdersTable.CustomerUserID);
            int CustomerID = Integer.parseInt(customerid);
            result.put("CustomerName", EngineUserManager.GetUserName(CustomerID));

            result.put("product_count", "" + GetOrderHistoryIDs(OrderID).size());
            String sellerid = result.get(Tables.OrdersTable.SellerUserID);
            int SellerID = Integer.parseInt(sellerid);
            //Get Status Details
            JSONObject SellerDet = new JSONObject();
            SellerDet.put("SellerDetails", EngineProductManager.GetSellerInfoData(SellerID));
            if (!SellerDet.isEmpty()) {
                result.putAll(SellerDet);
            }

            String statusid = result.get(Tables.OrdersTable.PaymentStatusID);
            int StatusID = Integer.parseInt(statusid);
            //Get Status Details
            JSONObject StatusDet = new JSONObject();
            StatusDet.put("StatusDetails", GetOrderStatusData(StatusID));
            if (!StatusDet.isEmpty()) {
                result.putAll(StatusDet);
            }
            //Get Payemnt Details
            JSONObject PaymentDet = new JSONObject();
            String OrderReference = result.get(Tables.OrdersTable.Reference);
            PaymentDet.put("PaymentDetails", GetOrderPaymentData(OrderReference));
            if (!PaymentDet.isEmpty()) {
                result.putAll(PaymentDet);
            }

            HashMap<Integer, HashMap<String, String>> OrderStatusHistoryDetList = GetOrderStatusHistoryDataByOrderID(OrderID);
            JSONObject StatusHistoryDet = new JSONObject();
            StatusHistoryDet.put("StatusHistoryDetails", OrderStatusHistoryDetList);
            if (!StatusHistoryDet.isEmpty()) {
                result.putAll(StatusHistoryDet);
            }

            String bkdate = result.get(Tables.OrdersTable.BookingDate);
            String bkDate = DateManager.readDate(bkdate);
            result.put(Tables.OrdersTable.BookingDate, bkDate);

            String bktime = result.get(Tables.OrdersTable.BookingTime);
            String bkTime = DateManager.readTime(bktime);
            result.put(Tables.OrdersTable.BookingTime, bkTime);
        }
        return result;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<Integer, HashMap<String, String>> GetOrderHistoryDataByOrderID(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
        HashMap<String, String> orderHistoryList = new HashMap<>();
        ArrayList<Integer> orderHistIDS = GetOrderHistoryIDs(OrderID);
        if (!orderHistIDS.isEmpty()) {
            for (int orderHistID : orderHistIDS) {
                orderHistoryList = GetOrderHistoryData(orderHistID);
                if (!orderHistoryList.isEmpty()) {
                    String productID = orderHistoryList.get(Tables.OrderHistoryTable.ProductID);
                    int ProductID = Integer.parseInt(productID);
                    HashMap<String, String> ProductDetails = EngineProductManager.GetProductData(ProductID);
                    JSONObject ProductDet = new JSONObject();
                    ProductDet.put("ProductDetails", ProductDetails);
                    if (!ProductDet.isEmpty()) {
                        orderHistoryList.putAll(ProductDet);
                    }
                    List.put(orderHistID, orderHistoryList);
                }
            }
        }
        return List;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetOrderInvoiceData(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> data = DBManager.GetTableData(Tables.OrderInvoicesTable.Table, "where " + Tables.OrderInvoicesTable.OrderID + " = " + OrderID);
        if (!data.isEmpty()) {
            String idate = data.get(Tables.OrderInvoicesTable.Date);
            String InvoiceDate = DateManager.readDate(idate);
            data.put("InvoiceDate", InvoiceDate);
            String itime = data.get(Tables.OrderInvoicesTable.Time);
            String InvoiceTime = DateManager.readTime(itime);
            data.put("InvoiceTime", InvoiceTime);
        }

        return data;
    }

    /**
     *
     * @param OrderReference
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetOrderPaymentData(String OrderReference) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> data = DBManager.GetTableData(Tables.OrderPaymentsTable.Table, "where " + Tables.OrderPaymentsTable.OrderReference + " = '" + OrderReference + "'");
        return data;
    }

    /**
     *
     * @param StatusID
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetOrderStatusData(int StatusID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> data = DBManager.GetTableData(Tables.OrderStatusTable.Table, "where " + Tables.OrderStatusTable.ID + " = " + StatusID);
        return data;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetOrderShippingMethodData(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> data = DBManager.GetTableData(Tables.OrderShippingMethodTable.Table, "where " + Tables.OrderShippingMethodTable.OrderID + " = " + OrderID);
        if (!data.isEmpty()) {
            String shippingmethodid = data.get(Tables.OrderShippingMethodTable.ShippingMethodID);
            int ShippingMethodID = Integer.parseInt(shippingmethodid);
            data.putAll(GetShippingMethodData(ShippingMethodID));
        }

        return data;
    }

    public static HashMap<String, String> GetShippingMethodData(int ShippingID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = new HashMap<>();
        Data = DBManager.GetTableData(Tables.ShippingTable.Table, "where " + Tables.ShippingTable.ID + " = " + ShippingID);
        if (!Data.isEmpty()) {
            String Name = Data.get(Tables.ShippingTable.Name);
            Data.put("ShippingMethodName", Name);
            String Email = Data.get(Tables.ShippingTable.Email);
            Data.put("ShippingMethodEmail", Email);
            String Interval = Data.get(Tables.ShippingTable.DeliveryInterval);
            Data.put("ShippingMethodDelInterval", Interval);
        }
        return Data;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetOrderFullData(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> result = new HashMap<>();
        result = DBManager.GetTableData(Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + OrderID);
        if (!result.isEmpty()) {
            result.put("OrderID", "" + OrderID);

            String customerid = result.get(Tables.OrdersTable.CustomerUserID);
            int CustomerID = Integer.parseInt(customerid);
            //Get Status Details
            JSONObject CustomerDet = new JSONObject();
            CustomerDet.put("CustomerDetails", GetCustomerInfoData(CustomerID));
            if (!CustomerDet.isEmpty()) {
                result.putAll(CustomerDet);
            }
            String sellerid = result.get(Tables.OrdersTable.SellerUserID);
            int SellerID = Integer.parseInt(sellerid);
            //Get Status Details
            JSONObject SellerDet = new JSONObject();
            SellerDet.put("SellerDetails", EngineProductManager.GetSellerInfoData(SellerID));
            if (!SellerDet.isEmpty()) {
                result.putAll(SellerDet);
            }
            String Shippingtypeid = result.get(Tables.OrdersTable.ShippingTypeID);
            int ShippingTypeID = Integer.parseInt(Shippingtypeid);
            String ShippingTypeName = EngineShippingManager.GetShippingTypeNameByID(ShippingTypeID);
            result.put("ShippingTypeName", ShippingTypeName);
            String Shippingaddressid = result.get(Tables.OrdersTable.ShippingAddressID);
            int ShippingAddressID = Integer.parseInt(Shippingaddressid);
            JSONObject ShippingAddressDet = new JSONObject();
            if (ShippingTypeID == 1) {
                ShippingAddressDet.put("ShippingAddressDetails", EngineAddressManager.GetAddressData(ShippingAddressID));
            } else if (ShippingTypeID == 2) {
                ShippingAddressDet.put("ShippingAddressDetails", EngineAddressManager.GetPickUpStationData(ShippingAddressID));
            }
            if (!ShippingAddressDet.isEmpty()) {
                result.putAll(ShippingAddressDet);
            }

            String statusid = result.get(Tables.OrdersTable.PaymentStatusID);
            int StatusID = Integer.parseInt(statusid);

            //Get Status Details
            JSONObject StatusDet = new JSONObject();
            StatusDet.put("StatusDetails", GetOrderStatusData(StatusID));
            if (!StatusDet.isEmpty()) {
                result.putAll(StatusDet);
            }

            HashMap<Integer, HashMap<String, String>> OrderHistoryDetList = GetOrderHistoryDataByOrderID(OrderID);
            JSONObject OrderHistoryDet = new JSONObject();
            OrderHistoryDet.put("HistoryDetails", OrderHistoryDetList);
            if (!OrderHistoryDet.isEmpty()) {
                result.putAll(OrderHistoryDet);
            }
            result.put("product_count", "" + GetOrderHistoryIDs(OrderID).size());

            HashMap<Integer, HashMap<String, String>> OrderStatusHistoryDetList = GetOrderStatusHistoryDataByOrderID(OrderID);
            JSONObject StatusHistoryDet = new JSONObject();
            StatusHistoryDet.put("StatusHistoryDetails", OrderStatusHistoryDetList);
            if (!StatusHistoryDet.isEmpty()) {
                result.putAll(StatusHistoryDet);
            }

            //Get Invoice Details
            JSONObject InvoiceDet = new JSONObject();
            InvoiceDet.put("InvoiceDetails", GetOrderInvoiceData(OrderID));
            if (!InvoiceDet.isEmpty()) {
                result.putAll(InvoiceDet);
            }
            //Get Payemnt Details
            JSONObject PaymentDet = new JSONObject();
            String OrderReference = result.get(Tables.OrdersTable.Reference);
            PaymentDet.put("PaymentDetails", GetOrderPaymentData(OrderReference));
            if (!PaymentDet.isEmpty()) {
                result.putAll(PaymentDet);
            }
            //Get ShippingMethod Details
            JSONObject ShippingMethodDet = new JSONObject();
            ShippingMethodDet.put("ShippingMethodDetails", GetOrderShippingMethodData(OrderID));
            if (!ShippingMethodDet.isEmpty()) {
                result.putAll(ShippingMethodDet);
            }

            String dCodeID = result.get(Tables.OrdersTable.DiscountCodeID);
            if (!dCodeID.equals("") && dCodeID != null) {
                int DiscountCodeID = Integer.parseInt(dCodeID);
                String DiscountCode = EngineDiscountManager.GetDiscountCodeByDiscounCodeID(DiscountCodeID);
                result.put("DiscountCode", DiscountCode);
                int DiscountDeductionTypeID = EngineDiscountManager.GetDiscountCodeDeductionTypeByDiscounCodeID(DiscountCodeID);
                String DiscountDeductionType = EngineDiscountManager.GetDiscountCodeDeductionTypeNameByID(DiscountDeductionTypeID);
                result.put("DiscountDeductionType", DiscountDeductionType);
            }

            String bkdate = result.get(Tables.OrdersTable.BookingDate);
            String bkDate = DateManager.readDate(bkdate);
            result.put(Tables.OrdersTable.BookingDate, bkDate);

            String bktime = result.get(Tables.OrdersTable.BookingTime);
            String bkTime = DateManager.readTime(bktime);
            result.put(Tables.OrdersTable.BookingTime, bkTime);

            String dldate = result.get(Tables.OrdersTable.DeliveryDate);
            if (!dldate.equals("") && !dldate.equals("null")) {
                String dlDate = DateManager.readDate(dldate);
                result.put(Tables.OrdersTable.DeliveryDate, dlDate);
            }

            String dltime = result.get(Tables.OrdersTable.DeliveryTime);
            if (!dltime.equals("") && !dltime.equals("null")) {
                String dlTime = DateManager.readTime(dltime);
                result.put(Tables.OrdersTable.DeliveryTime, dlTime);
            }

        }
        return result;
    }

    /**
     *
     * @param CustomerUserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetCustomerInfoData(int CustomerUserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = new HashMap<>();
        Data = DBManager.GetTableData(Tables.CustomersTable.Table, "where " + Tables.CustomersTable.UserID + " = " + CustomerUserID);
        if (!Data.isEmpty()) {
            String date = DBManager.GetString(Tables.UsersTable.Date, Tables.UsersTable.Table, "where " + Tables.UsersTable.ID + " = " + CustomerUserID);
            String dateJoined = DateManager.readDate(date);
            Data.put("CustEmail", EngineUserManager.GetUserEmail(CustomerUserID));
            Data.put("CustPhone", EngineUserManager.GetUserPhone(CustomerUserID));
            Data.put("CustName", EngineUserManager.GetUserName(CustomerUserID));
            Data.put("CustDateReg", dateJoined);
        }
        return Data;
    }

    /**
     *
     * @param OrderStatusHistoryID
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetOrderStatusHistoryData(int OrderStatusHistoryID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = new HashMap<>();
        Data = DBManager.GetTableData(Tables.OrderStatusHistoryTable.Table, "where " + Tables.OrderStatusHistoryTable.ID + " = " + OrderStatusHistoryID);
        if (!Data.isEmpty()) {
            String statusid = Data.get(Tables.OrderStatusHistoryTable.StatusID);
            int StatusID = Integer.parseInt(statusid);
            HashMap<String, String> res = GetOrderStatusData(StatusID);
            String tm = Data.get(Tables.OrderStatusHistoryTable.Time);
            String time = DateManager.readTime(tm);
            Data.put(Tables.OrderStatusHistoryTable.Time, time);

            String dt = Data.get(Tables.OrderStatusHistoryTable.Date);
            String date = DateManager.readDate(dt);
            Data.put(Tables.OrderStatusHistoryTable.Date, date);
            Data.putAll(res);
        }
        return Data;
    }

    public static HashMap<Integer, HashMap<String, String>> GetOrderStatusHistoryDataByOrderID(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
        HashMap<String, String> orderHistoryList = new HashMap<>();
        ArrayList<Integer> orderHistIDS = GetOrderStatusHistoryIDs(OrderID);
        if (!orderHistIDS.isEmpty()) {
            for (int orderHistID : orderHistIDS) {
                orderHistoryList = GetOrderStatusHistoryData(orderHistID);
                if (!orderHistoryList.isEmpty()) {
                    List.put(orderHistID, orderHistoryList);
                }
            }
        }
        return List;
    }

    /**
     *
     * @param OrderID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetOrderStatusHistoryIDs(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        IDs = DBManager.GetIntArrayList(Tables.OrderStatusHistoryTable.ID, Tables.OrderStatusHistoryTable.Table, "where " + Tables.OrderStatusHistoryTable.OrderID + " = " + OrderID);
        return IDs;
    }

    public static String UpdateSellerPayment() throws ParseException {
        String result = "failed";
        LocalTime TimeNow = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String TimeNowString = TimeNow.format(formatter);
        try {
            String savedIntervalCheckTime = DBManager.GetTime(Tables.SellerPaymentIntervalTable.Time, Tables.SellerPaymentIntervalTable.Table, "where " + Tables.SellerPaymentIntervalTable.ID + " = " + 1);
            if (!savedIntervalCheckTime.equals("none")) {
                int interval = DBManager.GetInt(Tables.SellerPaymentIntervalTable.HourIntervalCheck, Tables.SellerPaymentIntervalTable.Table, "where " + Tables.SellerPaymentIntervalTable.ID + " = " + 1);
                String NextIntervalCheckTime = "" + LocalTime.parse(savedIntervalCheckTime).plusHours(interval);
                boolean isAfter = LocalTime.parse(TimeNowString).isAfter(LocalTime.parse(NextIntervalCheckTime));
                if (isAfter) {
                    result = ProcessSellerPayment();
                }
            } else {
                result = CreateSellerPaymentInterval(2);
                result = ProcessSellerPayment();
            }
        } catch (Exception ex) {

        }
        return result;
    }

    public static String CreateSellerPaymentInterval(int IntervalInHours) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.SellerPaymentIntervalTable.HourIntervalCheck, IntervalInHours);
        int id = DBManager.insertTableDataReturnID(Tables.SellerPaymentIntervalTable.Table, tableData, "");
        String result = DBManager.UpdateCurrentTime(Tables.SellerPaymentIntervalTable.Table, Tables.SellerPaymentIntervalTable.Time, "where " + Tables.SellerPaymentIntervalTable.ID + " = " + id);
        return result;
    }

    public static String ProcessSellerPayment() throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.OrdersTable.ID, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.PaymentStatusID + " = " + 5);//Delivered
        LocalDate CurrentDate = LocalDate.now();
        LocalTime TimeNow = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String CurrentTimeString = TimeNow.format(formatter);
        LocalTime CurrentTime = LocalTime.parse(CurrentTimeString);
        if (!IDs.isEmpty()) {
            for (int id : IDs) {
                String orderDate = DBManager.GetString(Tables.OrdersTable.DeliveryDate, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + id);
                String orderTime = DBManager.GetString(Tables.OrdersTable.DeliveryTime, Tables.OrdersTable.Table, "where " + Tables.OrdersTable.ID + " = " + id);
                try {
                    LocalDate TxnDate = LocalDate.parse(orderDate);
                    LocalDate DateToPay = TxnDate.plusDays(2);
                    LocalTime TxnTime = LocalTime.parse(orderTime);
                    LocalTime TimeToPay = TxnTime.plusHours(48);
                    if (CurrentDate.isAfter(DateToPay) || CurrentDate.equals(DateToPay)) {
                        boolean isTimeAfter = CurrentTime.isAfter(TimeToPay);
                        if (isTimeAfter) {
                            result = ComputeSettledOrder(id);
                        } else {
                            result = "Not yet time or it has been completed";
                        }
                    } else {
                        result = "Not yet date or it has been completed";
                    }
                } catch (Exception ex) {
                    ex.getMessage();
                }
            }
        } else {
            result = "No Orders to process";
        }
        return result;
    }

    public static String ComputeSettledOrder(int OrderID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException, IOException {
        String result = "failed";
        int CurrentOrderStatus = GetOrderPaymentStatusID(OrderID);
        if (CurrentOrderStatus == 5 || CurrentOrderStatus == 6) {
            int SellerUserID = GetOrderSellerUserID(OrderID);
            double SellerAmount = GetOrderSellerAmount(OrderID);
            int SellerTypeID = EngineSubscriptionManager.GetSellerTypeIDBySellerUserID(SellerUserID);
            double AdminTransactionPercent = EngineTransactionManager.GetAdminTransactionPercentageBySellerType(SellerTypeID);
            double AdminTransactionShare = EngineDiscountManager.ComputePercentageAmount(AdminTransactionPercent, SellerAmount);
            double SellerTransactionShare = SellerAmount - AdminTransactionShare;
            double SellerBalance = SellerTransactionShare;
            double DiscountFees = GetOrderDiscountAmount(OrderID);
            String OrderRef = GetOrderReferenceNumber(OrderID);
            int DiscountCodeID = GetOrderDiscountCodeID(OrderID);
            ArrayList<Integer> OrderIdsByRef = GetOrderIDsByReferenceNumber(OrderRef);
            if (DiscountFees == 0) {
                result = EngineWalletManager.ComputeWalletRecord(SellerUserID, SellerUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), SellerBalance, "Move Fund", "For delivered Order.");
            } else {
                int SplitDiscountDeductionValue = EngineDiscountManager.GetDiscountSplitDeductionValue(DiscountCodeID);
                if (SplitDiscountDeductionValue == 0) {
                    result = EngineWalletManager.ComputeWalletRecord(SellerUserID, SellerUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), SellerBalance, "Move Fund", "For delivered Order.");

                } else {
                    double withDiscountAmount = (DiscountFees / OrderIdsByRef.size());
                    double DiscountAmountToRefund = (withDiscountAmount / 2);
                    double SellerAmountAfterDiscount = SellerBalance - DiscountAmountToRefund;
                    result = EngineWalletManager.ComputeWalletRecord(SellerUserID, SellerUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), SellerAmountAfterDiscount, "Move Fund", "For delivered Order.");
                }
            }

            if (result.equals("success")) {
                result = UpdateOrderPaymentStatusID(OrderID, 7);//settled
                CreateOrderStatusHistory(OrderID, 7);//settled

                String SellerUserName = EngineUserManager.GetUserName(SellerUserID);
                String body = "Hi " + SellerUserName + ",\n\nThe Order Amount of " + EngineTransactionManager.FormatNumber(SellerBalance) + "involved  with the Order Reference Number " + OrderRef + " has been transfered into your Wallet. \n\nCheers \nFyngram";
                EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), body, "Order Cancelled", SellerUserID);
                String SellerUserEmail = EngineUserManager.GetUserEmail(SellerUserID);
                EngineEmailManager.SendEmail(SellerUserEmail, body, "Fyngram Order Delivered");

            }
        }

        return result;
    }
}
