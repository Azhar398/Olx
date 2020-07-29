package com.app.olxapplication.ui.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.olxapplication.BaseFragment
import com.app.olxapplication.R
import com.app.olxapplication.ui.login.LoginActivity
import com.app.olxapplication.utilities.Constants
import com.app.olxapplication.utilities.SharedPref
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile,container,false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setdata()
        listener()
    }

    private fun listener() {
        ll_settings.setOnClickListener(this)
        ll_logout.setOnClickListener(this)
    }

    private fun setdata() {
        tvName.text = SharedPref(requireActivity()).getString(Constants.USER_NAME)
        tvEmail.text = SharedPref(requireActivity()).getString(Constants.USER_EMAIL)
        Glide.with(requireActivity())
            .load(SharedPref(requireActivity()).getString(Constants.USER_PHOTO))
            .placeholder(R.drawable.man)
            .into(imageViewUser)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ll_settings->{
                 findNavController().navigate(R.id.action_profile_to_settings)
            }
            R.id.ll_logout->{
                 showAletDialog()
            }

        }
    }

    private fun showAletDialog() {
        var  builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.logout))
        builder.setMessage(getString(R.string.sure_logout))
        builder.setIcon(R.drawable.ic_warning)

        builder.setPositiveButton(getString(R.string.yes)){dialog: DialogInterface?, which: Int ->
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()

            clearSession()

            startActivity(Intent(requireActivity(),LoginActivity::class.java))
            requireActivity().finish()
            dialog?.dismiss()

        }
        builder.setNegativeButton(getString(R.string.no)){dialog, which ->
            dialog.dismiss()

        }
        val alertDialog:AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    private fun clearSession() {
        SharedPref(requireActivity()).setString(Constants.USER_PHOTO,"")
        SharedPref(requireActivity()).setString(Constants.USER_EMAIL,"")
        SharedPref(requireActivity()).setString(Constants.USER_NAME,"")
        SharedPref(requireActivity()).setString(Constants.USER_PHONE,"")
        SharedPref(requireActivity()).setString(Constants.USER_ID,"")

    }
}