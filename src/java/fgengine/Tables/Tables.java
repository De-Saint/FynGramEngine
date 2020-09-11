/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Tables;

/**
 *
 * @author mac
 */
public class Tables {

    /**
     *
     */
    public static class ActivityTable {

        /**
         *
         */
        public static String Table = "fg_activities";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String UserID = "userid";

        /**
         *
         */
        public static String Category = "category";

        /**
         *
         */
        public static String Topic = "topic";

        /**
         *
         */
        public static String Details = "details";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";

        /**
         *
         */
        public static String Deleted = "deleted";
    }

    /**
     *
     */
    public static class AddressBusStopTable {

        /**
         *
         */
        public static String Table = "fg_address_busstop";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String StateID = "stateid";

        /**
         *
         */
        public static String LgaID = "lgaid";

        /**
         *
         */
        public static String TownID = "townid";
    }

    /**
     *
     */
    public static class AddressDetailsTable {

        /**
         *
         */
        public static String Table = "fg_address_details";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String UserID = "userid";

        /**
         *
         */
        public static String AddressTypeID = "address_type_id";

        /**
         *
         */
        public static String CountryID = "countryid";

        /**
         *
         */
        public static String StateID = "stateid";

        /**
         *
         */
        public static String LgaID = "lgaid";

        /**
         *
         */
        public static String TownID = "townid";

        /**
         *
         */
        public static String BusStopID = "busstopid";

        /**
         *
         */
        public static String StreetID = "streetid";

        /**
         *
         */
        public static String PostalCode = "postal_code";

        /**
         *
         */
        public static String AddressLine = "address_line";
        /**
         *
         */
        public static String CloseTo = "close_to";

        /**
         *
         */
        public static String FullAddress = "full_address";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";

        /**
         *
         */
        public static String DefaultAddress = "default_address";
        /**
         *
         */
        public static String Phone = "phone";
    }

    /**
     *
     */
    public static class AddressLGATable {

        /**
         *
         */
        public static String Table = "fg_address_lga";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String StateID = "stateid";
    }

    /**
     *
     */
    public static class AddressStateTable {

        /**
         *
         */
        public static String Table = "fg_address_state";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String CountryID = "countryid";
    }

    /**
     *
     */
    public static class AddressCountryTable {

        /**
         *
         */
        public static String Table = "fg_address_country";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";
    }

    /**
     *
     */
    public static class AddressStreetTable {

        /**
         *
         */
        public static String Table = "fg_address_street";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String StateID = "stateid";

        /**
         *
         */
        public static String LgaID = "lgaid";

        /**
         *
         */
        public static String TownID = "townid";

        /**
         *
         */
        public static String BusStopID = "busstopid";
    }

    /**
     *
     */
    public static class AddressTownTable {

        /**
         *
         */
        public static String Table = "fg_address_town";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String StateID = "stateid";

        /**
         *
         */
        public static String LgaID = "lgaid";
    }

    /**
     *
     */
    public static class AddressTypeTable {

        /**
         *
         */
        public static String Table = "fg_address_type";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";
    }

    /**
     *
     */
    public static class AdminTable {

        /**
         *
         */
        public static String Table = "fg_admin";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String UserID = "userid";

        /**
         *
         */
        public static String Firstname = "firstname";

        /**
         *
         */
        public static String Lastname = "lastname";

        /**
         *
         */
        public static String PermissionGroupID = "permission_group_id";
    }

    /**
     *
     */
    public static class BankDetailsTable {

        /**
         *
         */
        public static String Table = "fg_bank_details";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String UserID = "userid";

        /**
         *
         */
        public static String BankID = "bankid";

        /**
         *
         */
        public static String AccountNumber = "account_number";

        /**
         *
         */
        public static String AccountType = "account_type";
    }

    /**
     *
     */
    public static class BanksTable {

        /**
         *
         */
        public static String Table = "fg_banks";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";
    }

    /**
     *
     */
    public static class ShippingTable {

