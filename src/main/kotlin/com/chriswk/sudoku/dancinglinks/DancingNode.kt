package com.chriswk.sudoku.dancinglinks

open class DancingNode() {
    var L: DancingNode?
    var R: DancingNode?
    var U: DancingNode?
    var D: DancingNode?
    var C: ColumnNode? = null

    fun hookDown(node: DancingNode): DancingNode {
        assert(this.C == node.C)
        node.D = this.D
        node.D?.U = node
        node.U = this
        this.D = node
        return node
    }

    fun hookRight(node: DancingNode): DancingNode {
        node.R = this.R
        node.R?.L = node
        node.L = this
        this.R = node
        return node
    }

    fun unlinkLR() {
        this.L?.R = this.R
        this.R?.L = this.L
    }

    fun relinkLR() {
        this.R?.L = this
        this.L?.R = this.R?.L
    }

    fun unlinkUD() {
        this.U?.D = this.D
        this.D?.U = this.U
    }

    fun relinkUD() {
        this.D?.U = this
        this.U?.D = this.D?.U
    }

    init {
        D = this
        U = D
        R = U
        L = R
    }

    constructor(c: ColumnNode) : this() {
        C = c
    }
}
