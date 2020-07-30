/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import fgengine.Tables.Tables;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mac
 */
public class EngineTransactionManager {

    /**
     *
     * @param SellerTypeID
     * @param NewPercentageAmount
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws ParseException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateAdminTransactionPercentagePerSellerType(int SellerTypeID, int NewPercentageAmount) throws ClassNotFoundException, SQLException, ParseException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.UpdateIntData(Tables.SellerTypesTable.AdminTransactionPercentage, NewPercentageAmount, Tables.SellerTypesTable.Table, "where " + Tables.SellerTypesTable.ID + " = " + SellerTypeID);
        return result;
    }

    /**
     *
     * @param SellerTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws ParseException
     * @throws UnsupportedEncodingException
     */
    public static int GetAdminTransactionPercentageBySellerType(int SellerTypeID) throws ClassNotFoundException, SQLException, ParseException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.SellerTypesTable.AdminTransactionPercentage, Tables.SellerTypesTable.Table, "where " + Tables.SellerTypesTable.ID + " = " + SellerTypeID);
        return result;
    }

    /**
     *
     * @param FromUserID
     * @param ToUserID
     * @param Amount
     * @param TransactionCode
     * @param TransactionRef
     * @param PrimaryWalletNumber
     * @param CreditWalletNumber
     * @param DebitWalletNumber
     * @param ToUserOldBalance
     * @param ToUserNewBalance
     * @param FromUserOldBalance
     * @param FromUserNewBalance
     * @param Description
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String CreateTransaction(int FromUserID, int ToUserID, int Amount, String TransactionCode, String TransactionRef, String PrimaryWalletNumber, String CreditWalletNumber, String DebitWalletNumber, int ToUserOldBalance, int ToUserNewBalance, int FromUserOldBalance, int FromUserNewBalance, String Description) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        HashMap<String, Object> data = new HashMap<>();
        data.put(Tables.TransactionsTable.TransactionCode, TransactionCode);
        data.put(Tables.TransactionsTable.Reference, TransactionRef);
        data.put(Tables.TransactionsTable.FromUserID, FromUserID);
        data.put(Tables.TransactionsTable.ToUserID, ToUserID);
        data.put(Tables.TransactionsTable.Amount, Amount);
        data.put(Tables.TransactionsTable.PrimaryWalletNumber, PrimaryWalletNumber);
        data.put(Tables.TransactionsTable.CreditWalletNumber, CreditWalletNumber);
        data.put(Tables.TransactionsTable.DebitWalletNumber, DebitWalletNumber);
        data.put(Tables.TransactionsTable.Description, Description);
        data.put(Tables.TransactionsTable.ToUserOldBalance, ToUserOldBalance);
        data.put(Tables.TransactionsTable.ToUserNewBalance, ToUserNewBalance);
        data.put(Tables.TransactionsTable.FromUserOldBalance, FromUserOldBalance);
        data.put(Tables.TransactionsTable.FromUserNewBalance, FromUserNewBalance);
        int trxnid = DBManager.insertTableDataReturnID(Tables.TransactionsTable.Table, data, "");
        DBManager.UpdateCurrentDate(Tables.TransactionsTable.Table, Tables.TransactionsTable.Date, "where " + Tables.TransactionsTable.ID + " = " + trxnid);
        result = DBManager.UpdateCurrentTime(Tables.TransactionsTable.Table, Tables.TransactionsTable.Time, "where " + Tables.TransactionsTable.ID + " = " + trxnid);
        return result;
    }

    /**
     *
     * @param Name
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetTransactionTypeCodeByName(String Name) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.TransactionTypesTable.TransactionCode, Tables.TransactionTypesTable.Table, "Where " + Tables.TransactionTypesTable.Name + " = '" + Name + "'");
        return result;
    }

    /**
     *
     * @param FromUserID
     * @param ToUserID
     * @param FromWalletTypeID
     * @param ToWalletTypeID
     * @param Amount
     * @param TransactionTypeName
     * @param FromUserOldBalance
     * @param ToUserOldBalance
     * @param FromUserNewBalance
     * @param ToUserNewBalance
     * @param Description
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputeTransaction(int FromUserID, int ToUserID, int FromWalletTypeID, int ToWalletTypeID, int Amount, String TransactionTypeName, int FromUserOldBalance, int ToUserOldBalance, int FromUserNewBalance, int ToUserNewBalance, String Description) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "";
        String TransactionCode = GetTransactionTypeCodeByName(TransactionTypeName);
        String PrimaryWalletNumber = EngineWalletManager.GetUserWalletNumber(FromUserID);
        String DebitWalletNumber = EngineWalletManager.GetUserWalletNumber(FromUserID);
        String CreditWalletNumber = EngineWalletManager.GetUserWalletNumber(ToUserID);
        String dateNow = "" + UtilityManager.CurrentDate();
        String timeNow = "" + UtilityManager.CurrentTime();
        dateNow = dateNow.replace("-", "");
        timeNow = timeNow.replace(":", "");
        String TransactionRef = FromUserID + "" + ToUserID + "" + UtilityManager.randomAlphaNumeric(5) + dateNow + "" + timeNow;
        result = CreateTransaction(FromUserID, ToUserID, Amount, TransactionCode, TransactionRef, PrimaryWalletNumber, CreditWalletNumber, DebitWalletNumber, ToUserOldBalance, ToUserNewBalance, FromUserOldBalance, FromUserNewBalance, Description);
        return result;
    }

    /**
     *
     * @param Number
     * @return
     */
    public static String FormatNumber(int Number) {
        String formattedNumber = "";
        NumberFormat formatter;
        formatter = NumberFormat.getCurrencyInstance();
        formattedNumber = formatter.format(Number);
        formattedNumber = formattedNumber.substring(1);
        formattedNumber = "NGN" + formattedNumber;
        return formattedNumber;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetTransactionIDs(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        if (UserID == 1) {
            ids = DBManager.GetIntArrayListDescending(Tables.TransactionsTable.ID, Tables.TransactionsTable.Table, "ORDER BY id DESC");
        } else {
            ids = DBManager.GetIntArrayListDescending(Tables.TransactionsTable.ID, Tables.TransactionsTable.Table, "where " + Tables.TransactionsTable.FromUserID + " = " + UserID + " ORDER BY id DESC");
            ids.addAll(DBManager.GetIntArrayListDescending(Tables.TransactionsTable.ID, Tables.TransactionsTable.Table, "where " + Tables.TransactionsTable.ToUserID + " = " + UserID + " ORDER BY id DESC"));
            ids = UtilityManager.removeDuplicatesIntegerArrayList(ids);
        }
        return ids;
    }

    /**
     *
     * @param transactionID
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetTransactionDetails(int transactionID, int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> transactiondetails = DBManager.GetTableData(Tables.TransactionsTable.Table, "where " + Tables.TransactionsTable.ID + " = " + transactionID);
        String CreditAccountNum = transactiondetails.get(Tables.TransactionsTable.CreditWalletNumber);
        String DebitAccountNum = transactiondetails.get(Tables.TransactionsTable.DebitWalletNumber);

        String TransactionCode = transactiondetails.get(Tables.TransactionsTable.TransactionCode);
        String TransactionName = DBManager.GetString(Tables.TransactionTypesTable.Name, Tables.TransactionTypesTable.Table, "where " + Tables.TransactionTypesTable.TransactionCode + " = '" + TransactionCode + "'");
        transactiondetails.put("NameOfTransaction", TransactionName);
        int FromUserID = Integer.parseInt(transactiondetails.get(Tables.TransactionsTable.FromUserID));
        int ToUserID = Integer.parseInt(transactiondetails.get(Tables.TransactionsTable.ToUserID));
        transactiondetails.put("TransID", "" + transactionID);
        if (UserID == ToUserID) {
            transactiondetails.put("OtherAccountNumber", DebitAccountNum);
            String OtherAccountName = EngineUserManager.GetUserName(ToUserID);
            transactiondetails.put("OtherAccountName", OtherAccountName);
            transactiondetails.put("OtherTransactionType", "Credit");
        } else {
            transactiondetails.put("OtherAccountNumber", CreditAccountNum);
            String OtherAccountName = EngineUserManager.GetUserName(ToUserID);
            transactiondetails.put("OtherAccountName", OtherAccountName);
            transactiondetails.put("OtherTransactionType", "Debit");
        }
        transactiondetails.put("CreditAccountNumber", EngineWalletManager.GetUserWalletNumber(ToUserID));
        transactiondetails.put("DebitAccountNumber", EngineWalletManager.GetUserWalletNumber(FromUserID));
        transactiondetails.put("FromUserNewBalance", transactiondetails.get(Tables.TransactionsTable.FromUserNewBalance));

        String Date = transactiondetails.get(Tables.TransactionsTable.Date);
        String date = DateManager.readDate(Date);
        transactiondetails.put(Tables.TransactionsTable.Date, date);
        String Time = transactiondetails.get(Tables.TransactionsTable.Time);
        String time = DateManager.readTime(Time);
        transactiondetails.put(Tables.TransactionsTable.Time, time);
        String PryAccountName = EngineUserManager.GetUserName(FromUserID);
        transactiondetails.put("PrimaryAccountName", PryAccountName);
        return transactiondetails;
    }

    /**
     *
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetTransactionTypeIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.TransactionTypesTable.ID, Tables.TransactionTypesTable.Table, "");
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param TTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetTransactionTypeData(int TTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.TransactionTypesTable.Table, "where " + Tables.TransactionTypesTable.ID + " = " + TTypeID);
        return Details;
    }
    /**
     *
     * @param TransactionID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteTransaction(int TransactionID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.TransactionsTable.Table, "where " + Tables.TransactionsTable.ID + " = " + TransactionID);
        return result;
    }
    
    

}
