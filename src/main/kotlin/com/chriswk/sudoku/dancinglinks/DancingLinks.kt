package com.chriswk.sudoku.dancinglinks

class DancingLinks(cover: Array<BooleanArray>) {

    private val header: ColumnNode
    private var answer: MutableList<DancingNode>? = null

    private val size = 9

    private fun search(k: Int): IntArray? {
        if (header.R === header) {
            val solved = handleSolution(answer)
            return solved.map { it.toList() }.reduce { a, r ->
                a + r
            }.toIntArray()
        } else {
            var c = selectColumnNodeHeuristic()
            c!!.cover()

            var r = c.D!!
            while (r !== c) {
                answer!!.add(r)

                run {
                    var j = r.R
                    while (j !== r) {
                        j!!.C!!.cover()
                        j = j!!.R
                    }
                }

                search(k + 1)

                r = answer!!.removeAt(answer!!.size - 1)
                c = r.C

                var j = r.L
                while (j !== r) {
                    j!!.C!!.uncover()
                    j = j!!.L
                }
                r = r.D!!
            }
            c.uncover()
        }
        return null
    }

    private fun selectColumnNodeHeuristic(): ColumnNode? {
        var min = Integer.MAX_VALUE
        var ret: ColumnNode? = null
        var c: ColumnNode = header.R as ColumnNode
        while (c != header) {
            if (c.size < min) {
                min = c.size
                ret = c
            }
            c = c.R as ColumnNode
        }
        return ret
    }

    private fun makeDLXBoard(grid: Array<BooleanArray>): ColumnNode {
        val c = grid[0].size

        var headerNode: ColumnNode? = ColumnNode("header")
        val columnNodes = ArrayList<ColumnNode>()

        for (i in 0 until c) {
            val n = ColumnNode(Integer.toString(i))
            columnNodes.add(n)
            headerNode = headerNode!!.hookRight(n) as ColumnNode
        }
        headerNode = headerNode!!.R!!.C

        for (aGrid in grid) {
            var prev: DancingNode? = null
            for (j in 0 until c) {
                if (aGrid[j]) {
                    val col = columnNodes[j]
                    val newNode = DancingNode(col)
                    if (prev == null)
                        prev = newNode
                    col.U!!.hookDown(newNode)
                    prev = prev.hookRight(newNode)
                    col.size++
                }
            }
        }

        headerNode!!.size = c

        return headerNode
    }

    init {
        header = makeDLXBoard(cover)
    }

    fun runSolver(): IntArray? {
        answer = mutableListOf()
        return search(0)
    }

    private fun handleSolution(answer: List<DancingNode>?): Array<IntArray> {
        val result = parseBoard(answer!!)
        printSolution(result)
        return result
    }

    private fun parseBoard(answer: List<DancingNode>): Array<IntArray> {
        val result = Array(size) { IntArray(size) }
        for (n in answer) {
            var rcNode = n
            var min = Integer.parseInt(rcNode.C!!.name)
            var tmp = n.R
            while (tmp !== n) {
                val `val` = Integer.parseInt(tmp!!.C!!.name)
                if (`val` < min) {
                    min = `val`
                    rcNode = tmp
                }
                tmp = tmp.R
            }
            val ans1 = Integer.parseInt(rcNode.C!!.name)
            val ans2 = Integer.parseInt(rcNode.R!!.C!!.name)
            val r = ans1 / size
            val c = ans1 % size
            val num = ans2 % size + 1
            result[r][c] = num
        }
        return result
    }

    private fun printSolution(result: Array<IntArray>) {
        val size = result.size
        for (aResult in result) {
            val ret = StringBuilder()
            for (j in 0 until size) {
                ret.append(aResult[j]).append(" ")
            }
            println(ret)
        }
        println()
    }
}
