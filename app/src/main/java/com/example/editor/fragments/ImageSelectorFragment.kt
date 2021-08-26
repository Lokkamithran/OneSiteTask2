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
        //bitmap = MediaStore.Images.Media.getBitmap((activity as MainActivity).contentResolver, imageUri)

        selectImageText.visibility = View.GONE
        selectImageButton.visibility = View.GONE
        saveButton.visibility = View.VISIBLE
        chosenImageView.visibility = View.VISIBLE
        addTextButton.visibility = View.VISIBLE
        greyScaleButton.visibility = View.VISIBLE
    }
}
class DrawableImageView : androidx.appcompat.widget.AppCompatImageView, OnTouchListener {
    private var downx = 0f
    private var downy = 0f
    private var upx = 0f
    private var upy = 0f
    private var canvas: Canvas? = null
    private var paint: Paint? = null
    private var matrixMe: Matrix? = null

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
        paint = Paint()
        paint!!.color = resources.getColor(R.color.drawColor, resources.newTheme())
        paint!!.strokeWidth = 10f
        matrixMe = Matrix()
        canvas!!.drawBitmap(bmp!!, matrixMe!!, paint)
        setImageBitmap(alteredBitmap)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downx = event.x //event.getX();
                downy = event.y //event.getY();
            }
            MotionEvent.ACTION_MOVE -> {
                upx = event.x //event.getX();
                upy = event.y //event.getY();
                canvas!!.drawLine(downx, downy, upx, upy, paint!!)
                invalidate()
                downx = upx
                downy = upy
            }
            MotionEvent.ACTION_UP -> {
                upx = event.x //event.getX();
                upy = event.y //event.getY();
                //canvas!!.drawLine(downx, downy, upx, upy, paint!!)
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
//private const val STROKE_WIDTH = 12f
//class DrawCanvas (context: Context, attrs: AttributeSet): View(context, attrs) {
//    private lateinit var extraCanvas: Canvas
//    private var extraBitmap: Bitmap = bitmap
//    private val drawColour= ResourcesCompat.getColor(resources, R.color.drawColor, null)
//    private var path = Path()
//    private var motionTouchEventX = 0f
//    private var motionTouchEventY = 0f
//    private var currentX = 0f
//    private var currentY = 0f
//    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop
//
//    private val paint = Paint().apply {
//        color = drawColour
//        isAntiAlias = true
//        isDither = true
//        style = Paint.Style.STROKE
//        strokeJoin = Paint.Join.ROUND
//        strokeCap = Paint.Cap.ROUND
//        strokeWidth = STROKE_WIDTH
//    }
//
//    override fun onTouchEvent(event: MotionEvent): Boolean {
//        motionTouchEventX = event.x
//        motionTouchEventY = event.y
//        when(event.action){
//            MotionEvent.ACTION_DOWN -> touchStart()
//            MotionEvent.ACTION_MOVE -> touchMove()
//            MotionEvent.ACTION_UP -> touchUp()
//        }
//        return true
//    }
//
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//
//        //if(extraBitmap==null)
//            extraBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
//        extraCanvas = extraBitmap.let { Canvas(it) }
//    }
//
//    override fun onDraw(canvas: Canvas){
//        super.onDraw(canvas)
//
//        extraBitmap.let { canvas.drawBitmap(it, 0f, 0f, null) }
//    }
//
//    private fun touchStart() {
//        path.reset()
//        path.moveTo(motionTouchEventX, motionTouchEventY)
//        currentX = motionTouchEventX
//        currentY = motionTouchEventY
//    }
//
//    private fun touchMove() {
//        val dx = abs(motionTouchEventX - currentX)
//        val dy = abs(motionTouchEventY - currentY)
//        if(dx>=touchTolerance || dy>=touchTolerance){
//            path.quadTo(currentX, currentY, (currentX+motionTouchEventX)/2, (currentY+motionTouchEventY)/2)
//            currentX = motionTouchEventX
//            currentY = motionTouchEventY
//
//            extraCanvas.drawPath(path, paint)
//        }
//        invalidate()
//    }
//    private fun touchUp() {path.reset()}
//}