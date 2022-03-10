package ru.usatu.pass.utils.mifare

import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

class MifareClassic {
    private val TYPE = 1
    private val VERSION = 1

    private var compressed = false

    private var sectors = arrayListOf<MifareClassicSector>()

    fun read(din: DataInputStream) {
        val type: Int = din.readInt()
        val version: Int = din.readInt()
        compressed = if (type == TYPE && version == VERSION) {
            val count: Int = din.readInt()
            for (i in 0 until count) {
                val sector = MifareClassicSector()
                sector.read(din)
                sectors.add(sector)
            }
            din.readInt() == 1
        } else {
            throw IllegalArgumentException("Unexpected type $type version $version")
        }
    }

    fun write(dout: DataOutputStream) {
        dout.writeInt(TYPE)
        dout.writeInt(VERSION)
        dout.writeInt(sectors.size)
        for (sector in sectors) {
            sector.write(dout)
        }
        dout.writeInt(if (compressed) 1 else 0)
    }

    fun add(mifareClassicSectorData: MifareClassicSector) {
        if (mifareClassicSectorData.index == -1) throw RuntimeException()
        sectors.add(mifareClassicSectorData)
    }

    fun validate(): Boolean {
        for (sector in sectors) {
            if (!sector.validate()) {
                return false
            }
        }
        return true
    }

    override fun toString(): String {
        val buffer = StringBuffer()
        buffer.append("Mifare Classic:\n")
        for (sector in sectors) {
            buffer.append(sector.print())
            buffer.append('\n')
        }
        if (compressed) {
            buffer.append("+ sectors with equal trailer and all zero data")
        }
        return buffer.toString()
    }

    fun isCompressed(): Boolean {
        return compressed
    }

    fun setCompressed(compressed: Boolean) {
        this.compressed = compressed
    }

    operator fun get(index: Int): MifareClassicSector? {
        return sectors[index]
    }

    fun getSectorCount(): Int {
        return sectors.size
    }

    fun getDataSize(): Int {
        var size = 0
        for (sector in sectors) {
            size += sector.getDataSize()
        }
        return size
    }

    fun toByteArray(): ByteArray? {
        val bout = ByteArrayOutputStream()
        val dout = DataOutputStream(bout)
        try {
            write(dout)
        } finally {
            dout.close()
        }
        return bout.toByteArray()
    }
}