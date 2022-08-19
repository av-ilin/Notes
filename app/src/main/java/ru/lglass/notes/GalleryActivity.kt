package ru.lglass.notes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_gallery.*
import ru.lglass.notes.adapters.AdapterRecyclerGallery

class GalleryActivity : AppCompatActivity() {
    val gallery = mutableListOf<String>()
    val images = mutableListOf<String>()

    private fun fillGallery(){
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val column = arrayOf(MediaStore.Images.Media.DATA)
        val orderBy = MediaStore.Images.Media.DATE_ADDED
        val cursor = contentResolver.query(uri, column, null, null, "$orderBy DESC")
        val index = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        while (cursor.moveToNext())
            gallery.add(cursor.getString(index))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        fillGallery()
        rvGallery.setHasFixedSize(true)
        rvGallery.layoutManager = GridLayoutManager(this, 4)
        rvGallery.adapter = AdapterRecyclerGallery(gallery, object : AdapterRecyclerGallery.GalleryListener{
            override fun choose(position: Int, flag: Boolean) {
                if (flag)
                    images.add(gallery[position])
                else
                    images.remove(gallery[position])
            }
            override fun view(position: Int, context: Context) {
                val intent = Intent(context, ImageActivity::class.java)
                intent.putExtra("Images", gallery[position])
                startActivity(intent)
            }
        })
        btDone.setOnClickListener {
            val intent = Intent()
            intent.putExtra("Images", images.joinToString("|"))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        btCancelGallery.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}