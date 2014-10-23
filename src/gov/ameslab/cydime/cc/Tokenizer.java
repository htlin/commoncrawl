package gov.ameslab.cydime.cc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;

public class Tokenizer {
	
	private static boolean PRINT = true;
	private static int RUNS = 1;

	public static void main(String[] args) throws IOException {
		String doc = FileUtils.readFileToString(new File("ameslab.txt"));
//		System.out.println(doc);
		
		long time = System.currentTimeMillis();
		for (int i = 0; i < RUNS; i++) {
			tokStream("ameslab.txt");
		}
		time = System.currentTimeMillis() - time;
		System.out.println(time);
		
		
		time = System.currentTimeMillis();
		for (int i = 0; i < RUNS; i++) {
			tok1(doc);
		}
		time = System.currentTimeMillis() - time;
		System.out.println(time);
		
		time = System.currentTimeMillis();
		for (int i = 0; i < RUNS; i++) {
			tok2(doc);
		}
		time = System.currentTimeMillis() - time;
		System.out.println(time);
		
		time = System.currentTimeMillis();
		for (int i = 0; i < RUNS; i++) {
			tok3(doc);
		}
		time = System.currentTimeMillis() - time;
		System.out.println(time);
		
		time = System.currentTimeMillis();
		for (int i = 0; i < RUNS; i++) {
			tok4(doc);
		}
		time = System.currentTimeMillis() - time;
		System.out.println(time);
	}

	private static void tokStream(String file) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		List<String> strs = new ArrayList<String>();
		StreamTokenizer tok = new StreamTokenizer(in);
		while (tok.nextToken() != StreamTokenizer.TT_EOF) {
			strs.add(tok.sval);
		}
		
		if (PRINT) System.out.println(strs.size() + " " + strs);
	}

	private static void tok4(String doc) {
		String[] split = doc.split("\\W");
		for (int i = 0; i < split.length; i++) {
			String str = split[i];
		}
		
		if (PRINT) System.out.println(split.length + " " + Arrays.toString(split));
	}

	private static void tok3(String doc) {
		String[] split = doc.split("[\\p{IsPunctuation}\\p{IsWhite_Space}]+");
		for (int i = 0; i < split.length; i++) {
			String str = split[i];
		}
		
		if (PRINT) System.out.println(split.length + " " + Arrays.toString(split));
	}

	private static void tok2(String doc) {
		String[] split = doc.split("\\s");
		for (int i = 0; i < split.length; i++) {
			String str = split[i];
		}
		
		if (PRINT) System.out.println(split.length + " " + Arrays.toString(split));
	}

	private static void tok1(String doc) {
		StringTokenizer tokenizer = new StringTokenizer(doc);
		List<String> strs = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String str = tokenizer.nextToken();
			strs.add(str);
		}
		
		if (PRINT) System.out.println(strs.size() + " " + strs);
	}

}
