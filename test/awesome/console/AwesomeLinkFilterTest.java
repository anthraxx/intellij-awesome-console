package awesome.console;

import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase;
import org.junit.Test;

import java.util.List;

public class AwesomeLinkFilterTest extends CodeInsightFixtureTestCase {

    @Test
    public void testFilter() {
        testPathDetection("Just a file: test.txt", "test.txt");
        testPathDetection("Another file: _test.txt", "_test.txt");
        testPathDetection("Another file: test-me.txt", "test-me.txt");
        testPathDetection("File in a dir (unix style): subdir/test.txt pewpew", "subdir/test.txt");
        testPathDetection("File in a dir (Windows style): subdir\\test.txt pewpew", "subdir\\test.txt");

        // TODO: detect files without extension
        //testPathDetection("No extension: bin/script pewpew", "subdir/script");

        testPathDetection("With line: src/test.js:55", "src/test.js:55", 55);

        // Windows, exception from TypeScript compiler
        testPathDetection("From stack trace: src\\api\\service.ts(29,50)",
                "src\\api\\service.ts(29,50)", 29, 50);

        // JS exception
        testPathDetection("bla-bla /home/me/project/run.js:27 something",
                "/home/me/project/run.js:27", 27);

        // Java exception stack trace
        testPathDetection("bla-bla at (AwesomeLinkFilter.java:150) something",
                "AwesomeLinkFilter.java:150", 150);

        testPathDetection("Windows: d:\\my\\file.java:150",
                "d:\\my\\file.java:150", 150);

        testPathDetection("Mixed slashes: D:\\test\\me/test.txt - happens stometimes",
                "D:\\test\\me/test.txt");
    }

    private void testPathDetection(String line, String expected) {
        testPathDetection(line, expected, -1, -1);
    }

    private void testPathDetection(String line, String expected, int expectedRow) {
        testPathDetection(line, expected, expectedRow, -1);
    }

    private void testPathDetection(String line, String expected,
                                   int expectedRow, int expectedCol) {
        AwesomeLinkFilter filter = new AwesomeLinkFilter(myFixture.getProject());

        // Test only detecting file paths - no file existence check
        List<AwesomeLinkFilter.LinkMatch> results = filter.detectPaths(line);

        assertEquals("No matches in line \"" + line + "\"", 1, results.size());
        AwesomeLinkFilter.LinkMatch info = results.get(0);
        assertEquals(String.format("Expected filter to detect \"%s\" link in \"%s\"", expected, line),
                expected, info.match);

        if (expectedRow >= 0)
            assertEquals("Expected to capture row number", expectedRow, info.linkedRow);

        if (expectedCol >= 0)
            assertEquals("Expected to capture column number", expectedCol, info.linkedCol);
    }

}
