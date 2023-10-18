package dev.startsoftware.tictactoe.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.activity.viewModels
import dev.startsoftware.tictactoe.R
import dev.startsoftware.tictactoe.adapters.BoardAdapter
import dev.startsoftware.tictactoe.listeners.GameMoveListener
import dev.startsoftware.tictactoe.models.Cell
import dev.startsoftware.tictactoe.models.GameState
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
            if(state == GameState.WIN_X || state == GameState.WIN_O){
                updateScores(state == GameState.WIN_X)
            }
        }

        gridBoard.adapter = adapter
    }

    override fun onMove(position: Int) {
        viewModel.makeMove(position)
        viewModel.computerMove()
    }

    private fun updateScores(isPlayer1: Boolean){
        if(isPlayer1){
            val tvScore1 = findViewById<TextView>(R.id.tv_score1)
            var score = tvScore1.text.toString().toInt()
            score += 1
            tvScore1.text = score.toString()
        }else{
            val tvScore2 = findViewById<TextView>(R.id.tv_score2)
            var score = tvScore2.text.toString().toInt()
            score += 1
            tvScore2.text = score.toString()
        }
    }
}