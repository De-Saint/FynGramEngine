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
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mac
 */
public class EngineStockManager {

    /**
     *
     * @param Name
     * @param OrderID
     * @param ProductID
     * @param ProductPreviousQuantity
     * @param ProductQuantity
     * @param ProductCurrentQuantity
     * @param SellerUserID
     * @param CustomerUserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateStockMovement(String Name, int OrderID, int ProductID, int ProductPreviousQuantity, int ProductQuantity, int ProductCurrentQuantity, int SellerUserID, int CustomerUserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.StockMovementTable.Name, Name);
        tableData.put(Tables.StockMovementTable.OrderID, OrderID);
        tableData.put(Tables.StockMovementTable.ProductID, ProductID);
        tableData.put(Tables.StockMovementTable.SellerUserID, SellerUserID);
        tableData.put(Tables.StockMovementTable.CustomerUserID, CustomerUserID);
        tableData.put(Tables.StockMovementTable.ProductPreviousQuantity, ProductPreviousQuantity);
        tableData.put(Tables.StockMovementTable.ProductQuantity, ProductQuantity);
        tableData.put(Tables.StockMovementTable.ProductCurrentQuantity, ProductCurrentQuantity);
        int StockID = DBManager.insertTableDataReturnID(Tables.StockMovementTable.Table, tableData, "");
        String result = DBManager.UpdateCurrentDate(Tables.StockMovementTable.Table, Tables.StockMovementTable.Date, "where " + Tables.StockMovementTable.ID + " = " + StockID);
        DBManager.UpdateCurrentTime(Tables.StockMovementTable.Table, Tables.StockMovementTable.Time, "where " + Tables.StockMovementTable.ID + " = " + StockID);
        return result;
    }

    /**
     *
     * @param OrderID
     * @param Option
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputeStockMovement(int OrderID, String Option) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, IOException {
        String result = "failed";
        ArrayList<Integer> OrderHistoryIDs = EngineOrderManager.GetOrderHistoryIDs(OrderID);
        HashMap<String, String> OrderHistoryData = new HashMap<>();
        String MovementName = "";
        if (!OrderHistoryIDs.isEmpty()) {
            for (int HistoryID : OrderHistoryIDs) {
                OrderHistoryData = EngineOrderManager.GetOrderHistoryData(HistoryID);
                if (!OrderHistoryData.isEmpty()) {
                    int ProductID = Integer.parseInt(OrderHistoryData.get(Tables.OrderHistoryTable.ProductID));
                    int OrderedProductQuantity = Integer.parseInt(OrderHistoryData.get(Tables.OrderHistoryTable.Quantity));
                    int PreviousProductQuantity = EngineProductManager.GetProductQuantityByProductID(ProductID);
                    int SellerUserID = EngineOrderManager.GetOrderSellerUserID(OrderID);
                    int CustomerUserID = EngineOrderManager.GetOrderCustomerUserID(OrderID);
                    int NewProductTotalQuantity = 0;
                    if (Option.equals("Increase")) {//Refund
                        NewProductTotalQuantity = PreviousProductQuantity + OrderedProductQuantity;
                        MovementName = "Product Returned";
                    } else if (Option.equals("Decrease")) { //Order 
                        NewProductTotalQuantity = PreviousProductQuantity - OrderedProductQuantity;
                        int minimumQty = EngineProductManager.GetProductMinimumQuantityByProductID(ProductID);
                        result = ComputeStockEvaluation(minimumQty, NewProductTotalQuantity, ProductID, SellerUserID);
                        MovementName = "Product Ordered";
                    }
                    int ShippingMethodID = EngineOrderManager.GetOrderShippingMethodByOrderID(OrderID);
                    result = EngineShippingManager.UpdateShippingMethodNumberOfDelivery(ShippingMethodID, "Subtract");
                    result = EngineProductManager.UpdateProductQuantityByProductID(ProductID, NewProductTotalQuantity);
                    if (result.equals("success")) {
                        result = CreateStockMovement(MovementName, OrderID, ProductID, PreviousProductQuantity, OrderedProductQuantity, NewProductTotalQuantity, SellerUserID, CustomerUserID);
                        if (!result.endsWith("success")) {
                            result = "Product Quantity Management could not be completed.";
                        }
                    } else {
                        result = "Updating Product Quantity could not be completed.";
                    }
                }
            }
        }
        return result;
    }

    /**
     *
     * @param minimumQty
     * @param NewProductTotalQuantity
     * @param ProductID
     * @param SellerUserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputeStockEvaluation(int minimumQty, int NewProductTotalQuantity, int ProductID, int SellerUserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, IOException {
        String result = "failed";
        int NotyTypeID = GetProductStockNotificationTypeID(ProductID);
        String ProductName = EngineProductManager.GetProductNameByProductID(ProductID);

        if (minimumQty >= NewProductTotalQuantity) {
            String BodyMsg = "Hi " + EngineUserManager.GetUserName(SellerUserID) + ", \nYour Pruduct " + ProductName + " has gotten to its minumum quantity set. Please, restock the product as it has been removed from the store.";
            String Subject = "Fyngram Product ReStock Notification";
            switch (NotyTypeID) {
                //Email Only
                case 1:
                    try {
                    EngineEmailManager.SendEmail(EngineUserManager.GetUserEmail(SellerUserID), BodyMsg, Subject);
                } catch (UnsupportedEncodingException | ClassNotFoundException | SQLException Ex) {
                    System.out.println(Ex.getMessage());
                }
                break;
                case 2:
                    //Internal Messge Only
                    result = EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), BodyMsg, Subject, SellerUserID);
                    break;
                case 3:
                    //Both
                    result = EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), BodyMsg, Subject, SellerUserID);
                    if (result.equals("success")) {
                        try {
                            EngineEmailManager.SendEmail(EngineUserManager.GetUserEmail(SellerUserID), BodyMsg, Subject);
                        } catch (UnsupportedEncodingException | ClassNotFoundException | SQLException Ex) {
                            System.out.println(Ex.getMessage());
                        }
                    }
                    break;
                default:
                    break;
            }
        } else {
            result = "success";
        }

        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetProductStockNotificationTypeID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.ProductStockLevelTable.NotificationTypeID, Tables.ProductStockLevelTable.Table, "where " + Tables.ProductStockLevelTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @param NewQuantity
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ProductRestock(int ProductID, int NewQuantity) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateIntData(Tables.ProductQuantityTable.TotalQuantity, NewQuantity, Tables.ProductQuantityTable.Table, "where " + Tables.ProductQuantityTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param UserID
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetStockIDs(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        String UserType = EngineUserManager.GetUserTypeNameByUserID("" + UserID);
        if (UserType.equals("Admin")) {
            IDs = DBManager.GetIntArrayList(Tables.StockMovementTable.ID, Tables.StockMovementTable.Table, "");
        } else if (UserType.equals("Seller")) {
            IDs = DBManager.GetIntArrayList(Tables.StockMovementTable.ID, Tables.StockMovementTable.Table, "where " + Tables.StockMovementTable.SellerUserID + " = " + UserID);
        } else if (UserType.equals("Customer")) {
            IDs = DBManager.GetIntArrayList(Tables.StockMovementTable.ID, Tables.StockMovementTable.Table, "where " + Tables.StockMovementTable.CustomerUserID + " = " + UserID + " and " + Tables.StockMovementTable.Name + " = 'Product Returned'");
        }
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
    public static HashMap<String, String> GetStockMovementData(int StockID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> result = new HashMap<>();
        result = DBManager.GetTableData(Tables.StockMovementTable.Table, "where " + Tables.StockMovementTable.ID + " = " + StockID);
        if (!result.isEmpty()) {
            result.put("StockID", "" + StockID);
            String customerid = result.get(Tables.StockMovementTable.CustomerUserID);
            int CustomerUserID = Integer.parseInt(customerid);
            result.put("CustomerName", EngineUserManager.GetUserName(CustomerUserID));

            String sellerid = result.get(Tables.StockMovementTable.SellerUserID);
            int SellerUserID = Integer.parseInt(sellerid);
            String SellerName = DBManager.GetString(Tables.SellerInfoTable.BusinessName, Tables.SellerInfoTable.Table, "where " + Tables.SellerInfoTable.SellerUserID + " = " + SellerUserID);
            result.put("SellerName", SellerName);

            String productid = result.get(Tables.StockMovementTable.ProductID);
            int ProductID = Integer.parseInt(productid);
            String ProductName = DBManager.GetString(Tables.ProductInfoTable.Name, Tables.ProductInfoTable.Table, "where " + Tables.ProductInfoTable.ProductID + " = " + ProductID);
            result.put("ProductName", ProductName);

            String bkdate = result.get(Tables.StockMovementTable.Date);
            String bkDate = DateManager.readDate(bkdate);
            result.put(Tables.StockMovementTable.Date, bkDate);

            String bktime = result.get(Tables.StockMovementTable.Time);
            String bkTime = DateManager.readTime(bktime);
            result.put(Tables.StockMovementTable.Time, bkTime);
        }
        return result;
    }

}
