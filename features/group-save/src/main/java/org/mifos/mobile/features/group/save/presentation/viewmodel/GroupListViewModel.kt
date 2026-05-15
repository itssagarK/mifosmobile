package org.mifos.mobile.features.group.save.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.mifos.mobile.core.common.UiState
import org.mifos.mobile.features.group.save.domain.model.Group
import org.mifos.mobile.features.group.save.domain.repository.GroupRepository
import javax.inject.Inject

@HiltViewModel
class GroupListViewModel @Inject constructor(
    private val repository: GroupRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _centerFilter = MutableStateFlow<String?>(null)
    val centerFilter: StateFlow<String?> = _centerFilter.asStateFlow()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val groupsState: StateFlow<UiState<List<Group>>> = combine(
        _searchQuery,
        _centerFilter,
        repository.getGroups()
    ) { query, center, groups ->
        var filtered = groups
        if (query.isNotEmpty()) {
            filtered = filtered.filter { 
                it.name.contains(query, ignoreCase = true) || 
                it.centerName.contains(query, ignoreCase = true)
            }
        }
        if (center != null) {
            filtered = filtered.filter { it.centerName == center }
        }
        
        if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCenterFilterChanged(center: String?) {
        _centerFilter.value = center
    }

    fun refreshGroups() {
        viewModelScope.launch {
            repository.syncGroups()
        }
    }
}
