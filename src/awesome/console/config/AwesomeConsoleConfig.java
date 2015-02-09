package awesome.console.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@State(
		name = "Awesome Console Config",
		storages = {
				@Storage(id = "IDE config dir", file = StoragePathMacros.APP_CONFIG + "/awesomeconsoleconfig.xml")
		}
)
public class AwesomeConsoleConfig implements PersistentStateComponent<AwesomeConsoleConfig>, Configurable, ApplicationComponent {

	public boolean LIMIT_LINE_LENGTH = true;
	public int LINE_MAX_LENGTH = 1024;
	@Transient
	private AwesomeConsoleConfigForm form;

	@Nullable
	@Override
	public AwesomeConsoleConfig getState() {
		return this;
	}

	@Override
	public void loadState(final AwesomeConsoleConfig state) {
		XmlSerializerUtil.copyBean(state, this);
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
		form = new AwesomeConsoleConfigForm();
		initFromConfig();
		return form.mainpanel;
	}

	private void initFromConfig() {
		form.limitLineMatchingByCheckBox.setSelected(LIMIT_LINE_LENGTH);
		form.maxLengthTextField.setText(String.valueOf(LINE_MAX_LENGTH));
		form.maxLengthTextField.setEditable(LIMIT_LINE_LENGTH);
	}

	@Override
	public boolean isModified() {
		final String text = form.maxLengthTextField.getText().trim();
		if (text.length() < 1) {
			return true;
		}
		int len = -1;
		try {
			len = Integer.parseInt(text);
		} catch (NumberFormatException nfe) {
			return true;
		}
		return form.limitLineMatchingByCheckBox.isSelected() != LIMIT_LINE_LENGTH
				|| len != LINE_MAX_LENGTH;
	}

	@Override
	public void apply() throws ConfigurationException {
		final String text = form.maxLengthTextField.getText().trim();
		if (text.length() < 1) {
			showErrorDialog();
			return;
		}
		int i = -1;
		try {
			i = Integer.parseInt(text);
		} catch (NumberFormatException nfe) {
			showErrorDialog();
			return;
		}
		if (i < 1) {
			showErrorDialog();
			return;
		}
		LIMIT_LINE_LENGTH = form.limitLineMatchingByCheckBox.isSelected();
		LINE_MAX_LENGTH = i;
		loadState(this);
	}

	private void showErrorDialog() {
		JOptionPane.showMessageDialog(form.mainpanel, "Error: Please enter a positive value.", "Invalid value", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void reset() {
		initFromConfig();
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
