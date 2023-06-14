package fr.senesi.g2t;

import fr.senesi.g2t.exception.SyntaxException;
import fr.senesi.g2t.exception.TokenizationException;
import fr.senesi.g2t.fsm.FiniteStateMachine;
import fr.senesi.g2t.fsm.State;
import fr.senesi.g2t.grammar.Grammar;
import fr.senesi.g2t.reader.Reader;
import fr.senesi.g2t.table.Table;
import fr.senesi.g2t.tokenizer.Tokenizer;

public class Grammar2Table {
	public static final String VERSION = "2.1.1";

	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[]{"help"};
		}

		if (args[0].equals("help") || args[0].equals("?")) {
			System.out.println("Grammar2Table v" + VERSION);
			System.out.println("Usage: g2t <file> [options]");
			System.out.println("Options:");
			System.out.println("  -s,     --show-states         Show all states of the finite state machine");
			System.out.println("  -p[N],  --optimize-csv[=N]    Optimize CSV file with level N (default: 1)");
			System.out.println("                                0: no optimization");
			System.out.println("                                1: remove ERROR actions");
			System.out.println("                                2: remove leading commas and state number");
			System.out.println("                                3: replace action type by a single character");
			System.exit(0);
		}

		boolean showStates = false;
		int optimizeCSVLevel = 0;

		for (int i = 1; i < args.length; i++) {
			if (args[i].equals("--show-states") || args[i].equals("-s")) showStates = true;

			else if (args[i].equals("--optimize-csv") || args[i].equals("-p")) optimizeCSVLevel = 1;
			else if (args[i].startsWith("--optimize-csv=") || args[i].startsWith("-p")) {
				String level = args[i].startsWith("--optimize-csv=") ? args[i].substring(15) : args[i].substring(2);
				if (level.matches("[0-9]+")) optimizeCSVLevel = Integer.parseInt(level);

				else {
					System.err.println("Warning: invalid level for --optimize-csv, using default value (1)");
					optimizeCSVLevel = 1;
				}
			} else {
				System.err.println("Warning: skipping unknown option: " + args[i]);
			}
		}

		String[] read = Reader.getLines(args[0]);

		Tokenizer tokenizer = null;

		try {
			tokenizer = new Tokenizer(String.join("\n", read));
		} catch (TokenizationException e) {
			e.printStackTrace();
		}

		if (tokenizer == null) System.exit(1);

		Grammar grammar = null;

		try {
			grammar = new Grammar(tokenizer);
		} catch (SyntaxException e) {
			e.printStackTrace();
		}

		if (grammar == null) System.exit(1);

		FiniteStateMachine fsm = new FiniteStateMachine(grammar);
		fsm.createAllStates();

		if (showStates) for (State s : fsm.getStates()) s.print();

		Table table = new Table(fsm);
		String name = args[0].substring(0, args[0].lastIndexOf('.'));
		table.saveCSV(name + ".csv", optimizeCSVLevel);
		table.save(name + ".g2table");
	}
}