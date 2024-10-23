package com.example.tft.ui.teamWrc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tft.data.services.Teams.TeamsServices
import com.example.tft.model.wrc.TeamWrc

class TeamWrcViewModel : ViewModel() {
    private val _teams = MutableLiveData<List<TeamWrc>>()
    val teams: LiveData<List<TeamWrc>> = _teams

    init {
        loadTeams()
    }

    private fun loadTeams() {
        TeamsServices.getTeams { teams ->
            _teams.postValue(teams)
        }
    }
}
