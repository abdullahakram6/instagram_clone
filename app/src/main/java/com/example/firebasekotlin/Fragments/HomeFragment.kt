package com.example.firebasekotlin.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasekotlin.Adapter.PostAdapter
import com.example.firebasekotlin.Model.Post
import com.example.firebasekotlin.R
import com.example.firebasekotlin.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var binding: FragmentHomeBinding

class HomeFragment : Fragment() {

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        var recyclerView: RecyclerView? = null
        recyclerView = binding.recyclerViewHome
        val  linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it,postList as ArrayList<Post>)}
        recyclerView.adapter = postAdapter

        checkFollowings()

        return binding.root
    }

    private fun checkFollowings() {
        followingList=ArrayList()
            val followingRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists())
                {
                    (followingList as ArrayList<String>).clear()

                    for (snapshot in pO.children)
                    {
                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }

                    retrievePost()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun retrievePost() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(pO: DataSnapshot) {

                postList?.clear()

                for (snapshot in pO.children)
                {
                    val post = snapshot.getValue(Post::class.java)

                    for (id in (followingList as ArrayList<String>))
                    {
                        if (post!!.getPublisher() == id)
                        {
                            postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(pO: DatabaseError) {
            }

        })
    }
}