package awesome.console;

import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AwesomeProjectFilesIterator implements ContentIterator {
	private final Map<String, List<VirtualFile>> fileCache;
	private final Map<String, List<VirtualFile>> fileBaseCache;
	private final Map<String, List<VirtualFile>> innerClassCache;

	public AwesomeProjectFilesIterator(final Map<String, List<VirtualFile>> fileCache, final Map<String, List<VirtualFile>> fileBaseCache, final Map<String, List<VirtualFile>> innerClassCache) {
		this.fileCache = fileCache;
		this.fileBaseCache = fileBaseCache;
		this.innerClassCache = innerClassCache;
	}

	@Override
	public boolean processFile(final VirtualFile file) {
		if (!file.isDirectory()) {
			try {
				Reader r = new BufferedReader(new InputStreamReader(file.getInputStream()));
				StreamTokenizer st = new StreamTokenizer(r);
				int token = st.nextToken();
				int classCount = 0;
				while (token != StreamTokenizer.TT_EOF) {
					if (st.ttype == StreamTokenizer.TT_WORD && st.sval.equals("class")) {
						++classCount;
						st.nextToken();
						if (classCount > 1 && st.sval != null) {
							if (!innerClassCache.containsKey(st.sval)) {
								innerClassCache.put(st.sval, new ArrayList<VirtualFile>());
							}
							innerClassCache.get(st.sval).add(file);
							System.out.println("Found inner class: " + st.sval);
						}
					}
					token = st.nextToken();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			/** cache for full file name */
			final String filename = file.getName();
			if (!fileCache.containsKey(filename)) {
				fileCache.put(filename, new ArrayList<VirtualFile>());
			}
			fileCache.get(filename).add(file);
			/** cache for basename (fully qualified class names) */
			final String basename = file.getNameWithoutExtension();
			if (0 >= basename.length()) {
				return true;
			}
			if (!fileBaseCache.containsKey(basename)) {
				fileBaseCache.put(basename, new ArrayList<VirtualFile>());
			}
			fileBaseCache.get(basename).add(file);
		}

		return true;
	}
}
