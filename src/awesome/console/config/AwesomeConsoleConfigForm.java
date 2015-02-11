package awesome.console.config;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;

public class AwesomeConsoleConfigForm {
	public JPanel mainpanel;
	public JCheckBox limitLineMatchingByCheckBox;
	public JFormattedTextField maxLengthTextField;
	public JCheckBox matchLinesLongerThanCheckBox;

	private void createUIComponents() {
		setupLineLimit();
		setupSplitLineIntoChunk();
	}

	private void setupLineLimit() {
		limitLineMatchingByCheckBox = new JCheckBox("limitLineMatchingByCheckBox");
		limitLineMatchingByCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final boolean selected = limitLineMatchingByCheckBox.isSelected();
				maxLengthTextField.setEnabled(selected);
				matchLinesLongerThanCheckBox.setEnabled(selected);
			}
		});

		final DecimalFormat decimalFormat = new DecimalFormat("#####");
		final NumberFormatter formatter = new NumberFormatter(decimalFormat);
		formatter.setMinimum(0);
		formatter.setValueClass(Integer.class);
		maxLengthTextField = new JFormattedTextField(formatter);
		maxLengthTextField.setColumns(5);

		JPopupMenu popup = new JPopupMenu("Defaults");
		maxLengthTextField.setComponentPopupMenu(popup);

		final JMenuItem itm = popup.add("Restore defaults");
		itm.setMnemonic(KeyEvent.VK_R);
		itm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				maxLengthTextField.setText(String.valueOf(AwesomeConsoleConfig.DEFAULT_LINE_MAX_LENGTH));
				maxLengthTextField.setEnabled(true);
				limitLineMatchingByCheckBox.setSelected(AwesomeConsoleConfig.DEFAULT_LIMIT_LINE_LENGTH);
				matchLinesLongerThanCheckBox.setEnabled(true);
			}
		});
	}

	private void setupSplitLineIntoChunk() {
		matchLinesLongerThanCheckBox = new JCheckBox("matchLinesLongerThanCheckBox");
		matchLinesLongerThanCheckBox.setToolTipText("Check this to keep on matching the text of a line longer than the defined limit. Keep in mind: The text will be matched chunk by chunk, so it might miss some links.");
		JPopupMenu popup = new JPopupMenu("Defaults");
		matchLinesLongerThanCheckBox.setComponentPopupMenu(popup);

		final JMenuItem itm = popup.add("Restore defaults");
		itm.setMnemonic(KeyEvent.VK_R);
		itm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				matchLinesLongerThanCheckBox.setSelected(AwesomeConsoleConfig.DEFAULT_SPLIT_ON_LIMIT);
			}
		});
	}
}
