package dev.startsoftware.tictactoe.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.startsoftware.tictactoe.models.WinState
import dev.startsoftware.tictactoe.models.Board
import dev.startsoftware.tictactoe.models.Cell


class GameViewModel(private val size: Int): ViewModel() {
    private val _liveGameState = MutableLiveData(Board(size))
    private val _liveWinState = MutableLiveData(WinState.ONGOING)
    private var _liveTurn = MutableLiveData(Cell.X)
    private var filledCells = 0
    val liveGameState: LiveData<Board>
        get() = _liveGameState

    val liveWinState: LiveData<WinState>
        get() = _liveWinState

    val liveTurn: LiveData<Cell>
        get() = _liveTurn

    fun makeMove(position: Int): Boolean{
        if(_liveWinState.value != WinState.ONGOING)
            return false

        val x = position / size
        val y = position % size

        if(_liveGameState.value!!.cells[x][y] == Cell.EMPTY) {
            val bs = _liveGameState.value
            bs!!.cells[x][y] = _liveTurn.value!!
            _liveGameState.value = bs
            updateGameStatus(x, y)
            return true
        }

        return false
    }

    private fun updateGameStatus(x: Int, y: Int){
        filledCells += 1
        _liveTurn.value = getNextTurn()

        if(isWin(x, y))
            _liveWinState.value = getWinner(x, y)
        else if(isTie())
            _liveWinState.value = WinState.TIE
    }

    private fun getNextTurn() = if(_liveTurn.value == Cell.X) Cell.O else Cell.X

    private fun getWinner(x: Int, y: Int) = if(_liveGameState.value!!.cells[x][y] == Cell.X) WinState.PLAYER1 else WinState.PLAYER2

    private fun isTie() = filledCells == size * size

    private fun isWin(x: Int, y: Int): Boolean{
        return isHorizontalWin(x, y) || isVerticalWin(x, y) || isDiagonalWin(x, y)
    }

    private fun isHorizontalWin(x: Int, y: Int): Boolean{
        for(i in 0 until size) {
            if (_liveGameState.value!!.cells[i][y] != _liveGameState.value!!.cells[x][y])
                return false
        }

        return true
    }

    private fun isVerticalWin(x: Int, y: Int): Boolean{
        for(j in 0 until size) {
            if (_liveGameState.value!!.cells[x][j] != _liveGameState.value!!.cells[x][y])
                return false
        }

        return true
    }

    private fun isDiagonalWin(x: Int, y: Int): Boolean{
        if(x != y && x + y != size - 1)
            return false

        var isWin = true
        for(ij in 0 until size) {
            if (_liveGameState.value!!.cells[ij][ij] != _liveGameState.value!!.cells[x][y]) {
                isWin = false
                break
            }
        }

        if(isWin)
            return true

        for(ij in 0 until size) {
            if (_liveGameState.value!!.cells[ij][size - ij - 1] != _liveGameState.value!!.cells[x][y])
                return false
        }

        return true
    }
}