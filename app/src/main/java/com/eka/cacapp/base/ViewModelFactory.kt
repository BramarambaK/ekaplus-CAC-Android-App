package com.eka.cacapp.base

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eka.cacapp.repositories.*
import com.eka.cacapp.ui.dashboard.DashboardViewModel
import com.eka.cacapp.ui.diseaseIdent.DiseaseIdenViewModel
import com.eka.cacapp.ui.domainVerfiy.DomainVerViewModel
import com.eka.cacapp.ui.farmerConnect.FarmerConnectViewModel
import com.eka.cacapp.ui.insight.InsightViewModel
import com.eka.cacapp.ui.loginWithEka.LoginViewModel
import com.eka.cacapp.ui.settings.SettingsViewModel

/**
 * ViewModel Factory
 * */
class ViewModelFactory(
    private val repository: BaseRepository,
    private val context: Context
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DomainVerViewModel::class.java)
            -> DomainVerViewModel(repository as DomainVerifyRepository,context) as T

            modelClass.isAssignableFrom(LoginViewModel::class.java)
            -> LoginViewModel(repository as LoginRepository,context) as T

            modelClass.isAssignableFrom(DashboardViewModel::class.java)
            -> DashboardViewModel(repository as DashboardRepository,context) as T

            modelClass.isAssignableFrom(SettingsViewModel::class.java)
            -> SettingsViewModel(repository as SettingsRepository,context) as T

            modelClass.isAssignableFrom(InsightViewModel::class.java)
            -> InsightViewModel(repository as InsightRepository,context) as T

            modelClass.isAssignableFrom(DiseaseIdenViewModel::class.java)
            -> DiseaseIdenViewModel(repository as DiseaseIdentificationRepository,context) as T

            modelClass.isAssignableFrom(FarmerConnectViewModel::class.java)
            -> FarmerConnectViewModel(repository as FarmerConnectRepository,context) as T


            else -> throw IllegalArgumentException("ViewModel not found in factory")

        }
    }
}