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
package io.github.scms.renderer.asciidoc;

import static java.util.Objects.requireNonNull;

import io.github.scms.api.FileRenderer;
import io.github.scms.api.RenderRequest;
import io.github.scms.api.Resource;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsciiDoctorRenderer implements FileRenderer {

  private static final Logger LOG = LoggerFactory.getLogger(AsciiDoctorRenderer.class);

  private final Asciidoctor asciidoctor;

  private final Options options =
      OptionsBuilder.options().headerFooter(false).safe(SafeMode.SAFE).get();

  public AsciiDoctorRenderer(Asciidoctor asciidoctor) {
    this.asciidoctor = requireNonNull(asciidoctor);
  }

  @Override
  public boolean supports(String filename) {
    return filename != null && (filename.endsWith("adoc") || filename.endsWith("asciidoc"));
  }

  @Override
  public String getInputFileExtension() {
    return "adoc";
  }

  @Override
  public String getOutputFileExtension() {
    return "html";
  }

  @Override
  public void render(RenderRequest request) throws IOException {
    Resource resource = request.getResource();
    LOG.trace("Asciidoc: Converting [{}].", resource.getName());
    Reader reader = resource.getReader();
    Writer writer = request.getWriter();

    asciidoctor.convert(reader, writer, options);
  }
}
