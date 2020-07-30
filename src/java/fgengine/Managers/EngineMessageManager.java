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
public class EngineMessageManager {

    /**
     *
     * @param sender
     * @param bdy
     * @param subject
     * @param recipientid
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String sendMessage(int sender, String bdy, String subject, int recipientid) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.MessagesTable.Subject, subject);
        tableData.put(Tables.MessagesTable.IsRead, 0);
        tableData.put(Tables.MessagesTable.FromUserID, sender);
        tableData.put(Tables.MessagesTable.ToUserID, recipientid);
        tableData.put(Tables.MessagesTable.Body, bdy);
        tableData.put(Tables.MessagesTable.Deleted, 0);
        int msgid = DBManager.insertTableDataReturnID(Tables.MessagesTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.MessagesTable.Table, Tables.MessagesTable.Date, "where " + Tables.MessagesTable.ID + " = " + msgid);
        String result = DBManager.UpdateCurrentTime(Tables.MessagesTable.Table, Tables.MessagesTable.Time, "where " + Tables.MessagesTable.ID + " = " + msgid);
        return result;
    }

    /**
     *
     * @param MsgID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetMessageDetails(int MsgID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> msgdetails = DBManager.GetTableData(Tables.MessagesTable.Table, "where " + Tables.MessagesTable.ID + " = " + MsgID);
        if (!msgdetails.isEmpty()) {
            int FromUserID = Integer.parseInt(msgdetails.get(Tables.MessagesTable.FromUserID));
            String sendername = EngineUserManager.GetUserName(FromUserID);
            String senderemail = EngineUserManager.GetUserEmail(FromUserID);
            msgdetails.put("SenderName", sendername);
            msgdetails.put("SenderEmail", senderemail);
            int ToUserID = Integer.parseInt(msgdetails.get(Tables.MessagesTable.ToUserID));
            String recievername = EngineUserManager.GetUserName(ToUserID);
            String recieveremail = EngineUserManager.GetUserEmail(ToUserID);
            msgdetails.put("RecieverName", recievername);
            msgdetails.put("RecieverEmail", recieveremail);
            String Msgdate = msgdetails.get(Tables.MessagesTable.Date);
            String mdate = DateManager.readDate(Msgdate);
            msgdetails.put(Tables.MessagesTable.Date, mdate);

            String msgtime = msgdetails.get(Tables.MessagesTable.Time);
            String mtime = DateManager.readTime(msgtime);
            msgdetails.put(Tables.MessagesTable.Time, mtime);
            msgdetails.put("msgid", "" + MsgID);
        }
        return msgdetails;
    }

    /**
     *
     * @param meid
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetSentMessageIDs(int meid) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.MessagesTable.ID, Tables.MessagesTable.Table, "where " + Tables.MessagesTable.FromUserID + " = " + meid + " and " + Tables.MessagesTable.Deleted + " = " + 0);
        return ids;
    }

    /**
     *
     * @param meid
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetInboxMessageIDs(int meid) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.MessagesTable.ID, Tables.MessagesTable.Table, "where " + Tables.MessagesTable.ToUserID + " = " + meid + " and " + Tables.MessagesTable.Deleted + " = " + 0);
        ids = UtilityManager.SortAndReverseIntegerArrayList(ids);
        return ids;
    }

    /**
     *
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetAllMessagesTable() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.MessagesTable.ID, Tables.MessagesTable.Table, "where " + Tables.MessagesTable.Deleted + " = " + 0);
        ids = UtilityManager.SortAndReverseIntegerArrayList(ids);
        return ids;
    }

    /**
     *
     * @param msgid
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String MarkAsRead(int msgid) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateIntData(Tables.MessagesTable.IsRead, 1, Tables.MessagesTable.Table, "where " + Tables.MessagesTable.ID + " = " + msgid);
        return result;
    }

    /**
     *
     * @param msgid
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteMessage(int msgid) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.MessagesTable.Table, "where " + Tables.MessagesTable.ID + " = " + msgid);
        return result;
    }

    /**
     *
     * @param meid
     * @param start
     * @param limit
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetUnreadMessageIDs(int meid, int start, int limit) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.MessagesTable.ID, Tables.MessagesTable.Table, "where " + Tables.MessagesTable.ToUserID + " = " + meid + " and " + Tables.MessagesTable.IsRead + " = " + 0 + " LIMIT " + start + ", " + limit);
        return ids;
    }

    /**
     *
     * @param meid
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetUnreadMessageCount(int meid) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = new ArrayList<>();
        ids = DBManager.GetIntArrayList(Tables.MessagesTable.ID, Tables.MessagesTable.Table, "where " + Tables.MessagesTable.ToUserID + " = " + meid + " and " + Tables.MessagesTable.IsRead + " = " + 0);
        return ids;
    }

    /**
     *
     * @param searchtxt
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetSearchedMessageIDs(String searchtxt) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> ids = DBManager.GetIntArrayList(Tables.MessagesTable.ID, Tables.MessagesTable.Table, "where " + Tables.MessagesTable.Subject + " LIKE '%" + searchtxt + "%' OR " + Tables.MessagesTable.Body + " LIKE '%" + searchtxt + "%'");
        return ids;
    }
}
