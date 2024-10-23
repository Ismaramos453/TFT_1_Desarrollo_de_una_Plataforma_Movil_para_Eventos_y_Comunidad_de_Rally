package com.example.tft.ui.community

import androidx.lifecycle.ViewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tft.data.services.Question.QuestionServices
import com.example.tft.data.services.Votation.VotationServices
import com.example.tft.model.foro.Question
import com.example.tft.model.foro.Votation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class CommunityViewModel : ViewModel() {
    private val firestoreService = VotationServices
    private val _allQuestions = MutableLiveData<List<Question>>()
    private val _filteredQuestions = MediatorLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _filteredQuestions
    private val _items = MutableLiveData<List<Any>>()  // Esto incluirá Question y Votation
    val items: LiveData<List<Any>> = _items

    init {
        loadQuestions()
        loadItems()
        _filteredQuestions.addSource(_allQuestions) { filterQuestions("") }
    }

    private fun loadQuestions() {
        QuestionServices.getQuestions { questionsList ->
            _allQuestions.postValue(questionsList)
            _filteredQuestions.postValue(questionsList)  // Iniciar el filtro con todos los datos
        }
    }

    fun filterQuestions(query: String) {
        if (query.isEmpty()) {
            _filteredQuestions.value = _allQuestions.value
        } else {
            _filteredQuestions.value = _allQuestions.value?.filter {
                it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
            }
        }
    }

    private fun loadItems() {
        val allItems = mutableListOf<Any>()

        firestoreService.firestore.collection("questions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("CommunityViewModel", "Listen failed.", error)
                    return@addSnapshotListener
                }

                val questions = snapshot?.documents?.mapNotNull { it.toObject(Question::class.java) }
                if (questions != null) {
                    allItems.clear()
                    allItems.addAll(questions)
                    firestoreService.firestore.collection("votations")
                        .addSnapshotListener { votationSnapshot, votationError ->
                            if (votationError != null) {
                                Log.w("CommunityViewModel", "Listen failed.", votationError)
                                return@addSnapshotListener
                            }

                            val votations = votationSnapshot?.documents?.mapNotNull { it.toObject(Votation::class.java) }
                            if (votations != null) {
                                allItems.removeAll { it is Votation }
                                allItems.addAll(votations)
                                _items.postValue(allItems.toList())
                            }
                        }
                }
            }
    }


    fun voteOnVotation(votationId: String, option: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        VotationServices.voteOnVotation(votationId, option, userId) { success ->
            if (success) {
                loadItems()  // Recargar items para reflejar el cambio de votos
            }
        }
    }

    // Nueva función para recargar los ítems después de agregar una pregunta o votación
    fun refreshItems() {
        loadItems()
    }
}



