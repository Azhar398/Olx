package com.app.olxapplication.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.app.olxapplication.BaseFragment
import com.app.olxapplication.R
import com.app.olxapplication.utilities.Constants
import com.app.olxapplication.utilities.SharedPref
import kotlinx.android.synthetic.main.fragment_include_details.*
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.edPhone
import kotlinx.android.synthetic.main.fragment_settings.edPostalAddress
import kotlin.concurrent.fixedRateTimer

class SettingsFragment : BaseFragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_settings,container,false)
        return rootView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        edName.setText(SharedPref(requireActivity()).getString(Constants.USER_NAME))
        edEmailAddress.setText(SharedPref(requireActivity()).getString(Constants.USER_EMAIL))
        edPhone.setText(SharedPref(requireActivity()).getString(Constants.USER_PHONE))
        edPostalAddress.setText(SharedPref(requireActivity()).getString(Constants.USER_ADDRESS))

        listener()
    }

    private fun listener() {
        textViewSave.setOnClickListener(View.OnClickListener {
            saveData()
        })
    }

    private fun saveData() {
        if (edName.text.toString().isEmpty())
            edName.setError(getString(R.string.enter_name))
        else if (edEmailAddress.text.toString().isEmpty())
            edEmailAddress.setError(getString(R.string.enter_email))
        else{
            SharedPref(requireActivity()).setString(Constants.USER_NAME,edName.text.toString())
            SharedPref(requireActivity()).setString(Constants.USER_EMAIL,edEmailAddress.text.toString())
            SharedPref(requireActivity()).setString(Constants.USER_PHONE,edPhone.text.toString())
            SharedPref(requireActivity()).setString(Constants.USER_ADDRESS,edPostalAddress.text.toString())
            Toast.makeText(context,getString(R.string.saved_success),Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_settings_to_profile)

        }

    }
}