package dev.startsoftware.tictactoe

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var turn =1
    private var gameover=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cells= arrayOf(R.id.tx_a1,R.id.tx_a2,R.id.tx_a3,R.id.tx_a4,R.id.tx_a5,R.id.tx_a6,R.id.tx_a7,R.id.tx_a8,R.id.tx_a9)
        for (cell in cells) {
            val item=findViewById<TextView>(cell)
            item.setOnClickListener { processClickEvent(cell) }
        }

        findViewById<Button>(R.id.btn_restart).setOnClickListener{
            for (cell in cells) {
                val item=findViewById<TextView>(cell)
                item.setBackgroundColor(Color.parseColor("#FFFFFF"))
                item.text = ""
                turn = 1
                gameover = false
            }
        }
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
            updateScores(turn)
            gameover=true
            return
        }

        turn=if (turn==1) 2 else 1
        findViewById<TextView>(R.id.tx_turn).text = "Turn of Player: $turn"
    }

    private val possibleWins = arrayOf(
        arrayOf(R.id.tx_a1,R.id.tx_a2,R.id.tx_a3),
        arrayOf(R.id.tx_a4,R.id.tx_a5,R.id.tx_a6),
        arrayOf(R.id.tx_a7,R.id.tx_a8,R.id.tx_a9),

        arrayOf(R.id.tx_a1,R.id.tx_a5,R.id.tx_a9),
        arrayOf(R.id.tx_a7,R.id.tx_a5,R.id.tx_a3),

        arrayOf(R.id.tx_a1,R.id.tx_a4,R.id.tx_a7),
        arrayOf(R.id.tx_a2,R.id.tx_a5,R.id.tx_a8),
        arrayOf(R.id.tx_a3,R.id.tx_a6,R.id.tx_a9),
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

    private fun updateScores(player: Int){
        if(player == 1){
            var score = findViewById<TextView>(R.id.tv_score1).text.toString().toInt()
            score += 1
            findViewById<TextView>(R.id.tv_score1).text = score.toString()
        }else{
            var score = findViewById<TextView>(R.id.tv_score2).text.toString().toInt()
            score += 1
            findViewById<TextView>(R.id.tv_score2).text = score.toString()
        }
    }
}