package com.badoo.ribs.plugin.util

import com.badoo.ribs.plugin.generator.Replacements
import org.apache.commons.lang.StringUtils

fun String.applyReplacements(replacements: Replacements): String {
    return StringUtils.replaceEach(this, replacements.fromArray, replacements.toArray)
}