        /**
         *
         */
        public static String Table = "fg_shipping";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";
        /**
         *
         */
        public static String Phone = "phone";
        /**
         *
         */
        public static String Email = "email";

        /**
         *
         */
        public static String DeliveryInterval = "delivery_interval";

        /**
         *
         */
        public static String ShippingMethodPercentage = "shipping_method_percentage";

        /**
         *
         */
        public static String AdminShippingPercentage = "admin_shipping_percentage";
        /**
         *
         */
        public static String TotalEarnings = "total_earnings";
        /**
         *
         */
        public static String NumberOfDelivery = "number_of_delivery";
        /**
         *
         */
        public static String DateAdded = "date_added";
    }

    /**
     *
     */
    public static class CartTable {

        /**
         *
         */
        public static String Table = "fg_cart";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String UserID = "userid";

        /**
         *
         */
        public static String Amount = "amount";

        /**
         *
         */
        public static String ProductCount = "product_count";

        /**
         *
         */
        public static String Status = "status";

        /**
         *
         */
        public static String DiscountCodeID = "discount_code_id";

        /**
         *
         */
        public static String DiscountedAmount = "discounted_amount"; //the discount amount after the discount operation

        /**
         *
         */
        public static String DiscountAmount = "discount_amount"; //the discount amount involved

        /**
         *
         */
        public static String ShippingTypeID = "shipping_type_id";

        /**
         *
         */
        public static String ShippingAddressID = "shipping_address_id";
        /**
         *
         */
        public static String Fees = "fees";

        /**
         *
         */
        public static String TotalAmount = "total_amount";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";
    }

    /**
     *
     */
    public static class CartProductDetailsTable {

        /**
         *
         */
        public static String Table = "fg_cart_product_details";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String CartID = "cartid";

        /**
         *
         */
        public static String ProductID = "productid";

        /**
         *
         */
        public static String ProductPrice = "product_price";

        /**
         *
         */
        public static String ProductQuantity = "product_quantity";
    }

    /**
     *
     */
    public static class CashoutTable {

        /**
         *
         */
        public static String Table = "fg_cashout";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String UserID = "userid";

        /**
         *
         */
        public static String Amount = "amount";

        /**
         *
         */
        public static String RequestDate = "request_date";

        /**
         *
         */
        public static String RequestTime = "request_time";

        /**
         *
         */
        public static String Status = "status";

        /**
         *
         */
        public static String BankDetailsID = "bankdetails_id";
    }

    /**
     *
     */
    public static class CategoryTable {

        /**
         *
         */
        public static String Table = "fg_category";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String Description = "description";

        /**
         *
         */
        public static String ParentID = "parentid";

        /**
         *
         */
        public static String IsRootCategory = "isroot_category";

        /**
         *
         */
        public static String ImageID = "imageid";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";
    }

    /**
     *
     */
    public static class CategoryPropertiesTable {

        /**
         *
         */
        public static String Table = "fg_category_properties";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String CategoryID = "categoryid";

        /**
         *
         */
        public static String RootPropertyID = "rootpropertyid";

        /**
         *
         */
        public static String PropertyID = "propertyid";

    }

    /**
     *
     */
    public static class ComplaintTable {

        /**
         *
         */
        public static String Table = "fg_complaint";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String UserID = "userid";

        /**
         *
         */
        public static String Subject = "subject";

        /**
         *
         */
        public static String Description = "description";

        /**
         *
         */
        public static String Status = "status";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";
    }

    /**
     *
     */
    public static class CustomersTable {

        /**
         *
         */
        public static String Table = "fg_customers";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String UserID = "userid";

        /**
         *
         */
        public static String Firstname = "firstname";

        /**
         *
         */
        public static String Lastname = "lastname";
    }

    /**
     *
     */
    public static class DiscountCodesTable {

        /**
         *
         */
        public static String Table = "fg_discount_codes";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String Description = "description";

        /**
         *
         */
        public static String Code = "code";

