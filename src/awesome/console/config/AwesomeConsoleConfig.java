package awesome.console.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@State(
		name = "AwesomeConsole File Pattern",
		storages = {
				// TODO: find out why this is not working
				@Storage(id = "IDE config dir", file = StoragePathMacros.APP_CONFIG + "/awesomeconsoleconfig.xml")
		}
)
public class AwesomeConsoleConfig implements PersistentStateComponent<AwesomeConsoleConfig>, Configurable, ApplicationComponent {

	public boolean LIMIT_LINE_LENGTH = true;
	public int LINE_MAX_LENGTH = 1024;

	@Nullable
	@Override
	public AwesomeConsoleConfig getState() {
		System.err.println("getState() was called -> " + LIMIT_LINE_LENGTH + ", " + LINE_MAX_LENGTH);
		return this;
	}

	@Override
	public void loadState(final AwesomeConsoleConfig state) {
		XmlSerializerUtil.copyBean(state, this);
		System.err.println("loadState() was called -> " + LIMIT_LINE_LENGTH + ", " + state.LINE_MAX_LENGTH);
	}

	public static AwesomeConsoleConfig getInstance() {
		return ApplicationManager.getApplication().getComponent(AwesomeConsoleConfig.class);
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
}
