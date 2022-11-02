package com.example.myapplication


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity(), PhotoAdapter.Listener {

    lateinit var binding: ActivityMainBinding
    private val cameraRequestCode = 1
    private val galleryRequestCode = 2
    private var imageUri: Uri?=null
    private var tempUri: Uri?=null
    private val adapter = PhotoAdapter(this)
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isCameraPermissionGranted = false
    private var isReadPermissionGranted = false
    private var isWritePermissionGranted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions ->
            isCameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: isCameraPermissionGranted
            isReadPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
            isWritePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
        }

        requestPermission()
        init()

        val popupMenu1 = PopupMenu(
            this,
            binding.buttonAdd,
        )
        popupMenu1.menuInflater.inflate(R.menu.menu1_item, popupMenu1.menu)
        popupMenu1.setOnMenuItemClickListener { MenuItem ->
            val id = MenuItem.itemId


           if     (id == R.id.item_11) {
               camera()
           }
           else if     (id == R.id.item_22)  {
                    gallery()
                }
            false

        }
        binding.buttonAdd.setOnClickListener {
            popupMenu1.show()
        }


    }

    private fun requestPermission(){
        isCameraPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        isReadPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        isWritePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest : MutableList<String> = ArrayList()

        if (!isCameraPermissionGranted){
            permissionRequest.add(Manifest.permission.CAMERA)
        }
        if (!isReadPermissionGranted){
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!isWritePermissionGranted){
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionRequest.isNotEmpty()){
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    override fun onclick(uri: Uri) {
        imageUri=uri

        val popupMenu = PopupMenu(
            this,
            binding.rcView,
        )
        popupMenu.menuInflater.inflate(R.menu.menu_item, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { MenuItem ->
            val id = MenuItem.itemId


            if     (id == R.id.item_2) {
                camera()
            }
            else if     (id == R.id.item_3)  {
                gallery()
            }
            else if     (id == R.id.item_4)  {
                tempUri?.let { adapter.photoList.add(it) }
                Glide.with(this@MainActivity).load(imageUri).into(binding.imageView5)
                tempUri = imageUri
                adapter.removeItem(imageUri)
            }
            else if     (id == R.id.item_5)  {
                adapter.removeItem(imageUri)
            }
            false

        }

            popupMenu.show()

    }


        private fun camera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, cameraRequestCode)
    }

    private fun gallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, galleryRequestCode)
    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){

            when(requestCode){
                cameraRequestCode-> {

                    val bitmap = data?.extras?.get("data") as Bitmap

                    val uri = getImageUriFromBitmap(this@MainActivity, bitmap)
                    adapter.photoList.add(uri)
                    binding.rcView.adapter!!.notifyDataSetChanged()

                    val imageData = data.data
                    if (imageData != null) {

                        adapter.photoList.add(uri)
                        binding.rcView.adapter!!.notifyDataSetChanged()

                    }
                }

                galleryRequestCode->{

                    if (data != null) {
                        val imageData = data.data
                        if (imageData != null) {

                            adapter.photoList.add(imageData)
                            binding.rcView.adapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    private fun init() {
        binding.apply {

            val warehouse = resources.getStringArray(R.array.Warehouse)
            val warehouseAdapter =
                ArrayAdapter(this@MainActivity, R.layout.dropdown_item, warehouse)
            binding.autoCompleteTextView3.setAdapter(warehouseAdapter)
            rcView.layoutManager = GridLayoutManager(this@MainActivity, 5)
            rcView.adapter = adapter

        }
    }
}





