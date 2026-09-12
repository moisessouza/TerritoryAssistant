package com.application.territoryassistant.bd.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "TERRITORIO_VIZINHO",
    foreignKeys = [
        ForeignKey(
            entity = TerritorioEntity::class,
            parentColumns = ["ID"],
            childColumns = ["ID_TERRITORIO"]
        ),
        ForeignKey(
            entity = TerritorioEntity::class,
            parentColumns = ["ID"],
            childColumns = ["ID_VIZINHO"]
        )
    ]
)
data class TerritorioVizinhoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    val id: Int? = null,

    @ColumnInfo(name = "ID_TERRITORIO")
    val idTerritorio: Int,

    @ColumnInfo(name = "ID_VIZINHO")
    val idVizinho: Int
)
