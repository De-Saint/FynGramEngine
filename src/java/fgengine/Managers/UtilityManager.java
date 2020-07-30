/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

/**
 *
 * @author mac
 */
public class UtilityManager {

    /**
     *
     */
    public UtilityManager() {

    }

    /**
     *
     * @param passedMap
     * @return
     */
    public static LinkedHashMap<String, String> sortHashMapStringStringByValues(HashMap<String, String> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<String> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, String> sortedMap
                = new LinkedHashMap<>();

        Iterator<String> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            String val = valueIt.next().trim();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next().trim();
                String comp1 = passedMap.get(key);
                String comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    /**
     *
     * @param passedMap
     * @return
     */
    public static LinkedHashMap<Integer, String> SortHashMapIntStringByValues(HashMap<Integer, String> passedMap) {
        List<Integer> mapKeys = new ArrayList<>(passedMap.keySet());
        List<String> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<Integer, String> sortedMap = new LinkedHashMap<>();

        Iterator<String> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            String val = valueIt.next().trim();
            Iterator<Integer> keyIt = mapKeys.iterator();
            while (keyIt.hasNext()) {
                int key = keyIt.next();
                String comp1 = passedMap.get(key);
                String comp2 = val;
                if (comp1.trim().equals(comp2.trim())) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    /**
     *
     * @param passedMap
     * @return
     */
    public static ArrayList<Integer> SortHashMapIntStringReturnArrayListInt(HashMap<Integer, String> passedMap) {
        List<Integer> mapKeys = new ArrayList<>(passedMap.keySet());
        List<String> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        ArrayList<Integer> sortedList = new ArrayList<>();

        Iterator<String> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            String val = valueIt.next().trim();
            Iterator<Integer> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                int key = keyIt.next();
                String comp1 = passedMap.get(key);
                String comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedList.add(key);
                    break;
                }
            }
        }
        return sortedList;
    }

    /**
     *
     * @return
     * @throws ParseException
     */
    public static java.sql.Date CurrentDate() throws ParseException {
        Calendar currentdate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd");
        String Placeholder = formatter.format(currentdate.getTime());
        java.util.Date datenow = formatter.parse(Placeholder);
        java.sql.Date CurrentDate = new Date(datenow.getTime());
        return CurrentDate;
    }

    /**
     *
     * @return
     */
    public static java.sql.Time CurrentTime() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Time(today.getTime());
    }

    /**
     *
     * @param StringDate
     * @return
     */
    public static java.sql.Date getSqlDateFromString(String StringDate) {
        Date date;
        try {
            date = Date.valueOf(StringDate);
        } catch (Exception e) {
            date = null;
        }
        return date;
    }

    /**
     *
     * @param max
     * @param min
     * @return
     */
    public static int RandomNumber(int max, int min) {
        Random rand = new Random();
        int itID = rand.nextInt((max - min) + 1) + min;
        return itID;
    }

    /**
     *
     * @param LengthOfCode
     * @return
     */
    public static String GenerateAlphaNumericCode(int LengthOfCode) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < LengthOfCode; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return output;
    }

    /**
     *
     * @param price
     * @return
     */
    public static String PunctuatePrice(String price) {
        if (price.length() > 3) {
            price = price.substring(0, price.length() - 3) + "," + price.substring(price.length() - 3, price.length());
        }
        return price;
    }

    /**
     *
     * @param arraylist
     * @return
     */
    public static ArrayList<Integer> removeDuplicatesIntegerArrayList(ArrayList<Integer> arraylist) {
        ArrayList<Integer> result = new ArrayList<Integer>(new LinkedHashSet<Integer>(arraylist));
        return result;
    }

    /**
     *
     * @param arraylist
     * @return
     */
    public static ArrayList<Integer> SortAndReverseIntegerArrayList(ArrayList<Integer> arraylist) {
        Collections.sort(arraylist);
        Collections.reverse(arraylist);
        return arraylist;
    }

    /**
     *
     * @param text
     * @param character
     * @return
     */
    public static String getTextBeforeCharacter(String text, String character) {
        String res = text.substring(0, text.indexOf(character));
        return res;
    }

    /**
     *
     * @param value
     * @return
     * @throws ParseException
     */
    public static java.sql.Date GetExpiryDate(int value) throws ParseException {
        Calendar currentdate = Calendar.getInstance();
        currentdate.add(Calendar.DATE, value);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd");
        String Placeholder = formatter.format(currentdate.getTime());
        java.util.Date datenow = formatter.parse(Placeholder);
        java.sql.Date CurrentDate = new Date(datenow.getTime());
        return CurrentDate;
    }

    /**
     *
     * @param arraylist
     * @return
     */
    public static ArrayList<String> removeDuplicatesStringArrayList(ArrayList<String> arraylist) {
        ArrayList<String> result = new ArrayList<String>(new LinkedHashSet<String>(arraylist));
        return result;
    }

    /**
     *
     * @param arraylist
     * @return
     */
    public static String ConvertStringArrayListToString(ArrayList<String> arraylist) {
        String result = String.join(",", arraylist);//convert arraylist to string
        return result;
    }

    /**
     *
     * @param text
     * @param character
     * @return
     */
    public static String getTextAfterCharacter(String text, String character) {
        String res = text.substring(text.indexOf(character) + 1, text.length());
        return res;
    }

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     *
     * @param count
     * @return
     */
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    /**
     *
     * @param TimeString
     * @return
     */
    public static java.sql.Time getSqlTimeFromString(String TimeString) {
        Time time;
        try {
            time = Time.valueOf(TimeString);
        } catch (Exception e) {
            time = null;
        }
        return time;
    }

    /**
     *
     * @param TextToExtract
     * @param NumberOfCharacterToExtract
     * @return
     */
    public static String GetFirstCharacterText(String TextToExtract, int NumberOfCharacterToExtract) {
        return TextToExtract.length() < NumberOfCharacterToExtract ? TextToExtract : TextToExtract.substring(0, NumberOfCharacterToExtract);
    }

    /**
     *
     * @param TextToExtract
     * @param NumberOfCharacterToExtract
     * @return
     */
    public static String GetLastCharacterText(String TextToExtract, int NumberOfCharacterToExtract) {
        return TextToExtract.length() < NumberOfCharacterToExtract ? TextToExtract : TextToExtract.substring(TextToExtract.length() - NumberOfCharacterToExtract);
    }
    
    /**
     *
     * @param LengthOfCode
     * @return
     */
    public static String GenerateRandomNumber(int LengthOfCode) {
        String PIN = "";
        char[] chars = "1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < LengthOfCode; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        PIN = sb.toString();
        
        if (PIN.length() < LengthOfCode || PIN.startsWith("0")) {
            GenerateRandomNumber(LengthOfCode);
        }
        return PIN;
    }
}
