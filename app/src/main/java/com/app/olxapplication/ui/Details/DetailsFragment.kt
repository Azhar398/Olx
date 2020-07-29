package com.app.olxapplication.ui.Details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.olxapplication.BaseFragment
import com.app.olxapplication.R
import com.app.olxapplication.model.DataItemModel
import com.app.olxapplication.ui.Details.adapter.DetailImagesAdapter
import com.app.olxapplication.ui.PreviewImageActivity
import com.app.olxapplication.utilities.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_details.*
import java.text.SimpleDateFormat
import java.util.*

class DetailsFragment : BaseFragment(), DetailImagesAdapter.onItemclick {

    private lateinit var dataItemModel: DataItemModel
    val db  = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_details,container,false)
        return rootView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getItemDetails()

        clickListener()

        if (arguments?.getString(Constants.KEY).equals(Constants.CAR))
            llkmDriven.visibility=View.VISIBLE
    }

    private fun clickListener() {
        tvCall.setOnClickListener(View.OnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:"+dataItemModel.Phone)
            startActivity(dialIntent)
        })
    }

    private fun getItemDetails() {
        showProgressBar()

        db.collection(arguments?.getString(Constants.KEY)!!)
            .document(arguments?.getString(Constants.DOCUMENT_ID)!!)
            .get().addOnSuccessListener {
                hideProgressBar()
                dataItemModel = it.toObject(DataItemModel::class.java)!!
                setData()
                setPagerAdapter()
            }



    }

    private fun setPagerAdapter() {
        val detailImagesAdapter = DetailImagesAdapter(requireContext(),dataItemModel.images,this)
        viewPager.adapter = detailImagesAdapter
        viewPager.offscreenPageLimit = 1

    }

    private fun setData() {

        tvPrice.text = Constants.CURRENCY_SYMBOL+dataItemModel.Price
        tvTitle.text = dataItemModel.adtitle
        tvAddress.text = dataItemModel.address
        tvBrand.text = dataItemModel.Brand
        tvDescription.text = dataItemModel.addescription
        tvKmDriven.text = dataItemModel.kmDriven
        tvPhone.text = dataItemModel.Phone
        tvYear.text = dataItemModel.year
        val dateformat = SimpleDateFormat(
            "dd MMM" , Locale.getDefault()
        )
        tvDate.text = dateformat.format(dataItemModel.createdDate)



    }

    override fun onclick(position: Int) {
    startActivity(Intent(activity,PreviewImageActivity::class.java)
        .putExtra("imageUrl",dataItemModel.images.get(position)))
    }
}