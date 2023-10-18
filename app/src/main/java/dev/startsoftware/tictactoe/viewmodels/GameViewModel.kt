package dev.startsoftware.tictactoe.viewmodels

import android.util.Log
import android.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.startsoftware.tictactoe.models.GameState
import dev.startsoftware.tictactoe.models.Board
import dev.startsoftware.tictactoe.models.Cell
import dev.startsoftware.tictactoe.models.Player
import kotlin.math.pow
import kotlin.math.sqrt
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
        val x = position % size
        val y = position / size
        return makeMove(x, y)
    }

    private fun makeMove(x: Int, y: Int): Boolean {
        if (!initiateOrOnGoing() || x !in 0 until size || y !in 0 until size)
            return false

        if (_liveBoardState.value!!.cells[x][y] == Cell.EMPTY) {
            _liveBoardState.value!!.cells[x][y] = if (liveTurn.value!!.isX) Cell.X() else Cell.O()
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

        //randomComputerMove()
        heuristicComputerMove()
    }

    private fun randomComputerMove(){
        var position: Int
        while(true){
            position = Random.nextInt(size * size)
            if(makeMove(position))
                return
        }
    }

    private fun heuristicComputerMove(): Boolean{
        val board = liveBoardState.value!!

        val horizontalScores = horizontalHeuristics()
        val verticalScores = verticalHeuristics()
        val diagonalScore = diagonalHeuristic()
        val antiDiagonalScore = antiDiagonalHeuristic()

        var score: Int
        var minScore: Int = size * size + 1
        var minX: Int = -1
        var minY: Int = -1

        for(x in 0 until board.size){
            for(y in 0 until board.size){
                if(board.cells[x][y] is Cell.EMPTY){
                    score = minOf(horizontalScores[x], verticalScores[y])
                    if(inMainDiagonal(x, y))
                        score = minOf(score, diagonalScore)
                    if(inAntiDiagonal(x, y))
                        score = minOf(score, antiDiagonalScore)

                    if(score < minScore) {
                        minScore = score
                        minX = x
                        minY = y
                    }

                    if(score == minScore){
                        val distMin = sqrt((minX - size/2.0).pow(2) + (minY - size/2.0).pow(2))
                        val dist = sqrt((x - size/2.0).pow(2) + (y - size/2.0).pow(2))
                        if(dist < distMin && Random.nextInt(10) < 6){
                            minX = x
                            minY = y
                        }
                    }

                }
            }
        }

        return makeMove(minX, minY)
    }

    private fun inMainDiagonal(x: Int, y: Int) = x == y

    private fun inAntiDiagonal(x: Int, y: Int) = x + y == size - 1

    private fun horizontalHeuristics(): Array<Int>{
        val heuristics = Array(size){ 2 * size }
        for(row in 0 until size){
            heuristics[row] = calcHeuristicScore(liveBoardState.value!!.cells[row])
        }

        return heuristics
    }

    private fun verticalHeuristics(): Array<Int>{
        val heuristics = Array(size){ 2 * size }

        for(y in 0 until size){
            val columns = Array<Cell>(size){ Cell.EMPTY }
            for(x in 0 until size){
                columns[x] = liveBoardState.value!!.cells[x][y]
            }
            heuristics[y] = calcHeuristicScore(columns)
        }

        return heuristics
    }

    private fun diagonalHeuristic(): Int{
        val diagonals = Array<Cell>(size){ Cell.EMPTY }
        for(xy in 0 until size)
            diagonals[xy] = liveBoardState.value!!.cells[xy][xy]

        return calcHeuristicScore(diagonals)
    }

    private fun antiDiagonalHeuristic(): Int{
        val antiDiagonals = Array<Cell>(size){ Cell.EMPTY }
        for(xy in 0 until size)
            antiDiagonals[xy] = liveBoardState.value!!.cells[xy][size - xy - 1]

        return calcHeuristicScore(antiDiagonals)
    }

    private fun calcHeuristicScore(cells: Array<Cell>): Int{
        var score = size * (size - 1)
        var encounteredX = false
        var encounteredO = false

        for(cell in cells){
            if(cell is Cell.EMPTY)
                continue

            score -= when{
                (cell is Cell.X && liveTurn.value!!.isX) -> {
                    encounteredX = true
                    size
                }

                (cell is Cell.O && !liveTurn.value!!.isX) -> {
                    encounteredO = true
                    size
                }

                (cell is Cell.X && !liveTurn.value!!.isX) -> {
                    encounteredX = true
                    size - 1
                }

                (cell is Cell.O && liveTurn.value!!.isX) -> {
                    encounteredO = true
                    size - 1
                }

                else -> 0
            }

            if(encounteredX && encounteredO)
                return size * size
        }

        return score
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
        if(!inMainDiagonal(x, y))
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
        if(!inAntiDiagonal(x, y))
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