        /**
         *
         */
        public static String DiscountCodeTypeID = "discount_code_type_id";

        /**
         *
         */
        public static String DiscountCodeObjectID = "discount_code_object_id";

        /**
         *
         */
        public static String DiscountDeductionTypeID = "discount_deduction_type_id";

        /**
         *
         */
        public static String DeductionValue = "deduction_value";

        /**
         *
         */
        public static String TotalCreated = "total_created";

        /**
         *
         */
        public static String TotalAvailable = "total_available";

        /**
         *
         */
        public static String StartDate = "start_date";

        /**
         *
         */
        public static String ExpiryDate = "expiry_date";

        /**
         *
         */
        public static String Active = "active";
        /**
         *
         */
        public static String SplitDeductionValue = "split_deduction_value";
    }

    /**
     *
     */
    public static class DiscountCodeTypesTable {

        /**
         *
         */
        public static String Table = "fg_discount_code_types";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";
    }

    /**
     *
     */
    public static class DiscountCodeObjectTable {

        /**
         *
         */
        public static String Table = "fg_discount_code_object";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";
    }

    /**
     *
     */
    public static class DiscountCodeDeductionType {

        /**
         *
         */
        public static String Table = "fg_discount_code_deduction_type";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String Description = "description";
    }

    /**
     *
     */
    public static class CustomerDiscountCodesTable {

        /**
         *
         */
        public static String Table = "fg_customer_discount_codes";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String CustomerUserID = "customer_userid";

        /**
         *
         */
        public static String DiscountCodeID = "discount_code_id";

        /**
         *
         */
        public static String TotalAvailable = "total_available";

        /**
         *
         */
        public static String Status = "status";
    }

    /**
     *
     */
    public static class GuestTable {

        /**
         *
         */
        public static String Table = "fg_guest";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String IPAddress = "ipaddress";

        /**
         *
         */
        public static String ComputerName = "computer_name";

        /**
         *
         */
        public static String Email = "email";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";

        /**
         *
         */
        public static String Location = "location";

        /**
         *
         */
        public static String OperatingSystem = "operating_system";
    }

    /**
     *
     */
    public static class ImagesTable {

        /**
         *
         */
        public static String Table = "fg_images";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String ImageText = "image_text";

        /**
         *
         */
        public static String ObjectID = "objectid";

        /**
         *
         */
        public static String ObjectType = "objecttype";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";
    }

    /**
     *
     */
    public static class MessagesTable {

        /**
         *
         */
        public static String Table = "fg_messages";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String FromUserID = "from_userid";

        /**
         *
         */
        public static String ToUserID = "to_userid";

        /**
         *
         */
        public static String Subject = "subject";

        /**
         *
         */
        public static String Body = "body";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";

        /**
         *
         */
        public static String IsRead = "is_read";

        /**
         *
         */
        public static String Deleted = "deleted";
    }

    /**
     *
     */
    public static class NewFeatureRequestTable {

        /**
         *
         */
        public static String Table = "fg_new_feature_request";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String Email = "email";

        /**
         *
         */
        public static String Description = "description";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";
        /**
         *
         */
        public static String Status = "status";
    }

    /**
     *
     */
    public static class OrderHistoryTable {

        /**
         *
         */
        public static String Table = "fg_order_history";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String OrderID = "orderid";

        /**
         *
         */
        public static String ProductID = "productid";

        /**
         *
         */
        public static String Price = "price";

        /**
         *
         */
        public static String Quantity = "quantity";

        /**
         *
         */
        public static String SellerUserID = "seller_userid";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";
        /**
         *
         */
        public static String TrackingNumber = "tracking_number";
    }

    /**
     *
     */
    public static class OrderPaymentsTable {

        /**
         *
         */
        public static String Table = "fg_order_payments";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String OrderReference = "order_reference";

        /**
         *
         */
        public static String Amount = "amount";

        /**
         *
         */
        public static String PaymentMethod = "payment_method";

