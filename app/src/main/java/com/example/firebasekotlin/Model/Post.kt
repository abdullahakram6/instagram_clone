package com.example.firebasekotlin.Model

class Post {

    private var postid: String = ""
    private var postimage: String = ""
    private var publisher: String = ""
    private var description: String = ""
    private var likes: Int = 0
     var isLiked: Boolean = false
    private var timestamp: Long = 0 // Add timestamp property



    constructor()


    constructor(postid: String, postimage: String, publisher: String, description: String) {
        this.postid = postid
        this.postimage = postimage
        this.publisher = publisher
        this.description = description
        this.timestamp = timestamp // Initialize timestamp

    }


    ///Getter


    fun getPostid(): String{
        return postid
    }
    fun getPostimage(): String{
        return postimage
    }
    fun getPublisher(): String{
        return publisher
    }
    fun getDescription(): String{
        return description
    }

    fun getLikes(): Int {
        return likes
    }
    fun getTimestamp(): Long {
        return timestamp
    }


    ///Setter

    fun setpostid(postid: String)
    {
        this.postid=postid
    }
    fun setPostimage(postimage: String)
    {
        this.postimage=postimage
    }
    fun setPublisher(publisher: String)
    {
        this.publisher=publisher
    }
    fun setDescription(description: String)
    {
        this.description=description
    }

    fun setLikes(likes: Int)
    {
        this.likes = likes
    }

    fun setTimestamp(timestamp: Long) {
        this.timestamp = timestamp
    }
}