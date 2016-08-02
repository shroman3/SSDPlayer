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
import manager.VisualConfig;
import zoom.IZoomLevel;

public class ZoomLevelPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JLabel mCurrentZoomLevelLabel;
	private IZoomLevel mInitialZoomLevel;
	private JPanel mPalettePanel;
	private JPanel mZoomPalettePanel;
	private VisualConfig mVisualConfig;

	public ZoomLevelPanel(IZoomLevel initialZoomLevel, VisualConfig visualConfig) {
		setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		mInitialZoomLevel = initialZoomLevel;
		mVisualConfig = visualConfig;
		
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
		
		setPalette();
		add(mZoomPalettePanel);
	}

	private void setPalette() {
		mZoomPalettePanel.removeAll();
		if (mVisualConfig.getBlocksColorRange() == null) {
			return;
		}
		
		List<Color> colorRange = null;
		if (mVisualConfig.getBlocksColorRange() == null) {
			colorRange = Consts.defaultColorRange;
		} else {
			colorRange = mVisualConfig.getBlocksColorRange();
		}
		
		JLabel paletteLabel = new JLabel("Zoom Palette:");
		paletteLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		paletteLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3));
		mZoomPalettePanel.add(paletteLabel);
		
		mPalettePanel = new JPanel();
		mPalettePanel.setLayout(new BoxLayout(mPalettePanel, BoxLayout.X_AXIS));
		mZoomPalettePanel.add(mPalettePanel);
		
		for (int i = 0; i < colorRange.size(); i++) {
			JPanel paletteSquarePanel = new JPanel();
			paletteSquarePanel.setLayout(new BoxLayout(paletteSquarePanel, BoxLayout.Y_AXIS));
			
			Color color = colorRange.get(i);
			JPanel colorPanel = new JPanel();
			colorPanel.setBackground(color);
			
			colorPanel.setMinimumSize(new Dimension(20, 20));
			colorPanel.setPreferredSize(new Dimension(20, 20));
			colorPanel.setMaximumSize(new Dimension(20, 20));

			paletteSquarePanel.add(colorPanel);

			String value = " ";
			if (i == 0 && mVisualConfig.getRangeLowValue() != null) {
				value = Integer.toString(mVisualConfig.getRangeLowValue());
			} else if (i == colorRange.size() - 1 && mVisualConfig.getRangeHighValue() != null) {
				value = Integer.toString(mVisualConfig.getRangeHighValue());
			}
			
			JLabel valueLabel = new JLabel(value);
			valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			paletteSquarePanel.add(valueLabel);
			
			mPalettePanel.add(paletteSquarePanel);
		}
	}

	public void setZoomLevel(IZoomLevel zoomLevel) {
		mCurrentZoomLevelLabel.setText(getDisplayedZoomName(zoomLevel));
		setPalette();
		
		revalidate();
		repaint();
	}
	
	public String getDisplayedZoomName(IZoomLevel zoomLevel) {
		String zoomGroup = zoomLevel.getGroup();
		String zoomName = zoomLevel.getName();
		return (zoomGroup != null)? zoomGroup + " " + zoomName : zoomName;
	}
}