        /**
         *
         */
        public static String ReferenceCode = "reference_code";
    }

    /**
     *
     */
    public static class OrderInvoicesTable {

        /**
         *
         */
        public static String Table = "fg_order_invoices";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String InvoiceNumber = "number";

        /**
         *
         */
        public static String OrderID = "orderid";

        /**
         *
         */
        public static String Amount = "amount";

        /**
         *
         */
        public static String ShippingTypeID = "shipping_type_id";

        /**
         *
         */
        public static String ShippingAddressID = "shipping_address_id";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";
    }

    /**
     *
     */
    public static class OrderStatusTable {

        /**
         *
         */
        public static String Table = "fg_order_status";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String SendEmail = "send_email";

        /**
         *
         */
        public static String Color = "color";
    }

    /**
     *
     */
    public static class OrderCancelRulesTable {

        /**
         *
         */
        public static String Table = "fg_order_cancel_rules";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Percent = "percent";

        /**
         *
         */
        public static String EnforceRule = "enforce_rule";

    }

    /**
     *
     */
    public static class OrderShippingMethodTable {

        /**
         *
         */
        public static String Table = "fg_order_shipping_method";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String OrderID = "orderid";

        /**
         *
         */
        public static String ShippingMethodID = "shipping_method_id";

    }

    /**
     *
     */
    public static class OrdersTable {

        /**
         *
         */
        public static String Table = "fg_orders";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Reference = "reference";

        /**
         *
         */
        public static String ShippingTypeID = "shipping_type_id";

        /**
         *
         */
        public static String CustomerUserID = "customer_userid";

        /**
         *
         */
        public static String SellerUserID = "seller_userid";

        /**
         *
         */
        public static String SellerAmount = "seller_amount";

        /**
         *
         */
        public static String OrderAmount = "order_amount";

        /**
         *
         */
        public static String TotalPaid = "total_paid";

        /**
         *
         */
        public static String ShippingAddressID = "shipping_address_id";

        /**
         *
         */
        public static String DeliveryFees = "delivery_fees";

        /**
         *
         */
        public static String BookingDate = "booking_date";

        /**
         *
         */
        public static String BookingTime = "booking_time";

        /**
         *
         */
        public static String DiscountCodeID = "discount_code_id";

        /**
         *
         */
        public static String DiscountedAmount = "discounted_amount";

        /**
         *
         */
        public static String DiscountAmount = "discount_amount";

        /**
         *
         */
        public static String PaymentStatusID = "payment_status_id";

        /**
         *
         */
        public static String CartID = "cartid";

        /**
         *
         */
        public static String Message = "message";

        /**
         *
         */
        public static String ApprovedByUserid = "approved_by_userid";

        /**
         *
         */
        public static String Deleted = "deleted";

        /**
         *
         */
        public static String DeliveryDate = "delivery_date";

        /**
         *
         */
        public static String DeliveryTime = "delivery_time";
    }

    /**
     *
     */
    public static class PasswordRecoveryTable {

        /**
         *
         */
        public static String Table = "fg_password_recovery";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String UserID = "userid";

        /**
         *
         */
        public static String Code = "code";

        /**
         *
         */
        public static String Email = "email";

        /**
         *
         */
        public static String DateUpdated = "date_updated";

        /**
         *
         */
        public static String TimeUpdated = "time_updated";

    }


    /**
     *
     */
    public static class PaymentsTable {

        /**
         *
         */
        public static String Table = "fg_payments";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String UserID = "userid";

        /**
         *
         */
        public static String PaymentType = "payment_type";

        /**
         *
         */
        public static String Amount = "amount";

        /**
         *
         */
        public static String TransactionCode = "transaction_code";

        /**
         *
         */
        public static String ReferenceCode = "reference_code";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";

        /**
         *
         */
        public static String Deleted = "deleted";
    }

    /**
     *
     */
    public static class ParametersTable {

        /**
         *
         */
        public static String Table = "fg_parameters";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String PaystackSecretKey = "paystack_secret_key";

