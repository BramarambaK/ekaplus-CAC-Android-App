package com.eka.cacapp.utils;

import android.content.Context;

import com.eka.cacapp.data.insight.ChartParams;
import com.highsoft.highcharts.core.HIChartView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class InsightsViewsManager {

    public HIChartView setCardTypeView(
            Context context,
            HIChartView chartView,
            ChartParams chartParams,
            Boolean showDetails,
            Boolean showCardTitle
    ) {

        chartView.addView(
                InsightViewProvider.provideOptionsForCardChartType(chartParams,context,showDetails,
                        showCardTitle));
        return chartView;
    }

    public HIChartView setTableTypeView(
            Context context,
            HIChartView chartView,
            ChartParams chartParams,
            Boolean showTableTitle
    ) {

        chartView.addView(
                InsightViewProvider.provideOptionsForTableChartType(chartParams,context,showTableTitle));
        return chartView;
    }

    public HIChartView setChartWebView(
            Context context,
            HIChartView chartView,
            ChartParams chartParams,
            String platformUrl,
            String token,
            String chartType,
            String deviceId,
            String filterValue,
            Boolean mapNavigation
    ) {

        chartView.addView(
                InsightViewProvider.provideOptionsForWebViewChartType(chartParams,context,
                        platformUrl,token,chartType,deviceId, filterValue,mapNavigation));
        return chartView;
    }
}
