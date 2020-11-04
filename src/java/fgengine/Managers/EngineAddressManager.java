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
public class EngineAddressManager {

    /**
     *
     * @param UserID
     * @param AddressTypeID
     * @param CountryID
     * @param StateID
     * @param LgaID
     * @param TownID
     * @param BustopID
     * @param StreetID
     * @param PostalCode
     * @param CloseTo
     * @param AddressLine
     * @param FullAddress
     * @param MakeDefault
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateAddress(int UserID, int AddressTypeID, int CountryID, int StateID, int LgaID, int TownID, int BustopID, int StreetID, String PostalCode, String CloseTo, String AddressLine, String FullAddress, int MakeDefault, String Phone) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.AddressDetailsTable.UserID, UserID);
        tableData.put(Tables.AddressDetailsTable.AddressTypeID, AddressTypeID);
        tableData.put(Tables.AddressDetailsTable.CountryID, CountryID);
        tableData.put(Tables.AddressDetailsTable.StateID, StateID);
        tableData.put(Tables.AddressDetailsTable.LgaID, LgaID);
        tableData.put(Tables.AddressDetailsTable.TownID, TownID);
        tableData.put(Tables.AddressDetailsTable.BusStopID, BustopID);
        tableData.put(Tables.AddressDetailsTable.StreetID, StreetID);
        tableData.put(Tables.AddressDetailsTable.PostalCode, PostalCode);
        tableData.put(Tables.AddressDetailsTable.CloseTo, CloseTo);
        tableData.put(Tables.AddressDetailsTable.AddressLine, AddressLine);
        tableData.put(Tables.AddressDetailsTable.FullAddress, FullAddress);
        tableData.put(Tables.AddressDetailsTable.DefaultAddress, MakeDefault);
        tableData.put(Tables.AddressDetailsTable.Phone, Phone);
        int addressid = DBManager.insertTableDataReturnID(Tables.AddressDetailsTable.Table, tableData, "");
        DBManager.UpdateCurrentTime(Tables.AddressDetailsTable.Table, Tables.AddressDetailsTable.Time, "where " + Tables.AddressDetailsTable.ID + " = " + addressid);
        String result = DBManager.UpdateCurrentDate(Tables.AddressDetailsTable.Table, Tables.AddressDetailsTable.Date, "where " + Tables.AddressDetailsTable.ID + " = " + addressid);
        return result;
    }

    /**
     *
     * @param StateID
     * @param LgaID
     * @param TownID
     * @param BustopID
     * @param StreetName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int CreateStreet(int StateID, int LgaID, int TownID, int BustopID, String StreetName) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.AddressStreetTable.StateID, StateID);
        tableData.put(Tables.AddressStreetTable.LgaID, LgaID);
        tableData.put(Tables.AddressStreetTable.TownID, TownID);
        tableData.put(Tables.AddressStreetTable.BusStopID, BustopID);
        tableData.put(Tables.AddressStreetTable.Name, StreetName);
        int result = DBManager.insertTableDataReturnID(Tables.AddressStreetTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param StateID
     * @param LgaID
     * @param TownID
     * @param BusStopName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int CreateBusStop(int StateID, int LgaID, int TownID, String BusStopName) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.AddressBusStopTable.StateID, StateID);
        tableData.put(Tables.AddressBusStopTable.LgaID, LgaID);
        tableData.put(Tables.AddressBusStopTable.TownID, TownID);
        tableData.put(Tables.AddressBusStopTable.Name, BusStopName);
        int result = DBManager.insertTableDataReturnID(Tables.AddressBusStopTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param UserID
     * @param AddressTypeID
     * @param CountryID
     * @param StateID
     * @param LgaID
     * @param TownID
     * @param BusStopID
     * @param StreetID
     * @param PostalCode
     * @param ClostTo
     * @param AddressLine
     * @param MakeDefault
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputeAddress(int UserID, int AddressTypeID, int CountryID, int StateID, int LgaID, int TownID, int BusStopID, int StreetID, String PostalCode, String ClostTo, String AddressLine, int MakeDefault, String Phone) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> data = new HashMap<>();
        String statename = GetStateNameByID(StateID);
        String lganame = GetLGANameByID(LgaID);
        String townname = GetTownNameByID(TownID);
        String bstopname = GetBustopNameByID(BusStopID);
        String streetname = GetStreetNameByID(StreetID);
        String FullAddress = AddressLine + ", " + ClostTo + "," + streetname + ", " + bstopname + ", " + townname + ", " + lganame + ", " + statename + " State.";
        if (MakeDefault == 1) {
            int ExistingAddressDetailID = GetDefaultAddressDetailsIDByUserID("" + UserID);
            if (ExistingAddressDetailID != 0) {
                UpdateDefaultAddressDetailByID(ExistingAddressDetailID, 0);
            }
        }
        result = CreateAddress(UserID, AddressTypeID, CountryID, StateID, LgaID, TownID, BusStopID, StreetID, PostalCode, ClostTo, AddressLine, FullAddress, MakeDefault, Phone);
        return result;
    }

    /**
     *
     * @param StateID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetStateNameByID(int StateID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.GetString(Tables.AddressStateTable.Name, Tables.AddressStateTable.Table, "WHERE " + Tables.AddressStateTable.ID + " = " + StateID);
        return result;
    }

    /**
     *
     * @param StateID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetStateNameData(int StateID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> result = new HashMap<>();
        result = DBManager.GetTableData(Tables.AddressStateTable.Table, "WHERE " + Tables.AddressStateTable.ID + " = " + StateID);
        return result;
    }

    /**
     *
     * @param CountryID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetCountryNameByID(int CountryID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.GetString(Tables.AddressCountryTable.Name, Tables.AddressCountryTable.Table, "WHERE " + Tables.AddressCountryTable.ID + " = " + CountryID);
        return result;
    }

    /**
     *
     * @param CountryID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetCountryNameData(int CountryID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> result = new HashMap<>();
        result = DBManager.GetTableData(Tables.AddressCountryTable.Table, "WHERE " + Tables.AddressCountryTable.ID + " = " + CountryID);
        return result;
    }

    /**
     *
     * @param LgaID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetLGANameByID(int LgaID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.GetString(Tables.AddressLGATable.Name, Tables.AddressLGATable.Table, "WHERE " + Tables.AddressLGATable.ID + " = " + LgaID);
        return result;
    }

    /**
     *
     * @param LgaID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetLGANameData(int LgaID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> result = new HashMap<>();
        result = DBManager.GetTableData(Tables.AddressLGATable.Table, "WHERE " + Tables.AddressLGATable.ID + " = " + LgaID);
        return result;
    }

    /**
     *
     * @param TownID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetTownNameByID(int TownID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.GetString(Tables.AddressTownTable.Name, Tables.AddressTownTable.Table, "WHERE " + Tables.AddressTownTable.ID + " = " + TownID);
        return result;
    }

    /**
     *
     * @param TownID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetTownNameData(int TownID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> result = new HashMap<>();
        result = DBManager.GetTableData(Tables.AddressTownTable.Table, "WHERE " + Tables.AddressTownTable.ID + " = " + TownID);
        return result;
    }

    /**
     *
     * @param BusstopID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetBustopNameByID(int BusstopID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.GetString(Tables.AddressBusStopTable.Name, Tables.AddressBusStopTable.Table, "WHERE " + Tables.AddressBusStopTable.ID + " = " + BusstopID);
        return result;
    }

    /**
     *
     * @param BusstopID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetBustopNameData(int BusstopID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> result = new HashMap<>();
        result = DBManager.GetTableData(Tables.AddressBusStopTable.Table, "WHERE " + Tables.AddressBusStopTable.ID + " = " + BusstopID);
        return result;
    }

    /**
     *
     * @param StreetID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetStreetNameByID(int StreetID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.GetString(Tables.AddressStreetTable.Name, Tables.AddressStreetTable.Table, "WHERE " + Tables.AddressStreetTable.ID + " = " + StreetID);
        return result;
    }

    /**
     *
     * @param StreetID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetStreetNameData(int StreetID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> result = new HashMap<>();
        result = DBManager.GetTableData(Tables.AddressStreetTable.Table, "WHERE " + Tables.AddressStreetTable.ID + " = " + StreetID);
        return result;
    }

    /**
     *
     * @param AddressTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetAddressTypeNameByID(int AddressTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.GetString(Tables.AddressTypeTable.Name, Tables.AddressTypeTable.Table, "WHERE " + Tables.AddressTypeTable.ID + " = " + AddressTypeID);
        return result;
    }

    /**
     *
     * @param AddressTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetAddressTypeData(int AddressTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> result = new HashMap<>();
        result = DBManager.GetTableData(Tables.AddressTypeTable.Table, "WHERE " + Tables.AddressTypeTable.ID + " = " + AddressTypeID);
        return result;
    }
//----------------------------------------------------------------------- Pickup Station -------------------------------------------------//

    /**
     *
     * @param StationName
     * @param StateID
     * @param TownID
     * @param BusstopID
     * @param StreetID
     * @param Fees
     * @param OpeningHours
     * @param Phone
     * @param Email
     * @param AddressLine
     * @param CloseTo
     * @param SellerPickupPercentage
     * @param AdminPickupPercentage
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputePickUpStationAddress(String StationName, int StateID, int TownID, int BusstopID, int StreetID, int Fees, String OpeningHours, String Phone, String Email, String AddressLine, String CloseTo, int SellerPickupPercentage, int AdminPickupPercentage) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        String townname = GetTownNameByID(TownID);
        String bstopname = GetBustopNameByID(BusstopID);
        String streetname = GetStreetNameByID(StreetID);
        String FullAddress = AddressLine + ", " + streetname + ", " + bstopname + ", " + townname;

        int PickupRegionID = CreatePickUpRegion(StateID);
        int PickupTownID = CreatePickUpTown(PickupRegionID, TownID);
        int PickupStationID = CreatePickUpStation(PickupTownID, StationName, Phone, Email, OpeningHours, Fees, SellerPickupPercentage, AdminPickupPercentage);
        result = CreatePickUpStationAddress(PickupStationID, CloseTo, FullAddress);
        return result;
    }

    /**
     *
     * @param StateID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int CreatePickUpRegion(int StateID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int PickupRegionID = 0;
        HashMap<String, Object> data = new HashMap<>();
        data.put(Tables.PickUpRegionTable.StateID, StateID);
        PickupRegionID = DBManager.insertTableDataReturnID(Tables.PickUpRegionTable.Table, data, "");
        return PickupRegionID;
    }

    /**
     *
     * @param PickupRegionID
     * @param TownID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int CreatePickUpTown(int PickupRegionID, int TownID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int PickupTownID = 0;
        HashMap<String, Object> data = new HashMap<>();
        data.put(Tables.PickUpTownTable.PickupRegionID, PickupRegionID);
        data.put(Tables.PickUpTownTable.TownID, TownID);
        PickupTownID = DBManager.insertTableDataReturnID(Tables.PickUpTownTable.Table, data, "");
        return PickupTownID;
    }

    /**
     *
     * @param PickupTownID
     * @param StationName
     * @param Phone
     * @param Email
     * @param OpeningHours
     * @param Fees
     * @param SellerPickupPercentage
     * @param AdminPickupPercentage
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int CreatePickUpStation(int PickupTownID, String StationName, String Phone, String Email, String OpeningHours, double Fees, int SellerPickupPercentage, int AdminPickupPercentage) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> data = new HashMap<>();
        data.put(Tables.PickupStationTable.PickupTownID, PickupTownID);
        data.put(Tables.PickupStationTable.Name, StationName);
        data.put(Tables.PickupStationTable.Phone, Phone);
        data.put(Tables.PickupStationTable.Email, Email);
        data.put(Tables.PickupStationTable.OpeningHours, OpeningHours);
        data.put(Tables.PickupStationTable.Fees, Fees);
        data.put(Tables.PickupStationTable.SellerPickupPercentage, SellerPickupPercentage);
        data.put(Tables.PickupStationTable.AdminPickupPercentage, AdminPickupPercentage);
        int PickupStationID = DBManager.insertTableDataReturnID(Tables.PickupStationTable.Table, data, "");
        return PickupStationID;
    }

    /**
     *
     * @param PickupStationID
     * @param CloseTo
     * @param FullAddress
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreatePickUpStationAddress(int PickupStationID, String CloseTo, String FullAddress) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.PickupStationAddress.PickupStationID, PickupStationID);
        tableData.put(Tables.PickupStationAddress.CloseTo, CloseTo);
        tableData.put(Tables.PickupStationAddress.FullAddress, FullAddress);
        String result = DBManager.insertTableData(Tables.PickupStationAddress.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param PickupStationID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetPickUpStationData(int PickupStationID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = DBManager.GetTableData(Tables.PickupStationTable.Table, "where " + Tables.PickupStationTable.ID + " = " + PickupStationID);
        if (!Data.isEmpty()) {
//            int Addresstypeid = Integer.parseInt(Data.get(Tables.AddressDetailsTable.AddressTypeID));
//            String addresstypename = GetAddressTypeNameByID(Addresstypeid);
//            Data.put("addresstypename", addresstypename);
//            int UserID = Integer.parseInt(Data.get(Tables.AddressDetailsTable.UserID));
//            Data.put("addressusername", EngineUserManager.GetUserName(UserID));
        }
        return Data;
    }

    /**
     *
     * @param UserID
     * @param AddressDetailID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String SetDefaultAddress(String UserID, int AddressDetailID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int ExistingAddressDetailID = GetDefaultAddressDetailsIDByUserID(UserID);
        if (ExistingAddressDetailID != 0) {
            result = UpdateDefaultAddressDetailByID(ExistingAddressDetailID, 0);
            if (result.equals("success")) {
                result = UpdateDefaultAddressDetailByID(AddressDetailID, 1);
            } else {
                result = "Updaing Existing Default Address could not be completed.";
            }

        } else {
            result = UpdateDefaultAddressDetailByID(AddressDetailID, 1);
        }
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
    public static int GetDefaultAddressDetailsIDByUserID(String UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int AddressDetailsID = DBManager.GetInt(Tables.AddressDetailsTable.ID, Tables.AddressDetailsTable.Table, "where " + Tables.AddressDetailsTable.UserID + " = '" + UserID + "' AND " + Tables.AddressDetailsTable.DefaultAddress + " = " + 1);
        return AddressDetailsID;
    }

    /**
     *
     * @param AddressDetailID
     * @param Status
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateDefaultAddressDetailByID(int AddressDetailID, int Status) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.UpdateIntData(Tables.AddressDetailsTable.DefaultAddress, Status, Tables.AddressDetailsTable.Table, "where " + Tables.AddressDetailsTable.ID + " = " + AddressDetailID);
        return result;
    }

    /**
     *
     * @param ID
     * @param NewPercentage
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateAdminPickupPercentage(int ID, int NewPercentage) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.UpdateIntData(Tables.PickupStationTable.AdminPickupPercentage, NewPercentage, Tables.PickupStationTable.Table, "where " + Tables.PickupStationTable.ID + " = " + ID);
        return result;
    }

    /**
     *
     * @param ID
     * @param NewPercentage
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateSellerPickupPercentage(int ID, int NewPercentage) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.UpdateIntData(Tables.PickupStationTable.SellerPickupPercentage, NewPercentage, Tables.PickupStationTable.Table, "where " + Tables.PickupStationTable.ID + " = " + ID);
        return result;
    }

    /**
     *
     * @param PickupStationID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static double GetPickupStationFeesByID(int PickupStationID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double AddressDetailsID = DBManager.GetDouble(Tables.PickupStationTable.Fees, Tables.PickupStationTable.Table, "where " + Tables.PickupStationTable.ID + " = " + PickupStationID);
        return AddressDetailsID;
    }

    /**
     *
     * @param PickupStationID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetSellerPickupPercentage(int PickupStationID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int AddressDetailsID = DBManager.GetInt(Tables.PickupStationTable.SellerPickupPercentage, Tables.PickupStationTable.Table, "where " + Tables.PickupStationTable.ID + " = " + PickupStationID);
        return AddressDetailsID;
    }

    /**
     *
     * @param PickupStationID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetAdminPickupPercentage(int PickupStationID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int AddressDetailsID = DBManager.GetInt(Tables.PickupStationTable.AdminPickupPercentage, Tables.PickupStationTable.Table, "where " + Tables.PickupStationTable.ID + " = " + PickupStationID);
        return AddressDetailsID;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetAddressIDs(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList();
        if (UserID == 1) {
            IDs = DBManager.GetIntArrayListDescending(Tables.AddressDetailsTable.ID, Tables.AddressDetailsTable.Table, "Order by " + Tables.AddressDetailsTable.ID);
        } else {
            IDs = DBManager.GetIntArrayListDescending(Tables.AddressDetailsTable.ID, Tables.AddressDetailsTable.Table, "where " + Tables.AddressDetailsTable.UserID + " = " + UserID);
        }
        return IDs;
    }

    /**
     *
     * @param AddressID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetAddressData(int AddressID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Data = DBManager.GetTableData(Tables.AddressDetailsTable.Table, "where " + Tables.AddressDetailsTable.ID + " = " + AddressID);
        if (!Data.isEmpty()) {
            int Addresstypeid = Integer.parseInt(Data.get(Tables.AddressDetailsTable.AddressTypeID));
            String addresstypename = GetAddressTypeNameByID(Addresstypeid);
            Data.put("addresstypename", addresstypename);
            int UserID = Integer.parseInt(Data.get(Tables.AddressDetailsTable.UserID));
            Data.put("addressusername", EngineUserManager.GetUserName(UserID));

            String dt = Data.get(Tables.AddressDetailsTable.Date);
            String date = DateManager.readDate(dt);
            Data.put(Tables.AddressDetailsTable.Date, date);
            String tm = Data.get(Tables.AddressDetailsTable.Time);
            String time = DateManager.readTime(tm);
            Data.put(Tables.AddressDetailsTable.Time, time);
            Data.put("statename", GetStateNameByID(Integer.parseInt(Data.get(Tables.AddressDetailsTable.StateID))));
            Data.put("lganame", GetLGANameByID(Integer.parseInt(Data.get(Tables.AddressDetailsTable.LgaID))));
            Data.put("townname", GetTownNameByID(Integer.parseInt(Data.get(Tables.AddressDetailsTable.TownID))));
            Data.put("busname", GetBustopNameByID(Integer.parseInt(Data.get(Tables.AddressDetailsTable.BusStopID))));
            Data.put("streetname", GetStreetNameByID(Integer.parseInt(Data.get(Tables.AddressDetailsTable.StreetID))));

        }
        return Data;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetAddressDataByUserID(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int ID = GetDefaultAddressDetailsIDByUserID("" + UserID);
        HashMap<String, String> Data = GetAddressData(ID);
        return Data;
    }

    public static ArrayList<Integer> GetAddressTypeIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.AddressTypeTable.ID, Tables.AddressTypeTable.Table, "Order by " + Tables.AddressTypeTable.Name);
        return IDs;
    }

    public static ArrayList<Integer> GetStateIDs(int CountryID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.AddressStateTable.ID, Tables.AddressStateTable.Table, "where " + Tables.AddressStateTable.CountryID + " = " + CountryID + " Order by " + Tables.AddressStateTable.Name);
        return IDs;
    }

    public static ArrayList<Integer> GetLGAIDs(int StateID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.AddressLGATable.ID, Tables.AddressLGATable.Table, "where " + Tables.AddressLGATable.StateID + " = " + StateID + " Order by " + Tables.AddressLGATable.Name);
        return IDs;
    }

    public static ArrayList<Integer> GetTownIDs(int LGAID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.AddressTownTable.ID, Tables.AddressTownTable.Table, "where " + Tables.AddressTownTable.LgaID + " = " + LGAID + " Order by " + Tables.AddressTownTable.Name);
        return IDs;
    }

    public static ArrayList<Integer> GetBusStopIDs(int TownID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.AddressBusStopTable.ID, Tables.AddressBusStopTable.Table, "where " + Tables.AddressBusStopTable.TownID + " = " + TownID + " Order by " + Tables.AddressBusStopTable.Name);
        return IDs;
    }

    public static ArrayList<Integer> GetStreetIDs(int BusStopID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.AddressStreetTable.ID, Tables.AddressStreetTable.Table, "where " + Tables.AddressStreetTable.BusStopID + " = " + BusStopID + " Order by " + Tables.AddressStreetTable.Name);
        return IDs;
    }

    public static ArrayList<Integer> GetUserAddressIDs(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        if (UserID == 1) {
            IDs = DBManager.GetIntArrayListDescending(Tables.AddressDetailsTable.ID, Tables.AddressDetailsTable.Table, "Order by " + Tables.AddressDetailsTable.ID);
        } else {
            IDs = DBManager.GetIntArrayListDescending(Tables.AddressDetailsTable.ID, Tables.AddressDetailsTable.Table, "where " + Tables.AddressDetailsTable.UserID + " = " + UserID + " Order by " + Tables.AddressDetailsTable.ID);
        }
        return IDs;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException d) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param AddressDetailID
     * @param Status
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteAddressDetailByID(int AddressDetailID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.DeleteObject(Tables.AddressDetailsTable.Table, "where " + Tables.AddressDetailsTable.ID + " = " + AddressDetailID);
        return result;
    }

    /**
     *
     * @param AddressDetailID
     * @param Status
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetUserIDByAddressDetailID(int AddressDetailID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.AddressDetailsTable.UserID, Tables.AddressDetailsTable.Table, "where " + Tables.AddressDetailsTable.ID + " = " + AddressDetailID);
        return result;
    }

    /**
     *
     * @param AddressTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteAddressType(int AddressTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        result = DBManager.DeleteObject(Tables.AddressTypeTable.Table, "where " + Tables.AddressTypeTable.ID + " = " + AddressTypeID);
        return result;
    }

    /**
     *
     * @param Name
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateAddressType(String Name) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.AddressTypeTable.Name, Name);
        String result = DBManager.insertTableData(Tables.AddressTypeTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param Name
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String EditAddressType(int ID, String Name) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        if (!Name.equals("")) {
            result = DBManager.UpdateStringData(Tables.AddressTypeTable.Table, Tables.AddressTypeTable.Name, Name, "where " + Tables.AddressTypeTable.ID + " = " + ID);
        }
        return result;
    }

}
