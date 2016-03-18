package awesome.console;

import awesome.console.config.AwesomeConsoleConfig;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.filters.HyperlinkInfoFactory;
import com.intellij.ide.browsers.OpenUrlHyperlinkInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AwesomeLinkFilter implements Filter {
	//private static final Pattern FILE_PATTERN = Pattern.compile("([a-zA-Z0-9][a-zA-Z0-9/\\-_\\.]*\\.[a-zA-Z0-9\\-_\\.]+)((:|(, line ))(\\d+))?");
	private static final Pattern FILE_PATTERN = Pattern.compile(
			"(?<link>(?<path>(?:[a-zA-Z]:\\\\|/)?[a-zA-Z0-9_][a-zA-Z0-9/\\-_\\.\\\\]*\\.[a-zA-Z0-9\\-_\\.]+)" +
			"(?:(?::|, line |\\()(?<row>\\d+)(?:[:,](?<col>\\d+)\\))?)?)" // Optional row and col info
	);

	private static final Pattern URL_PATTERN = Pattern.compile(
			"((((ftp)|(file)|(https?)):/)?/[-_.!~*\\\\'()a-zA-Z0-9;\\\\/?:\\\\@&=+\\\\$,%#]+)"
	);

	private final AwesomeConsoleConfig config;
	private final Map<String, List<VirtualFile>> fileCache;
	private final Map<String, List<VirtualFile>> fileBaseCache;
	private final Project project;
	private final List<String> srcRoots;
	private final Matcher fileMatcher;
	private final Matcher urlMatcher;
	private ProjectRootManager projectRootManager;

	public AwesomeLinkFilter(final Project project) {
		this.project = project;
		this.fileCache = new HashMap<>();
		this.fileBaseCache = new HashMap<>();
		projectRootManager = ProjectRootManager.getInstance(project);
		srcRoots = getSourceRoots();
		config = AwesomeConsoleConfig.getInstance();
		fileMatcher = FILE_PATTERN.matcher("");
		urlMatcher = URL_PATTERN.matcher("");

		createFileCache();
	}

	@Nullable
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
		final HyperlinkInfoFactory hyperlinkInfoFactory = HyperlinkInfoFactory.getInstance();

		List<LinkMatch> matches = detectPaths(line);
		for(LinkMatch match: matches) {
			String path = PathUtil.getFileName(match.path);
			List<VirtualFile> matchingFiles = fileCache.get(path);

			if (null == matchingFiles) {
				matchingFiles = getResultItemsFileFromBasename(path);
				if (null == matchingFiles || 0 >= matchingFiles.size()) {
					continue;
				}
			}

			if (0 >= matchingFiles.size()) {
				continue;
			}

			final HyperlinkInfo linkInfo = hyperlinkInfoFactory.createMultipleFilesHyperlinkInfo(
					matchingFiles,
					match.linkedRow < 0 ? 0 : match.linkedRow - 1,
					project
			);

			results.add(new Result(
					startPoint + match.start,
					startPoint + match.end,
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
		projectRootManager.getFileIndex().iterateContent(
				new AwesomeProjectFilesIterator(fileCache, fileBaseCache));
	}

	private List<String> getSourceRoots() {
		final VirtualFile[] contentSourceRoots = projectRootManager.getContentSourceRoots();
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

	@NotNull
	public List<LinkMatch> detectPaths(String line) {
		List<LinkMatch> results = new LinkedList<>();

		fileMatcher.reset(line);

		while (fileMatcher.find()) {
			String match = fileMatcher.group("link");


			results.add(new LinkMatch(match, fileMatcher.group("path"),
					fileMatcher.start(), fileMatcher.end(),
					fileMatcher.group("row"), fileMatcher.group("col")));
		}

		return results;
	}

	public class LinkMatch {
		public String match; // Full link match (with additional info, such as row and col)
		public String path; // Just path - no additional info
		public int linkedRow;
		public int linkedCol;
		public int start;
		public int end;

		public LinkMatch(String match, String path, int start, int end,
						 @Nullable String row, @Nullable String col) {
			this.match = match;
			this.path = path;
			this.start = start;
			this.end = end;

			if (row != null)
				this.linkedRow = Integer.parseInt(row);

			if (col != null)
				this.linkedCol = Integer.parseInt(col);
		}
	}
}
