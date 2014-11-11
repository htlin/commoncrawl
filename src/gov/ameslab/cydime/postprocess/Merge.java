package gov.ameslab.cydime.postprocess;

import gov.ameslab.cydime.util.CUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Merge {
	
	private static Map<String, String> mSiteMap;
	private static Map<String, Double> mSiteScores;
	
	public static void main(String[] args) throws IOException {
		init();
		for (String key : mSiteMap.keySet()) {
			System.out.println("Processing " + mSiteMap.get(key));
			
			mSiteScores = CUtil.makeMap();
			for (File f : new File(args[0]).listFiles()) {
				System.out.println("Reading " + f);
				read(f, key);
			}
			
			System.out.println("Writing");
			CUtil.rescale(mSiteScores);
			write(key);
		}
	}
	
	private static void write(String key) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(mSiteMap.get(key)));
		
		List<String> hostnames = CUtil.makeList(mSiteScores.keySet());
		Collections.sort(hostnames);
		for (String hostname : hostnames) {
			out.write(hostname);
			out.write(",");
			out.write(mSiteScores.get(hostname).toString());
			out.newLine();
		}			
		
		out.close();
	}
	
	private static void read(File f, String key) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(f));
		String line;
		while ((line = in.readLine()) != null) {
			String[] split = line.split("\\t");
			String site = split[0].substring(0, 1);
			if (!key.equals(site)) continue;
			
			String hostname = split[0].substring(1);
			double score = Double.parseDouble(split[1]);
			Double oldScore = mSiteScores.get(hostname);
			if (oldScore == null) {
				oldScore = 0.0;
			}
			mSiteScores.put(hostname, Math.max(score, oldScore));
		}
		in.close();		
	}

	private static void init() {
		mSiteMap = CUtil.makeMap();
		mSiteMap.put("M", "www.ameslab.gov");
		mSiteMap.put("A", "www.anl.gov");
		mSiteMap.put("B", "www.bnl.gov");
		mSiteMap.put("N", "www.nrel.gov");
		mSiteMap.put("O", "www.ornl.gov");
		mSiteMap.put("P", "www.pnl.gov");
	}
	
}
