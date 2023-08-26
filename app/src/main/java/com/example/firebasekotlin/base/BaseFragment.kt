package com.example.firebasekotlin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<VB : ViewBinding, VM : ViewModel> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected lateinit var viewModel: VM


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding(inflater, container)
        viewModel = ViewModelProvider(this).get(getViewModel())

        return binding.root
    }
    abstract fun init()
    abstract fun setListener()
    abstract fun setValues()
    abstract fun getViewModel(): Class<VM>
    fun isEmpty(tv: TextView): Boolean {
        return TextUtils.isEmpty(getText(tv).trim { it <= ' ' })
    }

    fun isEmpty(et: EditText): Boolean {
        return TextUtils.isEmpty(getText(et))
    }

    fun isEmpty(et: AutoCompleteTextView): Boolean {
        return TextUtils.isEmpty(getText(et))
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

    fun isPhoneNumberValid(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }



    fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    fun showSnackBar(msg: String?) {
        //findViewById(android.R.id.content)
        Snackbar.make(requireView(), msg!!, Snackbar.LENGTH_SHORT)
            .show()
    }

    fun navigateAndClearBackStack(cls: Class<*>?) {
        val intent = Intent(requireContext(), cls)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        requireContext().startActivity(intent)
        (requireContext() as Activity).finish()
    }

    fun applicationContext(): Context = requireActivity().applicationContext

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    protected abstract fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    companion object {
        private const val TAG_PROGRESS_DIALOG = "progress_dialog"
        private const val TAG_ERROR_DIALOG = "error_dialog"
    }
}
