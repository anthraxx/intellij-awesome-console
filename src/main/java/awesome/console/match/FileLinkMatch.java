package awesome.console.match;

public class FileLinkMatch {
	public final String match; // Full link match (with additional info, such as row and col)
	public final String path; // Just path - no additional info
	public final int linkedRow;
	public final int linkedCol;
	public final int start;
	public final int end;

	public FileLinkMatch(final String match,
				  final String path,
				  final int start,
				  final int end,
				  final int row,
				  final int col) {
		this.match = match;
		this.path = path;
		this.start = start;
		this.end = end;
		this.linkedRow = row;
		this.linkedCol = col;
	}
}
