package awesome.console.config;

import com.intellij.openapi.components.*;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;

@State(
		name = "AwesomeConsole File Pattern",
		storages = {
				// TODO: find out why this is not working
				@Storage(id = "IDE config dir", file = StoragePathMacros.APP_CONFIG + "/awesomeconsoleconfig.xml")
//				@Storage(id = "default", file = StoragePathMacros.PROJECT_FILE),
//				@Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/awesomeconsolecfg.xml", scheme = StorageScheme.DIRECTORY_BASED)
		}
)
public class AwesomeConsoleConfig implements PersistentStateComponent<AwesomeConsoleConfig>, Configurable,
		ApplicationComponent, ExportableApplicationComponent {

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
		System.err.println("loadState() was called -> " + state.FILE_PATTERN);
	}

	public static AwesomeConsoleConfig getInstance() {
		return ServiceManager.getService(AwesomeConsoleConfig.class);
	}

	@Nls
	@Override
	public String getDisplayName() {
		return "Awesome Console Settings";
	}

	@Nullable
	@Override
	public String getHelpTopic() {
		return "help topic";
	}

	@Nullable
	@Override
	public JComponent createComponent() {
		return new AwesomeConsoleConfigForm().mainpanel;
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public void apply() throws ConfigurationException {

	}

	@Override
	public void reset() {

	}

	@Override
	public void disposeUIResources() {

	}

	@Override
	public void initComponent() {

	}

	@Override
	public void disposeComponent() {

	}

	@NotNull
	@Override
	public String getComponentName() {
		return "component name";
	}

	@NotNull
	@Override
	public File[] getExportFiles() {
		return new File[0];
	}

	@NotNull
	@Override
	public String getPresentableName() {
		return "presentable name";
	}
}
