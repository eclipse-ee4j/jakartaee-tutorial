import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * CLI application to check + fix links in antora source
 * 
 * Runs on the root folder e.g. src/main/antora and makes a 1st pass to index
 * all anchors. The second pass checks all links, making recommendations on
 * fixes when possible. Setting the "--overwrite" flag will write these
 * recommendations in place to the given file
 * 
 */
public class LinkChecker {

	class Location {
		public String mmodule;
		public String msubmod;
		public String mfile;

		boolean sameFile(Location other) {
			return mmodule.equals(other.mmodule) && msubmod.equals(other.msubmod) && mfile.equals(other.mfile);
		}

		// data structure to refer to a file location
		public Location(String module, String submod, String file) {
			mmodule = module;
			msubmod = submod;
			mfile = file;
		}

		@Override
		public String toString() {
			return mmodule + ":" + msubmod + "/" + msubmod + ".adoc";
		}
	}

	// data structure to hold information about an anchor
	class Reference extends Location {
		public String manchor;
		public String mtext;

		public Reference(String module, String submod, String file, String anchor, String text) {
			super(module, submod, file);
			manchor = anchor;
			mtext = text;
		}

		public String makeLink(boolean local) {
			String newlink = "xref:";
			if (!local) {
				newlink += mmodule + ":";
			}
			newlink += msubmod + "/" + msubmod + ".adoc#" + manchor + "[" + mtext + "]";
			return newlink;
		}
	}

	public static void main(String[] args) throws IOException {

		int argc = args.length;
		if (argc < 2) {
			System.out.println("usage: LinkChecker [--overwrite] basepath module");
			System.out.println("\tbasepath is e.g. src/main/antora");
			System.out.println("\tmodule is the module to be checked e.g. 'cdi'");
			System.out.println("\toverwrite flag will fix links in place");
		} else {
			String checkMod = args[argc - 1];
			String startPath = args[argc - 2];
			File modulesDir = checkExists(new File(startPath + "/modules"));
			boolean overwrite = args[0].equals("--overwrite");

			LinkChecker checker = new LinkChecker();
			checker.run(modulesDir, checkMod, overwrite);
		}
	}

	private Map<String, Reference> refmap = new HashMap<String, Reference>();
	private boolean showIndex = false;

	private void run(File modulesDir, String checkMod, boolean overwrite) throws IOException {
		// index anchors
		File[] modules = modulesDir.listFiles();
		for (File module : modules) {
			String moduleName = module.getName();
			File pagesDir = new File(module, "pages");
			if (pagesDir.exists()) {
				System.out.println("= Index module " + moduleName);
				for (File submod : pagesDir.listFiles()) {
					if (submod.isDirectory()) {
						for (File file : submod.listFiles()) {
							indexFile(moduleName, submod.getName(), file);
						}
					}
				}
			}
		}

		File checkDir = new File(modulesDir, checkMod);
		File checkFilesDir = checkExists(new File(checkDir, "pages"));
		for (File submod : checkFilesDir.listFiles()) {
			File[] files = submod.listFiles();
			Arrays.sort(files);
			for (File file : files) {
				if (file.getName().endsWith(".adoc")) {
					checkFile(checkMod, submod.getName(), file, overwrite);
				}
			}
		}
	}

	private void indexFile(String module, String submod, File file) throws IOException {
		if (showIndex) {
			System.out.println("== Indexing file: " + file.getCanonicalPath());
		}
		String content = new String(Files.readAllBytes(file.toPath()));
		// index headers
		Pattern p = Pattern.compile("(?m)^=+\s*(.+)");
		Matcher m = p.matcher(content);
		while (m.find()) {
			addAnchor(module, submod, file.getName(), anchorKey(m.group(1)), m.group(1));
		}
		// index inline [anchors]
		p = Pattern.compile("\\[\\[([^\\]]+)\\]\\]");
		m = p.matcher(content);
		while (m.find()) {
			addAnchor(module, submod, file.getName(), m.group(1), anchor2title(m.group(1)));
		}
	}

	private void addAnchor(String module, String submod, String filename, String key, String text) {
		Reference reference = new Reference(module, submod, filename, key, text);
		refmap.put(key, reference);
		if (showIndex) {
			System.out.println(key);
		}
	}

