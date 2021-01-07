# Jakarta EE Tutorial

![ build](https://github.com/eclipse-ee4j/jakartaee-tutorial/workflows/build/badge.svg)

This repository contains the source files that are used to build the
_Jakarta Enterprise Edition (Jakarta EE) Tutorial_. The source files
are authored in [AsciiDoc](http://asciidoc.org/).  AsciiDoc is similar
to markdown but is particularly suited for user documentation.  

Note that the Jakarta EE Tutorial code examples are located in a
separate repository
[eclipse-ee4j/jakartaee-tutorial-examples](https://github.com/eclipse-ee4j/jakartaee-tutorial-examples).

## Contributing
The easiest way to contribute is by opening an issue in this project
that contains feedback and review comments.

The Jakarta EE Tutorial project is also open for contributions and your
help is greatly appreciated. If you have an idea for the tutorial and
want to add a section or update an existing section, then review the
following links:

* [Contribute](CONTRIBUTING.md)
* [Pull Request Acceptance Workflow](src/main/jbake/assets/pr_doc_workflow.md)
* [License](LICENSE.md)

## Building the Jakarta EE Tutorial

The following directions explain how to do local builds of the
tutorial. Note that any changes that are pushed to the master branch
automatically trigger a build of the site files and tutorial sources.
The results are automatically pushed to the gh-pages branch. You can
view the published site
[here](https://eclipse-ee4j.github.io/jakartaee-tutorial).

### Pre-Requisites

- Maven
- JDK8+

Note that manually deploying the site requires password-less
authentication. This is done by exporting your SSH public key into your
GitHub account.

### Build the Site Locally

The site is generated under `target/staging`. Open
`file:///PATH_TO_PROJECT_DIR/target/staging` in a browser to view the
output.

```
mvn generate-resources
```


### Deploy the Site to Github Pages

If you want to manually push a build to the gh-pages branch, use:

```
mvn deploy -Ppublish-site
```
Never commit changes to the *gh-pages* branch directly.

### Produce a Zip File for Download

To produce a zip file containing the generated HTML files, use:

```
mvn package
```

When making a release on GitHub, this zip file should be added to the release.

## Links

- [JBake maven plugin documentation](https://github.com/Blazebit/jbake-maven-plugin)
- [JBake documentation](http://jbake.org/docs/2.5.1)
- [Freemarker documentation](http://freemarker.org/docs)
- [AsciiDoc User Guide](http://asciidoc.org/userguide.html)
- [Asciidoctor quick reference](http://asciidoctor.org/docs/asciidoc-syntax-quick-reference)
