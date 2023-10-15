package dev.startsoftware.tictactoe

data class Board(val size: Int,
                 val boardState: Array<Array<Cell>> = Array(size){ Array(size){ Cell.EMPTY } }) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Board

        if (size != other.size) return false
        if (!boardState.contentDeepEquals(other.boardState)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + boardState.contentDeepHashCode()
        return result
    }
}