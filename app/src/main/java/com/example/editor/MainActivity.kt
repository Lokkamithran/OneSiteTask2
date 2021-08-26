package com.example.editor

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.editor.fragments.ImageSelectorFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_image_selector.*


class MainActivity : AppCompatActivity(){

    private val imageSelectorFragment = ImageSelectorFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setFragment(imageSelectorFragment)
    }
    private fun setFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                selectImageText.visibility = View.VISIBLE
                selectImageButton.visibility = View.VISIBLE
                saveButton.visibility = View.GONE
                chosenImageView.visibility = View.GONE
                greyScaleButton.visibility = View.GONE
                greyScaleButton.setBackgroundColor(
                    greyScaleButton.context.resources.getColor(R.color.drawColor)
                    )
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                ImageSelectorFragment().onEnd()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}