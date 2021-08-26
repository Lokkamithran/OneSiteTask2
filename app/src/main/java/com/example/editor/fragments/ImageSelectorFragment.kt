package com.example.editor.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.editor.Communicator
import com.example.editor.MainActivity
import com.example.editor.R
import kotlinx.android.synthetic.main.fragment_image_selector.*
import kotlinx.android.synthetic.main.fragment_image_selector.view.*
import kotlin.math.abs
import java.lang.Exception
import android.view.MotionEvent

import android.view.View.OnTouchListener
import android.widget.ImageView


class ImageSelectorFragment : Fragment() {

    private var bitmap: Bitmap? = null
    private var alteredBitmap: Bitmap? = null
    private var isGreyscale = false

    private lateinit var imageUri: Uri
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_selector, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectImageButton.setOnClickListener{
            if (activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, 1001)
            } else pickImage()
        }
        greyScaleButton.setOnClickListener {
            val matrix = ColorMatrix()
            matrix.setSaturation(
                if (isGreyscale) {
                    isGreyscale = false
                    greyScaleButton.setBackgroundColor(
                        greyScaleButton.context.resources.getColor(R.color.drawColor)
                    )
                    1f
                } else {
                    isGreyscale = true
                    greyScaleButton.setBackgroundColor(
                        greyScaleButton.context.resources.getColor(R.color.drawColorGrayscale)
                    )
                    0f
                }
            )
            chosenImageView.colorFilter = ColorMatrixColorFilter(matrix)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            1001 -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pickImage()
                else
                    Toast.makeText(activity,"Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun pickImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,1)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == 1){
            imageUri = data?.data!!
            onImageSelected()

            try {
                val bmpFactoryOptions = BitmapFactory.Options()
//                bmpFactoryOptions.inJustDecodeBounds = true
//                bitmap = BitmapFactory
//                    .decodeStream(
//                        activity
//                            ?.contentResolver?.openInputStream(
//                                imageUri
//                            ), null, bmpFactoryOptions
//                    )
                bmpFactoryOptions.inJustDecodeBounds = false
                bitmap = BitmapFactory
                    .decodeStream(
                        activity?.contentResolver?.openInputStream(imageUri)
                        , null, bmpFactoryOptions)

                alteredBitmap = bitmap?.let {
                    Bitmap.createBitmap(it.width, it.height, it.config)
                }

                chosenImageView.setNewImage(alteredBitmap, bitmap)
            } catch (e: Exception) {
                Log.v("ERROR", e.toString())
            }
        }
    }
    private fun onImageSelected(){
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        selectImageText.visibility = View.GONE
        selectImageButton.visibility = View.GONE
        saveButton.visibility = View.VISIBLE
        chosenImageView.visibility = View.VISIBLE
        greyScaleButton.visibility = View.VISIBLE
    }
}
class DrawableImageView : androidx.appcompat.widget.AppCompatImageView, OnTouchListener {
    private var currentX = 0f
    private var currentY = 0f
    private var motionEventX = 0f
    private var motionEventY = 0f
    private var canvas: Canvas? = null
    private var matrixMe: Matrix? = null

    private val paint = Paint().apply {
        color = resources.getColor(R.color.drawColor, resources.newTheme())
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 12f
    }

    constructor(context: Context?) : super(context!!) {
        setOnTouchListener(this)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        setOnTouchListener(this)
    }
    constructor(
        context: Context?, attrs: AttributeSet?,
        defStyleAttr: Int
    ):super(context!!, attrs, defStyleAttr) {
        setOnTouchListener(this)
    }

    fun setNewImage(alteredBitmap: Bitmap?, bmp: Bitmap?) {
        canvas = Canvas(alteredBitmap!!)
        matrixMe = Matrix()
        canvas!!.drawBitmap(bmp!!, matrixMe!!, paint)
        setImageBitmap(alteredBitmap)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentX = getPointerCoords(event)[0]
                currentY = getPointerCoords(event)[1]
            }
            MotionEvent.ACTION_MOVE -> {
                motionEventX = getPointerCoords(event)[0]
                motionEventY = getPointerCoords(event)[1]
                canvas!!.drawLine(currentX, currentY, motionEventX, motionEventY, paint)
                invalidate()
                currentX = motionEventX
                currentY = motionEventY
            }
            MotionEvent.ACTION_UP -> {
                motionEventX = getPointerCoords(event)[0]
                motionEventY = getPointerCoords(event)[1]
                //canvas!!.drawLine(currentX, currentY, motionEventX, motionEventY, paint)
                invalidate()
            }
        }
        return true
    }
    fun getPointerCoords(e: MotionEvent): FloatArray {
        val index = e.actionIndex
        val coords = floatArrayOf(e.getX(index), e.getY(index))
        val matrix = Matrix()
        imageMatrix.invert(matrix)
        matrix.postTranslate(scrollX.toFloat(), scrollY.toFloat())
        matrix.mapPoints(coords)
        return coords
    }
}