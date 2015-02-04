package awesome.console;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProjectFileVisitor<T extends Path> extends SimpleFileVisitor<T> {

	private final Map<String, List<File>> fileCache;
	private final Map<String, List<File>> fileBaseCache;

	public ProjectFileVisitor(Map<String, List<File>> fileCache, Map<String, List<File>> fileBaseCache) {
		this.fileBaseCache = fileBaseCache;
		this.fileCache = fileCache;
	}

	@Override
	public FileVisitResult preVisitDirectory(final T dir, final BasicFileAttributes attrs) throws IOException {
		final File[] files = dir.toFile().listFiles(new FileFilter(){
			@Override
			public boolean accept(final File file) {
				return file.isFile();
			}
		});

		for (final File file : files) {
			/** cache for full file name */
			final String filename = file.getName();
			if (!fileCache.containsKey(filename)) {
				fileCache.put(filename, new ArrayList<File>());
			}
			fileCache.get(filename).add(file);
			/** cache for basename (full qualified class names) */
			final String basename = filename.contains(".") ? filename.substring(0, filename.lastIndexOf('.')) : filename;
			if (0 >= basename.length()) {
				continue;
			}
			if (!fileBaseCache.containsKey(basename)) {
				fileBaseCache.put(basename, new ArrayList<File>());
			}
			fileBaseCache.get(basename).add(file);
		}
		return FileVisitResult.CONTINUE;
	}
}
