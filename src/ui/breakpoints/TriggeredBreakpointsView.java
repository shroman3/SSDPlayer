package ui.breakpoints;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import general.Consts;

public class TriggeredBreakpointsView extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public TriggeredBreakpointsView() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		setBorder(BorderFactory.createLineBorder(Color.cyan));
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.add(new JLabel("Testing 1..2..3.."));
		mainPanel.add(new JLabel("Testing 1..2..3.."));
		mainPanel.add(new JLabel("Testing 1..2..3.."));
		mainPanel.add(new JLabel("Testing 1..2..3.."));
		mainPanel.add(new JLabel("Testing 1..2..3.."));
		mainPanel.add(new JLabel("Testing 1..2..3.."));
		mainPanel.add(new JLabel("Testing 1..2..3.."));
		mainPanel.add(new JLabel("Testing 1..2..3.."));
		mainPanel.add(new JLabel("Testing 1..2..3.."));
		mainPanel.add(new JLabel("Testing 1..2..3.."));
		mainPanel.add(new JLabel("Testing 1..2..3.."));

		JScrollPane scrollPanel = new JScrollPane(mainPanel);

		JLabel header = new JLabel("Triggered Breakpoints", SwingConstants.CENTER);
		header.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		header.setVerticalAlignment(SwingConstants.TOP);
		header.setFont(Consts.UI.SMALL_FONT);
		header.setMinimumSize(new Dimension(450, 30));
		header.setPreferredSize(new Dimension(450, 30));
		header.setMaximumSize(new Dimension(450, 30));
		
		add(header);
		add(scrollPanel);
		
		setMinimumSize(new Dimension(450, 120));
		setPreferredSize(new Dimension(450, 120));
		setMaximumSize(new Dimension(450, 120));
		
	}
}
