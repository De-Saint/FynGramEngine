/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import fgengine.Tables.Tables;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mac
 */
public class EngineSubscriptionManager {

    /**
     *
     * @param SubscriptionAmountID
     * @param NewAmount
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws ParseException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateSellerSubscriptionAmount(int SubscriptionAmountID, double NewAmount) throws ClassNotFoundException, SQLException, ParseException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.UpdateDoubleData(Tables.SellerSubscriptionAmountTable.Table, Tables.SellerSubscriptionAmountTable.Amount, NewAmount, "where " + Tables.SellerSubscriptionAmountTable.ID + " = " + SubscriptionAmountID);
        return result;
    }

    /**
     *
     * @param SellerUserID
     * @param Amount
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String CreateSubscription(int SellerUserID, double Amount) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {

        int SellerTypeID = GetSellerTypeIDBySellerUserID(SellerUserID);
        int SubscriptionTypeID = GetSellerSubscriptionTypeIDBySellerUserID(SellerUserID);
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.SellerSubscriptionTable.SellerUserID, SellerUserID);
        tableData.put(Tables.SellerSubscriptionTable.SellerTypeID, SellerTypeID);
        tableData.put(Tables.SellerSubscriptionTable.SubscriptionTypeID, SubscriptionTypeID);
        tableData.put(Tables.SellerSubscriptionTable.Amount, Amount);
        int subid = DBManager.insertTableDataReturnID(Tables.SellerSubscriptionTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.SellerSubscriptionTable.Table, Tables.SellerSubscriptionTable.StartDate, "where " + Tables.SellerSubscriptionTable.ID + " = " + subid);
        DBManager.UpdateCurrentTime(Tables.SellerSubscriptionTable.Table, Tables.SellerSubscriptionTable.StartTime, "where " + Tables.SellerSubscriptionTable.ID + " = " + subid);
        String result = EngineUserManager.UpdateSellerStatus(SellerUserID, "Paid");
        return result;
    }

    /**
     *
     * @param SubscriptionID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String GetSubscriptionStartDate(int SubscriptionID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = DBManager.GetString(Tables.SellerSubscriptionTable.StartDate, Tables.SellerSubscriptionTable.Table, "where " + Tables.SellerSubscriptionTable.ID + " = " + SubscriptionID);
        return result;
    }

    /**
     *
     * @param SubscriptionTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static int GetSubscriptionDuration(int SubscriptionTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        int result = DBManager.GetInt(Tables.SellerSubscriptionTypesTable.DurationInMonths, Tables.SellerSubscriptionTypesTable.Table, "where " + Tables.SellerSubscriptionTypesTable.ID + " = " + SubscriptionTypeID);
        return result;
    }

    /**
     *
     * @param SellerUserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ActivateSellerSubscription(int SellerUserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        int SubscriptionTypeID = GetSellerSubscriptionTypeIDBySellerUserID(SellerUserID);
        int SellerTypeID = GetSellerTypeIDBySellerUserID(SellerUserID);
        int SubscriptionID = GetSellerSubscriptionIDBySellerUserIDAndSellerTypeIDAndSubscriptionTypeID(SellerUserID, SellerTypeID, SubscriptionTypeID);

//        LocalDate date = LocalDate.now();
        String startDate = GetSubscriptionStartDate(SubscriptionID);
        LocalDate StartDateDate = LocalDate.parse(startDate);

        int Duration = GetSubscriptionDuration(SubscriptionTypeID);
        LocalDate EndDate = StartDateDate.plusMonths(Duration);

        DBManager.UpdateStringData(Tables.SellerSubscriptionTable.Table, Tables.SellerSubscriptionTable.EndDate, "" + EndDate, "where " + Tables.SellerSubscriptionTable.ID + " = " + SubscriptionID);
        EngineUserManager.UpdateSellerStatus(SellerUserID, "Activated");
        EngineUserManager.UpdateSellerActive(SellerUserID, 1);
        int AdminUserID = EngineUserManager.GetAdminUserID();
        double SubsAmount = GetSellerSubscriptionAmountBySellerTypeIDAndSubscriptionTypeID(SellerTypeID, SubscriptionTypeID);
        result = EngineWalletManager.ComputeWalletRecord(SellerUserID, AdminUserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), SubsAmount, "Activate Supplier Account", "");
        String msgbdy = "Congratulations!!! \nYour account has been successfully activated. \nThank you for being part of FynGram Onlne Store";
        EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), msgbdy, "Seller Account Activated", SellerUserID);
        try {
            String Email = EngineUserManager.GetUserEmail(SellerUserID);
            EngineEmailManager.SendEmail(Email, msgbdy, "Seller Account Activated");
        } catch (Exception ex) {
        }
        return result;
    }

    /**
     *
     * @param SellerUserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetSellerTypeIDBySellerUserID(int SellerUserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.SellersTable.SellerTypeID, Tables.SellersTable.Table, "where " + Tables.SellersTable.UserID + " = " + SellerUserID);
        return result;
    }

    /**
     *
     * @param SellerUserID
     * @param SellerTypeID
     * @param SubscriptionTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetSellerSubscriptionIDBySellerUserIDAndSellerTypeIDAndSubscriptionTypeID(int SellerUserID, int SellerTypeID, int SubscriptionTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.SellerSubscriptionTable.ID, Tables.SellerSubscriptionTable.Table, "where " + Tables.SellerSubscriptionTable.SellerUserID + " = " + SellerUserID + " And " + Tables.SellerSubscriptionTable.SellerTypeID + " = " + SellerTypeID + " And " + Tables.SellerSubscriptionTable.SubscriptionTypeID + " = " + SubscriptionTypeID);
        return result;
    }

    /**
     *
     * @param SellerTypeID
     * @param SubscriptionTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static double GetSellerSubscriptionAmountBySellerTypeIDAndSubscriptionTypeID(int SellerTypeID, int SubscriptionTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double result = DBManager.GetDouble(Tables.SellerSubscriptionAmountTable.Amount, Tables.SellerSubscriptionAmountTable.Table, "where " + Tables.SellerSubscriptionAmountTable.SellerTypeID + " = " + SellerTypeID + " And " + Tables.SellerSubscriptionAmountTable.SellerSubscriptionTypeID + " = " + SubscriptionTypeID);
        return result;
    }

    /**
     *
     * @param SellerUserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetSellerSubscriptionTypeIDBySellerUserID(int SellerUserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.SellersTable.SubscriptionTypeID, Tables.SellersTable.Table, "where " + Tables.SellersTable.UserID + " = " + SellerUserID);
        return result;
    }

    /**
     *
     * @param SubscriptionTyperID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetSellerSubscriptionTypeNameBySubscriptionTypeID(int SubscriptionTyperID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.SellerSubscriptionTypesTable.Name, Tables.SellerSubscriptionTypesTable.Table, "where " + Tables.SellerSubscriptionTypesTable.ID + " = " + SubscriptionTyperID);
        return result;
    }

    /**
     *
     * @param SellerTypeID
     * @param SubscriptionTypeID
     * @param Amount
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String CreateSubscriptionAmount(int SellerTypeID, int SubscriptionTypeID, double Amount) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.SellerSubscriptionAmountTable.SellerTypeID, SellerTypeID);
        tableData.put(Tables.SellerSubscriptionAmountTable.SellerSubscriptionTypeID, SubscriptionTypeID);
        tableData.put(Tables.SellerSubscriptionAmountTable.Amount, Amount);
        String result = DBManager.insertTableData(Tables.SellerSubscriptionAmountTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param Name
     * @param Description
     * @param Duration
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String CreateSubscriptionType(String Name, String Description, int Duration) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.SellerSubscriptionTypesTable.Name, Name);
        tableData.put(Tables.SellerSubscriptionTypesTable.Description, Description);
        tableData.put(Tables.SellerSubscriptionTypesTable.DurationInMonths, Duration);
        String result = DBManager.insertTableData(Tables.SellerSubscriptionTypesTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param SellerTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetSellerTypeNameBySellerTypeID(int SellerTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.SellerTypesTable.Name, Tables.SellerTypesTable.Table, "where " + Tables.SellerTypesTable.ID + " = " + SellerTypeID);
        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetSubscriptionTypeIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.SellerSubscriptionTypesTable.ID, Tables.SellerSubscriptionTypesTable.Table, "");
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param SubscriptionTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetSubscriptionTypeData(int SubscriptionTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.SellerSubscriptionTypesTable.Table, "where " + Tables.SellerSubscriptionTypesTable.ID + " = " + SubscriptionTypeID);
        if (!Details.isEmpty()) {
            ArrayList<Integer> SubTypeCount = GetSubscriptionBySubscriptionTypeID(SubscriptionTypeID);
            Details.put("SubcriptionTypeCount", "" + SubTypeCount.size());
        }
        return Details;
    }

    /**
     *
     * @param SubTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetSubscriptionBySubscriptionTypeID(int SubTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.SellerSubscriptionTable.ID, Tables.SellerSubscriptionTable.Table, "where " + Tables.SellerSubscriptionTable.SubscriptionTypeID + " = " + SubTypeID);
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetSubscriptionAmountIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.SellerSubscriptionAmountTable.ID, Tables.SellerSubscriptionAmountTable.Table, "");
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param SubAmountID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetSubscriptionAmountData(int SubAmountID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.SellerSubscriptionAmountTable.Table, "where " + Tables.SellerSubscriptionAmountTable.ID + " = " + SubAmountID);
        if (!Details.isEmpty()) {
            String subtypeid = Details.get(Tables.SellerSubscriptionAmountTable.SellerSubscriptionTypeID);
            int SubTypeID = Integer.parseInt(subtypeid);
            String SubTypeName = GetSellerSubscriptionTypeNameBySubscriptionTypeID(SubTypeID);
            Details.put("SubscriptionTypeName", SubTypeName);

            String subsellertypeid = Details.get(Tables.SellerSubscriptionAmountTable.SellerTypeID);
            int SellerTypeID = Integer.parseInt(subsellertypeid);
            String SellerTypeName = GetSellerTypeNameBySellerTypeID(SellerTypeID);
            Details.put("SellerTypeName", SellerTypeName);
        }
        return Details;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetAllSubscriptionIDs(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        if (UserID == 1) {
            IDs = DBManager.GetIntArrayList(Tables.SellerSubscriptionTable.ID, Tables.SellerSubscriptionTable.Table, "");
        } else {
            IDs = DBManager.GetIntArrayList(Tables.SellerSubscriptionTable.ID, Tables.SellerSubscriptionTable.Table, "where " + Tables.SellerSubscriptionTable.SellerUserID + " = " + UserID);
        }
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
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
    public static int GetUserSubscriptionID(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.SellerSubscriptionTable.ID, Tables.SellerSubscriptionTable.Table, "where " + Tables.SellerSubscriptionTable.SellerUserID + " = " + UserID);
        return result;
    }

    /**
     *
     * @param SubID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetSubscriptionData(int SubID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.SellerSubscriptionTable.Table, "where " + Tables.SellerSubscriptionTable.ID + " = " + SubID);
        if (!Details.isEmpty()) {
            String subtypeid = Details.get(Tables.SellerSubscriptionTable.SubscriptionTypeID);
            int SubsTypeID = Integer.parseInt(subtypeid);
            String SubTypeName = GetSellerSubscriptionTypeNameBySubscriptionTypeID(SubsTypeID);
            Details.put("SubscriptionTypeName", SubTypeName);

            String subsellertypeid = Details.get(Tables.SellerSubscriptionTable.SellerTypeID);
            int SellerTypeID = Integer.parseInt(subsellertypeid);
            String SellerTypeName = GetSellerTypeNameBySellerTypeID(SellerTypeID);
            Details.put("SellerTypeName", SellerTypeName);

            String selleruserid = Details.get(Tables.SellerSubscriptionTable.SellerUserID);
            int SellerUserID = Integer.parseInt(selleruserid);
            String SellerUserName = EngineUserManager.GetUserName(SellerUserID);
            Details.put("SellerUserName", SellerUserName);

            String sdate = Details.get(Tables.SellerSubscriptionTable.StartDate);
            String Sdate = DateManager.readDate(sdate);
            Details.put(Tables.SellerSubscriptionTable.StartDate, Sdate);

            String edate = Details.get(Tables.SellerSubscriptionTable.EndDate);
            String Edate = DateManager.readDate(edate);
            Details.put(Tables.SellerSubscriptionTable.EndDate, Edate);

            String stime = Details.get(Tables.SellerSubscriptionTable.StartTime);
            String STime = DateManager.readTime(stime);
            Details.put(Tables.SellerSubscriptionTable.StartTime, STime);
        }
        return Details;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetSellerTypeIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.SellerTypesTable.ID, Tables.SellerTypesTable.Table, "ORDER BY " + Tables.SellerTypesTable.Name + " DESC");
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param SellerTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetSellerTypeData(int SellerTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.SellerTypesTable.Table, "where " + Tables.SellerTypesTable.ID + " = " + SellerTypeID);
        if (!Details.isEmpty()) {
            ArrayList<Integer> sellercount = GetSellerUserIDsBySellerTypeID(SellerTypeID);
            Details.put("SellerTypeSellerCount", "" + sellercount.size());
        }

        return Details;
    }

    /**
     *
     * @param SubAmountID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteSubscriptionAmount(int SubAmountID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        HashMap<String, String> SubAmountData = GetSubscriptionAmountData(SubAmountID);
        String subtypeid = SubAmountData.get(Tables.SellerSubscriptionAmountTable.SellerSubscriptionTypeID);
        int SubTypeID = Integer.parseInt(subtypeid);
        String sellertypeid = SubAmountData.get(Tables.SellerSubscriptionAmountTable.SellerTypeID);
        int SellerTypeID = Integer.parseInt(sellertypeid);
        ArrayList<Integer> SubscriptionIDs = GetSubscriptionByActiveSubscriptionTypeID(SubTypeID);
        if (SubscriptionIDs.isEmpty()) {
            ArrayList<Integer> SellerUserIDs = GetSellerUserIDsBySellerTypeID(SellerTypeID);
            if (SellerUserIDs.isEmpty()) {
                result = DBManager.DeleteObject(Tables.SellerSubscriptionAmountTable.Table, "where " + Tables.SellerSubscriptionAmountTable.ID + " = " + SubAmountID);
            } else {
                result = "There are active subscriptions and can't be deleted";
            }
        } else {
            result = "There are active subscriptions and can't be deleted";
        }
        return result;
    }

    /**
     *
     * @param SubTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetSubscriptionByActiveSubscriptionTypeID(int SubTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.SellerSubscriptionTable.ID, Tables.SellerSubscriptionTable.Table, "where " + Tables.SellerSubscriptionTable.SubscriptionTypeID + " = " + SubTypeID + " And " + Tables.SellerSubscriptionTable.Status + " = 'Active'");
        return IDs;
    }

    /**
     *
     * @param SellerTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetSellerUserIDsBySellerTypeID(int SellerTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.SellersTable.UserID, Tables.SellersTable.Table, "where " + Tables.SellersTable.SellerTypeID + " = " + SellerTypeID);
        return IDs;
    }

    /**
     *
     * @param SubTypeID
     * @param Option
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String EnableOrDisableSubscriptionType(int SubTypeID, String Option) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        ArrayList<Integer> SubscriptionIDs = GetSubscriptionByActiveSubscriptionTypeID(SubTypeID);
        if (SubscriptionIDs.isEmpty()) {
            if (Option.equals("Enabled")) {
                result = DBManager.UpdateIntData(Tables.SellerSubscriptionTypesTable.Active, 1, Tables.SellerSubscriptionTypesTable.Table, "where " + Tables.SellerSubscriptionTypesTable.ID + " = " + SubTypeID);
            } else if (Option.equals("Disabled")) {
                result = DBManager.UpdateIntData(Tables.SellerSubscriptionTypesTable.Active, 0, Tables.SellerSubscriptionTypesTable.Table, "where " + Tables.SellerSubscriptionTypesTable.ID + " = " + SubTypeID);
            } else if (Option.equals("Deleted")) {
                result = DBManager.DeleteObject(Tables.SellerSubscriptionTypesTable.Table, "where " + Tables.SellerSubscriptionTypesTable.ID + " = " + SubTypeID);
            }
        } else {
            result = "There are connected active subscriptions and can't be deleted";
        }
        return result;
    }

}
