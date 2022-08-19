package ru.lglass.notes

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_add.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import ru.lglass.notes.adapters.AdapterRecyclerImagesAdd

class AddActivity : AppCompatActivity() {
    private val PICK_FROM_GALLERY = 1

    private val images: MutableList<String> = mutableListOf()

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            images.clear()
            images.addAll(result.data!!.getStringExtra("Images")!!.split("|"))
            rvImages.adapter = AdapterRecyclerImagesAdd(images, object: AdapterRecyclerImagesAdd.OnCancelClick{
                override fun cancel(position: Int) {
                    images.removeAt(position)
                    rvImages.adapter!!.notifyItemRemoved(position)
                }
            })
        }
    }

    private fun genId():String{
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        var id =  (1..6).map{ allowedChars.random() }.joinToString("")
        val fileList = applicationContext.fileList()
        while ("Note_$id" in fileList!!)
            id = (1..6).map{ allowedChars.random() }.joinToString("")
        return id
    }

    private fun addNote(name: String, description: String){
        // "{\"id\":\"$id\",\"name\":\"$name\",\"description\":\"$description\"}"
        val id = genId()
        val fileNoteName = "Note_$id"
        val image =
            if (images.isEmpty()) "None"
            else images.joinToString("|")
        val fileNoteContent = "{\"id\":\"$id\",\"name\":\"$name\",\"description\":\"$description\",\"images\":\"$image\"}"
        applicationContext.openFileOutput(fileNoteName, MODE_PRIVATE).write(fileNoteContent.toByteArray())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PICK_FROM_GALLERY -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "PERMISSION GRANTED", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, GalleryActivity::class.java)
                    resultLauncher.launch(intent)
                }
                else
                    Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        btCancel.setOnClickListener { finish() }
        btApply.setOnClickListener {
            addNote(teName.text.toString(), teDescription.text.toString())
            finish()
        }
        btImage.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PICK_FROM_GALLERY)
            } else {
                //val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                val intent = Intent(this, GalleryActivity::class.java)
                resultLauncher.launch(intent)
            }
        }
        rvImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvImages.adapter = AdapterRecyclerImagesAdd(images, object: AdapterRecyclerImagesAdd.OnCancelClick{
            override fun cancel(position: Int) {
                images.removeAt(position)
                rvImages.adapter!!.notifyItemRemoved(position)
            }
        })
        teDescription.setOnFocusChangeListener { _, b ->
            if (b)
                teName.visibility = View.GONE
            else
                teName.visibility = View.VISIBLE
        }

        KeyboardVisibilityEvent.setEventListener(this) { isOpen ->
            if (!isOpen)
                clAdd.requestFocus()
        }
    }
}