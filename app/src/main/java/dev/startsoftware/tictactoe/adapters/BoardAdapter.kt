package dev.startsoftware.tictactoe.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import dev.startsoftware.tictactoe.models.Cell
import dev.startsoftware.tictactoe.listeners.GameMoveListener
import dev.startsoftware.tictactoe.R
import dev.startsoftware.tictactoe.models.Board
import dev.startsoftware.tictactoe.models.GameState
import kotlin.math.sqrt

class BoardAdapter(private val context: Context, private var boardCells: Array<Cell>): ArrayAdapter<Cell>(context, 0, boardCells) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var cellView = convertView
        if(cellView == null){
            cellView = LayoutInflater.from(context).inflate(R.layout.board_cell, parent, false)
        }

        val cell = getItem(position)
        cellView!!.findViewById<TextView>(R.id.tv_cell).text = cell.toString()
        cellView.setOnClickListener{
            (context as GameMoveListener).onMove(position)
        }

        if(cell!!.win)
            cellView.setBackgroundColor(context.resources.getColor(R.color.black, null))
        else
            cellView.setBackgroundColor(context.resources.getColor(R.color.white, null))

        return cellView
    }

    fun setData(board: Board){
        val size = board.size
        var position: Int
        for(x in 0 until size){
            for(y in 0 until size){
                position = size * y + x
                boardCells[position] = board.cells[x][y]
            }
        }

        notifyDataSetChanged()
    }

    fun setWinningBoard(winState: GameState.Win): Cell{
        val size = sqrt(boardCells.size.toDouble()).toInt()
        val position = size * winState.y + winState.x

        if(GameState.WinDirection.HORIZONTAL.toInt() and winState.directions != 0)
            setHorizontalWin(position, size)

        if(GameState.WinDirection.VERTICAL.toInt() and winState.directions != 0)
            setVerticalWin(position, size)

        if(GameState.WinDirection.MAIN_DIAGONAL.toInt() and winState.directions != 0)
            setDiagonalWin(size)

        if(GameState.WinDirection.ANTI_DIAGONAL.toInt() and winState.directions != 0)
            setAntiDiagonalWin(size)

        notifyDataSetChanged()

        return boardCells[position]
    }

    private fun setHorizontalWin(position: Int, size: Int){
        val row = position / size
        for(i in 0 until size)
            boardCells[row * size + i].win = true
    }
    private fun setVerticalWin(position: Int, size: Int){
        val column = position % size
        for(i in 0 until size)
            boardCells[i * size + column].win = true
    }

    private fun setDiagonalWin(size: Int){
        for(i in 0 until size){
            boardCells[i * (size + 1)].win = true
        }
    }

    private fun setAntiDiagonalWin(size: Int){
        for(i in 0 until size){
            boardCells[(i + 1) * (size - 1)].win = true
        }
    }


}