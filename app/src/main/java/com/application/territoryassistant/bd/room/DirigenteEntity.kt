package com.application.territoryassistant.bd.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "DIRIGENTES")
data class DirigenteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    val id: Int? = null,

    @ColumnInfo(name = "NOME")
    val nome: String,

    @ColumnInfo(name = "EMAIL")
    val email: String? = null
)
