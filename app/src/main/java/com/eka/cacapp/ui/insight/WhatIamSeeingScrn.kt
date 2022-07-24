package com.eka.cacapp.ui.insight

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.eka.cacapp.R


class WhatIamSeeingScrn (mContext: Context, themeId: Int,appTtl :String): Dialog(mContext,themeId) {

    private lateinit var closeImg : ImageView
    private lateinit var txtView : TextView
    private var appTitle = appTtl
    init {
        setCancelable(false)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.what_imseeing_scrn)
        closeImg =  findViewById<ImageView>(R.id.close_icon)
        txtView =  findViewById<TextView>(R.id.txt_vw)

        closeImg.setOnClickListener {
            this.dismiss()
        }

        getWhatImSeeingForAppName(appTitle,txtView)


    }
    private fun getWhatImSeeingForAppName(appName : String,textView : TextView){

        when {
            appName.equals("Regulatory and Compliance",true) -> {

                addHeaderText("Used:\n")
                addNormalText("EU - EMIR - Sample Derivative Transactions US - CFTC 204 - Physical Transactions Grain Commodity Swiss – FinFrag – Sample Derivative Transactions\n\n")

                addHeaderText("Intelligence Engine :\n")
                addNormalText("EMIR - Report as Unavista spec with Deal Life Cycle CFTC 204 - Grain Position summary as per CFTC Spec FinFrag – Report as per SIX TR Spec including Deal life cycle\n\n")

                addHeaderText("Dataset:\n")
                addNormalText("> EMIR - Sample EU\n> FinFrag - Swiss Derivatives Transaction\n> US CFTC 204 - Physicals position exposure output")

            }
            appName.equals("Position and Mark to Market",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Open Physicals Contracts, Derivative Contracts and Inventory data\n\n")

                addHeaderText("Intelligence Engine :\n")
                addNormalText("Daily Position &amp; Price exposures, Mark to Market and Unrealized P&amp;L factoring in rollover for expired futures contracts. Exposure modelling allows for risk  distribution across delivery period and is reported in terms of As-Is, Input/ Output Processing Product or Cross Hedging.\n\n")

                addHeaderText("Dataset:\n")
                addNormalText("Open Physical and Derivative trade Data\nInventory Data\nBasis Prices and Futures\nData Quality and Location Differentials\nData Fx Rates Plant Conversion Yield Factors\nCross Hedge Ratio")

            }
            appName.equals("P&L Explained",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Previous and Current period contract, inventory, execution, market and invoice/accrual data\n\n")

                addHeaderText("Intelligence Engine :\n")
                addNormalText("Taking every variable that impacts Position and P&amp;L and measuring its standalone impact on the change in Position and P&amp;L. This computation is triggered for each of the variables, while maintaining other variables as constant, resulting in 60 attribution buckets.\n\nAttribution buckets across Market Movements, New Contracts, Contract Changes, Executions, Processing, Accruals and Invoicing.\n\n")

                addHeaderText("Dataset:\n")
                addNormalText("Physicals and Derivatives Trades\n" +
                        "Mark to Market\n" +
                        "Market Prices\n" +
                        "Execution Details\n" +
                        "Accrual Details")

            }
            appName.equals("Trade Finance",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Bank Cost Quotations and Trade Finance Scenarios\n\n")

                addHeaderText("Intelligence Engine :\n")
                addNormalText("Most Cost Effective trade finance opportunities for Loans and LCs, by generating all the Banks opportunities based on its Financing, Advising, Negotiating and Confirming Roles.\n\n")

            }
            appName.equals("VaR",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Trade information such as physical trades, futures, options, currency forwards. Historical price, FX rates, and interest rates data from different sources. User defines VaR parameters such as VaR Horizon, historical Price period to be used, VaR Confidence levels, number of iterations (for Monte Carlo Simulation method) and so on.\n\n")

                addHeaderText("Filtered On:\n")
                addNormalText("Portfolios created dynamically by users based on their requirements. A portfolio can contain trades of different types filtered on one or more filter criteria (e.g. Profit Center, Strategy, Product)\n\n")


                addHeaderText("Intelligence Engine :\n")
                addNormalText("Volatility and Correlation between different curves, asset classes and maturities.VaR can be created using different methods like Parametric (Variance-Covariance), Monte Carlo Simulation and Historical Simulation methods. Other measures such as Shortfall, Undiversified VaR, Standard Deviation of P&amp;L, Marked to Market (MTM) Value and P&amp;L frequency distribution are also returned by the engine.\n\n")

                addHeaderText("Dataset:\n")
                addNormalText("Trades\n" +
                        "Market Data")

            }
            appName.equals("Risk and Monitoring",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Multiple measures for monitoring such as P&amp;L value, VaR, Position Exposures, Market Prices and more\n\n")


                addHeaderText("Intelligence Engine :\n")
                addNormalText("Compare set limits against computed output values (computed by other Apps or other sources) to report threshold and limit breaches\n\n")


            }
            appName.equals("Purchase Analysis",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Quality Properties, Contracts/Trades, Market data - Price, Market data -FX, Master Data, Landfill Cost, Charges(TC/RC/Penalties) and Interest Rate\n\n")

                addHeaderText("Filtered On:\n")
                addNormalText("Entire data is used to do calculations\n\n")


                addHeaderText("Intelligence Engine :\n")
                addNormalText("-Calculate Raw Material Margin (RMM) in real-time for a combination of Quality, Feeding Point and Supplier.\n-Provides result of RMM for selected qualities on latest contracts from different suppliers if the ore is processed in different feeding points of smelters.\n\n")

                addHeaderText("Simulation Output :\n")
                addNormalText("Ability to simulate RMM calculation in real- time and thereby allow comparison of RMM results across multiple scenarios by shocking one or more of the below input parameters:· Quality assay values by metals· Contract info (Payable) by suppliers· Charges (TC/RC/Penalty) by suppliers· Market prices of metal prices across different time horizons· FX rates across different time horizons\n\n")

                addHeaderText("Dataset :\n")
                addNormalText("Quality (Assay Information), Contract, Processor Data (Smelter &amp; Feeding Point Properties), Other Costs (Landfill Costs, etc), Market Prices for traded elements across different purchasing options/time horizons like spot, budget and Long term. FX Rates")


            }
            appName.equals("Procurement Analysis",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Plan, Cost Model, Coverage and Spend Information for multiple Items (Raw Materials), Plants and Suppliers.\n\n")

                addHeaderText("Filtered On:\n")
                addNormalText("Portfolios created dynamically by users based on their requirements. A portfolio can contain different items filtered on one or more filter criteria (e.g. Plant, Buyer, Commodity, Category, etc.)\n\n")


                addHeaderText("Computed :\n")
                addNormalText("Coverage Percentages, Spend (Actual, Covered, Uncovered, Forecasted) and Variance (From Plan) for different items. The numbers can be combined to get results at category, sub-category, plant, etc. levels. In addition, the simulation module allows users to simulate probable market or business scenarios and see their impact on key procurement measures.\n\n")

                addHeaderText("Dataset :\n")
                addNormalText("Item &amp; Plan Data\n" +
                        "            Cost Model DataCoverage\n" +
                        "            Information")


            }
            appName.equals("Inventory Analytics",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Market prices, actual delivery, nominations data and inventory\n\n")

                addHeaderText("Computed:\n")
                addNormalText("Predictive analysis allows users to see likely trend based on past pattern and helps analysing inventory COG VS market to make better buy and sell decisions. Computes projected inventory helping traders in decision making and optimizing inventory position for profits. Inventory Limits helps to track inventory and notify.\n\n")


                addHeaderText("Dataset :\n")
                addNormalText("DS- Physicals\n" +
                        "DS- Market Prices\n" +
                        "DS Inventory Actuals\n" +
                        "DS Inventory Nominations")


            }
            appName.equals("Plan Performance",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Operation Plan and Operation Actuals\n\n")

                addHeaderText("Computed:\n")
                addNormalText("Actulas data modelled and aggregated to match the granularity of Production Plan.Each Task,Equipment and Resource of the Operation Actuals tagged with the Operation Plan to get as-per-plan and deviations metrices.\n\n")


                addHeaderText("\n6 performance metrics:\n")
                addNormalText("On-time,Off-Time,Planned Tonnage,Unplanned Tonnage,In-Sequence,Out-of-Sequence\n\n")

                addHeaderText("Dataset :\n")
                addNormalText("Operation Plan\n" +
                        "Operation Actulas\n" +
                        "Equipment,shift Roster and Delay\n" +
                        "Definitions")


            }
            appName.equals("Pre-Trade Analysis",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Logistic Route and INCO Term mapping,Revised Volume functionally and Product Cost methodology with User input Pre-Trade Scenario,Routes and Costs.\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("Suggests all possible logistic routes with their corresponding product locations. The app also calculates and suggests the Volume to offer based on logistic stowage and gives the best possible Offer/SalePrice and Margin.\n\n")

                addHeaderText("Dataset :\n")
                addNormalText(">Pre-Trade Scenario\n" +
                        ">Logistic Routes and Freights\n" +
                        ">Product Costs\n" +
                        ">Custom Costs\n" +
                        ">Processing Costs\n" +
                        ">FX Rates\n" +
                        ">Finance Costs\n" +
                        ">Any Other Costs")


            }
            appName.equals("Farmer Connect",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Data available and stored in the transaction system,accounting system,spread sheets and ticket information system.\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("Retrieves,filters and based on the Farmer preference,generates Insights and Alerts for Offers,Existing Contracts and their Status, Tickets/Transportation Details,and Sales and Invoice/Payments data with the Company.\n\n")


                addHeaderText("Dataset :\n")
                addNormalText(">Price Sheet\n" +
                        ">Contract Information\n" +
                        ">Ticket Details\n" +
                        ">Accounting")


            }
            appName.equals("Disease Prediction",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Historical Rust Incidence and Rust Severity with weather parameters\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("The model uses advance analytics agriculture feature engineering,which is modelled to factor in weather that impacts the plant,such as sunshine period,relative humidity,cloud cover,and other weather parameters.This with historical analysis such as seasonality,variety,location and other factors are modelled to bring out the Disease Prediction\n\n")


                addHeaderText("Dataset :\n")
                addNormalText(">Weather Data Daily\n" +
                        ">Weather Data Hourly\n" +
                        ">Historical Disease Incidence Data")


            }
            appName.equals("Yield Forecast",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Multiple farm variables and normalized weather variables at each location\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("Independent variables by means of Agriculture specific feature engineering.This was supplied to the Machine Learning algorithm,to create an ensemble model of weak learners that continually partitions to minimize residuals.\n\n")


                addHeaderText("Dataset :\n")
                addNormalText(">Historical Production Data\n" +
                        ">Weather Data\n" +
                        ">Soil Data")


            }
            appName.equals("Cash Flow",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Past payment behaviour in addition to Current Open Contract Value,Payment terms,Shipment Status\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("Predict Payment dates using historical trend analysis of the business partner.\n\n")


            }
            appName.equals("Crop Intelligence",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Component of Farm Health, Weather and Soil Sub Components\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("Using Machine Learning and Historical Analysis derive Farm Performance parameters that need immediate attention.\n\n")


            }
            appName.equals("Disease Risk Assessment",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Historical weather data across locations and forecasts,recommended growing conditions for the given crop,location wise history of disease occurrence and severity\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText(">Uses machine learning model to compute the probability of disease outbreak in a given location based on history of incidences and forecasted weather\n" +
                        ">Assigns normalised weather risk scores to locations, by comparing actual weather with the recommended growing conditions\n" +
                        ">Assigns normalised disease risk scores to locations based on number and severity of incidences\n" +
                        ">Calculates a combined risk score for every location, by assigning user-defined weightages to weather and disease risk scores \n\n")


            }
            appName.equals("Basis Analysis",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Basis Price and Futures Price for Wheat and Corn\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("Daily Basis change percentages, average contract basis and market basis for corn and wheat\n\n")


                addHeaderText("Dataset :\n")
                addNormalText(">Historical Basis Prices\n" +
                        ">Future")


            }
            appName.equals("Freight Exposure",true) -> {

                addHeaderText("Used:\n")
                addNormalText("FFA Contracts, Freight Prices\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("Freight Exposure calculated and Outliers detected for every route. In addition,real time limits and threshold tracking for every contract and route, and notifications/alerts sent.\n\n")


            }
            appName.equals("Logistics Operations Analysis",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Dead Freight Quantity,Dead Freight,Deviation from Goods Movement Record,Ticket Information and Freight Costs\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("Near-Real time tracking of Logistic movements for Deviations and Delays.Auto recalculations of Accurual Adjustments for such movements.\n\n")


            }
            appName.equals("Reconciliation",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Transaction,Accounting,ERP\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("Data modelling and transformations to bring in transactions from each disparate sources to a common model to enable Reconciliations.Multiple variable across quantity,price and amount are taken up for Reconcillation.Non-reconciled records are brought out and highlighted.\n\n")


                addHeaderText("Dataset :\n")
                addNormalText(">Transaction System Data\n" +
                        ">Accounting Data\n" +
                        ">ERP Data\n" +
                        ">Broker/Clearer Provided Data")


            }
            appName.equals("Credit Risk",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Counterparty Position and Mark to Market and Ratings\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("CP Credit Scores and credit exposure\n\n")


                addHeaderText("Dataset :\n")
                addNormalText(">As-Is-Position\n" +
                        ">Mark to Market")


            }
            appName.equals("Thomson Reuters App",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Basic Curves,Market Prices,Risk Monitoring measures,Yield Forecast,COGS,Exposure,P&L,Forward Price Curves\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText(">Computes day-to day Basis Price movements and identifies the locations with highest movement.\n" +
                        ">Compares average market basis and contract basis.\n" +
                        ">Compare set risk limits against computed output values(computed by other Apps or other sources)to report threshold and limit breaches\n" +
                        ">Computes P&L Chnage over time for Energy and Ags\n" +
                        ">Computes wheat yield predictions based on historical trends\n\n")


            }
            appName.equals("Hyper Local Weather",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Weather Forecasting Information\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("For the User defined location,ascertains the geo-codes to fetch daily and hourly weather information from the closest weather station.\n\n")


                addHeaderText("Dataset :\n")
                addNormalText(">Weather Forecast")


            }
            appName.equals("Power Spread Analysis",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Market Prices of Power,Coal,Emissions and Gas,Plant Efficiency\n\n")

                addHeaderText("Filtered On:\n")
                addNormalText("Input Data is set to be region specific(EU or US)\n\n")


                addHeaderText("Intelligence Engine:\n")
                addNormalText("Calculate Theoretical value of Spark Spreads(including Clean),Dark Spreads(include Clean)\n" +
                        "Calculates Merit Order of Various Power Plant based on Margin and Efficiency(Heat Rate)to meet Market Demand and helps in Trading decision\n")


                addHeaderText("Dataset :\n")
                addNormalText(">Market Prices - Power,Coal,Gas\n" +
                        ">Emissions(EUA/CER)\n" +
                        ">Emissions Intensity %\n" +
                        ">Plant Efficiency%")


            }
            appName.equals("Quality Arbitrage Analysis",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Quality P/D Schedule, Quality Reports and Stock/Silo Information\\n\\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("Fetches the Unallocated Stocks in a Silo, and studies the Quality results to ascertain the Stocks that can qualify for another Grade. Such Stocks are picked up and additional premium is calculated to arrive at the Arbitrage Opportunity.\n\n")


            }
            appName.equals("Vessel Management",true) -> {

                addHeaderText("Used:\n")
                addNormalText("LNG Cost Details such as Market Prices at different locations, liquification costs, regasification costs. Voyage details such as chartering rates for LNG vessels, shipping times between different ports, Ship capacities, ship status, etc for different trading strategies (spot sale, domestic resale, liquification and shipping) and shipping strategies (shipping to different ports, different fuel types used\n\n")

                addHeaderText("Filtered on:\n")
                addNormalText("Entire data is used to perform the calculations\n\n")

                addHeaderText("Computed:\n")
                addNormalText("The total revenue and Profit &amp; Loss (P&L) is calculated for different trading strategies, allowing user to choose the best to go with. Similarly, for all scheduled vessels, shipping costs (including possible demurrage and bunkering charges) are calculated and allows users to choose the best strategy to go with.\n\n")


                addHeaderText("Dataset :\n")
                addNormalText(">Cost Details\n" +
                        ">Cost Details LNG Market Rates\n" +
                        ">Shipping Rates\n" +
                        ">Shipping Rates Chartering Rates\n" +
                        ">Bunkering Rates>Demurrage Rates\n" +
                        ">Ship Details\n" +
                        ">Ship/Vessel Status\n" +
                        ">Ship Details (Capacity, Boil Off Rates, Speeds, etc.)")


            }
            appName.equals("Invoice Aging",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Invoice Amounts by Counterparties\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("Highest Overdue Amount,Total Overdue,Invoice Age Ranges\n\n")


                addHeaderText("Dataset :\n")
                addNormalText(">Invoices")


            }
            appName.equals("Plant Outage",true) -> {

                addHeaderText("Used:\n")
                addNormalText("TR Market Prices, TR historical Plant Outage Data.\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("Outage trend, Price trends, Positions at Risk based on Refinery Classification, Over and under hedge analysis.\n\n")


                addHeaderText("Dataset :\n")
                addNormalText(">Sales commitments\n" +
                        ">Historical Refinery Outage Data")


            }
            appName.equals("Emissions Hedging",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Market Price of EUA, CER.\n" +
                        "Power and Co2 trades\n\n")

                addHeaderText("Filtered On:\n")
                addNormalText("Entire data is used for calculations\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("Calculate Co2 exposure above capping limit.\n" +
                        "Calculate aggregated Co2 Position for forward year.\n" +
                        "Calculate Co2 Hedges and Stock(Inventory) Details\n\n")


                addHeaderText("Dataset :\n")
                addNormalText(">Power trades to reflect demand and supply\n" +
                        ">Co2 trades to reflect Co2 emitted from Power plants\n" +
                        ">Co2 Inventory data")


            }
            appName.equals("Customer Connect",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Data available and stored in the transaction system, accounting system, spread sheets and ticket information system.\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("Retrieves, filters and based on the Counter Party/ Business Partner generates Insights and Alerts for Offers, Existing Contracts and their Status, Tickets/Transportation Details, and Sales and Invoice/Payments data with the Company.\n\n")


                addHeaderText("Dataset :\n")
                addNormalText(">Price Sheet\n" +
                        ">Contract Information\n" +
                        ">Ticket Details\n" +
                        ">Accountin")


            }
            appName.equals("Supply Demand",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Inventory, Market trends, lead times and SoH forecasts\n\n")

                addHeaderText("Computed:\n")
                addNormalText("Demand modelling and Historical analysis on Demand Vs Market Vs SoH trends. Consume and consolidate data to provide obsolescence percentage, inventory turn over ratio by product/part number.\n\n")


                addHeaderText("Dataset :\n")
                addNormalText("> Inventory Analysis\n" +
                        "> Demand Analysis\n" +
                        "> Vendor Analysis\n" +
                        "> Jet Fuel Prices")


            }
            appName.equals("Price Trend Analysis",true) -> {

                addHeaderText("Used:\n")
                addNormalText("Historical Rust Incidence and Rust Severity with weather parameters\n\n")

                addHeaderText("Intelligence Engine:\n")
                addNormalText("The model uses advance analytics agriculture feature engineering,which is modelled to factor in weather that impacts the plant,such as sunshine period,relative humidity,cloud cover,and other weather parameters.This with historical analysis such as seasonality,variety,location and other factors are modelled to bring out the Disease Prediction\n\n")


                addHeaderText("Dataset :\n")
                addNormalText(">Weather Data Daily\n" +
                        ">Weather Data Hourly\n" +
                        ">Historical Disease Incidence Data")


            }
        }

    }

    fun getAttributedSpan(mString: String,textColor : Int, backgroundColor : Int,textSize : Int,
               isBold : Boolean): Spannable{
        val spannable: Spannable = SpannableString(mString)
        spannable.setSpan(ForegroundColorSpan(textColor), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(BackgroundColorSpan(backgroundColor), 0,
                spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan( AbsoluteSizeSpan(textSize), 0, spannable.length, SPAN_INCLUSIVE_INCLUSIVE)
        if(isBold){
            spannable.setSpan(StyleSpan(Typeface.BOLD), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return spannable
    }

    fun addHeaderText(textValue : String){
        txtView.append(getAttributedSpan(textValue,
                ContextCompat.getColor(context,R.color.app_primary_clr),ContextCompat.getColor(context,R.color.white),
                context.getResources().getDimensionPixelSize(R.dimen.what_im_see_16dp),true))
    }
    fun addNormalText(textValue: String){
        txtView.append(getAttributedSpan(textValue,
                ContextCompat.getColor(context,R.color.black),ContextCompat.getColor(context,R.color.white),
                context.getResources().getDimensionPixelSize(R.dimen.what_im_see_14dp),false));
    }
}