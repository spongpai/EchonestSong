package com.uci.edu.song;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FunctionUtils {
	public static void writeToFile(String file, String text, boolean append){
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, append));
			bw.append(text);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
