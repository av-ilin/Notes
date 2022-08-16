package ru.lglass.notes

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_view.*
import ru.lglass.notes.adapters.AdapterRecyclerImagesView

class ViewActivity : AppCompatActivity() {
    var images = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)
        tvNoteNameA.text = intent.getStringExtra("Name")
        tvNoteDescriptionA.text = intent.getStringExtra("Description")
        val img = intent.getStringExtra("Images")
        if (img != "None") {
            images = img!!.split("|")
            rvNoteImage.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            rvNoteImage.adapter = AdapterRecyclerImagesView(images, object : AdapterRecyclerImagesView.OnClick{
                override fun openImg(position: Int, context: Context) {
                    val intent = Intent(context, ImageActivity::class.java)
                    intent.putExtra("Images", img)
                    intent.putExtra("Current", position)
                    startActivity(intent)
                }
            })
        }
        btCancel.setOnClickListener { finish() }
    }
}