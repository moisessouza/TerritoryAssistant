package com.application.territoryassistant.bd.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ConfiguracoesDao {
    @Query("SELECT * FROM CONFIGURACOES WHERE ID = 1 LIMIT 1")
    fun getConfig(): ConfiguracoesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(configuracoes: ConfiguracoesEntity): Long

    @Query("UPDATE CONFIGURACOES SET TEXTO_PADRAO_DIRIGENTE_TERRITORIO = :texto WHERE ID = 1")
    fun updateTextoDirigente(texto: String): Int

    @Query("UPDATE CONFIGURACOES SET NUM_DIAS_ESPERA_TERRITORIO = :numDias WHERE ID = 1")
    fun updateNumDiasEspera(numDias: Int): Int
}
