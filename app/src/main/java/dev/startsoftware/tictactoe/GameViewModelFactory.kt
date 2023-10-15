package dev.startsoftware.tictactoe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameViewModelFactory(private val boardSize: Int): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(boardSize) as T
        }

        throw IllegalAccessException("ViewModel class not found!")
    }
}