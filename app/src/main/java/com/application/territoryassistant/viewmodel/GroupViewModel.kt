package com.application.territoryassistant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.territoryassistant.bd.room.GrupoEntity
import com.application.territoryassistant.repository.GroupRepository
import kotlinx.coroutines.launch

sealed class GroupUiState {
    object Loading : GroupUiState()
    data class Success(val groups: List<GrupoEntity>) : GroupUiState()
    data class Error(val message: String) : GroupUiState()
}

class GroupViewModel(private val repository: GroupRepository) : ViewModel() {

    private val _uiState = MutableLiveData<GroupUiState>(GroupUiState.Loading)
    val uiState: LiveData<GroupUiState> = _uiState

    fun loadGroups() {
        viewModelScope.launch {
            try {
                val groups = repository.getAllGroups()
                _uiState.value = GroupUiState.Success(groups)
            } catch (e: Exception) {
                _uiState.value = GroupUiState.Error(e.message ?: "Erro ao carregar grupos")
            }
        }
    }

    fun addGroup(nome: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.insertGroup(nome)
                loadGroups()
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun updateGroup(id: Int, nome: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateGroup(GrupoEntity(id, nome))
                loadGroups()
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun deleteGroup(id: Int, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                if (repository.hasTerritories(id)) {
                    onResult(false, "Não é possível excluir um grupo que possui territórios cadastrados.")
                } else {
                    repository.deleteGroup(id)
                    loadGroups()
                    onResult(true, null)
                }
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }
}
