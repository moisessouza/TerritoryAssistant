package com.application.territoryassistant.bd.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "DESIGNACAO",
    foreignKeys = [
        ForeignKey(
            entity = DirigenteEntity::class,
            parentColumns = ["ID"],
            childColumns = ["ID_DIRIGENTE"]
        ),
        ForeignKey(
            entity = TerritorioEntity::class,
            parentColumns = ["ID"],
            childColumns = ["ID_TERRITORIO"]
        )
    ]
)
data class DesignacaoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    val id: Int? = null,

    @ColumnInfo(name = "ID_TERRITORIO")
    val idTerritorio: Int,

    @ColumnInfo(name = "ID_DIRIGENTE")
    val idDirigente: Int,

    @ColumnInfo(name = "TIPO")
    val tipo: String,

    @ColumnInfo(name = "DATA_INICIO")
    val dataInicio: Long,

    @ColumnInfo(name = "DATA_FIM")
    val dataFim: Long? = null,

    @ColumnInfo(name = "MARCADO")
    val marcado: Int? = 0
)
