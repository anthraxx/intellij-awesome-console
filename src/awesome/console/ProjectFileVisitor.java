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

	private Map<String, List<File>> fileCache;

	public ProjectFileVisitor(Map<String, List<File>> fileCache) {
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
			if (!fileCache.containsKey(file.getName())) {
				fileCache.put(file.getName(), new ArrayList<File>());
			}
			fileCache.get(file.getName()).add(file);
		}
		return FileVisitResult.CONTINUE;
	}
}
