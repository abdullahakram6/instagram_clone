package com.example.firebasekotlin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import com.example.firebasekotlin.databinding.ActivityAddPostBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import java.io.ByteArrayOutputStream

class AddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostBinding

    private var myUrl = ""
    private var imageUri : Uri? = null
    private var storagePostPicRef: StorageReference?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Posts Pictures")
         binding = ActivityAddPostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.saveNewPostBtn.setOnClickListener { uploadImage()}

        CropImage.activity()
            .setAspectRatio(1, 1)
            .setAspectRatio(4, 5)
            .start(this@AddPostActivity)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
            {
                val result = CropImage.getActivityResult(data)
                imageUri = result.uri
                binding.imagePost.setImageURI(imageUri)

            }else{
                finish()
            }
    }



    private fun uploadImage() {
        when {
            TextUtils.isEmpty(binding.descriptionPost.text.toString()) -> Toast.makeText(this, "Enter a caption...", Toast.LENGTH_LONG).show()
            imageUri == null -> Toast.makeText(this, "Please Select Picture", Toast.LENGTH_LONG).show()
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Adding new post")
                progressDialog.setMessage("Please wait, we are uploading your picture...")
                progressDialog.show()

                val fileref = storagePostPicRef!!.child(System.currentTimeMillis().toString() + "jpg")

                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

                // Resize the image here
                val resizedBitmap = resizeBitmap(bitmap, 800) // Adjust the size as needed

                val baos = ByteArrayOutputStream()
                resizedBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                val uploadTask = fileref.putBytes(data)

                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileref.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                        val postId = ref.push().key
                        val postMap = HashMap<String, Any>()
                        postMap["postid"] = postId!!
                        postMap["description"] = binding.descriptionPost.text.toString()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postimage"] = myUrl

                        ref.child(postId).updateChildren(postMap)

                        Toast.makeText(this, "Post uploaded successfully", Toast.LENGTH_LONG).show()
                        finish()
                        progressDialog.dismiss()
                    } else {
                        progressDialog.dismiss()
                    }
                })
            }
        }
    }

    // Function to resize a Bitmap
    private fun resizeBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height

        val bitmapRatio = width.toFloat() / height.toFloat()

        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(image, width, height, true)
    }


}