        /**
         *
         */
        public static String PaystackPublicKey = "paystack_public_key";
        /**
         *
         */
        public static String SendGridKey = "sendgrid_key";

    }

    /**
     *
     */
    public static class PermissionGroupsTable {

        /**
         *
         */
        public static String Table = "fg_permission_groups";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String Permissions = "permissions";
    }

    /**
     *
     */
    public static class PermissionsTable {

        /**
         *
         */
        public static String Table = "fg_permissions";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";
    }

    /**
     *
     */
    public static class PickupStationTable {

        /**
         *
         */
        public static String Table = "fg_pickup_station";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String PickupTownID = "pickup_town_id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String Phone = "phone";

        /**
         *
         */
        public static String Email = "email";

        /**
         *
         */
        public static String OpeningHours = "opening_hours";

        /**
         *
         */
        public static String Fees = "fees";

        /**
         *
         */
        public static String SellerPickupPercentage = "seller_pickup_percentage";

        /**
         *
         */
        public static String AdminPickupPercentage = "admin_pickup_percentage";

    }

    /**
     *
     */
    public static class PickupStationAddress {

        /**
         *
         */
        public static String Table = "fg_pickup_station_address";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String PickupStationID = "pickup_station_id";

        /**
         *
         */
        public static String CloseTo = "close_to";

        /**
         *
         */
        public static String FullAddress = "fulladdress";

    }

    /**
     *
     */
    public static class PickUpTownTable {

        /**
         *
         */
        public static String Table = "fg_pickup_town";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String PickupRegionID = "pickup_region_id";

        /**
         *
         */
        public static String TownID = "townid";
    }

    /**
     *
     */
    public static class PickUpRegionTable {

        /**
         *
         */
        public static String Table = "fg_pickup_region";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String StateID = "stateid";
    }

    /**
     *
     */
    public static class ProductCategoriesTable {

        /**
         *
         */
        public static String Table = "fg_product_categories";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String ProductID = "productid";

        /**
         *
         */
        public static String CategoryID = "categoryid";
    }

    /**
     *
     */
    public static class ProductPropertiesTable {

        /**
         *
         */
        public static String Table = "fg_product_properties";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String ProductID = "productid";

        /**
         *
         */
        public static String PropertyID = "propertyid";
    }

    /**
     *
     */
    public static class ProductConditionTable {

        /**
         *
         */
        public static String Table = "fg_product_condition";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String Count = "count";
    }

    /**
     *
     */
    public static class ProductInfoTable {

        /**
         *
         */
        public static String Table = "fg_product_info";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String ProductID = "productid";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String ReferenceCode = "reference_code";

        /**
         *
         */
        public static String UPCBarcode = "upc_barcode";

        /**
         *
         */
        public static String Description = "description";
    }

    /**
     *
     */
    public static class ProductPriceTable {

        /**
         *
         */
        public static String Table = "fg_product_price";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String ProductID = "productid";

        /**
         *
         */
        public static String CostPrice = "cost_price";

        /**
         *
         */
        public static String SellingPrice = "selling_price";

        /**
         *
         */
        public static String BasePrice = "base_price";
    }

    /**
     *
     */
    public static class ProductQuantityTable {

        /**
         *
         */
        public static String Table = "fg_product_quantity";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String ProductID = "productid";

        /**
         *
         */
        public static String MinimumQuantity = "minimum_quantity";

        /**
         *
         */
        public static String TotalQuantity = "total_quantity";
    }

    /**
     *
     */
    public static class ProductShippingPackageTable {

        /**
         *
         */
        public static String Table = "fg_product_shipping_package";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String ProductID = "productid";

        /**
         *
         */
        public static String PackageHeight = "package_height";

        /**
         *
         */
        public static String PackageWidth = "package_width";

        /**
         *
         */
        public static String PackageDepth = "package_depth";
    }

    /**
     *
     */
    public static class ProductStockLevelTable {

