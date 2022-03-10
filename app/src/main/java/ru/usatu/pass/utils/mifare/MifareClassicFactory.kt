package ru.usatu.pass.utils.mifare

import java.io.ByteArrayInputStream
import java.io.DataInputStream

class MifareClassicFactory {
    fun newInstance(bytes: ByteArray?): MifareClassic {
        val tag = MifareClassic()
        val din = DataInputStream(ByteArrayInputStream(bytes))
        tag.read(din)
        return tag
    }
}