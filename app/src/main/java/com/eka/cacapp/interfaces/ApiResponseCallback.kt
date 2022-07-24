package com.eka.cacapp.interfaces

/**
 * Interface for ApiResponse callback
 * */

interface ApiResponseCallback {

    fun onSuccess()

    fun onFailure(th: Throwable)
}