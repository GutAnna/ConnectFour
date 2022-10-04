package connectfour

import kotlin.system.exitProcess

data class Player(val name: String, val disk: Char, var score:Int = 0)

object Game {
    lateinit var firstPlayer: Player
    lateinit var secondPlayer: Player
    var cols = 7
    var rows = 6
    var gamesCount = 1

    private val desk = mutableListOf<MutableList<Char>>()

    fun initGame() {
        val list = mutableListOf<Char>()
        desk.clear()
        for (i in 0 until cols) desk.add(list)
    }

    fun printWelcome() {
        println("${firstPlayer.name} VS ${secondPlayer.name}")
        println("$rows X $cols board")
    }

    fun setDimensions() {
        while (true) {
            println("Set the board dimensions (Rows x Columns)")
            println("Press Enter for default (6 x 7)")
            val prompt = readLine()
            if (prompt.isNullOrEmpty()) {
                return
            } else
                try {
                    val (rows, cols) = prompt.split("[xX]".toRegex()).map { it.trim().toInt() }
                    if (rows > 9 || rows < 5) println("Board rows should be from 5 to 9")
                    else if (cols > 9 || cols < 5) println("Board columns should be from 5 to 9")
                    else {
                        Game.rows = rows
                        Game.cols = cols
                        return
                    }
                } catch (e: Exception) {
                    println("Invalid input")
                }
        }
    }

    private fun getTransDesk(): MutableList<MutableList<Char>> {
        val transDesk = mutableListOf<MutableList<Char>>()
        for (i in 0 until rows) {
            val line = mutableListOf<Char>()
            for (j in 0 until cols) {
                val ch = try {
                    desk[j][rows - i - 1]
                } catch (e: Exception) {
                    ' '
                }
                line.add(ch)
            }
            transDesk.add(line)
        }
        return transDesk
    }

    fun printBoard() {
        val desk = getTransDesk()
        println(" ${(1..cols).toList().joinToString(" ")} ")
        for (i in 0 until rows) {
            println(
                "${PrintSymbols.MID.symb}${
                    desk[i].joinToString(
                        PrintSymbols.MID.symb
                    )
                }${PrintSymbols.MID.symb}"
            )
        }
        println(
            "${PrintSymbols.LEFT.symb}${
                List(cols) { PrintSymbols.BOTTOM.symb }.joinToString(
                    PrintSymbols.CROSS.symb
                )
            }${PrintSymbols.RIGHT.symb}"
        )
    }

    fun playerTurn(player: Player): Boolean {
        while (true) {
            println("${player.name}'s turn:")
            val prompt = readLine()
            try {
                val col = prompt!!.toInt()
                if (col in 1..cols) {
                    if (desk[col - 1].size == rows) println("Column $col is full") else {
                        val line = desk[col - 1].toMutableList()
                        line.add(player.disk)
                        desk[col - 1] = line
                        printBoard()
                        return checkIsFinish()
                    }
                } else println("The column number is out of range (1 - $cols)")
            } catch (e: Exception) {
                if (prompt == "end") {
                    println("Game over!"); exitProcess(0)
                }
                println("Incorrect column number")
            }
        }
    }

    private fun getLine(): String {
        val result = mutableListOf<Char>()
        for (i in 0 until rows) {
            for (j in 0 until cols)
                try {
                    result.add(desk[j][rows - i - 1])
                } catch (e: Exception) {
                    result.add('-')
                }
        }
        return result.joinToString("")
    }

    private fun hasFour(ch: Char): Boolean {
        val theBoardString = getLine().replace("$ch", "1")
        val hasPattern =
            { it: Int -> Regex("1.{$it}1.{$it}1.{$it}1").containsMatchIn(theBoardString) }
        val splitList = { it: Int -> Regex("1.{$it}1.{$it}1.{$it}1").split(theBoardString) }
        //horizontal
        if (hasPattern(0)) return true
        //vertical
        if (hasPattern(cols - 1)) return true
        //mainDiagonal
        if (hasPattern(cols)) {
            val headTail = splitList(cols)
            if (headTail.size == 2)
                if (headTail.first().length / cols <= cols - 4) return true
        }
        //secondaryDiagonal
        if (hasPattern(cols - 2)) {
            val headTail = splitList(cols - 2)
            if (headTail.size in 1..2)
                if (headTail.last().length % cols >= 3)
                    return true
        }
        return false
    }

    private fun deskIsFull(): Boolean = !getLine().contains('-')

    private fun checkIsFinish(): Boolean {
        val firstWon = hasFour(firstPlayer.disk)
        val secondWon = hasFour(secondPlayer.disk)
        if (firstWon && secondWon || !firstWon && !secondWon && deskIsFull()) {
            println("It is a draw")
            firstPlayer.score++
            secondPlayer.score++
            return true
        } else {
            if (firstWon) { firstPlayer.score +=2; println("Player ${firstPlayer.name} won") }
            if (secondWon) { secondPlayer.score +=2; println("Player ${secondPlayer.name} won") }
        }
        return firstWon || secondWon
    }

    fun setCountGames() {
        while (true) {
            println(
                "Do you want to play single or multiple games?\n" +
                        "For a single game, input 1 or press Enter\n" +
                        "Input a number of games:"
            )
            val count = readLine()
            if (count.isNullOrEmpty()) return
            try {
                gamesCount = count.toInt()
                if (gamesCount < 1) throw Exception("Invalid input")
                return
            } catch (e: Exception) {
                println("Invalid input")
            }
        }
    }

    fun finishGame(gameNumber: Int) {
        println("Score\n" +
                "${firstPlayer.name}: ${firstPlayer.score} ${secondPlayer.name}: ${secondPlayer.score}")
        if (gameNumber == gamesCount)
        println("Game over!")
    }
}

enum class PrintSymbols(val symb: String) {
    MID("║"), LEFT("╚"),
    BOTTOM("═"), CROSS("╩"), RIGHT("╝")
}

fun main() {
    // Players
    println("Connect Four\nFirst player's name:")
    Game.firstPlayer = Player(readLine()!!, 'o')
    println("Second player's name:")
    Game.secondPlayer = Player(readLine()!!, '*')
    // Board
    Game.setDimensions()
    //Game
    Game.setCountGames()
    Game.printWelcome()
    if (Game.gamesCount == 1) println("Single game")
    else println("Total ${Game.gamesCount} games")
    for (j in 1 .. Game.gamesCount) {
        if (Game.gamesCount > 1) println("Game #$j")
        Game.initGame()
        Game.printBoard()
        while (true) {
            if (j % 2 != 0) {
                if (Game.playerTurn(Game.firstPlayer))  break
                if (Game.playerTurn(Game.secondPlayer))  break
            } else {
                if (Game.playerTurn(Game.secondPlayer))  break
                if (Game.playerTurn(Game.firstPlayer)) break
            }
        }
        Game.finishGame(j);
    }
}