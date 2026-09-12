package com.application.territoryassistant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.territoryassistant.bd.room.DirigenteEntity
import com.application.territoryassistant.repository.LeaderRepository
import kotlinx.coroutines.launch

sealed class LeaderUiState {
    object Loading : LeaderUiState()
    data class Success(val leaders: List<DirigenteEntity>) : LeaderUiState()
    data class Error(val message: String) : LeaderUiState()
}

class LeaderViewModel(private val repository: LeaderRepository) : ViewModel() {

    private val _uiState = MutableLiveData<LeaderUiState>(LeaderUiState.Loading)
    val uiState: LiveData<LeaderUiState> = _uiState

    fun loadLeaders() {
        viewModelScope.launch {
            try {
                val leaders = repository.getAllLeaders()
                _uiState.value = LeaderUiState.Success(leaders)
            } catch (e: Exception) {
                _uiState.value = LeaderUiState.Error(e.message ?: "Erro ao carregar dirigentes")
            }
        }
    }

    fun addLeader(nome: String, email: String?, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.insertLeader(nome, email)
                loadLeaders()
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun updateLeader(id: Int, nome: String, email: String?, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateLeader(DirigenteEntity(id, nome, email))
                loadLeaders()
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun deleteLeader(id: Int, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteLeader(id)
                loadLeaders()
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}
