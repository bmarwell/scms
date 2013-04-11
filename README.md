# SCMS

SCMS (Simple Content Management System) is an extremely simple tool that exists to support static (or mostly static)
websites (or dynamic sites that achieve their dynamic nature mostly through JavaScript).

SCMS is extraordinarily simple: given a source directory tree full of Markdown files and html fragments, it
generates a new 1-to-1 directory tree with fully rendered HTML files.  It is expected that you will manage your content
(markdown files, images, etc) using a version control system (for example, git).

When it comes time to preview your changes in HTML or publish your site to web servers,  you run a simple SCMS command
line script to render your web site.  You can take the resulting directory tree and either commit to to version control
or just push it directly to your web servers.  Super easy.

Rendered output is fully customizable via [Velocity templates](http://velocity.apache.org/engine/devel/user-guide.html)

### Build

    $ mvn install
    $ cd cli/target
    $ java -jar scms-cli-<version>-cli.jar

The last command will show a help menu.

## Quickstart

SCMS requires a Java runtime. Ensure you have Java installed by running the following command:

    $ java -version

and you should see something like this:

    java version "1.6.0_43"
    Java(TM) SE Runtime Environment (build 1.6.0_43-b01-447-11M4203)
    Java HotSpot(TM) 64-Bit Server VM (build 20.14-b01-447, mixed mode)

If you don't see this, install Java first.  Ok, moving on...

### Directory Structure

Create a quick directory structure like the following on your file system.  This will be our quick starter project:

    mysite/
        templates/

The `mysite` directory is the root of our quick website project.  The `templates` directory is a sub directory.


### Configuration

Create a `config.scms.groovy` file in the root directory of your static website project with the following contents to get started:

    scms {

        excludes = ['templates/**']

        patterns {
            '**/*.md' {
                template = 'templates/default.vtl'
            }
        }
    }

Here's what the contents mean:

- The `scms` block is the top-level 'wrapper' containing all relevant SCMS configuration.
- The `excludes` property is a list of [Ant-style patterns](http://ant.apache.org/manual/dirtasks.html#patterns). Any
  file discovered matching one of these patterns will not be copied by SCMS to the output directory.
  The above example shows what most people want: to exclude any rendering templates.
- The `patterns` block contains one or more Ant-style patterns, each with their own config block to be applied when
  SCMS encounters a file matching that pattern.

The above `**/*.md` Ant-style pattern example in the `patterns` block ensures that, whenever a Markdown file is
encountered in the `mysite` directory or any of its children directories, SCMS will:

1. Read the Markdown file's contents
2. Convert those contents from Markdown to HTML
3. Merge the resulting HTML with the `templates/default.vtl` [Velocity](http://velocity.apache.org/engine/devel/user-guide.html) template.
   We'll cover templates in just a second.

Now our project structure looks like this:

    mysite/
        templates/
        config.scms.groovy

### HTML Template

Create a `default.vtl` template file in the `templates` subdirectory with the following contents:

    <html>
    <body>

    $content

    </body>
    </html>

This is a [Velocity](http://velocity.apache.org/engine/devel/user-guide.html) template file (the `.vtl` extension
indicates a Velocity Template Language file).  When SCMS runs, any encountered Markdown file will be
rendered to HTML and then that rendered HTML will replace the `$content` placeholder.

Now our project structure looks like this:

    mysite/
        templates/
            default.vtl
        config.scms.groovy

### Our First Content File

Create an `index.md` Markdown file at the root of your sample project with the following contents:

    # Hello World

    This is my first SCMS-rendered site!

Now our project structure looks like this:

    mysite/
        templates/
            default.vtl
        index.md
        config.scms.groovy

### Render your site

Now that we have our config, an HTML template and an initial bit of Markdown content, we can render our site.  Enter the 
project root directory:

    $ cd mysite

Now render your site.  We'll specify `output` as our destination directory, relative to the project root.  SCMS
will render all output to the `output` directory.  You can specify a different directory if you want the output to be
somewhere else.  Run this:

    $ java -jar scms-cli-<VERSION>-cli.jar output

Where `<VERSION>` is replaced by the SCMS version you're using.

After you've run this command, you'll see the following directory structure:

    mysite/
        output/
            index.html
        templates/
            default.vtl
        index.md
        config.scms.groovy

See the new `output` directory with the `index.html` file?  Open it up and this is what you'll see:

    <html>
    <body>

    <h1>Hello World</h1>
    <p>This is my first SCMS-rendered site!</p>

    </body>
    </html>

See how the `index.md` file was converted to the `<h1>` and `<p>` content?  And then see how the
`$content` placeholder in `default.vtl` was replaced with the converted HTML?

This is what SCMS does - sweet and simple.

### How does this work?

Now that you've gotten your feet wet, here's what is going on:

SCMS will produce a 1:1 recursive copy of the site in your source directory (the `mysite` directory above) to your 
specified destination directory (the `output` directory above).  But during that process, it will render all Markdown 
files as HTML files using the specified Velocity template(s) in `config.scms.groovy`.

As you can infer from `config.scms.groovy`, you can have multiple templates: for any file matching a particular pattern,
you can apply a specific template for that file.  Patterns are matched based on a 'first match wins' policy, so more
specific patterns should be defined before more general patterns.  If a file in the source directory tree does not 
match a pattern in `config.scms.groovy`, it is simply copied to the destination directory unchanged.

All that is left now is to learn a little bit of the [Velocity Template Language](http://velocity.apache.org/engine/devel/user-guide.html#Velocity_Template_Language_VTL:_An_Introduction)
so you can write as many `.vtl` Velocity templates as you want to customize the rendered output (look and feel) of your
site.

