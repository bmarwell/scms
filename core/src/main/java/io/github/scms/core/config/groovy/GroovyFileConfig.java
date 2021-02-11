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

import static java.util.Collections.unmodifiableMap;

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import io.github.scms.core.config.ScmsConfig;
import io.github.scms.core.config.ScmsFileRenderConfig;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroovyFileConfig implements ScmsConfig {

  private static final Logger LOG = LoggerFactory.getLogger(GroovyFileConfig.class);

  private final Map<String, Object> baseConfig = new ConcurrentHashMap<>();
  private final Map<String, Object> patterns = new LinkedHashMap<>();
  private final Map<String, Object> model = new LinkedHashMap<>();
  private final URI location;
  private final String envName;

  public GroovyFileConfig(File configFile, String envName) {
    this(configFile.toURI(), envName);
  }

  public GroovyFileConfig(URI configLocation) {
    this(configLocation, null);
  }

  public GroovyFileConfig(File configFile) {
    this(configFile.toURI(), null);
  }

  public GroovyFileConfig(URI configLocation, String envName) {
    this.location = configLocation;
    this.envName = envName;
    init();
  }

  private void init() {
    ConfigSlurper slurper;

    if (envName != null && !envName.isEmpty()) {
      slurper = new ConfigSlurper(envName);
    } else {
      slurper = new ConfigSlurper();
    }

    try {
      URL scriptLocation = location.toURL();
      ConfigObject cfgobj = slurper.parse(scriptLocation);
      if (!(cfgobj.get("scms") instanceof Map)) {
        String message =
            String.format(
                Locale.ENGLISH,
                "scms config is not a map! Make sure your config file %s starts with scms { … }.",
                location);
        throw new IllegalArgumentException(message);
      }
      final Map<String, Object> scmsConfigMap = (Map<String, Object>) (cfgobj.get("scms"));
      baseConfig.putAll(scmsConfigMap);
    } catch (MalformedURLException malformedURLException) {
      throw new IllegalArgumentException(
          "configfile not a valid url: [" + location + "].", malformedURLException);
    }

    Object patterns = baseConfig.get("patterns");
    if (patterns instanceof Map) {
      this.patterns.putAll((Map<String, Object>) patterns);
    }

    Object baseModel = baseConfig.get("model");
    if (baseModel instanceof Map) {
      this.model.putAll((Map<String, Object>) baseModel);
    }
  }

  public Map<String, Object> getBaseModel() {
    return unmodifiableMap(this.model);
  }

  @Override
  public ScmsFileRenderConfig forFile(String relPath) {
    return new GroovyFileFileRenderConfig(this, relPath);
  }

  public Map<String, Object> asMap() {
    return unmodifiableMap(baseConfig);
  }

  public Map<String, Object> getPatterns() {
    return unmodifiableMap(patterns);
  }
}
