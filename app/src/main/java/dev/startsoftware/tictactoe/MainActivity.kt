package dev.startsoftware.tictactoe

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.GridView
import android.widget.TextView
import androidx.activity.viewModels
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), GameMoveListener {
    private var boardSize = 3
    private val viewModel: GameViewModel by viewModels(factoryProducer = { GameViewModelFactory(boardSize) })

    private var turn =1
    private var gameover=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gridBoard = findViewById<GridView>(R.id.grid_board)

        boardSize = resources.getInteger(R.integer.board_size)

        val cells = Array(boardSize * boardSize){ Cell.EMPTY }

        val adapter = BoardAdapter(this, cells)
        viewModel.liveGameState.observe(this){ board ->
            Log.d("DEBUGGING GG", "onCreate: CHANGE OBSERVED")
            adapter.setData(board)
        }
        gridBoard.adapter = adapter
    }

    private fun processClickEvent(cellId: Int){
        if (gameover)
            return
        val existingValue:String=findViewById<TextView>(cellId).text.toString()
        if (existingValue.isNotEmpty()){
            return
        }
        if (turn==1) {
            findViewById<TextView>(cellId).text = "X"
            findViewById<TextView>(cellId).setTextColor(Color.parseColor("#ff0000"))
        }else {
            findViewById<TextView>(cellId).text = "O"
            findViewById<TextView>(cellId).setTextColor(Color.parseColor("#00ff00"))
        }

        val win=checkWin()
        if (win){
            findViewById<TextView>(R.id.tx_turn).text = "Congrats to Player $turn"
            gameover=true
            return
        }

        turn=if (turn==1) 2 else 1
        findViewById<TextView>(R.id.tx_turn).text = "Turn of Player: $turn"
    }

    /*private val possibleWins = arrayOf(
        arrayOf(R.id.tx_a1,R.id.tx_a2,R.id.tx_a3),
        arrayOf(R.id.tx_a4,R.id.tx_a5,R.id.tx_a6),
        arrayOf(R.id.tx_a7,R.id.tx_a8,R.id.tx_a9),

        arrayOf(R.id.tx_a1,R.id.tx_a5,R.id.tx_a9),
        arrayOf(R.id.tx_a7,R.id.tx_a5,R.id.tx_a3),

        arrayOf(R.id.tx_a1,R.id.tx_a4,R.id.tx_a7),
        arrayOf(R.id.tx_a2,R.id.tx_a5,R.id.tx_a8),
        arrayOf(R.id.tx_a3,R.id.tx_a6,R.id.tx_a9),
    )*/

    private val possibleWins = arrayOf(
        arrayOf(0)
    )

    private fun checkWin(): Boolean{
        for (possible in possibleWins){
            var seqStr=""
            for (cellId in possible){
                val existingValue:String=findViewById<TextView>(cellId).text.toString()
                if (existingValue.isEmpty())
                    break
                seqStr += existingValue
            }
            if (seqStr=="OOO" || seqStr=="XXX") {
                for (cellId in possible){
                    findViewById<TextView>(cellId).setBackgroundColor(Color.parseColor("#FF33F3"))
                }
                return true
            }
        }
        return false
    }

    override fun onMove(position: Int) {
        Log.d("DEBUGGING GG", "onMove: $position")
        viewModel.makeMove(position)
    }
}