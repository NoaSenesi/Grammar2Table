package g2t;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import g2t.Action.ActionType;

public class Table {
	private FSM fsm;
	private String terminals, nonTerminals;
	private Action[][] table;

	public Table(FSM fsm) {
		this.fsm = fsm;

		terminals = fsm.getAugmentedGrammar().getTerminals().replace("^", "") + "$";
		nonTerminals = fsm.getAugmentedGrammar().getNonTerminals();
	}

	public FSM getFiniteStateMachine() {
		return fsm;
	}

	private void buildTable() {
		int width = terminals.length() + nonTerminals.length();
		int height = fsm.getStates().size();
		table = new Action[height][width];

		String t = terminals + nonTerminals;

		for (int i = 0; i < height; i++) {
			State s = fsm.getStates().get(i);

			for (int j = 0; j < width; j++) {
				char c = t.charAt(j);

				if (s.shift(c) == null) {
					if (c == '$' && s.getRules().size() == 1 && s.getRules().get(0).getLeft().equals(fsm.getAugmentedGrammar().getAxiom())) {
						table[i][j] = new Action(ActionType.ACCEPT);
					} else {
						for (Rule r : s.getRules()) {
							if (r.getContext().indexOf(c) != -1 && r.canReduce()) {
								table[i][j] = new Action(ActionType.REDUCE, r);
								break;
							}
						}

						if (table[i][j] == null) table[i][j] = new Action(ActionType.ERROR);
					}
				} else {
					table[i][j] = new Action(ActionType.SHIFT, s.shift(c).getId());
				}
			}
		}
	}

	public Action[][] getTable() {
		if (table == null) buildTable();

		return table;
	}

	public void print() {
		int[] width = new int[terminals.length() + nonTerminals.length() + 1];

		for (Action[] line : getTable()) {
			for (int i = 0; i < line.length; i++) {
				if (line[i].toString().length() > width[i+1]) width[i+1] = line[i].toString().length();
			}

			for (State s : fsm.getStates()) {
				if (("I" + s.getId()).length() > width[0]) width[0] = ("I" + s.getId()).length();
			}

			if ("State".length() > width[0]) width[0] = "State".length();
		}

		System.out.print("┌");
		for (int i = 0; i < width.length; i++) {
			for (int j = 0; j < width[i] + 2; j++) System.out.print("─");
			if (i != width.length - 1) System.out.print("┬");
			if (i == terminals.length() || i == 0) System.out.print("┬");
		}
		System.out.println("┐");

		System.out.print("│ State ");
		for (int i = 0; i < width[0] - 5; i++) System.out.print(" ");

		System.out.print("││");
		for (int i = 0; i < terminals.length(); i++) {
			System.out.print(" " + terminals.charAt(i) + " ");
			for (int j = 0; j < width[i+1] - 1; j++) System.out.print(" ");
			System.out.print("│");
		}
		System.out.print("│");
		for (int i = 0; i < nonTerminals.length(); i++) {
			System.out.print(" " + nonTerminals.charAt(i) + " ");
			for (int j = 0; j < width[i + terminals.length() + 1] - 1; j++) System.out.print(" ");
			System.out.print("│");
		}
		System.out.println();

		System.out.print("├");
		for (int i = 0; i < width.length; i++) {
			for (int j = 0; j < width[i] + 2; j++) System.out.print("─");
			if (i != width.length - 1) System.out.print("┼");
			if (i == terminals.length() || i == 0) System.out.print("┼");
		}
		System.out.println("┤");

		for (int i = 0; i < getTable().length; i++) {
			String in = "I" + i;
			System.out.print("│ " + in + " ");
			for (int j = 0; j < width[0] - in.length(); j++) System.out.print(" ");

			System.out.print("││");
			for (int j = 0; j < getTable()[i].length; j++) {
				System.out.print(" " + getTable()[i][j] + " ");
				for (int k = 0; k < width[j+1] - getTable()[i][j].toString().length(); k++) System.out.print(" ");
				System.out.print("│");
				if (j == terminals.length() - 1) System.out.print("│");
			}
			System.out.println();
		}

		System.out.print("└");
		for (int i = 0; i < width.length; i++) {
			for (int j = 0; j < width[i] + 2; j++) System.out.print("─");
			if (i != width.length - 1) System.out.print("┴");
			if (i == terminals.length() || i == 0) System.out.print("┴");
		}
		System.out.println("┘");
	}

