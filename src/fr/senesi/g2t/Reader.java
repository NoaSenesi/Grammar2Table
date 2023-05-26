package fr.senesi.g2t;

import java.io.File;
import java.util.Scanner;

public class Reader {
	public static String[] getLines(String file) {
		File f = new File(file);

		if (!f.exists()) {
			System.out.println("File not found: " + file);
			System.exit(1);
		}

		String data = "";

		try {
			Scanner sc = new java.util.Scanner(f);

			while (sc.hasNextLine()) data += sc.nextLine() + "\n";

			sc.close();
		} catch (Exception e) {
			System.out.println("Error reading file: " + e.getMessage());
			System.exit(1);
		}

		return data.split("\n");
	}

	public static String[] cleanLines(String[] lines) {
		String[] cleanLines = new String[lines.length];

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			line = line.replaceAll("(//|#).*", "");
			line = line.trim();
			line = line.replaceAll("(\s|\t|\r|\n)", "");
			cleanLines[i] = line;
		}

		String data = String.join("", cleanLines);
		cleanLines = data.split(";");

		int n = 0;
		for (int i = 0; i < cleanLines.length; i++) {
			if (cleanLines[i].length() > 0) lines[n++] = cleanLines[i] + ";";
		}

		String[] res = new String[n];
		for (int i = 0; i < n; i++) res[i] = lines[i];

		return res;
	}
}