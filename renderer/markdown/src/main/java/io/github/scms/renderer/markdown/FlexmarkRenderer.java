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
package io.github.scms.renderer.markdown;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import io.github.scms.api.FileRenderer;
import io.github.scms.api.RenderRequest;
import io.github.scms.api.Resource;
import java.io.IOException;
import java.io.Reader;

public class FlexmarkRenderer implements FileRenderer {

  private final Parser parser;
  private final HtmlRenderer renderer;

  public FlexmarkRenderer(Parser parser, HtmlRenderer renderer) {
    this.parser = parser;
    this.renderer = renderer;
  }

  @Override
  public boolean supports(String filename) {
    return filename != null && (filename.endsWith("md") || filename.endsWith("markdown"));
  }

  @Override
  public String getInputFileExtension() {
    return "md";
  }

  @Override
  public String getOutputFileExtension() {
    return "html";
  }

  @Override
  public void render(RenderRequest request) throws IOException {
    Resource resource = request.getResource();
    Reader inputReader = resource.getReader();
    Document document = parser.parseReader(inputReader);
    renderer.render(document, request.getWriter());
  }
}
