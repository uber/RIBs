package com.uber.rib.core.requestcode

import java.lang.IllegalArgumentException

class RequestCodeRegistry(
    nbLowerBitsForIds: Int = 8
) {
    private val lowerBitsShift: Int = nbLowerBitsForIds - 0
    private val maskLowerBits = (1 shl lowerBitsShift) - 1
    private val maskHigherBits = 0xFFFFFFFF.toInt() - maskLowerBits
    private val requestCodes = mutableMapOf<Int, String>()

    init {
        if (nbLowerBitsForIds < 1) throw IllegalArgumentException("nbLowerBitsForIds can't be less than 1")
        if (nbLowerBitsForIds > 31) throw IllegalArgumentException("nbLowerBitsForIds can't be larger than 31")
    }

    fun generateGroupId(groupName: String): Int {
        var code = groupName.hashCode() shl lowerBitsShift

        while (requestCodes.containsKey(code) && requestCodes[code] != groupName) {
            code += 1 shl lowerBitsShift
        }

        requestCodes[code] = groupName

        return code
    }

    fun generateRequestCode(groupName: String, code: Int): Int {
        ensureCodeIsCorrect(code)
        return generateGroupId(groupName) + (code and maskLowerBits)
    }

    private fun ensureCodeIsCorrect(code: Int) {
        if (code < 1 || code != code and maskLowerBits) {
            throw RequestCodeDoesntFitInMask(
                "Requestcode '$code' does not fit requirements. Try 0 < code < $maskLowerBits"
            )
        }
    }

    fun resolveGroupId(code: Int): Int =
        code and maskHigherBits

    fun resolveRequestCode(code: Int): Int =
        code and maskLowerBits

}
