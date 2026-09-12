package com.application.territoryassistant.bd.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DirigenteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dirigente: DirigenteEntity): Long

    @Query("SELECT * FROM DIRIGENTES ORDER BY NOME ASC")
    fun getAll(): List<DirigenteEntity>

    @Query("SELECT * FROM DIRIGENTES WHERE ID = :id LIMIT 1")
    fun getById(id: Int): DirigenteEntity?

    @Query("SELECT * FROM DIRIGENTES WHERE ID IN (:ids) ORDER BY NOME ASC")
    fun getByIds(ids: List<Int>): List<DirigenteEntity>

    @Update
    fun update(dirigente: DirigenteEntity): Int

    @Query("DELETE FROM DIRIGENTES WHERE ID = :id")
    fun delete(id: Int): Int

    @Query("SELECT COUNT(ID) FROM DIRIGENTES")
    fun count(): Long
}
