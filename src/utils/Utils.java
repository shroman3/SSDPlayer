/*******************************************************************************
 * SSDPlayer Visualization Platform (Version 1.0)
 * Authors: Or Mauda, Roman Shor, Gala Yadgar, Eitan Yaakobi, Assaf Schuster
 * Copyright (c) 2015, Technion � Israel Institute of Technology
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
package utils;

public class Utils {
	public static class LpArgs{
		private Integer size;
		private Integer temperatrure;
		private Integer stripe;
		private Integer writeLevel;

		public LpArgs(Integer size, Integer temperatrure, Integer stripe, Integer writeLevel) {
			this.size = size;
			this.temperatrure = temperatrure;
			this.stripe = stripe;
			this.writeLevel = writeLevel;
		}

		public Integer getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}

		public Integer getTemperatrure() {
			return temperatrure;
		}

		public void setTemperatrure(int temperatrure) {
			this.temperatrure = temperatrure;
		}

		public Integer getStripe() {
			return stripe;
		}

		public void setStripe(int stripe) {
			this.stripe = stripe;
		}

		public Integer getWriteLevel() {
			return writeLevel;
		}

		public void setWriteLevel(Integer writeLevel) {
			this.writeLevel = writeLevel;
		}
	}

	public static class LpArgsBuilder{
		private Integer size;
		private Integer temperatrure;
		private Integer stripe;
		private Integer writeLevel;

		public LpArgsBuilder(){ }

		public LpArgs buildLpArgs(){
			return new LpArgs(size, temperatrure, stripe, writeLevel);
		}

		public LpArgsBuilder setAll(int val){
			this.size = val;
			this.temperatrure = val;
			this.stripe = val;
			this.writeLevel = val;
			return this;
		}

		public LpArgsBuilder size(int size){
			this.size = size;
			return this;
		}

		public LpArgsBuilder temperature(int temperatrure){
			this.temperatrure = temperatrure;
			return this;
		}

		public LpArgsBuilder stripe(int stripe){
			this.stripe = stripe;
			return this;
		}

		public LpArgsBuilder writeLevel(int writeLevel){
			this.writeLevel = writeLevel;
			return this;
		}
	}

	public enum GctType {
		PERCENT,
		BLOCKS
	}

	public static Boolean parseBoolean(String var){
		if (var.toLowerCase().equals("t")){
			return true;
		} else if (var.toLowerCase().equals("f")){
			return false;
		} else {
			return null;
		}
	}

	public static void validateNotNull(Object param, String paramName) {
		if (param == null) {
			throw new IllegalArgumentException(paramName + " parameter is null (It shouldn't be)");
		}
	}
	
	public static void validateNotNegative(int param, String paramName) {
		if (param < 0) {
			throw new IllegalArgumentException(paramName + " parameter is negative (It shouldn't be)");
		}
	}
	
	public static void validateNotNegative(double param, String paramName) {
		if (param < 0) {
			throw new IllegalArgumentException(paramName + " parameter is negative (It shouldn't be)");
		}
	}
	
	public static void validateInteger(double param, String paramName) {
		if (param != (int) param) {
			throw new IllegalArgumentException(paramName + " parameter is not an Integer (It should be)");
		}
	}
}