        /**
         *
         */
        public static String Table = "fg_product_stock_level";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String ProductID = "productid";

        /**
         *
         */
        public static String MinimumStockLevel = "minimum_stock_level";

        /**
         *
         */
        public static String NotificationTypeID = "notification_type_id";
    }

    /**
     *
     */
    public static class UnitsTable {

        /**
         *
         */
        public static String Table = "fg_units";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String Abbreviation = "abbreviation";

        /**
         *
         */
        public static String Description = "description";
    }

    /**
     *
     */
    public static class ProductUnitsTable {

        /**
         *
         */
        public static String Table = "fg_product_units";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String ProductID = "productid";

        /**
         *
         */
        public static String UnitID = "unitid";

        /**
         *
         */
        public static String UnitValue = "value";
    }

    /**
     *
     */
    public static class ProductsTable {

        /**
         *
         */
        public static String Table = "fg_products";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String ProductConditionID = "product_condition_id";

        /**
         *
         */
        public static String Active = "active";

        /**
         *
         */
        public static String Time = "time";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String ShowCondition = "show_condition";

        /**
         *
         */
        public static String ShowActualPrice = "show_actual_price";
    }

    /**
     *
     */
    public static class PropertiesTable {

        /**
         *
         */
        public static String Table = "fg_properties";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String ParentID = "parentid";

        /**
         *
         */
        public static String IsRootProperty = "isroot_property";

    }

    /**
     *
     */
    public static class ReviewsTable {

        /**
         *
         */
        public static String Table = "fg_reviews";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String RateValue = "rate_value";

        /**
         *
         */
        public static String UserID = "userid";

        /**
         *
         */
        public static String ObjectID = "objectid";

        /**
         *
         */
        public static String ObjectType = "objecttype";

        /**
         *
         */
        public static String Comment = "comment";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";

        /**
         *
         */
        public static String Deleted = "deleted";

    }

    /**
     *
     */
    public static class SellerInfoTable {

        /**
         *
         */
        public static String Table = "fg_seller_info";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String SellerUserID = "seller_userid";

        /**
         *
         */
        public static String BusinessName = "business_name";

        /**
         *
         */
        public static String BusinessEmail = "business_email";

        /**
         *
         */
        public static String BusinessPhone = "business_phone";

        /**
         *
         */
        public static String BusinessImageID = "business_imageid";

        /**
         *
         */
        public static String MinimumShippingDays = "shipping_days_min";

        /**
         *
         */
        public static String MaximumShippingDays = "shipping_days_max";

    }

    /**
     *
     */
    public static class SellerProductsTable {

        /**
         *
         */
        public static String Table = "fg_seller_products";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String SellerUserID = "seller_userid";

        /**
         *
         */
        public static String ProductID = "productid";

        /**
         *
         */
        public static String Status = "status";

        /**
         *
         */
        public static String Note = "note";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";

    }

    /**
     *
     */
    public static class SellerSubscriptionTable {

        /**
         *
         */
        public static String Table = "fg_seller_subscription";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String SellerUserID = "seller_userid";

        /**
         *
         */
        public static String SellerTypeID = "seller_type_id";

        /**
         *
         */
        public static String SubscriptionTypeID = "subscription_type_id";

        /**
         *
         */
        public static String Amount = "amount";

        /**
         *
         */
        public static String StartDate = "start_date";

        /**
         *
         */
        public static String StartTime = "start_time";

        /**
         *
         */
        public static String EndDate = "end_date";

        /**
         *
         */
        public static String Status = "status";

    }

    /**
     *
     */
    public static class SellerSubscriptionTypesTable {

        /**
         *
         */
        public static String Table = "fg_seller_subscription_types";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String Description = "description";

        /**
         *
         */
        public static String Active = "active";

        /**
         *
         */
        public static String DurationInMonths = "duration_in_months";

    }

    /**
     *
     */
    public static class SellerSubscriptionAmountTable {

