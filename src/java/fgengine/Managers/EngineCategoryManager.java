/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import fgengine.Tables.Tables;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONObject;

/**
 *
 * @author mac
 */
public class EngineCategoryManager {

    /**
     *
     * @param Name
     * @param Description
     * @param ParentID
     * @param IsRootCat
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static int CreateCategory(String Name, String Description, int ParentID, int IsRootCat) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.CategoryTable.Name, Name);
        tableData.put(Tables.CategoryTable.Description, Description);
        tableData.put(Tables.CategoryTable.ParentID, ParentID);
        tableData.put(Tables.CategoryTable.IsRootCategory, IsRootCat);
        int catid = DBManager.insertTableDataReturnID(Tables.CategoryTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.CategoryTable.Table, Tables.CategoryTable.Date, "where " + Tables.CategoryTable.ID + " = " + catid);
        DBManager.UpdateCurrentTime(Tables.CategoryTable.Table, Tables.CategoryTable.Time, "where " + Tables.CategoryTable.ID + " = " + catid);
        return catid;
    }

    /**
     *
     * @param CategoryID
     * @param RootPropertyID
     * @param PropertyID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String CreateCategoryProperty(int CategoryID, int RootPropertyID, int PropertyID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.CategoryPropertiesTable.CategoryID, CategoryID);
        tableData.put(Tables.CategoryPropertiesTable.RootPropertyID, RootPropertyID);
        tableData.put(Tables.CategoryPropertiesTable.PropertyID, PropertyID);
        String result = DBManager.insertTableData(Tables.CategoryPropertiesTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param Name
     * @param Desc
     * @param ParentID
     * @param IsRootID
     * @param Tags
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static int ComputeCategory(String Name, String Desc, int ParentID, int IsRootID, String Tags, String Propertiesids) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        int CatID = CreateCategory(Name, Desc, ParentID, IsRootID);
        if (CatID != 0) {
            String[] tags = Tags.split(",");
            for (String tag : tags) {
                EngineProductManager.CreateTags(CatID, "Category", tag);
            }
            ComputeCategoryProperty(CatID, Propertiesids);
        }
        return CatID;
    }

    /**
     *
     * @param CatID
     * @param Name
     * @param Desc
     * @param ParentID
     * @param IsRootID
     * @param Tags
     * @param Propertiesids
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static int EditCategory(int CatID, String Name, String Desc, int ParentID, int IsRootID, String Tags, String Propertiesids) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        if (!Tags.equals("")) {
            EngineProductManager.ComputeObjectTags(Tags, CatID, "Category");
        }

        if (!Name.equals("")) {
            String result = DBManager.UpdateStringData(Tables.CategoryTable.Table, Tables.CategoryTable.Name, Name, "where " + Tables.CategoryTable.ID + " = " + CatID);
        }
        if (!Desc.equals("")) {
            DBManager.UpdateStringData(Tables.CategoryTable.Table, Tables.CategoryTable.Description, Desc, "where " + Tables.CategoryTable.ID + " = " + CatID);

        }
        if (IsRootID != 0) {
            DBManager.UpdateIntData(Tables.CategoryTable.IsRootCategory, IsRootID, Tables.CategoryTable.Table, "where " + Tables.CategoryTable.ID + " = " + CatID);
        }
        if (ParentID != 0) {
            DBManager.UpdateIntData(Tables.CategoryTable.ParentID, ParentID, Tables.CategoryTable.Table, "where " + Tables.CategoryTable.ID + " = " + CatID);
        }
        if (!Propertiesids.isEmpty()) {
            ComputeCategoryProperty(CatID, Propertiesids);
        }
        return CatID;
    }

    /**
     *
     * @param CatID
     * @param PropertyIDs
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String ComputeCategoryProperty(int CatID, String PropertyIDs) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        if (!PropertyIDs.equals("")) {
            String[] propertyIDs = PropertyIDs.split(",");
            for (String propid : propertyIDs) {
                if (!propid.equals("")) {
                    int PropID = Integer.parseInt(propid);
                    int RootPropID = GetPropertyRootIDByPropertyID(PropID);
                    int existingPropID = CheckCategoryPropertyID(CatID, RootPropID, PropID);
                    if (existingPropID == 0) {
                        result = CreateCategoryProperty(CatID, RootPropID, PropID);
                    }
                }
            }
        }
        return result;
    }

    /**
     *
     * @param CatID
     * @param RootPropertyID
     * @param PropertyID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static int CheckCategoryPropertyID(int CatID, int RootPropertyID, int PropertyID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        int result = DBManager.GetInt(Tables.CategoryPropertiesTable.ID, Tables.CategoryPropertiesTable.Table, "where " + Tables.CategoryPropertiesTable.CategoryID + " = " + CatID
                + " AND " + Tables.CategoryPropertiesTable.RootPropertyID + " = " + RootPropertyID + " And " + Tables.CategoryPropertiesTable.PropertyID + " = " + PropertyID);
        return result;
    }

    /**
     *
     * @param Name
     * @param ParentID
     * @param IsRootProp
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String CreateProperties(String Name, int ParentID, int IsRootProp) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.PropertiesTable.Name, Name);
        tableData.put(Tables.PropertiesTable.ParentID, ParentID);
        tableData.put(Tables.PropertiesTable.IsRootProperty, IsRootProp);
        String result = DBManager.insertTableData(Tables.PropertiesTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param PropID
     * @param Name
     * @param ParentID
     * @param IsRootProp
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    public static String EditProperties(int PropID, String Name, int ParentID, int IsRootProp) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, ParseException {
        String result = "failed";
        if (!Name.equals("")) {
            result = DBManager.UpdateStringData(Tables.PropertiesTable.Table, Tables.PropertiesTable.Name, Name, "where " + Tables.PropertiesTable.ID + " = " + PropID);
        }
        if (ParentID != 0) {
            result = DBManager.UpdateIntData(Tables.PropertiesTable.ParentID, ParentID, Tables.PropertiesTable.Table, "where " + Tables.PropertiesTable.ID + " = " + PropID);
        }
        if (IsRootProp != 0) {
            result = DBManager.UpdateIntData(Tables.PropertiesTable.IsRootProperty, IsRootProp, Tables.PropertiesTable.Table, "where " + Tables.PropertiesTable.ID + " = " + PropID);
        }
        return result;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetRootCategoryIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.CategoryTable.ID, Tables.CategoryTable.Table, "where " + Tables.CategoryTable.IsRootCategory + " = " + 1 + " ORDER BY " + Tables.CategoryTable.Name);
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetRootCategoryCount() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.CategoryTable.ID, Tables.CategoryTable.Table, "where " + Tables.CategoryTable.IsRootCategory + " = " + 1 + " ORDER BY " + Tables.CategoryTable.Name);
        int count = IDs.size();
        return count;
    }

    /**
     *
     * @param start
     * @param limit
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetRootCategoryIDsWithLimit(int start, int limit) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.CategoryTable.ID, Tables.CategoryTable.Table, "where " + Tables.CategoryTable.IsRootCategory + " = " + 1 + " ORDER BY " + Tables.CategoryTable.Name + " LIMIT " + start + ", " + limit);
        return IDs;
    }

    /**
     *
     * @param RootCaID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetParentCategoryIDs(int RootCaID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.CategoryTable.ID, Tables.CategoryTable.Table, "where " + Tables.CategoryTable.ParentID + " = " + RootCaID);
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetParentCategoryCount() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.CategoryTable.ID, Tables.CategoryTable.Table, "");
        int count = 0;
        for (int id : IDs) {
            int parid = GetParentID(id);
            if (parid != 0) {
                int rootcheck = GetParentID(parid);
                if (rootcheck == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     *
     * @param RootCaID
     * @param Start
     * @param Limit
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetParentCategoryIDsWithLimit(int RootCaID, int Start, int Limit) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.CategoryTable.ID, Tables.CategoryTable.Table, "where " + Tables.CategoryTable.ParentID + " = " + RootCaID + " ORDER BY " + Tables.CategoryTable.Name + " LIMIT " + Start + ", " + Limit);
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetCategoryIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.CategoryTable.ID, Tables.CategoryTable.Table, "ORDER BY " + Tables.CategoryTable.Name);
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetTotalCategoryCount() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.CategoryTable.ID, Tables.CategoryTable.Table, "ORDER BY " + Tables.CategoryTable.ID);
        int count = IDs.size();
        return count;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetPropertyIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.PropertiesTable.ID, Tables.PropertiesTable.Table, "");
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetPropertyRootIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.PropertiesTable.ID, Tables.PropertiesTable.Table, "where " + Tables.PropertiesTable.IsRootProperty + " = " + 1);
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param RootPropID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetParentPropertyIDs(int RootPropID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.PropertiesTable.ID, Tables.PropertiesTable.Table, "where " + Tables.PropertiesTable.ParentID + " = " + RootPropID);
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param CatID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetCategoryData(int CatID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.CategoryTable.Table, "where " + Tables.CategoryTable.ID + " = " + CatID);
        if (!Details.isEmpty()) {
            try {
                int rootid = Integer.parseInt(Details.get(Tables.CategoryTable.IsRootCategory));
                String isroot = "";
                String parentname = "NIL";
                if (rootid == 1) {
                    isroot = "Yes";
                } else if (rootid == 0) {
                    isroot = "No";
                    int parentid = Integer.parseInt(Details.get(Tables.CategoryTable.ParentID));
                    parentname = GetCategoryName(parentid);
                }
                Details.put("IsRoot", isroot);
                Details.put("ParentName", parentname);

                //Get Image
                int ImageID = EngineImageManager.GetImageID(CatID, "Category");
                if (ImageID != 0) {
                    Details.put("ImageText", EngineImageManager.GetImageTextByImageID(ImageID));
                }

                //Get Tag Details
                HashMap<Integer, HashMap<String, String>> TagList = EngineProductManager.GetObjectTagList(CatID, "Category");
                JSONObject TagDet = new JSONObject();
                TagDet.put("TagDetails", TagList);
                if (!TagDet.isEmpty()) {
                    Details.putAll(TagDet);
                }

                //Get Tag Details
                HashMap<String, HashMap<String, String>> PropertyList = GetShopPropertiesByCategoryID(CatID);
                JSONObject PropertyDet = new JSONObject();
                PropertyDet.put("PropertyDetails", PropertyList);
                if (!PropertyDet.isEmpty()) {
                    Details.putAll(PropertyDet);
                }

            } catch (UnsupportedEncodingException | ClassNotFoundException | NumberFormatException | SQLException ex) {
            }
        }
        return Details;
    }

    /**
     *
     * @param CatID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, HashMap<String, String>> GetShopPropertiesByCategoryID(int CatID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> rootcatids = EngineCategoryManager.GetRootPropertyIDsByCategoryID(CatID);
        HashMap<String, HashMap<String, String>> catList = new HashMap<>();
        HashMap<Integer, Object> RootCatParIDs = new HashMap<>();
        if (!rootcatids.isEmpty()) {
            for (int rootcatid : rootcatids) {
                HashMap<String, String> topcatdetails = EngineCategoryManager.GetPropertyData(rootcatid);
                String Topcatid = "root" + rootcatid;
                catList.put(Topcatid, topcatdetails);
                ArrayList<Integer> parcatids = new ArrayList<>();
                parcatids = EngineCategoryManager.GetPropertyIDsByRootPropertyID(CatID, rootcatid);
                if (!parcatids.isEmpty()) {
                    for (int parcatid : parcatids) {
                        HashMap<String, String> parcatdetails = EngineCategoryManager.GetPropertyData(parcatid);
                        if (!parcatdetails.isEmpty()) {
                            String ParCatid = "par" + parcatid;
                            catList.put(ParCatid, parcatdetails);
                        }
                    }
                }
                RootCatParIDs.put(rootcatid, parcatids);
            }
        }
        JSONObject datares = new JSONObject();
        datares.put("RootCatParIDs", RootCatParIDs);
        datares.put("catList", catList);
        return datares;
    }

    /**
     *
     * @param PropID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetPropertyData(int PropID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.PropertiesTable.Table, "where " + Tables.PropertiesTable.ID + " = " + PropID);
        if (!Details.isEmpty()) {
            try {
                ArrayList<Integer> subcats = GetParentPropertyIDs(PropID);
                Details.put("values", "" + subcats.size());
            } catch (Exception ex) {
            }
        }
        return Details;
    }

    /**
     *
     * @param CatID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetCategoryName(int CatID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.CategoryTable.Name, Tables.CategoryTable.Table, "where " + Tables.CategoryTable.ID + " = " + CatID);
        return result;
    }

    /**
     *
     * @param PropertyID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetPropertyName(int PropertyID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.PropertiesTable.Name, Tables.PropertiesTable.Table, "where " + Tables.PropertiesTable.ID + " = " + PropertyID);
        return result;
    }

    /**
     *
     * @param CatID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetParentOrRootID(int CatID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.CategoryTable.ID, Tables.CategoryTable.Table, "where " + Tables.CategoryTable.ID + " = " + CatID);
        return result;
    }

    /**
     *
     * @param CatID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetParentID(int CatID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.CategoryTable.ParentID, Tables.CategoryTable.Table, "where " + Tables.CategoryTable.ID + " = " + CatID);
        return result;
    }

    /**
     *
     * @param CatID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteCategory(int CatID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        ArrayList<Integer> SubIDs = DBManager.GetIntArrayList(Tables.CategoryTable.ID, Tables.CategoryTable.Table, "where " + Tables.CategoryTable.IsRootCategory + " = " + CatID);
        SubIDs.addAll(DBManager.GetIntArrayList(Tables.CategoryTable.ID, Tables.CategoryTable.Table, "where " + Tables.CategoryTable.ParentID + " = " + CatID));
        if (!SubIDs.isEmpty()) {
            for (int catid : SubIDs) {
                result = DeleteCat(catid);
                DeleteCategory(catid);
            }
        }
        result = DeleteCat(CatID);
        return result;
    }

    /**
     *
     * @param CatID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteCat(int CatID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.DeleteObject(Tables.CategoryTable.Table, "where " + Tables.CategoryTable.ID + " = " + CatID);
        ArrayList<Integer> TagIDs = GetTags(CatID, "Category");
        if (!TagIDs.isEmpty()) {
            for (int tagid : TagIDs) {
                result = DBManager.DeleteObject(Tables.TagsTable.Table, "where " + Tables.TagsTable.ID + " = " + tagid);
            }
        }
        result = EngineImageManager.DeleteImage(CatID, "Category");

        ArrayList<Integer> PropIDs = GetPropertyIDSByCategoryID(CatID);
        if (!PropIDs.isEmpty()) {
            for (int propid : PropIDs) {
                result = DBManager.DeleteObject(Tables.CategoryPropertiesTable.Table, "where " + Tables.CategoryPropertiesTable.ID + " = " + propid);
            }
        }

        return result;
    }

    /**
     *
     * @param PropID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteProperties(int PropID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        ArrayList<Integer> SubIDs = DBManager.GetIntArrayList(Tables.PropertiesTable.ID, Tables.PropertiesTable.Table, "where " + Tables.PropertiesTable.IsRootProperty + " = " + PropID);
        SubIDs.addAll(DBManager.GetIntArrayList(Tables.PropertiesTable.ID, Tables.PropertiesTable.Table, "where " + Tables.PropertiesTable.ParentID + " = " + PropID));
        if (!SubIDs.isEmpty()) {
            for (int propid : SubIDs) {
                result = DBManager.DeleteObject(Tables.PropertiesTable.Table, "where " + Tables.PropertiesTable.ID + " = " + propid);
            }
        }

        ArrayList<Integer> PropIDs = GetPropertyIDSByPropertyID(PropID);
        if (!PropIDs.isEmpty()) {
            for (int propid : PropIDs) {
                result = DBManager.DeleteObject(Tables.CategoryPropertiesTable.Table, "where " + Tables.CategoryPropertiesTable.ID + " = " + propid);
            }
        }
        result = DBManager.DeleteObject(Tables.PropertiesTable.Table, "where " + Tables.PropertiesTable.ID + " = " + PropID);
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
    public static ArrayList<Integer> GetTags(int ObjectID, String ObjectType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.TagsTable.ID, Tables.TagsTable.Table, "where " + Tables.TagsTable.ObjectID + " = " + ObjectID + " And " + Tables.TagsTable.ObjectType + " = '" + ObjectType + "' ORDER BY " + Tables.TagsTable.Name);
        return IDs;
    }

    /**
     *
     * @param CatID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetPropertyIDSByCategoryID(int CatID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.CategoryPropertiesTable.ID, Tables.CategoryPropertiesTable.Table, "where " + Tables.CategoryPropertiesTable.CategoryID + " = " + CatID);
        return IDs;
    }

    /**
     *
     * @param CatID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetPropertyIDSByPropertyID(int PropID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int RootPropID = GetPropertyRootIDByPropertyID(PropID);
        ArrayList<Integer> IDs = new ArrayList();
        if (RootPropID == PropID) {
            IDs = DBManager.GetIntArrayList(Tables.CategoryPropertiesTable.ID, Tables.CategoryPropertiesTable.Table, "where " + Tables.CategoryPropertiesTable.RootPropertyID + " = " + RootPropID);
        } else {
            IDs = DBManager.GetIntArrayList(Tables.CategoryPropertiesTable.ID, Tables.CategoryPropertiesTable.Table, "where " + Tables.CategoryPropertiesTable.PropertyID + " = " + PropID);
        }
        return IDs;
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
    public static int GetTag(int ObjectID, String ObjectType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.TagsTable.ID, Tables.TagsTable.Table, "where " + Tables.TagsTable.ObjectID + " = " + ObjectID + " And " + Tables.TagsTable.ObjectType + " = '" + ObjectType + "'");
        return result;
    }

    /**
     *
     * @param TagID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetTagName(int TagID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.TagsTable.Name, Tables.TagsTable.Table, "where " + Tables.TagsTable.ID + " = " + TagID);
        return result;
    }

    /**
     *
     * @param ObjectID
     * @param ObjectType
     * @param SearchValue
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int SearchTag(int ObjectID, String ObjectType, String SearchValue) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.TagsTable.ID, Tables.TagsTable.Table, "where " + Tables.TagsTable.ObjectID + " = " + ObjectID + " And " + Tables.TagsTable.ObjectType + " = '" + ObjectType + "' And " + Tables.TagsTable.Name + " = '" + SearchValue + "'");
        return result;
    }
    /**
     *
     * @param PropertyID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetPropertyRootIDByPropertyID(int PropertyID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int PropRooID = 0;
        int Parent = DBManager.GetInt(Tables.PropertiesTable.ParentID, Tables.PropertiesTable.Table, "WHERE " + Tables.PropertiesTable.ID + " = " + PropertyID);
        if (Parent == 0) {
            PropRooID = PropertyID;
        } else if (Parent != 0) {
            int PropParID = DBManager.GetInt(Tables.PropertiesTable.ParentID, Tables.PropertiesTable.Table, "WHERE " + Tables.PropertiesTable.ID + " = " + Parent);
            if (PropParID == 0) {
                PropRooID = Parent;
            } else {
                PropRooID = PropParID;
            }
        }
        return PropRooID;
    }

    /**
     *
     * @param CategoryID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetCategoryRootIDByCategoryID(int CategoryID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int CatRooID = 0;
        int Parent = DBManager.GetInt(Tables.CategoryTable.ParentID, Tables.CategoryTable.Table, "WHERE " + Tables.CategoryTable.ID + " = " + CategoryID);
        if (Parent == 0) {
            CatRooID = CategoryID;
        } else if (Parent != 0) {
            int CatParID = DBManager.GetInt(Tables.CategoryTable.ParentID, Tables.CategoryTable.Table, "WHERE " + Tables.CategoryTable.ID + " = " + Parent);
            if (CatParID == 0) {
                CatRooID = Parent;
            } else if (CatParID != 0) {
                int CatSubParID = DBManager.GetInt(Tables.CategoryTable.ParentID, Tables.CategoryTable.Table, "WHERE " + Tables.CategoryTable.ID + " = " + CatParID);
                if (CatSubParID == 0) {
                    CatRooID = CatParID;
                } else {
                    CatRooID = CatSubParID;
                }
            }
        }
        return CatRooID;
    }

    /**
     *
     * @param TagID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetTagData(int TagID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.TagsTable.Table, "where " + Tables.TagsTable.ID + " = " + TagID);
        return Details;
    }

    /**
     *
     * @param CatID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetRootPropertyIDsByCategoryID(int CatID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.CategoryPropertiesTable.RootPropertyID, Tables.CategoryPropertiesTable.Table, "where " + Tables.CategoryPropertiesTable.CategoryID + " = " + CatID);
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param CatID
     * @param RootPropID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetPropertyIDsByRootPropertyID(int CatID, int RootPropID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.CategoryPropertiesTable.PropertyID, Tables.CategoryPropertiesTable.Table, "where " + 
                Tables.CategoryPropertiesTable.RootPropertyID + " = " + RootPropID + " AND " + Tables.CategoryPropertiesTable.CategoryID + " = " + CatID);
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }
}
