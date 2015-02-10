package awesome.console.config;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

public class AwesomeConsoleConfigForm {
	public static final String DEFAULT_MAX_LENGTH = "1024";
	public static final boolean DEFAULT_LIMIT = true;

	public JPanel mainpanel;
	public JCheckBox limitLineMatchingByCheckBox;
	public JFormattedTextField maxLengthTextField;

	public AwesomeConsoleConfigForm() {
		limitLineMatchingByCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				maxLengthTextField.setEditable(limitLineMatchingByCheckBox.isSelected());
			}
		});
	}

	private void createUIComponents() {
		final DecimalFormat decimalFormat = new DecimalFormat("#####");
		final NumberFormatter formatter = new NumberFormatter(decimalFormat);
		formatter.setMinimum(0);
		formatter.setValueClass(Integer.class);
		maxLengthTextField = new JFormattedTextField(formatter);
		maxLengthTextField.setColumns(5);

		JPopupMenu popup = new JPopupMenu("Defaults");
		maxLengthTextField.add(popup);
		maxLengthTextField.setComponentPopupMenu(popup);

		final JMenuItem itm = popup.add("Restore defaults");
		itm.setMnemonic(KeyEvent.VK_R);
		itm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				maxLengthTextField.setText(DEFAULT_MAX_LENGTH);
				limitLineMatchingByCheckBox.setSelected(DEFAULT_LIMIT);
			}
		});

	}
}
