package com.example.firebasekotlin.Model

class User
{
    private var userName: String = ""
    private var fullName: String = ""
    private var bio: String = ""
    private var uid: String = ""
    private var image: String = ""
    private var description: String = ""


    constructor()

    constructor(userName: String,fullName:String, bio:String, uid:String,image:String,description:String)
    {
        this.userName=userName
        this.fullName=fullName
        this.bio=bio
        this.uid=uid
        this.image=image
        this.description=description
    }

    fun getUsername(): String
    {
        return userName
    }
    fun setUsername(userName: String)
    {
        this.userName=userName
    }

    fun getFullname(): String
    {
        return fullName
    }
    fun setFullname(fullName: String)
    {
        this.fullName=fullName
    }
    fun getBio(): String
    {
        return bio
    }
    fun setBio(bio: String)
    {
        this.bio=bio
    }
    fun getImage(): String
    {
        return image
    }
    fun setImage(image: String)
    {
        this.image=image
    }
    fun getUID(): String
    {
        return uid
    }
    fun setUID(uid: String)
    {
        this.uid=uid
    }
}