package com.chriswk.sudoku.dancinglinks

class DancingLinksAlgorithm {

    fun solve(board: Array<IntArray>): IntArray? {
        val cover = initializeExactCoverBoard(board)
        val dlx = DancingLinks(cover)
        return dlx.runSolver()
    }

    private fun getIndex(row: Int, column: Int, num: Int): Int {
        return (row - 1) * BOARD_SIZE * BOARD_SIZE + (column - 1) * BOARD_SIZE + (num - 1)
    }

    private fun createExactCoverBoard(): Array<BooleanArray> {
        val coverBoard = Array(BOARD_SIZE * BOARD_SIZE * MAX_VALUE) { BooleanArray(BOARD_SIZE * BOARD_SIZE * CONSTRAINTS) }

        var hBase = 0
        hBase = checkCellConstraint(coverBoard, hBase)
        hBase = checkRowConstraint(coverBoard, hBase)
        hBase = checkColumnConstraint(coverBoard, hBase)
        checkSubsectionConstraint(coverBoard, hBase)

        return coverBoard
    }

    private fun checkSubsectionConstraint(coverBoard: Array<BooleanArray>, hB: Int): Int {
        var hBase = hB
        var row = COVER_START_INDEX
        while (row <= BOARD_SIZE) {
            var column = COVER_START_INDEX
            while (column <= BOARD_SIZE) {
                var n = COVER_START_INDEX
                while (n <= BOARD_SIZE) {
                    for (rowDelta in 0 until SUBSECTION_SIZE) {
                        for (columnDelta in 0 until SUBSECTION_SIZE) {
                            val index = getIndex(row + rowDelta, column + columnDelta, n)
                            coverBoard[index][hBase] = true
                        }
                    }
                    n++
                    hBase++
                }
                column += SUBSECTION_SIZE
            }
            row += SUBSECTION_SIZE
        }
        return hBase
    }

    private fun checkColumnConstraint(coverBoard: Array<BooleanArray>, hB: Int): Int {
        var hBase = hB
        for (column in COVER_START_INDEX..BOARD_SIZE) {
            var n = COVER_START_INDEX
            while (n <= BOARD_SIZE) {
                for (row in COVER_START_INDEX..BOARD_SIZE) {
                    val index = getIndex(row, column, n)
                    coverBoard[index][hBase] = true
                }
                n++
                hBase++
            }
        }
        return hBase
    }

    private fun checkRowConstraint(coverBoard: Array<BooleanArray>, hB: Int): Int {
        var hBase = hB
        for (row in COVER_START_INDEX..BOARD_SIZE) {
            var n = COVER_START_INDEX
            while (n <= BOARD_SIZE) {
                for (column in COVER_START_INDEX..BOARD_SIZE) {
                    val index = getIndex(row, column, n)
                    coverBoard[index][hBase] = true
                }
                n++
                hBase++
            }
        }
        return hBase
    }

    private fun checkCellConstraint(coverBoard: Array<BooleanArray>, hB: Int): Int {
        var hBase = hB
        for (row in COVER_START_INDEX..BOARD_SIZE) {
            var column = COVER_START_INDEX
            while (column <= BOARD_SIZE) {
                for (n in COVER_START_INDEX..BOARD_SIZE) {
                    val index = getIndex(row, column, n)
                    coverBoard[index][hBase] = true
                }
                column++
                hBase++
            }
        }
        return hBase
    }

    private fun initializeExactCoverBoard(board: Array<IntArray>): Array<BooleanArray> {
        val coverBoard = createExactCoverBoard()
        for (row in COVER_START_INDEX..BOARD_SIZE) {
            for (column in COVER_START_INDEX..BOARD_SIZE) {
                val n = board[row - 1][column - 1]
                if (n != NO_VALUE) {
                    for (num in MIN_VALUE..MAX_VALUE) {
                        if (num != n) {
                            coverBoard[getIndex(row, column, num)].fill(false)
                        }
                    }
                }
            }
        }
        return coverBoard
    }

    companion object {
        private val BOARD_SIZE = 9
        private val SUBSECTION_SIZE = 3
        private val NO_VALUE = 0
        private val CONSTRAINTS = 4
        private val MIN_VALUE = 1
        private val MAX_VALUE = 9
        private val COVER_START_INDEX = 1

        private val board = arrayOf(intArrayOf(8, 0, 0, 0, 0, 0, 0, 0, 0), intArrayOf(0, 0, 3, 6, 0, 0, 0, 0, 0), intArrayOf(0, 7, 0, 0, 9, 0, 2, 0, 0), intArrayOf(0, 5, 0, 0, 0, 7, 0, 0, 0), intArrayOf(0, 0, 0, 0, 4, 5, 7, 0, 0), intArrayOf(0, 0, 0, 1, 0, 0, 0, 3, 0), intArrayOf(0, 0, 1, 0, 0, 0, 0, 6, 8), intArrayOf(0, 0, 8, 5, 0, 0, 0, 1, 0), intArrayOf(0, 9, 0, 0, 0, 0, 4, 0, 0))

        @JvmStatic
        fun main(args: Array<String>) {
            val solver = DancingLinksAlgorithm()
            solver.solve(board)
        }
    }
}
