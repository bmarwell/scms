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
package io.github.scms.renderer.freemarker;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import io.github.scms.api.FileRenderer;
import io.github.scms.api.FileRendererFactory;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

public class FreemarkerRendererFactory implements FileRendererFactory {

  private File sourceDir;
  private File templateDir;

  @Override
  public FileRendererFactory withSourceDir(File sourceDir) {
    this.sourceDir = sourceDir;
    return this;
  }

  @Override
  public FileRendererFactory withTemplateDir(File templateDir) {
    this.templateDir = templateDir;
    return this;
  }

  @Override
  public FileRenderer create() {
    try {
      Configuration configuration = createConfiguration();
      return new FreemarkerFileRenderer(configuration);
    } catch (IOException ioException) {
      throw new UncheckedIOException(ioException);
    }
  }

  private Configuration createConfiguration() throws IOException {
    // Create your Configuration instance, and specify if up to what FreeMarker
    // version (here 2.3.29) do you want to apply the fixes that are not 100%
    // backward-compatible. See the Configuration JavaDoc for details.
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);

    // Specify the source where the template files come from.
    if (sourceDir != null) {
      cfg.setDirectoryForTemplateLoading(sourceDir);
    }

    // From here we will set the settings recommended for new projects. These
    // aren't the defaults for backward compatibility.

    // Set the preferred charset template files are stored in. UTF-8 is
    // a good choice in most applications:
    cfg.setDefaultEncoding("UTF-8");

    // Sets how errors will appear.
    // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

    // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
    cfg.setLogTemplateExceptions(false);

    // Wrap unchecked exceptions thrown during template processing into TemplateException-s:
    cfg.setWrapUncheckedExceptions(true);

    // Do not fall back to higher scopes when reading a null loop variable:
    cfg.setFallbackOnNullLoopVariable(false);

    return cfg;
  }
}
