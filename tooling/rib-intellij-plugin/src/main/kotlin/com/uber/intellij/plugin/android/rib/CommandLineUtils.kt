/*
 * Copyright (C) 2018-2019. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.intellij.plugin.android.rib

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.LinkedList

/** Some command line utilities. */
public object CommandLineUtils {

  /**
   * Executes `which` for a command.
   *
   * @param project the project.
   * @param command the command to find.
   * @return the path of the command, or null if not found.
   * @throws InterruptedException
   * @throws ExecutionException
   * @throws IOException
   */
  public fun which(project: Project, command: String): String {
    return executeWithLineOutput(project, "which", command).output()[0]
  }

  /**
   * Executes the given `command` appending the given `params` and returns the output.
   *
   * @param project the current project
   * @param command to execute
   * @param params to append
   * @return the process output
   * @throws InterruptedException
   * @throws ExecutionException
   * @throws IOException
   */
  public fun executeWithLineOutput(
    project: Project,
    command: String,
    vararg params: String,
  ): ProcessOutput {
    val commandLine = GeneralCommandLine(command)
    for (p in params) {
      commandLine.addParameter(p)
    }
    commandLine.setWorkDirectory(project.basePath)

    val process = commandLine.createProcess()
    process.waitFor()

    return ProcessOutput(
      consumeInputStream(process.inputStream),
      consumeInputStream(process.errorStream),
    )
  }

  private fun consumeInputStream(inputStream: InputStream?): List<String> {
    val lines = LinkedList<String>()

    if (inputStream == null) {
      return lines
    }

    val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("utf-8")))

    var line: String?
    do {
      line = reader.readLine()
      line?.let { lines.add(line.trim { it <= ' ' }) }
    } while (line != null)
    reader.close()

    return lines
  }

  /** Holder for process output and error stream. */
  public class ProcessOutput(private val output: List<String>, private val error: List<String>) {

    /** Returns the process std output */
    public fun output(): List<String> {
      return output
    }

    /** Returns the process error output */
    public fun error(): List<String> {
      return error
    }
  }
}
