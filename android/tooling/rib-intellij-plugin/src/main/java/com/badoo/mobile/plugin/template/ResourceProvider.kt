package com.badoo.mobile.plugin.template

import com.badoo.mobile.plugin.util.getResourceListing
import java.io.InputStream

class ResourceProvider {

    fun getResourceAsStream(resourceName: String): InputStream? = this::class.java.getResourceAsStream(resourceName)

    fun getResourceListing(directory: String): Array<String> = this::class.getResourceListing(directory)

}
