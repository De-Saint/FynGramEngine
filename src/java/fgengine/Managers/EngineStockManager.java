/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import fgengine.Tables.Tables;
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
    public static String ComputeStockMovement(int OrderID, String Option) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
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
                        MovementName = "Product Refunded";
                    } else if (Option.equals("Decrease")) { //Order 
                        NewProductTotalQuantity = PreviousProductQuantity - OrderedProductQuantity;
                        int minimumQty = EngineProductManager.GetProductMinimumQuantityByProductID(ProductID);
                        result = ComputeStockEvaluation(minimumQty, NewProductTotalQuantity, ProductID, SellerUserID);
                        MovementName = "Product Ordered";
                    }
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
    public static String ComputeStockEvaluation(int minimumQty, int NewProductTotalQuantity, int ProductID, int SellerUserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int NotyTypeID = GetProductStockNotificationTypeID(ProductID);
        String ProductName = EngineProductManager.GetProductNameByProductID(ProductID);

        if (minimumQty >= NewProductTotalQuantity) {
            String BodyMsg = "Hi " + EngineUserManager.GetUserName(SellerUserID) + ", \nYour Pruduct " + ProductName + " has gotten to its minumum quantity set. Please, restock the product as it has been removed from the store.";
            String Subject = "FynGram Product ReStock Notification";
            switch (NotyTypeID) {
                //Email Only
                case 1:
                    try {
                    result = EngineEmailManager.SendEmail(EngineUserManager.GetUserEmail(SellerUserID), BodyMsg, Subject);
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
                            result = EngineEmailManager.SendEmail(EngineUserManager.GetUserEmail(SellerUserID), BodyMsg, Subject);
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
}
