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

/**
 *
 * @author mac
 */
public class EngineCashoutManager {

    /**
     *
     * @param UserID
     * @param BankID
     * @param AccountNumber
     * @param AccountType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateBankDetails(int UserID, int BankID, String AccountNumber, String AccountType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.BankDetailsTable.UserID, UserID);
        tableData.put(Tables.BankDetailsTable.BankID, BankID);
        tableData.put(Tables.BankDetailsTable.AccountNumber, AccountNumber);
        tableData.put(Tables.BankDetailsTable.AccountType, AccountType);
        String result = DBManager.insertTableData(Tables.BankDetailsTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param BankDetailID
     * @param BankID
     * @param AccountNumber
     * @param AccountType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String EditBankDetails(int BankDetailID, int BankID, String AccountNumber, String AccountType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        if (BankID != 0) {
            result = DBManager.UpdateIntData(Tables.BankDetailsTable.BankID, BankID, Tables.BankDetailsTable.Table, "where " + Tables.BankDetailsTable.ID + " = " + BankDetailID);
        }
        if (!AccountNumber.equals("")) {
            result = DBManager.UpdateStringData(Tables.BankDetailsTable.Table, Tables.BankDetailsTable.AccountNumber, AccountNumber, "where " + Tables.BankDetailsTable.ID + " = " + BankDetailID);
        }
        if (!AccountType.equals("")) {
            result = DBManager.UpdateStringData(Tables.BankDetailsTable.Table, Tables.BankDetailsTable.AccountType, AccountType, "where " + Tables.BankDetailsTable.ID + " = " + BankDetailID);
        }

        return result;
    }

    /**
     *
     * @param BankName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateBank(String BankName) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.BanksTable.Name, BankName);
        String result = DBManager.insertTableData(Tables.BanksTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param UserID
     * @param Amount
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputeCashOut(int UserID, int Amount) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        int UserAmount = EngineWalletManager.GetUserBalance(UserID, EngineWalletManager.GetMainWalletID());
        int BankDetialID = GetBankDetailsIDByUserID(UserID);
        if (UserAmount >= Amount) {
            if (BankDetialID != 0) {
                result = EngineWalletManager.ComputeWalletRecord(UserID, UserID, EngineWalletManager.GetMainWalletID(), EngineWalletManager.GetPendingWalletID(), Amount, "Move Fund", "For cashout/payout request.");
                if (result.equals("success")) {
                    result = CreateCashOut(UserID, Amount, BankDetialID);
                }
            } else {
                result = "Please, Add Your Bank Account Details.";
            }
        } else {
            result = "Insufficent Fund";
        }
        return result;
    }

    /**
     *
     * @param UserID
     * @param Amount
     * @param BankDetails
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateCashOut(int UserID, int Amount, int BankDetails) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.CashoutTable.UserID, UserID);
        tableData.put(Tables.CashoutTable.Amount, Amount);
        tableData.put(Tables.CashoutTable.BankDetailsID, BankDetails);
        int cid = DBManager.insertTableDataReturnID(Tables.CashoutTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.CashoutTable.Table, Tables.CashoutTable.RequestDate, "where " + Tables.CashoutTable.ID + " = " + cid);
        String result = DBManager.UpdateCurrentTime(Tables.CashoutTable.Table, Tables.CashoutTable.RequestTime, "where " + Tables.CashoutTable.ID + " = " + cid);
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
    public static int GetBankDetailsIDByUserID(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.BankDetailsTable.ID, Tables.BankDetailsTable.Table, "where " + Tables.BankDetailsTable.UserID + " = " + UserID);
        return result;
    }

    /**
     *
     * @param CashOutID
     * @param Option
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ProcessCashOut(int CashOutID, String Option) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        int UserID = GetUserIDByCashOutID(CashOutID);
        String Amt = GetAmountByCashOutID(CashOutID);
        int Amount = Integer.parseInt(Amt);
        if (Option.equals("Approved")) {
            result = EngineWalletManager.ComputeWalletRecord(UserID, EngineUserManager.GetAdminUserID(), EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), Amount, "Move Fund", "For cashout/payout request.");
            String msg = "Hi " + EngineUserManager.GetUserName(UserID) + ", \nYour cashout request has been succesfully approved. For further assistance, please contact the support team";
            EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), msg, "Move Fund", UserID);
            DBManager.UpdateStringData(Tables.CashoutTable.Table, Tables.CashoutTable.Status, Option, "where " + Tables.CashoutTable.ID + " = " + CashOutID);
            //create paystack fund transfer to the user's bank account here.
        } else if (Option.equals("Rejected")) {
            result = EngineWalletManager.ComputeWalletRecord(UserID, UserID, EngineWalletManager.GetPendingWalletID(), EngineWalletManager.GetMainWalletID(), Amount, "Move Fund", "For cashout/payout request.");
            String msg = "Hi " + EngineUserManager.GetUserName(UserID) + ", \nYour cashout request was rejected. For further assistance, please contact the support team";
            EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), msg, "Move Fund", UserID);
            DBManager.UpdateStringData(Tables.CashoutTable.Table, Tables.CashoutTable.Status, Option, "where " + Tables.CashoutTable.ID + " = " + CashOutID);
        } else if (Option.equals("Deleted")) {
            result = DBManager.DeleteObject(Tables.CashoutTable.Table, "where " + Tables.CashoutTable.ID + " = " + CashOutID);
        }
        return result;
    }

    /**
     *
     * @param CashoutID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetUserIDByCashOutID(int CashoutID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.CashoutTable.UserID, Tables.CashoutTable.Table, "where " + Tables.CashoutTable.ID + " = " + CashoutID);
        return result;
    }

    /**
     *
     * @param CashoutID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetAmountByCashOutID(int CashoutID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.CashoutTable.Amount, Tables.CashoutTable.Table, "where " + Tables.CashoutTable.ID + " = " + CashoutID);
        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetBankIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.BanksTable.ID, Tables.BanksTable.Table, "ORDER BY " + Tables.BanksTable.Name + " DESC");
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param BankID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetBankData(int BankID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.BanksTable.Table, "where " + Tables.BanksTable.ID + " = " + BankID);
        return Details;
    }

    /**
     *
     * @param BankID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetBankNameByID(int BankID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.BanksTable.Name, Tables.BanksTable.Table, "where " + Tables.BanksTable.ID + " = " + BankID);
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
    public static ArrayList<Integer> GetCashOutIDs(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        if (UserID == 1) {
            IDs = DBManager.GetIntArrayList(Tables.CashoutTable.ID, Tables.CashoutTable.Table, "");
        } else {
            IDs = DBManager.GetIntArrayList(Tables.CashoutTable.ID, Tables.CashoutTable.Table, "where " + Tables.CashoutTable.UserID + " = " + UserID);
        }
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param CashOutID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetCashOutData(int CashOutID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.CashoutTable.Table, "where " + Tables.CashoutTable.ID + " = " + CashOutID);
        if (!Details.isEmpty()) {

            String userid = Details.get(Tables.CashoutTable.UserID);
            int UserID = Integer.parseInt(userid);
            String UserName = EngineUserManager.GetUserName(UserID);
            Details.put("cashUserName", UserName);
            Details.put("CashOutID", "" + CashOutID);

            String rdate = Details.get(Tables.CashoutTable.RequestDate);
            String Rdate = DateManager.readDate(rdate);
            Details.put(Tables.CashoutTable.RequestDate, Rdate);

            String stime = Details.get(Tables.CashoutTable.RequestTime);
            String STime = DateManager.readTime(stime);
            Details.put(Tables.CashoutTable.RequestTime, STime);

            String bankDetID = Details.get(Tables.CashoutTable.BankDetailsID);
            int BankDetailsID = Integer.parseInt(bankDetID);
            HashMap<String, String> BankDetails = GetBankDetailsData(BankDetailsID);
            if (!BankDetails.isEmpty()) {
                Details.putAll(BankDetails);
            }
        }
        return Details;
    }

    /**
     *
     * @param DetailsID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetBankDetailsData(int DetailsID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.BankDetailsTable.Table, "where " + Tables.BankDetailsTable.ID + " = " + DetailsID);
        if (!Details.isEmpty()) {
            String userid = Details.get(Tables.BankDetailsTable.UserID);
            int UserID = Integer.parseInt(userid);
            String UserName = EngineUserManager.GetUserName(UserID);
            Details.put("bankdetailUserName", UserName);

            String bankDetID = Details.get(Tables.BankDetailsTable.BankID);
            int BankDetailsID = Integer.parseInt(bankDetID);
            String BankName = GetBankNameByID(BankDetailsID);
            Details.put("bankName", BankName);
        }
        return Details;
    }

    /**
     *
     * @param BankDetailID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteBankDetails(int BankDetailID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.BankDetailsTable.Table, "where " + Tables.BankDetailsTable.ID + " = " + BankDetailID);
        return result;
    }

    /**
     *
     * @param BankID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteBank(int BankID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.BanksTable.Table, "where " + Tables.BanksTable.ID + " = " + BankID);
        return result;
    }
}
