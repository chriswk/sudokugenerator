package com.chriswk.sudoku.dancinglinks

import java.util.ArrayList
import javax.swing.text.html.HTML.Tag.U

class ColumnNode(var name: String) : DancingNode() {
    var size: Int = 0

    init {
        size = 0
        C = this
    }

    fun cover() {
        unlinkLR()
        var i = this.D
        while (i !== this) {
            var j = i?.R
            while (j !== i) {
                j?.unlinkUD()
                j?.C?.apply {
                    size--
                }
                j = j?.R
            }
            i = i?.D
        }
    }

    fun uncover() {
        var i = this.U
        while (i !== this) {
            var j = i?.L
            while (j !== i) {
                j?.C?.apply {
                    size++
                }
                j?.relinkUD()
                j = j?.L
            }
            i = i?.U
        }
        relinkLR()
    }

    private fun makeDLXBoard(grid: Array<BooleanArray>): ColumnNode {
        val COLS = grid[0].size

        var headerNode: ColumnNode? = ColumnNode("header")
        val columnNodes = ArrayList<ColumnNode>()

        for (i in 0 until COLS) {
            val n = ColumnNode(i.toString())
            columnNodes.add(n)
            headerNode = headerNode!!.hookRight(n) as ColumnNode
        }
        headerNode = headerNode!!.R?.C

        for (aGrid in grid) {
            var prev: DancingNode? = null
            for (j in 0 until COLS) {
                if (aGrid[j]) {
                    val col = columnNodes[j]
                    val newNode = DancingNode(col)
                    if (prev == null) prev = newNode
                    col.U?.hookDown(newNode)
                    prev = prev.hookRight(newNode)
                    col.size++
                }
            }
        }

        headerNode!!.size = COLS

        return headerNode
    }
}
