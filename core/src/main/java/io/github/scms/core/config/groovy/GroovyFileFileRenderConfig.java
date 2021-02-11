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
package io.github.scms.core.config.groovy;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;

import com.leshazlewood.scms.core.AntPathMatcher;
import com.leshazlewood.scms.core.PatternMatcher;
import io.github.scms.core.config.ScmsFileRenderConfig;
import io.github.scms.utils.FileUtils;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroovyFileFileRenderConfig implements ScmsFileRenderConfig {

  private static final Logger LOG = LoggerFactory.getLogger(GroovyFileFileRenderConfig.class);

  private final PatternMatcher patternMatcher = new AntPathMatcher();

  private final GroovyFileConfig baseConfig;
  private final Map<String, Object> fileConfig = new LinkedHashMap<>();
  private final Map<String, Object> model = new LinkedHashMap<>();
  private final boolean included;
  private final /* nullable */ String outputFileExtension;
  private final /* nullable */ String template;
  private String fileAction;

  public GroovyFileFileRenderConfig(GroovyFileConfig baseConfig, String relPath) {
    this.baseConfig = baseConfig;

    init(relPath);
    this.outputFileExtension =
        (String) Optional.ofNullable(this.fileConfig.get("outputFileExtension")).orElse(null);
    this.template = (String) Optional.ofNullable(this.fileConfig.get("template")).orElse(null);
    this.included = isIncluded(relPath);
  }

  private void initModel(String relPath) {
    final Map<String, Object> model = new LinkedHashMap<>();

    // see if there is a global model config. If so, add this first
    // (so they do not get to specify root or similar):
    Map<String, Object> baseConfigAsMap = getBaseConfigAsMap();
    Object rootModel = baseConfigAsMap.get("model");
    if (rootModel instanceof Map) {
      model.putAll((Map<String, Object>) rootModel);
    }

    // path from relPath relative to sourceDir (how to get to the root directory).
    String relDirPath = FileUtils.relativize(relPath);
    if ("".equals(relDirPath)) {
      // still need to reference it with a separator char in the file:
      relDirPath = ".";
    }
    model.put("root", relDirPath);

    this.model.putAll(model);
  }

  private void init(String relPath) {
    initModel(relPath);

    Map<String, Object> fileSpecificConfig = new ConcurrentHashMap<>(getBaseConfigAsMap());

    Map<String, Object> model = new LinkedHashMap<>();

    String action = "render";

    for (Map.Entry<String, Object> patternEntry : this.baseConfig.getPatterns().entrySet()) {

      String pattern = patternEntry.getKey();

      if (!patternMatcher.matches(pattern, relPath)) {
        continue;
      }

      assert patternEntry.getValue() instanceof Map
          : "Entry for pattern '" + pattern + "' must be a map.";
      Map patternConfig = (Map) patternEntry.getValue();
      fileSpecificConfig.putAll(patternConfig);

      // pattern-specific model
      if (patternConfig.get("model") instanceof Map) {
        model.putAll((Map<? extends String, Object>) patternConfig.get("model"));
      }

      if (patternConfig.containsKey("render")) {
        action = (String) patternConfig.get("render");
      }

      break; // stop pattern iteration - first match always wins
    }

    fileSpecificConfig.put("model", model);

    this.fileConfig.putAll(fileSpecificConfig);
    this.fileAction = action;
    this.model.putAll(model);
  }

  private boolean isIncluded(String relPath) {
    Object excludes = getBaseConfigAsMap().get("excludes");
    if (!(excludes instanceof Collection)) {
      // no exclude configuration == include everything.
      return true;
    }

    for (Object pattern : (Collection<?>) excludes) {
      if (!(pattern instanceof String)) {
        LOG.warn("Ignoring exclude [{}], not a string, ignoring.", pattern);
        continue;
      }
      if (patternMatcher.matches((String) pattern, relPath)) {
        return false;
      }
    }

    return true;
  }

  public boolean isIncluded() {
    return this.included;
  }

  public String getAction() {
    return this.fileAction;
  }

  public Optional<String> getOutputExtension() {
    return Optional.ofNullable(this.outputFileExtension);
  }

  public Optional<String> getTemplate() {
    return Optional.ofNullable(this.template);
  }

  public Map<String, Object> getModel() {
    return unmodifiableMap(this.model);
  }

  private Map<String, Object> asMap(Object maybeMap) {
    try {
      return (Map<String, Object>) maybeMap;
    } catch (ClassCastException classCastException) {
      return emptyMap();
    }
  }

  private Map<String, Object> getBaseConfigAsMap() {
    return this.baseConfig.asMap();
  }
}
