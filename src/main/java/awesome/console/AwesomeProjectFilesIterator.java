package awesome.console;

import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AwesomeProjectFilesIterator implements ContentIterator {
	private final Map<String, List<VirtualFile>> fileCache;
	private final Map<String, List<VirtualFile>> fileBaseCache;

	AwesomeProjectFilesIterator(final Map<String, List<VirtualFile>> fileCache, final Map<String, List<VirtualFile>> fileBaseCache) {
		this.fileCache = fileCache;
		this.fileBaseCache = fileBaseCache;
	}

	@Override
	public boolean processFile(final VirtualFile file) {
		if (file.isDirectory()) {
			return true;
		}

		/* cache for full file name */
		final String filename = file.getName();
		fileCache.computeIfAbsent(filename, (key) -> new ArrayList<>()).add(file);

		/* cache for basename (fully qualified class names) */
		final String basename = file.getNameWithoutExtension();
		if (basename.isEmpty()) {
			return true;
		}
		fileBaseCache.computeIfAbsent(basename, (key) -> new ArrayList<>()).add(file);
		return true;
	}
}
