package com.example.firebasekotlin

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.firebasekotlin.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.tvSignup2.setOnClickListener {
            startActivity(Intent(this , LoginActivity::class.java))
        }
        binding.btnregisteracc.setOnClickListener {
            CreateAccount()
        }

    }

    private fun CreateAccount() {
        val fullName = binding.siptxtfrstname.text.toString()
        val userName = binding.siptxtscndname.text.toString()
        val email = binding.siptxtemail.text.toString()
        val password = binding.siptxtpaswrd.text.toString()

        when{
            TextUtils.isEmpty(fullName) -> Toast.makeText(this , "FullName is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this , "UserName is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this , "Email is required", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this , "Password is required", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("signup")
                progressDialog.setMessage("Please wait this may take a while...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                        task -> if(task.isSuccessful)
                {
                    saveUserInfo(fullName , userName , email , progressDialog)
                }
                else{
                    val message = task.exception!!.toString()
                    Toast.makeText(this , "Error: $message", Toast.LENGTH_LONG).show()
                    mAuth.signOut()
                    progressDialog.dismiss()
                }
                }
            }
        }

    }

    private fun saveUserInfo(fullName: String, userName: String, email: String ,progressDialog:ProgressDialog)
    {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any >()
        userMap["uid"]= currentUserID
        userMap["fullName"]= fullName
        userMap["userName"]= userName.lowercase()
        userMap["email"]= email
        userMap["bio"]= ""
        userMap["image"]= "https://firebasestorage.googleapis.com/v0/b/meta-6c6cb.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=2227e475-c948-402f-8793-262e3b594b32"
        userMap["fullNameLowercase"] = fullName.lowercase()

        usersRef.child(currentUserID).setValue(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful)
            {
                progressDialog.dismiss()
                Toast.makeText(this , "Account has been created successfully", Toast.LENGTH_LONG).show()


                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(currentUserID)
                        .child("Following").child(currentUserID)
                        .setValue(true)


                val intent = Intent(this@SignUpActivity, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or  Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
            else{
                val message = task.exception!!.toString()
                Toast.makeText(this , "Error: $message", Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut()
                progressDialog.dismiss()
            }
        }

    }
}