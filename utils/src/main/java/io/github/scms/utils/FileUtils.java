/*
 * Copyright 2021 Les Hazlewood, scms contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.scms.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.StandardCopyOption;
import java.util.StringJoiner;
import java.util.stream.IntStream;

public final class FileUtils {

  private FileUtils() {
    // util class
  }

  public static String getRelativePath(File parent, File child) {
    String dirAbsPath = parent.getAbsolutePath();
    String fileAbsPath = child.getAbsolutePath();
    if (!fileAbsPath.startsWith(dirAbsPath)) {
      throw new IllegalArgumentException(
          "The specified file is not a child or grandchild of the 'parent' argument.");
    }
    String relPath = fileAbsPath.substring(dirAbsPath.length());
    if (relPath.startsWith(File.separator)) {
      relPath = relPath.substring(1);
    }
    return relPath;
  }

  public static String relativize(String filePath) {
    // this will put exactly one './' or '.\\' in front.
    File parentPath = new File(filePath);
    int counter = 0;
    while ((parentPath = parentPath.getParentFile()) != null) {
      if (parentPath.toString().equals(".")) {
        // skip (probably last) '.' current dir
        continue;
      }
      if (parentPath.toString().endsWith(File.separator + ".")) {
        // skip './' and '.\\' entries
        continue;
      }
      counter++;
    }

    if (counter <= 0) {
      return ".";
    }

    StringJoiner stringJoiner = new StringJoiner(File.separator);
    IntStream.of(counter).forEach(__ -> stringJoiner.add(".."));
    return stringJoiner.toString();
  }

  public static void ensureDirectory(File f) {
    if (f.exists()) {
      if (!f.isDirectory()) {
        throw new IllegalArgumentException("Specified file " + f + " is not a directory.");
      }
      return;
    }

    if (!f.mkdirs()) {
      throw new UncheckedIOException(new IOException("Unable to create directory " + f));
    }
  }

  public static void ensureFile(File fileToExist) {
    if (fileToExist.exists()) {
      if (fileToExist.isDirectory()) {
        throw new IllegalStateException(
            "File " + fileToExist + " was expected to be a file, not a directory.");
      }
      return;
    }

    File parentFile = fileToExist.getParentFile();
    parentFile.mkdirs();
    try {
      fileToExist.createNewFile();
    } catch (IOException javaIoIOException) {
      throw new UncheckedIOException(javaIoIOException);
    }
  }

  /** Reads all characters from a Reader and writes them to a Writer. */
  public static long copy(Reader r, Writer w) throws IOException {
    long nread = 0L;
    char[] buf = new char[4096];
    int n;
    while ((n = r.read(buf)) > 0) {
      w.write(buf, 0, n);
      nread += n;
    }
    return nread;
  }

  public static void copy(File src, File dest) throws IOException {
    Files.copy(
        src.toPath(),
        dest.toPath(),
        LinkOption.NOFOLLOW_LINKS,
        StandardCopyOption.REPLACE_EXISTING);
  }

  public static String getExtension(String path) {
    char ch = '.';
    int i = path.lastIndexOf(ch);
    if (i > 0) {
      return path.substring(i + 1);
    }
    return null;
  }
}
