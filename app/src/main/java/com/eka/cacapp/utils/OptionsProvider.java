package com.eka.cacapp.utils;

import com.eka.cacapp.data.insight.ChartParams;
import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.common.hichartsclasses.*;
import com.highsoft.highcharts.core.HIFunction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class OptionsProvider {

    public static HIOptions provideOptionsForChartType(ChartParams chartParams) {
        try {

            if (chartParams.getChartType().equalsIgnoreCase("column")
          ||  chartParams.getChartType().equalsIgnoreCase("Column3D")

            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();

                HIOptions hiOptions = new HIOptions();

                //For Chart Type
                HIChart chart = new HIChart();
                chart.setType(chartParams.getChartType());

                if(chartParams.getChartType().equalsIgnoreCase("Column3D")){
                    chart.setRenderTo("container");
                    chart.setType("column");
                    chart.setType(chartParams.getChartType());
                    chart.setOptions3d(new HIOptions3d());
                    chart.getOptions3d().setEnabled(true);
                    chart.getOptions3d().setAlpha(15);
                    chart.getOptions3d().setBeta(15);
                    chart.getOptions3d().setDepth(50);
                    chart.getOptions3d().setViewDistance(25);
                }

                hiOptions.setChart(chart);


                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIYAxis yAxis = new HIYAxis();
                yAxis.setMin(0);
                yAxis.setTitle(new HITitle());
                yAxis.getTitle().setText(yAxisTitle);
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yAxis);
                }});

                String xAxisId = chartParams.getXAxisId();
                ArrayList<HIColumn> barArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();

                ArrayList<String> columnList = getColumnList(chartParams,"values");

                ArrayList<String> legendList = getColumnList(chartParams,"legend");
                if(legendList.size()==0){

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        categoryList.add(i, xAxis);
                    }

                    for (int i = 0; i < columnList.size(); i++) {
                        HIColumn bar1 = new HIColumn();
                        bar1.setLabel(new HILabel());
                        valuesList.clear();
                        String valueId = columnList.get(i);
                        if(chartParams.getColumnMapDataJson().has(valueId)){
                            bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                        }
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jobj = jsonArray.optJSONObject(j);
                            valuesList.add(jobj.optDouble(valueId,0));
                            bar1.setData(new ArrayList<>(valuesList));
                        }
                        barArrayList.add(bar1);
                    }

                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryList));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});

                }else {


                    String legendId = legendList.get(0);

                    Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                    Set<String> categoryHashSet = new LinkedHashSet<>();



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        String legengCat = jobj.optJSONObject("_id").optString(legendId);
                        legendLinkedHashSet.add(legengCat);
                        categoryHashSet.add(xAxis);

                    }
                    String valueId = columnList.get(0);

                    for (String currentLegend : legendLinkedHashSet) {
                        HIColumn bar1 = new HIColumn();
                        bar1.setName(currentLegend);
                        valuesList.clear();
                        for(String currCategory : categoryHashSet){
                            double valurr = 0;
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (currCategory.equalsIgnoreCase(jobj.optJSONObject("_id").optString(xAxisId)) &&
                                        currentLegend.equalsIgnoreCase(jobj.optJSONObject("_id").optString(legendId))) {
                                    valurr = jobj.optDouble(valueId, 0);
                                }
                            }
                            valuesList.add(valurr);
                        }

                        bar1.setData(new ArrayList<>(valuesList));
                        barArrayList.add(bar1);
                    }


                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryHashSet));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});

                }

                hiOptions.setSeries(new ArrayList<>(barArrayList));

                return hiOptions;
            } else if (
                    chartParams.getChartType().equalsIgnoreCase("Bar")
          ||  chartParams.getChartType().equalsIgnoreCase("Bar3D")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();
                HIChart chart = new HIChart();
                chart.setType(chartParams.getChartType().toLowerCase());
                if(chartParams.getChartType().equalsIgnoreCase("Bar3D")){
                    chart.setRenderTo("container");
                    chart.setType(chartParams.getChartType());
                    chart.setOptions3d(new HIOptions3d());
                    chart.getOptions3d().setEnabled(true);
                    chart.getOptions3d().setAlpha(15);
                    chart.getOptions3d().setBeta(15);
                    chart.getOptions3d().setDepth(50);
                    chart.getOptions3d().setViewDistance(25);
                }
                hiOptions.setChart(chart);

                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HIBar> barArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();

                ArrayList<String> columnList = getColumnList(chartParams,"values");

                ArrayList<String> legendList = getColumnList(chartParams,"legend");

                if(legendList.size()==0){
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        categoryList.add(i, xAxis);
                    }

                    for (int i = 0; i < columnList.size(); i++) {
                        HIBar bar1 = new HIBar();
                        bar1.setLabel(new HILabel());
                        valuesList.clear();
                        String valueId = columnList.get(i);
                        if(chartParams.getColumnMapDataJson().has(valueId)){
                            bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                        }
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jobj = jsonArray.optJSONObject(j);
                            valuesList.add(jobj.optDouble(valueId,0));
                            bar1.setData(new ArrayList<>(valuesList));
                        }
                        barArrayList.add(bar1);
                    }

                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryList));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }else {
                    String legendId = legendList.get(0);

                    Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                    Set<String> categoryHashSet = new LinkedHashSet<>();



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        String legengCat = jobj.optJSONObject("_id").optString(legendId);
                        legendLinkedHashSet.add(legengCat);
                        categoryHashSet.add(xAxis);
                        categoryList.add(i, xAxis);
                    }
                    String valueId = columnList.get(0);

                    for (String currentLegend : legendLinkedHashSet) {
                        HIBar bar1 = new HIBar();
                        bar1.setName(currentLegend);
                        valuesList.clear();
                        for(String currCategory : categoryHashSet){
                            double valurr = 0;
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (currCategory.equalsIgnoreCase(jobj.optJSONObject("_id").optString(xAxisId)) &&
                                        currentLegend.equalsIgnoreCase(jobj.optJSONObject("_id").optString(legendId))) {
                                    valurr = jobj.optDouble(valueId, 0);
                                }
                            }
                            valuesList.add(valurr);
                        }

                        bar1.setData(new ArrayList<>(valuesList));
                        barArrayList.add(bar1);
                    }


                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryHashSet));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }



                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            } else if (
                    chartParams.getChartType().equalsIgnoreCase("StackedBar")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();
                HIChart chart = new HIChart();
                chart.setType("bar");
                hiOptions.setChart(chart);

                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotOptions = new HIPlotOptions();
                plotOptions.setSeries(new HISeries());
                plotOptions.getSeries().setStacking("normal");
                hiOptions.setPlotOptions(plotOptions);

                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HIBar> barArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();

                ArrayList<String> columnList = getColumnList(chartParams,"values");
                ArrayList<String> legendList = getColumnList(chartParams,"legend");

                if(legendList.size()==0) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        categoryList.add(i, xAxis);
                    }

                    for (int i = 0; i < columnList.size(); i++) {
                        HIBar bar1 = new HIBar();
                        valuesList.clear();
                        String valueId = columnList.get(i);
                        if (chartParams.getColumnMapDataJson().has(valueId)) {
                            bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                        }
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jobj = jsonArray.optJSONObject(j);
                            valuesList.add(jobj.optDouble(valueId, 0));
                            bar1.setData(new ArrayList<>(valuesList));
                        }
                        barArrayList.add(bar1);
                    }

                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryList));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }else {
                    String legendId = legendList.get(0);

                    Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                    Set<String> categoryHashSet = new LinkedHashSet<>();



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        String legengCat = jobj.optJSONObject("_id").optString(legendId);
                        legendLinkedHashSet.add(legengCat);
                        categoryHashSet.add(xAxis);
                        categoryList.add(i, xAxis);
                    }
                    String valueId = columnList.get(0);

                    for (String currentLegend : legendLinkedHashSet) {
                        HIBar bar1 = new HIBar();
                        bar1.setName(currentLegend);
                        valuesList.clear();
                        for(String currCategory : categoryHashSet){
                            double valurr = 0;
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (currCategory.equalsIgnoreCase(jobj.optJSONObject("_id").optString(xAxisId)) &&
                                        currentLegend.equalsIgnoreCase(jobj.optJSONObject("_id").optString(legendId))) {
                                    valurr = jobj.optDouble(valueId, 0);
                                }
                            }
                            valuesList.add(valurr);
                        }

                        bar1.setData(new ArrayList<>(valuesList));
                        barArrayList.add(bar1);
                    }


                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryHashSet));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }

                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            } else if (
                    chartParams.getChartType().equalsIgnoreCase("StackedPercentageColumn")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();
                HIChart chart = new HIChart();
                chart.setType("column");
                hiOptions.setChart(chart);

                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotOptions = new HIPlotOptions();
                plotOptions.setColumn(new HIColumn());
                plotOptions.getColumn().setStacking("percent");
                hiOptions.setPlotOptions(plotOptions);

                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HIColumn> barArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();

                ArrayList<String> columnList = getColumnList(chartParams,"values");
                ArrayList<String> legendList = getColumnList(chartParams,"legend");

                if(legendList.size()==0) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        categoryList.add(i, xAxis);
                    }

                    for (int i = 0; i < columnList.size(); i++) {
                        HIColumn bar1 = new HIColumn();
                        valuesList.clear();
                        String valueId = columnList.get(i);
                        if (chartParams.getColumnMapDataJson().has(valueId)) {
                            bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                        }
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jobj = jsonArray.optJSONObject(j);
                            valuesList.add(jobj.optDouble(valueId, 0));
                            bar1.setData(new ArrayList<>(valuesList));
                        }
                        barArrayList.add(bar1);
                    }

                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryList));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }else {
                    String legendId = legendList.get(0);

                    Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                    Set<String> categoryHashSet = new LinkedHashSet<>();



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        String legengCat = jobj.optJSONObject("_id").optString(legendId);
                        legendLinkedHashSet.add(legengCat);
                        categoryHashSet.add(xAxis);
                        categoryList.add(i, xAxis);
                    }
                    String valueId = columnList.get(0);

                    for (String currentLegend : legendLinkedHashSet) {
                        HIColumn bar1 = new HIColumn();
                        bar1.setName(currentLegend);
                        valuesList.clear();
                        for(String currCategory : categoryHashSet){
                            double valurr = 0;
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (currCategory.equalsIgnoreCase(jobj.optJSONObject("_id").optString(xAxisId)) &&
                                        currentLegend.equalsIgnoreCase(jobj.optJSONObject("_id").optString(legendId))) {
                                    valurr = jobj.optDouble(valueId, 0);
                                }
                            }
                            valuesList.add(valurr);
                        }

                        bar1.setData(new ArrayList<>(valuesList));
                        barArrayList.add(bar1);
                    }


                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryHashSet));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }

                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            } else if (
                    chartParams.getChartType().equalsIgnoreCase("StackedColumn")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();


                hiOptions = setCommonFeatures(hiOptions, chartParams);
                HIPlotOptions plotoptions = new HIPlotOptions();
                plotoptions.setColumn(new HIColumn());
                plotoptions.getColumn().setStacking("normal");

                hiOptions.setPlotOptions(plotoptions);

                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HIColumn> barArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();


                ArrayList<String> columnList = getColumnList(chartParams,"values");


                ArrayList<String> legendList = getColumnList(chartParams,"legend");

                if(legendList.size()==0) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        categoryList.add(i, xAxis);
                    }

                    for (int i = 0; i < columnList.size(); i++) {
                        HIColumn bar1 = new HIColumn();
                        valuesList.clear();
                        String valueId = columnList.get(i);
                        if (chartParams.getColumnMapDataJson().has(valueId)) {
                            bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                        }
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jobj = jsonArray.optJSONObject(j);
                            valuesList.add(jobj.optDouble(valueId, 0));
                            bar1.setData(new ArrayList<>(valuesList));
                        }
                        barArrayList.add(bar1);
                    }

                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryList));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }else {
                    String legendId = legendList.get(0);

                    Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                    Set<String> categoryHashSet = new LinkedHashSet<>();



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        String legengCat = jobj.optJSONObject("_id").optString(legendId);
                        legendLinkedHashSet.add(legengCat);
                        categoryHashSet.add(xAxis);
                        categoryList.add(i, xAxis);
                    }
                    String valueId = columnList.get(0);

                    for (String currentLegend : legendLinkedHashSet) {
                        HIColumn bar1 = new HIColumn();
                        bar1.setName(currentLegend);
                        valuesList.clear();
                        for(String currCategory : categoryHashSet){
                            double valurr = 0;
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (currCategory.equalsIgnoreCase(jobj.optJSONObject("_id").optString(xAxisId)) &&
                                        currentLegend.equalsIgnoreCase(jobj.optJSONObject("_id").optString(legendId))) {
                                    valurr = jobj.optDouble(valueId, 0);
                                }
                            }
                            valuesList.add(valurr);
                        }

                        bar1.setData(new ArrayList<>(valuesList));
                        barArrayList.add(bar1);
                    }


                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryHashSet));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }

                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            } else if (
                    chartParams.getChartType().equalsIgnoreCase("Area")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();


                HIChart chart = new HIChart();
                chart.setType("area");
                hiOptions.setChart(chart);

                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HIArea> barArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();

                ArrayList<String> columnList = getColumnList(chartParams,"values");
                ArrayList<String> legendList = getColumnList(chartParams,"legend");

                if(legendList.size()==0) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        categoryList.add(i, xAxis);
                    }

                    for (int i = 0; i < columnList.size(); i++) {
                        HIArea bar1 = new HIArea();
                        valuesList.clear();
                        String valueId = columnList.get(i);
                        if (chartParams.getColumnMapDataJson().has(valueId)) {
                            bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                        }
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jobj = jsonArray.optJSONObject(j);
                            valuesList.add(jobj.optDouble(valueId, 0));
                            bar1.setData(new ArrayList<>(valuesList));
                        }
                        barArrayList.add(bar1);
                    }

                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryList));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }
                else {
                    String legendId = legendList.get(0);

                    Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                    Set<String> categoryHashSet = new LinkedHashSet<>();



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        String legengCat = jobj.optJSONObject("_id").optString(legendId);
                        legendLinkedHashSet.add(legengCat);
                        categoryHashSet.add(xAxis);
                        categoryList.add(i, xAxis);
                    }
                    String valueId = columnList.get(0);

                    for (String currentLegend : legendLinkedHashSet) {
                        HIArea bar1 = new HIArea();
                        bar1.setName(currentLegend);
                        valuesList.clear();
                        for(String currCategory : categoryHashSet){
                            double valurr = 0;
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (currCategory.equalsIgnoreCase(jobj.optJSONObject("_id").optString(xAxisId)) &&
                                        currentLegend.equalsIgnoreCase(jobj.optJSONObject("_id").optString(legendId))) {
                                    valurr = jobj.optDouble(valueId, 0);
                                }
                            }
                            valuesList.add(valurr);
                        }

                        bar1.setData(new ArrayList<>(valuesList));
                        barArrayList.add(bar1);
                    }


                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryHashSet));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }


                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            } else if (
                    chartParams.getChartType().equalsIgnoreCase("AreaSpline")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();

                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotOptions = new HIPlotOptions();
                plotOptions.setAreaspline(new HIAreaspline());
                plotOptions.getAreaspline().setFillOpacity(0.5);
                hiOptions.setPlotOptions(plotOptions);


                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HIAreaspline> barArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();


                ArrayList<String> columnList = getColumnList(chartParams,"values");

                ArrayList<String> legendList = getColumnList(chartParams,"legend");

                if(legendList.size()==0) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        categoryList.add(i, xAxis);
                    }

                    for (int i = 0; i < columnList.size(); i++) {
                        HIAreaspline bar1 = new HIAreaspline();
                        valuesList.clear();
                        String valueId = columnList.get(i);
                        if (chartParams.getColumnMapDataJson().has(valueId)) {
                            bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                        }
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jobj = jsonArray.optJSONObject(j);
                            valuesList.add(jobj.optDouble(valueId, 0));
                            bar1.setData(new ArrayList<>(valuesList));
                        }
                        barArrayList.add(bar1);
                    }

                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryList));
                    //  hiOptions.setXAxis(new ArrayList<HIXAxis>(){{add(xaxis);}});

                    HIPlotBands plotband = new HIPlotBands();
                    plotband.setFrom(4.5);
                    plotband.setTo(6.5);
                    plotband.setColor(HIColor.initWithRGBA(68, 170, 213, 2));
                    xaxis.setPlotBands(new ArrayList<>(Arrays.asList(plotband)));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});

                }
                else {
                    String legendId = legendList.get(0);

                    Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                    Set<String> categoryHashSet = new LinkedHashSet<>();



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        String legengCat = jobj.optJSONObject("_id").optString(legendId);
                        legendLinkedHashSet.add(legengCat);
                        categoryHashSet.add(xAxis);
                        categoryList.add(i, xAxis);
                    }
                    String valueId = columnList.get(0);

                    for (String currentLegend : legendLinkedHashSet) {
                        HIAreaspline bar1 = new HIAreaspline();
                        bar1.setName(currentLegend);
                        valuesList.clear();
                        for(String currCategory : categoryHashSet){
                            double valurr = 0;
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (currCategory.equalsIgnoreCase(jobj.optJSONObject("_id").optString(xAxisId)) &&
                                        currentLegend.equalsIgnoreCase(jobj.optJSONObject("_id").optString(legendId))) {

                                    valurr = jobj.optDouble(valueId, 0);
                                }
                            }
                            valuesList.add(valurr);
                        }

                        bar1.setData(new ArrayList<>(valuesList));
                        barArrayList.add(bar1);
                    }


                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryHashSet));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }


                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            } else if (
                    chartParams.getChartType().equalsIgnoreCase("StackedPercentageArea")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();

                HIChart chart = new HIChart();
                chart.setType("area");
                hiOptions.setChart(chart);


                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotOptions = new HIPlotOptions();
                plotOptions.setArea(new HIArea());
                plotOptions.getArea().setStacking("percent");
                plotOptions.getArea().setLineColor(HIColor.initWithHexValue("ffffff"));
                plotOptions.getArea().setLineWidth(1);
                plotOptions.getArea().setMarker(new HIMarker());
                plotOptions.getArea().getMarker().setLineWidth(1);
                //  plotOptions.getArea().getMarker().setLineColor(HIColor.initWithHexValue("ffffff"));
                hiOptions.setPlotOptions(plotOptions);

                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HIArea> barArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();

                ArrayList<String> columnList = getColumnList(chartParams,"values");

                ArrayList<String> legendList = getColumnList(chartParams,"legend");

                if(legendList.size()==0) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        categoryList.add(i, xAxis);
                    }

                    for (int i = 0; i < columnList.size(); i++) {
                        HIArea bar1 = new HIArea();
                        valuesList.clear();
                        String valueId = columnList.get(i);
                        if (chartParams.getColumnMapDataJson().has(valueId)) {
                            bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                        }
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jobj = jsonArray.optJSONObject(j);
                            valuesList.add(jobj.optDouble(valueId, 0));
                            bar1.setData(new ArrayList<>(valuesList));
                        }
                        barArrayList.add(bar1);
                    }

                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryList));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});

                }
                else {
                    String legendId = legendList.get(0);

                    Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                    Set<String> categoryHashSet = new LinkedHashSet<>();



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        String legengCat = jobj.optJSONObject("_id").optString(legendId);
                        legendLinkedHashSet.add(legengCat);
                        categoryHashSet.add(xAxis);
                        categoryList.add(i, xAxis);
                    }
                    String valueId = columnList.get(0);

                    for (String currentLegend : legendLinkedHashSet) {
                        HIArea bar1 = new HIArea();
                        bar1.setName(currentLegend);
                        valuesList.clear();
                        for(String currCategory : categoryHashSet){
                            double valurr = 0;
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (currCategory.equalsIgnoreCase(jobj.optJSONObject("_id").optString(xAxisId)) &&
                                        currentLegend.equalsIgnoreCase(jobj.optJSONObject("_id").optString(legendId))) {

                                    valurr = jobj.optDouble(valueId, 0);
                                }
                            }
                            valuesList.add(valurr);
                        }

                        bar1.setData(new ArrayList<>(valuesList));
                        barArrayList.add(bar1);
                    }


                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryHashSet));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }


                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            } else if (
                    chartParams.getChartType().equalsIgnoreCase("StackedArea")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();


                //For chart Type
                HIChart chart = new HIChart();
                chart.setType("area");
                hiOptions.setChart(chart);


                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotOptions = new HIPlotOptions();
                plotOptions.setArea(new HIArea());
                plotOptions.getArea().setStacking("normal");
                plotOptions.getArea().setLineColor(HIColor.initWithHexValue("666666"));
                plotOptions.getArea().setLineWidth(1);
                plotOptions.getArea().setMarker(new HIMarker());
                plotOptions.getArea().getMarker().setLineWidth(1);
                //   plotOptions.getArea().getMarker().setLineColor(HIColor.initWithHexValue("666666"));
                hiOptions.setPlotOptions(plotOptions);


                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HIArea> barArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();


                ArrayList<String> columnList = getColumnList(chartParams,"values");

                ArrayList<String> legendList = getColumnList(chartParams,"legend");

                if(legendList.size()==0) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        categoryList.add(i, xAxis);
                    }

                    for (int i = 0; i < columnList.size(); i++) {
                        HIArea bar1 = new HIArea();
                        valuesList.clear();
                        String valueId = columnList.get(i);
                        if (chartParams.getColumnMapDataJson().has(valueId)) {
                            bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                        }
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jobj = jsonArray.optJSONObject(j);
                            valuesList.add(jobj.optDouble(valueId, 0));
                            bar1.setData(new ArrayList<>(valuesList));
                        }
                        barArrayList.add(bar1);
                    }

                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryList));
                    xaxis.setTickmarkPlacement("on");
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});

                    HIYAxis yAxis = new HIYAxis();
                    yAxis.setTitle(new HITitle());
                    yAxis.getTitle().setText(yAxisTitle);
                    yAxis.setLabels(new HILabels());
                    yAxis.getLabels().setFormatter(new HIFunction(
                            f -> {
                                return String.valueOf((Double) f.getProperty("value") / 1000);
                            },
                            new String[]{"value"}
                    ));
                    hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                        add(yAxis);
                    }});

                }
                else {
                    String legendId = legendList.get(0);

                    Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                    Set<String> categoryHashSet = new LinkedHashSet<>();



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        String legengCat = jobj.optJSONObject("_id").optString(legendId);
                        legendLinkedHashSet.add(legengCat);
                        categoryHashSet.add(xAxis);
                        categoryList.add(i, xAxis);
                    }
                    String valueId = columnList.get(0);

                    for (String currentLegend : legendLinkedHashSet) {
                        HIArea bar1 = new HIArea();
                        bar1.setName(currentLegend);
                        valuesList.clear();
                        for(String currCategory : categoryHashSet){
                            double valurr = 0;
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (currCategory.equalsIgnoreCase(jobj.optJSONObject("_id").optString(xAxisId)) &&
                                        currentLegend.equalsIgnoreCase(jobj.optJSONObject("_id").optString(legendId))) {
                                    valurr = jobj.optDouble(valueId, 0);
                                }
                            }
                            valuesList.add(valurr);
                        }

                        bar1.setData(new ArrayList<>(valuesList));
                        barArrayList.add(bar1);
                    }


                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryHashSet));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }


                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            }
            else if (
                    chartParams.getChartType().equalsIgnoreCase("Pie")
           || chartParams.getChartType().equalsIgnoreCase("Pie3D")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();

                HIChart chart = new HIChart();
                chart.setType("pie");
               // chart.setPlotShadow(false);
                if(chartParams.getChartType().equalsIgnoreCase("Pie3D")){
                    chart.setOptions3d(new HIOptions3d());
                    chart.getOptions3d().setEnabled(true);
                    chart.getOptions3d().setAlpha(45);
                    chart.getOptions3d().setBeta(0);
                }

                hiOptions.setChart(chart);


                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotOptions = new HIPlotOptions();
                plotOptions.setArea(new HIArea());
                plotOptions.getArea().setStacking("percent");
                plotOptions.getArea().setLineColor(HIColor.initWithHexValue("ffffff"));
                plotOptions.getArea().setLineWidth(1);
                plotOptions.getArea().setMarker(new HIMarker());
                plotOptions.getArea().getMarker().setLineWidth(1);
                //  plotOptions.getArea().getMarker().setLineColor(HIColor.initWithHexValue("ffffff"));
                hiOptions.setPlotOptions(plotOptions);

                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});


                String xAxisId = chartParams.getXAxisId();
                String valueId = chartParams.getValueIdList().get(0);
                ArrayList<HashMap> pieArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                HIPie pie = new HIPie();
                pie.setName(yAxisTitle);

                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jobj = jsonArray.optJSONObject(j);
                        HashMap<String, Object> map1 = new HashMap<>();
                        map1.put("name", jobj.optJSONObject("_id").optString(xAxisId));
                        map1.put("y", jobj.optDouble(valueId,0));
                        pieArrayList.add(map1);
                    }

                pie.setData(new ArrayList<>(pieArrayList));

                hiOptions.setSeries(new ArrayList<>(Collections.singletonList(pie)));

              //  hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            }


            else if (
                    chartParams.getChartType().equalsIgnoreCase("Spline")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();



                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotOptions = new HIPlotOptions();
                plotOptions.setSpline(new HISpline());
                plotOptions.getSpline().setMarker(new HIMarker());
                plotOptions.getSpline().getMarker().setRadius(4);
             //   plotOptions.getSpline().getMarker().setLineColor(HIColor.initWithHexValue("666666"));
                plotOptions.getSpline().getMarker().setLineWidth(1);
                hiOptions.setPlotOptions(plotOptions);

                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HISpline > barArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();

                ArrayList<String> columnList = getColumnList(chartParams,"values");

                ArrayList<String> legendList = getColumnList(chartParams,"legend");

                if(legendList.size()==0) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        categoryList.add(i, xAxis);
                    }

                    for (int i = 0; i < columnList.size(); i++) {
                        HISpline bar1 = new HISpline();
                        valuesList.clear();
                        String valueId = columnList.get(i);
                        if (chartParams.getColumnMapDataJson().has(valueId)) {
                            bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                        }
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jobj = jsonArray.optJSONObject(j);
                            valuesList.add(jobj.optDouble(valueId, 0));
                            bar1.setData(new ArrayList<>(valuesList));
                        }
                        barArrayList.add(bar1);
                    }

                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryList));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});

                }
                else {
                    String legendId = legendList.get(0);

                    Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                    Set<String> categoryHashSet = new LinkedHashSet<>();



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        String legengCat = jobj.optJSONObject("_id").optString(legendId);
                        legendLinkedHashSet.add(legengCat);
                        categoryHashSet.add(xAxis);
                        categoryList.add(i, xAxis);
                    }
                    String valueId = columnList.get(0);

                    for (String currentLegend : legendLinkedHashSet) {
                        HISpline bar1 = new HISpline();
                        bar1.setName(currentLegend);
                        valuesList.clear();
                        for(String currCategory : categoryHashSet){
                            double valurr = 0;
                            for (int j = 0; j < jsonArray.length(); j++) {

                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (currCategory.equalsIgnoreCase(jobj.optJSONObject("_id").optString(xAxisId)) &&
                                        currentLegend.equalsIgnoreCase(jobj.optJSONObject("_id").optString(legendId))) {


                                    valurr = jobj.optDouble(valueId, 0);
                                    valuesList.add(valurr);
                                }
                            }

                        }

                        bar1.setData(new ArrayList<>(valuesList));
                        barArrayList.add(bar1);
                    }


                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryHashSet));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }


                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            }
            else if (
                    chartParams.getChartType().equalsIgnoreCase("Line")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();

                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotoptions = new HIPlotOptions();
                plotoptions.setSeries(new HISeries());
                plotoptions.getSeries().setLabel(new HILabel());
                plotoptions.getSeries().getLabel().setConnectorAllowed(false);
               // plotoptions.getSeries().setPointStart(2010);
                hiOptions.setPlotOptions(plotoptions);

                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HILine > barArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();


                ArrayList<String> columnList = getColumnList(chartParams,"values");

                ArrayList<String> legendList = getColumnList(chartParams,"legend");

                if(legendList.size()==0) {


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        categoryList.add(i, xAxis);
                    }

                    for (int i = 0; i < columnList.size(); i++) {
                        HILine bar1 = new HILine();
                        valuesList.clear();
                        String valueId = columnList.get(i);
                        if (chartParams.getColumnMapDataJson().has(valueId)) {
                            bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                        }
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jobj = jsonArray.optJSONObject(j);
                            valuesList.add(jobj.optDouble(valueId, 0));
                            bar1.setData(new ArrayList<>(valuesList));
                        }
                        barArrayList.add(bar1);
                    }

                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryList));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});

                }
                else {
                    String legendId = legendList.get(0);

                    Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                    Set<String> categoryHashSet = new LinkedHashSet<>();



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        String legengCat = jobj.optJSONObject("_id").optString(legendId);
                        legendLinkedHashSet.add(legengCat);
                        categoryHashSet.add(xAxis);
                        categoryList.add(i, xAxis);
                    }
                    String valueId = columnList.get(0);

                    for (String currentLegend : legendLinkedHashSet) {
                        HILine bar1 = new HILine();
                        bar1.setName(currentLegend);
                        valuesList.clear();
                        for(String currCategory : categoryHashSet){
                            double valurr = 0;
                            for (int j = 0; j < jsonArray.length(); j++) {

                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (currCategory.equalsIgnoreCase(jobj.optJSONObject("_id").optString(xAxisId)) &&
                                        currentLegend.equalsIgnoreCase(jobj.optJSONObject("_id").optString(legendId))) {

                                    valurr = jobj.optDouble(valueId, 0);
                                    valuesList.add(valurr);

                                }
                            }
                        }

                        bar1.setData(new ArrayList<>(valuesList));
                        barArrayList.add(bar1);
                    }


                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryHashSet));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }



                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            }

            else if (
                    chartParams.getChartType().equalsIgnoreCase("LineColumnStacked")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();

                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotoptions = new HIPlotOptions();
                plotoptions.setColumn(new HIColumn());
                plotoptions.getColumn().setStacking("normal");
                // plotoptions.getSeries().setPointStart(2010);
                plotoptions.setSeries(new HISeries());
                plotoptions.getSeries().setLabel(new HILabel());
                plotoptions.getSeries().getLabel().setConnectorAllowed(false);
                hiOptions.setPlotOptions(plotoptions);

                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});

                HIYAxis yAxis = new HIYAxis();
                yAxis.setMin(0);
                yAxis.setTitle(new HITitle());
                yAxis.getTitle().setText(yAxisTitle);
                yAxis.setOpposite(true);
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yAxis);
                }});
                hiOptions.setYAxis(new ArrayList<>(Arrays.asList(yAxis,yaxis)));


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HISeries > barArrayList = new ArrayList<>();


                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();

                //values-column

                ArrayList<String> columnListColumn = getColumnList(chartParams,"values-column");
                ArrayList<String> columnList = getColumnList(chartParams,"values-line");



                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jobj = jsonArray.optJSONObject(i);
                    String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                    categoryList.add(i, xAxis);
                }

                for (int i = 0; i < columnListColumn.size(); i++) {
                    HIColumn bar1 = new HIColumn();
                    bar1.setLabel(new HILabel());
                    bar1.setYAxis(0);
                    valuesList.clear();
                    String valueId = columnListColumn.get(i);
                    if(chartParams.getColumnMapDataJson().has(valueId)){
                        bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jobj = jsonArray.optJSONObject(j);
                        valuesList.add(jobj.optDouble(valueId,0));
                        bar1.setData(new ArrayList<>(valuesList));
                    }
                    barArrayList.add(bar1);
                }

                for (int i = 0; i < columnList.size(); i++) {
                    HILine bar1 = new HILine();
                    valuesList.clear();
                    bar1.setYAxis(1);
                    String valueId = columnList.get(i);
                    if (chartParams.getColumnMapDataJson().has(valueId)) {
                        bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jobj = jsonArray.optJSONObject(j);
                        valuesList.add(jobj.optDouble(valueId, 0));
                        bar1.setData(new ArrayList<>(valuesList));
                    }
                    barArrayList.add(bar1);
                }


                HIXAxis xaxis = new HIXAxis();
                xaxis.setCategories(new ArrayList<>(categoryList));
                hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                    add(xaxis);
                }});




                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            }
            else if (
                    chartParams.getChartType().equalsIgnoreCase("LineColumn")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();

                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotoptions = new HIPlotOptions();
                plotoptions.setSeries(new HISeries());
                plotoptions.getSeries().setLabel(new HILabel());
                plotoptions.getSeries().getLabel().setConnectorAllowed(false);
                // plotoptions.getSeries().setPointStart(2010);
                hiOptions.setPlotOptions(plotoptions);

                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});

                HIYAxis yAxis = new HIYAxis();
                yAxis.setMin(0);
                yAxis.setTitle(new HITitle());
                yAxis.getTitle().setText(yAxisTitle);
                yAxis.setOpposite(true);
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yAxis);
                }});
                hiOptions.setYAxis(new ArrayList<>(Arrays.asList(yAxis,yaxis)));


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HISeries > barArrayList = new ArrayList<>();


                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();

                //values-column

                ArrayList<String> columnListColumn = getColumnList(chartParams,"values-column");
                ArrayList<String> columnList = getColumnList(chartParams,"values-line");



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        categoryList.add(i, xAxis);
                    }





                for (int i = 0; i < columnListColumn.size(); i++) {
                    HIColumn bar1 = new HIColumn();
                    bar1.setLabel(new HILabel());
                    bar1.setYAxis(0);
                    valuesList.clear();
                    String valueId = columnListColumn.get(i);
                    if(chartParams.getColumnMapDataJson().has(valueId)){
                        bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jobj = jsonArray.optJSONObject(j);
                        valuesList.add(jobj.optDouble(valueId,0));
                        bar1.setData(new ArrayList<>(valuesList));
                    }
                    barArrayList.add(bar1);
                }

                for (int i = 0; i < columnList.size(); i++) {
                    HILine bar1 = new HILine();
                    bar1.setYAxis(1);
                    valuesList.clear();
                    String valueId = columnList.get(i);
                    if (chartParams.getColumnMapDataJson().has(valueId)) {
                        bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jobj = jsonArray.optJSONObject(j);
                        valuesList.add(jobj.optDouble(valueId, 0));
                        bar1.setData(new ArrayList<>(valuesList));
                    }
                    barArrayList.add(bar1);
                }





                HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryList));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});






                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            }
            else if (
                    chartParams.getChartType().equalsIgnoreCase("LineArea")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();

                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotoptions = new HIPlotOptions();
                plotoptions.setSeries(new HISeries());
                plotoptions.getSeries().setLabel(new HILabel());
                plotoptions.getSeries().getLabel().setConnectorAllowed(false);
                // plotoptions.getSeries().setPointStart(2010);
                hiOptions.setPlotOptions(plotoptions);

                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});

                HIYAxis yAxis = new HIYAxis();
                yAxis.setMin(0);
                yAxis.setTitle(new HITitle());
                yAxis.getTitle().setText(yAxisTitle);
                yAxis.setOpposite(true);
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yAxis);
                }});
                hiOptions.setYAxis(new ArrayList<>(Arrays.asList(yAxis,yaxis)));


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HISeries > barArrayList = new ArrayList<>();


                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();

                //values-column

                ArrayList<String> columnListColumn = getColumnList(chartParams,"values-area");
                ArrayList<String> columnList = getColumnList(chartParams,"values-line");



                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jobj = jsonArray.optJSONObject(i);
                    String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                    categoryList.add(i, xAxis);
                }





                for (int i = 0; i < columnListColumn.size(); i++) {
                    HIArea bar1 = new HIArea();
                    bar1.setLabel(new HILabel());
                    bar1.setYAxis(0);
                    valuesList.clear();
                    String valueId = columnListColumn.get(i);
                    if(chartParams.getColumnMapDataJson().has(valueId)){
                        bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jobj = jsonArray.optJSONObject(j);
                        valuesList.add(jobj.optDouble(valueId,0));
                        bar1.setData(new ArrayList<>(valuesList));
                    }
                    barArrayList.add(bar1);
                }

                for (int i = 0; i < columnList.size(); i++) {
                    HILine bar1 = new HILine();
                    bar1.setYAxis(1);
                    valuesList.clear();
                    String valueId = columnList.get(i);
                    if (chartParams.getColumnMapDataJson().has(valueId)) {
                        bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jobj = jsonArray.optJSONObject(j);
                        valuesList.add(jobj.optDouble(valueId, 0));
                        bar1.setData(new ArrayList<>(valuesList));
                    }
                    barArrayList.add(bar1);
                }





                HIXAxis xaxis = new HIXAxis();
                xaxis.setCategories(new ArrayList<>(categoryList));
                hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                    add(xaxis);
                }});






                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            }
            else if (
                    chartParams.getChartType().equalsIgnoreCase("LineAreaStacked")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();

                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotoptions = new HIPlotOptions();
                plotoptions.setSeries(new HISeries());
                plotoptions.getSeries().setLabel(new HILabel());
                plotoptions.getSeries().getLabel().setConnectorAllowed(false);



                plotoptions.setArea(new HIArea());
                plotoptions.getArea().setStacking("normal");
                hiOptions.setPlotOptions(plotoptions);


                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});

                HIYAxis yAxis = new HIYAxis();
                yAxis.setMin(0);
                yAxis.setTitle(new HITitle());
                yAxis.getTitle().setText(yAxisTitle);
                yAxis.setOpposite(true);
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yAxis);
                }});
                hiOptions.setYAxis(new ArrayList<>(Arrays.asList(yAxis,yaxis)));


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HISeries > barArrayList = new ArrayList<>();


                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();


                ArrayList<String> columnListColumn = getColumnList(chartParams,"values-area");
                ArrayList<String> columnList = getColumnList(chartParams,"values-line");



                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jobj = jsonArray.optJSONObject(i);
                    String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                    categoryList.add(i, xAxis);
                }





                for (int i = 0; i < columnListColumn.size(); i++) {
                    HIArea bar1 = new HIArea();
                    bar1.setLabel(new HILabel());
                    bar1.setYAxis(0);
                    valuesList.clear();
                    String valueId = columnListColumn.get(i);
                    if(chartParams.getColumnMapDataJson().has(valueId)){
                        bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jobj = jsonArray.optJSONObject(j);
                        valuesList.add(jobj.optDouble(valueId,0));
                        bar1.setData(new ArrayList<>(valuesList));
                    }
                    barArrayList.add(bar1);
                }

                for (int i = 0; i < columnList.size(); i++) {
                    HILine bar1 = new HILine();
                    bar1.setYAxis(1);
                    valuesList.clear();
                    String valueId = columnList.get(i);
                    if (chartParams.getColumnMapDataJson().has(valueId)) {
                        bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jobj = jsonArray.optJSONObject(j);
                        valuesList.add(jobj.optDouble(valueId, 0));
                        bar1.setData(new ArrayList<>(valuesList));
                    }
                    barArrayList.add(bar1);
                }





                HIXAxis xaxis = new HIXAxis();
                xaxis.setCategories(new ArrayList<>(categoryList));
                hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                    add(xaxis);
                }});






                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            }

            else if (
                    chartParams.getChartType().equalsIgnoreCase("ScatterPlot")
            ) {

                //For Chart Type
                HIOptions hiOptions = new HIOptions();
                try {



                    HIChart chart = new HIChart();
                    chart.setType("scatter");
                    chart.setPlotBorderWidth(1);
                    chart.setZoomType("xy");
                    hiOptions.setChart(chart);


                    hiOptions = setCommonFeatures(hiOptions, chartParams);

                    String xAxisId = chartParams.getXAxisId();
                    ArrayList<HashMap<String, Object>> barArrayList = new ArrayList<>();
                    ArrayList<HIScatter> hiScatterArrayList = new ArrayList<>();

                    JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                    ArrayList<String> categoryList = new ArrayList<>();
                    ArrayList<Number> valuesList = new ArrayList<>();

                    ArrayList<String> xAxisList = getColumnList(chartParams,"xaxis");
                    ArrayList<String> yAxisList = getColumnList(chartParams,"yaxis");
                    ArrayList<String> columnList = new ArrayList<>();
                    columnList.addAll(xAxisList);
                    columnList.addAll(yAxisList);



                    ArrayList<String> legendList = getColumnList(chartParams,"legend");
                    if(legendList.size()==0){


                        for (int i = 0; i < jsonArray.length(); i++) {
                            HashMap<String, Object> bar1 = new HashMap<String, Object>();
                            JSONObject jobj = jsonArray.optJSONObject(i);
                            bar1.put("x",jobj.optDouble(xAxisList.get(0)));
                            bar1.put("y",jobj.optDouble(yAxisList.get(0)));
                            barArrayList.add(bar1);
                        }


                    }else {
                        String legendId = legendList.get(0);

                        Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                        Set<String> categoryHashSet = new LinkedHashSet<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobj = jsonArray.optJSONObject(i);
                            String legengCat = jobj.optString(legendId);
                            legendLinkedHashSet.add(legengCat);
                        }

                        for (String currentLegend : legendLinkedHashSet) {

                            for (int j = 0; j < jsonArray.length(); j++) {
                                HashMap<String, Object> bar1 = new HashMap<String, Object>();
                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (
                                        currentLegend.equalsIgnoreCase(jobj.optString(legendId))) {
                                    bar1.put("x",jobj.optInt(xAxisList.get(0),0));
                                    bar1.put("y",jobj.optDouble(yAxisList.get(0),0));
                                    bar1.put("name",currentLegend);
                                    barArrayList.add(bar1);

                                }
                            }


                        }

                    }


                    HIPlotOptions plotOptions = new HIPlotOptions();
                    plotOptions.setScatter(new HIScatter());
                    plotOptions.getScatter().setMarker(new HIMarker());
                    plotOptions.getScatter().getMarker().setRadius(15);
                    plotOptions.getScatter().getMarker().setStates(new HIStates());
                    plotOptions.getScatter().getMarker().getStates().setHover(new HIHover());
                    plotOptions.getScatter().getMarker().getStates().getHover().setEnabled(true);
                    plotOptions.getScatter().getMarker().getStates().getHover().setLineColor(HIColor.initWithRGB(100, 100, 100));
                    plotOptions.getScatter().setTooltip(new HITooltip());
                    plotOptions.getScatter().getTooltip().setHeaderFormat("<b>{point.name}</b><br>");

                    plotOptions.getScatter().getTooltip().setPointFormat(chartParams.getColumnMapDataJson().optString(xAxisList.get(0))+" : {point.x} , "+chartParams.getColumnMapDataJson().optString(yAxisList.get(0))+" : {point.y}");
                    hiOptions.setPlotOptions(plotOptions);


                    hiOptions.setPlotOptions(plotOptions);
                    HIScatter series1 = new HIScatter();
                    series1.setColor(HIColor.initWithRGBA(157, 200, 241,0.6));
                    series1.setData(barArrayList);
                    hiOptions.setSeries(new ArrayList<>(Arrays.asList(series1)));
                    // hiOptions.setSeries(new ArrayList<>(hiScatterArrayList));

                }catch (Exception e){

                }

                return hiOptions;
            }
            else if (
                    chartParams.getChartType().equalsIgnoreCase("Polar")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();

                HIChart chart = new HIChart();
                chart.setPolar(true);
                hiOptions.setChart(chart);


                hiOptions = setCommonFeatures(hiOptions, chartParams);

                String xAxisId = chartParams.getXAxisId();
                ArrayList<HISeries  > barArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<Number> valuesList = new ArrayList<>();

                ArrayList<String> categoryList = new ArrayList<>();

                ArrayList<String> columnList = getColumnList(chartParams,"values");

                ArrayList<String> legendList = getColumnList(chartParams,"legend");

                if(legendList.size()==0) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        categoryList.add(i, xAxis);
                    }


                    for (int i = 0; i < columnList.size(); i++) {
                        HILine bar1 = new HILine();
                        valuesList.clear();
                        String valueId = columnList.get(i);
                        if (chartParams.getColumnMapDataJson().has(valueId)) {
                            bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                        }
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jobj = jsonArray.optJSONObject(j);
                            valuesList.add(jobj.optDouble(valueId, 0));
                            bar1.setData(new ArrayList<>(valuesList));
                        }
                        barArrayList.add(bar1);
                    }

                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryList));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});

                }
                else {
                    String legendId = legendList.get(0);

                    Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                    Set<String> categoryHashSet = new LinkedHashSet<>();



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        String legengCat = jobj.optJSONObject("_id").optString(legendId);
                        legendLinkedHashSet.add(legengCat);
                        categoryHashSet.add(xAxis);
                        categoryList.add(i, xAxis);
                    }
                    String valueId = columnList.get(0);

                    for (String currentLegend : legendLinkedHashSet) {
                        HILine bar1 = new HILine();
                        bar1.setName(currentLegend);
                        valuesList.clear();
                        for(String currCategory : categoryHashSet){
                            double valurr = 0;
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (currCategory.equalsIgnoreCase(jobj.optJSONObject("_id").optString(xAxisId)) &&
                                        currentLegend.equalsIgnoreCase(jobj.optJSONObject("_id").optString(legendId))) {
                                    valurr = jobj.optDouble(valueId, 0);
                                }
                            }
                            valuesList.add(valurr);
                        }

                        bar1.setData(new ArrayList<>(valuesList));
                        barArrayList.add(bar1);
                    }


                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryHashSet));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }


                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            }
            else if (
                    chartParams.getChartType().equalsIgnoreCase("Bubble")
            ) {

                //For Chart Type
                HIOptions hiOptions = new HIOptions();

                HIChart chart = new HIChart();
                chart.setType("bubble");
                chart.setPlotBorderWidth(1);
                chart.setZoomType("xy");
                hiOptions.setChart(chart);

                HIPane pane = new HIPane();
                pane.setStartAngle(0);
                pane.setEndAngle(360);
                hiOptions.setPane(pane);


                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotOptions = new HIPlotOptions();
                plotOptions.setSeries(new HISeries());

                hiOptions.setPlotOptions(plotOptions);


                ArrayList<HashMap> bubbleArrayList = new ArrayList<>();

                ArrayList<String> xAxisList = new ArrayList<>();
                ArrayList<String> yAxisList = new ArrayList<>();
                JSONArray yFieldData = chartParams.getDataJson().optJSONArray("yFieldData");
                JSONArray xFieldData = chartParams.getDataJson().optJSONArray("xFieldData");



                    for (int i = 0; i < yFieldData.length(); i++) {
                        yAxisList.add(i, yFieldData.optString(i));
                    }
                    for (int i = 0; i < xFieldData.length(); i++) {
                        xAxisList.add(i, xFieldData.optString(i));
                    }

                    JSONArray seriesData = chartParams.getDataJson().optJSONArray("seriesData");


                    for (int i = 0; i < seriesData.length(); i++) {

                        JSONObject seriesObj = seriesData.optJSONObject(i);
                        JSONArray dataArr = seriesObj.optJSONArray("data");
                        for (int j = 0; j < dataArr.length(); j++) {
                            HashMap<String, Object> map1 = new HashMap<>();
                            JSONArray data = dataArr.optJSONArray(j);
                            if (data.length() > 2) {
                                map1.put("x", data.opt(0));
                                map1.put("y", data.opt(1));
                                map1.put("z", data.opt(2));
                            }
                            bubbleArrayList.add(j, map1);


                        }

                    }

                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(xAxisList));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});

                    HIYAxis yaxiss = new HIYAxis();
                    yaxiss.setTitle(new HITitle());
                    yaxiss.getTitle().setText("");
                    yaxiss.setCategories(new ArrayList<>(yAxisList));
                    hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                        add(yaxiss);
                    }});

                 String seriesName = "";
                ArrayList<String> columnList = getColumnList(chartParams,"values");
                if(columnList.size()>0){
                  seriesName =  chartParams.getColumnMapDataJson().optString(columnList.get(0));
                }

                HIBubble series1 = new HIBubble();
                series1.setName(seriesName);
                series1.setColor(HIColor.initWithRGB(79, 195, 247));
                series1.setData(new ArrayList<>(bubbleArrayList));
                hiOptions.setSeries(new ArrayList<>(Arrays.asList(series1)));
                return hiOptions;
            }
            else if (
                    chartParams.getChartType().equalsIgnoreCase("Heatmap")
            ) {

                //For Chart Type
                HIOptions hiOptions = new HIOptions();

                HIChart chart = new HIChart();
                chart.setType("heatmap");
                chart.setMarginTop(40);
                chart.setMarginBottom(80);
                chart.setPlotBorderWidth(1);
                hiOptions.setChart(chart);

                HIPane pane = new HIPane();
                pane.setStartAngle(0);
                pane.setEndAngle(360);
                hiOptions.setPane(pane);


                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotOptions = new HIPlotOptions();
                plotOptions.setSeries(new HISeries());
                hiOptions.setPlotOptions(plotOptions);




                ArrayList<HashMap> bubbleArrayList = new ArrayList<>();

                ArrayList<String> xAxisList = new ArrayList<>();
                ArrayList<String> yAxisList = new ArrayList<>();
                JSONArray yFieldData = chartParams.getDataJson().optJSONArray("yFieldData");
                JSONArray xFieldData = chartParams.getDataJson().optJSONArray("xFieldData");

                for(int i=0;i<yFieldData.length();i++){
                    yAxisList.add(i,yFieldData.optString(i));
                }
                for(int i=0;i<xFieldData.length();i++){
                    xAxisList.add(i,xFieldData.optString(i));
                }

                JSONArray seriesData = chartParams.getDataJson().optJSONArray("seriesData");


                for(int i=0;i<seriesData.length();i++){

                    JSONObject seriesObj = seriesData.optJSONObject(i);
                    JSONArray dataArr = seriesObj.optJSONArray("data");
                    for(int j=0;j<dataArr.length();j++){
                        HashMap<String, Object> map1 = new HashMap<>();

                        JSONArray data = dataArr.optJSONArray(j);
                        if(data.length()>2){
                            map1.put("x", data.opt(0));
                            map1.put("y", data.opt(1));
                            map1.put("z", data.opt(2));
                        }
                        bubbleArrayList.add(j,map1);


                    }

                }


                HIXAxis xaxis = new HIXAxis();
                xaxis.setCategories(new ArrayList<>(xAxisList));
                hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                    add(xaxis);
                }});


                HIYAxis yaxiss = new HIYAxis();

                yaxiss.setTitle(new HITitle());
                yaxiss.getTitle().setText("");


                yaxiss.setCategories(new ArrayList<>(yAxisList));
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxiss);
                }});

                String seriesName = "";
                ArrayList<String> columnList = getColumnList(chartParams,"values");
                if(columnList.size()>0){
                    seriesName =  chartParams.getColumnMapDataJson().optString(columnList.get(0));
                }

                HIHeatmap series1 = new HIHeatmap();
                series1.setName(seriesName);
                series1.setColor(HIColor.initWithRGB(157, 200, 241));
                series1.setData(new ArrayList<>(bubbleArrayList));
                hiOptions.setSeries(new ArrayList<>(Arrays.asList(series1)));

                hiOptions.additionalOptions = new HashMap<>();
                HashMap<String, Object> colorAxisOptions = new HashMap<>();
                colorAxisOptions.put("min", 0);
                colorAxisOptions.put("minColor", "#FFFFFF");
                colorAxisOptions.put("maxColor", "#7cb5ec");
                hiOptions.additionalOptions.put("colorAxis", colorAxisOptions);


                return hiOptions;
            }
            else if (
                    chartParams.getChartType().equalsIgnoreCase("Donut")

            ) {


                //For Chart Type
                HIOptions hiOptions = new HIOptions();


                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotoptions = new HIPlotOptions();
                plotoptions.setPie(new HIPie());
               // plotoptions.getPie().setShadow(false);
                plotoptions.getPie().setCenter(new ArrayList<>(Arrays.asList("50%", "50%")));
                hiOptions.setPlotOptions(plotoptions);


                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<HashMap> hiPieCatArrayList = new ArrayList<>();
                ArrayList<HashMap> hiPieChildArrayList = new ArrayList<>();

                HIPie pie1 = new HIPie();
                pie1.setSize("60%");

                HIPie pie2 = new HIPie();
                pie2.setSize("80%");
                pie2.setInnerSize("60%");

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jobj = jsonArray.optJSONObject(i);
                    JSONArray catArr = jobj.optJSONArray("categories");
                    for(int k=0;k<catArr.length();k++){
                        HashMap<String, Object> map1 = new HashMap<>();
                        map1.put("name", catArr.optString(k));

                        JSONArray dataAr = jobj.optJSONArray("data");
                        for(int l=0;l<dataAr.length();l++){

                            JSONObject jso = dataAr.optJSONObject(l);
                            String name =  jso.optJSONObject("drilldown").optString("parent");
                            if(catArr.optString(k).equalsIgnoreCase(name)){
                                map1.put("y", jso.opt("y"));
                                map1.put("color", jso.optString("color"));
                                hiPieCatArrayList.add(map1);
                                // }
                            }



                        }

                    }

                }

                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject jobj = jsonArray.optJSONObject(i);
                    for(int j=0;j<jobj.optJSONArray("data").length();j++){
                        JSONObject jso = jobj.optJSONArray("data").optJSONObject(j);
                        JSONObject drillDownObj = jso.optJSONObject("drilldown");
                        String childName = drillDownObj.optString("name");
                        String childColor = drillDownObj.optString("color");
                            for(int m=0;m<drillDownObj.optJSONArray("data").length();m++){
                        HashMap<String, Object> map2 = new HashMap<>();
                        map2.put("name", drillDownObj.optJSONArray("categories").opt(m));
                        map2.put("y", drillDownObj.optJSONArray("data").opt(m));
                        map2.put("color", childColor);
                        hiPieChildArrayList.add(map2);
                            }
                    }

                }

                pie1.setData(new ArrayList<>(hiPieCatArrayList));
                pie2.setData(new ArrayList<>(hiPieChildArrayList));


                hiOptions.setSeries(new ArrayList<>(Arrays.asList(pie1, pie2)));
                return hiOptions;

            }

            else if (
                             chartParams.getChartType().equalsIgnoreCase("Donut3D")
            ) {

                //For Chart Type
                HIOptions hiOptions = new HIOptions();

                HIChart chart = new HIChart();
                chart.setType("pie");

                chart.setOptions3d(new HIOptions3d());
                chart.getOptions3d().setEnabled(true);
                chart.getOptions3d().setAlpha(45);
                hiOptions.setChart(chart);

                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotOptions = new HIPlotOptions();
                plotOptions.setPie(new HIPie());
               // plotOptions.getPie().setInnerSize(0);
                plotOptions.getPie().setCenter(new ArrayList<>(Arrays.asList("50%", "50%")));

                plotOptions.getPie().setDepth(45);
                hiOptions.setPlotOptions(plotOptions);


                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<HashMap> hiPieCatArrayList = new ArrayList<>();
                ArrayList<HashMap> hiPieChildArrayList = new ArrayList<>();

                HIPie pie1 = new HIPie();
                pie1.setSize("50%");

                HIPie pie2 = new HIPie();
                pie2.setSize("70%");
                pie2.setInnerSize("70%");

                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jobj = jsonArray.optJSONObject(i);
                    JSONArray catArr = jobj.optJSONArray("categories");
                    for(int k=0;k<catArr.length();k++){

                        HashMap<String, Object> map1 = new HashMap<>();
                        map1.put("name", catArr.optString(k));

                        JSONArray dataAr = jobj.optJSONArray("data");
                        for(int l=0;l<dataAr.length();l++){

                            JSONObject jso = dataAr.optJSONObject(l);
                            String name =  jso.optJSONObject("drilldown").optString("parent");
                            if(catArr.optString(k).equalsIgnoreCase(name)){
                                map1.put("y", jso.opt("y"));
                                map1.put("color", jso.optString("color"));
                                hiPieCatArrayList.add(map1);
                            }
                        }

                    }

                }

                for(int i=0;i<jsonArray.length();i++) {
                    JSONObject jobj = jsonArray.optJSONObject(i);
                    for(int j=0;j<jobj.optJSONArray("data").length();j++){
                        JSONObject jso = jobj.optJSONArray("data").optJSONObject(j);
                        JSONObject drillDownObj = jso.optJSONObject("drilldown");
                        String childName = drillDownObj.optString("name");
                        String childColor = drillDownObj.optString("color");
                            for(int m=0;m<drillDownObj.optJSONArray("data").length();m++) {
                                HashMap<String, Object> map2 = new HashMap<>();
                                map2.put("name", drillDownObj.optJSONArray("categories").opt(m));
                                map2.put("y", drillDownObj.optJSONArray("data").opt(m));
                                map2.put("color", childColor);
                                hiPieChildArrayList.add(map2);
                            }
                    }

                }

                pie1.setData(new ArrayList<>(hiPieCatArrayList));
                pie2.setData(new ArrayList<>(hiPieChildArrayList));


                hiOptions.setSeries(new ArrayList<>(Arrays.asList(pie1, pie2)));


                return hiOptions;

            }
            else if (
                    chartParams.getChartType().equalsIgnoreCase("SemiPie")

            ) {


                HIOptions hiOptions = new HIOptions();

                HIChart chart = new HIChart();
                chart.setType("pie");
                chart.setBackgroundColor(null);
                chart.setPlotBorderWidth(0);
              //  chart.setPlotShadow(false);
                hiOptions.setChart(chart);

                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotOptions = new HIPlotOptions();
                plotOptions.setPie(new HIPie());
                plotOptions.getPie().setStartAngle(-90);
                plotOptions.getPie().setEndAngle(90);
                String[] centerList = new String[] {"50%", "75%" };
                plotOptions.getPie().setCenter(new ArrayList<>(Arrays.asList(centerList)));
                hiOptions.setPlotOptions(plotOptions);

                HITooltip tooltip = new HITooltip();
                tooltip.setPointFormat("<b>{point.percentage:.2f}%</b> {series.name}: <b>{point.y}</b>");

                hiOptions.setTooltip(tooltip);

                String xAxisId = chartParams.getXAxisId();

                ArrayList<String> valueList = getColumnList(chartParams,"values");
                ArrayList<String> sliceList = getColumnList(chartParams,"slice");
                String valueId = valueList.get(0);
                String sliceId = sliceList.get(0);


                String  seriesName =  chartParams.getColumnMapDataJson().optString(valueId);
                ArrayList<Object[] > pieArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                HIPie pie = new HIPie();
                pie.setName(seriesName);

                pie.setInnerSize("50%");

                for (int j = 0; j < jsonArray.length(); j++) {
                    JSONObject jobj = jsonArray.optJSONObject(j);

                    String firstParam = "";
                    if(jobj.has(xAxisId)){
                        firstParam = jobj.optJSONObject("_id").optString(sliceId)+":"+jobj.opt(xAxisId).toString();
                    }else{
                        firstParam = jobj.optJSONObject("_id").optString(sliceId) +":"+ jobj.optDouble(valueId,0);
                    }
                    Object[] object1 = new Object[] { firstParam, jobj.optDouble(valueId,0) };
                    pieArrayList.add(object1);
                }

                pie.setData(new ArrayList<>(pieArrayList));

                hiOptions.setSeries(new ArrayList<>(Arrays.asList(pie)));

                return hiOptions;
            }

            else if (
                    chartParams.getChartType().equalsIgnoreCase("StackedPercentageBar")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();


                //For Chart Type
                HIOptions hiOptions = new HIOptions();
                HIChart chart = new HIChart();
                chart.setType("bar");
                hiOptions.setChart(chart);

                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotOptions = new HIPlotOptions();
                plotOptions.setSeries(new HISeries());
                plotOptions.getSeries().setStacking("percent");
                hiOptions.setPlotOptions(plotOptions);


                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setMax(120);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});


                String xAxisId = chartParams.getXAxisId();
                ArrayList<HIBar> barArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();

                ArrayList<String> columnList = getColumnList(chartParams,"values");
                ArrayList<String> legendList = getColumnList(chartParams,"legend");

                if(legendList.size()==0) {

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        categoryList.add(i, xAxis);
                    }

                    for (int i = 0; i < columnList.size(); i++) {
                        HIBar bar1 = new HIBar();
                        valuesList.clear();
                        String valueId = columnList.get(i);
                        if (chartParams.getColumnMapDataJson().has(valueId)) {
                            bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                        }
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jobj = jsonArray.optJSONObject(j);
                            valuesList.add(jobj.optDouble(valueId, 0));
                            bar1.setData(new ArrayList<>(valuesList));
                        }
                        barArrayList.add(bar1);
                    }

                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryList));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }else {
                    String legendId = legendList.get(0);

                    Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                    Set<String> categoryHashSet = new LinkedHashSet<>();


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jobj = jsonArray.optJSONObject(i);
                        String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                        String legengCat = jobj.optJSONObject("_id").optString(legendId);
                        legendLinkedHashSet.add(legengCat);
                        categoryHashSet.add(xAxis);
                        categoryList.add(i, xAxis);
                    }
                    String valueId = columnList.get(0);

                    for (String currentLegend : legendLinkedHashSet) {
                        HIBar bar1 = new HIBar();
                        bar1.setName(currentLegend);
                        valuesList.clear();
                        for(String currCategory : categoryHashSet){
                            double valurr = 0;
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (currCategory.equalsIgnoreCase(jobj.optJSONObject("_id").optString(xAxisId)) &&
                                        currentLegend.equalsIgnoreCase(jobj.optJSONObject("_id").optString(legendId))) {
                                    valurr = jobj.optDouble(valueId, 0);
                                }
                            }
                            valuesList.add(valurr);
                        }

                        bar1.setData(new ArrayList<>(valuesList));
                        barArrayList.add(bar1);
                    }


                    HIXAxis xaxis = new HIXAxis();
                    xaxis.setCategories(new ArrayList<>(categoryHashSet));
                    hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                        add(xaxis);
                    }});
                }

                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            }

            else if (
                    chartParams.getChartType().equalsIgnoreCase("Scatter")
            ) {

                //For Chart Type
                HIOptions hiOptions = new HIOptions();
                try {


                    HIChart chart = new HIChart();
                    chart.setType("scatter");
                    chart.setPlotBorderWidth(1);
                    chart.setZoomType("xy");
                    hiOptions.setChart(chart);


                    hiOptions = setCommonFeatures(hiOptions, chartParams);

                    String xAxisId = chartParams.getXAxisId();
                    ArrayList<HashMap<String, Object>> barArrayList = new ArrayList<>();
                    ArrayList<HIScatter> hiScatterArrayList = new ArrayList<>();

                    JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                    ArrayList<String> categoryList = new ArrayList<>();
                    ArrayList<Number> valuesList = new ArrayList<>();

                    ArrayList<String> xAxisList = getColumnList(chartParams,"axis");
                    ArrayList<String> yAxisList = getColumnList(chartParams,"values");
                    ArrayList<String> columnList = new ArrayList<>();
                    columnList.addAll(xAxisList);
                    columnList.addAll(yAxisList);



                    ArrayList<String> legendList = getColumnList(chartParams,"legend");
                    if(legendList.size()==0){


                        for (int i = 0; i < jsonArray.length(); i++) {
                            HashMap<String, Object> bar1 = new HashMap<String, Object>();
                            JSONObject jobj = jsonArray.optJSONObject(i);
                            bar1.put("x",jobj.optJSONObject("_id").optDouble(xAxisList.get(0),0));
                            bar1.put("y",jobj.optDouble(yAxisList.get(0),0));
                            bar1.put("name",jobj.optJSONObject("_id").opt(xAxisList.get(0)).toString());
                            barArrayList.add(bar1);
                        }


                    }else {
                        String legendId = legendList.get(0);

                        Set<String> legendLinkedHashSet = new LinkedHashSet<>();
                        Set<String> categoryHashSet = new LinkedHashSet<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jobj = jsonArray.optJSONObject(i);
                            String legengCat = jobj.optString(legendId);
                            legendLinkedHashSet.add(legengCat);
                        }

                        for (String currentLegend : legendLinkedHashSet) {

                            for (int j = 0; j < jsonArray.length(); j++) {
                                HashMap<String, Object> bar1 = new HashMap<String, Object>();
                                JSONObject jobj = jsonArray.optJSONObject(j);
                                if (
                                        currentLegend.equalsIgnoreCase(jobj.optString(legendId))) {
                                    bar1.put("x",jobj.optJSONObject("_id").optDouble(xAxisList.get(0),0));
                                    bar1.put("y",jobj.optDouble(yAxisList.get(0),0));
                                    bar1.put("name",jobj.optJSONObject("_id").opt(xAxisList.get(0)).toString());
                                    barArrayList.add(bar1);

                                }
                            }


                        }

                    }


                    HIPlotOptions plotOptions = new HIPlotOptions();
                    plotOptions.setScatter(new HIScatter());
                    plotOptions.getScatter().setMarker(new HIMarker());
                    plotOptions.getScatter().getMarker().setRadius(5);
                    plotOptions.getScatter().getMarker().setStates(new HIStates());
                    plotOptions.getScatter().getMarker().getStates().setHover(new HIHover());
                    plotOptions.getScatter().getMarker().getStates().getHover().setEnabled(true);
                    plotOptions.getScatter().getMarker().getStates().getHover().setLineColor(HIColor.initWithRGB(100, 100, 100));
                    plotOptions.getScatter().setTooltip(new HITooltip());
                    plotOptions.getScatter().getTooltip().setHeaderFormat("<b>{point.name}</b><br>");

                    plotOptions.getScatter().getTooltip().setPointFormat(chartParams.getColumnMapDataJson().optString(xAxisList.get(0))+" : {point.name} , "+chartParams.getColumnMapDataJson().optString(yAxisList.get(0))+" : {point.y}");
                    hiOptions.setPlotOptions(plotOptions);


                    hiOptions.setPlotOptions(plotOptions);
                    HIScatter series1 = new HIScatter();
                    series1.setColor(HIColor.initWithRGBA(157, 200, 241,0.6));
                    series1.setData(barArrayList);
                    hiOptions.setSeries(new ArrayList<>(Arrays.asList(series1)));


                }catch (Exception e){

                }

                return hiOptions;
            }

            else if (
                    chartParams.getChartType().equalsIgnoreCase("LineLine")
            ) {
                String xAxisTitle = chartParams.getXAxisTitle();
                String yAxisTitle = chartParams.getYAxisTitle();

                //For Chart Type
                HIOptions hiOptions = new HIOptions();
                hiOptions = setCommonFeatures(hiOptions, chartParams);

                HIPlotOptions plotoptions = new HIPlotOptions();
                plotoptions.setSeries(new HISeries());
                plotoptions.getSeries().setLabel(new HILabel());
                plotoptions.getSeries().getLabel().setConnectorAllowed(false);
                hiOptions.setPlotOptions(plotoptions);

                HIYAxis yaxis = new HIYAxis();
                yaxis.setMin(0);
                yaxis.setTitle(new HITitle());
                yaxis.getTitle().setText(yAxisTitle);
                yaxis.getTitle().setAlign("high");
                yaxis.setLabels(new HILabels());
                yaxis.getLabels().setOverflow("justify");
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yaxis);
                }});

                HIYAxis yAxis = new HIYAxis();
                yAxis.setMin(0);
                yAxis.setTitle(new HITitle());
                yAxis.getTitle().setText(yAxisTitle);
                yAxis.setOpposite(true);
                hiOptions.setYAxis(new ArrayList<HIYAxis>() {{
                    add(yAxis);
                }});
                hiOptions.setYAxis(new ArrayList<>(Arrays.asList(yAxis,yaxis)));

                String xAxisId = chartParams.getXAxisId();
                ArrayList<HISeries > barArrayList = new ArrayList<>();

                JSONArray jsonArray = chartParams.getDataJson().optJSONArray("data");
                ArrayList<String> categoryList = new ArrayList<>();
                ArrayList<Number> valuesList = new ArrayList<>();

                ArrayList<String> columnListColumn = getColumnList(chartParams,"values-line-right");
                ArrayList<String> columnList = getColumnList(chartParams,"values-line");


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jobj = jsonArray.optJSONObject(i);
                    String xAxis = jobj.optJSONObject("_id").optString(xAxisId);
                    categoryList.add(i, xAxis);
                }


                for (int i = 0; i < columnListColumn.size(); i++) {
                    HILine bar1 = new HILine();
                    bar1.setLabel(new HILabel());
                    bar1.setYAxis(0);
                    valuesList.clear();
                    String valueId = columnListColumn.get(i);
                    if(chartParams.getColumnMapDataJson().has(valueId)){
                        bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jobj = jsonArray.optJSONObject(j);
                        valuesList.add(jobj.optDouble(valueId,0));
                        bar1.setData(new ArrayList<>(valuesList));
                    }
                    barArrayList.add(bar1);
                }

                for (int i = 0; i < columnList.size(); i++) {
                    HILine bar1 = new HILine();
                    bar1.setYAxis(1);
                    valuesList.clear();
                    String valueId = columnList.get(i);
                    if (chartParams.getColumnMapDataJson().has(valueId)) {
                        bar1.setName(chartParams.getColumnMapDataJson().optString(valueId));
                    }
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jobj = jsonArray.optJSONObject(j);
                        valuesList.add(jobj.optDouble(valueId, 0));
                        bar1.setData(new ArrayList<>(valuesList));
                    }
                    barArrayList.add(bar1);
                }


                HIXAxis xaxis = new HIXAxis();
                xaxis.setCategories(new ArrayList<>(categoryList));
                hiOptions.setXAxis(new ArrayList<HIXAxis>() {{
                    add(xaxis);
                }});


                hiOptions.setSeries(new ArrayList<>(barArrayList));
                return hiOptions;
            }







        } catch (Exception e) {

        }
        return null;
    }

    public static HIOptions setCommonFeatures(HIOptions hiOptions, ChartParams chartParams) {

        String chartTitle = chartParams.getChartTitle();
        String chartSubTitle = chartParams.getChartSubTitle();

        //For Export as Image Option
        HIExporting exporting = new HIExporting();
        exporting.setEnabled(chartParams.getEnableExport());
        hiOptions.setExporting(exporting);

        HICredits credits = new HICredits();
        credits.setEnabled(false);
        hiOptions.setCredits(credits);

        HITitle title = new HITitle();
        title.setText(chartTitle);
        hiOptions.setTitle(title);

        HISubtitle subtitle = new HISubtitle();
        subtitle.setText(chartSubTitle);
        hiOptions.setSubtitle(subtitle);

        HILegend legend = new HILegend();
        legend.setEnabled(false);
        hiOptions.setLegend(legend);

        return hiOptions;
    }

    public static ArrayList<String> getColumnList(ChartParams chartParams,String zoneName){
        ArrayList<String> columnList = new ArrayList<>();
        JSONArray configArr = chartParams.getDataViewJson().optJSONObject("visualizations")
                .optJSONObject("configuration").optJSONArray("config");
        for (int i = 0; i < configArr.length(); i++) {
            JSONObject configObj = configArr.optJSONObject(i);
            if (configObj.optString("zone").equalsIgnoreCase(zoneName)) {
                JSONArray columsArr = configObj.optJSONArray("columns");
                if(columsArr==null){
                    columsArr = new JSONArray();
                }
                for (int j = 0; j < columsArr.length(); j++) {
                    JSONObject columsObj = columsArr.optJSONObject(j);
                    columnList.add(j, columsObj.optString("columnId"));
                }
            }
        }
        return columnList;
    }


    public static ArrayList<String> getLegendList(ChartParams chartParams,String zoneName){
        ArrayList<String> columnList = new ArrayList<>();
        JSONArray configArr = chartParams.getDataViewJson().optJSONObject("visualizations")
                .optJSONObject("configuration").optJSONArray("config");
        for (int i = 0; i < configArr.length(); i++) {
            JSONObject configObj = configArr.optJSONObject(i);
            if (configObj.optString("zone").equalsIgnoreCase(zoneName)) {
                JSONArray columsArr = configObj.optJSONArray("columns");
                for (int j = 0; j < columsArr.length(); j++) {
                    JSONObject columsObj = columsArr.optJSONObject(j);
                    columnList.add(j, columsObj.optString("columnId"));
                }
            }
        }
        return columnList;
    }
}

