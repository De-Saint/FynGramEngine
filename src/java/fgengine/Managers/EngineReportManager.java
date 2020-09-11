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

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, Object> GetStats(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> UserData = new HashMap<>();
        if (UserID == 1) {
            int GetUserAddress = EngineAddressManager.GetUserAddresstIDs(UserID).size();
            UserData.put("address_count", GetUserAddress);

            int GetUserDiscounts = EngineDiscountManager.GetDiscountCodeIDs().size();
            UserData.put("discount_count", GetUserDiscounts);

            int GetUserMessages = EngineMessageManager.GetAllMessagesTable().size();
            UserData.put("message_count", GetUserMessages);

            int GetShippings = EngineShippingManager.GetShippingIDs().size();
            UserData.put("shipping_count", GetShippings);

            int GetUserCategory = EngineCategoryManager.GetCategoryIDs().size();
            UserData.put("category_count", GetUserCategory);

            int GetUserProperties = EngineCategoryManager.GetPropertyIDs().size();
            UserData.put("properties_count", GetUserProperties);

            int GetUserCustomers = EngineUserManager.GetAllCustomerUsers().size();
            UserData.put("customer_count", GetUserCustomers);

            int GetUserGuest = EngineUserManager.GetAllGuests().size();
            UserData.put("guest_count", GetUserGuest);

            int GetUserSellers = EngineUserManager.GetAllSellerUsers().size();
            UserData.put("sellers_count", GetUserSellers);

            int GetUserReviews = EngineReviewManager.GetUserRatingIDsByUserID(UserID).size();
            UserData.put("review_count", GetUserReviews);
        } else {
            int GetUserMessages = EngineMessageManager.GetInboxMessageIDs(UserID).size();
            UserData.put("message_count", GetUserMessages);
        }

        int GetUserSubs = EngineSubscriptionManager.GetAllSubscriptionIDs(UserID).size();
        UserData.put("subscription_count", GetUserSubs);

        int GetUserPayments = EnginePaymentManager.GetPaymentIDs(UserID).size();
        UserData.put("payment_count", GetUserPayments);

        double GetUserWalletBalance = EngineWalletManager.GetUserBalance(UserID, EngineWalletManager.GetMainWalletID());
        UserData.put("wallet_balance", GetUserWalletBalance);

        int GetUserTransactions = EngineTransactionManager.GetTransactionIDs(UserID).size();
        UserData.put("transaction_count", GetUserTransactions);

        int GetUserProducts = EngineProductManager.GetProductIDs(UserID).size();
        UserData.put("product_count", GetUserProducts);

        int GetUserCashOut = EngineCashoutManager.GetCashOutIDs(UserID).size();
        UserData.put("cashout_count", GetUserCashOut);

        int GetUserOrders = EngineOrderManager.GetOrderIDs(UserID).size();
        UserData.put("order_count", GetUserOrders);
        return UserData;
    }
}
