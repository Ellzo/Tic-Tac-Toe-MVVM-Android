package dev.startsoftware.tictactoe

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var turn = 1
    private var played = 0
    private var isComputer = false
    private var gameover=false

    private val cells= arrayOf(R.id.tx_a1,R.id.tx_a2,R.id.tx_a3,R.id.tx_a4,R.id.tx_a5,R.id.tx_a6,R.id.tx_a7,R.id.tx_a8,R.id.tx_a9)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for (cell in cells) {
            val item=findViewById<TextView>(cell)
            item.setOnClickListener { processClickEvent(cell) }
        }

        findViewById<Button>(R.id.btn_restart_multi).setOnClickListener{
            isComputer = false
            resetBoard()
        }

        findViewById<Button>(R.id.btn_restart_computer).setOnClickListener{
            isComputer = true
            resetBoard()
        }
    }

    private fun resetBoard(){
        for (cell in cells) {
            val item=findViewById<TextView>(cell)
            item.setBackgroundColor(Color.parseColor("#FFFFFF"))
            item.text = ""
        }
        played = 0
        turn = 1
        gameover = false
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
            makeVibration()
            updateScores(turn)
            gameover=true
            return
        }

        turn=if (turn==1) 2 else 1
        played += 1

        if(isComputer && turn == 2 && played < 9)
            computerPlay()
        else if(!isComputer)
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

    private fun makeVibration(){
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect: VibrationEffect

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // this effect creates the vibration of default amplitude for 1000ms(1 sec)
            vibrationEffect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE);

            // it is safe to cancel other vibrations currently taking place
            vibrator.cancel();
            vibrator.vibrate(vibrationEffect);
        }
    }

    private fun computerPlay(){
        var cellId: Int
        var existingValue: String
        do {
            cellId = cells.random()
            existingValue = findViewById<TextView>(cellId).text.toString()
        }while (existingValue.isNotEmpty())
        processClickEvent(cellId)
    }

    private fun updateScores(player: Int){
        if(player == 1){
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