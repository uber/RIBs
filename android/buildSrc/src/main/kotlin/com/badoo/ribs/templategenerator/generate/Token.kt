package com.badoo.ribs.templategenerator.generate

import java.io.Serializable

class Token(
    val id: String,
    val name: String,
    val sourceValue: String
) : Serializable
