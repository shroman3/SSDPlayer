package ui;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import general.Consts;
import log.Message.Message;

public class LogView extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel mMainPanel;
	private JScrollPane mScrollPanel;

	public LogView() {
		setLayout(new BoxLayout(this, 1));
		this.mMainPanel = new JPanel();
		this.mMainPanel.setLayout(new BoxLayout(this.mMainPanel, 1));
		this.mMainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		this.mScrollPanel = new JScrollPane(this.mMainPanel);
		this.mScrollPanel.setVerticalScrollBarPolicy(22);

		JLabel header = new JLabel("Messages Log", 0);
		header.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		header.setVerticalAlignment(1);
		header.setFont(Consts.getInstance().fonts.CAPTION);
		header.setMinimumSize(new Dimension(320, 25));
		header.setPreferredSize(new Dimension(320, 25));
		header.setMaximumSize(new Dimension(320, 25));

		add(header);
		add(this.mScrollPanel);

		setMinimumSize(new Dimension(320, 154));
		setPreferredSize(new Dimension(320, 154));
		setMaximumSize(new Dimension(320, 154));
	}

	public void log(Message message) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, 0));
		panel.setAlignmentX(0.0F);

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

		JLabel timeLabel = new JLabel(sdf.format(cal.getTime()) + " ");
//		Font timeFont = new Font(Consts.getInstance().fonts.CONTROL_ITALIC_FONT.getFontName(), 2, Consts.getInstance().fonts.CONTROL_FONT.getSize());
		timeLabel.setFont(Consts.getInstance().fonts.CONTROL_ITALIC_FONT);
		timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));

		JLabel messageLabel = new JLabel(message.getText());
		messageLabel.setForeground(message.getColor());
		panel.add(timeLabel);
		panel.add(messageLabel);

		this.mMainPanel.add(panel);
		validate();

		JScrollBar vertical = this.mScrollPanel.getVerticalScrollBar();
		vertical.setValue(vertical.getMaximum() + 1);
		this.mScrollPanel.repaint();
		this.mScrollPanel.revalidate();
	}
}
