package com.example.firebasekotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.util.Base64
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.io.File

abstract class BaseFragmentWithoutVM<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

//    private var progressDialog: ProgressDialog? = null
//    private var errorDialog: ErrorDialog? = null


    abstract fun init()

    abstract fun setListener()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)

        return binding.root
    }

    fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(msg: String) {
        //findViewById(android.R.id.content)
        Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT)
            .show()
    }

    fun navigateAndClearBackStack(cls: Class<*>?) {
        val intent = Intent(requireContext(), cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        requireContext().startActivity(intent)
        (requireContext() as Activity).finish()
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

    fun isEmailValid(email: String?): Boolean {
        return !Patterns.EMAIL_ADDRESS.matcher(email!!).matches()
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

    fun splitString(value: String): List<String> {
        val str = value
        val list: List<String> = str.split(",").toList()
        return list
    }


//    fun showProgressDialog() {
//        if (progressDialog == null) {
//            progressDialog = ProgressDialog()
//        }
//
//        progressDialog?.let {
//            if (!it.isVisible) {
//                it.show(requireActivity().supportFragmentManager, TAG_PROGRESS_DIALOG)
//            }
//        }
//    }
//    showprogressDiaglogcu

    fun convertToBase64(attachment: File): String {
        return Base64.encodeToString(attachment.readBytes(), Base64.NO_WRAP)
    }

//    fun isNetworkConnected(): Boolean {
//        val flag = Utils.isInternetAvailable()
//        if (!flag) {
//            showToast("Internet not connected!")
//        }
//        return flag
//    }


    fun stripHtml(html: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        } else {
            Html.fromHtml(html).toString()
        }
    }

    fun stripHtml1(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
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
//                it.show(requireActivity().supportFragmentManager, TAG_ERROR_DIALOG)
//            }
//        }
//    }

    fun toast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun applicationContext(): Context = requireActivity().applicationContext


//     fun select_language(language: String?, activity: Activity) {
//        PreferencesHelper.setLanguage(activity, language)
//        LocaleHelper.setLocale(activity, language)
//        // after changing language we should refresh activity
//        val intent = Intent(MyApp.context, SplashActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
//        startActivity(intent)
//        requireActivity().fragmentManager.popBackStack()
//    }


//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//        progressDialog?.dismiss()
//        progressDialog = null
//
//        errorDialog?.dismiss()
//        errorDialog = null
//    }

    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    companion object {
        private const val TAG_PROGRESS_DIALOG = "progress_dialog"
        private const val TAG_ERROR_DIALOG = "error_dialog"
    }
}
