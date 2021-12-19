package com.example.jacksonstateuniversity.Student

class Faculty {

    var firstname: String? = null
    var lastname: String? = null
    var id: String? = null
    var roll: String? = null
    var department: String? = null
    var email: String? = null
    var uid: String? = null

    constructor() {}

    constructor(
        firstname: String?, lastname: String?, id: String?, roll: String?,
        department: String?, email: String?, uid: String?
    ) {
        this.firstname = firstname
        this.lastname = lastname
        this.id = id
        this.roll = roll
        this.department = department
        this.email = email
        this.uid = uid
    }
}