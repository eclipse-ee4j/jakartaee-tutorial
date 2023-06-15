# Contributing to Jakarta EE Platform

Thanks for your interest in this project.

## Project description

The Eclipse Documentation for Jakarta EE project hosts documentation intended for programmers interested in developing and deploying Jakarta EE applications. It covers the technologies comprising the Jakarta EE platform and describes how to develop applications using Jakarta EE components and deploy them on Jakarta EE runtimes.

Link TBD

## Developer resources

Information regarding source code management, builds, coding standards, and
more.

* [https://projects.eclipse.org/projects/ee4j.jakartaee-platform/developer](https://projects.eclipse.org/projects/ee4j.jakartaee-platform/developer)

The project maintains the following source code repositories

* [jakartaee-tutorial-playbook](https://github.com/virtua-tech/jakartaee-tutorial-playbook)
* [jakartaee-tutorial](https://github.com/virtua-tech/jakartaee-tutorial)
* [jakartaee-tutorial-ui](https://github.com/virtua-tech/jakartaee-tutorial-ui)

### Creating diagrams

Diagrams are located in `src/main/asciidoc/images`.
Use [draw.io](https://draw.io) to create/adjust diagrams.
This tool is primarily chosen for being free to use and the most flexible.
For portability and maintainability, make sure that the diagram is saved/exported into following 3 formats:

- `*.drawio`
- `*.vsdx`
- `*.svg`

The `*.drawio` format ensures being able to reopen exactly the intended diagram in the original tool.
The `*.vsdx` format ensures being able to import the diagram in another tool as this is the most supported format by
various diagramming tools, just in case that draw.io stops to exist in some unpredictable future.
The `*.svg` format is ultimately used to embed the diagram in the tutorial document.

#### Diagram requirements

- Font must be 'Open Sans' conform
  the [Jakarta EE Brand Usage Handbook](https://jakarta.ee/legal/trademark_guidelines/jakarta-ee-branding-guidelines.pdf).
  You can use Google Fonts for this.
  In case you're using draw.io:
    - Wherever you see a 'Font' dropdown, unfold it.
      ![Select font](README/images/drawio-font-1-select-font.png)
    - If there's no 'Open Sans' option, pick 'Custom'.
      ![Select custom](README/images/drawio-font-2-select-custom.png)
    - Choose the 'Google Fonts' option and set the font name to 'Open Sans' and apply.
      ![Set Google Open Sans](README/images/drawio-font-3-set-google-open-sans.png)
    - Type text and verify that the font is Open Sans
      ![Using Google Open Sans](README/images/drawio-font-4-using-google-open-sans.png)

- Color must be one of those defined
  in [Jakarta EE Brand Usage Handbook](https://jakarta.ee/legal/trademark_guidelines/jakarta-ee-branding-guidelines.pdf),
  along with black and white.
    - Primary colors:
        - Blue: `#1B208B`
        - Orange: `#F98200`
        - Black: `#3D3D3D`
        - White: `#FFFFFF`
    - Secondary colors:
        - Yellow: `#FDB940`
        - Grey: `#58595B`
        - Dark blue: `#131660`

### Creating diagrams

Diagrams are located in `src/main/asciidoc/images`.
Use [draw.io](https://draw.io) to create/adjust diagrams.
This tool is primarily chosen for being free to use and the most flexible.
For portability and maintainability, make sure that the diagram is saved/exported into following 3 formats:

- `*.drawio`
- `*.vsdx`
- `*.svg`

The `*.drawio` format ensures being able to reopen exactly the intended diagram in the original tool.
The `*.vsdx` format ensures being able to import the diagram in another tool as this is the most supported format by
various diagramming tools, just in case that draw.io stops to exist in some unpredictable future.
The `*.svg` format is ultimately used to embed the diagram in the tutorial document.

#### Diagram requirements

- Font must be 'Open Sans' conform
  the [Jakarta EE Brand Usage Handbook](https://jakarta.ee/legal/trademark_guidelines/jakarta-ee-branding-guidelines.pdf).
  You can use Google Fonts for this.
  In case you're using draw.io:
    - Wherever you see a 'Font' dropdown, unfold it.
      ![Select font](README/images/drawio-font-1-select-font.png)
    - If there's no 'Open Sans' option, pick 'Custom'.
      ![Select custom](README/images/drawio-font-2-select-custom.png)
    - Choose the 'Google Fonts' option and set the font name to 'Open Sans' and apply.
      ![Set Google Open Sans](README/images/drawio-font-3-set-google-open-sans.png)
    - Type text and verify that the font is Open Sans
      ![Using Google Open Sans](README/images/drawio-font-4-using-google-open-sans.png)

- Color must be one of those defined
  in [Jakarta EE Brand Usage Handbook](https://jakarta.ee/legal/trademark_guidelines/jakarta-ee-branding-guidelines.pdf),
  along with black and white.
    - Primary colors:
        - Blue: `#1B208B`
        - Orange: `#F98200`
        - Black: `#3D3D3D`
        - White: `#FFFFFF`
    - Secondary colors:
        - Yellow: `#FDB940`
        - Grey: `#58595B`
        - Dark blue: `#131660`


## Eclipse Contributor Agreement

Before your contribution can be accepted by the project team contributors must
electronically sign the Eclipse Contributor Agreement (ECA).

* [http://www.eclipse.org/legal/ECA.php](http://www.eclipse.org/legal/ECA.php)

Commits that are provided by non-committers must have a Signed-off-by field in
the footer indicating that the author is aware of the terms by which the
contribution has been provided to the project. The non-committer must
additionally have an Eclipse Foundation account and must have a signed Eclipse
Contributor Agreement (ECA) on file.

For more information, please see the Eclipse Committer Handbook:
[https://www.eclipse.org/projects/handbook/#resources-commit](https://www.eclipse.org/projects/handbook/#resources-commit)

## Contact

Contact the project developers via the project's "dev" list.

* [TBD]
