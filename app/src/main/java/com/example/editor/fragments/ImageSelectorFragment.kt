package com.example.editor.fragments

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
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
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import android.graphics.ColorMatrixColorFilter

import android.graphics.ColorMatrix

import android.graphics.Bitmap
import android.renderscript.*
import android.util.DisplayMetrics

class ImageSelectorFragment : Fragment() {

    private var bitmap: Bitmap? = null
    private var alteredBitmap: Bitmap? = null
    private var bitmapSave: Bitmap? = null

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
                val permissions1 = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions1, 1001)
            } else pickImage()
        }
        saveButton.setOnClickListener {
            val bitmapDrawableImageView = chosenImageView.drawable
            bitmapSave = bitmapDrawableImageView.toBitmap()
            val outputStream: OutputStream?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    val contentResolver: ContentResolver =
                        (activity as MainActivity).contentResolver
                    val contentValues = ContentValues()
                    contentValues.put(
                        MediaStore.MediaColumns.DISPLAY_NAME,
                        "Image_${System.currentTimeMillis()}.jpg"
                    )
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    contentValues.put(
                        MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES
                                + File.separator
                                + "Photo_Editor"
                    )
                    val imageUri = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    outputStream =
                        Objects.requireNonNull(imageUri)
                            .let { contentResolver.openOutputStream(it!!) }
                    bitmapSave?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    Objects.requireNonNull(outputStream)


                    Toast.makeText(activity as MainActivity, "Image Saved", Toast.LENGTH_SHORT)
                        .show()
                } catch (e: Exception) {
                    Toast.makeText(
                        activity as MainActivity,
                        "Image not saved: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }else{
                if (activity?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                    val permissions1 = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions1, 1001)
                }
                else if (activity?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                    val permissions2 = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permissions2, 1002)
                }
                else saveImage(bitmapSave!!)
            }
        }
        greyScaleButton.setOnClickListener {
            val bitmapDrawableImageView = chosenImageView.drawable
            bitmapSave = bitmapDrawableImageView.toBitmap()
            bitmapSave = toGrayscale(bitmapSave!!)
            alteredBitmap = Bitmap.createBitmap(bitmapSave!!.width, bitmapSave!!.height, bitmapSave!!.config)
            chosenImageView.setNewImage(alteredBitmap, bitmapSave)
            greyScaleButton.setBackgroundColor(
                greyScaleButton.context.resources.getColor(R.color.drawColorGrayscale)
            )
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
                    Toast.makeText(activity,"Permission1 Denied", Toast.LENGTH_SHORT).show()
            }
            1002 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    saveImage(bitmapSave!!)
                else
                    Toast.makeText(activity, "Permission2 Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveImage(bitmap: Bitmap){
        var outputStream: OutputStream? = null
        val file = Environment.getExternalStorageDirectory()
            val dir = File(file.absolutePath + "/Editor/")
            dir.mkdir()

            val fileName = String.format("Image_${System.currentTimeMillis()}.png")
            val outFile = File(dir, fileName)

            try {
                outputStream = FileOutputStream(outFile)
            } catch (e: Exception) {
                Toast.makeText(
                    activity as MainActivity,
                    "Image not saved: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        Log.d("ImageFragment","the path is: ${outFile.absolutePath}")
        Toast.makeText(activity as MainActivity, "Image Saved", Toast.LENGTH_SHORT).show()
            try {
                outputStream?.flush()
            } catch (e: Exception) {
                Toast.makeText(
                    activity as MainActivity,
                    "Image not saved: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            try {
                outputStream?.close()
            } catch (e: Exception) {
                Toast.makeText(
                    activity as MainActivity,
                    "Image not saved: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
    private fun pickImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,1)
    }
    private fun toGrayscale(bmpOriginal: Bitmap): Bitmap? {
        val height: Int = bmpOriginal.height
        val width: Int = bmpOriginal.width
        val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        val matrix = Matrix()
        c.drawBitmap(bmpOriginal, matrix, paint)
        return bmpGrayscale
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && requestCode == 1){
            imageUri = data?.data!!
            onImageSelected()

            try {
                val bmpFactoryOptions = BitmapFactory.Options()
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
    private fun getPointerCoords(e: MotionEvent): FloatArray {
        val index = e.actionIndex
        val coords = floatArrayOf(e.getX(index), e.getY(index))
        val matrix = Matrix()
        imageMatrix.invert(matrix)
        matrix.postTranslate(scrollX.toFloat(), scrollY.toFloat())
        matrix.mapPoints(coords)
        return coords
    }
}