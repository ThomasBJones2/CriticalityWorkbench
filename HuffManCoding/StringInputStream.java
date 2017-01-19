package HuffManCoding;
/* 
 * Reference Huffman coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-huffman-coding
 * https://github.com/nayuki/Reference-Huffman-coding
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringInputStream{
	String theString;
	int charCount;
	
	public StringInputStream(String inString){
		theString = new String(inString);
		charCount = 0;
	}

	public void print(){
		System.out.println("Printing a StringInputStream");
		System.out.println(theString);
	}

	public int read(){
		int out = 0;
		if(charCount < theString.length()){
			out = (int)theString.charAt(charCount);
			charCount ++;
		} else {
			out = -1;
		}
		return out;	
	}

	public void reset(){
		charCount = 0;
	}

	public void close(){}



}
