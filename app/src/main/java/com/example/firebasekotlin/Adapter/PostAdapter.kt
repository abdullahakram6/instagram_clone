package com.example.firebasekotlin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.firebasekotlin.fragments.HomeFragment
import com.example.firebasekotlin.Model.Post
import com.example.firebasekotlin.Model.User
import com.example.firebasekotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(
    private val mContext: Context,
    private val mPost: List<Post>,
    private var likeClickListener: OnLikeClickListener
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser? = null

    fun updateLikeButtonState(position: Int, isLiked: Boolean) {
        val post = mPost[position]
        post.setLikes(if (isLiked) post.getLikes() - 1 else post.getLikes() + 1)
        notifyItemChanged(position, "like")
    }

    interface OnLikeClickListener {
        fun onLikeClick(position: Int)
    }

    fun setOnLikeClickListener(listener: HomeFragment) {
        likeClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder(view)
    }

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        // Return a unique ID for each item (e.g., post ID)
        return mPost[position].getPostid().hashCode().toLong()
    }


    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]
        val isLiked = post.getLikes() > 0

        holder.description.text = post.getDescription()

        holder.postImage.load(post.getPostimage()) {
        }

        publisherInfo(
            holder.profileImage,
            holder.userName,
            holder.publisher,
            holder.description,
            post.getPublisher()
        )

        if (isLiked) {
            holder.likeButton.setImageResource(R.drawable.heartfill)
        } else {
            holder.likeButton.setImageResource(R.drawable.heart)
        }

        holder.likeButton.setOnClickListener {
            likeClickListener.onLikeClick(position)

            post.isLiked = !post.isLiked

            if (post.isLiked) {
                holder.likeButton.setImageResource(R.drawable.heartfill)
            } else {
                holder.likeButton.setImageResource(R.drawable.heart)
            }
        }

        val likesCount = post.getLikes()
        val likesCountText = if (likesCount == 0) {
            "No Likes"
        } else if (likesCount == 1) {
            "1 Like"
        } else {
            "$likesCount Likes"
        }
        holder.likes_count.text = likesCountText
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView
        var postImage: ImageView
        var likeButton: ImageView
        var userName: TextView
        var likes_count: TextView
        var publisher: TextView
        var description: TextView

        init {
            profileImage = itemView.findViewById(R.id.user_profile_image_post)
            postImage = itemView.findViewById(R.id.post_image_home)
            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            userName = itemView.findViewById(R.id.user_name_search)
            likes_count = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)
        }
    }

    private fun publisherInfo(
        profileImage: CircleImageView,
        userName: TextView,
        publisher: TextView,
        description: TextView,
        publisherID: String
    ) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)


                    // Load and display the profile image using Coil
                    profileImage.load(user!!.getImage()) {
                        transformations(CircleCropTransformation()) // Apply a circular transformation to the image
                    }
                    userName.text = user.getUsername()
                    publisher.text = user.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
