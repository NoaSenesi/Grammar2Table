package g2t;

public class Grammar {
	private String[] lines;

	public Grammar(String file) {
		lines = Reader.getLines(file);
		lines = Reader.cleanLines(lines);
	}

	public String[] getLines() {
		return lines;
	}
}
