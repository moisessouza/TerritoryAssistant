package com.application.territoryassistant.bd.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "TERRITORIO",
    foreignKeys = [
        ForeignKey(
            entity = GrupoEntity::class,
            parentColumns = ["ID"],
            childColumns = ["ID_GRUPO"]
        )
    ]
)
data class TerritorioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    val id: Int? = null,

    @ColumnInfo(name = "COD")
    val cod: String,

    @ColumnInfo(name = "ID_GRUPO")
    val idGrupo: Int,

    @ColumnInfo(name = "ULTIMA_DATA_FIM")
    val ultimaDataFim: Long? = null,

    @ColumnInfo(name = "SUSPENSO")
    val suspenso: Int? = 0,

    @ColumnInfo(name = "OBSERVACOES")
    val observacoes: String? = null,

    @ColumnInfo(name = "FOTO_PATH")
    val fotoPath: String? = null
)
