package com.shinjaehun.jetpacknotesmvvmkotlinv2.model

data class FirebaseNote(
    val creationDate: String? = "",
    val contents: String? = "",
    val upVotes: Int? = 0,
    val imageUrl: String?  = "",
    val creator: String? = ""
)
