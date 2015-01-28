package awesome.console;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.OpenFileHyperlinkInfo;
import com.intellij.ide.browsers.OpenUrlHyperlinkInfo;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AwesomeLinkFilter implements Filter {
	private static final Pattern FILE_PATTERN = Pattern.compile("([a-zA-Z][a-zA-Z0-9/\\-_\\.]+\\.[a-z]+)(:(\\d+))?(:(\\d+))?");
	private static final Pattern URL_PATTERN = Pattern.compile("((file)?(https?)?://[-_.!~*\\\\'()a-zA-Z0-9;\\\\/?:\\\\@&=+\\\\$,%#]+)");
	private final Project project;

	public AwesomeLinkFilter(Project project) {
		this.project = project;
	}

	@Override
	public Result applyFilter(String s, int endPoint) {
		int startPoint = endPoint - s.length();
		Matcher matcher = URL_PATTERN.matcher(s);
		if (matcher.find()) {
			return new Result(startPoint + matcher.start(),
					startPoint + matcher.end(), new OpenUrlHyperlinkInfo(matcher.group(1)));
		} else {
			matcher = FILE_PATTERN.matcher(s);
			if (matcher.find()) {

				final File file1 = findFile(matcher.group(1), new File(project.getBasePath()));
				if (file1 == null) {
					return null;
				}
				VirtualFile file = project.getBaseDir().getFileSystem().findFileByPath(file1.getPath());
				if (file != null) {
					OpenFileDescriptor fileDescriptor = new OpenFileDescriptor(project,
							file,
							matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3)) - 1, // line
							matcher.group(5) == null ? 0 : Integer.parseInt(matcher.group(5)) - 1 // column
					);

					return new Result(startPoint + matcher.start(),
							startPoint + matcher.end(), new OpenFileHyperlinkInfo(fileDescriptor));
				}
			}
		}
		return new Result(startPoint, endPoint, null, new TextAttributes());
	}

	private File findFile(final String name, final File dir) {
		final File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				final File file1 = findFile(name, file);
				if (file1 != null) {
					return file1;
				}
			} else if (file.getName().contains(name)) {
				return file;
			}
		}
		return null;
	}
}
