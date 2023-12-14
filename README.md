### Note: This repository is no longer maintained and the project is now moved. To refer to the new project, follow this link: [Jakarta EE Tutorial](https://github.com/jakartaee/jakartaee-tutorial)
# Jakarta EE Tutorial

This repository contains the source files that are used to build the
_Jakarta Enterprise Edition (Jakarta EE) Tutorial_. 
The source files are authored in [AsciiDoc](https://asciidoc.org/). 
AsciiDoc is similar to markdown but is particularly suited for user documentation. 
The source files are processed and integrated into the Jakarta EE Documentation site using
[Antora](https://antora.org/), 
which is a tool for building documentation sites.

You can always find the most recent build of the Jakarta EE Documentation site here:
https://jakartaee.github.io/jakartaee-documentation/.

## A Note about Images

We keep the source files (VSD) and the published format (SVG)
for images in the `src/main/antora/modules/common/images` folder. 
However, currently all the source file names don't match their corresponding SVG file. 
If you modify a source image, 
please help us out and rename it to match the output image.

Guidelines for generating new images can be found in the [Contributing guide](CONTRIBUTING.md). 

## Building

The contents of this repo are built by
the [jakartaee-tutorial-playbook repository](https://github.com/jakartaee/jakartaee-documentation).
See that repo for details.

## Related Repositories

* [eclipse-ee4j/jakartaee-examples](https://github.com/eclipse-ee4j/jakartaee-examples) - Contains examples used in the tutorial (and additional examples)
* [jakartaee/jakartaee-documentation](https://github.com/jakartaee/jakartaee-documentation) - builds documentation site
* [jakartaee/jakartaee-documentation-ui](https://github.com/jakartaee/jakartaee-documentation-ui) - HTML and CSS assets used for the documentation site look and feel.

## Contributing

This project is open for contributions, and your
help is greatly appreciated. 
The easiest way to contribute is by opening an [issue](https://github.com/jakartaee/jakartaee-tutorial/issues) in this project
that contains feedback and review comments. 

You can also create PRs directly while viewing the published documentation by using the Edit button in the UI.

If you want to keep up with our project planning across all of the repos, see the [project board](https://github.com/orgs/jakartaee/projects/7).

If you'd like to propose changes or additions to the content and/or images,
please read the [Style guide](STYLE_GUIDE.adoc) and  
[Contributing guide](CONTRIBUTING.md) for more information.

