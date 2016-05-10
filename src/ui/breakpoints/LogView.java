package ui.breakpoints;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import general.Consts;

public class LogView extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel mMainPanel;
	private JScrollPane mScrollPanel;
	
	public LogView() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		mMainPanel = new JPanel();
		mMainPanel.setLayout(new BoxLayout(mMainPanel, BoxLayout.Y_AXIS));
		mMainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		mScrollPanel = new JScrollPane(mMainPanel);
		mScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		JLabel header = new JLabel("Message Log", SwingConstants.CENTER);
		header.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		header.setVerticalAlignment(SwingConstants.TOP);
		header.setFont(Consts.UI.SMALL_FONT);
		header.setMinimumSize(new Dimension(320, 25));
		header.setPreferredSize(new Dimension(320, 25));
		header.setMaximumSize(new Dimension(320, 25));
		
		add(header);
		add(mScrollPanel);
		
		setMinimumSize(new Dimension(320, 140));
		setPreferredSize(new Dimension(320, 140));
		setMaximumSize(new Dimension(320, 140));
	}
	
	public void log(String message) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		
		JLabel timeLabel = new JLabel(sdf.format(cal.getTime()) + " ");
		Font timeFont = new Font(Consts.UI.SMALLER_FONT.getFontName(), Font.ITALIC, Consts.UI.SMALLER_FONT.getSize());
		timeLabel.setFont(timeFont);
		timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
		
	    JLabel messageLabel = new JLabel(message);
		panel.add(timeLabel);
		panel.add(messageLabel);
	    
		mMainPanel.add(panel);
		this.validate();
		
		JScrollBar vertical = mScrollPanel.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum() + 1);
		mScrollPanel.repaint();
		mScrollPanel.revalidate();
	}
}