	private static String anchorKey(String str) {
		final var strWithPrefix = str.charAt(0) == '_' ? str : "_" + str;
		return strWithPrefix.toLowerCase().replaceAll("[\\s-]", "_").replace(":", "").replaceAll("[@?,&()]", "");
	}

	public static String anchor2title(String str) {
		str = str.replace("_", " ");
		String ret = "";
		String words[] = str.split("\\s");
		for (String word : words) {
			if(word.length() > 0) {
				String first = word.substring(0, 1);
				String rest = word.substring(1);
				ret += first.toUpperCase() + rest + " ";
			}
		}
		return ret.trim();
	}

	private void checkFile(String module, String submod, File file, boolean overwrite) throws IOException {
		System.out.println("== Checking file: " + file.getName());
		var content = new String(Files.readAllBytes(file.toPath()));
		// xref style
		Location myref = new Location(module, submod, file.getName());
		Pattern xrefBasicPattern = Pattern.compile("xref:([^\\[]*)\\[[^\\]]*\\]");
		Pattern xrefFullPattern = Pattern.compile("xref:((([^:]+):)?([^/]+)/(([^\\.]+)\\.adoc)#(.*))\\[([^\\]]*)\\]");
		Matcher m = xrefBasicPattern.matcher(content);
		Map<String, String> replacements = new HashMap<String, String>();
		while (m.find()) {
			Matcher fm = xrefFullPattern.matcher(m.group());
			if (!fm.matches() || !fm.group(4).equals(fm.group(6))) {
				// is it a simple xref:_anchor[]
				Reference ref = resolve(m.group(1));
				if (ref != null) {
					warn("xref link incomplete: " + m.group());
					String newlink = ref.makeLink(ref.mmodule.equals(myref.mmodule));
					replaceLink(replacements, m.group(), newlink);
				} else {
					error("xref could not be resolved: " + m.group());
				}
			} else {
				String anchor = fm.group(7);
				Reference ref = resolve(anchor);
				if (ref != null) {
					// check everything lines up!
					String linkmod = fm.group(3) != null ? fm.group(3) : module;
					Location linkloc = new Location(linkmod, fm.group(4), fm.group(5)); // , anchor, linkmod)
					if (linkloc.mmodule.equals(ref.mmodule) && linkloc.msubmod.equals(ref.msubmod)) {
						if (!ref.mtext.equals(fm.group(8))) {
							warn("mismatch link text: expected '" + ref.mtext + "' in " + fm.group());
						} else {
							ok("xref resolves: " + fm.group(1));
						}
					} else {
						warn("xref location mismatch: " + ref + " vs " + linkloc);
					}
				} else {
					error("failed to resolve xref: " + fm.group(1));
				}
			}
		}
		Pattern linkPattern = Pattern.compile("<<([^>]+)>>");
		m = linkPattern.matcher(content);
		while (m.find()) {
			String anchor = m.group(1);
			Reference ref = resolve(anchor);
			if (ref != null) {
				if (ref.sameFile(myref)) {
					ok("anchor exists: " + anchor);
				} else {
					warn("local anchor '" + anchor + "' from a different file: " + ref.mfile);
					String newlink = ref.makeLink(ref.mmodule.equals(myref.mmodule));
					replaceLink(replacements, m.group(), newlink);
				}
			} else {
				error("failed to resolve local anchor: " + anchor);
			}
		}
		if (overwrite && replacements.size() > 0) {
			for (String str : replacements.keySet()) {
				content = content.replace(str, replacements.get(str));
			}
			Files.write(file.toPath(), content.getBytes());
			System.out.println("\uD83D\uDCBE writing " + file.toPath());
		}
	}

	private void replaceLink(Map<String, String> replacements, String link, String newlink) {
		replacements.put(link, newlink);
		System.out.println("   -> recommended replacement: " + newlink);
	}

	private static void warn(String string) {
		System.out.println("\u270B " + string);
	}

	private static void error(String string) {
		System.out.println("\u274C " + string);
	}

	private static void ok(String string) {
		System.out.println("\u2705 " + string);
	}

	private Reference resolve(String reference) {
		Reference ret = refmap.get(reference);
		if (ret == null) {
			ret = refmap.get(anchorKey(reference));
		}
		return ret;
	}

	private static File checkExists(File file) throws IOException {
		if (!file.exists()) {
			System.err.println("Folder does not exist " + file.getCanonicalPath());
			System.exit(1);
		}
		return file;
	}

}
