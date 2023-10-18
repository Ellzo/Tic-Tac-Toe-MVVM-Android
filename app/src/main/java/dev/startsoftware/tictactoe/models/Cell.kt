package dev.startsoftware.tictactoe.models

sealed class Cell(open var win: Boolean){
    object EMPTY: Cell(false){
        override fun toString(): String {
            return "E"
        }
    }
    class X(override var win: Boolean = false): Cell(win){
        override fun toString(): String {
            return "X"
        }
    }

    class O(override var win: Boolean = false): Cell(win){
        override fun toString(): String {
            return "O"
        }
    }
}