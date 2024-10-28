package com.higtek.testBle

import androidx.compose.runtime.mutableStateListOf

data class MainDataClass (var isAuth: Boolean = false) {

    var tags = mutableStateListOf<String>("EMPTY")
}