package dev.startsoftware.tictactoe.models

sealed class Player(var score: Int = 0){
    abstract var displayName: String
    abstract var isX: Boolean

    object Player1: Player() {
        override var displayName = "Player 1"
        override var isX = true
            set(value) {
                field = value
                if(Player2.isX == value)
                    Player2.isX = !value
            }


    }

    object Player2: Player() {
        override var displayName = "Player 2"
        var isComputer: Boolean = true

        override var isX = false
            set(value){
                field = value
                if(Player1.isX == value)
                    Player1.isX = !value
            }
    }

    override fun toString(): String {
        return displayName
    }
}