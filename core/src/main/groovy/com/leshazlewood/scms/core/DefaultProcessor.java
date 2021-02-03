package com.leshazlewood.scms.core;

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import io.github.scms.api.*;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"ChangeToOperator", "GrMethodMayBeStatic"})
public class DefaultProcessor implements Processor {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultProcessor.class);

  public static final String DEFAULT_CONFIG_FILE_NAME = ".scms.groovy";

  private PatternMatcher patternMatcher = new AntPathMatcher();

  private Renderer velocityRenderer;
  private Renderer pegdownRenderer;
  private Set<Renderer> renderers = new HashSet<>();
  private Map<String, Renderer> renderersByExtension;

  private File sourceDir;
  private File destDir;
  private File configFile;
  private String envName;
  private Map<String, Object> config;

  @Override
  public void setSourceDir(File sourceDir) {
    this.sourceDir = sourceDir;
  }

  @Override
  public void setDestDir(File destDir) {
    this.destDir = destDir;
  }

  @Override
  public void setConfigFile(File configFile) {
    this.configFile = configFile;
  }

  @Override
  public void setEnvironment(String envName) {
    this.envName = envName;
  }

  @Override
  public void init() throws UncheckedIOException {

    if (sourceDir == null) {
      sourceDir = new File(System.getProperty("user.dir"));
    }
    ensureDirectory(sourceDir);

    if (destDir == null) {
      destDir = new File(sourceDir, "output");
    }
    ensureDirectory(destDir);

    if (sourceDir.getAbsolutePath().equals(destDir.getAbsolutePath())) {
      throw new IllegalArgumentException("Source directory and destination directory cannot be the same.");
    }


    File templateDir = new File(this.sourceDir, "templates");

    ServiceLoader<FileRendererFactory> serviceLoader = ServiceLoader.load(FileRendererFactory.class);
    Iterator<FileRendererFactory> rendererIterator = serviceLoader.iterator();
    while (rendererIterator.hasNext()) {
      FileRendererFactory fileRendererFactory = rendererIterator.next();
      FileRenderer fileRenderer = fileRendererFactory.withSourceDir(sourceDir).withTemplateDir(templateDir).create();
      renderers.add(fileRenderer);
    }


    renderersByExtension = asRendererMap(renderers);

    if (configFile == null) {
      configFile = new File(sourceDir, DEFAULT_CONFIG_FILE_NAME);
    }

    if (configFile.exists()) {
      if (configFile.isDirectory()) {
        throw new IllegalArgumentException("Expected configuration file " + configFile + " is a directory, not a file.");
      }


      ConfigSlurper slurper;

      if (envName != null && !envName.isEmpty()) {
        slurper = new ConfigSlurper(envName);
      } else {
        slurper = new ConfigSlurper();
      }


      try {
        URL scriptLocation = configFile.toURI().toURL();
        ConfigObject cfgobj = slurper.parse(scriptLocation);
        config = ((Map) (cfgobj.get("scms")));
      } catch (MalformedURLException malformedURLException) {
        throw new IllegalArgumentException(
            "configfile not a valid url: [" + configFile.getAbsolutePath() + "].", malformedURLException);
      }

    } else {
      config = new ConcurrentHashMap<>();
    }
  }

  private static Map<String, Renderer> asRendererMap(Collection<Renderer> c) {

    Map<String, Renderer> m = new LinkedHashMap<String, Renderer>();
    for (Renderer r : c) {
      if (r instanceof FileRenderer) {
        m.put(((FileRenderer) r).getInputFileExtension(), r);
      }
    }


    return m;
  }

  @Override
  public void run() {
    recurse(sourceDir);
  }

  private void ensureDirectory(File f) {
    if (f.exists()) {
      if (!f.isDirectory()) {
        throw new IllegalArgumentException("Specified file " + f + " is not a directory.");
      }
      return;
    }

    if (!f.mkdirs()) {
      throw new UncheckedIOException(new IOException("Unable to create directory " + f));
    }
  }

  private static void ensureFile(File f) {
    if (f.exists()) {
      if (f.isDirectory()) {
        throw new IllegalStateException("File " + f + " was expected to be a file, not a directory.");
      }
      return;
    }

    f.getParentFile().mkdirs();
    try {
      f.createNewFile();
    } catch (IOException javaIoIOException) {
      throw new UncheckedIOException(javaIoIOException);
    }
  }

  private static String getRelativePath(File parent, File child) {
    String dirAbsPath = parent.getAbsolutePath();
    String fileAbsPath = child.getAbsolutePath();
    if (!fileAbsPath.startsWith(dirAbsPath)) {
      throw new IllegalArgumentException("The specified file is not a child or grandchild of the 'parent' argument.");
    }
    String relPath = fileAbsPath.substring(dirAbsPath.length());
    if (relPath.startsWith(File.separator)) {
      relPath = relPath.substring(1);
    }
    return relPath;
  }

  private static String getRelativeDirectoryPath(String path) {
    if (path == null) {
      throw new IllegalArgumentException("path argument cannot be null.");
    }

    int lastSeparatorIndex = path.lastIndexOf(File.separatorChar);
    if (lastSeparatorIndex <= 0) {
      return ".";
    }
    String[] segments = path.split(File.separator);

    StringBuilder sb = new StringBuilder("");
    for (int i = 0; i < segments.length - 1; i++) {
      if (sb.length() > 0) {
        sb.append(File.separatorChar);
      }
      sb.append("..");
    }
    return sb.toString();
  }

  @SuppressWarnings("unchecked")
  private boolean isIncluded(File f) {

    if (f.equals(configFile)) {
      return false;
    }

    String absPath = f.getAbsolutePath();

        /*if (absPath.startsWith(destDir.getAbsolutePath()) ||
                absPath.startsWith(templatesDir.getAbsolutePath()) ||
                f.equals(configFile)) {
            return false;
        }*/

    //only forcefully exclude the destDir (we require this so we avoid infinite recursion).
    //We don't however forcefully exclude the scms config and/or templatesDir in the produced
    //site in case the user wants to allow site viewers to see this information, e.g.
    //an open source community site might want to show their config and templates to help others.

    if (absPath.startsWith(destDir.getAbsolutePath())) {
      return false;
    }

    //now check excluded patterns:
    String relPath = getRelativePath(sourceDir, f);

    if (config.get("excludes") instanceof Collection) {
      for (Object pattern : (Collection) config.get("excludes")) {
        if (!(pattern instanceof String)) {
          LOG.warn("Ignoring exclude [{}], not a string.", pattern);
          continue;
        }
        if (patternMatcher.matches((String) pattern, relPath)) {
          return false;
        }
      }
    }

    return true;
  }

  @SuppressWarnings("unchecked")
  private void recurse(File dir) {

    File[] files = dir.listFiles();
    if (files == null) {
      return;
    }

    for (final File f : files) {

      if (f.equals(destDir) || !isIncluded(f)) {
        continue;
      }

      if (f.isDirectory()) {
        String relPath = getRelativePath(sourceDir, f);
        File copiedDir = new File(destDir, relPath);
        ensureDirectory(copiedDir);
        recurse(f);
      } else {
        try {
          renderFile(f);
        } catch (IOException ioException) {
          throw new UncheckedIOException("Unable to render file " + f + ": " + ioException.getMessage(), ioException);
        }
      }
    }
  }

  private void renderFile(File f) throws IOException {

    String relPath = getRelativePath(sourceDir, f);

    Map<String, Object> config = new ConcurrentHashMap<>(this.config);

    Map<String, Object> model = new LinkedHashMap<String, Object>();

    if (config.containsKey("model") && config.get("model") instanceof Map) {
      model = (Map<String, Object>) config.get("model");
    } else {
      config.put("model", model);
    }

    String relDirPath = getRelativeDirectoryPath(relPath);
    if ("".equals(relDirPath)) {
      //still need to reference it with a separator char in the file:
      relDirPath = ".";
    }


    model.put("root", relDirPath);

    Map<String, Object> patterns = Collections.emptyMap();

    if (config.containsKey("patterns")) {
      assert config.get("patterns") instanceof Map : "scms.patterns must be a map";
      patterns = ((Map<String, Object>) (config.get("patterns")));
    }


    String action = "render";//default unless overridden

    for (Map.Entry<String, Object> patternEntry : patterns.entrySet()) {

      String pattern = patternEntry.getKey();

      if (patternMatcher.matches(pattern, relPath)) {

        assert patternEntry.getValue() instanceof Map : "Entry for pattern \'" + pattern + "\' must be a map.";
        Map patternConfig = (Map) patternEntry.getValue();
        config.putAll(patternConfig);

        //pattern-specific model
        if (patternConfig.get("model") instanceof Map) {
          model.putAll((Map<? extends String, Object>) patternConfig.get("model"));
        }


        if (patternConfig.containsKey("render")) {
          action = (String) patternConfig.get("render");
        }

        break;//stop pattern iteration - first match always wins
      }
    }

    config.put("model", model);

    if (action.equals("skip")) {
      return;

    } else if (action.equals("copy")) {
      File destFile = new File(destDir, relPath);
      ensureFile(destFile);
      copy(f, destFile);
      return;
    }

    //otherwise we need to render:
    Reader content = null;
    String destRelPath = relPath;//assume same unless it is itself a template

    Renderer renderer = getRenderer(config, destRelPath);

    while (renderer != null) {

      String extension = getExtension(destRelPath);
      if (content == null) {
        content = Files.newBufferedReader(f.toPath(), StandardCharsets.UTF_8);
      }

      destRelPath = destRelPath.substring(0, destRelPath.length() - (extension.length() + 1));

      String destExtension = (renderer instanceof FileRenderer) ? ((FileRenderer) renderer).getOutputFileExtension() : extension;

      if (DefaultGroovyMethods.asBoolean(config.get("outputFileExtension"))) {
        destExtension = (String) config.get("outputFileExtension");
      }


      Renderer nextRenderer = getRenderer(config, destRelPath);

      // if this is the last renderer set the extension, otherwise, skip it
      if (nextRenderer == null && !destRelPath.endsWith("." + destExtension)) {
        destRelPath += "." + destExtension;
      }


      content = render(renderer, model, destRelPath, content);
      renderer = nextRenderer;
    }


    if (DefaultGroovyMethods.asBoolean(config.get("template"))) {//a template will be used to render the contents
      String template = (String) config.get("template");
      File templateFile = new File(this.sourceDir, template);
      renderer = getRenderer(template);
      if (DefaultGroovyMethods.asBoolean(renderer)) {
        if (content == null) {
          content = Files.newBufferedReader(f.toPath(), StandardCharsets.UTF_8);
        }

        String contentString = new BufferedReader(content).readLine();
        model.put("content", contentString);
        content = Files.newBufferedReader(templateFile.toPath(), StandardCharsets.UTF_8);
        content = render(renderer, model, destRelPath, content);
      }
    }

    File destFile = new File(destDir, destRelPath);
    ensureFile(destFile);

    if (content != null) {
      //write out the rendered content to the destination file:
      BufferedWriter writer = new BufferedWriter(new FileWriter(destFile));
      copy(content, writer);
      content.close();
      writer.close();
    } else {
      //just copy the file over:
      copy(f, destFile);
    }
  }


  private String getExtension(String path) {
    char ch = '.';
    int i = path.lastIndexOf(ch);
    if (i > 0) {
      return path.substring(i + 1);
    }
    return null;
  }

  public Renderer getRenderer(String path) {
    String extension = getExtension(path);
    return renderersByExtension.get(extension);
  }

  public Renderer getRenderer(Map config, String path) {
    if ("velocity".equals(config.get("renderer"))) {
      return velocityRenderer;
    } else if ("pegdown".equals(config.get("renderer"))) {
      return pegdownRenderer;
    }

    for (Renderer r : renderers) {
      if (r instanceof FileRenderer && ((FileRenderer) r).supports(path)) {
        return r;
      }
    }


    return null;
  }

  public Reader render(Renderer renderer, Map<String, Object> model, String path, Reader reader) throws IOException {
    Resource resource = new DefaultResource(path, reader);
    StringWriter resultWriter = new StringWriter(8192);
    RenderRequest request = new DefaultRenderRequest(model, resource, resultWriter);
    renderer.render(request);
    reader.close();
    resultWriter.close();
    return new StringReader(resultWriter.toString());
  }

  /**
   * Reads all characters from a Reader and writes them to a Writer.
   */
  private static long copy(Reader r, Writer w) throws IOException {
    long nread = 0L;
    char[] buf = new char[4096];
    int n;
    while ((n = r.read(buf)) > 0) {
      w.write(buf, 0, n);
      nread += n;
    }
    return nread;
  }

  private static void copy(File src, File dest) throws IOException {
    Files.copy(src.toPath(), dest.toPath(), LinkOption.NOFOLLOW_LINKS, StandardCopyOption.REPLACE_EXISTING);
  }

  public PatternMatcher getPatternMatcher() {
    return patternMatcher;
  }

  public void setPatternMatcher(PatternMatcher patternMatcher) {
    this.patternMatcher = patternMatcher;
  }

  public Set<Renderer> getRenderers() {
    return renderers;
  }

  public void setRenderers(Set<Renderer> renderers) {
    this.renderers = renderers;
  }

  public Map<String, Renderer> getRenderersByExtension() {
    return renderersByExtension;
  }

  public void setRenderersByExtension(Map<String, Renderer> renderersByExtension) {
    this.renderersByExtension = renderersByExtension;
  }

  public File getSourceDir() {
    return sourceDir;
  }

  public File getDestDir() {
    return destDir;
  }

  public File getConfigFile() {
    return configFile;
  }

  public String getEnvName() {
    return envName;
  }

  public void setEnvName(String envName) {
    this.envName = envName;
  }

  public Map getConfig() {
    return config;
  }

  public void setConfig(Map config) {
    this.config = config;
  }

}
