package com.criticalityworkbench.huffmancoding;

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
import java.io.OutputStream;
import java.util.Arrays;


/**
 * Decompression application using adaptive Huffman coding.
 * <p>Usage: java AdaptiveHuffmanDecompress InputFile OutputFile</p>
 * <p>This decompresses files generated by the "AdaptiveHuffmanCompress" application.</p>
 */
public final class AdaptiveHuffmanDecompress {
	
	
	// To allow unit testing, this method is package-private instead of private.
	public static StringOutputStream decompress(BitInputStream in) throws IOException {
		StringOutputStream out = new StringOutputStream();
		
		int[] initFreqs = new int[257];
		Arrays.fill(initFreqs, 1);
		
		FrequencyTable freqs = new FrequencyTable(initFreqs);
		HuffmanDecoder dec = new HuffmanDecoder(in);
		dec.codeTree = freqs.buildCodeTree();  // Use same algorithm as the compressor
		int count = 0;  // Number of bytes written to the output file
		while (true) {
			// Decode and write one byte
			int symbol = dec.read();
			if (symbol == 256)  // EOF symbol
				break;
			out.write(symbol);
			count++;
			
			// Update the frequency table and possibly the code tree
			freqs.increment(symbol);
			if (count < 262144 && isPowerOf2(count) || count % 262144 == 0)  // Update code tree
				dec.codeTree = freqs.buildCodeTree();
			if (count % 262144 == 0)  // Reset frequency table
				freqs = new FrequencyTable(initFreqs);
		}
		out.close();

		return out;
	}
	
	
	private static boolean isPowerOf2(int x) {
		return x > 0 && Integer.bitCount(x) == 1;
	}
	
}