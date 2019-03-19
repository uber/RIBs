package com.badoo.ribs.plugin.template

import java.io.InputStream

interface ResourceProvider {

    fun getResourceAsStream(resourceName: String): InputStream?

    fun getResourceListing(directory: String): Array<String>

}
