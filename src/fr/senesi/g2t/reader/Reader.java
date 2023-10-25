package fr.senesi.g2t.reader;

import java.io.File;
import java.io.IOException;
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
		} catch (IOException e) {
			System.out.println("Error reading file: " + e.getMessage());
			System.exit(1);
		}

		return data.split("\n");
	}
}