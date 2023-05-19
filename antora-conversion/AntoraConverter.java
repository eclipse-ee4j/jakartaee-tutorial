///opt/homebrew/bin/jbang jbang "$0" "$@" ; exit $?
//DEPS com.github.lalyos:jfiglet:0.0.8

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

class AntoraConverter {

    record PageLocation(String path, String page) {
        public String getParentPage() {
            return path + ".adoc";
        }
        public String toString() {
            return path + "/" + getParentPage();
        }
    }

    static Map<String, PageLocation> anchorMap = new HashMap<>();

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
                if (updatedSubstring.startsWith("#")) {
                    updatedSubstring = updatedSubstring.replaceFirst("#", "#" + prefix);
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
        System.out.println("\uD83E\uDD16 Antora Converter: Convert raw Asciidoc files to Antora-compatible format");
        System.out.println("Walks through all folders and subfolders from the specified startPath and converts all .adoc files");

        var files = new ArrayList<File>();
        if (args.length == 0) {
            System.out.println("Usage: AntoraConverter.java [startPath] dryRun?");
            System.out.println("       Where [startPath] is the root folder to search (usually a specific Antora module or component folder)");
            System.out.println("       If 'dryRun' is specified, no files will actually be modified, but you will see information about the changes that would take place");
            System.exit(1);
        }
        String startPath = args[0];
        System.out.println("Start path: " + startPath);
        final var dryRun = args.length > 1 && args[1].equalsIgnoreCase("dryrun");
        if (dryRun) {
            System.out.println("=> dryRun is enabled; no files will be modified");
        }
        Files.walk(Paths.get(startPath)).filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".adoc"))
                .forEach(path -> files.add(path.toFile()));

        System.out.println("\uD83E\uDD16 Pre-scanning files for anchors, links, etc.");

        preProcess(files);
        process(files, dryRun);
    }

    private static void preProcess(ArrayList<File> files) throws IOException {
        for (File file : files) {
            System.out.println("\uD83D\uDD0E Pre-scanning file " + file.getAbsolutePath());
            String content = new String(Files.readAllBytes(file.toPath()));
            System.out.println("=> Processing anchor names");
            processInbetween(content, "[[", "]]", (anchor) -> {
                // NOTE: This currently will only work for files within the same module. This will need
                // to be updated for files in different modules.
                final var pageLocation = new PageLocation(file.getParentFile().getName(), file.getName());
                anchorMap.put("#" + anchor, pageLocation);
                System.out.printf("  => Stored anchor \"%s\" for %s%n", anchor, pageLocation);
            });
        }
    }

    private static void process(ArrayList<File> files, boolean dryRun) throws IOException {
        System.out.println("\uD83E\uDD16 Updating files");
        for (File file : files) {
            System.out.println("\uD83D\uDD0E Evaluating file " + file.getAbsolutePath());
            var content = new String(Files.readAllBytes(file.toPath()));
            int changeCount = 0;

            if (content.contains(("<<"))) {
                System.out.println("=> Updating in-page anchor references");
                content = replaceBetween(content, "<<", ">>", "_", "-", "_", null);
                if (!dryRun) {
                    Files.write(file.toPath(), content.getBytes());
                }
                changeCount++;
            }
            if (content.contains(("[["))) {
                System.out.println("=> Updating anchor names");
                content = replaceBetween(content, "[[", "]]", "_", "-", "_", null);
                if (!dryRun) {
                    Files.write(file.toPath(), content.getBytes());
                }
                changeCount++;
            }
            if (content.contains(("link:#"))) {
                System.out.println("=> Updating external anchor references");
                content = replaceBetween(content, "link:", "[", "_", "-", "_", (originalLink, newLink) -> {
                    var pageLocation = anchorMap.get(originalLink);
                    if (pageLocation == null) {
                        pageLocation = anchorMap.get(newLink);
                    }
                    if (pageLocation == null) {
                        System.out.printf("  => WARNING: No page location found for anchor %s or %s; the file with the anchor may not have been within the startPath%n",
                                originalLink, newLink);
                        return newLink;
                    }

                    // If this page name contains the parent's page name, it's either the parent page itself or an included page,
                    // so it's an internal reference.
                    if (file.getName().contains(pageLocation.getParentPage())) {
                        return newLink;
                    }
                    final var updatedLink = pageLocation + newLink;
                    System.out.printf("  => Updating anchor reference from %s to %s%n", originalLink, updatedLink);
                    return updatedLink;
                });
                if (!dryRun) {
                    Files.write(file.toPath(), content.getBytes());
                }
                changeCount++;
            }
            if (content.contains("link:")) {
                System.out.println("=> Converting links to xrefs");
                content = content.replace("link:", "xref:");
                if (!dryRun) {
                    Files.write(file.toPath(), content.getBytes());
                }
                changeCount++;
            }
            if (content.contains("image:") && !content.contains("image:common:")) {
                System.out.println("=> Converting inline image references to block image references and adding module prefix");
                content = content.replace("image:", "image::common:");
                if (!dryRun) {
                    Files.write(file.toPath(), content.getBytes());
                }
                changeCount++;
            }
            if (content.contains((".png["))) {
                System.out.println("=> Removing newlines from image captions");
                content = replaceBetween(content, ".png[", "]", "", System.getProperty("line.separator"), " ", null);
                if (!dryRun) {
                    Files.write(file.toPath(), content.getBytes());
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
