package g2t;

public class Grammar2Table {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java g2t.Grammar2Table <file>");
			System.exit(1);
		}

		String file = args[0];

		String[] lines = Reader.getLines(file);
		lines = Reader.cleanLines(lines);

		for (String line : lines) {
			System.out.println(line);
		}
	}
}