package dev.startsoftware.tictactoe.models

sealed class GameState{
    object Initiated: GameState()
    object OnGoing : GameState()
    data class Win(val x: Int, val y: Int, val directions: Int): GameState()
    object Tie: GameState()

    enum class WinDirection{
        HORIZONTAL,
        VERTICAL,
        MAIN_DIAGONAL,
        ANTI_DIAGONAL;

        fun toInt() = when(this){
            HORIZONTAL -> 1
            VERTICAL -> 2
            MAIN_DIAGONAL -> 4
            ANTI_DIAGONAL -> 8
        }
    }
}