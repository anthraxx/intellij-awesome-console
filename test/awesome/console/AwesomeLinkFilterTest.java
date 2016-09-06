package awesome.console;

import awesome.console.match.FileLinkMatch;
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class AwesomeLinkFilterTest extends CodeInsightFixtureTestCase {
	@Test
	public void testFileWithoutDirectory() {
		assertPathDetection("Just a file: test.txt", "test.txt");
	}

	@Test
	public void testFileContainingSpecialCharsWithoutDirectory() {
		assertPathDetection("Another file: _test.txt", "_test.txt");
		assertPathDetection("Another file: test-me.txt", "test-me.txt");
	}

	@Test
	public void testFileContainingDotsWithoutDirectory() {
		assertPathDetection("Just a file: t.es.t.txt", "t.es.t.txt");
	}

	@Test
	public void testFileInRelativeDirectoryUnixStyle() {
		assertPathDetection("File in a dir (unix style): subdir/test.txt pewpew", "subdir/test.txt");
	}

	@Test
	public void testFileInRelativeDirectoryWindowsStyle() {
		assertPathDetection("File in a dir (Windows style): subdir\\test.txt pewpew", "subdir\\test.txt");
	}

	@Test
	public void testFileInAbsoluteDirectoryWindowsStyleWithDriveLetter() {
		assertPathDetection("File in a absolute dir (Windows style): D:\\subdir\\test.txt pewpew", "D:\\subdir\\test.txt");
	}

	@Test
	public void testFileInAbsoluteDirectoryMixedStyleWithDriveLetter() {
		assertPathDetection("Mixed slashes: D:\\test\\me/test.txt - happens stometimes", "D:\\test\\me/test.txt");
	}

	@Test
	public void testFileInRelativeDirectoryWithLineNumber() {
		assertPathDetection("With line: src/test.js:55", "src/test.js:55", 55);
	}

	@Test
	public void testFileInRelativeDirectoryWithWindowsTypeScriptStyleLineAndColumnNumbers() {
		// Windows, exception from TypeScript compiler
		assertPathDetection("From stack trace: src\\api\\service.ts(29,50)", "src\\api\\service.ts(29,50)", 29, 50);
	}

	@Test
	public void testFileInAbsoluteDirectoryWithWindowsTypeScriptStyleLineAndColumnNumbers() {
		// Windows, exception from TypeScript compiler
		assertPathDetection("From stack trace: D:\\src\\api\\service.ts(29,50)", "D:\\src\\api\\service.ts(29,50)", 29, 50);
	}

	@Test
	public void testFileWithJavaExtensionInAbsoluteDirectoryAndLineNumbersWindowsStyle() {
		assertPathDetection("Windows: d:\\my\\file.java:150", "d:\\my\\file.java:150", 150);
	}

	@Test
	public void testFileWithJavaScriptExtensionInAbsoluteDirectoryWithLineNumbers() {
		// JS exception
		assertPathDetection("bla-bla /home/me/project/run.js:27 something", "/home/me/project/run.js:27", 27);
	}

	@Test
	public void testFileWithJavaStyleExceptionClassAndLineNumbers() {
		// Java exception stack trace
		assertPathDetection("bla-bla at (AwesomeLinkFilter.java:150) something", "AwesomeLinkFilter.java:150", 150);
	}

	@Ignore
	@Test
	public void ignore_testFileWithoutExtensionInRelativeDirectory() {
		// TODO: detect files without extension
		assertPathDetection("No extension: bin/script pewpew", "bin/script");
	}

	private void assertPathDetection(final String line, final String expected) {
		assertPathDetection(line, expected, -1, -1);
	}

	private void assertPathDetection(final String line, final String expected, final int expectedRow) {
		assertPathDetection(line, expected, expectedRow, -1);
	}

	private void assertPathDetection(final String line, final String expected, final int expectedRow, final int expectedCol) {
		AwesomeLinkFilter filter = new AwesomeLinkFilter(myFixture.getProject());

		// Test only detecting file paths - no file existence check
		List<FileLinkMatch> results = filter.detectPaths(line);

		assertEquals("No matches in line \"" + line + "\"", 1, results.size());
		FileLinkMatch info = results.get(0);
		assertEquals(String.format("Expected filter to detect \"%s\" link in \"%s\"", expected, line), expected, info.match);

		if (expectedRow >= 0)
			assertEquals("Expected to capture row number", expectedRow, info.linkedRow);

		if (expectedCol >= 0)
			assertEquals("Expected to capture column number", expectedCol, info.linkedCol);
	}
}
