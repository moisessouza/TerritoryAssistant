package com.application.territoryassistant.bd.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CONFIGURACOES")
data class ConfiguracoesEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    val id: Int? = null,

    @ColumnInfo(name = "TEXTO_PADRAO_DIRIGENTE_TERRITORIO")
    val textoPadraoDirigenteTerritorio: String? = null,

    @ColumnInfo(name = "NUM_DIAS_ESPERA_TERRITORIO")
    val numDiasEsperaTerritorio: Int? = 15
)
