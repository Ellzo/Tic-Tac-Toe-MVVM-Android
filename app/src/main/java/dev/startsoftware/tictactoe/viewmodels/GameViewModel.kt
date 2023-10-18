package dev.startsoftware.tictactoe.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.startsoftware.tictactoe.models.GameState
import dev.startsoftware.tictactoe.models.Board
import dev.startsoftware.tictactoe.models.Cell
import kotlin.random.Random


class GameViewModel(private val size: Int): ViewModel() {
    private val _liveBoardState = MutableLiveData(Board(size))
    private val _liveGameState = MutableLiveData<GameState>(GameState.Initiated)
    private var _liveTurn = MutableLiveData<Cell>(Cell.X())
    private var filledCells = 0
    val liveBoardState: LiveData<Board>
        get() = _liveBoardState

    val liveGameState: LiveData<GameState>
        get() = _liveGameState

    val liveTurn: LiveData<Cell>
        get() = _liveTurn

    fun restart(){
        _liveBoardState.value = Board(size)
        _liveGameState.value = GameState.Initiated
        _liveTurn.value = Cell.X()
        filledCells = 0
    }

    fun computerMove(){
        if(liveGameState.value !is GameState.OnGoing)
            return

        var isDone: Boolean
        var position: Int
        do{
            position = Random.nextInt(size * size)
           isDone = makeMove(position)
        }while(!isDone)
    }

    fun makeMove(position: Int): Boolean{
        if(_liveGameState.value is GameState.Initiated)
            _liveGameState.value = GameState.OnGoing

        if(_liveGameState.value !is GameState.OnGoing)
            return false

        val x = position % size
        val y = position / size

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

        updateTieStatus()
        updateWinStatus(x, y)
    }

    private fun getNextTurn() = if(_liveTurn.value is Cell.X) Cell.O() else Cell.X()

    private fun updateWinStatus(x: Int, y: Int){
        val isHorizontalWin = isHorizontalWin(x, y)
        val isVerticalWin = isVerticalWin(x, y)
        val isMainDiagonalWin = isMainDiagonalWin(x, y)
        val isAntiDiagonalWin = isAntiDiagonalWin(x, y)

        val isWin = isHorizontalWin or isVerticalWin or isMainDiagonalWin or isAntiDiagonalWin
        if(isWin != 0)
            _liveGameState.value = GameState.Win(x, y, isWin)
    }

    private fun updateTieStatus(){
        if(isTie())
            _liveGameState.value = GameState.Tie
    }

    //private fun getWinner(x: Int, y: Int) = if(_liveBoardState.value!!.cells[x][y] == Cell.X) GameState.PLAYER1 else GameState.PLAYER2

    private fun isTie() = filledCells == size * size

    private fun isHorizontalWin(x: Int, y: Int): Int{
        for(i in 0 until size) {
            if (_liveBoardState.value!!.cells[i][y].toString() != _liveBoardState.value!!.cells[x][y].toString())
                return 0
        }

        return GameState.WinDirection.HORIZONTAL.toInt()
    }

    private fun isVerticalWin(x: Int, y: Int): Int{
        for(j in 0 until size) {
            if (_liveBoardState.value!!.cells[x][j].toString() != _liveBoardState.value!!.cells[x][y].toString())
                return 0
        }

        return GameState.WinDirection.VERTICAL.toInt()
    }

    private fun isMainDiagonalWin(x: Int, y: Int): Int{
        if(x != y)
            return 0

        for(ij in 0 until size) {
            if (_liveBoardState.value!!.cells[ij][ij].toString() != _liveBoardState.value!!.cells[x][y].toString())
                return 0
        }

        return GameState.WinDirection.MAIN_DIAGONAL.toInt()
    }

    private fun isAntiDiagonalWin(x: Int, y: Int): Int{
        if(x + y != size - 1)
            return 0

        for(ij in 0 until size) {
            if (_liveBoardState.value!!.cells[ij][size - ij - 1].toString() != _liveBoardState.value!!.cells[x][y].toString())
                return 0
        }

        return GameState.WinDirection.ANTI_DIAGONAL.toInt()
    }
}