package com.badoo.ribs.plugin.template

class Template(
    val id: String,
    val name: String,
    val modulePackage: String,
    val sourcePackage: String,
    val tokens: List<Token>
) {

    override fun toString(): String = name

}
