package fr.iutlens.mmi.rogue

import android.util.Pair
import java.util.*

/**
 * Created by dubois on 24/12/2019.
 */
class RandomChooser<T> {
    private val rand = Random()

    private inner class Choice(val description: T, val size: Int, val weight: Int)

    private var v: Vector<Choice>

    constructor() {
        v = Vector()
    }

    constructor(list: Array<T>) {
        v = Vector()
        for (t in list) {
            addOption(t, 1, 1)
        }
    }

    fun addOption(description: T, size: Int, weight: Int) {
        v.add(Choice(description, size, weight))
    }

    private fun getOne(possible: Vector<Choice>): Choice? {
        var sumW = 0
        for (c in v) sumW += c.weight
        if (sumW <= 0) return null //FIXME
        sumW = rand.nextInt(sumW)
        for (c in possible) {
            sumW -= c.weight
            if (sumW < 0) {
                return c
            }
        }
        return null
    }

    val one: T?
        get() {
            val choice = getOne(v) ?: return null
            return choice.description
        }

    fun getOne(except: T): T {
        val possible = Vector(v)
        val it: MutableIterator<*> = possible.iterator()
        while (it.hasNext()) {
            if (except == it.next()) it.remove()
        }
        return getOne(possible)!!.description
    }

     fun get(maxSize: Int): Vector<T> {
        var maxSize = maxSize
        val result = Vector<T>()
        val possible = Vector<Choice>()
        for (c in v) if (c.size <= maxSize) possible.add(c)
        while (maxSize > 0 && possible.size > 0) {
            val c = getOne(possible) ?: return result
            // FIXME
            result.add(c.description)
            maxSize -= c.size
            val it = possible.iterator()
            while (it.hasNext()) if (it.next().size > maxSize) it.remove()
        }
        return result
    }
}