package com.application.territoryassistant.bd.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "ULTIMA_ACOES",
    foreignKeys = [
        ForeignKey(
            entity = DirigenteEntity::class,
            parentColumns = ["ID"],
            childColumns = ["ID_DIRIGENTE"]
        )
    ]
)
data class UltimaAcoesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    val id: Int? = null,

    @ColumnInfo(name = "COD_ACAO")
    val codAcao: String,

    @ColumnInfo(name = "COD_TERRITORIOS")
    val codTerritorios: String,

    @ColumnInfo(name = "ID_DIRIGENTE")
    val idDirigente: Int,

    @ColumnInfo(name = "DATA_INICIO")
    val dataInicio: Long? = null,

    @ColumnInfo(name = "DATA_FIM")
    val dataFim: Long? = null
)
