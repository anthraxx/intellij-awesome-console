package awesome.console.config;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AwesomeConsoleConfigForm {
	private JButton button1;
	private JRadioButton radioButton1;
	private JRadioButton radioButton2;
	public JPanel mainpanel;
	private JRadioButton radioButton3;
	private JRadioButton radioButton4;
	private JLabel label;

	public AwesomeConsoleConfigForm() {
		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				label.setText("Click!");
			}
		});
	}
}
