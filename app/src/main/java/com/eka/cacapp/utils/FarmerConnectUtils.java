package com.eka.cacapp.utils;

import com.eka.cacapp.data.farmconnect.FCSortData;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public final class FarmerConnectUtils {

    public static final String  PUBLISED_PRICE_TAB ="published_price";
    public static final String  BIDS_TAB ="bids_tab";

    public static final String COLUMNID = "columnId";
    public static final String USERNAME = "username";
    public static final String COLUMNNAME = "columnName";
    public static final String COLUMNTYPE = "columnType";

    public static final String OPERATOR = "operator";
    public static final String NIN = "nin";
    public static final String IN = "in";
    public static final String TYPE = "type";
    public static final String BASIC = "basic";
    public static final String VALUE = "value";
    public static final String FILTERS = "filters";
    public static final String LIMIT = "type";
    public static final String PAGE = "basic";
    public static final String PAGINATION = "pagination";

    public static final String FC_MODE_OFFEROR = "fc_mode_offeror";
    public static final String FC_MODE_BID = "fc_mode_bid";


    public static JsonObject getFarmerConnectGenReqParamObj(String userName, String operatorVal, int limit, int page) {

        JsonObject reqParams = new JsonObject();
        JsonArray filterArr = new JsonArray();
        JsonObject filterObj = new JsonObject();
        filterObj.addProperty(COLUMNID, USERNAME);
        filterObj.addProperty(COLUMNNAME, "User Name");
        filterObj.addProperty(COLUMNTYPE, 1);
        filterObj.addProperty(OPERATOR, operatorVal);
        filterObj.addProperty(TYPE, BASIC);
        JsonArray valueArr = new JsonArray();
        valueArr.add(userName);
        filterObj.add(VALUE, valueArr);
        filterArr.add(filterObj);
        reqParams.add(FILTERS, filterArr);
        JsonObject paginationObj = new JsonObject();
        paginationObj.addProperty(LIMIT, limit);
        paginationObj.addProperty(PAGE, page);
        reqParams.add(PAGINATION, paginationObj);

        return reqParams;

    }

    public static JsonObject getFcReqParamsWithStatus(String statusValue, String userName,
                                                      String operatorVal, int limit, int page) {
        JsonObject filterObj = new JsonObject();
        filterObj.addProperty(COLUMNID, "status");
        filterObj.addProperty(OPERATOR, IN);
        filterObj.addProperty(TYPE, BASIC);
        JsonArray valueArr = new JsonArray();
        valueArr.add(statusValue);
        filterObj.add(VALUE, valueArr);
        JsonObject reqParam = getFarmerConnectGenReqParamObj(userName, operatorVal, limit, page);
        reqParam.getAsJsonArray(FILTERS).add(filterObj);
        return reqParam;
    }

    public static ArrayList<FCSortData> getFcSortFields() {

        ArrayList<FCSortData> listSorData = new ArrayList<>();
        listSorData.add(new FCSortData("product", "Product", 1));

        listSorData.add(new FCSortData("quality", "Quality", 1));
        listSorData.add(new FCSortData("incoTerm", "Incoterm", 1));
        listSorData.add(new FCSortData("cropYear", "Crop Year", 1));

        listSorData.add(new FCSortData("location", "Location", 1));
        listSorData.add(new FCSortData("publishedPrice", "Published Price", 2));

        listSorData.add(new FCSortData("username", "User Name", 1));
        //  listSorData.add( FCSortData("quantity","Quantity",2))

        listSorData.add(new FCSortData("paymentTerms", "Payment Terms", 1));
        listSorData.add(new FCSortData("packingSize", "Packing Size", 1));
        listSorData.add(new FCSortData("packingType", "Packing Type", 1));

        return listSorData;
    }

    public static String miliiSecToMonthYear(long inputMilliSec) {

        Date date = new Date(inputMilliSec);
        SimpleDateFormat monthDate = new SimpleDateFormat("MMM-yyyy", Locale.ENGLISH);
        return monthDate.format(date);
    }

    public static String milliSecToDate(long inputMilliSec) {

        Date date = new Date(inputMilliSec);
        SimpleDateFormat monthDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return monthDate.format(date);
    }

    public static String milliSecToFcUpdtDate(long inputMilliSec) {

        try {
            Date date = new Date(inputMilliSec);
            SimpleDateFormat monthDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            return monthDate.format(date);
        }catch (Exception e){

            return "";
        }

    }


    public static String milliSecToBidLogDate(long inputMilliSec) {

        Date date = new Date(inputMilliSec);
        SimpleDateFormat monthDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
        return monthDate.format(date);
    }
}
