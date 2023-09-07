package com.example.firebasekotlin.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasekotlin.Adapter.UserAdapter
import com.example.firebasekotlin.Model.User
import com.example.firebasekotlin.R
import com.example.firebasekotlin.databinding.FragmentSearchBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding


    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root


        recyclerView = view.findViewById(R.id.recycler_view_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it, mUser as ArrayList<User>, true) }
        recyclerView?.adapter = userAdapter

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.searchEditText.text.toString() == "") {

                } else {
                    recyclerView?.visibility = View.VISIBLE
                     retrieveUsers()
                    searchUser(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }

        })
        return view
    }

    private fun searchUser(input: String) {

        val inputLowercase = input.toLowerCase()

        val query = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("fullNameLowercase")
            .startAt(inputLowercase)
            .endAt(inputLowercase+"\uf8ff")

        query.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                mUser?.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if(user?.getUID()!=   firebaseUser?.uid)
                    {
                        if(user != null)
                        {
                            mUser?.add(user)
                        }
                    }

                }
                userAdapter?.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError)
            {

            }
        })
        val queryByUsername = FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("userName")
            .startAt(input)
            .endAt(input + "\uf8ff")

        queryByUsername.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user?.getUID() != firebaseUser?.uid) {
                        if (user != null && !mUser?.contains(user)!!) {
                            mUser?.add(user)
                        }
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun retrieveUsers(){
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        usersRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot)
            {
                if (binding.searchEditText.text?.toString() == "") {
                    mUser?.clear()
                    for (snapshot in dataSnapshot.children) {
                        val user = snapshot.getValue(User::class.java)
                        if(user != null)
                        {
                            mUser?.add(user)
                        }
                    }
                    userAdapter?.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError)
            {

            }
        })
    }

}    