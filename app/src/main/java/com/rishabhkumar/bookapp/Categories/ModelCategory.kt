package com.rishabhkumar.bookapp.Categories

class ModelCategory {

    //variable must be same as firebase
    var id: String = ""
    var category:String = ""
    var timestamp :Long = 0L
    var uid:String = ""

    //empty constructor required by firebase
    constructor()


    //parametrized constructor
    constructor(id: String, category: String, timestamp: Long, uid: String) {
        this.id = id
        this.category = category
        this.timestamp = timestamp
        this.uid = uid
    }


}
