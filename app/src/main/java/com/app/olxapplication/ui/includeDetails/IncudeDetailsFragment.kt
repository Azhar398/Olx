package com.app.olxapplication.ui.includeDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.olxapplication.BaseFragment
import com.app.olxapplication.R
import com.app.olxapplication.utilities.Constants
import kotlinx.android.synthetic.main.fragment_include_details.*

class IncudeDetailsFragment : BaseFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_include_details, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments?.getString(Constants.KEY)!!.equals(Constants.CAR) ||
            arguments?.getString(Constants.KEY)!!.equals(Constants.Bike)) {
            llkmDriven.visibility = View.VISIBLE
        }

        listener()

    }

    private fun listener() {
        textViewNext.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
           R.id.textViewNext->{
               sendData()
           }
        }
    }

    private fun sendData() {
        if (edBrand.text?.isEmpty()!!)
            edBrand.setError(getString(R.string.enter_brand_name))
        else if (edPhone.text?.isEmpty()!!)
            edBrand.setError(getString(R.string.enter_phone_number))
        else{
            val bundle = Bundle()
            bundle.putString(Constants.BRAND,edBrand.text.toString())
            bundle.putString(Constants.YEAR,edYear.text.toString())
            bundle.putString(Constants.AD_TITLE,edAdTitle.text.toString())
            bundle.putString(Constants.AD_DESCRIPTION,edDescribe.text.toString())
            bundle.putString(Constants.ADDRESS,edPostalAddress.text.toString())
            bundle.putString(Constants.PHONE,edPhone.text.toString())
            bundle.putString(Constants.PRICE,edPrice.text.toString())
            bundle.putString(Constants.KM_DRIVEN,edKmDriven.text.toString())
            bundle.putString(Constants.KEY,arguments?.getString(Constants.KEY))
            findNavController().navigate(R.id.action_details_photo_upload,bundle)
        }

    }
}