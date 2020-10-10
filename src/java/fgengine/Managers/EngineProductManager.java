/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import fgengine.Tables.Tables;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author mac
 */
public class EngineProductManager {
    //Step 1, Create Product
    //Step 2, Approve Product

    /**
     *
     * @param SellerUserID
     * @param ProductName
     * @param ProductConditionID
     * @param ProductUnitID
     * @param ReferenceCode
     * @param UPCBarcode
     * @param Description
     * @param CategoryIDs
     * @param PropertyIDs
     * @param CostPrice
     * @param SellingPrice
     * @param MimimumQuantity
     * @param TotalQuantity
     * @param PackageHeight
     * @param PackageWidth
     * @param PackageDepth
     * @param MinimumStockLevel
     * @param NotificationTypeID
     * @param ProductUnitValue
     * @param ProductTags
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int ComputeProduct(int SellerUserID, String ProductName, int ProductConditionID, int ProductUnitID, String ReferenceCode,
            String UPCBarcode, String Description, String CategoryIDs, String PropertyIDs, double CostPrice, double SellingPrice,
            int MimimumQuantity, int TotalQuantity, int PackageHeight, int PackageWidth, int PackageDepth,
            int MinimumStockLevel, int NotificationTypeID, String ProductUnitValue, String ProductTags) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int ProductID = CreateProduct(ProductConditionID); //product
        if (ProductID != 0) {
            result = CreateProductUnit(ProductID, ProductUnitID, ProductUnitValue); //unit
            if (result.equals("success")) {
                result = CreateSellerProducts(SellerUserID, ProductID); //seller
                if (result.equals("success")) {
                    String info = CreateProductInformation(ProductID, ProductName, ReferenceCode, UPCBarcode, Description);//info
                    if (info.equals("success")) {
                        result = ComputeProductCategory(CategoryIDs, ProductID);//category
                        if (result.equals("success")) {
                            result = ComputeProductProperties(PropertyIDs, ProductID); //properties
                            if (result.equals("success")) {
                                String prices = CreateProductPrices(ProductID, CostPrice, SellingPrice); //price
                                if (prices.equals("success")) {
                                    String qty = CreateProductQuantity(ProductID, MimimumQuantity, TotalQuantity);//quantity
                                    if (qty.equals("success")) {
                                        String shippackage = CreateProductShippingPackage(ProductID, PackageHeight, PackageWidth, PackageDepth);//shipping package
                                        if (shippackage.equals("success")) {
                                            String stock = CreateProductStockLevel(ProductID, MinimumStockLevel, NotificationTypeID);//stock
                                            if (stock.equals("success")) {
                                                result = ComputeObjectTags(ProductTags, ProductID, "Product");
                                                if (!result.equals("success")) {
                                                    result = "Oops! Some product details were not created. Please contact the support team. - tags";
                                                }
                                            } else {
                                                result = "Oops! Some product details were not created. Please contact the support team. - stock";
                                            }
                                        } else {
                                            result = "Oops! Some product details were not created. Please contact the support team. - shippackage";
                                        }
                                    } else {
                                        result = "Oops! Some product details were not created. Please contact the support team. - qty";
                                    }
                                } else {
                                    result = "Oops! Some product details were not created. Please contact the support team. - prices";
                                }
                            } else {
                                result = "Oops! Some product details were not created. Please contact the support team. + properties";
                            }
                        } else {
                            result = "Oops! Some product details were not created. Please contact the support team. - category";
                        }
                    } else {
                        result = "Oops! Some product details were not created. Please contact the support team. - info";
                    }
                } else {
                    result = "Oops! Some product details were not created. Please contact the support team. - seller ";
                }
            } else {
                result = "Oops! Some product details were not created. Please contact the support team. - unit";
            }

        } else {
            result = "Oops! The Product was not created.";
        }
        result = result;
        return ProductID;
    }

    /**
     *
     * @param CategoryIDs
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputeProductCategory(String CategoryIDs, int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        if (!CategoryIDs.isEmpty()) {
            String[] catids = CategoryIDs.split(",");
            for (String catid : catids) {
                if (!catid.isEmpty()) {
                    int CatID = Integer.parseInt(catid);
                    int CatRootID = EngineCategoryManager.GetCategoryRootIDByCategoryID(CatID);
                    int ExistingProdCatID = GetProductCategoryID(ProductID, CatRootID);
                    if (ExistingProdCatID == 0) {
                        result = CreateProductCategories(ProductID, CatRootID);
                        if (CatRootID != CatID) {
                            CreateProductCategories(ProductID, CatID);
                        }
                    } else {
                        int ExistingProdCatID2 = GetProductCategoryID(ProductID, CatID);
                        if (ExistingProdCatID2 == 0) {
                            result = CreateProductCategories(ProductID, CatID);
                        }

                    }
                }
            }
        }
        return result;
    }

    /**
     *
     * @param ObjectTags
     * @param ObjectID
     * @param ObjectType
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputeObjectTags(String ObjectTags, int ObjectID, String ObjectType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        if (!ObjectTags.isEmpty()) {
            String[] Tags = ObjectTags.split(",");
            for (String tagname : Tags) {
                if (!tagname.isEmpty()) {
                    int existingTag = EngineCategoryManager.SearchTag(ObjectID, ObjectType, tagname);
                    if (existingTag == 0) {
                        result = CreateTags(ObjectID, ObjectType, tagname);
                    }
                }
            }
        }
        return result;
    }

    /**
     *
     * @param PropertyIDs
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputeProductProperties(String PropertyIDs, int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        String[] PropIDs = PropertyIDs.split(",");
        for (String Propid : PropIDs) {
            if (!Propid.isEmpty()) {
                int PropID = Integer.parseInt(Propid);
                int PropRootID = EngineCategoryManager.GetPropertyRootIDByPropertyID(PropID);
                int ExistingProdPropID1 = GetProductPropertyID(ProductID, PropRootID);
                int ExistingProdPropID = GetProductPropertyID(ProductID, PropID);
                if (ExistingProdPropID == 0 && ExistingProdPropID1 == 0) {
                    if (PropRootID != PropID && PropRootID != 0) {
                        result = CreateProductProperties(ProductID, PropID);
                    }
                }
            }
        }
        return result;
    }

    /**
     *
     * @param ProductID
     * @param PropertyID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetProductPropertyID(int ProductID, int PropertyID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.ProductPropertiesTable.ID, Tables.ProductPropertiesTable.Table, "where " + Tables.ProductPropertiesTable.ProductID + " = " + ProductID + " and " + Tables.ProductPropertiesTable.PropertyID + " = " + PropertyID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @param CategoryID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetProductCategoryID(int ProductID, int CategoryID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = 0;
        result = DBManager.GetInt(Tables.ProductCategoriesTable.ID, Tables.ProductCategoriesTable.Table, "where " + Tables.ProductCategoriesTable.ProductID + " = " + ProductID + " and " + Tables.ProductCategoriesTable.CategoryID + " = " + CategoryID);
        return result;
    }

    /**
     *
     * @param ProductConditionID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int CreateProduct(int ProductConditionID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ProductsTable.ProductConditionID, ProductConditionID);
        int ProductID = DBManager.insertTableDataReturnID(Tables.ProductsTable.Table, tableData, "");
        DBManager.UpdateCurrentDate(Tables.ProductsTable.Table, Tables.ProductsTable.Date, "where " + Tables.ProductsTable.ID + " = " + ProductID);
        DBManager.UpdateCurrentTime(Tables.ProductsTable.Table, Tables.ProductsTable.Time, "where " + Tables.ProductsTable.ID + " = " + ProductID);
        return ProductID;
    }

    /**
     *
     * @param ProductID
     * @param ProductUnitID
     * @param ProductUnitValue
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateProductUnit(int ProductID, int ProductUnitID, String ProductUnitValue) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ProductUnitsTable.ProductID, ProductID);
        tableData.put(Tables.ProductUnitsTable.UnitID, ProductUnitID);
        tableData.put(Tables.ProductUnitsTable.UnitValue, ProductUnitValue);
        String result = DBManager.insertTableData(Tables.ProductUnitsTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param ProductID
     * @param ProductName
     * @param ReferenceCode
     * @param UPCBarcode
     * @param Description
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateProductInformation(int ProductID, String ProductName, String ReferenceCode, String UPCBarcode, String Description) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ProductInfoTable.ProductID, ProductID);
        tableData.put(Tables.ProductInfoTable.Name, ProductName);
        tableData.put(Tables.ProductInfoTable.ReferenceCode, ReferenceCode);
        tableData.put(Tables.ProductInfoTable.UPCBarcode, UPCBarcode);
        tableData.put(Tables.ProductInfoTable.Description, Description);
        result = DBManager.insertTableData(Tables.ProductInfoTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param SellerUserID
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateSellerProducts(int SellerUserID, int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.SellerProductsTable.SellerUserID, SellerUserID);
        tableData.put(Tables.SellerProductsTable.ProductID, ProductID);
        int id = DBManager.insertTableDataReturnID(Tables.SellerProductsTable.Table, tableData, "");
        result = DBManager.UpdateCurrentDate(Tables.SellerProductsTable.Table, Tables.SellerProductsTable.Date, "where " + Tables.SellerProductsTable.ID + " = " + id);
        DBManager.UpdateCurrentTime(Tables.SellerProductsTable.Table, Tables.SellerProductsTable.Time, "where " + Tables.SellerProductsTable.ID + " = " + id);
        return result;
    }

    /**
     *
     * @param ProductID
     * @param CategoryID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateProductCategories(int ProductID, int CategoryID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ProductCategoriesTable.ProductID, ProductID);
        tableData.put(Tables.ProductCategoriesTable.CategoryID, CategoryID);
        result = DBManager.insertTableData(Tables.ProductCategoriesTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param ProductID
     * @param PropertyID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateProductProperties(int ProductID, int PropertyID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ProductPropertiesTable.ProductID, ProductID);
        tableData.put(Tables.ProductPropertiesTable.PropertyID, PropertyID);
        result = DBManager.insertTableData(Tables.ProductPropertiesTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param ProductID
     * @param CostPrice
     * @param SellingPrice
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateProductPrices(int ProductID, double CostPrice, double SellingPrice) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ProductPriceTable.ProductID, ProductID);
        tableData.put(Tables.ProductPriceTable.CostPrice, CostPrice);
        tableData.put(Tables.ProductPriceTable.SellingPrice, SellingPrice);
        tableData.put(Tables.ProductPriceTable.BasePrice, SellingPrice);
        result = DBManager.insertTableData(Tables.ProductPriceTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param ProductID
     * @param MinimumQuantity
     * @param TotalQuantity
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateProductQuantity(int ProductID, int MinimumQuantity, int TotalQuantity) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ProductQuantityTable.ProductID, ProductID);
        tableData.put(Tables.ProductQuantityTable.MinimumQuantity, MinimumQuantity);
        tableData.put(Tables.ProductQuantityTable.TotalQuantity, TotalQuantity);
        result = DBManager.insertTableData(Tables.ProductQuantityTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param ProductID
     * @param PackageHeight
     * @param PackageWidth
     * @param PackageDepth
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateProductShippingPackage(int ProductID, int PackageHeight, int PackageWidth, int PackageDepth) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ProductShippingPackageTable.ProductID, ProductID);
        tableData.put(Tables.ProductShippingPackageTable.PackageHeight, PackageHeight);
        tableData.put(Tables.ProductShippingPackageTable.PackageWidth, PackageWidth);
        tableData.put(Tables.ProductShippingPackageTable.PackageDepth, PackageDepth);
        result = DBManager.insertTableData(Tables.ProductShippingPackageTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param ProductID
     * @param MinimumStockLevel
     * @param NotificationTypeID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateProductStockLevel(int ProductID, int MinimumStockLevel, int NotificationTypeID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ProductStockLevelTable.ProductID, ProductID);
        tableData.put(Tables.ProductStockLevelTable.MinimumStockLevel, MinimumStockLevel);
        tableData.put(Tables.ProductStockLevelTable.NotificationTypeID, NotificationTypeID);
        result = DBManager.insertTableData(Tables.ProductStockLevelTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param ProductID
     * @param Status
     * @param Note
     * @param Option
     * @param ProductIDs
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ProcessProductStatus(int ProductID, String Status, String Note, String Option, String ProductIDs) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";

        if (Option.equals("Single")) {
            result = UpdateProductStatus(ProductID, Status, Note);
        } else if (Option.equals("Bulk")) {
            if (!ProductIDs.isEmpty()) {
                String[] prodids = ProductIDs.split(",");
                for (String prodid : prodids) {
                    if (!prodid.isEmpty()) {
                        int ProdID = Integer.parseInt(prodid);
                        result = UpdateProductStatus(ProdID, Status, Note);
                    }
                }
            }

        }

        return result;
    }

    /**
     *
     * @param ProductID
     * @param Status
     * @param Note
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateProductStatus(int ProductID, String Status, String Note) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        String SellerProdStatus = GetSellerProductStatus(ProductID);
        if (Status.equals("Rejected") && SellerProdStatus.equals("Pending")) {
            result = DBManager.UpdateStringData(Tables.SellerProductsTable.Table, Tables.SellerProductsTable.Status, Status, "where " + Tables.SellerProductsTable.ProductID + " = " + ProductID);
            DBManager.UpdateIntData(Tables.ProductsTable.Active, 0, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " = " + ProductID);
            DBManager.UpdateStringData(Tables.SellerProductsTable.Table, Tables.SellerProductsTable.Note, Note, "where " + Tables.SellerProductsTable.ProductID + " = " + ProductID);
        } else if (Status.equals("Activated")) {
            result = DBManager.UpdateStringData(Tables.SellerProductsTable.Table, Tables.SellerProductsTable.Status, Status, "where " + Tables.SellerProductsTable.ProductID + " = " + ProductID);
            DBManager.UpdateIntData(Tables.ProductsTable.Active, 1, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " = " + ProductID);
            DBManager.UpdateStringData(Tables.SellerProductsTable.Table, Tables.SellerProductsTable.Note, Note, "where " + Tables.SellerProductsTable.ProductID + " = " + ProductID);
        } else if (Status.equals("Deactivated") && SellerProdStatus.equals("Activated")) {
            result = DBManager.UpdateStringData(Tables.SellerProductsTable.Table, Tables.SellerProductsTable.Status, Status, "where " + Tables.SellerProductsTable.ProductID + " = " + ProductID);
            DBManager.UpdateIntData(Tables.ProductsTable.Active, 0, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " = " + ProductID);
            DBManager.UpdateStringData(Tables.SellerProductsTable.Table, Tables.SellerProductsTable.Note, Note, "where " + Tables.SellerProductsTable.ProductID + " = " + ProductID);
        } else if (Status.equals("Deleted") && !SellerProdStatus.equals("Activated")) {
            int ProductStatus = GetProductStatus(ProductID);
            result = DeleteProduct(ProductID, ProductStatus, SellerProdStatus);
        }
        if (!Status.equals("Deleted")) {
            int SellerUserID = GetProductSellerUserIDByProductID(ProductID);
            String email = EngineUserManager.GetUserEmail(SellerUserID);
            try {
                String body = "Hi " + EngineUserManager.GetUserName(SellerUserID) + ", \n\n" + Note + "\n\n Cheers \nFyngram.";
                EngineEmailManager.SendEmail(email, body, Status + "- Product");
                EngineMessageManager.sendMessage(EngineUserManager.GetAdminUserID(), body, Status + "- Product", SellerUserID);
            } catch (IOException ex) {
                Logger.getLogger(EngineProductManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return result;
    }

    /**
     *
     * @param ObjectID
     * @param ObjectType
     * @param TagName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String CreateTags(int ObjectID, String ObjectType, String TagName) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.TagsTable.ObjectID, ObjectID);
        tableData.put(Tables.TagsTable.ObjectType, ObjectType);
        tableData.put(Tables.TagsTable.Name, TagName);
        result = DBManager.insertTableData(Tables.TagsTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetProductSellerUserIDByProductID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.SellerProductsTable.SellerUserID, Tables.SellerProductsTable.Table, "where " + Tables.SellerProductsTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetSellerProductIDByProductID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.SellerProductsTable.ID, Tables.SellerProductsTable.Table, "where " + Tables.SellerProductsTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetProductQuantityByProductID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.ProductQuantityTable.TotalQuantity, Tables.ProductQuantityTable.Table, "where " + Tables.ProductQuantityTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @param NewProductTotalQuantity
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String UpdateProductQuantityByProductID(int ProductID, int NewProductTotalQuantity) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.UpdateIntData(Tables.ProductQuantityTable.TotalQuantity, NewProductTotalQuantity, Tables.ProductQuantityTable.Table, "where " + Tables.ProductQuantityTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetProductMinimumQuantityByProductID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.ProductQuantityTable.MinimumQuantity, Tables.ProductQuantityTable.Table, "where " + Tables.ProductQuantityTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetProductNameByProductID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.ProductInfoTable.Name, Tables.ProductInfoTable.Table, "where " + Tables.ProductInfoTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetProductDescriptionByProductID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.ProductInfoTable.Description, Tables.ProductInfoTable.Table, "where " + Tables.ProductInfoTable.ProductID + " = " + ProductID);
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
    public static ArrayList<Integer> GetSellerProducts(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.SellerProductsTable.ID, Tables.SellerProductsTable.Table, "where " + Tables.SellerProductsTable.SellerUserID + " = " + UserID);
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
    public static ArrayList<Integer> GetSellerProductIDs(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.SellerProductsTable.ProductID, Tables.SellerProductsTable.Table, "where " + Tables.SellerProductsTable.SellerUserID + " = " + UserID);
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetProductCondtions() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductConditionTable.ID, Tables.ProductConditionTable.Table, "Order by " + Tables.ProductConditionTable.Name);
        return IDs;
    }

    /**
     *
     * @param ConditionID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetProductCondtionData(int ConditionID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.ProductConditionTable.Table, "where " + Tables.ProductConditionTable.ID + " = " + ConditionID);
        return Details;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetStockNotificationIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayList(Tables.StockNotificationTypeTable.ID, Tables.StockNotificationTypeTable.Table, "Order By " + Tables.StockNotificationTypeTable.Name);
        IDs = UtilityManager.SortAndReverseIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param NotificationID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetStockNotificationData(int NotificationID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.StockNotificationTypeTable.Table, "where " + Tables.StockNotificationTypeTable.ID + " = " + NotificationID);

        return Details;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetUnitIDs() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.UnitsTable.ID, Tables.UnitsTable.Table, "ORDER BY " + Tables.UnitsTable.Name);
        return IDs;
    }

    /**
     *
     * @param UnitID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetUnitData(int UnitID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.UnitsTable.Table, "where " + Tables.UnitsTable.ID + " = " + UnitID);
        return Details;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetProductUnitData(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.ProductUnitsTable.Table, "where " + Tables.ProductUnitsTable.ProductID + " = " + ProductID);
        if (!Details.isEmpty()) {
            int UnitID = Integer.parseInt(Details.get(Tables.ProductUnitsTable.UnitID));
            String UnitName = GetUnitNameByID(UnitID);
            Details.put("UnitName", UnitName);
        }
        return Details;
    }

    /**
     *
     * @param UnitID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetUnitNameByID(int UnitID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.UnitsTable.Name, Tables.UnitsTable.Table, "where " + Tables.UnitsTable.ID + " = " + UnitID);
        return result;
    }

    /**
     *
     * @param SellerProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetProductSellerData(int SellerProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.SellerProductsTable.Table, "where " + Tables.SellerProductsTable.ID + " = " + SellerProductID);
        if (!Details.isEmpty()) {
            int SellerUserID = Integer.parseInt(Details.get(Tables.SellerProductsTable.SellerUserID));
            HashMap<String, String> InfoDetails = GetSellerInfoData(SellerUserID);
            Details.putAll(InfoDetails);
        }
        return Details;
    }

    /**
     *
     * @param SellerID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetSellerInfoData(int SellerID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.SellerInfoTable.Table, "where " + Tables.SellerInfoTable.SellerUserID + " = " + SellerID);
        if (!Details.isEmpty()) {
            Details.put("SellerUserName", Details.get(Tables.SellerInfoTable.BusinessName));
            int shipmin = Integer.parseInt(Details.get(Tables.SellerInfoTable.MinimumShippingDays));
            int shipmax = Integer.parseInt(Details.get(Tables.SellerInfoTable.MaximumShippingDays));

            LocalDate CurrentDate = LocalDate.now();
            LocalDate startDate = CurrentDate.plusDays(shipmin);
            LocalDate endDate = CurrentDate.plusDays(shipmax);
            String sdate = DateManager.readDate("" + startDate);
            Details.put("shipStartDate", sdate);
            String eDate = DateManager.readDate("" + endDate);
            Details.put("shipEndDate", eDate);
        }

        return Details;
    }

    /**
     *
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetProductIDs(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        if (UserID == 1) {
            IDs = DBManager.GetIntArrayListDescending(Tables.ProductsTable.ID, Tables.ProductsTable.Table, "ORDER BY id DESC");
        } else {
            IDs = GetSellerProductIDs(UserID);
        }
        return IDs;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetProductData(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {

        HashMap<String, String> Details = DBManager.GetTableData(Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " = " + ProductID);
        if (!Details.isEmpty()) {
            int CondtionID = Integer.parseInt(Details.get(Tables.ProductsTable.ProductConditionID));
            JSONObject ConditionDet = new JSONObject();
            ConditionDet.put("CondionDetails", GetProductCondtionData(CondtionID));
            if (!ConditionDet.isEmpty()) {
                Details.putAll(ConditionDet);
            }

            String addeddate = Details.get(Tables.ProductsTable.Date);
            LocalDate AddedDate = LocalDate.parse(addeddate);
            int showconditioncount = GetProductConditionCount(CondtionID);
            if (showconditioncount != 0) {
                LocalDate CurrentDate = LocalDate.now();
                if (AddedDate.plusDays(showconditioncount).isBefore(CurrentDate)) {
                    Details.put(Tables.ProductsTable.ShowCondition, "" + 0);
                }
            }
            Details.put("ProductID", "" + ProductID);
            //Get Unit Details
            JSONObject UnitDet = new JSONObject();
            UnitDet.put("UnitDetails", GetProductUnitData(ProductID));
            if (!UnitDet.isEmpty()) {
                Details.putAll(UnitDet);
            }

            //Get Category Details
            HashMap<Integer, HashMap<String, String>> CatList = GetProductCategoryList(ProductID);

            JSONObject CategoryDet = new JSONObject();
            CategoryDet.put("CategoryDetails", CatList);
            if (!CategoryDet.isEmpty()) {
                Details.putAll(CategoryDet);
            }

            //Get avarage ratings Details
            JSONObject RatingsDet = new JSONObject();
            RatingsDet.put("RatingDetails", EngineReviewManager.ObjectReviews(ProductID));
            Details.putAll(RatingsDet);

            //Get Rating Details
            HashMap<Integer, HashMap<String, String>> ReviewList = EngineReviewManager.GetObjectReviewList(ProductID);
            JSONObject ReviewDet = new JSONObject();
            ReviewDet.put("ReviewDetails", ReviewList);
            if (!ReviewDet.isEmpty()) {
                Details.putAll(ReviewDet);
            }

            int FirstCatID = GetProductFirstRootCatID(ProductID);
            int FirstCatRootID = EngineCategoryManager.GetCategoryRootIDByCategoryID(FirstCatID);
            if (FirstCatID != 0 || FirstCatRootID != 0) {
                String RootCatName = "";
                if (FirstCatRootID == 0) {
                    RootCatName = EngineCategoryManager.GetCategoryName(FirstCatRootID);
                    Details.put("RootCatID", "" + FirstCatRootID);
                } else {
                    RootCatName = EngineCategoryManager.GetCategoryName(FirstCatID);
                    Details.put("RootCatID", "" + FirstCatID);
                }
                Details.put("RootCatName", RootCatName);
            }

            //Get Property Details
            HashMap<Integer, HashMap<String, String>> PropList = GetProductPropertyList(ProductID);
            JSONObject PropertyDet = new JSONObject();
            PropertyDet.put("PropertyDetails", PropList);
            if (!PropertyDet.isEmpty()) {
                Details.putAll(PropertyDet);
            }

            //Get Image Details
            HashMap<Integer, HashMap<String, String>> ImageList2 = GetProductImageList2(ProductID);
            JSONObject ImageDet2 = new JSONObject();
            ImageDet2.put("ImageDetails2", ImageList2);
            if (!ImageDet2.isEmpty()) {
                Details.putAll(ImageDet2);
            }

            HashMap<String, String> ImageList = GetProductImageList(ProductID);
            JSONObject ImageDet = new JSONObject();
            ImageDet.put("ImageDetails", ImageList);
            if (!ImageDet.isEmpty()) {
                Details.putAll(ImageDet);
            }

            int FirstImageID = EngineImageManager.GetFirstImageID(ProductID, "Product");
            if (FirstCatID != 0 || FirstCatRootID != 0) {
                String FirstImage = EngineImageManager.GetImageTextByImageID(FirstImageID);
                Details.put("FirstImage", FirstImage);
            }
            int SecondImageID = EngineImageManager.GetSecondImageID(ProductID, "Product");
            if (SecondImageID != 0 || SecondImageID != 0) {
                String SecondImage = EngineImageManager.GetImageTextByImageID(SecondImageID);
                Details.put("SecondImage", SecondImage);
            }

            //Get Tag Details
            HashMap<Integer, HashMap<String, String>> TagList = GetObjectTagList(ProductID, "Product");
            JSONObject TagDet = new JSONObject();
            TagDet.put("TagDetails", TagList);
            if (!TagDet.isEmpty()) {
                Details.putAll(TagDet);
            }

            //Get Info
            int InfoID = GetProductInfoIDByProductID(ProductID);
            JSONObject InfoDet = new JSONObject();
            InfoDet.put("InfoDetails", GetProductInfoData(InfoID));
            if (!InfoDet.isEmpty()) {
                Details.putAll(InfoDet);
            }

            //Get Price
            int PriceID = GetProductPriceIDByProductID(ProductID);
            JSONObject PriceDet = new JSONObject();
            PriceDet.put("PriceDetails", GetProductPriceData(PriceID));
            if (!PriceDet.isEmpty()) {
                Details.putAll(PriceDet);
            }

            //Get Quantity
            int QuantityID = GetProductQuantityIDByProductID(ProductID);
            JSONObject QuantityDet = new JSONObject();
            QuantityDet.put("QuantityDetails", GetProductQuantityData(QuantityID));
            if (!QuantityDet.isEmpty()) {
                Details.putAll(QuantityDet);
            }

            //Get Sellers
            int SellerProdID = GetSellerProductIDByProductID(ProductID);
            JSONObject SellerDet = new JSONObject();
            SellerDet.put("SellerDetails", GetProductSellerData(SellerProdID));
            if (!SellerDet.isEmpty()) {
                Details.putAll(SellerDet);
            }
            //Get Stock
            int StockID = GetProductStockIDByProductID(ProductID);
            JSONObject StockDet = new JSONObject();
            StockDet.put("StockDetails", GetProductStockLevelData(StockID));
            if (!StockDet.isEmpty()) {
                Details.putAll(StockDet);
            }
            //Get Shipping
            int ShipPackageID = GetProductShippingPackageIDByProductID(ProductID);
            JSONObject ShippingPackageDet = new JSONObject();
            ShippingPackageDet.put("ShippingPackageDetails", GetProductShippingPackageData(ShipPackageID));
            if (!ShippingPackageDet.isEmpty()) {
                Details.putAll(ShippingPackageDet);
            }

            String date = Details.get(Tables.ProductsTable.Date);
            String Date = DateManager.readDate(date);
            Details.put(Tables.ProductsTable.Date, Date);
            String time = Details.get(Tables.ProductsTable.Time);
            String Time = DateManager.readTime(time);
            Details.put(Tables.ProductsTable.Time, Time);

        }
        return Details;
    }

    
      /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetMiniProductData(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {

        HashMap<String, String> Details = DBManager.GetTableData(Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " = " + ProductID);
        if (!Details.isEmpty()) {
            int CondtionID = Integer.parseInt(Details.get(Tables.ProductsTable.ProductConditionID));
            JSONObject ConditionDet = new JSONObject();
            ConditionDet.put("CondionDetails", GetProductCondtionData(CondtionID));
            if (!ConditionDet.isEmpty()) {
                Details.putAll(ConditionDet);
            }

            String addeddate = Details.get(Tables.ProductsTable.Date);
            LocalDate AddedDate = LocalDate.parse(addeddate);
            int showconditioncount = GetProductConditionCount(CondtionID);
            if (showconditioncount != 0) {
                LocalDate CurrentDate = LocalDate.now();
                if (AddedDate.plusDays(showconditioncount).isBefore(CurrentDate)) {
                    Details.put(Tables.ProductsTable.ShowCondition, "" + 0);
                }
            }
            Details.put("ProductID", "" + ProductID);
            //Get Unit Details


            int FirstCatID = GetProductFirstRootCatID(ProductID);
            int FirstCatRootID = EngineCategoryManager.GetCategoryRootIDByCategoryID(FirstCatID);
            if (FirstCatID != 0 || FirstCatRootID != 0) {
                String RootCatName = "";
                if (FirstCatRootID == 0) {
                    RootCatName = EngineCategoryManager.GetCategoryName(FirstCatRootID);
                    Details.put("RootCatID", "" + FirstCatRootID);
                } else {
                    RootCatName = EngineCategoryManager.GetCategoryName(FirstCatID);
                    Details.put("RootCatID", "" + FirstCatID);
                }
                Details.put("RootCatName", RootCatName);
            }


            int FirstImageID = EngineImageManager.GetFirstImageID(ProductID, "Product");
            if (FirstCatID != 0 || FirstCatRootID != 0) {
                String FirstImage = EngineImageManager.GetImageTextByImageID(FirstImageID);
                Details.put("FirstImage", FirstImage);
            }
            int SecondImageID = EngineImageManager.GetSecondImageID(ProductID, "Product");
            if (SecondImageID != 0 || SecondImageID != 0) {
                String SecondImage = EngineImageManager.GetImageTextByImageID(SecondImageID);
                Details.put("SecondImage", SecondImage);
            }


            //Get Info
            int InfoID = GetProductInfoIDByProductID(ProductID);
            JSONObject InfoDet = new JSONObject();
            InfoDet.put("InfoDetails", GetProductInfoData(InfoID));
            if (!InfoDet.isEmpty()) {
                Details.putAll(InfoDet);
            }

            //Get Price
            int PriceID = GetProductPriceIDByProductID(ProductID);
            JSONObject PriceDet = new JSONObject();
            PriceDet.put("PriceDetails", GetProductPriceData(PriceID));
            if (!PriceDet.isEmpty()) {
                Details.putAll(PriceDet);
            }

        }
        return Details;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<Integer, HashMap<String, String>> GetProductCategoryList(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<Integer, HashMap<String, String>> CatList = new HashMap<>();
        HashMap<String, String> CatDetailList = new HashMap<>();
        ArrayList<Integer> ProdCatIDS = GetProductCategoryIDsByProductID(ProductID);
        if (!ProdCatIDS.isEmpty()) {
            for (int ProdCatID : ProdCatIDS) {
                CatDetailList = GetProductCategoryData(ProdCatID);
                if (!CatDetailList.isEmpty()) {
                    CatList.put(ProdCatID, CatDetailList);
                }
            }
        }
        return CatList;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<Integer, HashMap<String, String>> GetProductImageList2(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<Integer, HashMap<String, String>> ImageList = new HashMap<>();
        HashMap<String, String> ImageDetailList = new HashMap<>();
        ArrayList<Integer> ImageIDS = EngineImageManager.GetImageIDs(ProductID, "Product");
        if (!ImageIDS.isEmpty()) {
            for (int ImageID : ImageIDS) {
                ImageDetailList = EngineImageManager.GetImageData(ImageID);
                if (!ImageDetailList.isEmpty()) {
                    ImageList.put(ImageID, ImageDetailList);
                }
            }
        }
        return ImageList;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetProductImageList(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<Integer, HashMap<String, String>> ImageList = new HashMap<>();
        HashMap<String, String> ImageDetailList = new HashMap<>();
        ArrayList<Integer> ImageIDS = EngineImageManager.GetImageIDs(ProductID, "Product");
        int count = 0;
        if (!ImageIDS.isEmpty()) {
            for (int ImageID : ImageIDS) {
                count++;
                String ImageText = EngineImageManager.GetImageTextByImageID(ImageID);
                ImageDetailList.put("ImageText" + count, ImageText);
            }
        }
        return ImageDetailList;
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
    public static HashMap<Integer, HashMap<String, String>> GetObjectTagList(int ObjectID, String ObectType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<Integer, HashMap<String, String>> TagList = new HashMap<>();
        HashMap<String, String> TagDetailList = new HashMap<>();
        ArrayList<Integer> TagIDS = EngineCategoryManager.GetTags(ObjectID, ObectType);
        if (!TagIDS.isEmpty()) {
            for (int TagID : TagIDS) {
                TagDetailList = EngineCategoryManager.GetTagData(TagID);
                if (!TagDetailList.isEmpty()) {
                    TagList.put(TagID, TagDetailList);
                }
            }
        }
        return TagList;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<Integer, HashMap<String, String>> GetProductPropertyList(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<Integer, HashMap<String, String>> PropList = new HashMap<>();
        HashMap<String, String> PropDetailList = new HashMap<>();
        ArrayList<Integer> PropIDS = GetProductPropertyIDsByProductID(ProductID);
        if (!PropIDS.isEmpty()) {
            for (int PropID : PropIDS) {
                PropDetailList = GetProductPropertyData(PropID);

                if (!PropDetailList.isEmpty()) {
                    PropList.put(PropID, PropDetailList);
                }
            }
        }
        return PropList;
    }

    /**
     *
     * @param ConditionID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetProductConditionName(int ConditionID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.ProductConditionTable.Name, Tables.ProductConditionTable.Table, "where " + Tables.ProductConditionTable.ID + " = " + ConditionID);
        return result;
    }

    /**
     *
     * @param ConditionID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetProductConditionCount(int ConditionID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.ProductConditionTable.Count, Tables.ProductConditionTable.Table, "where " + Tables.ProductConditionTable.ID + " = " + ConditionID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetProductInfoIDByProductID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.ProductInfoTable.ID, Tables.ProductInfoTable.Table, "where " + Tables.ProductInfoTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetProductPriceIDByProductID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.ProductPriceTable.ID, Tables.ProductPriceTable.Table, "where " + Tables.ProductPriceTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetProductQuantityIDByProductID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.ProductQuantityTable.ID, Tables.ProductQuantityTable.Table, "where " + Tables.ProductQuantityTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetProductStockIDByProductID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.ProductStockLevelTable.ID, Tables.ProductStockLevelTable.Table, "where " + Tables.ProductStockLevelTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetProductShippingPackageIDByProductID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.ProductShippingPackageTable.ID, Tables.ProductShippingPackageTable.Table, "where " + Tables.ProductShippingPackageTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList GetProductCategoryIDsByProductID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> result = DBManager.GetIntArrayList(Tables.ProductCategoriesTable.ID, Tables.ProductCategoriesTable.Table, "where " + Tables.ProductCategoriesTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList GetProductPropertyIDsByProductID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> result = DBManager.GetIntArrayList(Tables.ProductPropertiesTable.ID, Tables.ProductPropertiesTable.Table, "where " + Tables.ProductPropertiesTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProdCatID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetProductCategoryData(int ProdCatID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.ProductCategoriesTable.Table, "where " + Tables.ProductCategoriesTable.ID + " = " + ProdCatID);
        if (!Details.isEmpty()) {
            int CatID = Integer.parseInt(Details.get(Tables.ProductCategoriesTable.CategoryID));
            String CategoryName = EngineCategoryManager.GetCategoryName(CatID);
            Details.put("CategoryName", CategoryName);
        }
        return Details;
    }

    /**
     *
     * @param ProdPropID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetProductPropertyData(int ProdPropID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.ProductPropertiesTable.Table, "where " + Tables.ProductPropertiesTable.ID + " = " + ProdPropID);
        if (!Details.isEmpty()) {
            int PropID = Integer.parseInt(Details.get(Tables.ProductPropertiesTable.PropertyID));
            int RootPropID = EngineCategoryManager.GetPropertyRootIDByPropertyID(PropID);
            if (RootPropID != 0) {
                String RootPropName = EngineCategoryManager.GetPropertyName(RootPropID);
                Details.put("RootPropName", RootPropName);
            }
            EngineCategoryManager.GetPropertyData(PropID);
            Details.putAll(EngineCategoryManager.GetPropertyData(PropID));
        }
        return Details;
    }

    /**
     *
     * @param InfoID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetProductInfoData(int InfoID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.ProductInfoTable.Table, "where " + Tables.ProductInfoTable.ID + " = " + InfoID);
        return Details;
    }

    /**
     *
     * @param PriceID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetProductPriceData(int PriceID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.ProductPriceTable.Table, "where " + Tables.ProductPriceTable.ID + " = " + PriceID);
        return Details;
    }

    /**
     *
     * @param QuantityID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetProductQuantityData(int QuantityID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.ProductQuantityTable.Table, "where " + Tables.ProductQuantityTable.ID + " = " + QuantityID);
        return Details;
    }

    /**
     *
     * @param StockLevelID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetProductStockLevelData(int StockLevelID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.ProductStockLevelTable.Table, "where " + Tables.ProductStockLevelTable.ID + " = " + StockLevelID);
        if (!Details.isEmpty()) {
            int StockNotyID = Integer.parseInt(Details.get(Tables.ProductStockLevelTable.NotificationTypeID));
            String StockNotificationName = GetStockNotificationName(StockNotyID);
            Details.put("StockNotificationName", StockNotificationName);
        }
        return Details;
    }

    /**
     *
     * @param StockNotyID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetStockNotificationName(int StockNotyID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.StockNotificationTypeTable.Name, Tables.StockNotificationTypeTable.Table, "where " + Tables.StockNotificationTypeTable.ID + " = " + StockNotyID);
        return result;
    }

    /**
     *
     * @param ShippingPackageID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static HashMap<String, String> GetProductShippingPackageData(int ShippingPackageID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, String> Details = DBManager.GetTableData(Tables.ProductShippingPackageTable.Table, "where " + Tables.ProductShippingPackageTable.ID + " = " + ShippingPackageID);
        return Details;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetProductFirstRootCatID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetFirstInt(Tables.ProductCategoriesTable.CategoryID, Tables.ProductCategoriesTable.Table, "where " + Tables.ProductCategoriesTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int GetProductStatus(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        int result = DBManager.GetInt(Tables.ProductsTable.Active, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String GetSellerProductStatus(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.SellerProductsTable.Status, Tables.SellerProductsTable.Table, "where " + Tables.SellerProductsTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param ProductID
     * @param ProductStatus
     * @param SellerProdStatus
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String DeleteProduct(int ProductID, int ProductStatus, String SellerProdStatus) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        if (ProductStatus == 0 && !SellerProdStatus.equals("Activated")) {

            ArrayList<Integer> ImageIDS = EngineImageManager.GetImageIDs(ProductID, "Product");
            if (!ImageIDS.isEmpty()) {
                for (int ImageID : ImageIDS) {
                    result = DBManager.DeleteObject(Tables.ImagesTable.Table, "where " + Tables.ImagesTable.ID + " = " + ImageID);
                }
            }
            ArrayList<Integer> PropIDS = GetProductPropertyIDsByProductID(ProductID);
            if (!PropIDS.isEmpty()) {
                for (int PropID : PropIDS) {
                    result = DBManager.DeleteObject(Tables.ProductPropertiesTable.Table, "where " + Tables.ProductPropertiesTable.ID + " = " + PropID);
                }
            }

            ArrayList<Integer> TagIDS = EngineCategoryManager.GetTags(ProductID, "Product");
            if (!TagIDS.isEmpty()) {
                for (int TagID : TagIDS) {
                    result = DBManager.DeleteObject(Tables.TagsTable.Table, "where " + Tables.TagsTable.ID + " = " + TagID);
                }
            }

            ArrayList<Integer> ProdCatIDS = GetProductCategoryIDsByProductID(ProductID);
            if (!ProdCatIDS.isEmpty()) {
                for (int ProdCatID : ProdCatIDS) {
                    result = DBManager.DeleteObject(Tables.ProductCategoriesTable.Table, "where " + Tables.ProductCategoriesTable.ID + " = " + ProdCatID);
                }
            }

            DBManager.DeleteObject(Tables.ProductShippingPackageTable.Table, "where " + Tables.ProductShippingPackageTable.ProductID + " = " + ProductID);

            DBManager.DeleteObject(Tables.ProductUnitsTable.Table, "where " + Tables.ProductUnitsTable.ProductID + " = " + ProductID);

            DBManager.DeleteObject(Tables.SellerProductsTable.Table, "where " + Tables.SellerProductsTable.ProductID + " = " + ProductID);

            DBManager.DeleteObject(Tables.ProductStockLevelTable.Table, "where " + Tables.ProductStockLevelTable.ProductID + " = " + ProductID);

            DBManager.DeleteObject(Tables.ProductQuantityTable.Table, "where " + Tables.ProductQuantityTable.ProductID + " = " + ProductID);

            DBManager.DeleteObject(Tables.ProductPriceTable.Table, "where " + Tables.ProductPriceTable.ProductID + " = " + ProductID);

            DBManager.DeleteObject(Tables.ProductInfoTable.Table, "where " + Tables.ProductInfoTable.ProductID + " = " + ProductID);

            DBManager.DeleteObject(Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " = " + ProductID);

            ArrayList<Integer> ViewIDs = DBManager.GetIntArrayList(Tables.ProductViewedTable.ID, Tables.ProductViewedTable.Table, "where " + Tables.ProductViewedTable.ProductID + " = " + ProductID);
            if (!ViewIDs.isEmpty()) {
                for (int id : ViewIDs) {
                    result = DBManager.DeleteObject(Tables.ProductViewedTable.Table, "where " + Tables.ProductViewedTable.ID + " = " + id);
                }
            }
        } else {
            result = "Please Update the Product Status to Deactivate";
        }
        return result;
    }

    /**
     *
     * @param UnitID
     * @param Option
     * @param Name
     * @param Description
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ProcessProductUnit(int UnitID, String Option, String Name, String Description) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        switch (Option) {
            case "Created":
                result = CreateUnit(Name, Description);
                break;
            case "Edited":
                result = DBManager.UpdateStringData(Tables.UnitsTable.Table, Tables.UnitsTable.Name, Name, "where " + Tables.UnitsTable.ID + " = " + UnitID);
                DBManager.UpdateStringData(Tables.UnitsTable.Table, Tables.UnitsTable.Abbreviation, Name, "where " + Tables.UnitsTable.ID + " = " + UnitID);
                DBManager.UpdateStringData(Tables.UnitsTable.Table, Tables.UnitsTable.Description, Description, "where " + Tables.UnitsTable.ID + " = " + UnitID);
                break;
            case "Deleted":
                result = DBManager.DeleteObject(Tables.UnitsTable.Table, "where " + Tables.UnitsTable.ID + " = " + UnitID);
                break;
            default:
                break;
        }
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
    public static String CreateUnit(String Name, String Description) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.UnitsTable.Name, Name);
        tableData.put(Tables.UnitsTable.Description, Description);
        tableData.put(Tables.UnitsTable.Abbreviation, Name);
        String result = DBManager.insertTableData(Tables.UnitsTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param ConditionID
     * @param Option
     * @param Name
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ProcessProductCondition(int ConditionID, String Option, String Name) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        switch (Option) {
            case "Created":
                result = CreateCondition(Name);
                break;
            case "Edited":
                result = DBManager.UpdateStringData(Tables.ProductConditionTable.Table, Tables.ProductConditionTable.Name, Name, "where " + Tables.ProductConditionTable.ID + " = " + ConditionID);
                break;
            case "Deleted":
                result = DBManager.DeleteObject(Tables.ProductConditionTable.Table, "where " + Tables.ProductConditionTable.ID + " = " + ConditionID);
                break;
            default:
                break;
        }
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
    public static String CreateCondition(String Name) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        HashMap<String, Object> tableData = new HashMap<>();
        tableData.put(Tables.ProductConditionTable.Name, Name);
        String result = DBManager.insertTableData(Tables.ProductConditionTable.Table, tableData, "");
        return result;
    }

    /**
     *
     * @param UserID
     * @param Active
     * @param Option
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetProductIDsByActiveValue(int UserID, int Active, String Option) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductsTable.ID, Tables.ProductsTable.Table, "Where " + Tables.ProductsTable.Active + " = " + Active + " ORDER BY id DESC");
        if (!IDs.isEmpty()) {
            if (UserID != 1) {
                IDs = GetSellerProductIDsOptions(IDs, UserID);
            }
        }
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
    public static ArrayList<Integer> GetProductByIDs(int UserID, int MinID, double MaxID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductsTable.ID, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " BETWEEN " + MinID + " AND " + MaxID + " ORDER BY id DESC");
        if (!IDs.isEmpty()) {
            if (UserID != 1) {
                IDs = GetSellerProductIDsOptions(IDs, UserID);
            }
        }
        return IDs;
    }

    /**
     *
     * @param UserID
     * @param MinPrice
     * @param MaxPrice
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetProductIDsByPrice(int UserID, double MinPrice, double MaxPrice) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductPriceTable.ProductID, Tables.ProductPriceTable.Table, "where " + Tables.ProductPriceTable.SellingPrice + " BETWEEN " + MinPrice + " AND " + MaxPrice + " ORDER BY id DESC");
        if (!IDs.isEmpty()) {
            if (UserID != 1) {
                IDs = GetSellerProductIDsOptions(IDs, UserID);
            }
        }
        return IDs;
    }

    /**
     *
     * @param UserID
     * @param MinQty
     * @param MaxQty
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetProductIDsByQuantity(int UserID, double MinQty, double MaxQty) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductQuantityTable.ProductID, Tables.ProductQuantityTable.Table, "where " + Tables.ProductQuantityTable.TotalQuantity + " BETWEEN " + MinQty + " AND " + MaxQty + " ORDER BY id DESC");
        if (!IDs.isEmpty()) {
            if (UserID != 1) {
                IDs = GetSellerProductIDsOptions(IDs, UserID);
            }
        }
        return IDs;
    }

    /**
     *
     * @param UserID
     * @param Name
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetProductIDsByName(int UserID, String Name) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductInfoTable.ProductID, Tables.ProductInfoTable.Table, "where " + Tables.ProductInfoTable.Name + " = '" + Name + "' ORDER BY id DESC");
        if (!IDs.isEmpty()) {
            if (UserID != 1) {
                IDs = GetSellerProductIDsOptions(IDs, UserID);
            }
        }
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
    public static ArrayList<Integer> GetProductsByCategoryID(int CatID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        ArrayList<Integer> ProdIDs = DBManager.GetIntArrayListDescending(Tables.ProductCategoriesTable.ProductID, Tables.ProductCategoriesTable.Table, "where " + Tables.ProductCategoriesTable.CategoryID + " = " + CatID + " ORDER BY id DESC");
        for (int id : ProdIDs) {
            int active = DBManager.GetInt(Tables.ProductsTable.Active, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " = " + id);
            if (active == 1) {
                IDs.add(id);
            }
        }
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetRecentlyAddedProducts(int Limit) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductsTable.ID, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.Active + " = " + 1 + " ORDER BY " + Tables.ProductsTable.ID + " DESC LIMIT " + Limit);
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetAllRecentlyAddedProducts() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductsTable.ID, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.Active + " = " + 1 + " ORDER BY " + Tables.ProductsTable.ID + " DESC");
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetTopSellingProducts() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductsTable.ID, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.Active + " = " + 1 + " ORDER BY " + Tables.ProductsTable.ID + " DESC LIMIT " + 6);
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetBestSellersProducts() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductsTable.ID, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.Active + " = " + 1 + " ORDER BY " + Tables.ProductsTable.ID + " LIMIT " + 6);
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetMostViewed() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductsTable.ID, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.Active + " = " + 1 + " ORDER BY " + Tables.ProductsTable.ID + " LIMIT " + 0 + ", " + 6);
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetFeaturedProducts() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductsTable.ID, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.Active + " = " + 1 + " ORDER BY " + Tables.ProductsTable.ID + " LIMIT " + 0 + ", " + 9);
        return IDs;
    }

    /**
     *
     * @param UserID
     * @param CatID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetProductIDsByCategoryID(int UserID, String CatID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductCategoriesTable.ProductID, Tables.ProductCategoriesTable.Table, "where " + Tables.ProductCategoriesTable.CategoryID + " = " + CatID + " ORDER BY id DESC");
        if (!IDs.isEmpty()) {
            if (UserID != 1) {
                IDs = GetSellerProductIDsOptions(IDs, UserID);
            }
        }
        return IDs;
    }

    /**
     *
     * @param UserID
     * @param Status
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetProductIDsByStatus(int UserID, String Status) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.SellerProductsTable.ProductID, Tables.SellerProductsTable.Table, "where " + Tables.SellerProductsTable.Status + " = '" + Status + "' ORDER BY id DESC");
        if (!IDs.isEmpty()) {
            if (UserID != 1) {
                IDs = GetSellerProductIDsOptions(IDs, UserID);
            }
        }
        return IDs;
    }

    /**
     *
     * @param UserID
     * @param CatName
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetProductIDsByCategoryName(int UserID, String CatName) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> CatIDs = DBManager.GetIntArrayListDescending(Tables.CategoryTable.ID, Tables.CategoryTable.Table, "where " + Tables.CategoryTable.Name + " LIKE '%" + CatName + "%' ORDER BY id DESC");
        ArrayList<Integer> IDs = new ArrayList<>();
        if (!CatIDs.isEmpty()) {
            for (int catid : CatIDs) {
                ArrayList<Integer> ProCatIDs = DBManager.GetIntArrayListDescending(Tables.ProductCategoriesTable.ProductID, Tables.ProductCategoriesTable.Table, "where " + Tables.ProductCategoriesTable.CategoryID + " = " + catid + " ORDER BY id DESC");
                if (!ProCatIDs.isEmpty()) {
                    IDs.addAll(ProCatIDs);
                }
            }
        }
        if (!IDs.isEmpty()) {
            if (UserID != 1) {
                IDs = GetSellerProductIDsOptions(IDs, UserID);
            }
        }
        IDs = UtilityManager.removeDuplicatesIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param UserID
     * @param Name
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetProductIDsBySellerName(int UserID, String Name) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> SellerUserIDs = DBManager.GetIntArrayListDescending(Tables.SellersTable.UserID, Tables.SellersTable.Table, "where " + Tables.SellersTable.Firstname + " LIKE '%" + Name + "%' OR " + Tables.SellersTable.Lastname + " LIKE '%" + Name + "%' ORDER BY id DESC");
        ArrayList<Integer> IDs = new ArrayList<>();
        if (!SellerUserIDs.isEmpty()) {
            for (int selleruserid : SellerUserIDs) {
                ArrayList<Integer> SellProdIDs = DBManager.GetIntArrayListDescending(Tables.SellerProductsTable.ProductID, Tables.SellerProductsTable.Table, "where " + Tables.SellerProductsTable.SellerUserID + " = " + selleruserid + " ORDER BY id DESC");
                if (!SellProdIDs.isEmpty()) {
                    IDs.addAll(SellProdIDs);
                }
            }
        }
        if (!IDs.isEmpty()) {
            if (UserID != 1) {
                IDs = GetSellerProductIDsOptions(IDs, UserID);
            }
        }
        IDs = UtilityManager.removeDuplicatesIntegerArrayList(IDs);
        return IDs;
    }

    /**
     *
     * @param ProdIDS
     * @param UserID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetSellerProductIDsOptions(ArrayList<Integer> ProdIDS, int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        if (!ProdIDS.isEmpty()) {
            for (int prodid : ProdIDS) {
                int sellerprodid = DBManager.GetInt(Tables.SellerProductsTable.ProductID, Tables.SellerProductsTable.Table, "where " + Tables.SellerProductsTable.SellerUserID + " = " + UserID + " And " + Tables.SellerProductsTable.ProductID + " = " + prodid);
                if (sellerprodid != 0) {
                    IDs.add(sellerprodid);
                }
            }
        }
        return IDs;
    }

    /**
     *
     * @param SellerUserID
     * @param ProductName
     * @param ProductConditionID
     * @param ProductUnitID
     * @param ReferenceCode
     * @param UPCBarcode
     * @param Description
     * @param CategoryIDs
     * @param PropertyIDs
     * @param CostPrice
     * @param SellingPrice
     * @param MimimumQuantity
     * @param TotalQuantity
     * @param PackageHeight
     * @param PackageWidth
     * @param PackageDepth
     * @param MinimumStockLevel
     * @param NotificationTypeID
     * @param ProductUnitValue
     * @param ProductTags
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static int EditProduct(int SellerUserID, String ProductName, int ProductConditionID, int ProductUnitID, String ReferenceCode,
            String UPCBarcode, String Description, String CategoryIDs, String PropertyIDs,
            double CostPrice, double SellingPrice,
            int MimimumQuantity, int TotalQuantity,
            int PackageHeight, int PackageWidth, int PackageDepth,
            int MinimumStockLevel, int NotificationTypeID,
            String ProductUnitValue, String ProductTags, int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";

        result = DBManager.UpdateIntData(Tables.ProductShippingPackageTable.PackageDepth, PackageDepth, Tables.ProductShippingPackageTable.Table, "where " + Tables.ProductShippingPackageTable.ProductID + " = " + ProductID);
        result = DBManager.UpdateIntData(Tables.ProductShippingPackageTable.PackageWidth, PackageWidth, Tables.ProductShippingPackageTable.Table, "where " + Tables.ProductShippingPackageTable.ProductID + " = " + ProductID);
        result = DBManager.UpdateIntData(Tables.ProductShippingPackageTable.PackageHeight, PackageHeight, Tables.ProductShippingPackageTable.Table, "where " + Tables.ProductShippingPackageTable.ProductID + " = " + ProductID);

        if (NotificationTypeID != 0) {
            result = DBManager.UpdateIntData(Tables.ProductStockLevelTable.NotificationTypeID, MinimumStockLevel, Tables.ProductStockLevelTable.Table, "where " + Tables.ProductStockLevelTable.ProductID + " = " + ProductID);
        }

        if (MinimumStockLevel != 0) {
            result = DBManager.UpdateIntData(Tables.ProductStockLevelTable.MinimumStockLevel, MinimumStockLevel, Tables.ProductStockLevelTable.Table, "where " + Tables.ProductStockLevelTable.ProductID + " = " + ProductID);
        }
        if (MimimumQuantity != 0) {
            result = DBManager.UpdateIntData(Tables.ProductQuantityTable.MinimumQuantity, TotalQuantity, Tables.ProductQuantityTable.Table, "where " + Tables.ProductQuantityTable.ProductID + " = " + ProductID);
        }
        if (TotalQuantity != 0) {
            result = DBManager.UpdateIntData(Tables.ProductQuantityTable.TotalQuantity, TotalQuantity, Tables.ProductQuantityTable.Table, "where " + Tables.ProductQuantityTable.ProductID + " = " + ProductID);
        }
        if (CostPrice != 0.) {
            DBManager.UpdateDoubleData(Tables.ProductPriceTable.Table, Tables.ProductPriceTable.CostPrice, CostPrice, "where " + Tables.ProductPriceTable.ProductID + " = " + ProductID);
        }

        if (SellingPrice != 0) {
            result = DBManager.UpdateDoubleData(Tables.ProductPriceTable.Table, Tables.ProductPriceTable.SellingPrice, SellingPrice, "where " + Tables.ProductPriceTable.ProductID + " = " + ProductID);
            result = DBManager.UpdateDoubleData(Tables.ProductPriceTable.Table, Tables.ProductPriceTable.BasePrice, SellingPrice, "where " + Tables.ProductPriceTable.ProductID + " = " + ProductID);
        }
        if (!ProductName.equals("")) {
            result = DBManager.UpdateStringData(Tables.ProductInfoTable.Table, Tables.ProductInfoTable.Name, ProductName, "where " + Tables.ProductInfoTable.ProductID + " = " + ProductID);
        }
        if (!Description.equals("")) {
            result = DBManager.UpdateStringData(Tables.ProductInfoTable.Table, Tables.ProductInfoTable.Description, Description, "where " + Tables.ProductInfoTable.ProductID + " = " + ProductID);
        }
        if (!UPCBarcode.equals("")) {
            result = DBManager.UpdateStringData(Tables.ProductInfoTable.Table, Tables.ProductInfoTable.UPCBarcode, UPCBarcode, "where " + Tables.ProductInfoTable.ProductID + " = " + ProductID);
        }
        if (!ReferenceCode.equals("")) {
            result = DBManager.UpdateStringData(Tables.ProductInfoTable.Table, Tables.ProductInfoTable.ReferenceCode, ReferenceCode, "where " + Tables.ProductInfoTable.ProductID + " = " + ProductID);
        }
        if (!ProductUnitValue.equals("")) {
            result = DBManager.UpdateStringData(Tables.ProductUnitsTable.Table, Tables.ProductUnitsTable.UnitValue, ProductUnitValue, "where " + Tables.ProductUnitsTable.ProductID + " = " + ProductID);
        }
        if (ProductConditionID != 0) {
            result = DBManager.UpdateIntData(Tables.ProductsTable.ProductConditionID, ProductConditionID, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " = " + ProductID);
        }
        if (ProductUnitID != 0) {
            result = DBManager.UpdateIntData(Tables.ProductUnitsTable.UnitID, ProductUnitID, Tables.ProductUnitsTable.Table, "where " + Tables.ProductUnitsTable.ID + " = " + ProductID);
        }
        String[] Tags = ProductTags.split(",");
        if (Tags.length > 0) {
            result = ComputeObjectTags(ProductTags, ProductID, "Product");
        }

        String[] PropIDs = PropertyIDs.split(",");
        if (PropIDs.length > 0) {
            result = ComputeProductProperties(PropertyIDs, ProductID);
        }
        String[] catids = CategoryIDs.split(",");
        if (catids.length > 0) {
            result = ComputeProductCategory(CategoryIDs, ProductID);
        }

        return ProductID;

    }

    public static String GetCategoryMinAndMaxPrice(int CatID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "";
        ArrayList<Integer> ProdIDs = EngineProductManager.GetProductsByCategoryID(CatID);
        double minAmount = 0.0;
        double maxAmount = 0.0;
        ArrayList<Double> Prices = new ArrayList<>();
        if (!ProdIDs.isEmpty()) {
            for (int ProdID : ProdIDs) {
                double price = GetProductSellingPrice(ProdID);
                if (price != 0) {
                    Prices.add(price);
                }
            }
        }
        if (!Prices.isEmpty()) {
            minAmount = findMin(Prices);
            maxAmount = findMax(Prices);
            result = minAmount + "-" + maxAmount;
        } else {
            result = 10 + "-" + 100;
        }

        return result;
    }

    /**
     *
     * @param list
     * @return
     */
    public static Double findMin(ArrayList<Double> list) {
        return Collections.min(list);
    }

