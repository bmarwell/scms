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

import java.io.File;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParallelProcessor extends DefaultProcessor {

  private static final Logger LOG = LoggerFactory.getLogger(ParallelProcessor.class);

  private List<CompletableFuture<?>> threads;

  private ExecutorService executorService;

  @Override
  public void init() throws UncheckedIOException {
    super.init();
    this.threads = new ArrayList<>();
    checkExecutorService();
  }

  private void checkExecutorService() {
    if (null == executorService) {
      throw new IllegalStateException("ExecutorService not initialized");
    }
  }

  @Override
  public void run() {
    try {
      CompletableFuture<Void> mainThread = CompletableFuture.runAsync(super::run);
      mainThread.join();

      CompletableFuture.allOf(threads.toArray(new CompletableFuture[0])).join();
      List<CompletableFuture<?>> failed =
          threads.stream()
              .filter(CompletableFuture::isCompletedExceptionally)
              .collect(Collectors.toList());
      if (failed.size() > 0) {
        LOG.warn("Failed: [{}]", failed.size());
      }
      executorService.shutdown();
    } catch (CompletionException | CancellationException interrupted) {
      LOG.warn("Interrupted!", interrupted);
      Thread.currentThread().interrupt();
    }
  }

  @Override
  protected void recurse(File dir) {
    Runnable recurseThread = () -> super.recurse(dir);
    CompletableFuture<Void> recurseFuture =
        CompletableFuture.runAsync(recurseThread, executorService);
    threads.add(recurseFuture);
  }

  @Override
  protected void renderFile(File file) {
    Runnable renderThread = () -> super.renderFile(file);
    CompletableFuture<Void> renderFuture =
        CompletableFuture.runAsync(renderThread, executorService);
    threads.add(renderFuture);
  }

  public ExecutorService getExecutorService() {
    return executorService;
  }

  public void setExecutorService(ExecutorService executorService) {
    this.executorService = executorService;
  }

  @Override
  public void close() throws Exception {
    super.close();
    this.executorService.shutdown();
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ParallelProcessor.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("threads=" + threads)
        .add("executorService=" + executorService)
        .toString();
  }
}
