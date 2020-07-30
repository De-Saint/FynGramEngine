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
    public static String CreateReview(int UserID, int RateValue, int ObjectID, String ObjectType, String Comment) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
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
    public static String ComputeObjectAverageReview(int ObjectID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int totalratevalue = 0;
        double averageValue = 0.0;
        int ratevalue = 0;
        ArrayList<Integer> UserRatingIDs = GetObjectRatingIDs(ObjectID);
        if (!UserRatingIDs.isEmpty()) {
            for (int rateid : UserRatingIDs) {
                ratevalue = GetRateValueByReviewID(rateid);
                totalratevalue += ratevalue;
            }

            averageValue = totalratevalue / UserRatingIDs.size();
        }
        result = "" + averageValue;
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
    public static ArrayList<Integer> GetUserRatingIDsByUserID(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.ReviewsTable.ID, Tables.ReviewsTable.Table, "where " + Tables.ReviewsTable.UserID + " = " + UserID);
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
        ids = DBManager.GetIntArrayList(Tables.ReviewsTable.ID, Tables.ReviewsTable.Table, "where " + Tables.ReviewsTable.ObjectID + " = " + ObjectID);
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
    public static int GetRateValueByReviewID(int ReviewID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.ReviewsTable.RateValue, Tables.ReviewsTable.Table, "where " + Tables.ReviewsTable.ID + " = " + ReviewID);
        return result;
    }
}
