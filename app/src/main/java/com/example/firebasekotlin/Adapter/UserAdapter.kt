package com.example.firebasekotlin.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.firebasekotlin.fragments.ProfileFragment
import com.example.firebasekotlin.Model.User
import com.example.firebasekotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private var mContext: Context,
    private var mUser: List<User>,
    private var isFragment: Boolean = false
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNameTextView.text = user.getUsername()
        holder.userfullNameTextView.text = user.getFullname()
        holder.userProfileImage.load(user.getImage()) {
            placeholder(R.drawable.profile) // Optional: Placeholder image while loading
            crossfade(true) // Optional: Enable crossfade animation
            transformations(CircleCropTransformation()) // Optional: Apply a circular transformation to the image
        }
        checkFollowingStatus(user.getUID(), holder.followTv)

        holder.itemView.setOnClickListener(View.OnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
            pref?.putString("profileId", user.getUID())
            pref?.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
        })

        holder.followButton.setOnClickListener {
            if (holder.followTv.text.toString() == "Follow") {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUID())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    mContext,
                                                    "Followed Successfully",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                }
                            }
                        }
                }
            } else {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUID())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUID())
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    mContext,
                                                    "Unfollowed Successfully",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTextView: TextView = itemView.findViewById(R.id.user_name_search)
        var userfullNameTextView: TextView = itemView.findViewById(R.id.user_full_name_search)
        var followTv: TextView = itemView.findViewById(R.id.follow_tv_search)
        var userProfileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_search)
        var followButton: LinearLayout = itemView.findViewById(R.id.follow_btn_search)
    }


    private fun checkFollowingStatus(uid: String, followTv: TextView) {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }
        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (datasnapshot.child(uid).exists()) {
                    followTv.text = "Following"
                } else {
                    followTv.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
