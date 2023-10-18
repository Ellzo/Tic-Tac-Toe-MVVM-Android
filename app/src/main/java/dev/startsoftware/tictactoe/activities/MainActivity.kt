package dev.startsoftware.tictactoe.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.activity.viewModels
import dev.startsoftware.tictactoe.R
import dev.startsoftware.tictactoe.adapters.BoardAdapter
import dev.startsoftware.tictactoe.listeners.GameMoveListener
import dev.startsoftware.tictactoe.models.GameState
import dev.startsoftware.tictactoe.models.Player
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

        val tvTurnOfPlayer = findViewById<TextView>(R.id.tv_player)

        findViewById<Button>(R.id.btn_restart).setOnClickListener {
            viewModel.restart()
        }

        viewModel.liveBoardState.observe(this){ board ->
            adapter.setData(board)
        }

        viewModel.liveGameState.observe(this){ state ->
            //TODO: Make vibration
        }

        viewModel.liveTurn.observe(this){player ->
            tvTurnOfPlayer.text = player.displayName
            // TODO: Make some delay before next turn
        }

        val tvScore1 = findViewById<TextView>(R.id.tv_score1)
        viewModel.livePlayer1.observe(this){player1 ->
            tvScore1.text = player1.score.toString()
        }

        val tvScore2 = findViewById<TextView>(R.id.tv_score2)
        viewModel.livePlayer2.observe(this){player2 ->
            tvScore2.text = player2.score.toString()
        }

        gridBoard.adapter = adapter
    }

    override fun onMove(position: Int) {
        viewModel.makeMove(position)
    }
}