        /**
         *
         */
        public static String Table = "fg_seller_subscription_amount";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String SellerTypeID = "seller_type_id";

        /**
         *
         */
        public static String SellerSubscriptionTypeID = "seller_subscription_type_id";

        /**
         *
         */
        public static String Amount = "amount";

    }

    /**
     *
     */
    public static class SellerTypesTable {

        /**
         *
         */
        public static String Table = "fg_seller_types";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String AdminTransactionPercentage = "admin_transaction_percentage";

    }

    /**
     *
     */
    public static class SellersTable {

        /**
         *
         */
        public static String Table = "fg_sellers";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String UserID = "userid";

        /**
         *
         */
        public static String Firstname = "firstname";

        /**
         *
         */
        public static String Lastname = "lastname";

        /**
         *
         */
        public static String SubscriptionTypeID = "subscription_type_id";

        /**
         *
         */
        public static String SellerTypeID = "seller_type_id";

        /**
         *
         */
        public static String Status = "status";

        /**
         *
         */
        public static String Active = "active";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";

    }

    /**
     *
     */
    public static class StockMovementTable {

        /**
         *
         */
        public static String Table = "fg_stock_movement";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String OrderID = "orderid";

        /**
         *
         */
        public static String ProductID = "productid";

        /**
         *
         */
        public static String SellerUserID = "seller_userid";

        /**
         *
         */
        public static String CustomerUserID = "customer_userid";

        /**
         *
         */
        public static String ProductPreviousQuantity = "product_previous_quantity";

        /**
         *
         */
        public static String ProductQuantity = "product_quantity";

        /**
         *
         */
        public static String ProductCurrentQuantity = "product_current_quantity";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";

    }

    /**
     *
     */
    public static class StockNotificationTypeTable {

        /**
         *
         */
        public static String Table = "fg_stock_notification_type";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

    }

    /**
     *
     */
    public static class TagsTable {

        /**
         *
         */
        public static String Table = "fg_tags";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String ObjectID = "objectid";

        /**
         *
         */
        public static String ObjectType = "objecttype";

    }

    /**
     *
     */
    public static class TransactionTypesTable {

        /**
         *
         */
        public static String Table = "fg_transaction_types";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        /**
         *
         */
        public static String TransactionCode = "code";

        /**
         *
         */
        public static String Description = "description";

    }

    /**
     *
     */
    public static class TransactionsTable {

        /**
         *
         */
        public static String Table = "fg_transactions";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String TransactionCode = "transaction_code";

        /**
         *
         */
        public static String Reference = "reference";

        /**
         *
         */
        public static String FromUserID = "from_userid";

        /**
         *
         */
        public static String ToUserID = "to_userid";

        /**
         *
         */
        public static String Amount = "amount";

        /**
         *
         */
        public static String PrimaryWalletNumber = "primary_wallet_number";

        /**
         *
         */
        public static String CreditWalletNumber = "credit_wallet_number";

        /**
         *
         */
        public static String DebitWalletNumber = "debit_wallet_number";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";

        /**
         *
         */
        public static String Description = "description";

        /**
         *
         */
        public static String ToUserOldBalance = "to_userid_old_balance";

        /**
         *
         */
        public static String ToUserNewBalance = "to_userid_new_balance";

        /**
         *
         */
        public static String FromUserOldBalance = "from_userid_old_balance";

        /**
         *
         */
        public static String FromUserNewBalance = "from_userid_new_balance";

    }

    /**
     *
     */
    public static class UserTypesTable {

        /**
         *
         */
        public static String Table = "fg_user_types";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

        public static String Abbreviation = "abbrev";

    }

    /**
     *
     */
    public static class UsersTable {

        /**
         *
         */
        public static String Table = "fg_users";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Email = "email";

        /**
         *
         */
        public static String Phone = "phone";

        /**
         *
         */
        public static String Password = "password";

        /**
         *
         */
        public static String Newsletters = "newsletters";

        /**
         *
         */
        public static String Online = "online";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";

