/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import fgengine.Tables.Tables;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONObject;

/**
 *
 * @author mac
 */
public class EngineCartManager {

    /**
     *
     * @param UserID
     * @param GuestID
     * @param Amount
     * @param ProductCount
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int CreateCart(String UserID, String Amount, int ProductCount) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.CartTable.UserID, UserID);
        tableData.put(Tables.CartTable.Amount, Amount);
        tableData.put(Tables.CartTable.TotalAmount, Amount);
        tableData.put(Tables.CartTable.ProductCount, ProductCount);
        int CartID = DBManager.insertTableDataReturnID(Tables.CartTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.CartTable.Table, Tables.CartTable.Date, "where " + Tables.CartTable.ID + " = " + CartID);
        DBManager.UpdateCurrentTime(Tables.CartTable.Table, Tables.CartTable.Time, "where " + Tables.CartTable.ID + " = " + CartID);
        return CartID;
    }

    /**
     *
     * @param CartID
     * @param ProductID
     * @param ProductPrice
     * @param ProductQuantity
     * @param Option
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputeCartProductDetails(int CartID, int ProductID, double ProductPrice, int ProductQuantity, String Action) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int CartProductDetailsID = DBManager.GetInt(Tables.CartProductDetailsTable.ID, Tables.CartProductDetailsTable.Table, "where " + Tables.CartProductDetailsTable.CartID + " = " + CartID + " And " + Tables.CartProductDetailsTable.ProductID + " = " + ProductID);
        if (CartProductDetailsID != 0) {
            String oldProductPrice = GetCartProductPriceByID(CartProductDetailsID);
            double OldProductPrice = Double.parseDouble(oldProductPrice);

            int OldProductQuantity = GetCartProductQuantityByID(CartProductDetailsID);
            double NewProductPrice = 0.0;
            int NewProductQuantity = 0;
            if (Action.equals("Increase")) {
                NewProductPrice = OldProductPrice + ProductPrice;
                NewProductQuantity = OldProductQuantity + ProductQuantity;
            } else if (Action.equals("Decrease")) {
                NewProductPrice = OldProductPrice - ProductPrice;
                NewProductQuantity = OldProductQuantity - ProductQuantity;
            }
            result = UpdateCartProductPrice(CartProductDetailsID, "" + NewProductPrice);
            if (result.equals("success")) {
                result = UpdateCartProductQuantity(CartProductDetailsID, NewProductQuantity);
                if (result.equals("success")) {
                    if (NewProductQuantity == 0 && NewProductPrice == 0) {
                        result = DeleteCartProduct(CartID, ProductID);
                    }
                } else {
                    result = "Cart Product Quantity could not be updated";
                }
            } else {
                result = "Cart Product Price could not be updated";
            }
        } else {
            result = CreateCartProductDetails(CartID, ProductID, "" + ProductPrice, ProductQuantity);
        }

        return result;
    }

    /**
     *
     * @param CartProductDetailsID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetCartProductPriceByID(int CartProductDetailsID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.CartProductDetailsTable.ProductPrice, Tables.CartProductDetailsTable.Table, "where " + Tables.CartProductDetailsTable.ID + " = " + CartProductDetailsID);
        return result;
    }

    /**
     *
     * @param CartProductDetailsID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetCartProductQuantityByID(int CartProductDetailsID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int Qty = DBManager.GetInt(Tables.CartProductDetailsTable.ProductQuantity, Tables.CartProductDetailsTable.Table, "where " + Tables.CartProductDetailsTable.ID + " = " + CartProductDetailsID);
        return Qty;
    }

    /**
     *
     * @param CartProductDetailsID
     * @param NewProductQuantity
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateCartProductQuantity(int CartProductDetailsID, int NewProductQuantity) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateIntData(Tables.CartProductDetailsTable.ProductQuantity, NewProductQuantity, Tables.CartProductDetailsTable.Table, "where " + Tables.CartProductDetailsTable.ID + " = " + CartProductDetailsID);
        return result;
    }

    /**
     *
     * @param CartProductDetailsID
     * @param NewProductPrice
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateCartProductPrice(int CartProductDetailsID, String NewProductPrice) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateStringData(Tables.CartProductDetailsTable.Table, Tables.CartProductDetailsTable.ProductPrice, NewProductPrice, "where " + Tables.CartProductDetailsTable.ID + " = " + CartProductDetailsID);
        return result;
    }

    /**
     *
     * @param CartID
     * @param ProductID
     * @param ProductPrice
     * @param ProductQuantity
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateCartProductDetails(int CartID, int ProductID, String ProductPrice, int ProductQuantity) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.CartProductDetailsTable.CartID, CartID);
        tableData.put(Tables.CartProductDetailsTable.ProductID, ProductID);
        tableData.put(Tables.CartProductDetailsTable.ProductPrice, ProductPrice);
        tableData.put(Tables.CartProductDetailsTable.ProductQuantity, ProductQuantity);
        result = DBManager.insertTableData(Tables.CartProductDetailsTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param UserID
     * @param GuestID
     * @param UserType
     * @param ProductID
     * @param ProductPrice
     * @param ProductQuantity
     * @param Action
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputeCart(String UserID, int ProductID, double ProductPrice, int ProductQuantity, String Action) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int ExistingCartID = GetCartIDByUserID(UserID);

        if (ExistingCartID != 0) {
            //update the amount in the cart table
            String oldAmount = GetCartAmountByUserID(UserID);
            double OldAmount = Double.parseDouble(oldAmount);
            String oldTotalAmount = GetCartTotalAmountByUserID(UserID);
            double OldTotalAmount = Double.parseDouble(oldTotalAmount);
            int OldProductCount = GetCartProductCountByUserID(UserID);
            double NewAmount = 0.0;
            double NewTotalAmount = 0.0;
            int NewProductCount = 0;

            if (Action.equals("Increase")) {
                NewAmount = OldAmount + ProductPrice;
                NewTotalAmount = OldTotalAmount + ProductPrice;
                NewProductCount = OldProductCount + ProductQuantity;
            } else if (Action.equals("Decrease")) {
                NewAmount = OldAmount - ProductPrice;
                NewTotalAmount = OldTotalAmount - ProductPrice;
                NewProductCount = OldProductCount - ProductQuantity;
            }
            result = UpdateCartAmount(ExistingCartID, "" + NewAmount);
            if (result.equals("success")) {
                result = UpdateCartTotalAmount(ExistingCartID, "" + NewTotalAmount);
                if (result.equals("success")) {
                    result = UpdateCartProductCount(ExistingCartID, NewProductCount);
                    if (result.equals("success")) {
                        result = ComputeCartProductDetails(ExistingCartID, ProductID, ProductPrice, ProductQuantity, Action);
                    } else {
                        result = "Cart total product count could not be updated";
                    }
                } else {
                    result = "Cart total amount count could not be updated";
                }
            } else {
                result = "Cart amount could not be updated";
            }
        } else {
            int CartID = CreateCart(UserID, "" + ProductPrice, ProductQuantity);
            result = ComputeCartProductDetails(CartID, ProductID, ProductPrice, ProductQuantity, Action);
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
    public static String GetCartAmountByUserID(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.CartTable.Amount, Tables.CartTable.Table, "where " + Tables.CartTable.UserID + " = '" + UserID + "'");
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
    public static String GetCartTotalAmountByUserID(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.CartTable.TotalAmount, Tables.CartTable.Table, "where " + Tables.CartTable.UserID + " = '" + UserID + "'");
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
    public static String GetCartAmountByCartID(int CartID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.CartTable.Amount, Tables.CartTable.Table, "where " + Tables.CartTable.ID + " = " + CartID);
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
    public static String GetCartTotalAmountByCartID(int CartID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.CartTable.TotalAmount, Tables.CartTable.Table, "where " + Tables.CartTable.ID + " = " + CartID);
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
    public static int GetCartProductCountByUserID(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.CartTable.ProductCount, Tables.CartTable.Table, "where " + Tables.CartTable.UserID + " = '" + UserID + "'");
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
    public static int GetCartProductCountByCartID(int CartID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.CartTable.ProductCount, Tables.CartTable.Table, "where " + Tables.CartTable.ID + " = " + CartID);
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
    public static int GetCartIDByUserID(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.CartTable.ID, Tables.CartTable.Table, "where " + Tables.CartTable.UserID + " = '" + UserID + "'");
        return result;
    }

    /**
     *
     * @param CartID
     * @param NewProductCount
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateCartProductCount(int CartID, int NewProductCount) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateIntData(Tables.CartTable.ProductCount, NewProductCount, Tables.CartTable.Table, "where " + Tables.CartTable.ID + " = " + CartID);
        return result;
    }

    /**
     *
     * @param CartID
     * @param NewAmount
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateCartAmount(int CartID, String NewAmount) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateStringData(Tables.CartTable.Table, Tables.CartTable.Amount, NewAmount, "where " + Tables.CartTable.ID + " = " + CartID);
        return result;
    }

    /**
     *
     * @param CartID
     * @param NewAmount
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateCartTotalAmount(int CartID, String NewAmount) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateStringData(Tables.CartTable.Table, Tables.CartTable.TotalAmount, NewAmount, "where " + Tables.CartTable.ID + " = " + CartID);
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
    public static String DeleteOrEmtpyCart(int CartID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        ArrayList<Integer> CartProductDetailIDs = GetCartProductDetailsIDsByCartID(CartID);
        if (!CartProductDetailIDs.isEmpty()) {
            for (int CartProductDetailsID : CartProductDetailIDs) {
                result = DBManager.DeleteObject(Tables.CartProductDetailsTable.Table, "where " + Tables.CartProductDetailsTable.ID + " = " + CartProductDetailsID);
            }
        }
        if (result.equals("success")) {
            result = DBManager.DeleteObject(Tables.CartTable.Table, "where " + Tables.CartTable.ID + " = " + CartID);
        } else {
            result = "Error - Empting or Deleting Cart Product(s) could not be completed. Please, try again";
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
    public static ArrayList<Integer> GetCartProductDetailsIDsByCartID(int CartID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.CartProductDetailsTable.ID, Tables.CartProductDetailsTable.Table, "where " + Tables.CartProductDetailsTable.CartID + " = " + CartID);
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetCartIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.CartTable.ID, Tables.CartTable.Table, "order by id");
        return IDs;
    }

    /**
     *
     * @param CartID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetCartProductDetailsDataByCartID(int CartID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = DBManager.GetTableData(Tables.CartProductDetailsTable.Table, "where " + Tables.CartProductDetailsTable.CartID + " = " + CartID);
        return Data;
    }

    /**
     *
     * @param CartProductDetailsID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetCartProductDetailsDataByID(int CartProductDetailsID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = DBManager.GetTableData(Tables.CartProductDetailsTable.Table, "where " + Tables.CartProductDetailsTable.ID + " = " + CartProductDetailsID);
        return Data;
    }

    //---------------------------------------------------------------WishList--------------------------------------------------------//
    /**
     *
     * @param UserID
     * @param Amount
     * @param ProductCount
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int CreateWishList(String UserID, String Amount, int ProductCount) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.WishlistTable.UserID, UserID);
        tableData.put(Tables.WishlistTable.Amount, Amount);
        tableData.put(Tables.WishlistTable.ProductCount, ProductCount);
        int WishD = DBManager.insertTableDataReturnID(Tables.WishlistTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.WishlistTable.Table, Tables.WishlistTable.Date, "where " + Tables.WishlistTable.ID + " = " + WishD);
        DBManager.UpdateCurrentTime(Tables.WishlistTable.Table, Tables.WishlistTable.Time, "where " + Tables.WishlistTable.ID + " = " + WishD);
        return WishD;
    }

    /**
     *
     * @param WishlistID
     * @param ProductID
     * @param ProductPrice
     * @param ProductQuantity
     * @param Option
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputeWishListProductDetails(int WishlistID, int ProductID, double ProductPrice, int ProductQuantity, String Option) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int WishlistProductDetailsID = DBManager.GetInt(Tables.WishlistProductDetailsTable.ID, Tables.WishlistProductDetailsTable.Table, "where " + Tables.WishlistProductDetailsTable.WishlistID + " = " + WishlistID + " And " + Tables.CartProductDetailsTable.ProductID + " = " + ProductID);
        if (WishlistProductDetailsID != 0) {
            result = "Product has already been added to your Saved Items";
        } else {
            result = CreateWishListProductDetails(WishlistID, ProductID, "" + ProductPrice, ProductQuantity);
        }

        return result;
    }

    /**
     *
     * @param WishListProductDetailsID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetWishListProductPriceByID(int WishListProductDetailsID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.WishlistProductDetailsTable.ProductPrice, Tables.WishlistProductDetailsTable.Table, "where " + Tables.WishlistProductDetailsTable.ID + " = " + WishListProductDetailsID);
        return result;
    }

    /**
     *
     * @param WishListProductDetailsID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetWishListProductQuantityByID(int WishListProductDetailsID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int Qty = DBManager.GetInt(Tables.WishlistProductDetailsTable.ProductQuantity, Tables.WishlistProductDetailsTable.Table, "where " + Tables.WishlistProductDetailsTable.ID + " = " + WishListProductDetailsID);
        return Qty;
    }

    /**
     *
     * @param WishListProductDetailsID
     * @param NewProductQuantity
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateWishListProductQuantity(int WishListProductDetailsID, int NewProductQuantity) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateIntData(Tables.WishlistProductDetailsTable.ProductQuantity, NewProductQuantity, Tables.WishlistProductDetailsTable.Table, "where " + Tables.WishlistProductDetailsTable.ID + " = " + WishListProductDetailsID);
        return result;
    }

    /**
     *
     * @param WishListProductDetailsID
     * @param NewProductPrice
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateWishListProductPrice(int WishListProductDetailsID, String NewProductPrice) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateStringData(Tables.WishlistProductDetailsTable.Table, Tables.WishlistProductDetailsTable.ProductPrice, NewProductPrice, "where " + Tables.WishlistProductDetailsTable.ID + " = " + WishListProductDetailsID);
        return result;
    }

    /**
     *
     * @param WishlistID
     * @param ProductID
     * @param ProductPrice
     * @param ProductQuantity
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateWishListProductDetails(int WishlistID, int ProductID, String ProductPrice, int ProductQuantity) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.WishlistProductDetailsTable.WishlistID, WishlistID);
        tableData.put(Tables.WishlistProductDetailsTable.ProductID, ProductID);
        tableData.put(Tables.WishlistProductDetailsTable.ProductPrice, ProductPrice);
        tableData.put(Tables.WishlistProductDetailsTable.ProductQuantity, ProductQuantity);
        result = DBManager.insertTableData(Tables.WishlistProductDetailsTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param UserID
     * @param ProductID
     * @param ProductPrice
     * @param Option
     * @param ProductQuantity
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputeWishList(String UserID, int ProductID, double ProductPrice, int ProductQuantity, String Option) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int ExistingWishListID = GetWishListIDByUserID(UserID);
        if (ExistingWishListID != 0) {
            //update the amount in the cart table
            String oldAmount = GetWishListAmountByUserID(UserID);
            double OldAmount = Double.parseDouble(oldAmount);
            double NewAmount = 0.0;
            if (Option.equals("Increase")) {
                NewAmount = OldAmount + ProductPrice;
            } else if (Option.equals("Decrease")) {
                NewAmount = OldAmount - ProductPrice;
            }
            //create new cart product details
            result = ComputeWishListProductDetails(ExistingWishListID, ProductID, ProductPrice, ProductQuantity, Option);
            if (result.equals("success")) {
                //update the product count in the cart
                int OldProductCount = GetWishListProductCountByUserID(UserID);
                int NewProductCount = 0;
                if (Option.equals("Increase")) {
                    NewProductCount = OldProductCount + ProductQuantity;
                } else if (Option.equals("Decrease")) {
                    NewProductCount = OldProductCount - ProductQuantity;
                }
                result = UpdateWishListProductCount(ExistingWishListID, NewProductCount);
                if (result.equals("success")) {
                    result = UpdateWishListAmount(ExistingWishListID, "" + NewAmount);
                } else {
                    result = "WishList total product count could not be updated";
                }
            }
        } else {
            int WishListID = CreateWishList(UserID, "" + ProductPrice, ProductQuantity);
            result = ComputeWishListProductDetails(WishListID, ProductID, ProductPrice, ProductQuantity, Option);
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
    public static String GetWishListAmountByUserID(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.WishlistTable.Amount, Tables.WishlistTable.Table, "where " + Tables.WishlistTable.UserID + " = '" + UserID + "'");
        return result;
    }

    /**
     *
     * @param WishListID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetWishListAmountByWishListID(int WishListID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.WishlistTable.Amount, Tables.WishlistTable.Table, "where " + Tables.WishlistTable.ID + " = " + WishListID);
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
    public static int GetWishListProductCountByUserID(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.WishlistTable.ProductCount, Tables.WishlistTable.Table, "where " + Tables.WishlistTable.UserID + " = '" + UserID + "'");
        return result;
    }

    /**
     *
     * @param WishListID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetWishListProductCountByWishListID(int WishListID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.WishlistTable.ProductCount, Tables.WishlistTable.Table, "where " + Tables.WishlistTable.ID + " = " + WishListID);
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
    public static int GetWishListIDByUserID(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.WishlistTable.ID, Tables.WishlistTable.Table, "where " + Tables.WishlistTable.UserID + " = '" + UserID + "'");
        return result;
    }

    /**
     *
     * @param WishListID
     * @param NewProductCount
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateWishListProductCount(int WishListID, int NewProductCount) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateIntData(Tables.WishlistTable.ProductCount, NewProductCount, Tables.WishlistTable.Table, "where " + Tables.WishlistTable.ID + " = " + WishListID);
        return result;
    }

    /**
     *
     * @param WishListID
     * @param NewAmount
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateWishListAmount(int WishListID, String NewAmount) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateStringData(Tables.WishlistTable.Table, Tables.WishlistTable.Amount, NewAmount, "where " + Tables.WishlistTable.ID + " = " + WishListID);
        return result;
    }

    /**
     *
     * @param WishListID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteOrEmtpyWishList(int WishListID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        ArrayList<Integer> WishListProductDetailIDs = GetWishListProductDetailsIDsByWishListID(WishListID);
        if (!WishListProductDetailIDs.isEmpty()) {
            for (int WishListProductDetailsID : WishListProductDetailIDs) {
                result = DBManager.DeleteObject(Tables.WishlistProductDetailsTable.Table, "where " + Tables.WishlistProductDetailsTable.ID + " = " + WishListProductDetailsID);
            }
        }
        if (result.equals("success")) {
            result = DBManager.DeleteObject(Tables.WishlistTable.Table, "where " + Tables.WishlistTable.ID + " = " + WishListID);
        } else {
            result = "Error - Empting or Deleting WishList Product(s) could not be completed. Please, try again";
        }
        return result;
    }

    /**
     *
     * @param CartID
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteCartProduct(int CartID, int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int CartProductDetailsID = DBManager.GetInt(Tables.CartProductDetailsTable.ID, Tables.CartProductDetailsTable.Table, "where " + Tables.CartProductDetailsTable.CartID + " = " + CartID + " And " + Tables.CartProductDetailsTable.ProductID + " = " + ProductID);
        String cartProductPrice = GetCartProductPriceByID(CartProductDetailsID);
        double CartProductPrice = Double.parseDouble(cartProductPrice);
        int CartProductQuantity = GetCartProductQuantityByID(CartProductDetailsID);

        DBManager.DeleteObject(Tables.CartProductDetailsTable.Table, "where " + Tables.CartProductDetailsTable.ID + " = " + CartProductDetailsID);

        String cartAmount = GetCartAmountByCartID(CartID);
        double CartAmount = Double.parseDouble(cartAmount);
        String cartTotalAmount = GetCartTotalAmountByCartID(CartID);
        double CartTotalAmount = Double.parseDouble(cartTotalAmount);
        int OldCartProductCount = GetCartProductCountByCartID(CartID);
        double NewCartAmount = CartAmount - CartProductPrice;
        double NewTotalCartAmount = CartTotalAmount - CartProductPrice;
        int NewCartProductCount = OldCartProductCount - CartProductQuantity;

        result = UpdateCartProductCount(CartID, NewCartProductCount);
        if (result.equals("success")) {
            result = UpdateCartAmount(CartID, "" + NewCartAmount);
            if (result.equals("success")) {
                result = UpdateCartTotalAmount(CartID, "" + NewTotalCartAmount);
            } else {
                result = "Cart Amount could not be updated";
            }
        } else {
            result = "Cart Product Count could not be updated";
        }
        if (NewCartProductCount == 0) {
            result = DBManager.DeleteObject(Tables.CartTable.Table, "where " + Tables.CartTable.ID + " = " + CartID);

        }

        return result;
    }

    /**
     *
     * @param WishlistID
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteWishListProduct(int WishlistID, int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int WishlistProductDetailsID = DBManager.GetInt(Tables.WishlistProductDetailsTable.ID, Tables.WishlistProductDetailsTable.Table, "where " + Tables.WishlistProductDetailsTable.WishlistID + " = " + WishlistID + " And " + Tables.CartProductDetailsTable.ProductID + " = " + ProductID);
        String wishListProductPrice = GetWishListProductPriceByID(WishlistProductDetailsID);
        double WishListProductPrice = Double.parseDouble(wishListProductPrice);
        int WishListProductQuantity = GetWishListProductQuantityByID(WishlistProductDetailsID);

        DBManager.DeleteObject(Tables.WishlistProductDetailsTable.Table, "where " + Tables.WishlistProductDetailsTable.ID + " = " + WishlistProductDetailsID);

        String wishListAmount = GetWishListAmountByWishListID(WishlistID);
        double WishListAmount = Double.parseDouble(wishListAmount);
        int OldWishListProductCount = GetWishListProductCountByWishListID(WishlistID);
        double NewWishListAmount = WishListAmount - WishListProductPrice;
        int NewWishListProductCount = OldWishListProductCount - WishListProductQuantity;

        result = UpdateWishListProductCount(WishlistID, NewWishListProductCount);
        if (result.equals("success")) {
            result = UpdateWishListAmount(WishlistID, "" + NewWishListAmount);
            if (!result.equals("success")) {
                result = "WishList Amount could not be updated";
            }
        } else {
            result = "WishList Product Count could not be updated";
        }
         if (NewWishListProductCount == 0) {
            result = DBManager.DeleteObject(Tables.WishlistTable.Table, "where " + Tables.WishlistTable.ID + " = " + WishlistID);

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
    public static String AddAllWishListProductsToCart(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int WishListID = GetWishListIDByUserID(UserID);
        String wishListAmount = GetWishListAmountByWishListID(WishListID);
        double WishListAmount = Double.parseDouble(wishListAmount);
        int WishListProductCount = GetWishListProductCountByWishListID(WishListID);
        ArrayList<Integer> WishListProductDetailIDs = GetWishListProductDetailsIDsByWishListID(WishListID);
        HashMap<String, String> WishListProductDetailsData = new HashMap<>();

        int CartID = GetCartIDByUserID(UserID);
        if (CartID != 0) {
            //update the amount in the cart table
            String cartAmount = GetCartAmountByUserID(UserID);
            double CartAmount = Double.parseDouble(cartAmount);
            String cartTotalAmount = GetCartTotalAmountByUserID(UserID);
            double CartTotalAmount = Double.parseDouble(cartTotalAmount);
            double NewCartAmount = CartAmount + WishListAmount;
            double NewCartTotalAmount = CartTotalAmount += WishListAmount;
            result = UpdateCartAmount(CartID, "" + NewCartAmount);

            if (result.equals("success")) {
                //update the product count in the cart
                result = UpdateCartTotalAmount(CartID, "" + NewCartTotalAmount);
                if (result.equals("success")) {
                    int CartProductCount = GetCartProductCountByUserID(UserID);
                    int NewProductCount = CartProductCount += WishListProductCount;
                    result = UpdateCartProductCount(CartID, NewProductCount);
                    if (result.equals("success")) {
                        if (!WishListProductDetailIDs.isEmpty()) {
                            for (int WishListProductDetailID : WishListProductDetailIDs) {
                                WishListProductDetailsData = GetWishListProductDetailsDataByID(WishListProductDetailID);
                                int ProductID = Integer.parseInt(WishListProductDetailsData.get(Tables.WishlistProductDetailsTable.ProductID));
                                int ProductPrice = Integer.parseInt(WishListProductDetailsData.get(Tables.WishlistProductDetailsTable.ProductPrice));
                                int ProductQuantity = Integer.parseInt(WishListProductDetailsData.get(Tables.WishlistProductDetailsTable.ProductQuantity));
                                result = ComputeCartProductDetails(CartID, ProductID, ProductPrice, ProductQuantity, "Increase");
                            }
                        }
                    } else {
                        result = "Cart total product count could not be updated";
                    }
                } else {
                    result = "Cart total amount could not be updated";
                }
            } else {
                result = "Cart  amount could not be updated";
            }
        } else {
            CartID = CreateCart(UserID, "" + WishListAmount, WishListProductCount);
            if (!WishListProductDetailIDs.isEmpty()) {
                for (int WishListProductDetailID : WishListProductDetailIDs) {
                    WishListProductDetailsData = GetWishListProductDetailsDataByID(WishListProductDetailID);
                    int ProductID = Integer.parseInt(WishListProductDetailsData.get(Tables.WishlistProductDetailsTable.ProductID));
                    String ProductPrice = WishListProductDetailsData.get(Tables.WishlistProductDetailsTable.ProductPrice);
                    int ProductQuantity = Integer.parseInt(WishListProductDetailsData.get(Tables.WishlistProductDetailsTable.ProductQuantity));
                    result = CreateCartProductDetails(CartID, ProductID, ProductPrice, ProductQuantity);
                }
            }
        }
        if (result.equals("success")) {
            result = DeleteOrEmtpyWishList(WishListID);
        }

        return result;
    }

    /**
     *
     * @param ID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetWishListProductDetailsDataByID(int ID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> data = DBManager.GetTableData(Tables.WishlistProductDetailsTable.Table, "where " + Tables.WishlistProductDetailsTable.ID + " = " + ID);
        return data;
    }

    /**
     *
     * @param WishListID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetWishListProductDetailsIDsByWishListID(int WishListID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.WishlistProductDetailsTable.ID, Tables.WishlistProductDetailsTable.Table, "where " + Tables.WishlistProductDetailsTable.WishlistID + " = " + WishListID);
        return IDs;
    }

    /**
     *
     * @param UserID
     * @param WishListID
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String AddWishListProductToCart(String UserID, int WishListID, int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int WishlistProductDetailsID = DBManager.GetInt(Tables.WishlistProductDetailsTable.ID, Tables.WishlistProductDetailsTable.Table, "where " + Tables.WishlistProductDetailsTable.WishlistID + " = " + WishListID + " And " + Tables.CartProductDetailsTable.ProductID + " = " + ProductID);
        if (WishlistProductDetailsID != 0) {
            String wishListProductPrice = GetWishListProductPriceByID(WishlistProductDetailsID);
            double WishListProductPrice = Double.parseDouble(wishListProductPrice);
            int WishListProductQuantity = GetWishListProductQuantityByID(WishlistProductDetailsID);

            String oldWishListAmount = GetWishListAmountByWishListID(WishListID);
            double OldWishListAmount = Double.parseDouble(oldWishListAmount);
            int OldWishListProductCount = GetWishListProductCountByWishListID(WishListID);
            double NewWishListAmount = OldWishListAmount - WishListProductPrice;
            int NewWishListProductCount = OldWishListProductCount - WishListProductQuantity;

            result = UpdateWishListProductCount(WishListID, NewWishListProductCount);
            if (result.equals("success")) {
                result = UpdateWishListAmount(WishListID, "" + NewWishListAmount);
                if (result.equals("success")) {
                    //Delete item from cart
                    result = DBManager.DeleteObject(Tables.WishlistProductDetailsTable.Table, "where " + Tables.WishlistProductDetailsTable.ID + " = " + WishlistProductDetailsID);
                    if (result.equals("success")) {
                        //Add the product to cart,
                        result = ComputeCart(UserID, ProductID, WishListProductPrice, WishListProductQuantity, "Increase");
                        if (!result.equals("success")) {
                            result = "Adding Product to Cart could not be completed. 2";
                        }
                    } else {
                        result = "WishList Product could not be deleted";
                    }
                } else {
                    result = "WishList Amount could not be updated";
                }
            } else {
                result = "WishList Product Count could not be updated";
            }
        } else {
            result = "Adding Product to Cart could not be completed. 1";
        }
        return result;
    }

    /**
     *
     * @param UserID
     * @param DiscountCode
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputeCartDiscountCode(String UserID, String DiscountCode) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        String cartAmount = GetCartTotalAmountByUserID(UserID);
        double CartAmount = Double.parseDouble(cartAmount);
        int CartID = GetCartIDByUserID(UserID);
        double CartDiscountedAmount = 0.0;
        int DiscountCodeID = EngineDiscountManager.GetDiscountCodeIDByCode(UserID, DiscountCode);
        if (DiscountCodeID != 0) {
            int ExistingDiscountCodeID = GetDiscountCodeIDByCartID(CartID);
            if (ExistingDiscountCodeID == 0 && ExistingDiscountCodeID != DiscountCodeID) {
                int DiscountDeductionTypeID = EngineDiscountManager.GetDiscountCodeDeductionTypeByDiscounCodeID(DiscountCodeID);
                int DiscountDeductionValue = EngineDiscountManager.GetDiscountCodeDeductionValueByDiscounCodeID(DiscountCodeID);
                if (DiscountDeductionTypeID == 1) {//percentage
                    double DiscountAmt = EngineDiscountManager.ComputePercentageAmount(DiscountDeductionValue, CartAmount);
                    CartDiscountedAmount = CartAmount - DiscountAmt;
                    result = DBManager.UpdateStringData(Tables.CartTable.Table, Tables.CartTable.DiscountAmount, "" + DiscountAmt, "where " + Tables.CartTable.ID + " = " + CartID);
                } else if (DiscountDeductionTypeID == 2) {//amount
                    CartDiscountedAmount = CartAmount - DiscountDeductionValue;
                    result = DBManager.UpdateIntData(Tables.CartTable.DiscountAmount, DiscountDeductionValue, Tables.CartTable.Table, "where " + Tables.CartTable.ID + " = " + CartID);
                }
                if (result.equals("success")) {
                    result = DBManager.UpdateStringData(Tables.CartTable.Table, Tables.CartTable.DiscountedAmount, "" + CartDiscountedAmount, "where " + Tables.CartTable.ID + " = " + CartID);
                    if (result.equals("success")) {
                        result = DBManager.UpdateIntData(Tables.CartTable.DiscountCodeID, DiscountCodeID, Tables.CartTable.Table, "where " + Tables.CartTable.ID + " = " + CartID);
                        if (result.equals("success")) {
                            result = DBManager.UpdateStringData(Tables.CartTable.Table, Tables.CartTable.TotalAmount, "" + CartDiscountedAmount, "where " + Tables.CartTable.ID + " = " + CartID);
                            if (result.equals("success")) {
                                result = EngineDiscountManager.UpdateDiscountCodeUsage(UserID, DiscountCodeID);
                            } else {

                                result = "Discount Code Total Amount could not be updated";
                            }
                        } else {
                            result = "Discount Code ID could not be updated";
                        }
                    } else {
                        result = "Discount Code Amount could not be updated";
                    }
                } else {
                    result = "Discount Code Discount Amount could not be updated";
                }
            } else {
                result = "Discount Code has already been added and cannot be used more than once on an order.";
            }
        } else {
            result = "Discount Code has expired or it has exceeded it's maximum usage. Please, use another discount code.";
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
    public static String GetDiscountAmountByCartID(int CartID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.CartTable.DiscountAmount, Tables.CartTable.Table, "where " + Tables.CartTable.ID + " = " + CartID);
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
    public static int GetDiscountCodeIDByCartID(int CartID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.CartTable.DiscountCodeID, Tables.CartTable.Table, "where " + Tables.CartTable.ID + " = " + CartID);
        return result;
    }

    /**
     *
     * @param ShippingTypeID
     * @param UserID
     * @param ShippingAdddresID
     * @param ShippingFees
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputeCartShipping(int ShippingTypeID, String UserID, int ShippingAdddresID, double ShippingFees) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int CartID = GetCartIDByUserID(UserID);
        String oldCartTotalAmount = GetCartTotalAmountByUserID(UserID);
        double OldCartTotalAmount = Double.parseDouble(oldCartTotalAmount);
        int shippingaddressid = GetCartShippingAddressIDByCartID(CartID);
        if (shippingaddressid == 0) {
            result = UpdateCartShippingAddressIDByCartID(CartID, ShippingAdddresID);
            if (result.equals("success")) {
                //add the  fees to the cart amount 
                double newCartTotalAmount = OldCartTotalAmount + ShippingFees;
                result = UpdateCartTotalAmount(CartID, "" + newCartTotalAmount);
                if (result.equals("success")) {
                    result = UpdateCartShippingTypeIDByCartID(CartID, ShippingTypeID);
                    if (result.equals("success")) {
                        result = UpdateCartShippingFeesByCartID(CartID, "" + ShippingFees);
                        if (!result.equals("success")) {
                            result = "Updating Cart Shipping fess could not be completed.";
                        }
                    } else {
                        result = "Updating Cart Shipping Type could not be completed.";
                    }
                } else {
                    result = "Updating Cart Total Amount  could not be completed.";
                }
            } else {
                result = "Cart Shipping could not be completed.";
            }
        } else {
            result = "Shipping Address has been updated";
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
    public static int GetCartShippingAddressIDByCartID(int CartID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.CartTable.ShippingAddressID, Tables.CartTable.Table, "where " + Tables.CartTable.ID + " = " + CartID);
        return result;
    }

    /**
     *
     * @param CartID
     * @param Fees
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateCartShippingFeesByCartID(int CartID, String Fees) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateStringData(Tables.CartTable.Table, Tables.CartTable.Fees, Fees, "Where " + Tables.CartTable.ID + " = " + CartID);
        return result;
    }

    /**
     *
     * @param CartID
     * @param Fees
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateCartStatusByCartID(int CartID, String Status) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateStringData(Tables.CartTable.Table, Tables.CartTable.Status, Status, "Where " + Tables.CartTable.ID + " = " + CartID);
        return result;
    }

    /**
     *
     * @param CartID
     * @param ShippingTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateCartShippingTypeIDByCartID(int CartID, int ShippingTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateIntData(Tables.CartTable.ShippingTypeID, ShippingTypeID, Tables.CartTable.Table, "Where " + Tables.CartTable.ID + " = " + CartID);
        return result;
    }

    /**
     *
     * @param CartID
     * @param ShippingID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateCartShippingAddressIDByCartID(int CartID, int ShippingAddressID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateIntData(Tables.CartTable.ShippingAddressID, ShippingAddressID, Tables.CartTable.Table, "Where " + Tables.CartTable.ID + " = " + CartID);
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
    public static HashMap<String, String> GetCartDataByUserID(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = new HashMap<>();
        Data = DBManager.GetTableData(Tables.CartTable.Table, "where " + Tables.CartTable.UserID + " = '" + UserID + "'");

        return Data;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetFullCartDataByUserID(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = new HashMap<>();
        Data = DBManager.GetTableData(Tables.CartTable.Table, "where " + Tables.CartTable.UserID + " = '" + UserID + "'");
        if (!Data.isEmpty()) {
            int CartID = Integer.parseInt(Data.get(Tables.CartTable.ID));
            HashMap<Integer, HashMap<String, String>> CartProdDetList = EngineCartManager.GetCartProductDetailsList(CartID);
            JSONObject CartProductDet = new JSONObject();
            CartProductDet.put("CartProductDetails", CartProdDetList);
            if (!CartProductDet.isEmpty()) {
                Data.putAll(CartProductDet);
            }

            String dt = Data.get(Tables.CartTable.Date);
            String date = DateManager.readDate(dt);
            Data.put(Tables.CartTable.Date, date);

        }
        return Data;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetFullCartDataByCartID(int CartID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = new HashMap<>();
        Data = DBManager.GetTableData(Tables.CartTable.Table, "where " + Tables.CartTable.ID + " = " + CartID);
        if (!Data.isEmpty()) {
            String userName = "";
            String userid = Data.get(Tables.CartTable.UserID);
            if (EngineAddressManager.isNumeric(userid)) {
                int UserID = Integer.parseInt(userid);
                userName = EngineUserManager.GetUserName(UserID);
            } else {
                userName = "Guest " + userid;
            }
            Data.put("cartUsername", userName);
            String dt = Data.get(Tables.CartTable.Date);
            String date = DateManager.readDate(dt);
            Data.put(Tables.CartTable.Date, date);

            String shippingTypeName = "";
            String shiptypeid = Data.get(Tables.CartTable.ShippingTypeID);
            int ShippingTypeID = Integer.parseInt(shiptypeid);
            if (ShippingTypeID != 0) {
                shippingTypeName = EngineShippingManager.GetShippingTypeNameByID(ShippingTypeID);
            } else {
                shippingTypeName = "Not yet added";
            }
            Data.put("shippingTypeName", shippingTypeName);

        }
        return Data;
    }

    /**
     *
     * @param CartID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<Integer, HashMap<String, String>> GetCartProductDetailsList(int CartID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
        HashMap<String, String> ProdDetailList = new HashMap<>();
        ArrayList<Integer> ProdDetIDS = GetCartProductDetailsIDsByCartID(CartID);
        if (!ProdDetIDS.isEmpty()) {
            for (int ProdDetID : ProdDetIDS) {
                ProdDetailList = GetCartProductDetailsDataByID(ProdDetID);
                if (!ProdDetailList.isEmpty()) {
                    String productID = ProdDetailList.get(Tables.CartProductDetailsTable.ProductID);
                    int ProductID = Integer.parseInt(productID);
                    HashMap<String, String> ProductDetails = EngineProductManager.GetProductData(ProductID);
                    JSONObject ProductDet = new JSONObject();
                    ProductDet.put("ProductDetails", ProductDetails);
                    if (!ProductDet.isEmpty()) {
                        ProdDetailList.putAll(ProductDet);
                    }
                    List.put(ProdDetID, ProdDetailList);
                }
            }
        }
        return List;
    }

    public static String UpdateCartUserID(String OldUserID, String NewUserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int CartID = GetCartIDByUserID(OldUserID);
        String result = "";
        if (CartID != 0) {
            result = DBManager.UpdateStringData(Tables.CartTable.Table, Tables.CartTable.UserID, NewUserID, "where " + Tables.CartTable.ID + " = " + CartID);
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
    public static HashMap<String, String> GetFullWishListDataByUseID(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = new HashMap<>();
        Data = DBManager.GetTableData(Tables.WishlistTable.Table, "where " + Tables.WishlistTable.UserID + " = '" + UserID + "'");
        if (!Data.isEmpty()) {
            int WishListID = Integer.parseInt(Data.get(Tables.WishlistTable.ID));
            HashMap<Integer, HashMap<String, String>> WishListProdDetList = EngineCartManager.GetWishListProductDetailsList(WishListID);
            JSONObject CartProductDet = new JSONObject();
            CartProductDet.put("WishListProductDetails", WishListProdDetList);
            if (!CartProductDet.isEmpty()) {
                Data.putAll(CartProductDet);
            }

            String dt = Data.get(Tables.CartTable.Date);
            String date = DateManager.readDate(dt);
            Data.put(Tables.CartTable.Date, date);

        }
        return Data;
    }

    /**
     *
     * @param WishListID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<Integer, HashMap<String, String>> GetWishListProductDetailsList(int WishListID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<Integer, HashMap<String, String>> List = new HashMap<>();
        HashMap<String, String> ProdDetailList = new HashMap<>();
        ArrayList<Integer> ProdDetIDS = GetWishListProductDetailsIDsByWishListID(WishListID);
        if (!ProdDetIDS.isEmpty()) {
            for (int ProdDetID : ProdDetIDS) {
                ProdDetailList = GetWishListProductDetailsDataByID(ProdDetID);
                if (!ProdDetailList.isEmpty()) {
                    String productID = ProdDetailList.get(Tables.WishlistProductDetailsTable.ProductID);
                    int ProductID = Integer.parseInt(productID);
                    HashMap<String, String> ProductDetails = EngineProductManager.GetProductData(ProductID);
                    JSONObject ProductDet = new JSONObject();
                    ProductDet.put("ProductDetails", ProductDetails);
                    if (!ProductDet.isEmpty()) {
                        ProdDetailList.putAll(ProductDet);
                    }
                    List.put(ProdDetID, ProdDetailList);
                }
            }
        }
        return List;
    }

}
