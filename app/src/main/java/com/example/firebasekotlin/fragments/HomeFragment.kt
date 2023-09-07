package com.example.firebasekotlin.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasekotlin.Adapter.PostAdapter
import com.example.firebasekotlin.Model.Post
import com.example.firebasekotlin.Model.User
import com.example.firebasekotlin.databinding.FragmentHomeBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var binding: FragmentHomeBinding

class HomeFragment : Fragment(), PostAdapter.OnLikeClickListener {

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null
    private lateinit var shimmerView: ShimmerFrameLayout
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        shimmerView = binding.shimmerViewContainer
        recyclerView = binding.recyclerViewHome

        retrievePost()
        checkFollowings()


        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>, this) }
        postAdapter?.setOnLikeClickListener(this) // Set the like click listener
        binding.recyclerViewHome.adapter = postAdapter


        val recyclerView: RecyclerView = binding.recyclerViewHome
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager



        return binding.root
    }

    private fun checkFollowings() {
        followingList = ArrayList()
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()) {
                    (followingList as ArrayList<String>).clear()

                    for (snapshot in pO.children) {
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
        postRef.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tempList = ArrayList<Post>()

                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)

                    for (id in (followingList as ArrayList<String>)) {
                        if (post!!.getPublisher() == id) {
                            tempList.add(post)
                        }
                    }
                }

                tempList.sortByDescending { it.getTimestamp() }

                postList?.clear()
                postList?.addAll(tempList)

                postAdapter?.notifyDataSetChanged()

                shimmerView.stopShimmer()
                shimmerView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }


    override fun onLikeClick(position: Int) {
        val post = postList?.get(position)

        if (post != null) {
            val isLiked = post.getLikes() > 0

            if (isLiked) {
                post.setLikes(post.getLikes() - 1)
            } else {
                post.setLikes(post.getLikes() + 1)
            }
            updateLikeCount(post)

            postAdapter?.updateLikeButtonState(position, !isLiked)
        }

    }


    private fun updateLikeCount(post: Post) {
        val postRef =
            FirebaseDatabase.getInstance().reference.child("Posts").child(post.getPostid())
        postRef.child("likes").setValue(post.getLikes())
    }

}