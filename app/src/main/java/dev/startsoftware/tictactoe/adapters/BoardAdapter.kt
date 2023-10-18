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


}