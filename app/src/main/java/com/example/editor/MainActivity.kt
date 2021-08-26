package com.example.editor

import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.editor.fragments.ImageSelectorFragment
import kotlinx.android.synthetic.main.activity_main.*
import androidx.annotation.NonNull


class MainActivity : AppCompatActivity(){

    private val imageSelectorFragment = ImageSelectorFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        swapFragments(imageSelectorFragment)
    }
    fun swapFragments(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                swapFragments(ImageSelectorFragment())
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
        }
        return super.onOptionsItemSelected(item)
    }

//    override fun passBitmap(bitmap: Bitmap) {
//        val bundle = Bundle()
//        bundle.putParcelable("bitmap", bitmap)
//
//        val frag2 = EditImageFragment()
//        frag2.arguments = bundle
//
//        swapFragments(frag2)
//    }
}