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
package com.leshazlewood.scms.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/** @since 0.1 */
public enum Version {
  INSTANCE;

  private static final String BUILD_PROPERTIES = "/io/github/scms/core/build.properties";
  private final String version;
  private final String revision;

  public static String version() {
    return INSTANCE.getVersion();
  }

  public static String revision() {
    return INSTANCE.getRevision();
  }

  Version() {
    try (InputStream resourceAsStream = Version.class.getResourceAsStream(BUILD_PROPERTIES)) {
      Properties props = new Properties();
      props.load(resourceAsStream);

      this.version =
          Optional.ofNullable(props.getProperty("version"))
              .orElseThrow(
                  () ->
                      new IllegalStateException(
                          "Version property missing from file [" + BUILD_PROPERTIES + "]."));
      this.revision =
          Optional.ofNullable(props.getProperty("revision"))
              .orElseThrow(
                  () ->
                      new IllegalStateException(
                          "Version property missing from file [" + BUILD_PROPERTIES + "]."));
    } catch (IOException javaIoIOException) {
      throw new IllegalStateException(
          "Invalid build, file " + BUILD_PROPERTIES + " missing.", javaIoIOException);
    }
  }

  public String getVersion() {
    return this.version;
  }

  public String getRevision() {
    return this.revision;
  }
}
