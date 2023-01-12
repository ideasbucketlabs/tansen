/*
 * This file is part of the Tansen project.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package com.ideasbucket.tansen.util

import java.nio.file.FileSystems
import java.util.Locale

object OSProperties {

    private val directorySeparator = FileSystems.getDefault().separator
    private val isWindows = System.getProperty("os.name").lowercase(Locale.getDefault()).startsWith("windows")

    @JvmStatic
    fun isWindows(): Boolean = isWindows

    @JvmStatic
    fun osDirectorySeparator(): String = directorySeparator
}
