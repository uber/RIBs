package com.badoo.mobile.plugin.template

import com.badoo.mobile.plugin.util.getResourceListing
import java.io.InputStream

class JavaResourceProvider : ResourceProvider {

    override fun getResourceAsStream(resourceName: String): InputStream? = this::class.java.getResourceAsStream(resourceName)

    override fun getResourceListing(directory: String): Array<String> = this::class.getResourceListing(directory)

}
