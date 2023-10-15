package dev.startsoftware.tictactoe

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class BoardAdapter(private val context: Context, private var boardCells: Array<Cell>): ArrayAdapter<Cell>(context, 0, boardCells) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var cellView = convertView
        if(cellView == null){
            cellView = LayoutInflater.from(context).inflate(R.layout.board_cell, parent, false)
        }

        val cell = getItem(position)
        cellView!!.findViewById<TextView>(R.id.tv_cell).text = if (cell != Cell.EMPTY) cell.toString() else "E"
        cellView.setOnClickListener{
            (context as GameMoveListener).onMove(position)
        }

        return cellView
    }

    fun setData(board: Board){
        boardCells = board.boardState.flatten().toTypedArray()
        notifyDataSetChanged()
    }


}