package dev.startsoftware.tictactoe

import android.view.View
import android.view.View.OnClickListener

interface GameMoveListener{
    fun onMove(position: Int)
}