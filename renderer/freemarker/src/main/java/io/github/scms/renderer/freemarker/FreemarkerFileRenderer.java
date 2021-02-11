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
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.github.scms.api.FileRenderer;
import io.github.scms.api.RenderRequest;
import java.io.IOException;

public class FreemarkerFileRenderer implements FileRenderer {
  private final Configuration configuration;

  public FreemarkerFileRenderer(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public boolean supports(String filename) {
    return filename != null && (filename.endsWith("ftl") || filename.endsWith("ftlh"));
  }

  @Override
  public String getInputFileExtension() {
    return "ftlh";
  }

  @Override
  public String getOutputFileExtension() {
    return "html";
  }

  @Override
  public void render(RenderRequest request) throws IOException {
    String resourceName = request.getResource().getName();

    try {
      Template template =
          new Template(resourceName, request.getResource().getReader(), this.configuration);
      template.process(request.getModel(), request.getWriter());
    } catch (TemplateException templateException) {
      throw new IllegalArgumentException("cannot render file " + resourceName, templateException);
    }
  }
}
