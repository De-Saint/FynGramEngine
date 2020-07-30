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
import java.util.HashMap;

/**
 *
 * @author mac
 */
public class EngineImageManager {

    /**
     *
     * @param ObjectID
     * @param ObjectType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteImage(int ObjectID, String ObjectType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.ImagesTable.Table, "where " + Tables.ImagesTable.ObjectID + " = " + ObjectID + " And " + Tables.ImagesTable.ObjectType + " = '" + ObjectType + "'");
        return result;
    }

    /**
     *
     * @param ObjectID
     * @param ObjectType
     * @param ImageText
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateImage(int ObjectID, String ObjectType, String ImageText) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ImagesTable.ObjectID, ObjectID);
        tableData.put(Tables.ImagesTable.ObjectType, ObjectType);
        tableData.put(Tables.ImagesTable.ImageText, ImageText);
        int imgID = DBManager.insertTableDataReturnID(Tables.ImagesTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.ImagesTable.Table, Tables.ImagesTable.Date, "where " + Tables.ImagesTable.ID + " = " + imgID);
        result = DBManager.UpdateCurrentTime(Tables.ImagesTable.Table, Tables.ImagesTable.Time, "where " + Tables.ImagesTable.ID + " = " + imgID);
        return result;
    }

    /**
     *
     * @param ObjectID
     * @param ObjectType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetImageID(int ObjectID, String ObjectType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.ImagesTable.ID, Tables.ImagesTable.Table, "where " + Tables.ImagesTable.ObjectID + " = " + ObjectID + " And " + Tables.ImagesTable.ObjectType + " = '" + ObjectType + "'");
        return result;
    }

    /**
     *
     * @param ImageID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetImageTextByImageID(int ImageID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.ImagesTable.ImageText, Tables.ImagesTable.Table, "where " + Tables.ImagesTable.ID + " = " + ImageID);
        return result;
    }

    /**
     *
     * @param ImageID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetImageData(int ImageID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.ImagesTable.Table, "where " + Tables.ImagesTable.ID + " = " + ImageID);
        return Details;
    }

    /**
     *
     * @param ObjectID
     * @param ObjectType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList GetImageIDs(int ObjectID, String ObjectType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> result = DBManager.GetIntArrayList(Tables.ImagesTable.ID, Tables.ImagesTable.Table, "where " + Tables.ImagesTable.ObjectID + " = " + ObjectID + " And " + Tables.ImagesTable.ObjectType + " = '" + ObjectType + "'");
        return result;
    }

    /**
     *
     * @param ObjectID
     * @param ObjectType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetFirstImageID(int ObjectID, String ObjectType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetFirstInt(Tables.ImagesTable.ID, Tables.ImagesTable.Table, "where " + Tables.ImagesTable.ObjectID + " = " + ObjectID + " And " + Tables.ImagesTable.ObjectType + " = '" + ObjectType + "'");
        return result;
    }

    /**
     *
     * @param ObjectID
     * @param ObjectType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetSecondImageID(int ObjectID, String ObjectType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetSecondInt(Tables.ImagesTable.ID, Tables.ImagesTable.Table, "where " + Tables.ImagesTable.ObjectID + " = " + ObjectID + " And " + Tables.ImagesTable.ObjectType + " = '" + ObjectType + "' ORDER BY id ASC LIMIT " + 1 + ", " + 1);
        return result;
    }
}
