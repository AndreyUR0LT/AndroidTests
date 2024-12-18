package com.higtek.testBle

import androidx.compose.runtime.mutableStateListOf

data class MainDataClass (var isAuth: Boolean = false) {

    var tags = mutableStateListOf<String>("EMPTY")
    var listOfUserNames = mutableStateListOf<String>("-")
    //var listOfUserNames = mutableStateListOf<String>("One", "Two", "Four", "Four1", "Four2")
    var userName : String = ""

    var listOfMhhtUsers = mutableStateListOf<MHHTUser>()
}