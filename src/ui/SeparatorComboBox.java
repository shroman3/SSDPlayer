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
package ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.basic.*;

/**
 *  Class that allows you to add a JSeparator to the ComboBoxModel.
 *
 *  The separator is rendered as a horizontal line. Using the Up/Down arrow
 *  keys will cause the combo box selection to skip over the separator.
 *  If you attempt to select the separator with the mouse, the selection
 *  will be ignored and the drop down will remain open.
 */
public class SeparatorComboBox extends JComboBox implements KeyListener
{
	//  Track key presses and releases

	private boolean released = true;

	//  Track when the separator has been selected
	private boolean separatorSelected = false;

	/**
	 *  Standard constructor. See JComboBox API for details
	 */
	public SeparatorComboBox()
	{
		super();
		init();
	}

	/**
	 *  Standard constructor. See JComboBox API for details
	 */
	public SeparatorComboBox(ComboBoxModel model)
	{
		super(model);
		init();
	}

	/**
	 *  Standard constructor. See JComboBox API for details
	 */
	public SeparatorComboBox(Object[] items)
	{
		super(items);
		init();
	}

	/**
	 *  Standard constructor. See JComboBox API for details
	 */
	public SeparatorComboBox(Vector<?> items)
	{
		super(items);
		init();
	}

	private void init()
	{
		setRenderer( new SeparatorRenderer() );
		addKeyListener(this);
	}

	/**
	 *	Prevent selection of the separator by keyboard or mouse
	 */
	@Override
	public void setSelectedIndex(int index)
	{
		Object value = getItemAt(index);

		//  Attempting to select a separator

		if (value instanceof JSeparator)
		{
			//  If no keys have been pressed then we must be using the mouse.
			//  Prevent selection of the Separator when using the mouse

			if (released)
			{
				separatorSelected = true;
				return;
			}

			//  Skip over the Separator when using the Up/Down keys

			int current = getSelectedIndex();
			index += (index > current) ? 1 : -1;

			if (index == -1 || index >= dataModel.getSize())
				return;
		}

		super.setSelectedIndex(index);
	}

	/**
	 *  Prevent closing of the popup when attempting to select the
	 *  separator with the mouse.
	 */
	@Override
	public void setPopupVisible(boolean visible)
	{
		//  Keep the popup open when the separator was clicked on

		if (separatorSelected)
		{
			separatorSelected = false;
			return;
		}

		super.setPopupVisible(visible);
	}

//
//  Implement the KeyListener interface
//
	public void keyPressed(KeyEvent e)
	{
		released = false;
	}

	public void keyReleased(KeyEvent e)
	{
		released = true;
	}

	public void keyTyped(KeyEvent e) {}

	/**
	 *  Class to render the JSeparator compenent
	 */
	class SeparatorRenderer extends BasicComboBoxRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus)
		{
			super.getListCellRendererComponent(
				list, value, index, isSelected, cellHasFocus);

			if (value instanceof JSeparator)
				return (JSeparator)value;

			return this;
		}
	}
}
