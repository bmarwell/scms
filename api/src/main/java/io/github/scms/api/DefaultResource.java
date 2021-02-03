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
package io.github.scms.api;

import java.io.Reader;

public class DefaultResource implements Resource {

  private final String name;
  private final Reader reader;

  public DefaultResource(String name, Reader reader) {
    this.name = name;
    this.reader = reader;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Reader getReader() {
    return this.reader;
  }
}
