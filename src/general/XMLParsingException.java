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

public abstract class XMLParsingException extends Exception {
	private static final long serialVersionUID = 1L;

	public XMLParsingException() {}

	public XMLParsingException(String message) {
		super(message);
	}

	public static class IllegalXMLElement extends XMLParsingException {
		private static final long serialVersionUID = 1L;
		
		public IllegalXMLElement() {}
		
		public IllegalXMLElement(String elementName) {
			super("Illegal element name given: "+ elementName);
		}
	}
	
	public static class NoSuchXMLElement extends XMLParsingException {
		private static final long serialVersionUID = 1L;
		
		public NoSuchXMLElement() {}
		
		public NoSuchXMLElement(String elementName) {
			super("Element with specified name doesn't exist: "+ elementName);
		}
	}
	
	public static class NoSuchXMLAttribute extends XMLParsingException {
		private static final long serialVersionUID = 1L;
		
		public NoSuchXMLAttribute() {}
		
		public NoSuchXMLAttribute(String element, String attrName) {
			super("Attribute:" + attrName + " doesn't exist in element: " + element);
		}
	}
	
	public static class WrongTypeXMLElement extends XMLParsingException {
		private static final long serialVersionUID = 1L;
		
		public WrongTypeXMLElement() {}
		
		public WrongTypeXMLElement(String element, String type, String given) {
			super("Element:" + element + " supose to be of type: " + type + " ,given: " + given);
		}
	}
	
	public static class IllegalXMLAttribute extends XMLParsingException {
		private static final long serialVersionUID = 1L;
		
		public IllegalXMLAttribute() {}
		
		public IllegalXMLAttribute(String element, String attrName, String type) {
			super("Attribute:" + attrName + " in element: " + element + " supose to be of type: " + type);
		}
	}
}
