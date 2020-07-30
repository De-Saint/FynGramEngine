/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import fgengine.Tables.Tables;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;

/**
 *
 * @author mac
 */
public class EnginePushManager {

    /**
     *
     * @param title
     * @param message
     * @param additionalData
     * @param userid
     * @return
     * @throws Exception
     */
    public static HttpURLConnection androidPushNotification(String title, String message, String additionalData, int userid) throws Exception {
        HttpURLConnection conn = null;
        String SERVER_KEY = "AAAAc8K4P_w:APA91bEMJVZ80rkTzPKkblkp_zoqinUIN8K7sCKlIHIHY-FthS0GvSFrp8chgQINitmKzFydk_-VJ6YbelT7uBiCb4t1b8PGjTQ6PpYRlxbTI0K_zZ-93O0Sjfh5eJHriBG12fhLQ6FZPneMBZ2_TwX4GUWmXDUHWg";
        String DEVICE_TOKEN = GetDeviceToken(userid);
        String pushMessage = "{\"data\":{\"title\":\"" + title + "\",\"message\":\"" + message + "\",\"extradata\":\"" + additionalData + "\"},\"to\":\"" + DEVICE_TOKEN + "\"}";
        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(pushMessage.getBytes());
            System.out.println(conn.getResponseCode());
            System.out.println(conn.getResponseMessage());
            String result = conn.getResponseMessage();
        } catch (IOException e) {
            String error = e.getMessage();
            System.out.print(error);
            return conn;
        }
        return conn;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetDeviceToken(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
    String result = DBManager.GetString(Tables.UsersTable.DeviceToken, Tables.UsersTable.Table, "where " + Tables.UsersTable.ID + " = " + UserID);
    return result;
}

    /**
     *
     * @param UserID
     * @param DeviceToken
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateDeviceToken(int UserID, String DeviceToken) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        result = DBManager.UpdateStringData(Tables.UsersTable.Table, Tables.UsersTable.DeviceToken, DeviceToken, "where " + Tables.UsersTable.ID + " = " + UserID);
        return result;
    }
}
