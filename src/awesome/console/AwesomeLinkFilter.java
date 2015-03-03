package awesome.console;

import awesome.console.config.AwesomeConsoleConfig;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.filters.HyperlinkInfoFactory;
import com.intellij.ide.browsers.OpenUrlHyperlinkInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AwesomeLinkFilter implements Filter {
	private static final Pattern FILE_PATTERN = Pattern.compile("([a-zA-Z0-9][a-zA-Z0-9/\\-_\\.]*\\.[a-zA-Z0-9\\-_\\.]+)(:(\\d+))?");
	private static final Pattern URL_PATTERN = Pattern.compile("((((ftp)|(file)|(https?)):/)?/[-_.!~*\\\\'()a-zA-Z0-9;\\\\/?:\\\\@&=+\\\\$,%#]+)");
	private final AwesomeConsoleConfig config;
	private final Map<String, List<VirtualFile>> fileCache;
	private final Map<String, List<VirtualFile>> fileBaseCache;
	private final Project project;
	private final List<String> srcRoots;
	private final Matcher fileMatcher;
	private final Matcher urlMatcher;

	public AwesomeLinkFilter(final Project project) {
		this.project = project;
		this.fileCache = new HashMap<>();
		this.fileBaseCache = new HashMap<>();
		srcRoots = getSourceRoots();
		config = AwesomeConsoleConfig.getInstance();
		fileMatcher = FILE_PATTERN.matcher("");
		urlMatcher = URL_PATTERN.matcher("");

		createFileCache();
	}

	@Override
	public Result applyFilter(final String line, final int endPoint) {
		final List<ResultItem> results = new ArrayList<>();
		final int startPoint = endPoint - line.length();
		final List<String> chunks = splitLine(line);
		int offset = 0;
		for (final String chunk : chunks) {
			if (config.SEARCH_URLS) {
				results.addAll(getResultItemsUrl(chunk, startPoint + offset));
			}
			results.addAll(getResultItemsFile(chunk, startPoint + offset));
			offset += chunk.length();
		}
		return new Result(results);
	}

	public List<String> splitLine(final String line) {
		final List<String> chunks = new ArrayList<>();
		final int length = line.length();
		if (!config.LIMIT_LINE_LENGTH || config.LINE_MAX_LENGTH >= length) {
			chunks.add(line);
			return chunks;
		}
		if (!config.SPLIT_ON_LIMIT) {
			chunks.add(line.substring(0, config.LINE_MAX_LENGTH));
			return chunks;
		}
		int offset = 0;
		do {
			final String chunk = line.substring(offset, Math.min(length, offset + config.LINE_MAX_LENGTH));
			chunks.add(chunk);
			offset += config.LINE_MAX_LENGTH;
		} while (offset < length - 1);
		return chunks;
	}

	public List<ResultItem> getResultItemsUrl(final String line, final int startPoint) {
		final List<ResultItem> results = new ArrayList<>();
		urlMatcher.reset(line);
		while (urlMatcher.find()) {
			final String match = urlMatcher.group(1);
			final String file = getFileFromUrl(match);
			if (null != file && !new File(file).exists()) {
				continue;
			}
			results.add(
					new Result(
							startPoint + urlMatcher.start(),
							startPoint + urlMatcher.end(),
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
		if (url.startsWith(fileUrl)) {
			return url.substring(fileUrl.length());
		}
		return null;
	}

	public List<ResultItem> getResultItemsFile(final String line, final int startPoint) {
		final List<ResultItem> results = new ArrayList<>();
		fileMatcher.reset(line);
		while (fileMatcher.find()) {
			final String match = fileMatcher.group(1);
			List<VirtualFile> matchingFiles = fileCache.get(match);
			if (null == matchingFiles) {
				matchingFiles = getResultItemsFileFromBasename(match);
				if (null == matchingFiles || 0 >= matchingFiles.size()) {
					continue;
				}
			}

			if (0 >= matchingFiles.size()) {
				continue;
			}
			final HyperlinkInfo linkInfo = HyperlinkInfoFactory.getInstance().createMultipleFilesHyperlinkInfo(
					matchingFiles,
					fileMatcher.group(3) == null ? 0 : Integer.parseInt(fileMatcher.group(3)) - 1,
					project
			);
			results.add(
					new Result(
							startPoint + fileMatcher.start(),
							startPoint + fileMatcher.end(),
							linkInfo)
			);
		}
		return results;
	}

	public List<VirtualFile> getResultItemsFileFromBasename(final String match) {
		final ArrayList<VirtualFile> matches = new ArrayList<>();
		final char packageSeparator = '.';
		final int index = match.lastIndexOf(packageSeparator);
		if (-1 >= index) {
			return matches;
		}
		final String basename = match.substring(index + 1);
		if (0 >= basename.length()) {
			return matches;
		}
		if (!fileBaseCache.containsKey(basename)) {
			return matches;
		}
		final String path = match.substring(0, index).replace(packageSeparator, File.separatorChar);
		for (final VirtualFile file : fileBaseCache.get(basename)) {
			final VirtualFile parent = file.getParent();
			if (null == parent) {
				continue;
			}
			if (!matchSource(parent.getPath(), path)) {
				continue;
			}
			matches.add(file);
		}
		return matches;
	}

	private void createFileCache() {
		final ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
		final AwesomeCollectingContentIterator awesomeCollectingContentIterator = new AwesomeCollectingContentIterator();
		fileIndex.iterateContent(awesomeCollectingContentIterator);

		for (final VirtualFile file : awesomeCollectingContentIterator.getFiles()) {
			/** cache for full file name */
			final String filename = file.getName();
			if (!fileCache.containsKey(filename)) {
				fileCache.put(filename, new ArrayList<VirtualFile>());
			}
			fileCache.get(filename).add(file);
			/** cache for basename (fully qualified class names) */
			final String basename = file.getNameWithoutExtension();
			if (0 >= basename.length()) {
				continue;
			}
			if (!fileBaseCache.containsKey(basename)) {
				fileBaseCache.put(basename, new ArrayList<VirtualFile>());
			}
			fileBaseCache.get(basename).add(file);
		}
	}

	private List<String> getSourceRoots() {
		final VirtualFile[] contentSourceRoots = ProjectRootManager.getInstance(project).getContentSourceRoots();
		final List<String> roots = new ArrayList<>();
		for (final VirtualFile root : contentSourceRoots) {
			roots.add(root.getPath());
		}
		return roots;
	}

	private boolean matchSource(final String parent, final String path) {
		for (final String srcRoot : srcRoots) {
			if ((srcRoot + File.separatorChar + path).equals(parent)) {
				return true;
			}
		}
		return false;
	}
}
