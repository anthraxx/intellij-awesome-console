package awesome.console;

public class IntegrationTest {
	public static void main(final String[] args) {
		System.out.println("Just a file: file1.java");
		System.out.println("Just a file with line num: file1.java:5");
		System.out.println("Just a file with line num and col: file1.java:5:3");
		System.out.println("Just a file with line num and col: file_with.special-chars.js:5:3");
		System.out.println("bla-bla at (AwesomeLinkFilter.java:150) something");
		System.out.println("Just a file with path: integration/file1.java");
		System.out.println("Just a file with path: test/awesome/integration/file1.java");
		System.out.println("Just a file with path: ./test/awesome/integration/file1.java");
		System.out.println("Absolute path: /tmp");
		System.out.println("omfg something: git://xkcd.com/ yay");
		System.out.println("omfg something: http://xkcd.com/ yay");
		System.out.println("omfg something: http://8.8.8.8/ yay");
		System.out.println("omfg something: https://xkcd.com/ yay");
		System.out.println("omfg something: http://xkcd.com yay");
		System.out.println("omfg something: ftp://8.8.8.8:2424 yay");
		System.out.println("omfg something: file:///tmp yay");
		System.out.println("omfg something: ftp://user:password@xkcd.com:1337/some/path yay");
		System.out.println("C:\\Windows\\Temp");
		System.out.println("C:\\Windows/Temp");
		System.out.println("C:/Windows/Temp");
		System.out.println("omfg something: file://C:/Windows yay");
		System.out.println("awesome.console.AwesomeLinkFilter:5");
		System.out.println("awesome.console.AwesomeLinkFilter.java:5");
	}
}
