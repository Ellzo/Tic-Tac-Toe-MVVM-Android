package dev.startsoftware.tictactoe.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.startsoftware.tictactoe.models.GameState
import dev.startsoftware.tictactoe.models.Board
import dev.startsoftware.tictactoe.models.Cell


class GameViewModel(private val size: Int): ViewModel() {
    private val _liveBoardState = MutableLiveData(Board(size))
    private val _liveGameState = MutableLiveData(GameState.ONGOING)
    private var _liveTurn = MutableLiveData(Cell.X)
    private var filledCells = 0
    val liveBoardState: LiveData<Board>
        get() = _liveBoardState

    val liveGameState: LiveData<GameState>
        get() = _liveGameState

    val liveTurn: LiveData<Cell>
        get() = _liveTurn

    fun restart(){
        _liveBoardState.value = Board(size)
        _liveGameState.value = GameState.ONGOING
        _liveTurn.value = Cell.X
        filledCells = 0
    }

    fun makeMove(position: Int): Boolean{
        if(_liveGameState.value != GameState.ONGOING)
            return false

        val x = position / size
        val y = position % size

        if(_liveBoardState.value!!.cells[x][y] == Cell.EMPTY) {
            val bs = _liveBoardState.value
            bs!!.cells[x][y] = _liveTurn.value!!
            _liveBoardState.value = bs
            updateGameStatus(x, y)
            return true
        }

        return false
    }

    private fun updateGameStatus(x: Int, y: Int){
        filledCells += 1
        _liveTurn.value = getNextTurn()

        if(isWin(x, y))
            _liveGameState.value = getWinner(x, y)
        else if(isTie())
            _liveGameState.value = GameState.TIE
    }

    private fun getNextTurn() = if(_liveTurn.value == Cell.X) Cell.O else Cell.X

    private fun getWinner(x: Int, y: Int) = if(_liveBoardState.value!!.cells[x][y] == Cell.X) GameState.PLAYER1 else GameState.PLAYER2

    private fun isTie() = filledCells == size * size

    private fun isWin(x: Int, y: Int): Boolean{
        return isHorizontalWin(x, y) || isVerticalWin(x, y) || isMainDiagonalWin(x, y) || isAntiDiagonalWin(x, y)
    }

    private fun isHorizontalWin(x: Int, y: Int): Boolean{
        for(i in 0 until size) {
            if (_liveBoardState.value!!.cells[i][y] != _liveBoardState.value!!.cells[x][y])
                return false
        }

        return true
    }

    private fun isVerticalWin(x: Int, y: Int): Boolean{
        for(j in 0 until size) {
            if (_liveBoardState.value!!.cells[x][j] != _liveBoardState.value!!.cells[x][y])
                return false
        }

        return true
    }

    private fun isMainDiagonalWin(x: Int, y: Int): Boolean{
        if(x != y)
            return false

        for(ij in 0 until size) {
            if (_liveBoardState.value!!.cells[ij][ij] != _liveBoardState.value!!.cells[x][y])
                return false
        }

        return true
    }

    private fun isAntiDiagonalWin(x: Int, y: Int): Boolean{
        if(x + y != size - 1)
            return false

        for(ij in 0 until size) {
            if (_liveBoardState.value!!.cells[ij][size - ij - 1] != _liveBoardState.value!!.cells[x][y])
                return false
        }

        return true
    }
}