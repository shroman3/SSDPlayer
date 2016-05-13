package ui.zoom;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import general.Consts;
import zoom.IZoomLevel;

public class ZoomLevelPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel mCurrentZoomLevelLabel;
	private IZoomLevel mInitialZoomLevel;
	private JPanel mPalettePanel;
	private JPanel mZoomPalettePanel;

	public ZoomLevelPanel(IZoomLevel initialZoomLevel) {
		setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		mInitialZoomLevel = initialZoomLevel;
		
		initSelectedZoom();
		initZoomPalette();
		
		setMinimumSize(new Dimension(250, 150));
		setPreferredSize(new Dimension(250, 150));
		setMaximumSize(new Dimension(250, 150));
	}

	private void initSelectedZoom() {
		JPanel selectedZoomPanel = new JPanel();
		selectedZoomPanel.setLayout(new BoxLayout(selectedZoomPanel, BoxLayout.X_AXIS));
		selectedZoomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel zoomLabel = new JLabel("Zoom Level:");
		zoomLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
		selectedZoomPanel.add(zoomLabel);
		
		String displayedZoomName = getDisplayedZoomName(mInitialZoomLevel);
		mCurrentZoomLevelLabel = new JLabel(displayedZoomName);
		mCurrentZoomLevelLabel.setFont(new Font(Consts.UI.SMALL_FONT.getFontName(), Font.BOLD, Consts.UI.SMALL_FONT.getSize()));
		
		selectedZoomPanel.add(mCurrentZoomLevelLabel);
		
		add(selectedZoomPanel);
	}
	
	private void initZoomPalette() {
		mZoomPalettePanel = new JPanel();
		mZoomPalettePanel.setLayout(new BoxLayout(mZoomPalettePanel, BoxLayout.X_AXIS));
		mZoomPalettePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		mZoomPalettePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		List<Color> zoomPalette = mInitialZoomLevel.getPalette();
		setPalette(zoomPalette);
		
		add(mZoomPalettePanel);
	}

	private void setPalette(List<Color> zoomPalette) {
		mZoomPalettePanel.removeAll();
		if (zoomPalette == null) return;
		
		JLabel paletteLabel = new JLabel("Zoom Palette:");
		paletteLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
		mZoomPalettePanel.add(paletteLabel);
		
		mPalettePanel = new JPanel();
		mPalettePanel.setLayout(new BoxLayout(mPalettePanel, BoxLayout.X_AXIS));
		mZoomPalettePanel.add(mPalettePanel);
		
		for (int i = 0; i < zoomPalette.size(); i++) {
			Color color = zoomPalette.get(i);
			
			JPanel colorPanel = new JPanel();
			colorPanel.setBackground(color);
			
			colorPanel.setMinimumSize(new Dimension(20, 20));
			colorPanel.setPreferredSize(new Dimension(20, 20));
			colorPanel.setMaximumSize(new Dimension(20, 20));
			
			mPalettePanel.add(colorPanel);
		}
	}

	public void setZoomLevel(IZoomLevel zoomLevel) {
		mCurrentZoomLevelLabel.setText(getDisplayedZoomName(zoomLevel));
		setPalette(zoomLevel.getPalette());
		
		revalidate();
		repaint();
	}
	
	public String getDisplayedZoomName(IZoomLevel zoomLevel) {
		String zoomGroup = zoomLevel.getGroup();
		String zoomName = zoomLevel.getName();
		return (zoomGroup != null)? zoomGroup + " " + zoomName : zoomName;
	}
}
