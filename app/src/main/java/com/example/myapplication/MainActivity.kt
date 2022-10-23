package com.example.myapplication


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity(), FotoAdapter.Listener {

    lateinit var binding: ActivityMainBinding
    private val cameraRequestCode = 1
    private val galleryRequestCode = 2
    private var imageUri: Uri?=null
    private var tempUri: Uri?=null
    private val adapter = FotoAdapter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerForContextMenu(binding.buttonAdd)

        init()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.menu_item,menu)
    }



    override fun onContextItemSelected(item: MenuItem): Boolean{
        when(item.itemId){
            R.id.item_2 -> {
                cameraCheckPermission()
                galleryCheckPermissionW()
                galleryCheckPermissionR()
                when (Build.VERSION.SDK_INT) {
                    in 1..22 -> {
                        if (allPPermissionGranted() && allPPermissionGrantedW() && allPPermissionGrantedR()) {
                            camera()
                        }
                    }
                    else ->
                        if (allPPermissionGranted() && allPPermissionGrantedR()) {
                            camera()
                        }
                }
            }
            R.id.item_3 -> {
                galleryCheckPermissionW()
                galleryCheckPermissionR()
                if (allPPermissionGrantedR()) {
                    gallery()
                }

            }
            R.id.item_4 -> {


                tempUri?.let { adapter.fotolist.add(it) }
                Glide.with(this@MainActivity).load(imageUri).into(binding.imageView5)
                tempUri = imageUri
                adapter.removeItem(imageUri)
            }
            R.id.item_5 -> {
                adapter.removeItem(imageUri)
            }
        }
        return super.onContextItemSelected(item)
    }

    private fun cameraCheckPermission() {
        if (allPPermissionGranted()){
            //Toast.makeText(this,
            //"Camera permission", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this, Constants.REQUIRED_PERMISSIONS_CAMERA,
                Constants.REQUEST_CODE_PERMISSIONS
            )
        }
    }
    private fun galleryCheckPermissionR() {
        if (allPPermissionGrantedR()){
            //Toast.makeText(this,
            //"Read permission", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this, Constants.REQUIRED_PERMISSIONS_READ,
                Constants.REQUEST_CODE_PERMISSIONS,
            )
        }
    }
    private fun galleryCheckPermissionW() {

        when (Build.VERSION.SDK_INT) {
            in 1..22 -> {

                if (allPPermissionGrantedW()) {
                    //Toast.makeText(this,
                    //"Write permission", Toast.LENGTH_SHORT).show()
                } else {
                    ActivityCompat.requestPermissions(
                        this, Constants.REQUIRED_PERMISSIONS_WRITE,
                        Constants.REQUEST_CODE_PERMISSIONS,
                    )
                }
            }
            else -> //Toast.makeText(this,
               // "Write permission not need SDK>", Toast.LENGTH_SHORT).show()
            return
        }
    }
    private fun allPPermissionGranted() =
        Constants.REQUIRED_PERMISSIONS_CAMERA.all {
            ContextCompat.checkSelfPermission(
                baseContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }
    private fun allPPermissionGrantedR() =
        Constants.REQUIRED_PERMISSIONS_READ.all {
            ContextCompat.checkSelfPermission(
                baseContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }
    private fun allPPermissionGrantedW() =
        Constants.REQUIRED_PERMISSIONS_WRITE.all {
            ContextCompat.checkSelfPermission(
                baseContext, it
            ) == PackageManager.PERMISSION_GRANTED
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
                    adapter.fotolist.add(uri)
                    binding.rcView.adapter!!.notifyDataSetChanged()

                    val imageData = data.data
                    if (imageData != null) {

                        adapter.fotolist.add(uri)
                        binding.rcView.adapter!!.notifyDataSetChanged()

                    }
                }

                galleryRequestCode->{

                    if (data != null) {
                        val imageData = data.data
                        if (imageData != null) {

                            adapter.fotolist.add(imageData)
                            binding.rcView.adapter!!.notifyDataSetChanged()
                        }
                    }
            }
        }
    }
}

    override fun onclick(uri: Uri) {
        imageUri=uri
        openContextMenu(binding.rcView)
        registerForContextMenu(binding.rcView)
    }

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    private fun init(){
        binding.apply {

            val warehouse = resources.getStringArray(R.array.Warehouse)
            val warehouseAdapter = ArrayAdapter(this@MainActivity, R.layout.dropdown_item, warehouse)
            binding.autoCompleteTextView3.setAdapter(warehouseAdapter)
            rcView.layoutManager = GridLayoutManager(this@MainActivity, 5)
            rcView.adapter = adapter
            buttonAdd.setOnClickListener{

                btnCamera.visibility = View.VISIBLE
                btnCamera.setOnClickListener {

                    cameraCheckPermission()
                    galleryCheckPermissionW()
                    galleryCheckPermissionR()
                    when (Build.VERSION.SDK_INT) {
                        in 1..22 -> {
                            if (allPPermissionGranted() && allPPermissionGrantedW() && allPPermissionGrantedR()) {
                                camera()
                            }
                        }
                        else ->
                            if (allPPermissionGranted() && allPPermissionGrantedR()) {
                                camera()
                            }
                    }

                    btnCamera.visibility = View.GONE
                    btnGallery.visibility = View.GONE
                }
                btnGallery.visibility = View.VISIBLE
                btnGallery.setOnClickListener {
                    galleryCheckPermissionR()
                    if (allPPermissionGrantedR()) {
                        gallery()
                    }


                    btnCamera.visibility = View.GONE
                    btnGallery.visibility = View.GONE
                }
            }
        }
    }
}



