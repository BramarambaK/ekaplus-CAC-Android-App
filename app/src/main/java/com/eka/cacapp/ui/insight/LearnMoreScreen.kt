package com.eka.cacapp.ui.insight

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.eka.cacapp.R
import com.eka.cacapp.data.qtcLayout.WorkFlowMargin
import com.eka.cacapp.utils.WorkFlowViews
import com.eka.cacapp.utils.WorkFlowViews.createTextView


class LearnMoreScreen (mContext: Context, themeId: Int,appTtl :String): Dialog(mContext,themeId) {

    private lateinit var closeImg : ImageView
    private lateinit var lineLay : LinearLayout
    private var appTitle = appTtl
    init {
        setCancelable(false)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.learn_more_scrn)
        closeImg =  findViewById<ImageView>(R.id.close_icon)
        lineLay = findViewById(R.id.basic_dyn_ll);

        closeImg.setOnClickListener {
            this.dismiss()
        }


        getWhatImSeeingForAppName(appTitle)



    }
    private fun getWhatImSeeingForAppName(appName : String){

        when {
            appName.equals("Regulatory and Compliance",true) -> {

                addNormalText("An intelligent, flexible and robust solution designed to meet necessary regulatory obligations in a dynamic regulatory environment.")
                addBgColorText("\nRole\n" +
                        "   - Risk Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture: Grains, Canola\n" +
                        "   - Energy: Physical Power, Gas, Renewables\n" +
                        "   - Derivatives: FX, Contract For Differences, Credit, Interest Rates\n")
                addNormalText("\nThe Regulatory and Compliance App provides a wide range of regulatory reports like EMIR, CFTC, MiFiD, ICE, MAR meeting different obligations, based on regulators, geography, commodity or asset class.\n" +
                        "The solution is integrated with Trade Repository, allowing users to submit transactions and track its status while meeting reporting deadlines. The highly scalable solution can be easily adapted with dynamic regulatory requirements.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Transaction Data\n" +
                        "   - Depending upon the Regulatory Report\n" +
                        "\n" +
                        "2. Market Prices from TR\n" +
                        "   - if applicable\n" +
                        "\n" +
                        "3. LEI {Legal Entity Identifier} Code from TR\n" +
                        "   - if applicable\n" +
                        "\n" +
                        "2. Frequency\n" +
                        "   - Adhoc/On Demand/Scheduled as per Regulatory Requirement\n" +
                        "\n" +
                        "3. Complexity\n" +
                        "   - Medium\n" +
                        "\n" +
                        "4. Enrichments\n" +
                        "   - Regulatory Reports are highly customized as the fields to be reported are based on the reporting requirements" )


                addHeaderText("Intelligence Engine :")
                addNormalText(" 1. Handle Multi-Geographic and Cross Commodity\n" +
                        "2. Visualization – User can utilize the reported data in various visuals, dashboards from Tracking or Surveliance perspective.\n" +
                        "3. Monitoring – Alerts and Notifications can be set up for users to manage deadlines, act upon rejection by trade repository\n" +
                        "4. Exhaustive Reporting\n" +
                        "5. Error Handling capabilities\n" +
                        "6. Deal Life Cycle Handling – Handling Deal Life Cycle Events by itself as per Trade Repository Integeration.\n" +
                        "7. UTI Generation – Can generate UTI for each transaction with either Concatenation or Algorithmic based. For e.g ISDA UTI for EMIR or UTI for REMIT\n" +
                        "8. Comprehensive Entity Mapping" )

                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Overall Summary on Single or Multiple Geography\n" +
                        "2. Status Summary with Drill Down to Report Level\n" +
                        "3. Individual Report on Consolidated Level")


            }
            appName.equals("Position and Mark to Market",true) -> {

                addNormalText("Spot opportunities and risks on time with real-time visibility into overall exposure, aligned with potential impact of dynamic market prices.")
                addBgColorText("\nRole\n" +
                        "   - Trader\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture: Grains\n" +
                        "   - Softs: Coffee, Sugar, Dairy\n" +
                        "   - Energy: Gas, Crude and Refined Oil, Biofuel\n" +
                        "   - Metals\n")
                addNormalText("\nThe Position and Mark to Market App automates a four-day manual reconciliation process into a 30 second task, providing a single point of truth on exposure. It provides real time visibility into your inventory, position and exposure. It lets you take advantage of opportunities and mitigate risks with predictive algorithms to make more informed and strategic hedging decisions.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText(" 1. Data Sources\n" +
                        "   - Physical trades\n" +
                        "   - Derivative trades\n" +
                        "   - Forward Prices from TR\n" +
                        "   - Historical prices from TR\n" +
                        "\n" +
                        "2. Frequency\n" +
                        "   - End of day and/or near real time\n" +
                        "\n" +
                        "3. Pre-Built Model\n" +
                        "   - Position Consolidation\n" +
                        "4. Complexity\n" +
                        "   - Medium\n" +
                        "5. Enrichments\n" +
                        "   - 10" )


                addHeaderText("Intelligence Engine :")
                addNormalText("  1. Consolidates position from CTRM, broker system, and multiple spreadsheets\n" +
                        "2. Conversion of units and currencies to base unit for reporting\n" +
                        "3. Support for cross hedging, hedge ratios\n" +
                        "4. Flexibility to handle all pricing types like fixed, basis fixed, index, Futures fixed, unpriced and report them under different pre-defined or user defined buckets\n" +
                        "5. Flexibility to view exposure in terms of original product, feedstock or output product\n" +
                        "6. Flexibility to take in outright or delta adjusted positions for options\n" +
                        "7. View historical and forward price trends\n" +
                        "8. User defined rules for hedging strategy\n" +
                        "9. Add additional user defined fields to customize reports\n" +
                        "10. Flexibility to support regional as well as a global view\n" +
                        "11. Alerts and monitors to signal breaches on position and prices\n" +
                        "12. Track changes and movement over a period of time" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Overall Position Analysis to view position by trade type, price type and change between yesterday and today\n" +
                        "2. Hedge analysis to Comparison between recommended hedge and actual hedge strategy\n" +
                        "3. Supply demand analysis to view long and short positions by delivery months and view market price and market price trend to decide to buy now and store vs buy later\n" +
                        "4. Monitor to identify maximum position swing by strategy, book, commodity\n" +
                        "5. View PTBF analysis to see market trend and upcoming contracts for price fixing. View historical price fixes by date to analyze effectiveness and improve subsequent decision making\n" +
                        "6. View position movement over a period of time to view trends\n" +
                        "7. View position limits and underlying trades breaching the limits\n" +
                        "8. View outliers and position movement trends")


            }
            appName.equals("P&L Explained",true) -> {

                addNormalText("Explain P&L and position movements with 99% accuracy by tracking over 150 events in a fraction of the time it took earlier.")
                addBgColorText("\nRole\n" +
                        "   - Trader, Finance, Risk Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture: Grains\n" +
                        "   - Softs: Coffee, Sugar\n" +
                        "   - Dairy\n" +
                        "   - Energy: Gas, Crude and Refined Oil, Biofuel\n" +
                        "   - Metals")
                addNormalText("\nThe P&L Explained app helps identify root causes in P&L, enabling users to make better decisions faster. Finance, audit, risk, and traders can gain a clear understanding of P&L movement by drilling down to individual transaction level. Managers can quickly spot inaccurate assumptions that could affect future trading decisions and measure the performance of traders, books, and business units.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Physical trades\n" +
                        "   - Trade Cancellations\n" +
                        "   - Derivative trades\n" +
                        "   - Forward Prices from TR\n" +
                        "   - FX rates from TR\n" +
                        "   - Basis from TR\n" +
                        "   - Location differentials\n" +
                        "   - Inventory details\n" +
                        "   - Accruals and cost estimate details\n" +
                        "   - Realized and unrealized p&l between any 2 dates\n" +
                        "\n" +
                        "2. Frequency\n" +
                        "   - End of day / Periodic\n" +
                        "\n" +
                        "3. Pre-Built Model\n" +
                        "   - P&L Attribution\n" +
                        "\n" +
                        "4. Complexity\n" +
                        "   - High\n" +
                        "\n" +
                        "5. Enrichments\n" +
                        "   - 10" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Over 50+ configurable buckets to explain P&L change most granularly and with less than 5% in unexplained\n" +
                        "2. Complex calculations to compute changes from open to execution to realization\n" +
                        "3. Make timely decisions around position sizes and direction based on day to day trends\n" +
                        "4. Gain insight into market movement split between exchange price & FX\n" +
                        "5. Understand P&L movement for contract amendments (price, estimates, grade, etc.) and non-trader actions like overfill/underfill/washouts\n" +
                        "6. Recalculate P&L for realized buckets to explain the change from open to executed buckets\n" +
                        "7. View and analyze position and root-cause changes at any entity level including book, trader, origin, geography, type, etc\n" +
                        "8. Simple and interactive UI to drill down to individual transactions" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. View P&L explained buckets with drill down to trade level\n" +
                        "2. View P&L change by any pre-defined or user-defined entity like strategy, BU, region, trader\n" +
                        "3. Monitor to identify maximum p&l and position swing by strategy, book, commodity, grade and other dimensions\n" +
                        "4. Analyze historical data to spot outliers")

            }
            appName.equals("Trade Finance",true) -> {
                addNormalText("Get recommendations on best banks to engage with for Loans and LC instruments. Monitor trade finance risks based on credit rating, value, shipment dates and more.")
                addBgColorText("\nRole\n" +
                        "   - Analyst, CFO, Finance Manager, Trader, Risk Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture, Biofuels, Food and Beverage, Industrial Manufacturing, Metals, Oils, Gas, Power\n")
                addNormalText("\nThe Purchase Analysis App enables users to make the right decision on purchasing the right ores and on feeding the ores to the right smelters. It also allows users to:\\n 1.Quickly look at Qualities that return the highest RMM. \\n 2.Perform analysis across Qualities on various parameters such as Free Metal revenue, Treatment Charges, Penalty Charges, etc.\\n 3.Drilldown into the components that make up the RMM and analyze the contribution of each component to the RMM.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Bank Cost Quotation\n" +
                        "   - Trade Finance Scenario\n" +
                        "\n" +
                        "2. Frequency\n" +
                        "   - On Demand\n" +
                        "\n" +
                        "3. Complexity\n" +
                        "   - Medium\n" +
                        "\n" +
                        "4. Enrichments\n" +
                        "   - 8" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Recommendation of the best bank to engage for Trade Finance instruments\n" +
                        "2. Get best and least charging Issuing, Advising, Financing and Negotiating Bank\n" +
                        "3. Financial Instrument Cost calculations for Interest, Import/ Export, Document, Advising, Confirming, Deferred Payment and Negotiating Charges" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Trade Finance Recommendations – considering all Costs, Risks and Value\n" +
                        "2. Loan comparison across various tenors and Banks by cost\n" +
                        "3. Risk Tracking of LC against Bank Rating, Contract Shipment, etc.")


            }
            appName.equals("VaR",true) -> {


                addNormalText("Identify risk by comparing potential market scenarios derived from multiple simulation models. Sophisticated algorithms built-into the system lets you view volatility and correlations within portfolios to assess the impact of market risks.")
                addBgColorText("\nRole\n" +
                        "   - Trader, Finance, Risk Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture: Grains\n" +
                        "   - Softs: Coffee, Sugar\n" +
                        "   - Dairy\n" +
                        "   - Energy: Gas, Crude and Refined Oil, Biofuel\n" +
                        "   - Base and Precious\n")
                addNormalText("\nThe VaR App consolidates information from different sources, including C/ETRM spreadsheets and market data providers. It provides insights into market risk using VaR results, thus enabling users to make more informed decisions and take mitigation measures, if required.\n" +
                        "This app calculates VaR using Monte Carlo, Historical Simulation and Parametric methods. It supports the creation of flexible VaR risk portfolios and multiple market scenarios to predict potential impact of price shocks and 'what-if' trades.\n" +
                        "With this app, users can automate the VaR process to run at set times or intraday (on demand) as needed. It lets users customize VaR dashboards to be viewed as per user roles, and also configure VaR limits, with a round the clock monitoring system that sends alerts in the event of a breach.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Physical trades\n" +
                        "   - Derivative trades\n" +
                        "   - FX Trades\n" +
                        "   - Forward Prices from TR\n" +
                        "   - FX rates from TR\n" +
                        "- Interest rate from TR\n" +
                        "   - Historical data from TR\n" +
                        "\n" +
                        "2. Frequency\n" +
                        "   - End of day /Periodic /On demand\n" +
                        "\n" +
                        "3. Pre-Built Model\n" +
                        "   - FEA MakeVC and VaR method models\n" +
                        "\n" +
                        "4. Complexity\n" +
                        "   - Complex\n" +
                        "\n" +
                        "5. Enrichments\n" +
                        "   - 10" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Manage different trade types – VaR app allowes users to upload different trade types such as commodity physical trades, futures, options, forwards etc. and calculate VaR for each trade type\n" +
                        "2. Integrate data easily from multiple systems – Bring together data from disparate sources such as market prices from data providers like Thomson Reuters, Bloomberg, trade information from CTRM/ETRM systems or from csv/excel sheets maintained by users\n" +
                        "3. Calculate VaR for Portfolios at different levels – Users can configure different portfolios and sub-portfolios and can run VaR on them together or separately\n" +
                        "4. Multiple VaR Models – Use different models such as Parametric (also called Analytical or variance-covariance), Monte Carlo Simulation and Historical Simulation models. Users can run different models on the same portfolio and compare the results\n" +
                        "5. Get Volatility & Correlation information – Get information on volatility and corrleation between different market curves, interest rates and exchange rates\n" +
                        "6. Run VaR on Stressed Prices – Shock prices on selected curves and run VaR. User can compare VaR values run on normal and shocked prices to see impact of what-if changes in market\n" +
                        "7. Setup Risk Limits on VaR - User can setup risk limits on VaR values so that they will know when VaR has breached governance/compliance limits\n" +
                        "8. Visualization – Users have powerful visualization options available which they can use to view VaR values. Users can configure to view VaR histogram, historical trendlines as well as pure tabular view of VaR values using diffferent methods" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Single portfolio with multiple scenarios - View and compare VaR Scenarios at 90, 95, 99% CI across a 1 day holding period results for the Monte Carlo, Historical Simulation and Parametric methods for the global risk portfolio\n" +
                        "2. View and analyze single or multiple scenarios across multiple portfolios\n" +
                        "3. View underlying parameters, trades , prices that is contributing to the VaR run\n" +
                        "4. Compare historical VaR run overtime and view trends\n" +
                        "5. Monitor the hard and soft risk limits set on the VaR results to check on hard and soft risk breaches and see utilization.\n" +
                        "6. Create What If and price shocks and run VaR against the risk portfolios to view the impacts and make comparisons.")


            }
            appName.equals("Risk and Monitoring",true) -> {

                addNormalText("Define risk limit policies, analyze global risk across multiple portfolios and books, and track limit breaches and utilizations.")
                addBgColorText("\nRole\n" +
                        "   - Risk Team and Traders\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture: Grains\n" +
                        "   - Softs: Coffee, Sugar\n" +
                        "   - Dairy\n" +
                        "   - Energy: Gas, Crude and Refined Oil, Biofuel\n")
                addNormalText("\nThe Risk and Monitoring App lets you define risk limit policies against position, P&L, and VaR, with alert notifications for user groups in the event of a breach. It lets you analyze details into the breach to assess individual impact of trades on the P&L.\nAdditional views from the trading results data can be set up monitor to monitor the breach details in more depth such as tracking the worst performing trades by P&L.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Physical and financial trades from CTRM or spreadsheets\n" +
                        "   - Futures, cash and basis prices from TR\n" +
                        "   - Commodity forward prices from TR\n" +
                        "   - Historic commodity, FX price and interest rate data from TR\n" +
                        "   - Position\n" +
                        "   - P&L\n" +
                        "   - VaR\n" +
                        "   - P&L explained\n" +
                        " \n" +
                        "2. Frequency\n" +
                        "   - End of day and/or near real time\n" +
                        "\n" +
                        "3. Pre-Built Model\n" +
                        "   - Risk limit monitors against position, p&L and VaR\n" +
                        "\n" +
                        "4. Complexity\n" +
                        "   - Medium\n" +
                        "\n" +
                        "5. Enrichments\n" +
                        "   - 5")


                addHeaderText("Intelligence Engine :")
                addNormalText(" 1. Consolidates position and P&L from CTRM and multiple spreadsheets to set limits against.\n" +
                        " 2. Conversion of units and currencies to base unit for limit reporting.\n" +
                        " 3. Calculates limit results based on the limit policy configurations on positions, P&L.\n" +
                        " 4. Calculates VaR results using different VAR methods and scenarios.\n" +
                        " 5. Flexibility to support regional as well as a global view\n" +
                        " 6. Alerts and monitors to signal breaches on position, prices, p&l, VaR and any external data\n" +
                        " 7. Track limit policies over time." )
                addHeaderText("\n\nKey Insights\n")
                addNormalText(" 1. View summary of breaches by positions, P&L and VaR limits on a daily basis.\n" +
                        " 2. Track the limit utilization across the different policy dimensions.\n" +
                        " 3. Track worst trades by p&l performance.\n" +
                        " 4. Set limits policies to alert traders and risk team in the event of a breach.")


            }
            appName.equals("Purchase Analysis",true) -> {

                addNormalText("Compute Raw Material Margin per Smelter with a possiblity of simulation to achieve highest margins. Take the right decision on purchasing the right ores, and feed the ores to the right smelters.")
                addBgColorText("\nRole\n" +
                        "   - Purchasing Officer, Smelter Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "- Metals\n")
                addNormalText("\nThe Purchase Analysis App enables users to make the right decision on purchasing the right ores and on feeding the ores to the right smelters. It also allows users to:\\n 1.Quickly look at Qualities that return the highest RMM. \\n 2.Perform analysis across Qualities on various parameters such as Free Metal revenue, Treatment Charges, Penalty Charges, etc.\\n 3.Drilldown into the components that make up the RMM and analyze the contribution of each component to the RMM.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Contract\n" +
                        "   - Quality (Assay information)\n" +
                        "   - Processor data (Smelter & Feeding Point properties)\n" +
                        "   - Other Costs (Landfill Cost, etc)\n" +
                        "   - Market Prices for traded elements across different purchasing options/time horizons like spot, budget and long-term\n" +
                        "   - FX rates" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Calculate Raw Material Margin (RMM) in real-time for a combination of Quality, Feeding Point and Supplier. The algorithm provides result of RMM for selected qualities on latest contracts from different suppliers if the ore is processed in different feeding points of smelters.\n" +
                        "\n" +
                        "2. Ability to create portfolios for a combination of the following dimensions and use them in insight creation. Dimension - Quality,Supplier and Feeding Point" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Ability to visualize RMM by quality (sorted by highest RMM) across different levels like Supplier, Feeding Point, RRM Movement through its components.\n" +
                        "\n" +
                        "2. Ability to view and drill down to different components of Raw Material Margin like: Free Metal Value, Treatment Charges, Refining Charges, Penalty, Landfill Cost, Payment Terms in Base Currency for a combination of Quality, Feeding Point and Supplier")

                addHeaderText("\n\nMonitor\n")
                addNormalText("1. Shows the monitors applicable for the Insight and the Monitor Summary page; the summary page shows the monitor, the view by parameters box, and the trade details for the monitor.\n" +
                        "\n" +
                        "2. Ability to filter the RMM details grid by clicking on the view by grid.")

                addHeaderText("\n\nSimulation\n")
                addNormalText("1. Ability to create a scenario by shocking Quality assay values by metals, Contract info (Payable and Deductions) by suppliers, Charges (TC/RC/Penalty) by suppliers, Market prices of metal prices across different time horizons, or FX rates across different time horizons to create a simulation.\n" +
                        "\n" +
                        "2. Ability to add a new quality through collections into an existing data-set, include the same in a scenario and run simulation.")


            }
            appName.equals("Procurement Analysis",true) -> {

                addNormalText("Track commodity risk, coverage, spend and variance across all categories and make better buying decisions while managing price and volume risk.")
                addBgColorText("\nRole\n" +
                        "   - Buyer, Category Manager, Procurement Finance, CFO, Risk Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "- F&B, Industrial Manufacturing, PetChem\n")
                addNormalText("\nThe Procurement Analysis App allows manufacturing companies track, monitor, analyze and manage enterprise-wide budgeted, actual and projected spends. It lets users simulate projected spend and perform detailed spend attribution analysis.\\n\\n With this app, manufacturing companies can also view coverage and price risk to perform market simulations and see resulting impact on coverage. The app sends user alerts in the event of coverage breaching corporate governance policies.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Annual budgets and Volume forecasts\n" +
                        "   - Physical Coverage by cost component\n" +
                        "   - Derivative trades\n" +
                        "   - Forward Prices from TR\n" +
                        "   - FX rates from TR\n" +
                        "   - Cost Models\n" +
                        "   - Actuals from invoices\n" +
                        "\n" +
                        "2. Frequency\n" +
                        "   - On Demand\n" +
                        "\n" +
                        "3. Pre-Built Model\n" +
                        "   - Procurement\n" +
                        "\n" +
                        "4. Complexity\n" +
                        "   - High\n" +
                        "\n" +
                        "5. Enrichments\n" +
                        "   - 40" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Integrate data from multiple systems – Bring together large volumes of disparate information from systems such as ERP and spreadsheets to run analyses and gain answers immediately, instead of waiting a week or longer\n" +
                        "2. Simulate market changes, coverage, and spend – Run complex forecasting models and scenarios, ask \"what if\" questions to determine expected results. Evaluate how projected changes in market prices, supplier costs, and demand can affect the bottom line\n" +
                        "3. Track exposures — View forward-looking coverage by component and against changing market prices. Gain consolidated views of coverage by component or supplier. Learn individual cost contributors by component. Enable automatic corrections of coverage to components. View coverage movement over a period of time\n" +
                        "4. View historical spend – Understand supplier behavior and pricing trends. Gain improved analysis of spend behavior\n" +
                        "5. Set up monitors – Get alerts when certain spend and coverage thresholds are reached. Determine which components are most likely to exceed budget\n" +
                        "6. Track Hedges – Analyze hedging strategies, track market buy signals and automatic and manual allocation of hedges to individual items to track coverage at most granular level\n" +
                        "7. Customize – Create additional insights with flexible and multiple hierarchy\n" +
                        "8. Simple and interactive UI to drill down to individual transactions" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Enable executive decision making with customized reporting that aids collaboration across the organization\n" +
                        "2. Make the most profitable decisions by performing analysis on forecasted, actual, and projected spend. Include gains and losses of hedge programs in spend analysis\n" +
                        "3. Predict the impact to coverage with market simulations that illustrate the effect on coverage before taking action\n" +
                        "4. Make course corrections by pinpointing contributing factors with attribution analysis.\n" +
                        "5. Maintain coverage within corporate governance policies\n" +
                        "6. Coverage analysis and reports to track over and under coverage along with coverage movement\n" +
                        "7. Analyze hedge strategies and its effect on overall spend")


            }
            appName.equals("Inventory Analytics",true) -> {

                addNormalText("View and track projected inventory.")
                addBgColorText("\nRole\n" +
                        "   - Trader, Logistics Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture: Grains\n" +
                        "   - Softs: Coffee, Sugar\n" +
                        "   - Dairy\n" +
                        "   - Energy: Gas, Crude and Refined Oil, Biofuel,Coal\n" +
                        "   - Metals\n")
                addNormalText("\nThe Inventory Analytics App allows traders and logistic managers to view projected and forward-looking inventory on a daily basis taking in account long and short positions. It also helps analyze inventory COG VS market to make better buy and sell decisions.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Physical trades\n" +
                        "   - Nominations\n" +
                        "   - Actual delivery\n" +
                        "   - Market prices from TR\n" +
                        "   - Blend Details\n  " +
                        "2. Frequency\n" +
                        "   - On Demand\n" +
                        "3. Pre-Built Model\n" +
                        "   - Projected inventory\n" +
                        "4. Complexity\n" +
                        "   - High\n" +
                        "5. Enrichments\n" +
                        "   - 10" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Computes daily opening and closing balance for inventory by product, grade and location matching up sales and purchases\n" +
                        "2. Track planned vs actual delivery\n" +
                        "3. Predictive analysis allows users to see likley trend based on past pattern\n" +
                        "4. Computes cost of goods of inventory and tracks against market price\n" +
                        "5. Analyze historical supply demand patterns\n" +
                        "6. Alerts and monitors to signal breaches on inventory position, high volume trade, -ve inventory, market and inventory price breaches" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText(" 1. View opening, closing balance, net long, short by location\n" +
                        "2. Analyze inventory price against market to take strategic decision on whether to buy and store more or to sell\n" +
                        "3. View most profitable inventory locations and grades\n" +
                        "4. Track planned Vs actual delivery by date\n" +
                        "5. View estimated deliveries based on past projections\n")


            }
            appName.equals("Plan Performance",true) -> {

                addNormalText("Facilities such as grain sites, port terminals, and mines are under constant pressure to increase throughput and to add value to stored or mined products. These facilities then use processes such as blending, milling, separating, crushing, or treating of products for optimal fulfillment of orders. Bulk handling facilities must continually work to both increase throughput and achieve exacting cargo requirements. Receival, transfer, and loading functions must become more efficient.")
                addHeaderText("Using the Plan Performance App, the user can:\n")
                addNormalText("1. Automate process decisions\n" +
                        "2. Maximize throughput\n" +
                        "3. Make better stacking and reclaiming decisions\n" +
                        "4. Improve operational awareness and control with real-time product tracking and smart sequence control")

                addHeaderText("Key measures for evaluating performance of a plan for each resource are:\n")
                addNormalText("1. Variance between actual tonnes from the Insight CM based on the operator's tasks, the logistics plan and the production coordinator's plan in CAC.\n" +
                        "2. Variance of cumulative tonnes and cumulative deviation by a resource for a period of time, say last seven days.\n" +
                        "3. Variance from the target rates compared with net loading rates and gross loading rates by resource and product.\n" +
                        "4. Visibility of the Number of inbound trains and total outbound tonnage from the site, plotted against days of the week.\n" +
                        "5. Visibility of the number of trains at planned and unplanned dumping stations, i.e. , the number of trains that were dumped or not dumped as planned, i.e., they were dumped at the station assigned to them in the production coordinator's plan.\n" +
                        "6. Visibility of the number of ships at planned berths/unplanned berths, i.e., trains that were docked at the berth assigned to them in the production coordinator's plan.\n" +
                        "7. Measures of tonnes that were reclaimed from planned/unplanned stockpiles. i.e., the no of tonnes reclaimed from stockpiles by resource that planned/not planned to be used as per the production coordinator's plan for a period of time.\n" +
                        "8. Visibility on the number of trains in the dumping stations that are in/out of sequence based on the planned time in the production coordinator's plan.\n" +
                        "9. Visibility of number of trains that arrive early, on-time or late based on their designated time in the production coordinator's plan.\n" +
                        "10. Visibility on roll up by year, month, day, and shift of time spent and tonnes moved, adhering to deviating from the plan for a period of time.")


            }
            appName.equals("Pre-Trade Analysis",true) -> {

                addNormalText("Arrive at the best Price and Margin for a Trade.")
                addBgColorText("\nRole\n" +
                        "   - CRO, CFO, Trader, Risk Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture, Biofuels, Food and Beverage, Oil and Gas, Power, Industrial Manufacturing\n")
                addNormalText("\nIn order to generate the best possible Price and Margin, there is a need to be able to determine the possible costs and the best logistic route. The Costs involved are of various categories like Freight Costs based on the movement, Product Cost, Finance Costs, etc. In addition, there are different Units of Measurement used in each cost, coupled with different Currencies and/or weight Conversion factors.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1.  Data Sources\n" +
                        "Pre-Trade Scenario\n" +
                        "Logistics Path          - Inland Freight\n" +
                        "   - Ocean Freight\n" +
                        "   - List of Transloading Facilities\n" +
                        "   - Stowage Cost\n" +
                        "   - Transloading Cost\n" +
                        "   - Packing Details\n" +
                        "   - Shipment Capacity Details\n" +
                        "Product Cost Details    - Product Cost\n" +
                        "   - Packing Costs\n" +
                        "   - Processing Costs\n" +
                        "Inspection and Analysis - General Costs\n" +
                        "Customs\n" +
                        "Financial Costs         - Payment Rates\n" +
                        "   - Finance Rate\n" +
                        "   - Country Rate\n" +
                        "FX Rate\n" +
                        "2.Frequency             - Online\n" +
                        "3.Complexity            - High" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. To generate all possible options of fulfilling a Sale Offer with corresponding Costs and Sale Price. Based on, Cost of Products, Logistic Routes, Processing, Customs, Finance and General Costs\n" +
                        "2. To generate the best route based on User Defined INCO Term and Route mapping, for example Sale Offer for a FCA through “Pickup and Delivery” and “Transload”\n" +
                        "3. To calculate the Revised Offer Volume – this functionality helps utilize the complete capacity of an Equipment based on its Stowage, thereby reducing per MT cost of goods.\n" +
                        "4. To generate the most cost effective permutation and combinations of the 2 Leg logistic route through Transloader/ Container Yard with details of mode of transport and equipment to use.\n" +
                        "5. Ability to append/attach ANY User Defined Costs such as the Premium, Bagging, Bag, Palletization, Processing, etc." )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("            1. Top Margin and Price\n" +
                        "            2. Best Prices and Costs\n" +
                        "            3. Most Traded Scenarios")


            }
            appName.equals("Farmer Connect",true) -> {

                addNormalText("Increase Farmer loyalty and stickiness by enabling Farmer to make better contracts with price transparency and alerts coming from multiple sources and channels. Enable real time updates on tickets, deliveries and validate accounting entries.")
                addBgColorText("\nRole\n" +
                        "   - Trader, Grower Services\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture, Biofuels, Food and Beverage\n")
                addNormalText("\nFarmer Connect enables BOTH the business and its farmers to be on top of every business activity by giving most refined and relevant business insights and with an always on alerts/notifications feature set.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1.  Data Sources\n" +
                        "   - Bid/Price Sheet\n" +
                        "   - Contract and Trading system\n" +
                        "   - Ticket and Delivery application\n" +
                        "   - Accounting system\n" +
                        "   - Advices/ Market Data\n" +
                        "2.Frequency\n" +
                        "   - Online\n" +
                        "3.Complexity\n" +
                        "   - Low" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Retrieves, filters and based on the Farmer preference, generates Insights and Alerts for Offers, Existing Contracts and their Status, Tickets/Transportation Details, and Sales and Invoice/Payments data with the Company." )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Bid Information\n" +
                        "2. Contracts Insight\n" +
                        "3. Tickets and Deliveries\n" +
                        "4. Sales and Invoices\n" +
                        "5. Payments\n" +
                        "6. Agronomy Advice")


            }
            appName.equals("Disease Prediction",true) -> {

                addNormalText("Historical Rust Incidence and Rust Severity with weather parameters\\nThe model uses advance analytics agriculture feature engineering, which is modelled to factor in weather that impacts the plant, such as sunshine period, relative humidity, cloud cover, and other weather parameters. This with historical analysis such as seasonality, variety, location and other factors are modelled to bring out the Disease Prediction")
                addHeaderText("Dataset\n")
                addNormalText("1.  Weather Data Daily\n" +
                        "2.  Weather Data Hourly\n" +
                        "3.  Historical Disease Incidence Data")

            }
            appName.equals("Yield Forecast",true) -> {

                addNormalText("Predict crop yields by geography based on the historical yield results and forecasted weather conditions.")
                addBgColorText("\nRole\n" +
                        "   - Grower, Procurement Manager, Grower Service Provider\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture, Food & Beverage\n")
                addNormalText("\nThe Yield Forecast App takes into account historical crop data and weather forecast, to analyze actual vs. forecasted yield. The app also recommends best crop mix and sowing dates tailored to achieve next season's revenue and profitability goals.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Farm inputs and outputs\n" +
                        "   - Historical weather\n" +
                        "   - Forecasted weather data\n" +
                        "2. Frequency\n" +
                        "   - As required.\n" +
                        "3. Pre-Built Model\n" +
                        "   - None." )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Combines farm inputs and outputs data with weather data\n" +
                        "2. Applies machine learning on history of farm inputs, weather and outputs\n" +
                        "3. Predicts the farm yield for the upcoming season, given the weather forecasts and the farm inputs planned for the upcoming season\n" +
                        "4. Record and track changes" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Predict average yield per hectare for a given location\n" +
                        "2. Understand the relative importance of farm and weather variables for achieving the desired yield\n" +
                        "3. View combinations of farm inputs to achieve best yield results")


            }
            appName.equals("Cash Flow",true) -> {
                addNormalText("Cash Flow app for more accurate projected and predicted finance by estimating payment due date from business partners and analyzing history of their payment behavior.")
                addBgColorText("\nRole\n" +
                        "   - Analyst, Category Manager, CFO, Finance Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture: Grains\n" +
                        "   - Softs: Coffee, Sugar\n" +
                        "   - Dairy\n" +
                        "   - Energy: Gas, Crude and Refined Oil, Biofuel\n" +
                        "   - Metals\n")
                addNormalText("\nThe Cash Flow app allows you to project cash flow by estimating payment dates based on payment terms and shipment status. It also lets you predict payment dates from business partners by analyzing their past payment records using machine learning algorithms. The Cash flow app allows you to project cash flow by estimating payment dates based on payment terms and shipment status. It also lets you predict payment dates from business partners by analyzing their past payment records using machine learning algorithms.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("Projected cash flow analysis\n" +
                        "1. Data Sources\n" +
                        "   - Physical trades\n" +
                        "2. Frequency\n" +
                        "   - End of day\n" +
                        "3. Complexity\n" +
                        "   - Simple\n" +
                        "4. Enrichments\n" +
                        "   - 10\n" +
                        "Predicted cash flow analysis\n" +
                        "1. Data Sources\n" +
                        "   - Physical trades\n" +
                        "2. Frequency\n" +
                        "   - End of day\n" +
                        "3. Complexity\n" +
                        "   - Complex" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Projected cash flow app estimates payment date based on payment term as mentioned in the contract and adding the credit days as per the payment term to the shipment date\n" +
                        "2. Analysis of payment history and pattern of payment behavior helps to get predicted payment date\n" +
                        "3. Net contract value on the predicted payment date is indicative of predicted cash flow" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText(" 1. Net cash flow on predicted payment date\n" +
                        "2. Net cash flow on estimated payment date\n" +
                        "3. Top 5 net receipts and payments by counterparties\n" +
                        "4. Cash flow ladder")


            }
            appName.equals("Crop Intelligence",true) -> {

                addNormalText("Assess farm health using ideal growing conditions and output from other farms as benchmarks. Compare blocks within a farm by analyzing their differences.")
                addBgColorText("\nRole\n" +
                        "   - Grower Service Provider\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture\n")
                addNormalText("\nThe Crop Intelligence App provides a detailed farm health scorecard by comparing soil, weather, geographic and disease status of each paddock with ideal conditions. This app helps optimize farm interventions through early detection of concern areas for preventive and proactive action.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText(" 1. Data Sources\n" +
                        "   - Geolocation of paddocks\n" +
                        "   - Weather data\n" +
                        "   - Soil data\n" +
                        "   - Disease occurrence data\n" +
                        "2. Frequency\n" +
                        "   - As required.\n" +
                        "3. Pre-Built Model\n" +
                        "   - Crop Intelligence Model" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Combines information of soil, weather and disease occurrence with geolocation\n" +
                        "2. Scores a block on soil conditions, weather conditions, disease and geography, based on defined ideal parameters range. Calculates composite score by combining these component scores\n" +
                        "3. Tags a block as \"needs immediate attention\", \"needs attention\" or \"fair\" based on pre-determined score range\n" +
                        "4. Flexibility to modify the criteria for scoring, based on a given crop and its recommended growing conditions in a given region\n" +
                        "5. Alerts and monitors to signal breaches on component or composite scores of a block\n" +
                        "6. Track changes and movement over a period of time" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. View how the overall conditions of one farm compare against another\n" +
                        "2. Analyze how farm conditions have improved/ worsened on a month on month basis\n" +
                        "3. Identify farms and blocks that require attention.\n" +
                        "4. Deep dive into one farm. Compare conditions of one block against another.\n" +
                        "5. View how each of the underlying factors have changed on a month on month basis. For eg: Zinc content in soil, rainfall, temperature, disease outbreaks etc.")


            }
            appName.equals("Disease Risk Assessment",true) -> {

                addNormalText("Analyze risks posed by unfavorable weather and disease outbreaks on a crop, across locations.")
                addBgColorText("\nRole\n" +
                        "   - Trader, Procurement Manager, Grower Service Provider\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture, Food & Beverage\n")
                addNormalText("\nThe disease risk assessment app lets you hedge procurement risks and prioritize pest management advisory by assigning risk classification for areas and associated trade contracts, based on historic disease occurrences and weather conditions.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Physical trades\n" +
                        "   - Disease occurrence history\n" +
                        "   - Weather\n" +
                        "2. Frequency\n" +
                        "   - As required.\n" +
                        "3. Pre-Built Model\n" +
                        "   - Compound Risk Model" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Combines disease history and weather data\n" +
                        "2. Assigns disease risk scores to locations based on counts and intensities of reported diseases\n" +
                        "3. Assigns weather risk scores to locations by comparing actual weather conditions against ideal growing conditions recommended for the crop\n" +
                        "4. Calculates compound risk by combining disease and weather risk scores\n" +
                        "5. Flexibility to modify weather risk calculations depending upon the crop, variety and recommended growing conditions\n" +
                        "6. Flexibility to define disease intensities depending upon the potential damage associated with a given disease\n" +
                        "7. Flexibility to modify the weightages for disease and weather risk scores in the compound risk score\n" +
                        "8. Alerts and monitors to signal breaches on open trade contracts by risk category, location and delivery month\n" +
                        "9. Track changes and movement over a period of time" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText(" 1. View location wise compound, disease and weather risk\n" +
                        "2. Deep dive into the counts and intensities of plant diseases reported across locations\n" +
                        "3. Analyze seasonality of compound, disease and weather risk\n" +
                        "4. View overall volume and value of trade contracts, by risk categories\n" +
                        "5. Deep dive into trade contracts by risk category, location and delivery month")

            }
            appName.equals("Basis Analysis",true) -> {

                addNormalText("Basis Analysis for more profitable trading by comparing all possible opportunities in the market and portfolio.")
                addBgColorText("\nRole\n" +
                        "   - Trader\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture: Grains\n" +
                        "   - Softs: Coffee, Sugar\n" +
                        "   - Dairy\n" +
                        "   - Energy: Gas, Crude and Refined Oil, Biofuel\n" +
                        "   - Metals\n")
                addNormalText("\nView past and forward movements of basis by commodity, location and location groups. This app allows users to minimize risks and leverage opportunities by limiting exposure with timely visibility and prompt alerts on major changes in basis. The app simulates 'what-if' positions, giving traders visibility into potential impact on P&L.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Physical trades\n" +
                        "   - Futures, Cash and Basis Prices from TR\n" +
                        "   - Historical prices from TR\n" +
                        "   - Sentiment data from TR\n" +
                        "2. Frequency\n" +
                        "   - End of day and/or near real time\n" +
                        "3. Pre-Built Model\n" +
                        "   - Position and Mark to Market\n" +
                        "4. Complexity\n" +
                        "   - Simple\n" +
                        "5. Enrichments\n" +
                        "   - 10" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Consolidates position from CTRM and multiple spreadsheets\n" +
                        "2. Conversion of units and currencies to base unit for reporting\n" +
                        "3. View historical and forward price trends\n" +
                        "4. Add additional user defined fields to customize reports and build user defined hierarchies\n" +
                        "5. Flexibility to support regional as well as a global view\n" +
                        "6. Alerts and monitors to signal breaches on position and prices\n" +
                        "7. Track changes and movement over a period of timeage is greater than 0.8 the stem/plant is classified as Threat Found." )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. View summary of basis exposure by month, change in basis exposure by grade\n" +
                        "2.Monitor net basis exposure by month\n" +
                        "3.View historical movement of cash prices Vs futures prices\n" +
                        "4.Set monitor to alert when basis is between a user defined range\n" +
                        "5.View average historical price on contracts and compare against the market to monitor effectiveness")

            }
            appName.equals("Freight Exposure",true) -> {

                addNormalText("View overall freight exposure and hedge accurately and effectively.")
                addBgColorText("\nRole\n" +
                        "   - Trader\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture: Grains\n" +
                        "   - Softs: Coffee, Sugar\n" +
                        "   - Dairy\n" +
                        "   - Energy: Gas, Crude and Refined Oil, Biofuel\n" +
                        "   - Metals\n")
                addNormalText("\nThe Freight Analysis App lets you automate freight exposure reports across global and regional business units. It allows users to view net exposure by combining FFA with freight exposure.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Physical trades\n" +
                        "   - FFA trades\n" +
                        "   - Freight Prices from excel upload\n" +
                        "2. Frequency\n" +
                        "   - End of day and/or near real time\n" +
                        "3. Pre-Built Model\n" +
                        "   - Freight Exposure\n" +
                        "4. Complexity\n" +
                        "   - Simple\n" +
                        "5. Enrichments\n" +
                        "   - 5" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Consolidates position from CTRM, broker system, and multiple spreadsheets\n" +
                        "2. Conversion of units and currencies to base unit for reporting\n" +
                        "3. Flexibility to define routes and lookup instruments associated with hedging\n" +
                        "4. View historical and forward price trends\n" +
                        "5. User defined rules for hedging strategy\n" +
                        "6. Add additional user defined fields to customize reports\n" +
                        "7. Flexibility to support regional as well as a global view\n" +
                        "8. Alerts and monitors to signal breaches on position and prices\n" +
                        "9. Track changes and movement over a period of time\n" +
                        "10. Monitor freight price movement" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Freight expsoure by route and instrument\n" +
                        "2. View net exposure by route and instrument\n" +
                        "3. Monitor to identify maximum change in exposure by route and instrument\n" +
                        "4. Monitor to view if market prices are within a user defined range\n" +
                        "5. View expsoure movement over a period of time to view trends")

            }
            appName.equals("Logistics Operations Analysis",true) -> {

                addNormalText("Track logistics operations in near real time for quick response to delays and deviations.")
                addBgColorText("\nRole\n" +
                        "   - Operations Manager, Supply Chain Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture, Biofuels, Food and Beverage, Industrial Manufacturing, Metals, Oils\n")
                addNormalText("\nThe Logistics Operations Analysis App lets you track goods movement and delays, deviations, and accrual adjustments for change of routes and paths. It also allows you to analyze KPI by logistics company, its route, origin, destination and more.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Planned Movements\n" +
                        "   - Ticket Information\n" +
                        "   - Freight Movement Cost Matrix\n" +
                        "2. Frequency\n" +
                        "   - On demand\n" +
                        "3. Complexity\n" +
                        "   - Medium\n" +
                        "4. Enrichments\n" +
                        "   - 8" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Delay and Deviation identification and notification\n" +
                        "2. Accrual adjustment calculations for movements that have difference between plan vs actual movements\n" +
                        "3. Dead Freight calculations\n" +
                        "4. Tracks and spots overall outliers in routes, freight providers, logistic counter parties based on frequency of recurring logistic issues" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Freight accrual adjustment\n" +
                        "2. Goods movements, delays and deviations\n" +
                        "3. Load time tracking\n" +
                        "4. Route, freight provider, logistic counter party KPIs")

            }
            appName.equals("Reconciliation",true) -> {

                addNormalText("Standardize, control and streamline all reconciliations. Match payments, adjustments, receipts, contracts, stocks and commissions to the last cent.")
                addBgColorText("\nRole\n" +
                        "   - Analyst, CFO, Finance Manager, Trader, Risk Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture, Biofuels, Food and Beverage, Industrial Manufacturing, Metals, Oils, Gas\n")
                addNormalText("\nThe Reconciliation App undertakes complex grouping, matching and mathematical calculations to reconcile trades, stocks, commissions and more, from disparate systems.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - CTRM/ETRM Trading Application\n" +
                        "   - Accounting Application\n" +
                        "   - Broker Files\n" +
                        "2. Frequency\n" +
                        "   - EOD and EOM\n" +
                        "3. Complexity\n" +
                        "   - Medium\n" +
                        "4. Enrichments\n" +
                        "   - 23" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Complex grouping and mapping of GL/ Accounts from all the various systems for easy comparison.\n" +
                        "2. Bring forward previous month details and append delta records of current period to perform the reconciliation process.\n" +
                        "3. Complex mathematical calculations run in the reconciliation process." )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Breaks and differences identification made easier\n" +
                        "2. Save time and cost thereby reducing EOD and EOM time pressure\n" +
                        "3. Replace all manual recon activities with one-click Recon app\n" +
                        "4. Identify and avoid fraudulent activities, manual or system integrations errors in journals")


            }
            appName.equals("Credit Risk",true) -> {

                addNormalText("Monitor counterparty credit exposure.")
                addBgColorText("\nRole\n" +
                        "   - Risk Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "- Ags\n")
                addNormalText("\nConsolidate position across counterparties and their associated credit exposures.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Credit Exposure from spreadsheet\n" +
                        "   - Counterparty ratings from rating agencies (spreadsheet)\n" +
                        "   - Position by Counterparty from CTRM\n" +
                        "   - Market Price\n" +
                        "2. Frequency\n" +
                        "   - On demand\n" +
                        "3. Complexity\n" +
                        "   - Simple\n" +
                        "4. Enrichments\n" +
                        "   - 5" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Consolidate position data from CTRM systems and spreadsheets across all business units, across all counterparties\n" +
                        "2. View position and mark to mark by counterparties and ratings\n" +
                        "3. Set up limits to monitor credit exposure by counterparty groups and as well as individual cp's\n" +
                        "4. Monitor risk breaches and risk limit utilization" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Credit Limit Management\n" +
                        "2. Credit Insights\n" +
                        "3. Credit Exposure\n" +
                        "4. Market Prices")

            }
            appName.equals("Thomson Reuters App",true) -> {

                addNormalText("Eka connectors to Thomson Reuters bring in end-of-day data, intraday and historical prices from Thomson Reuters, has extensive coverage of market data from leading global exchanges for ags, metals, power, natural gas, oil, FX, interest rates, freights. In addition to market prices Eka has company fundamentals, corporate actions and ratings data. It also covers reference data feeds for FATCA, MIFID regulatory reporting. These feeds provides ability to assess counterparty risk, corporate structures, country of risk and counterparty's regulatory compliance status.\\nMarket prices coupled with trades data and alternative data sets such as weather, crop production, company fundamentals, social media, news can be leveraged by commodity market participants for improved decision making and gain competitive advantage against their peers in the industry.")
                addNormalText("\nThomson Reuters App in Eka enable the following functionalities for commodities market:\n")
                addNormalText("1. Basis Analysis for various agricultural commodities\n" +
                        "2. Yield Predictions for Wheat\n" +
                        "3. Global Buy insights for Procurement\n" +
                        "4. Hedging Analysis\n" +
                        "5. VaR Stress Analysis\n" +
                        "6. P&L Analysis\n" +
                        "7. Monitoring Global Risk Limits")


            }
            appName.equals("Hyper Local Weather",true) -> {

                addNormalText("Based on weather forecast have the right amount of water available. Also know when to and when not to put the fertilizer/pesticides, so as to not drain them due to impending rainfall forecast.")
                addBgColorText("\nRole\n" +
                        "   - Trader, Grower Services\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture, Biofuels, Food and Beverage, Oil and Gas, Power, Industrial Manufacturing\n")
                addNormalText("\nHourly and Daily Forecast\n" +
                        "   - Rainfall, Temp, Wind Speed, Sunshine, Cloud Cover are some of the parameters covered")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Daily Weather Forecast\n" +
                        "   - Hourly Weather Forecast\n" +
                        "2. Frequency\n" +
                        "   - Online\n" +
                        "3. Complexity\n" +
                        "   - Low" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. For the User defined location, ascertains the geo-codes to fetch daily and hourly weather information from the closest weather station." )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Today’s Forecast\n" +
                        "2. Historical Weather")


            }
            appName.equals("Power Spread Analysis",true) -> {

                addNormalText("Analyse electricity spreads and achieve the maximum profitability on power stations by producing power for the customer demand and participate in the wholsale trading market.")
                addBgColorText("\nRole\n" +
                        "   - Trader\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Energy\n")
                addNormalText("Asset Classes\n" +
                        "   - Power, Gas, Coal")
                addNormalText("\nThe Power Spread Analysis provides an overveiw on Spreads(including Clean Spreads), Merit order of Power Plant and Wholesale Market Activity to help trader obtain the maximum margin on Power Plant while meeting the the forecasted demand and supply and possiblity to trade short or surplus capacity.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Power Plant Data: Power Output, Emissions Intensity, Efficiency %\n" +
                        "   - Market Prices from TR - Power, Gas, Coal, EUA, CER" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Calculate Spark Spread, Dark Spread including Clean Spreads.\n" +
                        "2. Calculate Margins for each Power Station.\n" +
                        "3. Consolidate all the data together easily to calculate the merit order of their power stations across the market forward curve to optimise the fuel to power spread analysis and profit from the wholesale market conditions.\n" +
                        "4. Provides analysis on forecasted Market Demand and Plant Output Supply.\n" +
                        "5. Monitoring – Alerts and Notifications can be set up for users to manage any breaches." )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Spreads at Baseload\n" +
                        "2. Clean Spreads at Baseload\n" +
                        "3. Merit Order of Plants\n" +
                        "4. Plant Combined Output on each Month\n" +
                        "5. Market Demand and Supply\n" +
                        "6. Price Trends – Power, Gas, Coal, EUA, CER")


            }
            appName.equals("Quality Arbitrage Analysis",true) -> {

                addNormalText("Quality arbitrage opportunity to allocate stocks to Sale Contract to achieve better Profits and Margins.")
                addBgColorText("\nRole\n" +
                        "   - CRO, CFO, Trader, Risk Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture, Biofuels, Food and Beverage, Oil and Gas, Power, Industrial Manufacturing\n")
                addNormalText("\nTrader’s miss opportunities due to lack of full visibility of quality in the supply chain, and possibility of doing a quality arbitrage. The app helps Trader increase profit and margin.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Silo Information\n" +
                        "   - Quality P/D Schedule\n" +
                        "   - Quality Reports\n" +
                        "   - Sales Contracts\n" +
                        "2. Frequency\n" +
                        "   - Online\n" +
                        "3. Complexity\n" +
                        "   - High" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Fetches the Unallocated Stocks in a Silo, and studies the Quality results to ascertain the Stocks that can qualify for another Grade. Such Stocks are picked up and additional premium is calculated to arrive at the Arbitrage Opportunity." )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Quality Arbitrage Opportunity")


            }
            appName.equals("Vessel Management",true) -> {

                addNormalText("Explore the optimum trade strategy based on shipping times, commodity prices and costs such as shipping costs, liquification costs, regasification costs, throughput costs, etc.\\nFind out the best voyage strategy based on factors such as shipping costs, charter costs, bunkering charges, boil-off rates, demurrage rates, shipping times etc. to determine which voyage strategy would deliver maximum profits.\\nGet complete view of your vessels, their status and location and possible delays to plan vessel strategy efficiently.")
                addBgColorText("\nRole\n" +
                        "   - Trader, Operations Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Energy: LNG (Liquified Natural Gas)\n")
                addNormalText("\nVessel Management app allows users to analyze and compare Revenues and P&L for different trading strategies (e.g. spot sale vs. shipping to different ports) and get vessel status for self-owned as well chartered vessels to plan voyages in advance and save idle time.\\nThe app also allows you to compare different voyage strategies (considering different rates such as fuel costs, port charges, bunkering charges etc.) to decide on the optimum voyage strategy.\\nFinally, it allows users to view vessel status, their last location, estimated voyage time (for in-voyage vessels) and possible delays. This enables users to plan their strategy on vessels for future voyages.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Physical trades\n" +
                        "   - Shipping Times\n" +
                        "   - Shipping Costs\n" +
                        "   - Additional Charges (Bunkering, liquification, regasification, etc.)\n" +
                        "   - Vessel Details (Status, source, destination, last known location, etc.)\n" +
                        "2. Frequency\n" +
                        "   - On demand\n" +
                        "3. Complexity\n" +
                        "   - Simple\n" +
                        "4. Enrichments\n" +
                        "   - 15" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Calculate Margin and P&L for different trade strategies – Bring together trade data, costs and and price information to suggest to user the best trading strategies to use.\n" +
                        "2. Dynamic Cost Calculation – Based on ship speeds, current position and distance, predict delays, and related demurrage costs, if any. Similarly, based on boil-off rates and voyage strategy, calculate bunkering charges, if applicable.\n" +
                        "3. Highlight the optimum voyage strategies based on profitability, which is calulcated by taking multiple factors and scenarios into consideration. Optional costs such as demurrage and bunkering are calulcated on case to case basis based on the scenario selected." )
                addHeaderText("\n\nKey Insights\n")
                addNormalText(" 1. Cost Information\n" +
                        "2. Voyage time information\n" +
                        "3. P&L for different trading strategies\n" +
                        "4. Vessel Information\n" +
                        "5. In-Voyage vessel summary (current location, last known port, ETA etc.)\n" +
                        "6. Spend and P&L for different voyage strategies (based on fuel selection)")

            }
            appName.equals("Invoice Aging",true) -> {


                addNormalText("Track payment behaviour of counterparties, monitor exposure and set alerts for counterparties credit breaches.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Open invoices from transaction systems" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Compute highest overdue amounts and counterparties with highest exposure\n" +
                        "2. Compute invoice aging ranges >30,30-60,60-90 or more\n" +
                        "3. Alerts when counterparty breaches overdue invoice limits" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Highest overdue amount\n" +
                        "2. Total Overdue\n" +
                        "3. Invoice overdue limit breaches by counterparties\n" +
                        "4. Top 10 counterparties by overdue amount and payment delays\n" +
                        "5. Invoice Aging Report with counterparties that have overdue amounts in >30, 30-60,60-90 and >90 aging ranges")


            }
            appName.equals("Plant Outage",true) -> {

                addNormalText("Track and analyze impact of plant outages on volume and value.")
                addBgColorText("\nRole\n" +
                        "   - Risk Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Refined Products\n")
                addNormalText("\nConsolidate position across business units and view impact due to external factors like planned and unplanned outages and take corrective action in near real time.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Historical Plant outages from TR\n" +
                        "   - Physical positions by refinery\n" +
                        "   - Hedges\n" +
                        "2. Frequency\n" +
                        "   - On Demand\n" +
                        "3. Pre-Built Model\n" +
                        "   - Refinery Outage\n" +
                        "4. Complexity\n" +
                        "   - Simple\n" +
                        "5. Enrichments\n" +
                        "   - 5" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Consolidate position data from multiple ETRM systems and spreadsheets across all business units, across all geos\n" +
                        "2. Cleanse, transform and apply business rules to get a single consolidated dashboard to view position\n" +
                        "3. Classify refineries as stable, moderately stable, unstable using historical outage data and custom defined rules\n" +
                        "4. Combine position data with refinery outage to see how much of my position falls into the above classification\n" +
                        "5. Alerts and limits to track any market activity and sudden spikes in demand and supply shortage\n" +
                        "6. Add what-if to see impact of shortfall on P&L" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Historical analysis of outages\n" +
                        "2. Consolidated position\n" +
                        "3. View positions at risk based on refinery status\n" +
                        "4. View market trends\n" +
                        "5. View hedge policy and deviations from the same")


            }
            appName.equals("Emissions Hedging",true) -> {

                addNormalText("Real time visibility on Carbon Portfolio and tactful insights enabling right hedging decision.")
                addBgColorText("\nRole\n" +
                        "   - Portfolio Manager, Analyst – Carbon Desk\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Energy\n")
                addNormalText("\nThe Emissions Hedging app provides a real time visibility on Co2 Positions and underlying exposure, taking Inventory(Stock) and Registry details into account. The Co2 exposure is calculated for current and forward years. Trader can buy extra allowances or hedge EUA or swap CER for EUA to make money and cover the Co2 exposure.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Power Trades indicating demand and supply from Market.\n" +
                        "   - Market Prices from TR – EUA, CER\n" +
                        "   - Co2 Trades indicating Stock, Registry, Hedges." )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Consolidate all the data together easily from different systems to calculate the exposure and hedges made to cover them.\n" +
                        "2. Provide aggregated Co2 positions to help forward planning.\n" +
                        "3. Alerts and Notification on Emissions Allowances Price changes, Positions breach." )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("            1. Carbon Portfolio Dashboard for Management level\n" +
                        "            2. Co2 Hedges and Stock Details\n" +
                        "            3. Aggregated Co2 Positions\n" +
                        "            4. Price Trends : EUA vs CER")


            }
            appName.equals("Customer Connect",true) -> {

                addNormalText("Reach out, connect and give near real time information access to your Customers.")
                addBgColorText("\nRole\n" +
                        "   - Trader\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture, Biofuels, Food and Beverage, Industrial Manufacturing, Metals, Oils, Gas\n")
                addNormalText("\nThe Customer Connect App enables Counterparties and Business Partners to easily, quickly and seamlessly view all the required information across Offers, Sales Contracts, Tickets and Deliveries and Invoices and Payments.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - CTRM/ETRM Trading Application\n" +
                        "   - Ticketing Application\n" +
                        "   - Accounting Application\n" +
                        "   - Invoicing and Banking Application\n" +
                        "   - External Market Data Provider\n" +
                        "2. Frequency\n" +
                        "   - Online\n" +
                        "3. Complexity\n" +
                        "   - Low" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Retrieves, filters and based on the Counter Party/ Business Partner generates Insights and Alerts for Offers, Existing Contracts and their Status, Tickets/Transportation Details, and Sales and Invoice/Payments data with the Company." )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Offers\n" +
                        "2. Open Sale Contracts and Detailed Pricing and Quantity information\n" +
                        "3. Upcoming Deliveries\n" +
                        "4. Invoices and Payments status\n" +
                        "5. Market Prices")


            }
            appName.equals("Supply Demand",true) -> {

                addNormalText("Demand and Vendor analysis for manufacturing, enabling better planning and strategy.")
                addBgColorText("\nRole\n" +
                        "   - CFO, Risk Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Food and Beverage, Industrial Manufacturing\n")
                addNormalText("\nVendor, Inventory and Demand scoring components definition and weightage")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Data Sources\n" +
                        "   - Inventory\n" +
                        "   - Demand and Supply Information\n" +
                        "   - Vendor and Credit Information\n" +
                        "2. Frequency\n" +
                        "   - Online\n" +
                        "3. Complexity\n" +
                        "   - High" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Every entity is treated independently and a polynomial is fit to generate as many model as number of entities, to generate demand forecasting.\n" +
                        "2. In addition, Dead Stock Analysis, Scoring and EoQ computation is undertaken to arrive and score demand numbers." )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Vendor Analysis\n" +
                        "2. Inventory Analysis\n" +
                        "3. Strategy Analysis")


            }
            appName.equals("Price Trend Analysis",true) -> {

                addNormalText("Click and ascertain if Plant/Crop is infected with disease.")
                addBgColorText("\nRole\n" +
                        "   - Role Farmer/Grower,Liaison Officer,Field Agents\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture, Food and Beverage\n")
                addNormalText("\nUser will be able to click a photo or upload from gallery, the image of the plant/crop. Intelligence engine runs disease identification model to ascertain if Threat is found or not.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1.Frequency\n" +
                        "   - Online\n" +
                        "2.Complexity\n" +
                        "   - High" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. The disease identification model has been trained over images of healthy and infected stems/plant.\n" +
                        "2. The model uses convolutional neural network, one of deep neural network architectures to learn the patterns in the healthy and infected images. Based on the learning it classifies new observations as infected or healthy.\n" +
                        "3. The model gives probabilities of the stem being infected or healthy based on the image of the stem/plant and classifies it in one of the two classes based on a threshold of 0.8. For example - Only when the probability of stem/plant being infected based on its image is greater than 0.8 the stem/plant is classified as Threat Found." )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Images uploaded will get appended with disease identification results.")


            }
            appName.equals("Options Valuation",true) -> {

                addNormalText("Options are non-linear instruments, whose value does not directly correlate with the value of underlying asset (commodity futures, equities, etc.) Hence it becomes imperative to understand the true value of an option and how it relates with underlying asset prices and other market parameters. The purpose of option valuation app is to allow users to find the true value of their option trades, along with key option Greek variables.")
                addBgColorText("\nRole\n" +
                        "   - Trader, Finance, Risk Manager\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture: Grains\n" +
                        "   - Softs: Coffee, Sugar, Dairy\n" +
                        "   - Energy: Gas, Crude and Refined Oil, Biofuel\n" +
                        "   - Metals\n")
                addNormalText("\nThe option valuation App consolidates options information from different sources, including C/ETRM systems, brokers and market data providers (for market prices).\n" +
                        "This app performs Option valuation using Black Scholes method. It supports the creation of flexible options portfolios to perform valuation of multiple trades.\n" +
                        "With this app, users can automate the run valuation process at set times or intraday (on demand) as needed. It lets users customize Option Valuation dashboards to be viewed as per user roles, and configure Option Valuation limits, with a round the clock monitoring system that sends alerts in the event of a breach.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1.  Data Sources\n" +
                        "   - Derivative Trades\n" +
                        "   - Market Prices\n" +
                        "   - Interest Rates\n" +
                        "   - Instrument Attributes\n" +
                        "2. Frequency\n" +
                        "   - On Demand\n" +
                        "3. Pre-Built Model\n" +
                        "   - Option Valuation\n" +
                        "4. Complexity\n" +
                        "   - High" )


                addHeaderText("Intelligence Engine :")
                addNormalText("1. Consolidates option data from C/ETRM, broker system, market data providers and spreadsheets\n" +
                        "2. Create option portfolios to run valuation\n" +
                        "3. Calculation of price volatility to be used in option valuation\n" +
                        "4. Use Black Scholes model to run valuation\n" +
                        "5. Calculate option value and Greeks – Delta, Gamma, Rho, Theta & Vega\n" +
                        "6. Add additional user defined fields to customize reports\n" +
                        "7. Alerts and monitors to signal breaches on position and prices\n" +
                        "8. Track changes and movement over a period" )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Multiple portfolios with Option Valuation - View and compare option valuation of multiple portfolios with different trade set underlying.\n" +
                        "2. Compare historical Option valuation run overtime and view trends\n" +
                        "3. Monitor the hard and soft risk limits set on the Option Valuation results to check on hard and soft risk breaches and see utilization.")


            }
            appName.equals("Disease Identification",true) -> {

                addNormalText("Click and ascertain if Plant/Crop is infected with disease.")
                addBgColorText("\nRole\n" +
                        "   - Role Farmer/Grower,Liaison Officer,Field Agents\n")
                addNormalText("")
                addBgColorText("\nSegment\n" +
                        "   - Agriculture, Food and Beverage\n")
                addNormalText("\nUser will be able to click a photo or upload from gallery, the image of the plant/crop. Intelligence engine runs disease identification model to ascertain if Threat is found or not.")

                addHeaderText("\n\nTechnical Specifications\n")

                addNormalText("1. Frequency\n" +
                        "   - Online\n" +
                        "2. Complexity\n" +
                        "   - High" )


                addHeaderText("Intelligence Engine :")
                addNormalText("The disease identification model has been trained over images of healthy and infected stems/plant.\n" +
                        "2. The model uses convolutional neural network, one of deep neural network architectures to learn the patterns in the healthy and infected images. Based on the learning it classifies new observations as infected or healthy.\n" +
                        "3. The model gives probabilities of the stem being infected or healthy based on the image of the stem/plant and classifies it in one of the two classes based on a threshold of 0.8. For example - Only when the probability of stem/plant being infected based on its image is greater than 0.8 the stem/plant is classified as Threat Found." )
                addHeaderText("\n\nKey Insights\n")
                addNormalText("1. Images uploaded will get appended with disease identification results.")


            }
        }

    }


    fun addHeaderText(textValue : String){

        val textView =createTextView(context,textValue, WorkFlowViews.MATCH_PARENT, WorkFlowViews.WRAP_CONTENT,
                0f, WorkFlowMargin(10, 10, 0, 0), Gravity.LEFT,
                R.color.app_primary_clr,R.dimen.learn_more_hdr_sz)
        textView!!.setTypeface(textView!!.typeface,Typeface.BOLD)
        lineLay.addView(textView)

    }
    fun addNormalText(textValue: String){
        val textView =createTextView(context,textValue, WorkFlowViews.MATCH_PARENT, WorkFlowViews.WRAP_CONTENT,
                0f, WorkFlowMargin(10, 10, 0, 0), Gravity.LEFT,
                R.color.black,R.dimen.learn_more_para_sz)
        lineLay.addView(textView)

    }
    fun addBgColorText(textValue: String){

        val textView =createTextView(context,textValue, WorkFlowViews.MATCH_PARENT, WorkFlowViews.WRAP_CONTENT,
                0f, WorkFlowMargin(10, 10, 0, 0), Gravity.LEFT,
                R.color.black,R.dimen.learn_more_para_sz)
        textView!!.setBackgroundColor(ContextCompat.getColor(context,R.color.learn_more_txt_bg))
        lineLay.addView(textView)

    }



}