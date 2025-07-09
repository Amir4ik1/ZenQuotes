package ru.zenquotes.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Quote(
    @PrimaryKey()
    val id: Int?=null,
    val quote: String,
    val author: String,
    var liked: Boolean,
): java.io.Serializable