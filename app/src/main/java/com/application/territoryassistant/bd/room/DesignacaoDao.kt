package com.application.territoryassistant.bd.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DesignacaoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(designacao: DesignacaoEntity): Long

    @Query("SELECT * FROM DESIGNACAO WHERE ID = :id LIMIT 1")
    fun getById(id: Int): DesignacaoEntity?

    @Query("SELECT * FROM DESIGNACAO WHERE DATA_FIM IS NULL")
    fun getEmAberto(): List<DesignacaoEntity>

    @Query("SELECT ID FROM DESIGNACAO WHERE DATA_FIM IS NULL AND ID_TERRITORIO = :idTerritorio LIMIT 1")
    fun getAbertoByTerritorio(idTerritorio: Int): Int?

    @Query("SELECT COUNT(ID_DIRIGENTE) FROM DESIGNACAO WHERE ID_DIRIGENTE = :idDirigente")
    fun countByDirigente(idDirigente: Int): Long

    @Query("SELECT COUNT(ID_TERRITORIO) FROM DESIGNACAO WHERE ID_TERRITORIO = :idTerritorio")
    fun countByTerritorio(idTerritorio: Int): Long

    @Update
    fun update(designacao: DesignacaoEntity): Int

    @Query("DELETE FROM DESIGNACAO WHERE ID = :id")
    fun delete(id: Int): Int

    @Query("UPDATE DESIGNACAO SET MARCADO = :marcado WHERE ID = :id")
    fun updateMarcado(id: Int, marcado: Int): Int
}