    /**
     *
     * @param list
     * @return
     */
    public static Double findMax(ArrayList<Double> list) {
        return Collections.max(list);
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static double GetProductSellingPrice(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        double result = DBManager.GetDouble(Tables.ProductPriceTable.SellingPrice, Tables.ProductPriceTable.Table, "where " + Tables.ProductPriceTable.ProductID + " = " + ProductID);
        return result;
    }

    /**
     *
     * @param CatID
     * @param MinPrice
     * @param MaxPrice
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetProductsByPriceAndCatID(int CatID, double MinPrice, double MaxPrice) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        ArrayList<Integer> ProdIDs = DBManager.GetIntArrayListDescending(Tables.ProductCategoriesTable.ProductID, Tables.ProductCategoriesTable.Table, "where " + Tables.ProductCategoriesTable.CategoryID + " = " + CatID + " ORDER BY id DESC");
        if (!ProdIDs.isEmpty()) {
            for (int id : ProdIDs) {
                double price = GetProductSellingPrice(id);
                if (price >= MinPrice && price <= MaxPrice) {
                    IDs.add(id);
                }
            }
        }
        return IDs;
    }

    /**
     *
     * @param ProductID
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetRelatedProductsByCategoryID(int ProductID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        int CatID = GetProductFirstRootCatID(ProductID);
        ArrayList<Integer> ProdIDs = DBManager.GetIntArrayListDescending(Tables.ProductCategoriesTable.ProductID, Tables.ProductCategoriesTable.Table, "where " + Tables.ProductCategoriesTable.CategoryID + " = " + CatID + " ORDER BY " + Tables.ProductCategoriesTable.ID + " LIMIT " + 0 + ", " + 4);
        for (int id : ProdIDs) {
            int active = DBManager.GetInt(Tables.ProductsTable.Active, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " = " + id);
            if (active == 1) {
                IDs.add(id);
            }
        }
        return IDs;
    }

    /**
     *
     * @param UserID
     * @param ProductID
     * @param IpAddress
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ComputeUserProductViewed(int UserID, int ProductID, String IpAddress) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        ArrayList<Integer> ViewIDs = DBManager.GetIntArrayList(Tables.ProductViewedTable.ID, Tables.ProductViewedTable.Table, "where " + Tables.ProductViewedTable.UserID + " = " + UserID + " or " + Tables.ProductViewedTable.IpAddress + " = '" + IpAddress + "'");
        if (ViewIDs.size() >= 4) {
            int id = DBManager.GetFirstInt(Tables.ProductViewedTable.ID, Tables.ProductViewedTable.Table, "where " + Tables.ProductViewedTable.UserID + " = " + UserID + " or " + Tables.ProductViewedTable.IpAddress + " = '" + IpAddress + "'");
            DBManager.DeleteObject(Tables.ProductViewedTable.Table, "where " + Tables.ProductViewedTable.ID + " = " + id);
            CreateUserProductViewed(UserID, ProductID, IpAddress);
        } else {
            CreateUserProductViewed(UserID, ProductID, IpAddress);
        }
        return result;
    }

    public static String CreateUserProductViewed(int UserID, int ProductID, String IpAddress) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        int exitingid = DBManager.GetInt(Tables.ProductViewedTable.ID, Tables.ProductViewedTable.Table, "where " + Tables.ProductViewedTable.UserID + " = " + UserID + " and " + Tables.ProductViewedTable.ProductID + " = " + ProductID);
        exitingid = DBManager.GetInt(Tables.ProductViewedTable.ID, Tables.ProductViewedTable.Table, "where " + Tables.ProductViewedTable.IpAddress + " = '" + IpAddress + "' and " + Tables.ProductViewedTable.ProductID + " = " + ProductID);
        if (exitingid == 0) {
            HashMap<String, Object> tableData = new HashMap<>();
            tableData.put(Tables.ProductViewedTable.UserID, UserID);
            tableData.put(Tables.ProductViewedTable.ProductID, ProductID);
            tableData.put(Tables.ProductViewedTable.IpAddress, IpAddress);
            DBManager.insertTableData(Tables.ProductViewedTable.Table, tableData, "");
        }
        return result;
    }

    /**
     *
     * @param UserID
     * @param IpAddress
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetRecentlyViewedProductIDs(int UserID, String IpAddress) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductViewedTable.ProductID, Tables.ProductViewedTable.Table, "where " + Tables.ProductViewedTable.UserID + " = " + UserID + " or " + Tables.ProductViewedTable.IpAddress + " = '" + IpAddress + "'");
        return IDs;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetDetailedFeaturedProducts() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = DBManager.GetIntArrayListDescending(Tables.ProductsTable.ID, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.Active + " = " + 1 + " ORDER BY " + Tables.ProductsTable.ID + " LIMIT " + 5 + ", " + 9);
        return IDs;
    }

    /**
     *
     * @param Option
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static String ProcessProductActualPrice(String Option) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = "failed";
        ArrayList<Integer> ProdIDs = GetProductIDs(1);
        if (!ProdIDs.isEmpty()) {
            for (int id : ProdIDs) {
                if (Option.equals("Yes")) {
                    result = DBManager.UpdateIntData(Tables.ProductsTable.ShowActualPrice, 1, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " = " + id);
                } else if (Option.equals("No")) {
                    result = DBManager.UpdateIntData(Tables.ProductsTable.ShowActualPrice, 0, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " = " + id);
                }
            }
        }
        return result;
    }

    /**
     *
     * @param CatID
     * @param SortingValue
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetProductsBySorting(int CatID, String SortingValue) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        ArrayList<Integer> IDS = new ArrayList<>();
        int CatRootCat = EngineCategoryManager.GetCategoryRootIDByCategoryID(CatID);
        if (SortingValue.equals("Newest Arrival")) {
            //sort by dateGetRecentlyAddedProducts
            IDS = GetAllRecentlyAddedProducts();
        } else if (SortingValue.equals("Low to High")) {
            //sort by price low to hie
            IDS = DBManager.GetIntArrayListDescending(Tables.ProductPriceTable.ProductID, Tables.ProductPriceTable.Table, " ORDER BY " + Tables.ProductPriceTable.SellingPrice + " ASC");
        } else if (SortingValue.equals("High to Low")) {
            //sort by price low to hie
            IDS = DBManager.GetIntArrayListDescending(Tables.ProductPriceTable.ProductID, Tables.ProductPriceTable.Table, " ORDER BY " + Tables.ProductPriceTable.SellingPrice + " DESC");
        }
        if (!IDS.isEmpty()) {
            for (int id : IDS) {
                int prodcat = GetProductFirstRootCatID(id);
                if (prodcat == CatRootCat) {
                    int active = DBManager.GetInt(Tables.ProductsTable.Active, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " = " + id);
                    if (active == 1) {
                        IDs.add(id);
                    }
                }
            }
        }

        return IDs;
    }

    /**
     *
     * @param SearchValue
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetProductIDsBySearchValue(String SearchValue) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        ArrayList<Integer> IDS = new ArrayList<>();
        //By Product Name
        ArrayList<Integer> ProdIDs = DBManager.GetIntArrayListDescending(Tables.ProductInfoTable.ProductID, Tables.ProductInfoTable.Table, "where " + Tables.ProductInfoTable.Name + " LIKE '%" + SearchValue + "%' ORDER BY id DESC");
        IDs.addAll(ProdIDs);

        //By Category Name
        ArrayList<Integer> CatIDs = DBManager.GetIntArrayListDescending(Tables.CategoryTable.ID, Tables.CategoryTable.Table, "where " + Tables.CategoryTable.Name + " LIKE '%" + SearchValue + "%' ORDER BY id DESC");
        if (!CatIDs.isEmpty()) {
            for (int catid : CatIDs) {
                ArrayList<Integer> ProCatIDs = DBManager.GetIntArrayListDescending(Tables.ProductCategoriesTable.ProductID, Tables.ProductCategoriesTable.Table, "where " + Tables.ProductCategoriesTable.CategoryID + " = " + catid + " ORDER BY id DESC");
                if (!ProCatIDs.isEmpty()) {
                    IDs.addAll(ProCatIDs);
                }
            }
        }

        //By Property/Brand Name
        ArrayList<Integer> PropIDs = DBManager.GetIntArrayListDescending(Tables.PropertiesTable.ID, Tables.PropertiesTable.Table, "where " + Tables.PropertiesTable.Name + " LIKE '%" + SearchValue + "%' ORDER BY id DESC");
        if (!PropIDs.isEmpty()) {
            for (int propid : PropIDs) {
                ArrayList<Integer> ProdPropIDs = DBManager.GetIntArrayListDescending(Tables.ProductPropertiesTable.ProductID, Tables.ProductPropertiesTable.Table, "where " + Tables.ProductPropertiesTable.PropertyID + " = " + propid + " ORDER BY id DESC");
                if (!ProdPropIDs.isEmpty()) {
                    IDs.addAll(ProdPropIDs);
                }
            }
        }

        IDs = UtilityManager.removeDuplicatesIntegerArrayList(IDs);
        for (int id : IDs) {
            int active = DBManager.GetInt(Tables.ProductsTable.Active, Tables.ProductsTable.Table, "where " + Tables.ProductsTable.ID + " = " + id);
            if (active == 1) {
                IDS.add(id);
            }
        }
        return IDS;
    }

    /**
     *
     * @return @throws ClassNotFoundException
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public static ArrayList<Integer> GetDashBoardProducts(int UserID) throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        ArrayList<Integer> IDs = new ArrayList<>();
        if (UserID == 1) {
            IDs = DBManager.GetIntArrayListDescending(Tables.ProductsTable.ID, Tables.ProductsTable.Table, "ORDER BY " + Tables.ProductsTable.ID + " DESC LIMIT " + 0 + ", " + 7);
        } else {
            IDs = IDs = DBManager.GetIntArrayListDescending(Tables.SellerProductsTable.ProductID, Tables.SellerProductsTable.Table, "where " + Tables.SellerProductsTable.SellerUserID + " = " + UserID + " ORDER BY " + Tables.SellerProductsTable.ID + " DESC LIMIT " + 0 + ", " + 7);
        }
        return IDs;
    }

}
