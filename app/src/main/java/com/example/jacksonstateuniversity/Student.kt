package com.example.jacksonstateuniversity


class Student {

    var firstname: String? = null
    var lastname: String? = null
    var id: String? = null
    var roll: String? = null
    var year: String? = null
    var email: String? = null
    var uid: String? = null

    constructor() {}

    constructor(
        firstname: String?, lastname: String?, id: String?, roll: String?,
        year: String?, email: String?, uid: String?
    ) {
        this.firstname = firstname
        this.lastname = lastname
        this.id = id
        this.roll = roll
        this.year = year
        this.email = email
        this.uid = uid
    }
}