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
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONObject;

/**
 *
 * @author mac
 */
public class EngineDiscountManager {

    /**
     *
     * @param Name
     * @param Description
     * @param Code
     * @param DiscountCodeTypeID
     * @param DiscountCodeObjectID
     * @param DiscountDeductionTypeID
     * @param DecductionValue
     * @param StartingDate
     * @param ExpiryDate
     * @param TotalPerCustomer
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int CreateDiscountCode(String Name, String Description, String Code, int DiscountCodeTypeID, int DiscountCodeObjectID, int DiscountDeductionTypeID, int DecductionValue, Date StartingDate, Date ExpiryDate, int TotalPerCustomer, int SplitDeductionValue) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.DiscountCodesTable.Name, Name);
        tableData.put(Tables.DiscountCodesTable.Description, Name);
        tableData.put(Tables.DiscountCodesTable.Code, Code);
        tableData.put(Tables.DiscountCodesTable.DiscountCodeTypeID, DiscountCodeTypeID);
        tableData.put(Tables.DiscountCodesTable.DiscountCodeObjectID, DiscountCodeObjectID);
        tableData.put(Tables.DiscountCodesTable.DiscountDeductionTypeID, DiscountDeductionTypeID);
        tableData.put(Tables.DiscountCodesTable.DeductionValue, DecductionValue);
        tableData.put(Tables.DiscountCodesTable.ExpiryDate, ExpiryDate);
        tableData.put(Tables.DiscountCodesTable.StartDate, StartingDate);
        tableData.put(Tables.DiscountCodesTable.SplitDeductionValue, SplitDeductionValue);
        int discountid = DBManager.insertTableDataReturnID(Tables.DiscountCodesTable.Table, tableData, "");
        return discountid;
    }

    /**
     *
     * @param Name
     * @param Description
     * @param DiscountCodeTypeID
     * @param DiscountCodeObjectID
     * @param DiscountCodeDeductionTypeID
     * @param DecductionValue
     * @param Startingdate
     * @param Expirydate
     * @param CustomerUserID
     * @param TotalPerCustomer
     * @param SplitDeductionValue
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputeDiscountCode(String Name, String Description, int DiscountCodeTypeID, int DiscountCodeObjectID,
            int DiscountCodeDeductionTypeID, int DecductionValue, String Startingdate, String Expirydate, int CustomerUserID, int TotalPerCustomer, int SplitDeductionValue) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        String Code = GenerateDiscountCode(DiscountCodeTypeID, DiscountCodeObjectID, DiscountCodeDeductionTypeID);
        Date ExpiryDate = UtilityManager.getSqlDateFromString(Expirydate);
        Date StartingDate = UtilityManager.getSqlDateFromString(Startingdate);
        int DiscountCodeID = CreateDiscountCode(Name, Description, Code, DiscountCodeTypeID, DiscountCodeObjectID, DiscountCodeDeductionTypeID, DecductionValue, StartingDate, ExpiryDate, TotalPerCustomer, SplitDeductionValue);
        if (DiscountCodeID != 0) {
            if (DiscountCodeTypeID == 1) {//Single Customer
                result = CreateCustomerDiscountCode(DiscountCodeID, CustomerUserID, TotalPerCustomer);
                if (result.equals("success")) {
                    result = UpdateDiscountCodeTotalAvailable(DiscountCodeID, TotalPerCustomer);
                    if (result.equals("success")) {
                        result = UpdateDiscountCodeTotalCreated(DiscountCodeID, TotalPerCustomer);
                        if (!result.equals("success")) {
                            result = "Could not update the total created Discount Code";
                        }
                    } else {
                        result = "Could not update the total availalbe Discount Code";
                    }
                } else {
                    result = "Could not create the single customer Discount Code";
                }

            } else if (DiscountCodeTypeID == 2) {
                ArrayList<Integer> AllCustomerIDs = EngineUserManager.GetAllCustomerUsers();
                if (!AllCustomerIDs.isEmpty()) {
                    for (int customerid : AllCustomerIDs) {
                        result = CreateCustomerDiscountCode(DiscountCodeID, customerid, TotalPerCustomer);
                    }
                }
                int TotalAvaialble = TotalPerCustomer * AllCustomerIDs.size();
                if (result.equals("success")) {
                    result = UpdateDiscountCodeTotalAvailable(DiscountCodeID, TotalAvaialble);
                    if (result.equals("success")) {
                        result = UpdateDiscountCodeTotalCreated(DiscountCodeID, TotalAvaialble);
                    }
                }
            }
        }

        return result;
    }

    /**
     *
     * @param DiscountCodeTypeID
     * @param DiscountCodeObjectID
     * @param DiscountCodeDeductionTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GenerateDiscountCode(int DiscountCodeTypeID, int DiscountCodeObjectID, int DiscountCodeDeductionTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        String CodeSet = UtilityManager.randomAlphaNumeric(2);
        String DCodeTypeName = GetDiscountCodeTypeNameByID(DiscountCodeTypeID);
        String firstDCodeTypeName = UtilityManager.GetFirstCharacterText(DCodeTypeName, 1);
        String CodeSet1 = UtilityManager.randomAlphaNumeric(3);
        String DCodeObjectName = GetDiscountCodeObjectNameByID(DiscountCodeObjectID);
        String firstDCodeObjectName = UtilityManager.GetFirstCharacterText(DCodeObjectName, 1);
        String CodeSet2 = UtilityManager.randomAlphaNumeric(2);
        String DCodeDeductionTypeName = GetDiscountCodeDeductionTypeNameByID(DiscountCodeDeductionTypeID);
        String firstDCodeDeductionTypeName = UtilityManager.GetFirstCharacterText(DCodeDeductionTypeName, 1);
        String CodeSet3 = UtilityManager.randomAlphaNumeric(2);
        result = CodeSet + firstDCodeTypeName + CodeSet3 + firstDCodeDeductionTypeName + CodeSet2 + firstDCodeObjectName + CodeSet1;
        return result;
    }

    /**
     *
     * @param DiscountCodeTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetDiscountCodeTypeNameByID(int DiscountCodeTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.DiscountCodeTypesTable.Name, Tables.DiscountCodeTypesTable.Table, "where " + Tables.DiscountCodeTypesTable.ID + " = " + DiscountCodeTypeID);
        return result;
    }

    /**
     *
     * @param DiscountCodeObjectID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetDiscountCodeObjectNameByID(int DiscountCodeObjectID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.DiscountCodeObjectTable.Name, Tables.DiscountCodeObjectTable.Table, "where " + Tables.DiscountCodeObjectTable.ID + " = " + DiscountCodeObjectID);
        return result;
    }

    /**
     *
     * @param DiscountCodeDeductionTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetDiscountCodeDeductionTypeNameByID(int DiscountCodeDeductionTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.DiscountCodeDeductionType.Name, Tables.DiscountCodeDeductionType.Table, "where " + Tables.DiscountCodeDeductionType.ID + " = " + DiscountCodeDeductionTypeID);
        return result;
    }

    /**
     *
     * @param DiscountCodeID
     * @param CustomerUserID
     * @param TotalAvailable
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateCustomerDiscountCode(int DiscountCodeID, int CustomerUserID, int TotalAvailable) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.CustomerDiscountCodesTable.CustomerUserID, CustomerUserID);
        tableData.put(Tables.CustomerDiscountCodesTable.DiscountCodeID, DiscountCodeID);
        tableData.put(Tables.CustomerDiscountCodesTable.TotalAvailable, TotalAvailable);
        String result = DBManager.insertTableData(Tables.CustomerDiscountCodesTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param DiscountCodeID
     * @param TotalAvailable
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateDiscountCodeTotalAvailable(int DiscountCodeID, int TotalAvailable) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.UpdateIntData(Tables.DiscountCodesTable.TotalAvailable, TotalAvailable, Tables.DiscountCodesTable.Table, "where " + Tables.DiscountCodesTable.ID + " = " + DiscountCodeID);
        return result;
    }

    /**
     *
     * @param DiscountCodeID
     * @param TotalAvailable
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateCustomerTotalAvailable(int DiscountCodeID, int TotalAvailable, String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.UpdateIntData(Tables.CustomerDiscountCodesTable.TotalAvailable, TotalAvailable, Tables.CustomerDiscountCodesTable.Table, "where " + Tables.CustomerDiscountCodesTable.DiscountCodeID + " = " + DiscountCodeID + " AND " + Tables.CustomerDiscountCodesTable.CustomerUserID + " = '" + UserID + "'");
        return result;
    }

    /**
     *
     * @param CustomerUserID
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetCustomerTotalAvailable(String CustomerUserID, int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.CustomerDiscountCodesTable.TotalAvailable, Tables.CustomerDiscountCodesTable.Table, "where " + Tables.CustomerDiscountCodesTable.CustomerUserID + " = '" + CustomerUserID + "' and " + Tables.CustomerDiscountCodesTable.DiscountCodeID + " = " + DiscountCodeID);
        return result;
    }

    /**
     *
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetDiscountCodeTotalAvailable(int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.DiscountCodesTable.TotalAvailable, Tables.DiscountCodesTable.Table, "where " + Tables.DiscountCodesTable.ID + " = " + DiscountCodeID);
        return result;
    }

    /**
     *
     * @param DiscountCodeID
     * @param TotalCreated
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateDiscountCodeTotalCreated(int DiscountCodeID, int TotalCreated) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.UpdateIntData(Tables.DiscountCodesTable.TotalCreated, TotalCreated, Tables.DiscountCodesTable.Table, "where " + Tables.DiscountCodesTable.ID + " = " + DiscountCodeID);
        return result;
    }

    /**
     *
     * @param UserID
     * @param DiscountCode
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static int GetDiscountCodeIDByCode(String UserID, String DiscountCode) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        LocalDate CurrentDate = LocalDate.now();
        int DiscountCodeID = DBManager.GetInt(Tables.DiscountCodesTable.ID, Tables.DiscountCodesTable.Table, "where " + Tables.DiscountCodesTable.Code + " = '" + DiscountCode + "'");
        String ExpiryDate = GetDiscountCodeExpiryDateByDiscounCodeID(DiscountCodeID);
        int DiscountCodeStatus = GetDiscountCodeStatusByDiscounCodeID(DiscountCodeID);
        String CustomerDiscountCodeStatus = GetCustomerDiscountCodeStatus(UserID, DiscountCodeID);
        if (DiscountCodeStatus == 1) {//active
            if (CustomerDiscountCodeStatus.equals("Unused")) {
                LocalDate ConvertedExpiryDate = LocalDate.parse(ExpiryDate);
                if (CurrentDate.isAfter(ConvertedExpiryDate)) {
                    UpdateDiscountCodeStatus(DiscountCodeID, 2);
                    DiscountCodeID = 0;
                }
            } else {
                DiscountCodeID = 0;
            }
        } else {
            DiscountCodeID = 0;
        }

        return DiscountCodeID;
    }

    /**
     *
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetDiscountCodeStatusByDiscounCodeID(int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.DiscountCodesTable.Active, Tables.DiscountCodesTable.Table, "where " + Tables.DiscountCodesTable.ID + " = " + DiscountCodeID);
        return result;
    }

    /**
     *
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetDiscountCodeByDiscounCodeID(int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.DiscountCodesTable.Code, Tables.DiscountCodesTable.Table, "where " + Tables.DiscountCodesTable.ID + " = " + DiscountCodeID);
        return result;
    }

    /**
     *
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetDiscountCodeDeductionTypeByDiscounCodeID(int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.DiscountCodesTable.DiscountDeductionTypeID, Tables.DiscountCodesTable.Table, "where " + Tables.DiscountCodesTable.ID + " = " + DiscountCodeID);
        return result;
    }

    /**
     *
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetDiscountCodeExpiryDateByDiscounCodeID(int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.DiscountCodesTable.ExpiryDate, Tables.DiscountCodesTable.Table, "where " + Tables.DiscountCodesTable.ID + " = " + DiscountCodeID);
        return result;
    }

    /**
     *
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static double GetDiscountCodeDeductionValueByDiscounCodeID(int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double result = DBManager.GetInt(Tables.DiscountCodesTable.DeductionValue, Tables.DiscountCodesTable.Table, "where " + Tables.DiscountCodesTable.ID + " = " + DiscountCodeID);
        return result;
    }

    /**
     *
     * @param PecertageValue
     * @param TotalAmount
     * @return
     */
    public static double ComputePercentageAmount(double PecertageValue, double TotalAmount) {
        double amt;
        double newamt;
        newamt = (PecertageValue * TotalAmount);
        amt = (newamt / 100);
        return amt;
    }

