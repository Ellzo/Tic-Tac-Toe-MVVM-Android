package dev.startsoftware.tictactoe.models

data class Board(val size: Int,
                 val cells: Array<Array<Cell>> = Array(size){ Array(size){ Cell.EMPTY } }) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (size != other.size) return false
        if (!cells.contentDeepEquals(other.cells)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + cells.contentDeepHashCode()
        return result
    }
}