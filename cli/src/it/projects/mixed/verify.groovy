def file = new File( basedir, 'target/classes/site/core.html' );

assert file.exists()
assert file.text.contains('<input type="hidden" id="ghEditPage" value="core.adoc.ftlh"></input>')
assert file.text.contains('<a href="architecture.html">Architecture</a>')
