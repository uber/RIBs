package com.badoo.ribs.plugin.util

import com.badoo.ribs.plugin.generator.Replacements
import com.badoo.ribs.plugin.template.Token

fun Replacements.addTokenReplacements(token: Token, tokenValue: String) {
    getPossibleModifications(token.sourceValue)
        .zip(getPossibleModifications(tokenValue))
        .forEach { (from, to) -> add(from, to) }
}

private fun getPossibleModifications(capitalizedCamelCase: String): List<String> = listOf(
    capitalizedCamelCase,
    capitalizedCamelCase.decapitalize(),
    capitalizedCamelCase.toSnakeCase(),
    capitalizedCamelCase.toPackageName()
)

private fun String.toSnakeCase() = toLowerCaseWithSeparator('_')
private fun String.toPackageName() = toLowerCaseWithSeparator('.')

private fun String.toLowerCaseWithSeparator(separator: Char): String {
    val text = StringBuilder()
    var isFirst = true
    this.forEach {
        if (it.isUpperCase()) {
            if (isFirst) isFirst = false
            else text.append(separator)
            text.append(it.toLowerCase())
        } else {
            text.append(it)
        }
    }
    return text.toString()
}
