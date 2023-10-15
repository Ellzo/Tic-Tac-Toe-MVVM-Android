package dev.startsoftware.tictactoe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



class GameViewModel(private val size: Int): ViewModel() {
    private val _liveGameState = MutableLiveData(Board(size))
    private val _liveWinState = MutableLiveData(WinState.ONGOING)
    private var filledCells = 0
    private var turn = Cell.X
    val liveGameState: LiveData<Board>
        get() = _liveGameState

    val liveWinState: LiveData<WinState>
        get() = _liveWinState

    fun makeMove(position: Int): Boolean{
        if(_liveWinState.value != WinState.ONGOING)
            return false

        val x = position % size
        val y = position / size

        Log.d("DEBUGGING GG", "makeMove: x = $x, y = $y")
        Log.d("DEBUGGING GG", "makeMove: cell = ${_liveGameState.value!!.boardState[x][y]}")

        if(_liveGameState.value!!.boardState[x][y] == Cell.EMPTY) {
            _liveGameState.value!!.boardState[x][y] = turn
            Log.d("DEBUGGING GG", "makeMove: turn = $turn")
            updateStatus(x, y)
            return true
        }

        return false
    }

    private fun updateStatus(x: Int, y: Int){
        filledCells += 1
        nextTurn()

        if(isWin(x, y))
            _liveWinState.value = getWinner(x, y)
        else if(isTie())
            _liveWinState.value = WinState.TIE
    }

    private fun nextTurn(){
        turn = if(turn == Cell.X) Cell.O else Cell.X
    }

    private fun isWin(x: Int, y: Int): Boolean{
        return isHorizontalWin(x, y) || isVerticalWin(x, y) || isDiagonalWin(x, y)
    }

    private fun isHorizontalWin(x: Int, y: Int): Boolean{
        for(i in 0 until size) {
            if (_liveGameState.value!!.boardState[i][y] != _liveGameState.value!!.boardState[x][y])
                return false
        }

        return true
    }

    private fun isVerticalWin(x: Int, y: Int): Boolean{
        for(j in 0 until size) {
            if (_liveGameState.value!!.boardState[x][j] != _liveGameState.value!!.boardState[x][y])
                return false
        }

        return true
    }

    private fun isDiagonalWin(x: Int, y: Int): Boolean{
        if(x != y || x + y != size)
            return false

        var isWin = true
        for(ij in 0 until size) {
            if (_liveGameState.value!!.boardState[ij][ij] != _liveGameState.value!!.boardState[x][y]) {
                isWin = false
                break
            }
        }

        if(isWin)
            return true

        for(ij in 0 until size) {
            if (_liveGameState.value!!.boardState[ij][size - ij] != _liveGameState.value!!.boardState[x][y])
                return false
        }

        return true
    }

    private fun getWinner(x: Int, y: Int): WinState {
        return if(_liveGameState.value!!.boardState[x][y] == Cell.X) WinState.PLAYER1 else WinState.PLAYER2
    }

    private fun isTie() = filledCells == size * size
}