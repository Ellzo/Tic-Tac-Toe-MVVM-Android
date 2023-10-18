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
            if(state == GameState.WIN && viewModel.liveTurn.value != null)
                updateScores(viewModel.liveTurn.value!!)
        }

        viewModel.liveTurn.observe(this){player ->
            tvTurnOfPlayer.text = player.displayName
            // TODO: Make some delay before next turn
        }

        gridBoard.adapter = adapter
    }

    override fun onMove(position: Int) {
        viewModel.makeMove(position)
    }

    private fun updateScores(player: Player){
        if(player is Player.Player1){
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