package com.eka.cacapp.data.insight

import java.io.Serializable

data class InsightDetailData(
        val supported: Boolean,
        val chartParam: ChartParams? = null
) : Serializable