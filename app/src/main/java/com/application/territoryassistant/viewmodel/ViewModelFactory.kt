package com.application.territoryassistant.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.application.territoryassistant.bd.room.AppDatabase
import com.application.territoryassistant.repository.GroupRepository
import com.application.territoryassistant.repository.LeaderRepository

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getInstance(context)
        return when {
            modelClass.isAssignableFrom(LeaderViewModel::class.java) -> {
                LeaderViewModel(LeaderRepository(db.dirigenteDao())) as T
            }
            modelClass.isAssignableFrom(GroupViewModel::class.java) -> {
                GroupViewModel(GroupRepository(db.grupoDao())) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
