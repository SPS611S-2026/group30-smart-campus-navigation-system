package com.example.finalsps.InAppNav



sealed class Screen(val route: String) {
    object Map : Screen("map")
    object Search : Screen("search")
    object Bookmark : Screen("bookmark")
    object Nav : Screen("nav")
}
