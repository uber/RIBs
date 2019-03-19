package com.badoo.ribs.android.requestcode

import android.os.Bundle
import java.util.HashMap

class RequestCodeRegistry constructor(
    initialState: Bundle?,
    nbLowerBitsForIds: Int = 4
) {
    private val requestCodes: HashMap<Int, String> =
        (initialState?.getSerializable(KEY_REQUEST_CODE_REGISTRY) as? HashMap<Int, String>) ?: hashMapOf()

    private val lowerBitsShift: Int = nbLowerBitsForIds - 0
    private val maskLowerBits = (1 shl lowerBitsShift) - 1
    private val maskHigherBits = 0x0000FFFF - maskLowerBits

    init {
        if (nbLowerBitsForIds < 1) throw IllegalArgumentException("nbLowerBitsForIds can't be less than 1")
        if (nbLowerBitsForIds > 4) throw IllegalArgumentException("nbLowerBitsForIds can't be larger than 4")
    }

    fun generateGroupId(groupName: String): Int {
        var code = (groupName.hashCode() shl lowerBitsShift) and 0x0000FFFF

        while (requestCodes.containsKey(code) && requestCodes[code] != groupName) {
            code += (1 shl lowerBitsShift) and 0x0000FFFF
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

    fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(KEY_REQUEST_CODE_REGISTRY, HashMap(requestCodes))
    }

    companion object {
        private const val KEY_REQUEST_CODE_REGISTRY = "requestCodeRegistry"
    }
}
