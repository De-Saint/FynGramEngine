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
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author mac
 */
public class EngineShippingManager {

    /**
     *
     * @param Name
     * @param DeliveryInterval
     * @param AdminPercentage
     * @param ShipMethodPercentage
     * @param Phone
     * @param Email
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateShipping(String Name, String DeliveryInterval, int AdminPercentage, int ShipMethodPercentage, String Phone, String Email) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ShippingTable.Name, Name);
        tableData.put(Tables.ShippingTable.Phone, Phone);
        tableData.put(Tables.ShippingTable.Email, Email);
        tableData.put(Tables.ShippingTable.DeliveryInterval, DeliveryInterval);
        tableData.put(Tables.ShippingTable.AdminShippingPercentage, AdminPercentage);
        tableData.put(Tables.ShippingTable.ShippingMethodPercentage, ShipMethodPercentage);
        int id = DBManager.insertTableDataReturnID(Tables.ShippingTable.Table, tableData, "");
        String result = DBManager.UpdateCurrentDate(Tables.ShippingTable.Table, Tables.ShippingTable.DateAdded, "where " + Tables.ShippingTable.ID + " = " + id);
        return result;
    }

    /**
     *
     * @param ShippingID
     * @param Name
     * @param DeliveryInterval
     * @param AdminPercentage
     * @param ShipMethodPercentage
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String EditShipping(int ShippingID, String Name, String DeliveryInterval, int AdminPercentage, int ShipMethodPercentage, String Phone, String Email) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        if (!Name.equals("")) {
            result = DBManager.UpdateStringData(Tables.ShippingTable.Table, Tables.ShippingTable.Name, Name, "where " + Tables.ShippingTable.ID + " = " + ShippingID);
        }
        if (!Phone.equals("")) {
            result = DBManager.UpdateStringData(Tables.ShippingTable.Table, Tables.ShippingTable.Phone, Phone, "where " + Tables.ShippingTable.ID + " = " + ShippingID);
        }
        if (!Email.equals("")) {
            result = DBManager.UpdateStringData(Tables.ShippingTable.Table, Tables.ShippingTable.Email, Email, "where " + Tables.ShippingTable.ID + " = " + ShippingID);
        }
        if (!DeliveryInterval.equals("")) {
            result = DBManager.UpdateStringData(Tables.ShippingTable.Table, Tables.ShippingTable.DeliveryInterval, DeliveryInterval, "where " + Tables.ShippingTable.ID + " = " + ShippingID);
        }

        if (AdminPercentage != 0) {
            result = DBManager.UpdateIntData(Tables.ShippingTable.AdminShippingPercentage, AdminPercentage, Tables.ShippingTable.Table, "where " + Tables.ShippingTable.ID + " = " + ShippingID);
        }
        if (ShipMethodPercentage != 0) {
            result = DBManager.UpdateIntData(Tables.ShippingTable.ShippingMethodPercentage, ShipMethodPercentage, Tables.ShippingTable.Table, "where " + Tables.ShippingTable.ID + " = " + ShippingID);
        }
        return result;
    }

    /**
     *
     * @param CartAmount
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetShippingFees(double CartAmount) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        int shippingfeesid = 0;
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.ShippingFeesTable.ID, Tables.ShippingFeesTable.Table, "");
        if (!IDs.isEmpty()) {
            for (int id : IDs) {
                String minCartAmount = DBManager.GetString(Tables.ShippingFeesTable.MinCartAmount, Tables.ShippingFeesTable.Table, "where " + Tables.ShippingFeesTable.ID + " = " + id);
                double MinCartAmount = Double.parseDouble(minCartAmount);
                String maxCartAmount = DBManager.GetString(Tables.ShippingFeesTable.MaxCartAmount, Tables.ShippingFeesTable.Table, "where " + Tables.ShippingFeesTable.ID + " = " + id);
                double MaxCartAmount = Double.parseDouble(maxCartAmount);

                if (CartAmount > MinCartAmount && (CartAmount < MaxCartAmount && MaxCartAmount != 0)) {
                    result = DBManager.GetString(Tables.ShippingFeesTable.DeliveryFees, Tables.ShippingFeesTable.Table, "where " + Tables.ShippingFeesTable.ID + " = " + id);
                } else if (CartAmount > MinCartAmount && MaxCartAmount == 0) {
                    result = DBManager.GetString(Tables.ShippingFeesTable.DeliveryFees, Tables.ShippingFeesTable.Table, "where " + Tables.ShippingFeesTable.ID + " = " + id);
                }
                shippingfeesid = id;
            }
        }
        return result + "#" + shippingfeesid;
    }

    /**
     *
     * @param ShippingID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetAdminShippingPercentage(int ShippingID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.ShippingTable.AdminShippingPercentage, Tables.ShippingTable.Table, "where " + Tables.ShippingTable.ID + " = " + ShippingID);
        return result;
    }

    /**
     *
     * @param ShippingID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetShippingMethodPercentage(int ShippingID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.ShippingTable.ShippingMethodPercentage, Tables.ShippingTable.Table, "where " + Tables.ShippingTable.ID + " = " + ShippingID);
        return result;
    }

    /**
     *
     * @param ShippingTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetShippingTypeNameByID(int ShippingTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.ShippingTypeTable.Name, Tables.ShippingTypeTable.Table, "where " + Tables.ShippingTypeTable.ID + " = " + ShippingTypeID);
        return result;
    }

    /**
     *
     * @param ShippingMethodID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetShippingMethodName(int ShippingMethodID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.ShippingTable.Name, Tables.ShippingTable.Table, "where " + Tables.ShippingTable.ID + " = " + ShippingMethodID);
        return result;
    }

    /**
     *
     * @param ShippingMethodID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetShippingMethodEmail(int ShippingMethodID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.ShippingTable.Email, Tables.ShippingTable.Table, "where " + Tables.ShippingTable.ID + " = " + ShippingMethodID);
        return result;
    }

    /**
     *
     * @param ShippingMethodID
     * @param ShippingFeesAmount
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateShippingMethodEarnings(int ShippingMethodID, double ShippingFeesAmount) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        double ExistingEarning = 0.0;
        result = DBManager.GetString(Tables.ShippingTable.TotalEarnings, Tables.ShippingTable.Table, "where " + Tables.ShippingTable.ID + " = " + ShippingMethodID);
        ExistingEarning = Double.parseDouble(result);
        ExistingEarning = ExistingEarning + ShippingFeesAmount;

        result = DBManager.UpdateStringData(Tables.ShippingTable.Table, Tables.ShippingTable.TotalEarnings, "" + ExistingEarning, "where " + Tables.ShippingTable.ID + " = " + ShippingMethodID);
        return result;
    }

    /**
     *
     * @param ShippingMethodID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateShippingMethodNumberOfDelivery(int ShippingMethodID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int ExistingDelivery = DBManager.GetInt(Tables.ShippingTable.NumberOfDelivery, Tables.ShippingTable.Table, "where " + Tables.ShippingTable.ID + " = " + ShippingMethodID);
        ExistingDelivery = ExistingDelivery + 1;
        result = DBManager.UpdateIntData(Tables.ShippingTable.NumberOfDelivery, ExistingDelivery, Tables.ShippingTable.Table, "where " + Tables.ShippingTable.ID + " = " + ShippingMethodID);
        return result;
    }

    public static ArrayList<Integer> GetShippingIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.ShippingTable.ID, Tables.ShippingTable.Table, "");
        return IDs;
    }

    public static HashMap<String, String> GetShippingData(int ShippingID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = new HashMap<>();
        Data = DBManager.GetTableData(Tables.ShippingTable.Table, "where " + Tables.ShippingTable.ID + " = " + ShippingID);
        if (!Data.isEmpty()) {
            String dt = Data.get(Tables.ShippingTable.DateAdded);
            String date = DateManager.readDate(dt);
            Data.put(Tables.ShippingTable.DateAdded, date);
        }
        return Data;
    }

    /**
     *
     * @param ShippingID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteShipping(int ShippingID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.ShippingTable.Table, "where " + Tables.ShippingTable.ID + " = " + ShippingID);
        return result;
    }

    /**
     *
     * @param ShippingFeesID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteShippingFees(int ShippingFeesID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.ShippingFeesTable.Table, "where " + Tables.ShippingFeesTable.ID + " = " + ShippingFeesID);
        return result;
    }

    public static ArrayList<Integer> GetShippingFeesIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ShippingFeesTable.ID, Tables.ShippingFeesTable.Table, "Order by " + Tables.ShippingFeesTable.MinCartAmount);
        return IDs;
    }

    /**
     *
     * @param ShippingFeesID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetShippingFeesData(int ShippingFeesID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = new HashMap<>();
        Data = DBManager.GetTableData(Tables.ShippingFeesTable.Table, "where " + Tables.ShippingFeesTable.ID + " = " + ShippingFeesID);
        return Data;
    }

    /**
     *
     * @param ShippingAmt
     * @param ShippingMaxCartAmt
     * @param Action
     * @param ShippingMinCartAmt
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateShippingFees(double ShippingAmt, double ShippingMaxCartAmt, double ShippingMinCartAmt, int Action) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        double MaxAmt = 0.0;
        if (Action == 1) {//enter the amount
            MaxAmt = ShippingMaxCartAmt;
        } else if (Action == 0) { //choose greater than
            if (CheckMaxAmount()) {
                result = "Error:A greater than amount exit. Please, edit the maximum amount of the existing or specify a maximum amount.";
                return result;
            }
        }
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ShippingFeesTable.DeliveryFees, ShippingAmt);
        tableData.put(Tables.ShippingFeesTable.MinCartAmount, ShippingMinCartAmt);
        tableData.put(Tables.ShippingFeesTable.MaxCartAmount, MaxAmt);
        result = DBManager.insertTableData(Tables.ShippingFeesTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param ShippingFeesID
     * @param ShippingAmt
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String EditShippingFees(int ShippingFeesID, double ShippingAmt) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        if (ShippingAmt != 0) {
            result = DBManager.UpdateStringData(Tables.ShippingFeesTable.Table, Tables.ShippingFeesTable.DeliveryFees, "" + ShippingAmt, "where " + Tables.ShippingFeesTable.ID + " = " + ShippingFeesID);
        }
        return result;
    }

    public static boolean CheckMaxAmount() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        boolean result = true;
        ArrayList<String> AllMaxCartAmount = DBManager.GetStringArrayList(Tables.ShippingFeesTable.MaxCartAmount, Tables.ShippingFeesTable.Table, "");
        String existingMin = Collections.min(AllMaxCartAmount);
        double ExsitingMix = Double.parseDouble(existingMin);
        if (ExsitingMix > 0) {
            return result = false;
        } else {
            return result = true;
        }
    }
}
