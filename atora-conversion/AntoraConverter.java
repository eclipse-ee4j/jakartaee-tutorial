///opt/homebrew/bin/jbang jbang "$0" "$@" ; exit $?
//DEPS com.github.lalyos:jfiglet:0.0.8

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

class AntoraConverter {

    record PageLocation(String path, String page) {
        public String toString() {
            return path + "/" + page;
        }
    }
    static Map<String, PageLocation> anchorMap = new HashMap<>();

    final static String START_PATH_PREFIX = "src/main/antora/modules/";

    public static String replaceBetween(String str, String start, String end, String prefix, String fromString, String toString,
                                        BiFunction<String, String, String> contentUpdater) {
        StringBuilder result = new StringBuilder();
        int index = 0;
        int startIndex, endIndex;

        while ((startIndex = str.indexOf(start, index)) != -1 && (endIndex = str.indexOf(end, startIndex + start.length())) != -1) {
            result.append(str, index, startIndex + start.length());
            String substring = str.substring(startIndex + start.length(), endIndex);
            String updatedSubstring = substring.replace(fromString, toString);

            // don't add the prefix if nothing has changed
            if (!substring.equals(updatedSubstring)) {
                // special case for anchors
                if (updatedSubstring.startsWith("#"))  {
                    updatedSubstring = updatedSubstring.replaceFirst("#",  "#" + prefix);
                } else {
                    updatedSubstring = prefix + updatedSubstring;
                }
            }
            if (contentUpdater != null) {
                updatedSubstring = contentUpdater.apply(substring, updatedSubstring);
            }
            result.append(updatedSubstring);
            index = endIndex;
        }
        result.append(str, index, str.length());
        return result.toString();
    }

    public static void processInbetween(String str, String start, String end, Consumer<String> updatedContentConsumer) {
        int index = 0;
        int startIndex, endIndex;

        while ((startIndex = str.indexOf(start, index)) != -1 && (endIndex = str.indexOf(end, startIndex + start.length())) != -1) {
            String substring = str.substring(startIndex + start.length(), endIndex);
            updatedContentConsumer.accept(substring);
            index = endIndex;
        }
    }

    public static void main(String... args) throws Exception {
        var dryRun = true;
        System.out.println("\uD83E\uDD16 Antora Converter: Convert raw Asciidoc files to Antora-compatible format");
        System.out.println("Walks through all folders and subfolders from the specified startPath and converts all .adoc files");
        if (dryRun) {
            System.out.println("=> dryRun is enabled; no files will be modified");
        }
        var files = new ArrayList<File>();
        if (args.length == 0) {
            System.out.println("Usage: AntoraConverter.java [startPath]");
            System.out.println("       Where [startPath] is relative to " + START_PATH_PREFIX);
            System.exit(1);
        }
        String startPath = START_PATH_PREFIX + args[0];
        System.out.println("Start path: " + startPath);
        Files.walk(Paths.get(startPath)).filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".adoc"))
                .forEach(path -> files.add(path.toFile()));

        System.out.println("\uD83E\uDD16 Pre-scanning files for anchors, links, etc.");

        for (File file : files) {
            System.out.println("\uD83D\uDD0E Pre-scanning file " + file.getAbsolutePath());
            String content = new String(Files.readAllBytes(file.toPath()));
            System.out.println("=> Processing anchor names");
            processInbetween(content, "[[", "]]", (anchor) -> {
                final var pageLocation = new PageLocation(file.getParentFile().getName(), file.getName());
                anchorMap.put("#" + anchor, pageLocation);
                System.out.printf("  => Stored anchor \"%s\" for %s%n", anchor, pageLocation);
            });
        }

        System.out.println("\uD83E\uDD16 Updating files");
        for (File file : files) {
            System.out.println("\uD83D\uDD0E Evaluating file " + file.getAbsolutePath());
            String content = new String(Files.readAllBytes(file.toPath()));
            int changeCount = 0;

            if (content.contains(("<<"))) {
                System.out.println("=> Updating anchor links");
                String modifiedContent = replaceBetween(content, "<<", ">>", "_", "-", "_", null);
                System.out.println("  => Updating anchor link");
                if (!dryRun) {
                    Files.write(file.toPath(), modifiedContent.getBytes());
                }
                changeCount++;
            }
            if (content.contains(("[["))) {
                System.out.println("=> Updating anchor names");
                String modifiedContent = replaceBetween(content, "[[", "]]", "_", "-", "_", null);
                if (!dryRun) {
                    Files.write(file.toPath(), modifiedContent.getBytes());
                }
                changeCount++;
            }
            if (content.contains(("link:#"))) {
                System.out.println("=> Updating anchor links");
                String modifiedContent = replaceBetween(content, "link:", "[", "_", "-", "_", (originalLink, newLink) -> {
                    var pageLocation = anchorMap.get(originalLink);
                    if (pageLocation == null) {
                        pageLocation = anchorMap.get(newLink);
                    }
                    if (pageLocation == null) {
                        System.out.printf("  => WARNING: No page location found for anchor %s or %s; the file with the anchor may not have been within the startPath%n",
                                originalLink, newLink);
                        return newLink;
                    }
                    // If it's a link to the current page, no need to reference another page.
                    if (pageLocation.page.equalsIgnoreCase(file.getName())) {
                        return newLink;
                    }
                    final var updatedLink = pageLocation + "#" + newLink;
                    System.out.printf("  => Updating anchor link from %s to %s%n", originalLink, updatedLink);
                    return updatedLink;
                });
                if (!dryRun) {
                    Files.write(file.toPath(), modifiedContent.getBytes());
                }
                changeCount++;
            }
            if (content.contains("link:")) {
                System.out.println("=> Converting links to use xrefs");
                String modifiedContent = content.replace("link:", "xref:");
                if (!dryRun) {
                    Files.write(file.toPath(), modifiedContent.getBytes());
                }
                changeCount++;
            }
            if (content.contains("image:")) {
                System.out.println("=> Converting inline image references to block image references and adding module prefix");
                String modifiedContent = content.replace("image:", "image::common:");
                if (!dryRun) {
                    Files.write(file.toPath(), modifiedContent.getBytes());
                }
                changeCount++;
            }
            if (content.contains((".png["))) {
                System.out.println("=> Removing newlines from image captions");
                String modifiedContent = replaceBetween(content, ".png[", "]", "", System.getProperty("line.separator"), " ", null);
                if (!dryRun) {
                    Files.write(file.toPath(), modifiedContent.getBytes());
                }
                changeCount++;
            }
            if (changeCount == 0) {
                System.out.println("=> (no changes necessary)");
            } else {
                System.out.println("=> " + changeCount + " changes made");
            }
        }
    }
}
