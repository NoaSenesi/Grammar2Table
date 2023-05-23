package g2t;

public class Grammar2Table {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java g2t.Grammar2Table <file>");
			System.exit(1);
		}

		Grammar g = new Grammar(args[0]);

		System.out.println(g.firsts('S'));
		System.out.println();
		System.out.println(g.firsts('E'));
		System.out.println();
		System.out.println(g.firsts('R'));
		System.out.println();

		g.printRules();
	}
}