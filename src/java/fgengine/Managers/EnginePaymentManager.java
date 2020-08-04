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
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mac
 */
public class EnginePaymentManager {

    /**
     *
     * @param UserID
     * @param PaymentType
     * @param Amount
     * @param TransactionCode
     * @param RefereceCode
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws java.text.ParseException
     */
    public static String ComputeSubscriptionFees(int UserID, String PaymentType, int Amount, String TransactionCode, String RefereceCode) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        result = EnginePaymentManager.CreatePayment(UserID, PaymentType, Amount, TransactionCode, RefereceCode);
        if (result.equals("success")) {
            result = EngineWalletManager.ComputeWalletRecord(EngineUserManager.GetAdminUserID(), UserID, EngineWalletManager.GetMainWalletID(), EngineWalletManager.GetPendingWalletID(), Amount, PaymentType, "");
            if (result.equals("success")) {
                result = EngineSubscriptionManager.CreateSubscription(UserID, Amount);
                if (result.endsWith("success")) {
                    String msgbdy = "Congratulations!!! \nYou have successfully paid your subscription fees as a Seller of FynGram Online Store. \nYour Account would be activated and updated after your payment has been confirmed. \nThank you for being part of FynGram Onlne Store";
                    EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), msgbdy, "Seller Account Subscription", UserID);
                    try {
                        String Email = EngineUserManager.GetUserEmail(UserID);
                        EngineEmailManager.SendEmail(Email, msgbdy, "Seller Account Activated");
                    } catch (UnsupportedEncodingException | ClassNotFoundException | SQLException ex) {
                    }
                } else {
                    result = "Something went wrong, not able to create subscription";
                }
            } else {
                result = "Something went wrong, not able to create subscription";
            }
        } else {
            result = "Something went wrong, not able to create subscription";
        }
        return result;
    }

    /**
     *
     * @param UserID
     * @param PaymentType
     * @param Amount
     * @param TransactionCode
     * @param RefereceCode
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreatePayment(int UserID, String PaymentType, int Amount, String TransactionCode, String RefereceCode) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.PaymentsTable.UserID, UserID);
        tableData.put(Tables.PaymentsTable.PaymentType, PaymentType);
        tableData.put(Tables.PaymentsTable.Amount, Amount);
        tableData.put(Tables.PaymentsTable.TransactionCode, TransactionCode);
        tableData.put(Tables.PaymentsTable.ReferenceCode, RefereceCode);
        int payid = DBManager.insertTableDataReturnID(Tables.PaymentsTable.Table, tableData, "");
        String result = DBManager.UpdateCurrentDate(Tables.PaymentsTable.Table, Tables.PaymentsTable.Date, "where " + Tables.PaymentsTable.ID + " = " + payid);
        DBManager.UpdateCurrentTime(Tables.PaymentsTable.Table, Tables.PaymentsTable.Time, "where " + Tables.PaymentsTable.ID + " = " + payid);
        return result;
    }

    /**
     *
     * @param UserID
     * @param TotalAmount
     * @param TransactionCode
     * @param RefereceCode
     * @param PaymentType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputePaymentWithCash(int UserID, int TotalAmount, String TransactionCode, String RefereceCode, String PaymentType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";//
        result = CreatePayment(UserID, PaymentType, TotalAmount, TransactionCode, RefereceCode);
        if (result.equals("success")) {
            if (PaymentType.equals("CheckOut Payment")) {
                result = EngineWalletManager.ComputeWalletRecord(EngineUserManager.GetAdminUserID(), UserID, EngineWalletManager.GetMainWalletID(), EngineWalletManager.GetMainWalletID(), TotalAmount, "Fund Wallet", "For placing an Order.");
                EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), "Hi, " + EngineUserManager.GetUserName(UserID) + ", \nThe Wallet equivalent of " + EngineTransactionManager.FormatNumber(TotalAmount) + " - Order Amount, has been transferred into your wallet and had also been used to pay for the order. \nThe order amount would be refunded into your Main Wallet, if your Order is cancelled.", "CheckOut Payment With Cash", UserID);
            } else {
                result = EngineWalletManager.ComputeWalletRecord(EngineUserManager.GetAdminUserID(), UserID, EngineWalletManager.GetMainWalletID(), EngineWalletManager.GetMainWalletID(), TotalAmount, PaymentType, "For placing an Order.");
                EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), "Hi, " + EngineUserManager.GetUserName(UserID) + ", \nThe Wallet equivalent of " + EngineTransactionManager.FormatNumber(TotalAmount) + ", has been transferred into your wallet ", PaymentType, UserID);
            }
        }
        return result;//return to the browser
    }

    /**
     *
     * @param paymentID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetPaymentsData(int paymentID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> details = DBManager.GetTableData(Tables.PaymentsTable.Table, "where " + Tables.PaymentsTable.ID + " = " + paymentID);
        if (!details.isEmpty()) {
            int UserID = Integer.parseInt(details.get(Tables.PaymentsTable.UserID));
            String UserName = EngineUserManager.GetUserName(UserID);
            details.put("UserName", UserName);
            String dt = details.get(Tables.PaymentsTable.Date);
            String dta = DateManager.readDate(dt);
            details.put(Tables.PaymentsTable.Date, dta);
            String tm = details.get(Tables.PaymentsTable.Time);
            String time = DateManager.readTime(tm);
            details.put(Tables.PaymentsTable.Time, time);
        }

        return details;
    }

    /**
     *
     * @param StartDate
     * @param EndDate
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<String> GetPaymentIDsBtDates(Date StartDate, Date EndDate) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<String> ids = new ArrayList<>();
        ids = DBManager.GetStringArrayList(Tables.PaymentsTable.ID, Tables.PaymentsTable.Table, "where " + Tables.PaymentsTable.Date + " BETWEEN '" + StartDate + "' AND '" + EndDate + "' ORDER BY id DESC");
        ids = UtilityManager.removeDuplicatesStringArrayList(ids);
        return ids;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetPaymentIDs(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        if (UserID == 1) {
            ids = DBManager.GetIntArrayList(Tables.PaymentsTable.ID, Tables.PaymentsTable.Table, "");
        } else {
            ids = DBManager.GetIntArrayList(Tables.PaymentsTable.ID, Tables.PaymentsTable.Table, "where " + Tables.PaymentsTable.UserID + " =  " + UserID);
        }
        ids = UtilityManager.SortAndReverseIntegerArrayList(ids);
        return ids;
    }

    /**
     *
     * @param PaymentID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeletePayment(int PaymentID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.PaymentsTable.Table, "where " + Tables.PaymentsTable.ID + " = " + PaymentID);
        return result;
    }

    /**
     *
     * @param PaymentID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetPaymentReferenceCode(int PaymentID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.PaymentsTable.ReferenceCode, Tables.PaymentsTable.Table, "where " + Tables.PaymentsTable.ID + " = " + PaymentID);
        return result;
    }
}
