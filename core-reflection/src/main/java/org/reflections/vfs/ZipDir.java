/**
 * Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
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
package org.reflections.vfs;

import org.rapidpm.dependencies.core.logger.Logger;
import org.rapidpm.dependencies.core.logger.LoggingService;
import org.reflections.Reflections;
import repacked.com.google.common.collect.AbstractIterator;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;


public class ZipDir implements Vfs.Dir {
  final java.util.zip.ZipFile jarFile;

  public ZipDir(JarFile jarFile) {
    this.jarFile = jarFile;
  }

  public String getPath() {
    return jarFile.getName();
  }

  public Iterable<Vfs.File> getFiles() {
    return () -> new AbstractIterator<Vfs.File>() {
      final Enumeration<? extends ZipEntry> entries = jarFile.entries();

      protected Vfs.File computeNext() {
        while (entries.hasMoreElements()) {
          ZipEntry entry = entries.nextElement();
          if (! entry.isDirectory()) {
            return new ZipFile(ZipDir.this , entry);
          }
        }

        return endOfData();
      }
    };
  }

  public void close() {
    try {
      jarFile.close();
    } catch (IOException e) {
      final LoggingService log = Logger.getLogger(Reflections.class);
      if (log != null) {
        log.warning("Could not close JarFile" , e);
      }
    }
  }

  @Override
  public String toString() {
    return jarFile.getName();
  }
}
