package awesome.console;

import com.intellij.openapi.roots.CollectingContentIterator;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AwesomeCollectingContentIterator implements CollectingContentIterator {
	private final List<VirtualFile> files;

	public AwesomeCollectingContentIterator() {
		this.files = new ArrayList<>();
	}

	@NotNull
	@Override
	public List<VirtualFile> getFiles() {
		return files;
	}

	@Override
	public boolean processFile(final VirtualFile fileOrDir) {
		if (!fileOrDir.isDirectory()) {
			files.add(fileOrDir);
		}
		return true;
	}
}
