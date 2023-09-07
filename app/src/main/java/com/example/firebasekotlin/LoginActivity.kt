package com.example.firebasekotlin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.firebasekotlin.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : BaseActivityWithoutVM<ActivityLoginBinding>() {
    lateinit var firebaseAuth: FirebaseAuth

    companion object {
        private const val RC_SIGN_IN = 9001
    }

            override fun getViewBinding(): ActivityLoginBinding =
        ActivityLoginBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)

        init()
        setListener()
    }

    private fun init() {
        firebaseAuth = FirebaseAuth.getInstance()
        val firebase : DatabaseReference= FirebaseDatabase.getInstance().reference
        val currentUser = firebaseAuth.currentUser
    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("AIzaSyDWLfEFtK5s75aSTw-H0wFnQJiL6BD5SJw")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setListener() {
        mViewBinding.apply {
            tvSignup.setOnClickListener {
                navigateToNextActivity(SignUpActivity::class.java)
            }
            btnLogin.setOnClickListener {
                navigateToNextActivity(HomeActivity::class.java)
            }
            btnLogin.setOnClickListener {
                val email = lgnEmail.text.toString()
                val password = passmain.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty())
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                navigateAndClearBackStack(HomeActivity::class.java)
                            } else {
                                showToast("Account not Registered")
                            }
                        } else {
                    showToast("Empty fields are not allowed")
                }
            }
        signInButton.setOnClickListener {
            signIn()
        }}
    }

    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser != null){
            navigateAndClearBackStack(HomeActivity::class.java)
        }

    }

}






