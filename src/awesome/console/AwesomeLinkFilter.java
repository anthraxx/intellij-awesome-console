package awesome.console;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.filters.HyperlinkInfoFactory;
import com.intellij.ide.browsers.OpenUrlHyperlinkInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AwesomeLinkFilter implements Filter {
	private static final Pattern FILE_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z0-9/\\-_\\.]+\\.[a-z]+)(:(\\d+))?(:(\\d+))?");
	private static final Pattern URL_PATTERN = Pattern.compile("((((ftp)|(file)|(https?)):/)?/[-_.!~*\\\\'()a-zA-Z0-9;\\\\/?:\\\\@&=+\\\\$,%#]+)");
	private final Map<String, List<File>> fileCache = new HashMap<String, List<File>>();
	private final Project project;

	public AwesomeLinkFilter(final Project project) {
		this.project = project;
		createFileCache(new File(project.getBasePath()));
	}

	@Override
	public Result applyFilter(final String line, final int endPoint) {
		final List<ResultItem> results = new ArrayList<ResultItem>();
		final int startPoint = endPoint - line.length();
		results.addAll(getResultItemsUrl(line, startPoint));
		results.addAll(getResultItemsFile(line, startPoint));
		return new Result(results);
	}

	public List<ResultItem> getResultItemsUrl(final String line, final int startPoint) {
		final List<ResultItem> results = new ArrayList<ResultItem>();
		final Matcher matcher = URL_PATTERN.matcher(line);
		while (matcher.find()) {
			final String match = matcher.group(1);
			final String file = getFileFromUrl(match);
			if (null != file && !new File(file).exists()) {
				continue;
			}
			results.add(
					new Result(
							startPoint + matcher.start(),
							startPoint + matcher.end(),
							new OpenUrlHyperlinkInfo(match))
			);
		}
		return results;
	}

	public String getFileFromUrl(final String url) {
		if (url.startsWith("/")) {
			return url;
		}
		final String fileUrl = "file://";
		if(url.startsWith(fileUrl)) {
			return url.substring(fileUrl.length());
		}
		return null;
	}

	public List<ResultItem> getResultItemsFile(final String line, final int startPoint) {
		final List<ResultItem> results = new ArrayList<ResultItem>();
		final Matcher matcher = FILE_PATTERN.matcher(line);
		while (matcher.find()) {
			final List<VirtualFile> virtualFiles = new ArrayList<VirtualFile>();
			final List<File> matchingFiles = fileCache.get(matcher.group(1));
			if (null == matchingFiles) {
				continue;
			}
			for (final File file : matchingFiles) {
				final VirtualFile virtualFile = project.getBaseDir().getFileSystem().findFileByPath(file.getPath());
				if (virtualFile == null) {
					continue;
				}
				virtualFiles.add(virtualFile);
			}
			if (0 >= virtualFiles.size()) {
				continue;
			}
			final HyperlinkInfo linkInfo = HyperlinkInfoFactory.getInstance().createMultipleFilesHyperlinkInfo(
					virtualFiles,
					matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3)) - 1,
					project
			);
			results.add(
					new Result(
							startPoint + matcher.start(),
							startPoint + matcher.end(),
							linkInfo)
			);
		}
		return results;
	}

	private void createFileCache(final File dir) {
		try {
			Files.walkFileTree(dir.toPath(), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
					final File[] files = dir.toFile().listFiles(new FileFilter() {
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
			});
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
