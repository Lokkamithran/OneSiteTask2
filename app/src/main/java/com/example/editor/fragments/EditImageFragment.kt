package com.example.editor.fragments

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.editor.MainActivity
import com.example.editor.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_edit_image.*

import android.os.Build
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import java.io.IOException
import kotlin.math.abs


//class EditImageFragment : Fragment() {

//    var isGreyscale = false
//    var bitmap: Bitmap? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        val binding = inflater.inflate(R.layout.fragment_edit_image, container, false)
//        bitmap = arguments?.getParcelable("bitmap")
//        return binding
//    }
//    @RequiresApi(Build.VERSION_CODES.P)
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        val imageUri = MainActivity.imageUri
//        if(imageUri!=null)
//            imageView.setImageURI(imageUri)
//
//        bitmap = MediaStore.Images.Media.getBitmap((activity as MainActivity).contentResolver, imageUri)


//        greyScaleButton.setOnClickListener {
//            val matrix = ColorMatrix()
//            matrix.setSaturation(if(isGreyscale) {
//                isGreyscale = false
//                1f
//            }else {
//                isGreyscale = true
//                0f
//            })
//            imageView.colorFilter = ColorMatrixColorFilter(matrix)
//        }
//    }
//}
