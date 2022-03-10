package ru.usatu.pass.utils

import java.util.*

object StringUtils {
    fun hexStringToByteArray(s: String): ByteArray? {
        require(s.length % 2 == 0) { "Bad input string: $s" }
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                    + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    fun bytesToHexString(bytes: ByteArray?) = bytesToHexString(bytes!!, true)

    fun bytesToHexString(bytes: ByteArray, space: Boolean): String {
        val sb = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            sb.append(String.format("%02x", b))
            if (space) {
                sb.append(' ')
            }
        }
        return sb.toString().trim { it <= ' ' }.uppercase(Locale.getDefault())
    }

}