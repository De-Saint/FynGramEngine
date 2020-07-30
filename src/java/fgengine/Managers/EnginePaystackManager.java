/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import fgengine.Tables.Tables;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author mac
 */
public class EnginePaystackManager {

    /**
     *
     */
    public static EnginePaystackManager instance = null;

    HttpClient client = (HttpClient) new DefaultHttpClient();

    /**
     *
     * @return
     */
    public static EnginePaystackManager getInstance() {
        if (instance == null) {
            instance = new EnginePaystackManager();
        }
        return instance;
    }

    /**
     *
     * @param trxref
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public String PayStackPay(String trxref) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String payres = "";
        String secretKey = GetPaystackSecretKey();
        payres = Pay(trxref, secretKey);
        return payres;
    }

    /**
     *
     * @param trxref
     * @param SecretKey
     * @return
     */
    public String Pay(String trxref, String SecretKey) {
        String payres = "";
        try {
            HttpGet newRequest = new HttpGet("https://api.paystack.co/transaction/verify/" + trxref);
            newRequest.addHeader("Content-type", "application/json");
            newRequest.addHeader("Authorization", "Bearer " + SecretKey);
            newRequest.addHeader("Cache-Control", "no-cache");
            HttpResponse newResponse = this.client.execute((HttpUriRequest) newRequest);
            HttpEntity entity = newResponse.getEntity();
            StringBuilder Sbuilder = new StringBuilder();
            if (entity != null) {
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        Sbuilder.append(line);
                    }
                } catch (IOException | IllegalStateException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new Exception("Error Occured while connecting to paystack url");
            }
            payres = Sbuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return payres;
    }

    /**
     *
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetPaystackSecretKey() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        result = DBManager.GetString(Tables.ParametersTable.PaystackSecretKey, Tables.ParametersTable.Table, "where " + Tables.ParametersTable.ID + " = " + 1);
        return result;
    }

    /**
     *
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetPaystackPublicKey() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        result = DBManager.GetString(Tables.ParametersTable.PaystackPublicKey,  Tables.ParametersTable.Table, "where " + Tables.ParametersTable.ID + " = " + 1);
        return result;
    }
    
    /**
     *
     * @param IpAddress
     * @param AccessKey
     * @return
     */
    public String GetLocation(String IpAddress, String AccessKey) {
        String payres = "";
        try {
            HttpGet newRequest = new HttpGet("http://api.ipstack.com/" + IpAddress + "?access_key=" + AccessKey);
            newRequest.addHeader("Content-type", "application/json");
            newRequest.addHeader("Cache-Control", "no-cache");
            HttpResponse newResponse = this.client.execute((HttpUriRequest) newRequest);
            HttpEntity entity = newResponse.getEntity();
            StringBuilder Sbuilder = new StringBuilder();
            if (entity != null) {
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        Sbuilder.append(line);
                    }
                } catch (IOException | IllegalStateException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new Exception("Error Occured while connecting to paystack url");
            }
            payres = Sbuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return payres;
    }
}
