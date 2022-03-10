package ru.usatu.pass.utils.mifare

import ru.usatu.pass.utils.StringUtils
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.*


class MifareClassicSector {
    var trailerBlockKeyA: ByteArray? = null
    var trailerBlockAccessConditions: ByteArray? = null
    var trailerBlockKeyB: ByteArray? = null

    var index = -1
    val blocks = arrayListOf<ByteArray>()

    fun getSize(): Int {
        var size = 4 + 4
        size += 4
        size += trailerBlockKeyA?.size ?: 0
        size += 4
        size += trailerBlockAccessConditions?.size ?: 0
        size += 4
        size += trailerBlockKeyB?.size ?: 0
        size += 4
        for (block in blocks) {
            size += 4
            size += block.size
        }
        return size
    }

    fun read(din: DataInputStream) {
        val size: Int = din.readInt()
        din.mark(size - 4)
        index = din.readInt()

        val trailerBlockKeyACount: Int = din.readInt()
        if (trailerBlockKeyACount > 0) {
            trailerBlockKeyA = ByteArray(trailerBlockKeyACount)
            din.readFully(trailerBlockKeyA)
        }

        val trailerBlockAccessConditionsCount: Int = din.readInt()
        if (trailerBlockAccessConditionsCount > 0) {
            trailerBlockAccessConditions = ByteArray(trailerBlockAccessConditionsCount)
            din.readFully(trailerBlockAccessConditions)
        }

        val trailerBlockKeyBCount: Int = din.readInt()
        if (trailerBlockKeyBCount > 0) {
            trailerBlockKeyB = ByteArray(trailerBlockKeyBCount)
            din.readFully(trailerBlockKeyB)
        }

        val count: Int = din.readInt()
        for (i in 0 until count) {
            val bufferSize: Int = din.readInt()
            val buffer = ByteArray(bufferSize)
            din.readFully(buffer)
            blocks.add(buffer)
        }
        din.reset()
        din.skip((size - 4).toLong())
    }

    fun write(dout: DataOutputStream) {
        val size = getSize()
        dout.writeInt(size)
        dout.writeInt(index)
        if (trailerBlockKeyA != null) {
            dout.writeInt(trailerBlockKeyA!!.size)
            dout.write(trailerBlockKeyA)
        } else {
            dout.writeInt(0)
        }
        if (trailerBlockAccessConditions != null) {
            dout.writeInt(trailerBlockAccessConditions!!.size)
            dout.write(trailerBlockAccessConditions)
        } else {
            dout.writeInt(0)
        }
        if (trailerBlockKeyB != null) {
            dout.writeInt(trailerBlockKeyB!!.size)
            dout.write(trailerBlockKeyB)
        } else {
            dout.writeInt(0)
        }
        dout.writeInt(blocks.size)
        for (block in blocks) {
            dout.writeInt(block.size)
            dout.write(block)
        }
    }

    fun addBlock(block: ByteArray) {
        blocks.add(block)
    }

    fun hasTrailerBlockKeyA() = trailerBlockKeyA != null

    fun hasTrailerBlockKeyB() = trailerBlockKeyB != null

    fun hasTrailerBlockAccessConditions() = trailerBlockAccessConditions != null

    fun isBlankData(): Boolean {
        for (block in blocks) {
            for (i in block.indices) {
                if (block[i] != 0.toByte()) {
                    return false
                }
            }
        }
        return true
    }

    fun isEqualTrailer(sector: MifareClassicSector) =
        Arrays.equals(
            trailerBlockKeyA,
            sector.trailerBlockKeyA
        ) && Arrays.equals(
            trailerBlockAccessConditions,
            sector.trailerBlockAccessConditions
        ) && Arrays.equals(
            trailerBlockKeyB,
            sector.trailerBlockKeyB
        )

    fun isCompressable(sector: MifareClassicSector) =
        isEqualTrailer(sector) && sector.isBlankData()

    fun validate() =
        hasTrailerBlockKeyA() && hasTrailerBlockKeyB() && hasTrailerBlockAccessConditions()

    private fun hasTrailerAccessConditions() = trailerBlockAccessConditions != null

    fun blockCount(): Int = blocks.size + 1

    fun getDataBlock(index: Int) =
        if (index < blocks.size)
            blocks[index]
        else
            null

    fun getDataSize(): Int {
        var size = 16
        for (block in blocks) {
            size += block.size
        }
        return size
    }

    fun print(): String? {
        val buffer = StringBuffer()
        for (i in blocks.indices) {
            buffer.append(printDataBlock(i))
            buffer.append('\n')
        }
        buffer.append(printTrailerBlock())
        return buffer.toString()
    }

    fun printBlock(index: Int): String? {
        if (index < blocks.size) {
            return printDataBlock(index)
        }
        if (index == blocks.size) {
            return printTrailerBlock()
        }
        throw IllegalArgumentException()
    }

    private fun printDataBlock(index: Int): String? {
        return StringUtils.bytesToHexString(blocks[index])
    }

    fun printTrailerBlock(): String {
        val buffer = StringBuffer()
        if (trailerBlockKeyA != null) {
            buffer.append(StringUtils.bytesToHexString(trailerBlockKeyA))
        } else {
            buffer.append("------------")
        }
        if (trailerBlockAccessConditions != null) {
            buffer.append(StringUtils.bytesToHexString(trailerBlockAccessConditions))
        } else {
            buffer.append("--------")
        }
        if (trailerBlockKeyB != null) {
            buffer.append(StringUtils.bytesToHexString(trailerBlockKeyB))
        } else {
            buffer.append("------------")
        }
        return buffer.toString()
    }
}