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
	public static final String VERSION = "2.1.0";

	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[]{"help"};
		}

		if (args[0].equals("help") || args[0].equals("?")) {
			System.out.println("Grammar2Table v" + VERSION);
			System.out.println("Usage: g2t <file> [options]");
			System.out.println("Options:");
			System.out.println("  -s, --show-states    Show all states of the finite state machine");
			System.out.println("  -p, --optimize-csv   Optimize CSV file by removing ERROR actions");
			System.exit(0);
		}

		boolean showStates = false, optimizeCSV = false;
		for (int i = 1; i < args.length; i++) {
			if (args[i].equals("--show-states") || args[i].equals("-s")) showStates = true;
			if (args[i].equals("--optimize-csv") || args[i].equals("-p")) optimizeCSV = true;
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
		table.saveCSV(args[0] + ".csv", optimizeCSV);
		table.save(args[0] + ".txt");
	}
}