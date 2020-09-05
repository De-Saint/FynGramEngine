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
public class EngineWalletManager {

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetMainWalletID() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.WalletTypesTable.ID, Tables.WalletTypesTable.Table, "where " + Tables.WalletTypesTable.ID + " = " + 1);
        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetPendingWalletID() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.WalletTypesTable.ID, Tables.WalletTypesTable.Table, "where " + Tables.WalletTypesTable.ID + " = " + 2);
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
    public static String CreateWallet(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        String WalletNumber = ComputeWalletNumber(UserID);
        String Balance = GetMainWalletID() + ":" + 0 + ";" + GetPendingWalletID() + ":" + 0;//1:0;2:0
        String WalletPin = ComputeWalletPin(UserID);
        HashMap<String, Object> data = new HashMap<>();
        data.put(Tables.WalletTable.UserID, UserID);
        data.put(Tables.WalletTable.Balance, Balance);
        data.put(Tables.WalletTable.WalletNumber, WalletNumber);
        data.put(Tables.WalletTable.WalletPin, WalletPin);
        result = DBManager.insertTableData(Tables.WalletTable.Table, data, "");
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
    public static String ComputeWalletNumber(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        String Firstname = EngineUserManager.GetUserFirstName(UserID);
        String Lastname = EngineUserManager.GetUserLastName(UserID);

        String FNamefirstCha = UtilityManager.GetFirstCharacterText(Firstname.toUpperCase(), 2);
        String LNfirstCha = UtilityManager.GetFirstCharacterText(Lastname.toUpperCase(), 2);

        String FNamelastCha = UtilityManager.GetLastCharacterText(Firstname.toUpperCase(), 2);
        String LNlastCha = UtilityManager.GetLastCharacterText(Lastname.toUpperCase(), 2);

        String randomString = UtilityManager.GenerateRandomNumber(2);
        result = LNlastCha + FNamefirstCha + randomString + LNfirstCha + FNamelastCha;
        return result;
    }

    /**
     *
     * @param UserID
     * @return
     */
    public static String ComputeWalletPin(int UserID) {
        String result, num = "";
        String userid = "" + UserID;
        if (userid.length() > 2) {
            num = UtilityManager.GetFirstCharacterText(userid, 2);
        } else if (userid.length() < 2) {
            num = userid + "1";
        }
        result = UtilityManager.GenerateRandomNumber(2);
        result = result + num;
        if (result.length() == 4) {
            return result;
        } else {
            if (result.length() == 3) {
                result = result + "1";
                return result;
            }
            if (result.length() == 2) {
                result = result + "01";
            }
            if (result.length() == 1) {
                result = result + UtilityManager.GenerateRandomNumber(2);
                return result;
            }

        }
        return result;
    }

    /**
     *
     * @param FromUserID
     * @param ToUserID
     * @param TransactionAmount
     * @param FromWalletTypeID
     * @param ToWalletTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String CreateWalletRecord(int FromUserID, int ToUserID, double TransactionAmount, int FromWalletTypeID, int ToWalletTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "falied";
        result = InsertWalletRecord(FromUserID, TransactionAmount, FromWalletTypeID, "Debit");
        if (result.equals("success")) {
            result = InsertWalletRecord(ToUserID, TransactionAmount, ToWalletTypeID, "Credit");
        } else {
            //refund
            InsertWalletRecord(FromUserID, TransactionAmount, FromWalletTypeID, "Credit");
        }
        return result;
    }

//    public static String Process
    /**
     *
     * @param FromUserID
     * @param ToUserID
     * @param FromWalletTypeID
     * @param ToWalletTypeID
     * @param TransactionAmount
     * @param TransactionTypeName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputeWalletRecord(int FromUserID, int ToUserID, int FromWalletTypeID, int ToWalletTypeID, double TransactionAmount, String TransactionTypeName, String Narration) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        String Description = "";
        int ToUserOldBalance = 0;
        int FromUserOldBalance = 0;
        int ToUserNewBalance = 0;
        int FromUserNewBalance = 0;
        String toBodyMsg = "";
        String fromBodyMsg = "";
        String FromWalletName = GetWalletNameByID(FromWalletTypeID);
        String ToWalletName = GetWalletNameByID(ToWalletTypeID);
        if (TransactionTypeName.equals("Subscription Fees")) {
            result = EngineWalletManager.InsertWalletRecord(FromUserID, TransactionAmount, FromWalletTypeID, "Credit");
            Description = "Hi " + EngineUserManager.GetUserName(ToUserID) + ", \n\nYou have successfully transferred " + EngineTransactionManager.FormatNumber(TransactionAmount) + " to Fyngram Account as payment for your Subscription. \n\nCheers \nFyngram.";
            EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), Description, TransactionTypeName, ToUserID);
        } else if (TransactionTypeName.equals("Activate Supplier Account")) {
            Description = "Seller's Account Activation for " + EngineUserManager.GetUserName(FromUserID);
            toBodyMsg = "Hi " + EngineUserManager.GetUserName(FromUserID) + ", \n\nYour Seller's Account has been successfully activated, and your Subscription Fees recieved. \n\nCheers \nFyngram.";
            EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), toBodyMsg, TransactionTypeName, FromUserID);
        } else if (TransactionTypeName.equals("Fund Wallet")) {
            result = EngineWalletManager.InsertWalletRecord(FromUserID, TransactionAmount, FromWalletTypeID, "Credit");
            Description = "Hi " + EngineUserManager.GetUserName(ToUserID) + ", \n\nYou funded your Wallet - " + EngineWalletManager.GetUserWalletNumber(ToUserID) + " with " + EngineTransactionManager.FormatNumber(TransactionAmount);
            EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), Description, TransactionTypeName, ToUserID);
        } else if (TransactionTypeName.equals("Move Fund")) {
            if (FromWalletTypeID == 1) {
                fromBodyMsg = "Hi " + EngineUserManager.GetUserName(FromUserID) + ", \n\n" + EngineTransactionManager.FormatNumber(TransactionAmount) + " had been deducted from your " + FromWalletName + " Wallet. \n\nCheers \nFyngram.";
                EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), fromBodyMsg, TransactionTypeName, FromUserID);
            }
            if (ToWalletTypeID == 1) {
                toBodyMsg = "Hi " + EngineUserManager.GetUserName(ToUserID) + ", \n\n" + EngineTransactionManager.FormatNumber(TransactionAmount) + " had been credited into your " + ToWalletName + " Wallet. \n\nCheers \nFyngram.";
                EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), toBodyMsg, TransactionTypeName, ToUserID);
            }
            Description = EngineTransactionManager.FormatNumber(TransactionAmount) + " has been transferred from your " + GetWalletNameByID(FromWalletTypeID) + " Wallet to your " + GetWalletNameByID(ToWalletTypeID) + " Wallet. - " + Narration + ". \n\nPlease, you can also check your messages for details. \n\nCheers \nFyngram.";
        }
        FromUserOldBalance = GetUserBalance(FromUserID, FromWalletTypeID);
        ToUserOldBalance = GetUserBalance(ToUserID, ToWalletTypeID);
        result = EngineWalletManager.CreateWalletRecord(FromUserID, ToUserID, TransactionAmount, FromWalletTypeID, ToWalletTypeID);
        FromUserNewBalance = GetUserBalance(FromUserID, FromWalletTypeID);
        ToUserNewBalance = GetUserBalance(ToUserID, ToWalletTypeID);
        result = EngineTransactionManager.ComputeTransaction(FromUserID, ToUserID, FromWalletTypeID, ToWalletTypeID, TransactionAmount, TransactionTypeName, FromUserOldBalance, ToUserOldBalance, FromUserNewBalance, ToUserNewBalance, Description);
        return result;
    }

    /**
     *
     * @param UserID
     * @param TransactionAmount
     * @param WalletType
     * @param TransactionType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String InsertWalletRecord(int UserID, double TransactionAmount, int WalletType, String TransactionType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        String userbalance = DBManager.GetString(Tables.WalletTable.Balance, Tables.WalletTable.Table, "where " + Tables.WalletTable.UserID + " = " + UserID);
        if (WalletType == 1) {
            String mainBalRes = userbalance.split(";")[0];//1:0
            int mainBalID = Integer.parseInt(mainBalRes.split(":")[0]);
            int mainBalValue = Integer.parseInt(mainBalRes.split(":")[1]);
            if (TransactionType.equals("Credit")) {
                mainBalValue += TransactionAmount;
            } else if (TransactionType.equals("Debit")) {
                mainBalValue -= TransactionAmount;
            }
            String newMainBalRes = mainBalID + ":" + mainBalValue + ";" + userbalance.split(";")[1];
            userbalance = userbalance.replace(userbalance, newMainBalRes);

        } else {
            String PendingBalRes = userbalance.split(";")[1];//2:0
            int PendingBalID = Integer.parseInt(PendingBalRes.split(":")[0]);
            int PendingBalValue = Integer.parseInt(PendingBalRes.split(":")[1]);
            if (TransactionType.equals("Credit")) {
                PendingBalValue += TransactionAmount;
            } else if (TransactionType.equals("Debit")) {
                PendingBalValue -= TransactionAmount;
            }
            String newPendingBalRes = userbalance.split(";")[0] + ";" + PendingBalID + ":" + PendingBalValue;
            userbalance = userbalance.replace(userbalance, newPendingBalRes);
        }
        result = DBManager.UpdateStringData(Tables.WalletTable.Table, Tables.WalletTable.Balance, userbalance, "where " + Tables.WalletTable.UserID + " = " + UserID);
        return result;
    }

    /**
     *
     * @param UserID
     * @param WalletType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetUserBalance(int UserID, int WalletType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        String userbalance = DBManager.GetString(Tables.WalletTable.Balance, Tables.WalletTable.Table, "where " + Tables.WalletTable.UserID + " = " + UserID);
        if (userbalance.length() > 0) {
            if (WalletType == 1) {
                String mainBalRes = userbalance.split(";")[0];//1:0
                String mainBalValue = mainBalRes.split(":")[1];
                result = Integer.parseInt(mainBalValue);
            } else {
                String PendingBalRes = userbalance.split(";")[1];//2:0
                String PendingBalValue = PendingBalRes.split(":")[1];
                result = Integer.parseInt(PendingBalValue);
            }
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
    public static String GetUserWalletNumber(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.GetString(Tables.WalletTable.WalletNumber, Tables.WalletTable.Table, "where " + Tables.WalletTable.UserID + " = " + UserID);
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
    public static int GetUserWalletPIN(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.WalletTable.WalletPin, Tables.WalletTable.Table, "where " + Tables.WalletTable.UserID + " = " + UserID);
        return result;
    }

    /**
     *
     * @param WalletID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetWalletNameByID(int WalletID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.WalletTypesTable.Name, Tables.WalletTypesTable.Table, "where " + Tables.WalletTypesTable.ID + " = " + WalletID);
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
    public static HashMap<String, String> ComputeWalletDetails(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> data = GetWalletDetailsByUserID(UserID);
        if (!data.isEmpty()) {
            int UserBalance = GetUserBalance(UserID, GetMainWalletID());
            data.put("MainBalance", "" + UserBalance);
            int UserPendingBalance = GetUserBalance(UserID, GetPendingWalletID());
            data.put("PendingBalance", "" + UserPendingBalance);
            int usertype = EngineUserManager.GetUserTypeIDByUserID("" + UserID);
            if (usertype == 1) {
                int TotalSellerBalance = GetAllSellersMainBalance();
                data.put("TotalSellerBalance", "" + TotalSellerBalance);

                int TotalSellersPendingBalance = GetAllSellersPendingBalance();

                int TotalCustomerBalance = GetAllCustomersBalance();
                data.put("TotalCustomerBalance", "" + TotalCustomerBalance);

                int TotalCustomersPendingBalance = GetAllCustomersPendingBalance();

                int TotalMainWallets = UserBalance + TotalSellerBalance + TotalCustomerBalance;
                data.put("TotalMainWallets", "" + TotalMainWallets);

                int TotalPendingWallets = UserPendingBalance + TotalSellersPendingBalance + TotalCustomersPendingBalance;
                data.put("TotalPendingWallets", "" + TotalPendingWallets);

                double TotalShippingEarnings = EngineShippingManager.GetAllShippingBalances();
                data.put("TotalShippingEarnings", "" + TotalShippingEarnings);

            }
        }

        return data;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetWalletDetailsByUserID(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> data = DBManager.GetTableData(Tables.WalletTable.Table, "where " + Tables.WalletTable.UserID + " = " + UserID);
        return data;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetAllSellersMainBalance() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        ArrayList<Integer> SellerIDs = EngineUserManager.GetAllSellerUsers();
        if (!SellerIDs.isEmpty()) {
            for (int sellerid : SellerIDs) {
                int sellerbal = GetUserBalance(sellerid, GetMainWalletID());
                result = result + sellerbal;
            }
        }
        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetAllSellersPendingBalance() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        ArrayList<Integer> SellerIDs = EngineUserManager.GetAllSellerUsers();
        if (!SellerIDs.isEmpty()) {
            for (int sellerid : SellerIDs) {
                int sellerbal = GetUserBalance(sellerid, GetPendingWalletID());
                result = result + sellerbal;
            }
        }
        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetAllCustomersBalance() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        ArrayList<Integer> CustomerIDs = EngineUserManager.GetAllCustomerUsers();
        if (!CustomerIDs.isEmpty()) {
            for (int sellerid : CustomerIDs) {
                int customerbal = GetUserBalance(sellerid, GetMainWalletID());
                result = result + customerbal;
            }
        }
        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetAllCustomersPendingBalance() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        ArrayList<Integer> CustomerIDs = EngineUserManager.GetAllCustomerUsers();
        if (!CustomerIDs.isEmpty()) {
            for (int sellerid : CustomerIDs) {
                int customerbal = GetUserBalance(sellerid, GetPendingWalletID());
                result = result + customerbal;
            }
        }
        return result;
    }

    /**
     *
     * @param WalletNumber
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetUseIDByWalletNumber(String WalletNumber) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.WalletTable.UserID, Tables.WalletTable.Table, "where " + Tables.WalletTable.WalletNumber + " = '" + WalletNumber + "'");
        return result;
    }
}
