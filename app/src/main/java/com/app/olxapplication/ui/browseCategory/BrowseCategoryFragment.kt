package com.app.olxapplication.ui.browseCategory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.olxapplication.BaseFragment
import com.app.olxapplication.R
import com.app.olxapplication.model.CategoriesModel
import com.app.olxapplication.model.DataItemModel
import com.app.olxapplication.ui.browseCategory.adapter.BrowseCategoryAdapter
import com.app.olxapplication.utilities.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_browse.*
import kotlinx.android.synthetic.main.fragment_browse.edSearch
import kotlinx.android.synthetic.main.fragment_browse.rv_categories
import kotlinx.android.synthetic.main.fragment_home.*

class BrowseCategoryFragment : BaseFragment(), BrowseCategoryAdapter.ItemClickListener {

    private lateinit var categoriesAdapter: BrowseCategoryAdapter
    private lateinit var dataItemModel: MutableList<DataItemModel>
    val db=FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_browse,container,false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rv_categories.layoutManager = LinearLayoutManager(context)

        getlist()

        textListener()


    }

    private fun getlist() {
        showProgressBar()

        db.collection(arguments?.getString(Constants.KEY)!!)
            .get().addOnSuccessListener {
                hideProgressBar()
                dataItemModel = it.toObjects(DataItemModel::class.java)
                setAdapter()

            }

    }

    private fun setAdapter() {
        categoriesAdapter = BrowseCategoryAdapter(dataItemModel,this)
        rv_categories.adapter=categoriesAdapter
    }

    override fun onItemClick(position: Int) {
        var bundle = Bundle()
        bundle.putString(Constants.DOCUMENT_ID,dataItemModel.get(position).id)
        bundle.putString(Constants.KEY,dataItemModel.get(position).type)
        findNavController().navigate(R.id.action_browse_to_details,bundle)
    }


    private fun textListener() {
        edSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterList(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    private fun filterList(toString: String) {
        var temp:MutableList<DataItemModel> = ArrayList()
        for (data in dataItemModel){

            if (data.Brand.contains(toString.capitalize())||data.Brand.contains(toString)
                ||data.adtitle.contains(toString.capitalize())||data.adtitle.contains(toString)
            ){
                temp.add(data)
            }
        }
        categoriesAdapter.updateList(temp)
    }
}