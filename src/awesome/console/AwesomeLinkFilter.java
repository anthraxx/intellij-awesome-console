package awesome.console;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.ide.browsers.OpenUrlHyperlinkInfo;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AwesomeLinkFilter implements Filter {
	private static final Pattern FILE_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z0-9/\\-_\\.]+\\.[a-z]+)(:(\\d+))?(:(\\d+))?");
	private static final Pattern URL_PATTERN = Pattern.compile("((file)?(https?)?://[-_.!~*\\\\'()a-zA-Z0-9;\\\\/?:\\\\@&=+\\\\$,%#]+)");
	private final Project project;

	public AwesomeLinkFilter(Project project) {
		this.project = project;
	}

	public AwesomeLinkFilter() {
		this.project = null;
	}

	@Override
	public Result applyFilter(String s, int endPoint) {
		final List<ResultItem> results = new ArrayList<ResultItem>();

		int startPoint = endPoint - s.length();
		Matcher matcher = URL_PATTERN.matcher(s);

		if (matcher.find()) {
			return new Result(startPoint + matcher.start(),
					startPoint + matcher.end(), new OpenUrlHyperlinkInfo(matcher.group(1)));
		}
		matcher = FILE_PATTERN.matcher(s);
		if (matcher.find()) {
			final List<File> matchingFiles = new ArrayList<File>();
			final List <VirtualFile> matchingVirtualFiles = new ArrayList<VirtualFile>();
			findFile(matchingFiles, matcher.group(1), new File(project.getBasePath()));

			Result result = null;
			for (final File file : matchingFiles) {
				VirtualFile virtualFile = project.getBaseDir().getFileSystem().findFileByPath(file.getPath());
				if (virtualFile == null) {
					continue;
				}
				matchingVirtualFiles.add(virtualFile);
				OpenFileDescriptor fileDescriptor = new OpenFileDescriptor(project,
						virtualFile,
						matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3)) - 1, // line
						matcher.group(5) == null ? 0 : Integer.parseInt(matcher.group(5)) - 1 // column
				);
				result = new Result(startPoint + matcher.start(),
						startPoint + matcher.end(), new OpenFileHyperlinkInfo(fileDescriptor));
				result.setNextAction(NextAction.CONTINUE_FILTERING);
				results.add(result);
			}
			if (null != result) {
				result.setNextAction(NextAction.EXIT);
			}
			return new Result(results);
		}
		return new Result(startPoint, endPoint, null, new TextAttributes());
	}

	private List<File> findFile(List<File> matches, final String name, final File dir) {
		final File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				findFile(matches, name, file);
			} else if (file.getName().contains(name)) {
				matches.add(file);
			}
		}
		return matches;
	}
}
