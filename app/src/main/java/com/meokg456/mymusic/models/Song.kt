package com.meokg456.mymusic.models

import java.io.Serializable

class Song(var title: String, var authors: List<String>, var avatarUrl: String) : Serializable{
}