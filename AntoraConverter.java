///opt/homebrew/bin/jbang jbang "$0" "$@" ; exit $?
//DEPS com.github.lalyos:jfiglet:0.0.8

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class AntoraConverter {

    public static String replaceBetween(String str, String start, String end, String prefix, char fromChar, char toChar) {
        StringBuilder result = new StringBuilder();
        int index = 0;
        int startIndex, endIndex;

        while ((startIndex = str.indexOf(start, index)) != -1 && (endIndex = str.indexOf(end, startIndex + start.length())) != -1) {
            result.append(str, index, startIndex + start.length());
            String substring = str.substring(startIndex + start.length(), endIndex);
            String updatedSubstring = substring.replace(fromChar, toChar);
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
        List<File> files = new ArrayList<>();
        String startPath = args.length == 0  ? "." : args[0];
        Files.walk(Paths.get(startPath)).filter(Files::isRegularFile) //.forEach(path -> System.out.println(path.getFileName()));
                .filter(path -> path.toString().endsWith(".adoc"))
                .forEach(path -> files.add(path.toFile()));

        for (File file : files) {
            System.out.println("Evaluating file " + file.getAbsolutePath());
            String content = new String(Files.readAllBytes(file.toPath()));
            int changeCount = 0;

            if (content.contains(("<<"))) {
                System.out.println("=> Updating anchor links");
                String modifiedContent = replaceBetween(content, "<<", ">>", "_",'-', '_');
                Files.write(file.toPath(), modifiedContent.getBytes());
                changeCount++;
            }
            if (content.contains(("[["))) {
                System.out.println("=> Updating anchor names");
                String modifiedContent = replaceBetween(content, "[[", "]]", "_", '-', '_');
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