        /**
         *
         */
        public static String UserType = "usertype"; //int 11

        /**
         *
         */
        public static String Gender = "gender";

        /**
         *
         */
        public static String DeviceToken = "device_token";
         /**
         *
         */
        public static String Status = "status";

    }

    /**
     *
     */
    public static class WalletTable {

        /**
         *
         */
        public static String Table = "fg_wallet";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String UserID = "userid";

        /**
         *
         */
        public static String Balance = "balance";

        /**
         *
         */
        public static String WalletPin = "wallet_pin";

        /**
         *
         */
        public static String WalletNumber = "wallet_number";

    }

    /**
     *
     */
    public static class WalletTypesTable {

        /**
         *
         */
        public static String Table = "fg_wallet_types";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

    }

    /**
     *
     */
    public static class WishlistTable {

        /**
         *
         */
        public static String Table = "fg_wishlist";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String UserID = "userid";

        /**
         *
         */
        public static String Amount = "amount";

        /**
         *
         */
        public static String ProductCount = "product_count";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";

    }

    /**
     *
     */
    public static class WishlistProductDetailsTable {

        /**
         *
         */
        public static String Table = "fg_wishlist_product_details";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String WishlistID = "wishlistid";

        /**
         *
         */
        public static String ProductID = "productid";

        /**
         *
         */
        public static String ProductPrice = "product_price";

        /**
         *
         */
        public static String ProductQuantity = "product_quantity";

    }

    /**
     *
     */
    public static class ShippingTypeTable {

        /**
         *
         */
        public static String Table = "fg_shipping_type";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String Name = "name";

    }

    /**
     *
     */
    public static class SessionTable {

        /**
         *
         */
        public static String Table = "fg_session";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String SessionID = "sessionid";

        /**
         *
         */
        public static String LoginID = "loginid";

        /**
         *
         */
        public static String Date = "date";

        /**
         *
         */
        public static String Time = "time";

        /**
         *
         */
        public static String VisitCount = "visit_count";
        /**
         *
         */
        public static String IPAddress = "ipaddress";
    }

    /**
     *
     */
    public static class ShippingFeesTable {

        /**
         *
         */
        public static String Table = "fg_shipping_fees";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String MinCartAmount = "min_cart_amount";
        /**
         *
         */
        public static String MaxCartAmount = "max_cart_amount";

        /**
         *
         */
        public static String DeliveryFees = "delivery_fees";

    }
    /**
     *
     */
    public static class OrderStatusHistoryTable {

        /**
         *
         */
        public static String Table = "fg_order_status_history";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String OrderID = "orderid";
        /**
         *
         */
        public static String StatusID = "statusid";
        /**
         *
         */
        public static String Date = "date";
        /**
         *
         */
        public static String Time = "time";

    }
    /**
     *
     */
    public static class SellerPaymentIntervalTable {

        /**
         *
         */
        public static String Table = "fg_seller_payment_interval";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String HourIntervalCheck = "hour_interval_check";
        /**
         *
         */
        public static String Time = "time";
         /**
         *
         */
        public static String HourPaymentDelay = "hour_payment_delay";

    }
      /**
     *
     */
    public static class ProductViewedTable {

        /**
         *
         */
        public static String Table = "fg_product_viewed";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String ProductID = "productid";

        /**
         *
         */
        public static String UserID = "userid";
        /**
         *
         */
        public static String IpAddress = "ipaddress";

    }
    
      /**
     *
     */
    public static class ShippingPaymentHistoryTable {

        /**
         *
         */
        public static String Table = "fg_shipping_payment_history";

        /**
         *
         */
        public static String ID = "id";

        /**
         *
         */
        public static String ShippingID = "shippingid";

        /**
         *
         */
        public static String Amount = "amount";
        /**
         *
         */
        public static String Date = "date";
        
         /**
         *
         */
        public static String Time = "time";
         /**
         *
         */
        public static String DeliveryCount = "delivery_count";

    }

}
