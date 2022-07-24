package com.eka.cacapp.data.domainverify

import com.google.gson.annotations.SerializedName

/**
 * Data class for domain verification response
 * */

data class DomainVerifyResModel(
        var business_partner_users: Boolean,
        var is_mfa_enabled: Boolean,
        var disbale_resend_otp_link_in_seconds : Int =0,

        @SerializedName("identityProviderSetting")
        var identifyProviderSetting: IdentifyProviderSetting

)

data class IdentifyProviderSetting(
        var show_eka_login: Boolean,
        var mobile_client_id : String = "",
        var identity_provider_type : String = "",
        var enabled_external_logout : Boolean = false,
        var enabled_sso_mobile : Boolean = false,
        var mobile_redirect_uri : String = "",
        var issuer : String = "",
        var mobile_logout_redirect_uri : String = "",
        var tenant_id : String = "",
        var tenant_name : String = ""

)