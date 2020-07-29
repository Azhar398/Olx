package com.app.olxapplication.ui.uploadPhoto

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.olxapplication.BaseFragment
import com.app.olxapplication.MainActivity
import com.app.olxapplication.R
import com.app.olxapplication.ui.PreviewImageActivity
import com.app.olxapplication.ui.uploadPhoto.adapter.UploadImagesAdapter
import com.app.olxapplication.utilities.Constants
import com.app.olxapplication.utilities.OnActivityResultData
import com.app.olxapplication.utilities.SharedPref
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_upload_photo.*
import me.echodev.resizer.Resizer
import net.alhazmy13.mediapicker.Image.ImagePicker
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class UploadPhotoFragment : BaseFragment(), View.OnClickListener,
    UploadImagesAdapter.ItemClickListener {


    private val imageUriList: ArrayList<String> = ArrayList()
    private var count = 0
    private lateinit var uploadTask: UploadTask
    private var imagesAdapter: UploadImagesAdapter? = null
    private var selectedImageArrayList: ArrayList<String> = ArrayList()
    private var outputFileUri: String? = null
    internal var dialog: BottomSheetDialog? = null
    internal var selectedImage: File? = null
    internal var TAG = UploadPhotoFragment::class.java.simpleName
    val db = FirebaseFirestore.getInstance()
    internal lateinit var storageRef: StorageReference
    internal lateinit var imageRef: StorageReference
    internal lateinit var storage: FirebaseStorage


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_upload_photo, container, false)
        return rootView
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.layoutManager = GridLayoutManager(activity, 3)

        storage = FirebaseStorage.getInstance()

        storageRef = storage.getReference()

        listener()

        registerCallBackPhoto()

    }
    private fun compressFile(file: File): File {
        val resizedImage: File = Resizer(activity)
            .setTargetLength(1024)
            .setQuality(80)
            .setOutputFormat("PNG")
            .setOutputFilename(file.name.substring(0, file.name.indexOf(".") + 1))
            .setOutputDirPath(activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath)
            .setSourceImage(file)
            .getResizedFile()
        return resizedImage
    }

    private fun registerCallBackPhoto() {
        (activity as MainActivity).getOnActivityResult(object : OnActivityResultData {
            override fun resultData(bundle: Bundle) {
                linearLayoutChoosePhoto.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                val mPaths = bundle.getStringArrayList(Constants.IMAGE_PATHS)

                selectedImage = compressFile(File(mPaths!![0]))
                outputFileUri = mPaths[0]
                selectedImageArrayList.add(mPaths[0])
                setAdapter()
            }

        })
    }

    private fun setAdapter() {
        if (imagesAdapter != null) {
            imagesAdapter!!.customNotify(selectedImageArrayList)
        } else {
            imagesAdapter = UploadImagesAdapter(requireActivity(), selectedImageArrayList, this)
            recyclerView.adapter = imagesAdapter
        }
    }

    private fun listener() {
        imageViewChoosePhoto.setOnClickListener(this)
        buttonPreview.setOnClickListener(this)
        buttonUpload.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageViewChoosePhoto -> {
                showBottomSheetDialog()
            }
            R.id.buttonPreview -> {
                if (selectedImage != null) {
                    startActivity(
                        Intent(
                            activity,
                            PreviewImageActivity::class.java
                        ).putExtra("imageUri", outputFileUri)
                    )
                }
            }
            R.id.buttonUpload -> {
                if (selectedImage == null || !selectedImage!!.exists())
                    Toast.makeText(requireActivity(), "Please select Photo", Toast.LENGTH_SHORT)
                        .show()
                else
                    saveFileInFirebaseStorage()
            }
        }
    }

    private fun saveFileInFirebaseStorage() {
        for (i in 0..selectedImageArrayList.size - 1) {
            val file = File(selectedImageArrayList[i])
            uploadImage(file, file.name, i)
        }
    }

    private fun uploadImage(file: File, name: String, i: Int) {
        imageRef = storageRef.child("images/$name")


        uploadTask = imageRef.putFile(Uri.fromFile(file))

        uploadTask.addOnSuccessListener(object : OnSuccessListener<UploadTask.TaskSnapshot> {
            override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                imageRef.downloadUrl.addOnSuccessListener {
                    count++
                    val url = it.toString()
                    imageUriList.add(url)
                    if (count == selectedImageArrayList.size)
                        postAd()
                }

            }

        })
    }

    private fun postAd() {
        showProgressBar()
        val documentId = db.collection(arguments?.getString(Constants.KEY)!!).document().id

        val documentData = hashMapOf(
            Constants.ADDRESS to arguments?.getString(Constants.ADDRESS),
            Constants.BRAND to arguments?.getString(Constants.BRAND),
            Constants.AD_DESCRIPTION to arguments?.getString(Constants.AD_DESCRIPTION),
            Constants.AD_TITLE to arguments?.getString(Constants.AD_TITLE),
            Constants.PHONE to arguments?.getString(Constants.PHONE),
            Constants.PRICE to arguments?.getString(Constants.PRICE),
            Constants.TYPE to arguments?.getString(Constants.KEY),
            Constants.YEAR to arguments?.getString(Constants.YEAR),
            Constants.KM_DRIVEN to arguments?.getString(Constants.KM_DRIVEN),
            Constants.Id to documentId,
            Constants.USER_ID to SharedPref(requireActivity()).getString(Constants.USER_ID),
            Constants.CREATED_DATE to Date(),
            "images" to imageUriList

        )

        db.collection(arguments?.getString(Constants.KEY)!!)
            .add(documentData)
            .addOnSuccessListener {
                updateDocumentId(it.id)
            }
    }

    private fun updateDocumentId(id: String) {
        val docData = mapOf(
            Constants.Id to id
        )
        db.collection(arguments?.getString(Constants.KEY)!!)
            .document(id)
            .update(docData).addOnSuccessListener {
                hideProgressBar()
                Toast.makeText(requireActivity(), "Ad Posted Successfully", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_photo_my_ads)

            }


    }

    private fun showBottomSheetDialog() {
        val linearInflater =
            requireActivity().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.bottomsheet_dialog, null)
        dialog = BottomSheetDialog(requireActivity())
        dialog?.setContentView(view)
        dialog?.window?.findViewById<View>(R.id.design_bottom_sheet)
            ?.setBackgroundColor(resources.getColor(android.R.color.transparent))

        var textViewGallery = dialog!!.findViewById<TextView>(R.id.textViewPhoto)
        var textViewCamera = dialog!!.findViewById<TextView>(R.id.textViewCamera)
        var textViewCancel = dialog!!.findViewById<TextView>(R.id.textViewCancel)

        textViewCamera?.setOnClickListener {
            dialog!!.dismiss()
            chooseImage(ImagePicker.Mode.CAMERA)
        }
        textViewGallery?.setOnClickListener {
            dialog!!.dismiss()
            chooseImage(ImagePicker.Mode.GALLERY)

        }
        textViewCancel?.setOnClickListener {
            dialog!!.dismiss()
        }


        dialog?.show()
        val lp = WindowManager.LayoutParams()
        val window = dialog?.window
        lp.copyFrom(window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
    }

    private fun chooseImage(mode: ImagePicker.Mode) {
        ImagePicker.Builder(requireActivity())
            .mode(mode)
            .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
            .directory(ImagePicker.Directory.DEFAULT)
            .extension(ImagePicker.Extension.PNG)
            .allowMultipleImages(false)
            .enableDebuggingMode(true)
            .build()
    }

    override fun onItemClick() {
        showBottomSheetDialog()
    }
}