package com.example.firebasekotlin

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.viewbinding.ViewBinding
import com.example.firebasekotlin.base.MyApp
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar


abstract class BaseActivityWithoutVM<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var mViewBinding: VB
//    private var progressDialog: ProgressDialog? = null
//    private var errorDialog: ErrorDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewBinding = getViewBinding()
    }

    /**
     * It returns [VB] which is assigned to [mViewBinding] and used in [onCreate]
     */
    abstract fun getViewBinding(): VB


//    fun showProgressDialog() {
//        if (progressDialog == null) {
//            progressDialog = ProgressDialog()
//        }
//        progressDialog?.let {
//            if (!it.isVisible) {
//                it.show(supportFragmentManager, TAG_PROGRESS_DIALOG)
//            }
//        }
//    }



//    fun isNetworkConnected(): Boolean {
//        val flag = Utils.isInternetAvailable()
//        if (!flag) {
//            showSnackBar("Internet not connected!")
//        }
//        return flag
//    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun changeStatusBarColor(statusBarColor: Int, myActivityReference: Activity) {
        val window = myActivityReference.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = statusBarColor
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun showSnackBarError(msg: String?) {
        Snackbar.make(mViewBinding.root.rootView, msg!!, Snackbar.LENGTH_SHORT)
            .setAction("Ok") {
            }
            .setBackgroundTint(MyApp.context.resources.getColor(R.color.blue, null))
            .show()
    }


    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(msg: String?) {
        Snackbar.make(mViewBinding.root.rootView, msg!!, Snackbar.LENGTH_SHORT)
            .show()
    }

    fun navigateAndClearBackStack(cls: Class<*>?) {
        val intent = Intent(this, cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

//    fun hideProgressDialog() = progressDialog?.dismiss()
//
//    fun showErrorDialog(title: String, message: String) {
//        if (errorDialog == null) {
//            errorDialog = ErrorDialog()
//        }
//        errorDialog?.apply {
//            this.title = title
//            this.message = message
//        }
//        errorDialog?.let {
//            if (!it.isVisible) {
//                it.show(supportFragmentManager, TAG_ERROR_DIALOG)
//            }
//        }
//    }


    fun navigateToNextActivity(cls: Class<*>?) {
        val intent = Intent(this, cls)
        startActivity(intent)
    }

    fun getText(et: EditText): String {
        return et.text.toString().trim { it <= ' ' }
    }

    fun getText(et: AutoCompleteTextView): String {
        return et.text.toString().trim { it <= ' ' }
    }

    fun getText(tv: TextView): String {
        return tv.text.toString().trim { it <= ' ' }
    }

    fun getText(button: MaterialButton): String {
        return button.text.toString().trim { it <= ' ' }
    }

    fun setVisibility(visible: View, gone: View) {
        visible.visibility = View.VISIBLE
        gone.visibility = View.GONE
    }

    fun isEmpty(tv: TextView): Boolean {
        return TextUtils.isEmpty(getText(tv).trim { it <= ' ' })
    }

    fun isEmpty(et: EditText): Boolean {
        return TextUtils.isEmpty(getText(et))
    }

    fun isEmpty(et: AutoCompleteTextView): Boolean {
        return TextUtils.isEmpty(getText(et))
    }

    fun isEmailValid(email: String?): Boolean {
        return !Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
    }



    fun stripHtml(html: String): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        } else {
            HtmlCompat.fromHtml(html,FROM_HTML_MODE_LEGACY).toString()
        }
    }



//    override fun onDestroy() {
//        super.onDestroy()
//        progressDialog?.dismiss()
//        progressDialog = null
//
//        errorDialog?.dismiss()
//        errorDialog = null
//    }

    companion object {
        private const val TAG_PROGRESS_DIALOG = "progress_dialog"
        private const val TAG_ERROR_DIALOG = "error_dialog"
    }

}
