/*
 * Copyright (C) 2023. Uber Technologies
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
package com.uber.rib.workers.root.main.workers

import android.app.Application
import com.uber.rib.core.RibDispatchers
import com.uber.rib.core.Worker
import com.uber.rib.core.WorkerScopeProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.coroutines.CoroutineContext

class IoWorker(private val application: Application) : Worker {

  /**
   * This worker is guaranteed to always run on IO. (Independently of Dispatchers passed at
   * WorkerBinder.bind)
   */
  override val coroutineContext: CoroutineContext = RibDispatchers.IO

  private var file: File? = null

  override fun onStart(lifecycle: WorkerScopeProvider) {
    val filePath = application.getExternalFilesDir(null)?.absolutePath + "IoWorker.txt"
    file = File(filePath)
    file?.createFile()
  }

  override fun onStop() {
    file?.delete()
  }

  private fun File.createFile() {
    createNewFile()
    if (exists()) {
      val outputStream: OutputStream = FileOutputStream(file)
      // Simulate big file creation
      for (i in 1..500000) {
        outputStream.write(i)
      }
      outputStream.close()
    }
  }
}
