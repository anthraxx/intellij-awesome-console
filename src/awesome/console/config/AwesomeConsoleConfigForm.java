package awesome.console.config;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AwesomeConsoleConfigForm {
	private final AwesomeConsoleConfig config;
	public JPanel mainpanel;
	private JCheckBox limitLineMatchingByCheckBox;
	private JFormattedTextField maxLengthTextField;

	public AwesomeConsoleConfigForm() {
		config = AwesomeConsoleConfig.getInstance();

		// Init from config
		limitLineMatchingByCheckBox.setSelected(config.LIMIT_LINE_LENGTH);
		maxLengthTextField.setText(String.valueOf(config.LINE_MAX_LENGTH));

		maxLengthTextField.setEditable(limitLineMatchingByCheckBox.isSelected());

		// Listeners
		limitLineMatchingByCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				config.LIMIT_LINE_LENGTH = limitLineMatchingByCheckBox.isSelected();
			}
		});
		maxLengthTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(final DocumentEvent e) {
				try {
					config.LINE_MAX_LENGTH = Integer.parseInt(maxLengthTextField.getText());
				} catch (NumberFormatException nfe) {
					JOptionPane.showMessageDialog(null, "Error: Please enter number bigger than 0", "Error Message", JOptionPane.ERROR_MESSAGE);
				}
			}

			@Override
			public void removeUpdate(final DocumentEvent e) {
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
			}
		});
	}
}
