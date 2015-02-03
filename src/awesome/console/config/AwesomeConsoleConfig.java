package awesome.console.config;

import com.intellij.openapi.components.*;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

@State(
		name = "AwesomeConsole File Pattern",
		storages = {
				// TODO: find out why this is not working
				@Storage(id = "IDE config dir", file = StoragePathMacros.APP_CONFIG + "/awesomeconsoleconfig.xml")
//				@Storage(id = "default", file = StoragePathMacros.PROJECT_FILE),
//				@Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/awesomeconsolecfg.xml", scheme = StorageScheme.DIRECTORY_BASED)
		}
)
public class AwesomeConsoleConfig implements PersistentStateComponent<AwesomeConsoleConfig> {
	public String FILE_PATTERN = "([a-zA-Z][a-zA-Z0-9/\\-_\\.]+\\.[a-z]+)(:(\\d+))?(:(\\d+))?";

	@Nullable
	@Override
	public AwesomeConsoleConfig getState() {
		System.err.println("getState() was called -> " + FILE_PATTERN);
		return this;
	}

	@Override
	public void loadState(final AwesomeConsoleConfig state) {
		XmlSerializerUtil.copyBean(state, this);
	}

	public static AwesomeConsoleConfig getInstance() {
		return ServiceManager.getService(AwesomeConsoleConfig.class);
	}
}
