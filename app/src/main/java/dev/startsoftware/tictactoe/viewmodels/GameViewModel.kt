package dev.startsoftware.tictactoe.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.startsoftware.tictactoe.models.GameState
import dev.startsoftware.tictactoe.models.Board
import dev.startsoftware.tictactoe.models.Cell
import dev.startsoftware.tictactoe.models.Player
import kotlin.properties.Delegates
import kotlin.random.Random


class GameViewModel(private val size: Int): ViewModel() {
    private val _liveBoardState = MutableLiveData<Board>()
    private val _liveGameState = MutableLiveData<GameState>()
    private var _liveTurn = MutableLiveData<Player>()
    private var filledCells by Delegates.notNull<Int>()

    val liveBoardState: LiveData<Board>
        get() = _liveBoardState

    val liveGameState: LiveData<GameState>
        get() = _liveGameState

    val liveTurn: LiveData<Player>
        get() = _liveTurn

    init{
        restart()
    }

    fun restart(){
        _liveBoardState.value = Board(size)
        _liveGameState.value = GameState.INITIATED
        filledCells = 0

        _liveTurn.value = nextTurn()
        _liveTurn.value!!.isX = true

        moveIfComputer()
    }

    fun makeMove(position: Int): Boolean{
        if(!initiateOrOnGoing())
            return false

        val x = position % size
        val y = position / size

        if(_liveBoardState.value!!.cells[x][y] == Cell.EMPTY) {
            _liveBoardState.value!!.cells[x][y] = if(liveTurn.value!!.isX) Cell.X() else Cell.O()
            updateGameStatus(x, y)
            _liveBoardState.notifyObserver()

            moveIfComputer()

            return true
        }

        return false
    }

    private fun moveIfComputer(){
        if(!initiateOrOnGoing())
            return

        if(!(liveTurn.value is Player.Player2 && (liveTurn.value as Player.Player2).isComputer))
            return

        var isDone: Boolean
        var position: Int
        do{
            position = Random.nextInt(size * size)
            isDone = makeMove(position)
        }while(!isDone)
    }

    private fun initiateOrOnGoing(): Boolean{
        if(_liveGameState.value == GameState.INITIATED)
            _liveGameState.value = GameState.ONGOING

        return _liveGameState.value == GameState.ONGOING
    }

    private fun updateGameStatus(x: Int, y: Int){
        filledCells += 1

        if(!updateWinStatus(x, y) and isTie())
            _liveGameState.value = GameState.TIE

        if(liveGameState.value == GameState.ONGOING)
            _liveTurn.value = nextTurn()
    }

    private fun nextTurn() = if(_liveTurn.value is Player.Player1) Player.Player2 else Player.Player1

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
        if(isWin) {
            _liveGameState.value = GameState.WIN
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

    private fun isTie() = filledCells == size * size

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.value = this.value
    }
}