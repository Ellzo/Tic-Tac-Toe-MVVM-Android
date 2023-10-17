package dev.startsoftware.tictactoe.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import androidx.activity.viewModels
import dev.startsoftware.tictactoe.R
import dev.startsoftware.tictactoe.adapters.BoardAdapter
import dev.startsoftware.tictactoe.listeners.GameMoveListener
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
        val cells = viewModel.liveBoardState.value!!.cells.flatten().toTypedArray()
        val adapter = BoardAdapter(this, cells)

        findViewById<Button>(R.id.btn_restart).setOnClickListener {
            viewModel.restart()
        }

        viewModel.liveBoardState.observe(this){ board ->
            adapter.setData(board)
        }

        viewModel.liveGameState.observe(this){ state ->
            // TODO: Handle win state
            // Note: To color the winning cells, you might need to store some additional flags of the winning
            //  e.g. (x, y), win type: horizontal, vertical, ect.
        }

        gridBoard.adapter = adapter
    }

    override fun onMove(position: Int) {
        viewModel.makeMove(position)
    }
}