    /**
     *
     * @param UserID
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateDiscountCodeUsage(String UserID, int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        //for discount
        int DiscountCodeTotalAvailable = GetDiscountCodeTotalAvailable(DiscountCodeID);
        int NewDiscountCodeTotalAvailable = DiscountCodeTotalAvailable - 1;
        if (NewDiscountCodeTotalAvailable != 0) {
            result = UpdateDiscountCodeTotalAvailable(DiscountCodeID, NewDiscountCodeTotalAvailable);
        } else {
            result = UpdateDiscountCodeTotalAvailable(DiscountCodeID, NewDiscountCodeTotalAvailable);
            UpdateDiscountCodeStatus(DiscountCodeID, 0);
        }
        //for customer
        int CustomerTotalAvailable = GetCustomerTotalAvailable(UserID, DiscountCodeID);
        int NewCustomerTotalAvail = CustomerTotalAvailable - 1;
        if (NewCustomerTotalAvail != 0) {
            result = UpdateCustomerTotalAvailable(DiscountCodeID, NewCustomerTotalAvail, UserID);
        } else {
            result = UpdateCustomerTotalAvailable(DiscountCodeID, NewCustomerTotalAvail, UserID);
            UpdateCustomerDiscountCodeStatus(UserID, DiscountCodeID);//to unused
        }

        return result;
    }

    /**
     *
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetDiscountCodeTypeIDByDiscounCodeID(int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.DiscountCodesTable.DiscountCodeTypeID, Tables.DiscountCodesTable.Table, "where " + Tables.DiscountCodesTable.ID + " = " + DiscountCodeID);
        return result;
    }

    /**
     *
     * @param DiscountCodeID
     * @param NewValue
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateDiscountCodeStatus(int DiscountCodeID, int NewValue) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateIntData(Tables.DiscountCodesTable.Active, NewValue, Tables.DiscountCodesTable.Table, "where " + Tables.DiscountCodesTable.ID + " = " + DiscountCodeID);
        return result;
    }

    /**
     *
     * @param UserID
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateCustomerDiscountCodeStatus(String UserID, int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int CustomerDiscountCodeID = GetCustomerDiscountCodeIDByCustomerUserIDAndDiscountCodeID(UserID, DiscountCodeID);
        String result = DBManager.UpdateStringData(Tables.CustomerDiscountCodesTable.Table, Tables.CustomerDiscountCodesTable.Status, "Used", "where " + Tables.CustomerDiscountCodesTable.ID + " =  " + CustomerDiscountCodeID);
        return result;
    }

    /**
     *
     * @param UserID
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetCustomerDiscountCodeStatus(String UserID, int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int CustomerDiscountCodeID = GetCustomerDiscountCodeIDByCustomerUserIDAndDiscountCodeID(UserID, DiscountCodeID);
        String result = DBManager.GetString(Tables.CustomerDiscountCodesTable.Status, Tables.CustomerDiscountCodesTable.Table, "where " + Tables.CustomerDiscountCodesTable.ID + " =  " + CustomerDiscountCodeID);
        return result;
    }

    /**
     *
     * @param CustomerUserID
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetCustomerDiscountCodeIDByCustomerUserIDAndDiscountCodeID(String CustomerUserID, int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.CustomerDiscountCodesTable.ID, Tables.CustomerDiscountCodesTable.Table, "where " + Tables.CustomerDiscountCodesTable.CustomerUserID + " = '" + CustomerUserID + "' and " + Tables.CustomerDiscountCodesTable.DiscountCodeID + " = " + DiscountCodeID);
        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetDiscountTypeIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.DiscountCodeTypesTable.ID, Tables.DiscountCodeTypesTable.Table, "");
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param DiscountTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetDiscountTypeData(int DiscountTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.DiscountCodeTypesTable.Table, "where " + Tables.DiscountCodeTypesTable.ID + " = " + DiscountTypeID);
        return Details;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetDeductionTypeIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.DiscountCodeDeductionType.ID, Tables.DiscountCodeDeductionType.Table, "");
        return IDs;
    }

    /**
     *
     * @param DeductionTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetDeductionTypeData(int DeductionTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.DiscountCodeDeductionType.Table, "where " + Tables.DiscountCodeDeductionType.ID + " = " + DeductionTypeID);
        return Details;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetDiscountObjectIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.DiscountCodeObjectTable.ID, Tables.DiscountCodeObjectTable.Table, "");
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param DiscountObjectID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetDiscountObjectData(int DiscountObjectID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.DiscountCodeObjectTable.Table, "where " + Tables.DiscountCodeObjectTable.ID + " = " + DiscountObjectID);
        return Details;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetDiscountCodeIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.DiscountCodesTable.ID, Tables.DiscountCodesTable.Table, "");
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetDiscountCodeData(int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.DiscountCodesTable.Table, "where " + Tables.DiscountCodesTable.ID + " = " + DiscountCodeID);
        if (!Details.isEmpty()) {
            try {
                int DiscountCodeTypeID = Integer.parseInt(Details.get(Tables.DiscountCodesTable.DiscountCodeTypeID));
                String DiscountCodeTypeName = GetDiscountCodeTypeNameByID(DiscountCodeTypeID);
                Details.put("DiscountCodeTypeName", DiscountCodeTypeName);

                int DiscountDeductionTypeID = Integer.parseInt(Details.get(Tables.DiscountCodesTable.DiscountDeductionTypeID));
                String DiscountDeductionTypeName = GetDiscountCodeDeductionTypeNameByID(DiscountDeductionTypeID);
                Details.put("DiscountDeductionTypeName", DiscountDeductionTypeName);

                int DiscountObjectID = Integer.parseInt(Details.get(Tables.DiscountCodesTable.DiscountCodeObjectID));
                String DiscountObjectName = GetDiscountCodeObjectNameByID(DiscountObjectID);
                Details.put("DiscountObjectName", DiscountObjectName);

                if (DiscountCodeTypeID == 1) {
                    int CustomerUserID = GetCustomerUserIDByDiscountCodeID(DiscountCodeID);
                    String CustomerUserName = EngineUserManager.GetUserName(CustomerUserID);
                    Details.put("DiscountCustomerUserName", CustomerUserName);
                }

                String sdat = Details.get(Tables.DiscountCodesTable.StartDate);
                String sDate = DateManager.readDate(sdat);
                Details.put(Tables.DiscountCodesTable.StartDate, sDate);

                String edat = Details.get(Tables.DiscountCodesTable.ExpiryDate);
                String eDate = DateManager.readDate(edat);
                Details.put(Tables.DiscountCodesTable.ExpiryDate, eDate);

//                LocalDate CurrentDate = LocalDate.now();
//                LocalDate ConvertedExpiryDate = LocalDate.parse(edat);
//                if (CurrentDate.isAfter(ConvertedExpiryDate)) {
//                    UpdateDiscountCodeStatus(DiscountCodeID, 2);
//                }
                String Status = "";
                int active = Integer.parseInt(Details.get(Tables.DiscountCodesTable.Active));
                switch (active) {
                    case 1:
                        Status = "Active";
                        break;
                    case 0:
                        Status = "InActive";
                        break;
                    case 2:
                        Status = "Expired";
                        break;
                    default:
                        break;
                }
                Details.put("Status", Status);
            } catch (Exception ex) {
            }
        }
        return Details;
    }

    /**
     *
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetCustomerUserIDByDiscountCodeID(int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.CustomerDiscountCodesTable.CustomerUserID, Tables.CustomerDiscountCodesTable.Table, "where " + Tables.CustomerDiscountCodesTable.DiscountCodeID + " = " + DiscountCodeID);
        return result;
    }

    /**
     *
     * @param CustomerUserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetDiscountCodeIDByCustomerUserID(int CustomerUserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.CustomerDiscountCodesTable.DiscountCodeID, Tables.CustomerDiscountCodesTable.Table, "where " + Tables.CustomerDiscountCodesTable.DiscountCodeID + " = " + CustomerUserID);
        return result;
    }

    /**
     *
     * @param Option
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ProcessDiscountCode(String Option, int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        if (Option.equals("Deleted")) {
            ArrayList<Integer> CustomerDiscountCodeIDs = GetCustomerDiscountCodeIDsByDiscountCodeID(DiscountCodeID);
            if (!CustomerDiscountCodeIDs.isEmpty()) {
                for (int ID : CustomerDiscountCodeIDs) {
                    result = DBManager.DeleteObject(Tables.CustomerDiscountCodesTable.Table, "where " + Tables.CustomerDiscountCodesTable.ID + " = " + ID);
                }
            }
            result = DBManager.DeleteObject(Tables.DiscountCodesTable.Table, "where " + Tables.DiscountCodesTable.ID + " = " + DiscountCodeID);
        } else if (Option.equals("Stopped")) {
            result = UpdateDiscountCodeStatus(DiscountCodeID, 0);
        }
        return result;
    }

    /**
     *
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetCustomerDiscountCodeIDsByDiscountCodeID(int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.CustomerDiscountCodesTable.ID, Tables.CustomerDiscountCodesTable.Table, "where " + Tables.CustomerDiscountCodesTable.DiscountCodeID + " = " + DiscountCodeID);
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetCustomerDiscountCodeIDsByCustomerUserID(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.CustomerDiscountCodesTable.ID, Tables.CustomerDiscountCodesTable.Table, "where " + Tables.CustomerDiscountCodesTable.CustomerUserID + " = " + UserID);
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param Name
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateDiscountCodeType(String Name) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.DiscountCodeTypesTable.Name, Name);
        result = DBManager.insertTableData(Tables.DiscountCodeTypesTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param Name
     * @param Description
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateDiscountCodeDeductionType(String Name, String Description) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.DiscountCodeDeductionType.Name, Name);
        tableData.put(Tables.DiscountCodeDeductionType.Description, Name);
        result = DBManager.insertTableData(Tables.DiscountCodeDeductionType.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param DiscountCodeTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteDiscountCodeType(int DiscountCodeTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.DiscountCodeTypesTable.Table, "where " + Tables.DiscountCodeTypesTable.ID + " = " + DiscountCodeTypeID);
        return result;
    }

    /**
     *
     * @param DeductionTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteDeductionType(int DeductionTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.DiscountCodeDeductionType.Table, "where " + Tables.DiscountCodeDeductionType.ID + " = " + DeductionTypeID);
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
    public static ArrayList<Integer> GetCustomerDiscountCodeIDs(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.CustomerDiscountCodesTable.ID, Tables.CustomerDiscountCodesTable.Table, "where " + Tables.CustomerDiscountCodesTable.CustomerUserID + " = " + UserID);
        return IDs;
    }

    /**
     *
     * @param CustomerDiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetCustomerDiscountCodeData(int CustomerDiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = DBManager.GetTableData(Tables.CustomerDiscountCodesTable.Table, "where " + Tables.CustomerDiscountCodesTable.ID + " = " + CustomerDiscountCodeID);
        if (!Data.isEmpty()) {
            try {
                String DCodeID = Data.get(Tables.CustomerDiscountCodesTable.DiscountCodeID);
                int DiscountCodeID = Integer.parseInt(DCodeID);
                HashMap<String, String> Details = GetDiscountCodeData(DiscountCodeID);
                JSONObject discountcodeData = new JSONObject();
                discountcodeData.put("DiscountCodeData", Details);
                Data.putAll(discountcodeData);
            } catch (Exception ex) {
            }
        }
        return Data;
    }

    /**
     *
     * @param DiscountCodeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetDiscountSplitDeductionValue(int DiscountCodeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.DiscountCodesTable.SplitDeductionValue, Tables.DiscountCodesTable.Table, "where " + Tables.DiscountCodesTable.ID + " = " + DiscountCodeID);
        return result;
    }
}
