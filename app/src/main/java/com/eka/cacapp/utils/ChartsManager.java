package com.eka.cacapp.utils;

import com.eka.cacapp.data.insight.ChartParams;
import com.highsoft.highcharts.core.HIChartView;

public class ChartsManager {

    public HIChartView setChart(
            HIChartView chartView,
            ChartParams chartParams
    ) {

        chartView.setOptions(OptionsProvider.provideOptionsForChartType(chartParams));

        chartView.invalidate();

        return chartView;
    }

}

