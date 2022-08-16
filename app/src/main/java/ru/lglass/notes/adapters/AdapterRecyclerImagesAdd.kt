package ru.lglass.notes.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.lglass.notes.R
import java.io.File

class AdapterRecyclerImagesAdd (private val images: List<String>, private val listener: OnCancelClick) : RecyclerView.Adapter<AdapterRecyclerImagesAdd.ImagesAddViewHolder>() {
    public interface OnCancelClick{
        fun cancel(position: Int)
    }

    class ImagesAddViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val image: ImageView = itemView.findViewById(R.id.ivAddImage)
        private val button: FloatingActionButton = itemView.findViewById(R.id.btDeleteImage)
        public fun bind(imagePath: String, listener: OnCancelClick, position: Int){
            val bMap: Bitmap
            if (File(imagePath).exists())
                bMap = BitmapFactory.decodeFile(imagePath)
            else {
                bMap = ContextCompat.getDrawable(
                    itemView.context,
                    R.drawable.ic_baseline_not_interested_24
                )!!.toBitmap()
                image.setBackgroundColor(Color.BLACK)
            }
            image.setImageBitmap(bMap)
            button.setOnClickListener { listener.cancel(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesAddViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_add_image, parent, false)
        return ImagesAddViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImagesAddViewHolder, position: Int) {
        holder.bind(images[position],listener, position)
    }

    override fun getItemCount() = images.size
}