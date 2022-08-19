package ru.lglass.notes.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.lglass.notes.R

class AdapterRecyclerGallery (private val images: List<String>, private val listener: GalleryListener)
    :RecyclerView.Adapter<AdapterRecyclerGallery.GalleryViewHolder>() {
    public interface GalleryListener{
        fun choose(position: Int, flag: Boolean)
        fun view(position: Int, context: Context)
    }

    class GalleryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private var choose = false
        private val image: ImageView = itemView.findViewById(R.id.ivGImage)
        private val button: FloatingActionButton = itemView.findViewById(R.id.btChoose)
        public  fun bind(imagePath: String, listener: GalleryListener){
            Glide.with(itemView.context).load(imagePath).into(image)
            image.setOnClickListener { listener.view(position, itemView.context) }
            button.setOnClickListener {
                choose = !choose
                if (choose)
                    button.backgroundTintList = ColorStateList.valueOf(Color.BLUE)
                else
                    button.backgroundTintList = ColorStateList.valueOf(Color.WHITE)
                listener.choose(position, choose)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_gallery, parent, false)
        return GalleryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(images[position], listener)
    }

    override fun getItemCount() = images.size
}