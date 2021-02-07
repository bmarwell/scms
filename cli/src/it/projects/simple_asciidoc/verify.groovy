def file = new File( basedir, 'target/classes/shiro-site/core.html' );

assert file.exists()
assert file.text.contains('<input type="hidden" id="ghEditPage" value="core.adoc"></input>')
