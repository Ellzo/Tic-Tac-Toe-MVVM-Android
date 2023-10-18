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

    override fun equals(other: Any?): Boolean {
        if(other !is Cell){
            return false
        }

        if(this is EMPTY && other is EMPTY)
            return true

        if(this is X && other is X)
            return true

        if(this is O && other is O)
            return true

        return false
    }
}