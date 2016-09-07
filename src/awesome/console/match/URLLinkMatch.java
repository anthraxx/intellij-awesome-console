package awesome.console.match;

public class URLLinkMatch {
	public final String match;
	public final int start;
	public final int end;

	public URLLinkMatch(final String match,
						final int start,
						final int end) {
		this.match = match;
		this.start = start;
		this.end = end;
	}
}
