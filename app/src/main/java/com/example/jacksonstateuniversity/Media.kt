package com.example.jacksonstateuniversity




class Media {
    var messageId: String? = null
    var senderId: String? = null
    var message: String? = null
    var mediaUrlList: ArrayList<String>? = null

    constructor() {}

    constructor(messageId: String, senderId: String, message: String, mediaUrlList: ArrayList<String> ){
        this.messageId = messageId
        this.senderId = senderId
        this.message = message
        this.mediaUrlList = mediaUrlList

    }

}