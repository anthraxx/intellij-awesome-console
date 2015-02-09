package awesome.console.config;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class AwesomeConsoleConfigForm {
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
		// formatter.setMaximum(65534);
		formatter.setValueClass(Integer.class);
		maxLengthTextField = new JFormattedTextField(formatter);
		maxLengthTextField.setColumns(5);
	}
}
