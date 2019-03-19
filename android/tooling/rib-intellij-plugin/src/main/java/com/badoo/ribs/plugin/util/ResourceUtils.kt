package com.badoo.ribs.plugin.util

import java.io.File
import java.net.URLDecoder
import java.util.jar.JarFile
import kotlin.reflect.KClass

/**
 * List directory contents for a resource folder. This is basically a brute-force
 * implementation. Works for regular files and also JARs. Found at
 * http://stackoverflow.com/questions/6247144/how-to-load-a-folder-from-a-jar.
 *
 * @param clazz Any java class that lives in the same place as the resources you want.
 * @param path  Should end with "/", but not start with one.
 */
fun KClass<*>.getResourceListing(path: String): Array<String> {
    val clazz = this.java

    var dirUrl = clazz.classLoader.getResource(path)
    if (dirUrl != null && dirUrl.protocol == "file") {
        // A file path: easy enough
        return File(dirUrl.toURI()).list()
    }

    if (dirUrl == null) {
        // In case of a jar file, we can't actually find a directory. Have to assume the same jar as
        // clazz.
        val me = clazz.name.replace(".", "/") + ".class"
        dirUrl = clazz.classLoader.getResource(me)
    }

    if (dirUrl!!.protocol == "jar") {
        // Strip out only the JAR file.
        val jarPath = dirUrl.path.substring(5, dirUrl.path.indexOf("!"))

        // Gives ALL entries in jar and avoids duplicates.
        val jar = JarFile(URLDecoder.decode(jarPath, "UTF-8"))
        val entries = jar.entries()
        val result = HashSet<String>()

        while (entries.hasMoreElements()) {
            val element = entries.nextElement()
            val name = element.name
            if (name.startsWith(path) && !element.isDirectory) {
                result.add(name)
            }
        }
        return result.toTypedArray()
    }

    throw UnsupportedOperationException("Cannot list files for URL $dirUrl")
}
