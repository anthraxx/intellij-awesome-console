package awesome.console;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.filters.HyperlinkInfoFactory;
import com.intellij.ide.browsers.OpenUrlHyperlinkInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AwesomeLinkFilter implements Filter {
	private static final Pattern FILE_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z0-9/\\-_\\.]+\\.[a-z]+)(:(\\d+))?(:(\\d+))?");
	private static final Pattern URL_PATTERN = Pattern.compile("((ftp)?(file)?(https?)?://[-_.!~*\\\\'()a-zA-Z0-9;\\\\/?:\\\\@&=+\\\\$,%#]+)");
	private final Project project;

	public AwesomeLinkFilter(Project project) {
		this.project = project;
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
			results.add(
				new Result(
					startPoint + matcher.start(),
					startPoint + matcher.end(),
					new OpenUrlHyperlinkInfo(matcher.group(1)))
			);
		}
		return results;
	}

	public List<ResultItem> getResultItemsFile(final String line, final int startPoint) {
		final List<ResultItem> results = new ArrayList<ResultItem>();
		final Matcher matcher = FILE_PATTERN.matcher(line);
		while (matcher.find()) {
			final List<File> matchingFiles = new ArrayList<File>();
			final List <VirtualFile> virtualFiles = new ArrayList<VirtualFile>();
			findFile(matchingFiles, matcher.group(1), new File(project.getBasePath()));

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

	public List<File> findFile(final List<File> matches, final String name, final File dir) {
		final File[] files = dir.listFiles();
		for (final File file : files) {
			if (file.isDirectory()) {
				findFile(matches, name, file);
				continue;
			}
			if (file.getName().equals(name)) {
				matches.add(file);
			}
		}
		return matches;
	}
}
