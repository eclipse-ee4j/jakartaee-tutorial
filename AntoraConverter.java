///opt/homebrew/bin/jbang jbang "$0" "$@" ; exit $?
//DEPS com.github.lalyos:jfiglet:0.0.8

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class AntoraConverter {

    final static String START_PATH_PREFIX = "src/main/antora/modules/";

    public static String replaceBetween(String str, String start, String end, String prefix, String fromString, String toString) {
        StringBuilder result = new StringBuilder();
        int index = 0;
        int startIndex, endIndex;

        while ((startIndex = str.indexOf(start, index)) != -1 && (endIndex = str.indexOf(end, startIndex + start.length())) != -1) {
            result.append(str, index, startIndex + start.length());
            String substring = str.substring(startIndex + start.length(), endIndex);
            String updatedSubstring = substring.replace(fromString, toString);
            // don't add the prefix if nothing has changed
            if (!substring.equals(updatedSubstring)) {
                result.append(prefix);
                result.append(updatedSubstring);
            } else {
                result.append(substring);
            }
            index = endIndex;
        }
        result.append(str, index, str.length());
        return result.toString();
    }

    public static void main(String... args) throws Exception {
        System.out.println("\uD83E\uDD16 Antora Converter: Convert raw Asciidoc files to Antora-compatible format");
        System.out.println("Walks through all folders and subfolders from the specified startPath and converts all .adoc files");
        List<File> files = new ArrayList<>();
        if (args.length == 0) {
            System.out.println("Usage: AntoraConverter.java [startPath]");
            System.out.println("       Where [startPath] is relative to " + START_PATH_PREFIX);
            System.exit(1);
        }
        String startPath = START_PATH_PREFIX + args[0];
        System.out.println("Start path: " + startPath);
        Files.walk(Paths.get(startPath)).filter(Files::isRegularFile) //.forEach(path -> System.out.println(path.getFileName()));
                .filter(path -> path.toString().endsWith(".adoc"))
                .forEach(path -> files.add(path.toFile()));

        for (File file : files) {
            System.out.println("\uD83D\uDD0E Evaluating file " + file.getAbsolutePath());
            String content = new String(Files.readAllBytes(file.toPath()));
            int changeCount = 0;

            if (content.contains(("<<"))) {
                System.out.println("=> Updating anchor links");
                String modifiedContent = replaceBetween(content, "<<", ">>", "_","-", "_");
                Files.write(file.toPath(), modifiedContent.getBytes());
                changeCount++;
            }
            if (content.contains(("[["))) {
                System.out.println("=> Updating anchor names");
                String modifiedContent = replaceBetween(content, "[[", "]]", "_", "-", "_");
                Files.write(file.toPath(), modifiedContent.getBytes());
                changeCount++;
            }
            if (content.contains("image:")) {
                System.out.println("=> Converting inline image references to block image references and adding module prefix");
                String modifiedContent = content.replace("image:", "image::common:");
                Files.write(file.toPath(), modifiedContent.getBytes());
                changeCount++;
            }
            if (content.contains((".png["))) {
                System.out.println("=> Removing newlines from image captions");
                String modifiedContent = replaceBetween(content, ".png[", "]", "", System.getProperty("line.separator"), " ");
                Files.write(file.toPath(), modifiedContent.getBytes());
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
