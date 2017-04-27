/*******************************************************************************
 * SSDPlayer Visualization Platform (Version 1.0)
 * Authors: Roman Shor, Gala Yadgar, Eitan Yaakobi, Assaf Schuster
 * Copyright (c) 2015, Technion – Israel Institute of Technology
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 * the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 *******************************************************************************/
package general;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

public class Consts {
	public static class Fonts {
		public final Font CAPTION;// = new Font("Segoe UI", Font.PLAIN, 16);
		public final Font CAPTION_BOLD;// = new Font("Segoe UI", Font.BOLD, 16);
		public final Font CONTROL_FONT;// = new Font("Segoe UI", Font.PLAIN, 12);
		public final Font CONTROL_ITALIC_FONT;// = new Font("Segoe UI", Font.ITALIC, 12);
		public final Font PAGE_FONT;// = new Font("Segoe UI", Font.PLAIN, 10);
		public final Font INVISIBLE_FONT = new Font("Segoe UI", Font.PLAIN, 0);
		
		private Fonts(XMLGetter xmlGetter) throws XMLParsingException {
			String fontname = xmlGetter.getStringField("visual", "font_type");
			int captionFontSize = xmlGetter.getIntField("visual", "caption_font_size");
			CAPTION = new Font(fontname, Font.PLAIN, captionFontSize);
			CAPTION_BOLD = new Font(fontname, Font.BOLD, captionFontSize);
			int controlFontSize = xmlGetter.getIntField("visual", "control_font_size");
			CONTROL_FONT = new Font(fontname, Font.PLAIN, controlFontSize);
			CONTROL_ITALIC_FONT = new Font(fontname, Font.ITALIC, controlFontSize);
			PAGE_FONT = new Font(fontname, Font.PLAIN, xmlGetter.getIntField("visual", "page_font_size"));
		}

	}
	
	public static class Colors {
		public final Color ACTIVE;// = Color.red;
		public final Color OUTER_BG;// = new Color(40,40,40);
		public final Color INTERMEDIATE_BG;// = new Color(50,50,50);
		public final Color INNER_BG;// = Color.darkGray;
		public final Color BORDER;// = Color.gray;
		public final Color CONTROL_TEXT;// = Color.lightGray;
		public final Color PAGE_TEXT;// = Color.black;
		public final Color HIGHLIGHT;// = new Color(85,85,85);

		private Colors(XMLGetter xmlGetter) throws XMLParsingException {
			ACTIVE = xmlGetter.getColorField("visual", "active_color");
			OUTER_BG = xmlGetter.getColorField("visual", "outer_bg_color");
			INTERMEDIATE_BG = xmlGetter.getColorField("visual", "intermediate_bg_color");
			INNER_BG = xmlGetter.getColorField("visual", "inner_bg_color");
			BORDER = xmlGetter.getColorField("visual", "border_color");
			CONTROL_TEXT = xmlGetter.getColorField("visual", "control_text_color");
			PAGE_TEXT = xmlGetter.getColorField("visual", "page_text_color");
			HIGHLIGHT = xmlGetter.getColorField("visual", "highlight_color");
		}
	}

	public final Fonts fonts;
	public final Colors colors;
	
	public Consts(XMLGetter xmlGetter) throws XMLParsingException {
		fonts = new Fonts(xmlGetter);
		colors = new Colors(xmlGetter);
	}

	private static Consts instance;
	public static void initialize(XMLGetter xmlGetter) throws XMLParsingException {
		instance = new Consts(xmlGetter);
	}
	
	public static Consts getInstance() {
		return instance;
	}
	
	@SuppressWarnings("serial")
	public static ArrayList<Color> defaultColorRange = new ArrayList<Color>() {{
		 add(Color.white);
		 add(new Color(242, 242, 242));
		 add(new Color(217, 217, 217));
		 add(new Color(191, 191, 191));
		 add(new Color(166, 166, 166));
		 add(new Color(140, 140, 140));
		 add(new Color(115, 115, 115));
		 add(new Color(89, 89, 89));
		 add(new Color(64, 64, 64));
	}};
}
