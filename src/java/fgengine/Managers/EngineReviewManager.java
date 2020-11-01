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
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mac
 */
public class EngineReviewManager {

    /**
     *
     * @param UserID
     * @param RateValue
     * @param ObjectID
     * @param ObjectType
     * @param Comment
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateReview(int UserID, double RateValue, int ObjectID, String ObjectType, String Comment) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ReviewsTable.UserID, UserID);
        tableData.put(Tables.ReviewsTable.RateValue, RateValue);
        tableData.put(Tables.ReviewsTable.ObjectID, ObjectID);
        tableData.put(Tables.ReviewsTable.ObjectType, ObjectType);
        tableData.put(Tables.ReviewsTable.Comment, Comment);
        int rateid = DBManager.insertTableDataReturnID(Tables.ReviewsTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.ReviewsTable.Table, Tables.ReviewsTable.Date, "where " + Tables.ReviewsTable.ID + " = " + rateid);
        String result = DBManager.UpdateCurrentTime(Tables.ReviewsTable.Table, Tables.ReviewsTable.Time, "where " + Tables.ReviewsTable.ID + " = " + rateid);
        return result;
    }

    /**
     *
     * @param ObjectID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static double ComputeObjectAverageReview(int ObjectID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double totalratevalue = 0.0;
        double averageValue = 0.0;
        ArrayList<Integer> UserRatingIDs = GetObjectRatingIDs(ObjectID);
        if (!UserRatingIDs.isEmpty()) {
            for (int rateid : UserRatingIDs) {
                double RateValue = GetRateValueByReviewID(rateid);
                totalratevalue += RateValue;
            }

            averageValue = totalratevalue / UserRatingIDs.size();
        }
        return averageValue;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetUserRatingIDsByUserID(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        if (UserID == 1) {
            ids = DBManager.GetIntArrayListDescending(Tables.ReviewsTable.ID, Tables.ReviewsTable.Table, "Order by " + Tables.ReviewsTable.ID);
        } else {
            ids = DBManager.GetIntArrayListDescending(Tables.ReviewsTable.ID, Tables.ReviewsTable.Table, "where " + Tables.ReviewsTable.UserID + " = " + UserID + " Order by " + Tables.ReviewsTable.ID);
        }
        return ids;
    }

    /**
     *
     * @param ObjectID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetObjectRatingIDs(int ObjectID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayListDescending(Tables.ReviewsTable.ID, Tables.ReviewsTable.Table, "where " + Tables.ReviewsTable.ObjectID + " = " + ObjectID + " Order by " + Tables.ReviewsTable.ID);
        return ids;
    }

    /**
     *
     * @param ObjectID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetRatingIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.ReviewsTable.ID, Tables.ReviewsTable.Table, " Order by " + Tables.ReviewsTable.ID);
        return ids;
    }

    /**
     *
     * @param ReviewID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static double GetRateValueByReviewID(int ReviewID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double result = DBManager.GetDouble(Tables.ReviewsTable.RateValue, Tables.ReviewsTable.Table, "where " + Tables.ReviewsTable.ID + " = " + ReviewID);
        return result;
    }

    /**
     *
     * @param ObjectID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> ObjectReviews(int ObjectID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> data = new HashMap<>();
        double averageRatings = ComputeObjectAverageReview(ObjectID);
        int ratings = GetObjectRatingIDs(ObjectID).size();
        data.put("AverageRatings", "" + averageRatings);
        data.put("NumberOfRatings", "" + ratings);
        return data;
    }

    /**
     *
     * @param ObjectID
     * @param ObectType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<Integer, HashMap<String, String>> GetObjectReviewList(int ObjectID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<Integer, HashMap<String, String>> ReviewList = new HashMap<>();
        HashMap<String, String> List = new HashMap<>();
        ArrayList<Integer> RatingIDS = GetObjectRatingIDs(ObjectID);
        if (!RatingIDS.isEmpty()) {
            for (int ratingID : RatingIDS) {
                List = ReviewData(ratingID);
                if (!List.isEmpty()) {
                    ReviewList.put(ratingID, List);
                }
            }
        }
        return ReviewList;
    }

    /**
     *
     * @param ObjectID
     * @param ObectType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<HashMap<String, String>> GetObjectReviewList2(int ObjectID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        HashMap<String, String> List = new HashMap<>();
        ArrayList<Integer> RatingIDS = GetObjectRatingIDs(ObjectID);
        if (!RatingIDS.isEmpty()) {
            for (int ratingID : RatingIDS) {
                List = ReviewData(ratingID);
                if (!List.isEmpty()) {
                    list.add(List);
                }
            }
        }
        return list;
    }

    /**
     *
     * @param ReviewID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> ReviewData(int ReviewID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> data = DBManager.GetTableData(Tables.ReviewsTable.Table, "where " + Tables.ReviewsTable.ID + " = " + ReviewID);
        if (!data.isEmpty()) {

            String userid = data.get(Tables.ReviewsTable.UserID);
            int userID = Integer.parseInt(userid);
            String UserName = EngineUserManager.GetUserName(userID);
            data.put("reviewUsername", UserName);
            data.put("reviewUserImage", "none");
            String prodid = data.get(Tables.ReviewsTable.ObjectID);
            int ProductID = Integer.parseInt(prodid);
            String productName = EngineProductManager.GetProductNameByProductID(ProductID);
            data.put("reviewProductName", productName);
            String productDesc = EngineProductManager.GetProductDescriptionByProductID(ProductID);
            data.put("reviewProductDesc", productDesc);
            data.put("reviewProductImage", "none");

            String dt = data.get(Tables.ReviewsTable.Date);
            String date = DateManager.readDate(dt);
            data.put(Tables.ReviewsTable.Date, date);

            String tm = data.get(Tables.ReviewsTable.Time);
            String time = DateManager.readTime(tm);
            data.put(Tables.ReviewsTable.Time, time);
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
    public static HashMap<Integer, HashMap<String, String>> GetUserReviewList(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<Integer, HashMap<String, String>> ReviewList = new HashMap<>();
        HashMap<String, String> List = new HashMap<>();
        ArrayList<Integer> RatingIDS = new ArrayList<>();
        if (UserID == 1) {
            RatingIDS = GetRatingIDs();
        } else {
            RatingIDS = GetUserRatingIDsByUserID(UserID);
        }

        if (!RatingIDS.isEmpty()) {
            for (int ratingID : RatingIDS) {
                List = ReviewData(ratingID);
                if (!List.isEmpty()) {
                    ReviewList.put(ratingID, List);
                }
            }
        }
        return ReviewList;
    }

    /**
     *
     * @param ReviewID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteReview(int ReviewID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.ReviewsTable.Table, "where " + Tables.ReviewsTable.ID + " = " + ReviewID);
        return result;
    }

}
