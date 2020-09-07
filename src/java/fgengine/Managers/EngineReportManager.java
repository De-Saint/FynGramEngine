/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author mac
 */
public class EngineReportManager {

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, Object> GetCustomerStats(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> UserData = new HashMap<>();
        int GetUserAddress = EngineAddressManager.GetUserAddresstIDs(UserID).size();
        UserData.put("address_count", GetUserAddress);

        int GetUserReviews = EngineReviewManager.GetUserRatingIDsByUserID(UserID).size();
        UserData.put("review_count", GetUserReviews);

        int GetUserDiscounts = EngineDiscountManager.GetCustomerDiscountCodeIDsByCustomerUserID(UserID).size();
        UserData.put("discount_count", GetUserDiscounts);

        int GetUserPayments = EnginePaymentManager.GetPaymentIDs(UserID).size();
        UserData.put("payment_count", GetUserPayments);

        double GetUserWalletBalance = EngineWalletManager.GetUserBalance(UserID, EngineWalletManager.GetMainWalletID());
        UserData.put("wallet_balance", GetUserWalletBalance);

        int GetUserMessages = EngineMessageManager.GetInboxMessageIDs(UserID).size();
        UserData.put("message_count", GetUserMessages);

        int GetUserTransactions = EngineTransactionManager.GetTransactionIDs(UserID).size();
        UserData.put("transaction_count", GetUserTransactions);

        int GetUserWishList = EngineCartManager.GetWishListProductCountByUserID("" + UserID);
        UserData.put("wishlist_count", GetUserWishList);
        
        int GetUserOrders = EngineOrderManager.GetOrderIDsByCustomerUserID(UserID).size();
        UserData.put("order_count", GetUserOrders);
        return UserData;
    }
}
