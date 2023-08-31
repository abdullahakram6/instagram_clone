package com.example.firebasekotlin.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.firebasekotlin.AccountSettingsActivity
import com.example.firebasekotlin.Model.User
import com.example.firebasekotlin.R
import com.example.firebasekotlin.databinding.FragmentProfileBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding


    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser


    lateinit var app_bar_layout_profile_frag: AppBarLayout
    lateinit var scroll_view: ScrollView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root



        app_bar_layout_profile_frag = view.findViewById(R.id.app_bar_layout_profile_frag)
        scroll_view = view.findViewById(R.id.scroll_view)

        app_bar_layout_profile_frag.visibility = View.GONE
        scroll_view.visibility = View.GONE


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none").toString()
        }

        if (profileId == firebaseUser.uid) {

            binding.editAccountSettingsTV.text = "Edit Profile"
        } else if (profileId != firebaseUser.uid) {
            checkFollowAndFollowingButtonStatus()
        }

        binding.editAccountSettingsTV.setOnClickListener {
            val getButtonText = binding.editAccountSettingsTV.text.toString()

            when {
                getButtonText == "Edit Profile" -> startActivity(
                    Intent(
                        context,
                        AccountSettingsActivity::class.java
                    )
                )

                getButtonText == "Follow" -> {
                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .setValue(true)
                    }
                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .setValue(true)
                    }

                }

                getButtonText == "Following" -> {

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .removeValue()
                    }
                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .removeValue()
                    }

                }
            }

        }

        getFollowers()
        getFollowings()
        userInfo()
        return view

    }

    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef = firebaseUser.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }
        if (followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child(profileId).exists()) {


                        binding.editAccountSettingsTV.text = "Following"
                    } else {


                        binding.editAccountSettingsTV.text = "Follow"

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        }
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    binding.totalFollowers.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun getFollowings() {

        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    binding.totalFollowing.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    Log.d("user image", user!!.getImage())

                    binding.proImageProfileFrag.load(user.getImage()) {
                        placeholder(R.drawable.profile) // Optional: Placeholder image while loading
                        transformations(CircleCropTransformation()) // Optional: Apply a circular transformation to the image
                    }



                    binding.profileFragmentUsername.text = user.getUsername()
                    binding.fullNameProfileFrag.text = user.getFullname()
                    binding.bioProfileFrag.text = user.getBio()


                    app_bar_layout_profile_frag.visibility = View.VISIBLE
                    scroll_view.visibility = View.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
}