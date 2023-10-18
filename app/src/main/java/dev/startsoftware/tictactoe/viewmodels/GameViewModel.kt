package dev.startsoftware.tictactoe.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.startsoftware.tictactoe.models.GameState
import dev.startsoftware.tictactoe.models.Board
import dev.startsoftware.tictactoe.models.Cell
import kotlin.properties.Delegates
import kotlin.random.Random


class GameViewModel(private val size: Int): ViewModel() {
    private val _liveBoardState = MutableLiveData<Board>()
    private val _liveGameState = MutableLiveData<GameState>()
    private var _liveTurn = MutableLiveData<Cell>()
    private var filledCells by Delegates.notNull<Int>()

    val liveBoardState: LiveData<Board>
        get() = _liveBoardState

    val liveGameState: LiveData<GameState>
        get() = _liveGameState

    val liveTurn: LiveData<Cell>
        get() = _liveTurn

    init{
        restart()
    }

    fun restart(){
        _liveBoardState.value = Board(size)
        _liveGameState.value = GameState.INITIATED
        _liveTurn.value = Cell.X()
        filledCells = 0
    }

    fun computerMove(){
        if(liveGameState.value != GameState.ONGOING)
            return

        var isDone: Boolean
        var position: Int
        do{
            position = Random.nextInt(size * size)
           isDone = makeMove(position)
        }while(!isDone)
    }

    fun makeMove(position: Int): Boolean{
        if(_liveGameState.value == GameState.INITIATED)
            _liveGameState.value = GameState.ONGOING

        if(_liveGameState.value != GameState.ONGOING)
            return false

        val x = position % size
        val y = position / size

        if(_liveBoardState.value!!.cells[x][y] == Cell.EMPTY) {
            _liveBoardState.value!!.cells[x][y] = _liveTurn.value!!
            updateGameStatus(x, y)
            _liveBoardState.notifyObserver()
            return true
        }

        return false
    }

    private fun updateGameStatus(x: Int, y: Int){
        filledCells += 1
        _liveTurn.value = getNextTurn()

        if(!updateWinStatus(x, y) and isTie())
            _liveGameState.value = GameState.TIE
    }

    private fun getNextTurn() = if(_liveTurn.value is Cell.X) Cell.O() else Cell.X()

    private fun updateWinStatus(x: Int, y: Int): Boolean{
        val isHorizontalWin = isHorizontalWin(x, y)
        if(isHorizontalWin)
            setCellsOfHorizontalWin(y)

        val isVerticalWin = isVerticalWin(x, y)
        if(isVerticalWin)
            setCellsOfVerticalWin(x)

        val isMainDiagonalWin = isMainDiagonalWin(x, y)
        if(isMainDiagonalWin)
            setCellsOfMainDiagonalWin()

        val isAntiDiagonalWin = isAntiDiagonalWin(x, y)
        if(isAntiDiagonalWin)
            setCellsOfAntiDiagonalWin()

        val isWin = isHorizontalWin || isVerticalWin || isMainDiagonalWin || isAntiDiagonalWin
        Log.d("TAG - WIN", "updateWinStatus: $isWin")
        if(isWin) {
            _liveGameState.value = getWinner(x, y)
            return true
        }

        return false
    }

    private fun isHorizontalWin(x: Int, y: Int): Boolean{
        for(i in 0 until size) {
            if (_liveBoardState.value!!.cells[i][y] != _liveBoardState.value!!.cells[x][y])
                return false
        }

        return true
    }

    private fun setCellsOfHorizontalWin(y: Int){
        for(i in 0 until size)
            _liveBoardState.value!!.cells[i][y].win = true
    }

    private fun isVerticalWin(x: Int, y: Int): Boolean{
        for(j in 0 until size) {
            if (_liveBoardState.value!!.cells[x][j] != _liveBoardState.value!!.cells[x][y])
            if (_liveBoardState.value!!.cells[x][j] != _liveBoardState.value!!.cells[x][y])
                return false
        }

        return true
    }

    private fun setCellsOfVerticalWin(x: Int){
        for(j in 0 until size)
            _liveBoardState.value!!.cells[x][j].win = true
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

    private fun setCellsOfMainDiagonalWin(){
        for(ij in 0 until size)
            _liveBoardState.value!!.cells[ij][ij].win = true
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

    private fun setCellsOfAntiDiagonalWin(){
        for(ij in 0 until size)
            _liveBoardState.value!!.cells[ij][size - ij - 1].win = true
    }

    private fun getWinner(x: Int, y: Int) = if(_liveBoardState.value!!.cells[x][y] is Cell.X) GameState.WIN_X else GameState.WIN_O

    private fun isTie() = filledCells == size * size

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}