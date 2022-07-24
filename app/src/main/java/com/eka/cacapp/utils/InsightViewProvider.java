package com.eka.cacapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.eka.cacapp.R;
import com.eka.cacapp.data.insight.ChartParams;
import com.eka.cacapp.data.insight.InsightCardValue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class InsightViewProvider {

    public static LinearLayout provideOptionsForWebViewChartType(ChartParams chartParams, Context context,
                                                               String platformUrl,String token,
                                                                 String chartType,
                                                                 String deviceId,
                                                                 String filterValue,
                                                                 Boolean mapNavigation) {

        LinearLayout ll = createLinearLay(context,
                LinearLayout.VERTICAL, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);

        final WebView web = new WebView(context);





//        String url = platformUrl+"/apps/WebviewApp?dId="+chartParams.getDataViewId()+"&chartType="+chartType+"&deviceId="+deviceId+"&showToolbar=false";
        String url = platformUrl+"/apps/WebviewApp";


        web.setWebViewClient(new ChartWebViewClient());
        web.getSettings().setJavaScriptEnabled(true);

        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setDatabaseEnabled(true);
        web.getSettings().setAllowContentAccess(true);
        web.getSettings().setAllowFileAccess(true);

        String mimeType = "text/html";

        String encoding = "utf-8";


        String injection = "<script type='text/javascript'>" +
                "sessionStorage.setItem('" + "accessToken" + "',  '" + token + "');" +
                "sessionStorage.setItem('" + "dId" + "',  '" + chartParams.getDataViewId() + "');" +
                "sessionStorage.setItem('" + "chartType" + "',  '" + chartType + "');" +
                "sessionStorage.setItem('" + "deviceId" + "',  '" + deviceId + "');" +
                "sessionStorage.setItem('" + "showToolbar" + "',  '"+false+"');" +
                "sessionStorage.setItem('" + "filters" + "',  '" + filterValue + "');" ;

        if (chartType == "DotMap") {
            injection = injection + "sessionStorage.setItem('" + "mapNavigation" + "',  '" + mapNavigation + "');" ;
        }

        injection = injection +
                "window.location.replace('"+url+"');" +
                "</script>";

        web.loadDataWithBaseURL(url,injection,mimeType,encoding,"");
        // web.loadUrl(url);
        web.setLayoutParams(new LinearLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        ll.addView(web);
        return ll;
    }

    public static LinearLayout provideOptionsForTableChartType(ChartParams chartParams, Context context,
                                                               Boolean showTableTitle) {


        LinearLayout ll = createLinearLay(context,
                LinearLayout.VERTICAL, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);


        ScrollView scrollView = new ScrollView(context);
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);

        TableLayout tableLayout = new TableLayout(context);

        if(showTableTitle) {


            TextView chartTitleTv = createCardValueTv(context);
            chartTitleTv.setText(chartParams.getChartTitle());
            chartTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    context.getResources().getDimension(R.dimen.insight_card_val_tx_sz));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            chartTitleTv.setBackgroundColor(context.getResources().getColor(R.color.black));
            chartTitleTv.setTextColor(context.getResources().getColor(R.color.white));
            chartTitleTv.setLayoutParams(params);
            chartTitleTv.setPadding(10, 10, 10, 10);
            ll.addView(chartTitleTv);
        }


        HashMap<Integer, JSONObject> numberFormatMap = new HashMap<>();
        JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
        ArrayList<String> columnList = new ArrayList<>();

        JSONObject formatObj = chartParams.getDataViewJson().optJSONObject("visualizations")
                .optJSONObject("format");
        JSONArray formatArr = formatObj.optJSONArray("format");
        String colorVal = "";
        for (int i = 0; i < formatArr.length(); i++) {
            JSONObject obj = formatArr.optJSONObject(i);
            if (obj.optString("type").equalsIgnoreCase("background")) {
                JSONArray optionsArr = obj.optJSONArray("options");
                for (int j = 0; j < optionsArr.length(); j++) {
                    JSONObject optionObj = optionsArr.optJSONObject(j);
                    if (optionObj.optString("type").equalsIgnoreCase("color")) {
                        colorVal = optionObj.optString("value");
                    }
                }
            }
        }

        if (!colorVal.isEmpty()) {
            colorVal = "#" + colorVal;
            ll.setBackgroundColor(Color.parseColor(colorVal));
        }

        JSONArray configArr = chartParams.getDataViewJson().optJSONObject("visualizations")
                .optJSONObject("configuration").optJSONArray("config");
        for (int i = 0; i < configArr.length(); i++) {
            JSONObject configObj = configArr.optJSONObject(i);
            if (configObj.optString("zone").equalsIgnoreCase("columns-table")) {
                JSONArray columsArr = configObj.optJSONArray("columns");
                for (int j = 0; j < columsArr.length(); j++) {
                    JSONObject columsObj = columsArr.optJSONObject(j);
                    columnList.add(j, columsObj.optString("columnId"));
                    if (columsObj.has("numberFormat")) {
                        numberFormatMap.put(j, columsObj.optJSONObject("numberFormat"));
                    }

                }
            }
        }

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        tableLayout.setLayoutParams(tableParams);

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject dataObject = jsonArray.optJSONObject(i);

            TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(rowParams);

            TableRow headerRow = new TableRow(context);
            headerRow.setLayoutParams(rowParams);

            for (int j = 0; j < columnList.size(); j++) {
                TextView labelTv = createCardValueTv(context);
                labelTv.setMinWidth(300);
                labelTv.setMinHeight(160);
                labelTv.setPadding(20, 5, 20, 5);
                labelTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimension(R.dimen.insight_tbl_tx_sz));

                String id = columnList.get(j);
                String label = "";
                String value = "";

                if (i == 0 && chartParams.getColumnMapDataJson().has(id)) {
                    TextView headerTv = createCardValueTv(context);
                    headerTv.setMinWidth(300);
                    headerTv.setMinHeight(160);
                    headerTv.setPadding(20, 5, 20, 5);
                    headerTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                            context.getResources().getDimension(R.dimen.insight_tbl_tx_sz));
                    headerTv.setBackground(context.getResources().getDrawable(R.drawable.cell_shape));
                    headerTv.setTextColor(context.getResources().getColor(R.color.white));
                    label = chartParams.getColumnMapDataJson().optString(id);
                    headerTv.setText(label);
                    headerRow.addView(headerTv);
                }


                value = dataObject.optString(id);
                labelTv.setText(value);
                tableRow.addView(labelTv);
                if (i % 2 == 0) {
                    tableRow.setBackgroundColor(context.getResources().getColor(R.color.white));
                } else {
                    tableRow.setBackgroundColor(context.getResources().getColor(R.color.table_row_bg));
                }


            }


            if (i == 0) {
                tableLayout.addView(headerRow);
            }

            tableLayout.addView(tableRow);
        }

        horizontalScrollView.addView(tableLayout);
        scrollView.addView(horizontalScrollView);
        ll.addView(scrollView);
        return ll;
    }


    public static LinearLayout provideOptionsForCardChartType(ChartParams chartParams, Context context,
                                                              Boolean showDetails,Boolean showHeader) {


        LinearLayout ll = createLinearLay(context,
                LinearLayout.VERTICAL, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);

        ll.setBackground(context.getResources().getDrawable(R.drawable.insight_card_bg));

        ll.setGravity(Gravity.CENTER);



        if(showHeader) {
            TextView chartTitleTv = createCardValueTv(context);
            chartTitleTv.setText(chartParams.getChartTitle());
            chartTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    context.getResources().getDimension(R.dimen.insight_card_val_tx_sz));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 50, 0, 60);
            chartTitleTv.setLayoutParams(params);
            ll.addView(chartTitleTv);
        }

        HashMap<Integer, JSONObject> numberFormatMap = new HashMap<>();
        JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
        ArrayList<String> columnList = new ArrayList<>();

        JSONObject formatObj = chartParams.getDataViewJson().optJSONObject("visualizations")
                .optJSONObject("format");
        JSONArray formatArr = formatObj.optJSONArray("format");
        String colorVal = "";
        for (int i = 0; i < formatArr.length(); i++) {
            JSONObject obj = formatArr.optJSONObject(i);
            if (obj.optString("type").equalsIgnoreCase("background")) {
                JSONArray optionsArr = obj.optJSONArray("options");
                for (int j = 0; j < optionsArr.length(); j++) {
                    JSONObject optionObj = optionsArr.optJSONObject(j);
                    if (optionObj.optString("type").equalsIgnoreCase("color")) {
                        colorVal = optionObj.optString("value");
                    }
                }
            }
        }

        if (!colorVal.isEmpty()) {
            colorVal = "#" + colorVal;
            ll.setBackgroundColor(Color.parseColor(colorVal));
        }


        JSONArray configArr = chartParams.getDataViewJson().optJSONObject("visualizations")
                .optJSONObject("configuration").optJSONArray("config");
        for (int i = 0; i < configArr.length(); i++) {
            JSONObject configObj = configArr.optJSONObject(i);
            if (configObj.optString("zone").equalsIgnoreCase("values-card")) {
                JSONArray columsArr = configObj.optJSONArray("columns");
                for (int j = 0; j < columsArr.length(); j++) {
                    JSONObject columsObj = columsArr.optJSONObject(j);
                    columnList.add(j, columsObj.optString("columnId"));
                    if (columsObj.has("numberFormat")) {
                        numberFormatMap.put(j, columsObj.optJSONObject("numberFormat"));
                    }

                }
            }
        }


        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject dataObject = jsonArray.optJSONObject(i);
            for (int j = 0; j < columnList.size(); j++) {
                TextView labelTv = createCardValueTv(context);

                LinearLayout.LayoutParams paramsLabel = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsLabel.setMargins(0, 5, 0, 30);
                labelTv.setLayoutParams(paramsLabel);
                labelTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimension(R.dimen.insight_card_lbl_tx_sz));

                TextView valueTv = createCardValueTv(context);
                valueTv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        context.getResources().getDimension(R.dimen.insight_card_val_tx_sz));


                if (j == 3 && !showDetails) {
                    valueTv.setText("...");
                    labelTv.setText("See more");
                    ll.addView(valueTv);
                    ll.addView(labelTv);
                    return ll;
                }

                String id = columnList.get(j);
                String label = "";
                double value = 0;
                if (chartParams.getColumnMapDataJson().has(id)) {
                    label = chartParams.getColumnMapDataJson().optString(id);
                }
                value = dataObject.optDouble(id);
                labelTv.setText(label);


                String valueStr = String.valueOf(value);
                if (numberFormatMap.containsKey(j)) {
                    InsightCardValue insightCardValue = getFormattedValue(numberFormatMap.get(j), value);
                    valueStr = insightCardValue.getText();

                    if (insightCardValue.getColor().equalsIgnoreCase("red")) {
                        valueTv.setTextColor(context.getResources().getColor(R.color.red_color));
                    } else {
                        valueTv.setTextColor(context.getResources().getColor(R.color.black));
                    }


                }

                valueTv.setText(valueStr);


                ll.addView(valueTv);
                ll.addView(labelTv);

            }
        }


        return ll;
    }

    public static InsightCardValue getFormattedValue(JSONObject jsonObject, double currentValue) {
        String result = "";
        double number = currentValue;
        String numberFormatSuffix = "";
        String numberFormatType = jsonObject.optString("numberFormatType");

        String prefix = jsonObject.optString("prefix");
        String suffix = jsonObject.optString("suffix");
        String groupingSeparator = jsonObject.optString("groupingSeparator");
        String decimalSeparator = jsonObject.optString("decimalSeparator");
        String decimalPlaces = jsonObject.optString("decimalPlaces");
        String negativeNumberStyle = jsonObject.optString("negativeNumbers");


        if (numberFormatType.equalsIgnoreCase("BillionMillionKilo")) {

            if ((int) Math.abs(currentValue) / 1000000000 > 0) {
                number = currentValue / 1000000000;
                numberFormatSuffix = "B";
            } else if ((int) Math.abs(currentValue) / 1000000 > 0) {
                number = currentValue / 1000000;
                numberFormatSuffix = "M";
            } else if ((int) Math.abs(currentValue) > 0) {
                number = currentValue / 1000;
                numberFormatSuffix = "K";
            }
        } else if (numberFormatType.equalsIgnoreCase("Percentage")) {
            number = currentValue * 100;
            numberFormatSuffix = "%";
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
        DecimalFormat decimalFormatter = (DecimalFormat) formatter;

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        ;
        decimalFormatter.setGroupingUsed(true);

        if (!groupingSeparator.equalsIgnoreCase("")) {
            otherSymbols.setGroupingSeparator(groupingSeparator.charAt(0));
        }
        if (!decimalSeparator.equalsIgnoreCase("")) {
            otherSymbols.setDecimalSeparator(decimalSeparator.charAt(0));
        }

        decimalFormatter.setDecimalFormatSymbols(otherSymbols);
        String formattedNumber = decimalFormatter.format(number);
        formattedNumber = prefix + formattedNumber + numberFormatSuffix + suffix;

        String formattedNumberAbsolute = decimalFormatter.format(number);
        formattedNumberAbsolute = prefix + formattedNumberAbsolute + numberFormatSuffix + suffix;

        String color = "black";
        if (currentValue < 0) {

            switch (negativeNumberStyle) {

                case "2":
                    formattedNumber = "(" + formattedNumberAbsolute + ")";
                    color = "red";
                    break;

                case "3":
                    formattedNumber = "(" + formattedNumberAbsolute + ")";
                    break;


                default:
                    break;
            }
        }
        return new InsightCardValue(formattedNumber, color);


    }


    private static LinearLayout createLinearLay(Context context, int orientation,
                                                int width, int height,
                                                int gravity, float weight) {
        LinearLayout parent = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height, weight);
        params.setMargins(10, 10, 10, 10);
        parent.setLayoutParams(params);
        parent.setOrientation(orientation);
        parent.setGravity(gravity);
        return parent;
    }

    private static TextView createCardValueTv(Context context) {

        TextView textView = new TextView(context);
        textView.setTextColor(context.getResources().getColor(R.color.black));
        textView.setGravity(Gravity.CENTER);
        return textView;

    }


    private static class ChartWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    }

}
