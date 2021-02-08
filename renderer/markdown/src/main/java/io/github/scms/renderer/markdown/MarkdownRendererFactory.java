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

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.github.scms.api.FileRenderer;
import io.github.scms.api.FileRendererFactory;
import java.io.File;
import java.util.Arrays;

public class MarkdownRendererFactory implements FileRendererFactory {

  @Override
  public FileRendererFactory withSourceDir(File sourceDir) {
    return this;
  }

  @Override
  public FileRendererFactory withTemplateDir(File templateDir) {
    return this;
  }

  @Override
  public FileRenderer create() {
    MutableDataSet options = new MutableDataSet();
    options.set(HtmlRenderer.GENERATE_HEADER_ID, true);
    options.set(HtmlRenderer.RENDER_HEADER_ID, true);
    options.set(
        Parser.EXTENSIONS,
        Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

    Parser parser = Parser.builder(options).build();
    HtmlRenderer renderer = HtmlRenderer.builder(options).build();

    return new FlexmarkRenderer(parser, renderer);
  }
}
