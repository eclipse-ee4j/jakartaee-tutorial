import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Simple app to add a message partial to the top of each chapter
 * 
 * The messages should be created under ROOT/partials
 * 
 */
public class AddMessage {

	public static void main(String[] args) throws IOException {

		int argc = args.length;
		if (argc < 2) {
			System.out.println("usage: AddMessage <partial name> <file root>");
			System.out.println("\tall files under <file root> will have the named partial added");
		} else {
			String partialName = args[0];
			File fileRoot = new File(args[1]);
			new AddMessage().run(partialName, fileRoot);
		}
	}

	private void run(String partialName, File root) throws IOException {
		Path dir = Paths.get(root.getAbsolutePath());
		Files.walk(dir).filter(path -> path.toString().matches(".*[^0-9]{2}.adoc"))
				.filter(path -> !path.endsWith("nav.adoc")).forEach(path -> runFile(path, partialName));
	}

	static private Charset charset = StandardCharsets.UTF_8;

	private void runFile(Path path, String partialName) {
		System.out.println("updating " + path.toString());
		try {
			String content = new String(Files.readAllBytes(path), charset);
			String partial = "include::ROOT:partial\\$" + partialName + ".adoc[]";
			String regex = "^(=\s*.*)";
			content = content.replaceAll(regex, "$1\n\n" + partial);
			Files.write(path, content.getBytes(charset));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
