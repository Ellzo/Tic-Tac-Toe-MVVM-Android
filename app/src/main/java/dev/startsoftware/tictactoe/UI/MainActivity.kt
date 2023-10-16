package dev.startsoftware.tictactoe.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.GridView
import android.widget.TextView
import androidx.activity.viewModels
import dev.startsoftware.tictactoe.R
import dev.startsoftware.tictactoe.adapters.BoardAdapter
import dev.startsoftware.tictactoe.listeners.GameMoveListener
import dev.startsoftware.tictactoe.models.Cell
import dev.startsoftware.tictactoe.viewmodels.GameViewModel
import dev.startsoftware.tictactoe.viewmodels.GameViewModelFactory
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), GameMoveListener {
    private var boardSize by Delegates.notNull<Int>()
    private val viewModel: GameViewModel by viewModels(factoryProducer = { GameViewModelFactory(boardSize) })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardSize = resources.getInteger(R.integer.board_size)

        val gridBoard = findViewById<GridView>(R.id.grid_board)
        gridBoard.numColumns = boardSize

        val cells = Array(boardSize * boardSize){ Cell.EMPTY }

        val adapter = BoardAdapter(this, cells)
        viewModel.liveGameState.observe(this){ board ->
            adapter.setData(board)
        }

        viewModel.liveWinState.observe(this){state ->
            // TODO: Handle win state
        }

        gridBoard.adapter = adapter
    }

    override fun onMove(position: Int) {
        viewModel.makeMove(position)
    }
}