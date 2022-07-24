package com.eka.cacapp.utils


import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.eka.cacapp.R
import com.eka.cacapp.helper.NetworkHelper
import com.eka.cacapp.network.Resource
import com.eka.cacapp.ui.loginWithEka.LoginWithEkaFrag
import org.json.JSONObject
import java.lang.Exception

private val SOMETHING_WENT_WRONG_ERROR_MSG = "Something went wrong, Please try again later"

fun Fragment.handleApiError(failure: Resource.Failure,
                            retry: (() -> Unit)? = null) {
   if (failure.errorCode == 401 && this is LoginWithEkaFrag) {
       Toast.makeText(context, "Invalid Credentials!!", Toast.LENGTH_SHORT).show()
    }else {
       context?.let { apiErrorHandler(it,failure,retry) }
   }
}
fun Activity.handleApiError(failure: Resource.Failure,
                            retry: (() -> Unit)? = null) {
    apiErrorHandler(this,failure,retry)
}

fun apiErrorHandler(context: Context,failure: Resource.Failure,
                   retry: (() -> Unit)? = null){

    when {
        failure.isNetworkError -> {
            Toast.makeText(context, SOMETHING_WENT_WRONG_ERROR_MSG, Toast.LENGTH_SHORT).show()
        }

        failure.errorCode == 401 -> {
                Toast.makeText(context, "UNAUTHORIZED !!", Toast.LENGTH_SHORT).show()
        }
        failure.errorCode == 5001 -> {
            Toast.makeText(context, "Security Error (001). Contact System Administrator", Toast.LENGTH_SHORT).show()
        }
        /*failure.errorCode == 500 -> {

            Toast.makeText(context, SOMETHING_WENT_WRONG_ERROR_MSG, Toast.LENGTH_SHORT).show()
        }*/
        failure.errorCode == 503 -> {
            Toast.makeText(context, "Service Unavailable", Toast.LENGTH_SHORT).show()
        }
        failure.errorCode == 400 || failure.errorCode == 500-> {
            var errorMsg = SOMETHING_WENT_WRONG_ERROR_MSG
            try {
                val res = failure.errorBody?.string()?: ""
                val jsonObject = JSONObject(res)
                errorMsg = jsonObject.optString("errorMessage")
                if(errorMsg.isEmpty()){
                    errorMsg = jsonObject.optString("msg")
                }
                if(errorMsg.isEmpty()){
                    errorMsg = SOMETHING_WENT_WRONG_ERROR_MSG
                }
            } catch (e: Exception) {

            }
            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
        }
        else -> {
            var error = SOMETHING_WENT_WRONG_ERROR_MSG
            if (failure.errorBody?.toString() != null) {
                error = failure.errorBody?.toString()
            }
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }

    }

}

fun EditText.enableDatePkrForFltr(){
    this.isEnabled = true
    this.isCursorVisible = false
    this.showSoftInputOnFocus = false
    this.setClickable(true)
    this.setTextSize(TypedValue.COMPLEX_UNIT_SP,14f);
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.calendar_img, 0);
}

fun EditText.disableDatePkrForFltr(){
    this.setClickable(false)
    this.isEnabled = false
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    this.setTextSize(TypedValue.COMPLEX_UNIT_SP,18f);
}


fun EditText.resetFltrFieldToDefault(){
    this.setFocusable(true)
    this.isCursorVisible = true
    this.showSoftInputOnFocus = true
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    this.setOnClickListener {

    }
    this.setOnTouchListener(null)
    this.setTag(0)
    this.setTextSize(TypedValue.COMPLEX_UNIT_SP,18f);

}



fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Fragment.checkInternet(): Boolean = if (NetworkHelper.isInternetAvailable(requireActivity())) {
    true
} else {
    Toast.makeText(requireActivity(), getString(R.string.internet_error_msg), Toast.LENGTH_SHORT).show()
    false
}

fun Fragment.allowOnlyPortrait(){
    this.requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setRetainInstance(true);
}

fun Fragment.allowOnlyLandScape(){
    this.requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    setRetainInstance(true);
}

fun Fragment.allowAllOrientation(){
    this.requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    setRetainInstance(true);
}


fun TextView.leftDrawable(@DrawableRes id: Int = 0, @DimenRes sizeRes: Int) {
    val drawable = ContextCompat.getDrawable(context, id)
    val size = resources.getDimensionPixelSize(sizeRes)
    drawable?.setBounds(0, 0, size, size)
    this.setCompoundDrawables(drawable, null, null, null)
}