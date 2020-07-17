package com.app.olxapplication.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.app.olxapplication.R
import com.app.olxapplication.model.CategoriesModel
import com.app.olxapplication.ui.home.adapter.CategoriesAdapter
import com.app.olxapplication.utilities.Constants
import com.app.olxapplication.utilities.SharedPref
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), CategoriesAdapter.ItemClickListener {

    private lateinit var categoriesAdapter: CategoriesAdapter
    val db= FirebaseFirestore.getInstance()
    private lateinit var categoriesModel:MutableList<CategoriesModel>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvCityName.text = SharedPref(requireActivity()).getString(Constants.CITY_NAME)

        getCategoryList()

        textListener()

    }

    private fun textListener() {
        edSearch.addTextChangedListener(object :TextWatcher{
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
        var temp:MutableList<CategoriesModel> = ArrayList()
        for (data in categoriesModel){

            if (data.key.contains(toString.capitalize())||data.key.contains(toString)){
                temp.add(data)
            }
        }
        categoriesAdapter.updateList(temp)
    }

    private fun getCategoryList() {
        db.collection("Categories").get().addOnSuccessListener {
            categoriesModel = it.toObjects(CategoriesModel::class.java)
            setAdapter()
        }

    }

    private fun setAdapter() {
        rv_categories.layoutManager = GridLayoutManager(context,3)
        categoriesAdapter = CategoriesAdapter(categoriesModel,this)
        rv_categories.adapter= categoriesAdapter
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(context,"Hey"+position,Toast.LENGTH_SHORT).show()
    }
}