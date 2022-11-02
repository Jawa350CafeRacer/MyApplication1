package com.example.myapplication

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FotoItemBinding

class PhotoAdapter(private val listener: Listener): RecyclerView.Adapter<PhotoAdapter.PhotoHolder>() {
    var photoList = ArrayList<Uri>()


    class PhotoHolder(item: View): RecyclerView.ViewHolder(item) {
        private val binding = FotoItemBinding.bind(item)

        fun bind(uri: Uri, listener: Listener) = with(binding) {
           im.setImageURI(uri)
            itemView.setOnClickListener {
                listener.onclick(uri)
              //  popupMenus(it)
            }
        }

       // @SuppressLint("DiscouragedPrivateApi")
       // private fun popupMenus(view: View?) {
       //     val popupMenus = PopupMenu(c, view)
       //     popupMenus.inflate(R.menu.menu_item)
       //     popupMenus.setOnMenuItemClickListener {
       //         when(it.itemId){
      //              R.id.item_2 -> {
       //               Toast.makeText(c, "AAAA", Toast.LENGTH_SHORT).show()
       //                 true
       //             }
        //            R.id.item_3 -> {
       //                 Toast.makeText(c, "AAAA", Toast.LENGTH_SHORT).show()
        //                true
        //            }
         //           R.id.item_4 -> {
         //               Toast.makeText(c, "AAAA", Toast.LENGTH_SHORT).show()
        //                true
        //            }
        //            R.id.item_5 -> {
        //                Toast.makeText(c, "AAAA", Toast.LENGTH_SHORT).show()
        //                true
        //            }
         //           else -> true
         //       }

         //   }
         //   popupMenus.show()
         //   val popup = PopupMenu::class.java.getDeclaredField("mPopup")
         //   popup.isAccessible = true
        //    val menu = popup.get(popupMenus)
        //    menu.javaClass.getDeclaredMethod("setForceIcon", Boolean::class.java)
        //        .invoke(menu, true)
        //}


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.foto_item, parent, false)
        return PhotoHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        holder.bind(photoList[position], listener)
    }

    override fun getItemCount(): Int {
       return photoList.size
    }

    interface Listener{
        fun onclick(uri: Uri)
       // val popupMenus = PopupMenu(lis)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItem(uri: Uri?) {
        photoList.remove(uri)
        notifyDataSetChanged()
    }
}
