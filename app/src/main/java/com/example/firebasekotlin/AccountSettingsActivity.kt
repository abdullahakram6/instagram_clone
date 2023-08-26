package com.example.firebasekotlin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.firebasekotlin.Model.User
import com.example.firebasekotlin.databinding.ActivityAccountSettingsBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingsBinding
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri : Uri? = null
    private var storageProfilePicRef: StorageReference?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")


        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@AccountSettingsActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or  Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        binding.changeImageTextBtn.setOnClickListener {
            checker="clicked"
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@AccountSettingsActivity)
        }

        binding.saveInfoProfileBtn.setOnClickListener {
            if(checker == "clicked")
            {
                uploadImageAndUpdateInfo()
            }
            else
            {
                updateUserInfoOnly()
            }
        }
        userInfo()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
    {
        val result = CropImage.getActivityResult(data)
        imageUri = result.uri
        binding.profileImageView.setImageURI(imageUri)

    }
    }

    private fun updateUserInfoOnly() {

        if(binding.fullNameProfileFrag.text.toString() == "")
        {
            Toast.makeText(this , "Please Enter FullName", Toast.LENGTH_LONG).show()
        }
        else if(binding.usernameProfileFrag.text.toString() == "")
        {
            Toast.makeText(this , "Please Enter userName", Toast.LENGTH_LONG).show()
        }
        else if(binding.bioProfileFrag.text.toString() == "")
        {
            Toast.makeText(this , "Please Enter bio", Toast.LENGTH_LONG).show()
        }
        else
        {
            val usersRef = FirebaseDatabase.getInstance().reference.child("Users")

            val userMap = HashMap<String, Any >()
            userMap["fullName"]= binding.fullNameProfileFrag.text.toString().lowercase()
            userMap["userName"]= binding.usernameProfileFrag.text.toString().lowercase()
            userMap["bio"]= binding.bioProfileFrag.text.toString()

            usersRef.child(firebaseUser.uid).updateChildren(userMap)
                .addOnSuccessListener {

                    Toast.makeText(this , "Account information has been updated successfully", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this , "Failed to update account information: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }


    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    Log.d("user image", user!!.getImage())

                    Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(binding.profileImageView)

                    binding.usernameProfileFrag.setText(user.getUsername())
                    binding.fullNameProfileFrag.setText(user.getFullname())
                    binding.bioProfileFrag.setText( user.getBio())

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private fun uploadImageAndUpdateInfo()
    {


        when
        {
            binding.fullNameProfileFrag.text.toString() == "" -> Toast.makeText(this , "Please Enter FullName", Toast.LENGTH_LONG).show()
            binding.usernameProfileFrag.text.toString() == "" -> Toast.makeText(this , "Please Enter userName", Toast.LENGTH_LONG).show()
            binding.bioProfileFrag.text.toString() == "" -> Toast.makeText(this , "Please Enter bio", Toast.LENGTH_LONG).show()
            imageUri == null -> Toast.makeText(this , "Please Set Profile Picture", Toast.LENGTH_LONG).show()

            else ->
            {

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Account Settings")
                progressDialog.setMessage("Please wait, we are updating your profile...")
                progressDialog.show()

                val fileref = storageProfilePicRef!!.child(firebaseUser!!.uid + "jpg")
                var uploadTask : StorageTask<*>
                uploadTask = fileref.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful)
                    {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileref.downloadUrl
                }).addOnCompleteListener ( OnCompleteListener<Uri>{task ->
                    if (task.isSuccessful)
                    {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any >()
                        userMap["fullName"]= binding.fullNameProfileFrag.text.toString().lowercase()
                        userMap["userName"]= binding.usernameProfileFrag.text.toString().lowercase()
                        userMap["bio"]= binding.bioProfileFrag.text.toString()
                        userMap["image"]= myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)


                        Toast.makeText(this , "Profile Picture Set ", Toast.LENGTH_LONG).show()
                        finish()
                        progressDialog.dismiss()
                    }
                    else{
                        progressDialog.dismiss()
                    }
                } )

            }
        }
    }
}