	public void save(String filename) {
		File file = new File(filename);
		if (file.exists()) file.delete();

		try {
			file.createNewFile();
			FileWriter writer = new FileWriter(file);

			int[] width = new int[terminals.length() + nonTerminals.length() + 1];

			for (Action[] line : getTable()) {
				for (int i = 0; i < line.length; i++) {
					if (line[i].toString().length() > width[i+1]) width[i+1] = line[i].toString().length();
				}

				for (State s : fsm.getStates()) {
					if (("I" + s.getId()).length() > width[0]) width[0] = ("I" + s.getId()).length();
				}

				if ("State".length() > width[0]) width[0] = "State".length();
			}

			writer.write("┌");
			for (int i = 0; i < width.length; i++) {
				for (int j = 0; j < width[i] + 2; j++) writer.write("─");
				if (i != width.length - 1) writer.write("┬");
				if (i == terminals.length() || i == 0) writer.write("┬");
			}
			writer.write("┐\n");

			writer.write("│ State ");
			for (int i = 0; i < width[0] - 5; i++) writer.write(" ");

			writer.write("││");
			for (int i = 0; i < terminals.length(); i++) {
				writer.write(" " + terminals.charAt(i) + " ");
				for (int j = 0; j < width[i+1] - 1; j++) writer.write(" ");
				writer.write("│");
			}
			writer.write("│");
			for (int i = 0; i < nonTerminals.length(); i++) {
				writer.write(" " + nonTerminals.charAt(i) + " ");
				for (int j = 0; j < width[i + terminals.length() + 1] - 1; j++) writer.write(" ");
				writer.write("│");
			}
			writer.write("\n");

			writer.write("├");
			for (int i = 0; i < width.length; i++) {
				for (int j = 0; j < width[i] + 2; j++) writer.write("─");
				if (i != width.length - 1) writer.write("┼");
				if (i == terminals.length() || i == 0) writer.write("┼");
			}
			writer.write("┤\n");

			for (int i = 0; i < getTable().length; i++) {
				String in = "I" + i;
				writer.write("│ " + in + " ");
				for (int j = 0; j < width[0] - in.length(); j++) writer.write(" ");

				writer.write("││");
				for (int j = 0; j < getTable()[i].length; j++) {
					writer.write(" " + getTable()[i][j] + " ");
					for (int k = 0; k < width[j+1] - getTable()[i][j].toString().length(); k++) writer.write(" ");
					writer.write("│");
					if (j == terminals.length() - 1) writer.write("│");
				}
				writer.write("\n");
			}

			writer.write("└");
			for (int i = 0; i < width.length; i++) {
				for (int j = 0; j < width[i] + 2; j++) writer.write("─");
				if (i != width.length - 1) writer.write("┴");
				if (i == terminals.length() || i == 0) writer.write("┴");
			}
			writer.write("┘\n");

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveCSV(String filename) {
		File file = new File(filename);
		if (file.exists()) file.delete();

		try {
			file.createNewFile();
			FileWriter writer = new FileWriter(file);

			writer.write("State");
			for (int i = 0; i < terminals.length(); i++) writer.write("," + terminals.charAt(i));
			for (int i = 0; i < nonTerminals.length(); i++) writer.write("," + nonTerminals.charAt(i));
			writer.write("\n");

			for (int i = 0; i < getTable().length; i++) {
				String in = "I" + i;
				writer.write(in);
				for (int j = 0; j < getTable()[i].length; j++) writer.write("," + getTable()[i][j]);
				writer.write("\n");
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}