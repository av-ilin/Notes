package ru.lglass.notes

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_change.*
import ru.lglass.notes.adapters.AdapterRecyclerImagesAdd

class ChangeActivity : AppCompatActivity() {
    private val PICK_FROM_GALLERY = 1

    private var images: MutableList<String> = mutableListOf()

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try{
                val data: Intent? = result.data
                val imageUri = data!!.data

                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor? =
                    contentResolver.query(imageUri!!, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                val filePath: String = cursor.getString(columnIndex)
                cursor.close()

                images.add(filePath)
                rvChangeImages.adapter!!.notifyItemInserted(images.size - 1)
            }catch (e: Exception){}
        }
    }

    private fun changeNote(name: String, description: String){
        // "{\"id\":\"$id\",\"name\":\"$name\",\"description\":\"$description\"}"
        val id = intent.getStringExtra("ID")
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
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    resultLauncher.launch(intent)
                }
                else
                    Toast.makeText(this, "PERMISSION DENIED", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change)
        teChangeName.setText(intent.getStringExtra("Name"))
        teChangeDescription.setText(intent.getStringExtra("Description"))
        if (intent.getStringExtra("Images") != "None") {
            val img = intent.getStringExtra("Images")!!.split("|")
            images = MutableList(img.size){i -> img[i]}
        }
        rvChangeImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvChangeImages.adapter = AdapterRecyclerImagesAdd(images, object: AdapterRecyclerImagesAdd.OnCancelClick{
            override fun cancel(position: Int) {
                images.removeAt(position)
                rvChangeImages.adapter!!.notifyItemRemoved(position)
            }
        })
        btCancelCA.setOnClickListener { finish() }
        btApplyCA.setOnClickListener {
            changeNote(teChangeName.text.toString(), teChangeDescription.text.toString())
            finish()
        }
        btAddImage.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PICK_FROM_GALLERY)
            } else {
                //val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                resultLauncher.launch(intent)
            }
        }
    }
}