package fr.senesi.g2t;

import java.io.File;

import fr.senesi.g2t.exception.SyntaxException;
import fr.senesi.g2t.exception.TokenizationException;
import fr.senesi.g2t.fsm.FiniteStateMachine;
import fr.senesi.g2t.fsm.State;
import fr.senesi.g2t.grammar.Grammar;
import fr.senesi.g2t.reader.Reader;
import fr.senesi.g2t.table.Table;
import fr.senesi.g2t.tokenizer.Tokenizer;

public class Grammar2Table {
	public static final String VERSION = "3.0.1";

	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[]{"help"};
		}

		if (args[0].equals("help") || args[0].equals("?")) {
			System.out.println("Grammar2Table v" + VERSION);
			System.out.println("Usage: g2t <file> [options]");
			System.out.println("Options:");
			System.out.println("  -s,       --show-states         Show all states of the finite state machine");
			System.out.println("  -n,       --no-table            Prevents the table from exporting");
			System.out.println("  -q,       --quiet               Quiet mode, only show errors");
			System.out.println("  -c,       --compact             Merge states to remove doubles when no ambiguity");
			System.out.println("  -o<name>, --output=name         Output file name (default: <file>)");
			System.out.println("  -p[N],    --optimize-csv[=N]    Optimize CSV file with level N (default: 1)");
			System.out.println("                                  0: no optimization");
			System.out.println("                                  1: remove ERROR actions");
			System.out.println("                                  2: remove leading commas and state number");
			System.out.println("                                  3: replace action type by a single character,");
			System.out.println("                                     replace arrow by equals and removes \"I\" in states");
			System.out.println("                                  4: Replace REDUCE action rules by the non-terminal to");
			System.out.println("                                     reduce to and the number of stack pop to do");
			System.out.println("                                  5: Remove first space after the first letter");
			System.out.println("                                     and replace non-terminals by their index at the top");
			System.exit(0);
		}

		boolean showStates = false, noTable = false, quiet = false, compact = false;
		int optimizeCSVLevel = 0;
		String output = args[0].substring(0, args[0].lastIndexOf('.'));

		for (int i = 1; i < args.length; i++) {
			if (args[i].equals("--show-states") || args[i].equals("-s")) showStates = true;
			else if (args[i].equals("--no-table") || args[i].equals("-n")) noTable = true;
			else if (args[i].equals("--quiet") || args[i].equals("-q")) quiet = true;
			else if (args[i].equals("--compact") || args[i].equals("-c")) compact = true;

			else if (args[i].equals("--output")) System.err.println("Warning: no output file name specified, skipping option");
			else if (args[i].startsWith("--output=") || args[i].startsWith("-o")) {
				String name = args[i].startsWith("--output=") ? args[i].substring(9) : args[i].substring(2);

				if (name.length() == 0) {
					System.err.println("Warning: no output file name specified, using default value (" + output + ")");
					continue;
				}

				if (!name.matches("(\\.?\\.\\/)*([A-Za-z0-9\\-_\\.]+\\/)*([A-Za-z0-9\\-_\\.]+)")) {
					System.err.println("Warning: output file name must only contain letters, numbers, hyphens, underscores and periods, using default value (" + output + ")");
					continue;
				}

				if (name.contains("/")) {
					String folder = name.substring(0, name.lastIndexOf('/'));
					File f = new File(folder);

					if (!f.exists()) {
						System.err.println("Warning: output folder does not exist, using default value (" + output + ")");
						continue;
					}
				}

				output = name;
			}

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

		long start = System.currentTimeMillis();

		String[] read = Reader.getLines(args[0]);

		Tokenizer tokenizer = null;

		if (!quiet) System.out.println("Tokenizing...");

		try {
			tokenizer = new Tokenizer(String.join("\n", read));
		} catch (TokenizationException e) {
			e.printStackTrace();
		}

		if (tokenizer == null) System.exit(1);

		Grammar grammar = null;

		if (!quiet) System.out.println("Parsing...");

		try {
			grammar = new Grammar(tokenizer);
		} catch (SyntaxException e) {
			e.printStackTrace();
		}

		if (grammar == null) System.exit(1);

		if (!quiet) System.out.println("Creating finite state machine...");

		FiniteStateMachine fsm = new FiniteStateMachine(grammar);
		if (quiet) fsm.setQuiet(true);
		fsm.createAllStates();

		if (showStates) for (State s : fsm.getStates()) s.print();

		if (!quiet) System.out.println("Creating table...");
		Table table = new Table(fsm);
		if (quiet) table.setQuiet();
		if (compact) table.compact();

		if (!quiet) System.out.println("Exporting...");

		table.saveCSV(output + ".csv", optimizeCSVLevel);
		if (!noTable) table.save(output + ".g2table");

		start = System.currentTimeMillis() - start;
		float secs = (float) start / 1000;

		if (!quiet) System.out.println("Done in " + secs + " second" + (secs >= 2 ? "s" : "") + "!");
	}
}