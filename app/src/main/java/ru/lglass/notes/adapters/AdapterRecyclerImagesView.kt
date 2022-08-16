package ru.lglass.notes.adapters

import android.content.Context
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
import ru.lglass.notes.R
import java.io.File

class AdapterRecyclerImagesView (private val images: List<String>, private val listener: OnClick) : RecyclerView.Adapter<AdapterRecyclerImagesView.ImagesViewHolder>(){
    public interface OnClick{
        fun openImg(position: Int, context: Context)
    }

    class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val image: ImageView = itemView.findViewById(R.id.ivImageView)
        public fun bind(imagePath: String, listener: OnClick, position: Int){
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
            image.setOnClickListener { listener.openImg(position, itemView.context) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_view_image, parent, false)
        return  ImagesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        holder.bind(images[position], listener, position)
    }

    override fun getItemCount() = images.size
}