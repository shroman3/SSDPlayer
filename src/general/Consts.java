/*******************************************************************************
 * SSDPlayer Visualization Platform (Version 1.0)
 * Authors: Or Mauda, Roman Shor, Gala Yadgar, Eitan Yaakobi, Assaf Schuster
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

public class Consts {
	public static class UI {
		public static final Font FONT = new Font("Segoe UI", Font.PLAIN, 13);
		public static final Font BOLD = new Font("Segoe UI", Font.BOLD, 11);
		public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 11);
		public static final Font SMALLER_FONT = new Font("Segoe UI", Font.PLAIN, 9);
		public static final Font TINY_FONT = new Font("Segoe UI", Font.PLAIN, 8);
		public static final Font INVISIBLE_FONT = new Font("Segoe UI", Font.PLAIN, 0);
	}
	
	public static class Colors {
		public static final Color ACTIVE = Color.red;
		public static final Color BG = Color.darkGray;
		public static final Color SELECTED_BG = Color.gray;
		public static final Color BORDER = Color.gray;
		public static final Color TEXT = Color.lightGray;
		public static final Color PAGE_TEXT = Color.black;
		public static final Color BLACK = Color.black;
		public static final Color CLEAN = Color.white;
		public static final Color CONTROL = new Color(40,40,40);
		public static final Color CONTROL_LIGHTER = new Color(50,50,50);
		public static final Color HIGHLIGHT = new Color(85,85,85);
	}
}
