package ui.breakpoints;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import breakpoints.BreakpointBase;
import general.Consts;

public class TriggeredBreakpointsView extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel mMainPanel;
	
	public TriggeredBreakpointsView() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		mMainPanel = new JPanel();
		mMainPanel.setLayout(new BoxLayout(mMainPanel, BoxLayout.Y_AXIS));

		JScrollPane scrollPanel = new JScrollPane(mMainPanel);
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		JLabel header = new JLabel("Triggered Breakpoints", SwingConstants.CENTER);
		header.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		header.setVerticalAlignment(SwingConstants.TOP);
		header.setFont(Consts.UI.SMALL_FONT);
		header.setMinimumSize(new Dimension(450, 25));
		header.setPreferredSize(new Dimension(450, 25));
		header.setMaximumSize(new Dimension(450, 25));
		
		add(header);
		add(scrollPanel);
		
		setMinimumSize(new Dimension(450, 140));
		setPreferredSize(new Dimension(450, 140));
		setMaximumSize(new Dimension(450, 140));
	}
	
	public void updateTriggeredBreakpoints(List<BreakpointBase> breakpoints) {
		mMainPanel.removeAll();
		
		for (BreakpointBase breakpoint : breakpoints) {
			if (breakpoint.isHit()) {
				JLabel bpLabel = new JLabel(breakpoint.getDescription());
				float[] hsbValues = new float[3];
				Color.RGBtoHSB(198, 13, 0, hsbValues);
				bpLabel.setForeground(Color.getHSBColor(hsbValues[0], hsbValues[1], hsbValues[2]));
				bpLabel.setBorder(new EmptyBorder(3, 3, 0, 3));
				mMainPanel.add(bpLabel);
			}
		}
		
		mMainPanel.revalidate();
		mMainPanel.repaint();
	}
}
