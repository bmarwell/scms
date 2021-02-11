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
package io.github.scms.core.config.empty;

import static java.util.Collections.emptyMap;

import io.github.scms.core.config.ScmsFileRenderConfig;
import java.util.Map;
import java.util.Optional;

public class EmptyFileRenderConfig implements ScmsFileRenderConfig {

  @Override
  public boolean isIncluded() {
    // add some logic
    return true;
  }

  @Override
  public String getAction() {
    return "render";
  }

  @Override
  public Map<String, Object> getModel() {
    return emptyMap();
  }

  @Override
  public Optional<String> getOutputExtension() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getTemplate() {
    return Optional.empty();
  }
}
