package fr.senesi.g2t.table;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.senesi.g2t.fsm.FiniteStateMachine;
import fr.senesi.g2t.fsm.Rule;
import fr.senesi.g2t.fsm.State;
import fr.senesi.g2t.table.Action.ActionType;

public class Table {
	private FiniteStateMachine fsm;
	private List<String> tokens;
	private Action[][] table;

	public Table(FiniteStateMachine fsm) {
		this.fsm = fsm;

		tokens = new ArrayList<>();
		tokens.addAll(fsm.getGrammar().getTerminals());
		tokens.add("$");
		tokens.addAll(fsm.getGrammar().getNonTerminals());
		tokens.remove(fsm.getGrammar().getAxiom());
	}

	public FiniteStateMachine getFiniteStateMachine() {
		return fsm;
	}

	private void buildTable() {
		int width = fsm.getGrammar().getTerminals().size() + fsm.getGrammar().getNonTerminals().size();
		int height = fsm.getStates().size();
		table = new Action[height][width];

		for (int i = 0; i < height; i++) {
			State s = fsm.getStates().get(i);

			for (int j = 0; j < width; j++) {
				String token = tokens.get(j);

				if (s.shift(token) == null) {
					if (token == "$" && s.getRules().size() == 1 && s.getRules().get(0).getLeft().equals(fsm.getGrammar().getAxiom())) {
						table[i][j] = new Action(ActionType.ACCEPT);
					} else {
						for (Rule r : s.getRules()) {
							if (r.getContext().contains(token) && r.isFinished()) {
								table[i][j] = new Action(ActionType.REDUCE, r);
								break;
							}
						}

						if (table[i][j] == null) table[i][j] = new Action(ActionType.ERROR);
					}
				} else {
					table[i][j] = new Action(ActionType.SHIFT, s.shift(token).getId());
				}
			}
		}
	}

	public Action[][] getTable() {
		if (table == null) buildTable();

		return table;
	}

	public void print() {
		int[] width = new int[tokens.size() + 1];
		width[0] = 5;

		for (int i = 1; i < width.length; i++) {
			if (tokens.get(i-1).length() > width[i]) width[i] = tokens.get(i-1).length();
		}

		for (Action[] line : getTable()) {
			for (int i = 0; i < line.length; i++) {
				if (line[i].toString().length() > width[i+1]) width[i+1] = line[i].toString().length();
			}

			for (State s : fsm.getStates()) {
				if (("I" + s.getId()).length() > width[0]) width[0] = ("I" + s.getId()).length();
			}
		}

		System.out.print("┌");
		for (int i = 0; i < width.length; i++) {
			for (int j = 0; j < width[i] + 2; j++) System.out.print("─");
			if (i != width.length - 1) System.out.print("┬");
			if (i == fsm.getGrammar().getTerminals().size() + 1 || i == 0) System.out.print("┬");
		}
		System.out.println("┐");

		System.out.print("│ State ");
		for (int i = 0; i < width[0] - 5; i++) System.out.print(" ");
		System.out.print("│");
		for (int i = 1; i < width.length; i++) {
			System.out.print("│ ");
			System.out.print(tokens.get(i-1));
			for (int j = 0; j < width[i] - tokens.get(i-1).length() + 1; j++) System.out.print(" ");
			if (i == fsm.getGrammar().getTerminals().size() + 1) System.out.print("│");
		}
		System.out.println("│");

		System.out.print("├");
		for (int i = 0; i < width.length; i++) {
			for (int j = 0; j < width[i] + 2; j++) System.out.print("─");
			if (i != width.length - 1) System.out.print("┼");
			if (i == fsm.getGrammar().getTerminals().size() + 1 || i == 0) System.out.print("┼");
		}
		System.out.println("┤");

		for (int i = 0; i < getTable().length; i++) {
			System.out.print("│ I" + i + " ");
			for (int j = 0; j < width[0] - ("I" + i).length(); j++) System.out.print(" ");
			System.out.print("│");
			for (int j = 0; j < getTable()[i].length; j++) {
				System.out.print("│ ");
				System.out.print(getTable()[i][j]);
				for (int k = 0; k < width[j+1] - getTable()[i][j].toString().length() + 1; k++) System.out.print(" ");
				if (j == fsm.getGrammar().getTerminals().size()) System.out.print("│");
			}
			System.out.println("│");
		}

		System.out.print("└");
		for (int i = 0; i < width.length; i++) {
			for (int j = 0; j < width[i] + 2; j++) System.out.print("─");
			if (i != width.length - 1) System.out.print("┴");
			if (i == fsm.getGrammar().getTerminals().size() + 1 || i == 0) System.out.print("┴");
		}
		System.out.println("┘");
	}

	public void save(String filename) {
		File file = new File(filename);
		if (file.exists()) file.delete();

		try {
			file.createNewFile();
			FileWriter writer = new FileWriter(file);

			int[] width = new int[tokens.size() + 1];
			width[0] = 5;

			for (int i = 1; i < width.length; i++) {
				if (tokens.get(i-1).length() > width[i]) width[i] = tokens.get(i-1).length();
			}

			for (Action[] line : getTable()) {
				for (int i = 0; i < line.length; i++) {
					if (line[i].toString().length() > width[i+1]) width[i+1] = line[i].toString().length();
				}

				for (State s : fsm.getStates()) {
					if (("I" + s.getId()).length() > width[0]) width[0] = ("I" + s.getId()).length();
				}
			}

			writer.write("┌");
			for (int i = 0; i < width.length; i++) {
				for (int j = 0; j < width[i] + 2; j++) writer.write("─");
				if (i != width.length - 1) writer.write("┬");
				if (i == fsm.getGrammar().getTerminals().size() + 1 || i == 0) writer.write("┬");
			}
			writer.write("┐\n");

			writer.write("│ State ");
			for (int i = 0; i < width[0] - 5; i++) writer.write(" ");
			writer.write("│");
			for (int i = 1; i < width.length; i++) {
				writer.write("│ ");
				writer.write(tokens.get(i-1));
				for (int j = 0; j < width[i] - tokens.get(i-1).length() + 1; j++) writer.write(" ");
				if (i == fsm.getGrammar().getTerminals().size() + 1) writer.write("│");
			}
			writer.write("│\n");

			writer.write("├");
			for (int i = 0; i < width.length; i++) {
				for (int j = 0; j < width[i] + 2; j++) writer.write("─");
				if (i != width.length - 1) writer.write("┼");
				if (i == fsm.getGrammar().getTerminals().size() + 1 || i == 0) writer.write("┼");
			}
			writer.write("┤\n");

			for (int i = 0; i < getTable().length; i++) {
				writer.write("│ I" + i + " ");
				for (int j = 0; j < width[0] - ("I" + i).length(); j++) writer.write(" ");
				writer.write("│");
				for (int j = 0; j < getTable()[i].length; j++) {
					writer.write("│ ");
					writer.write(getTable()[i][j].toString());
					for (int k = 0; k < width[j+1] - getTable()[i][j].toString().length() + 1; k++) writer.write(" ");
					if (j == fsm.getGrammar().getTerminals().size()) writer.write("│");
				}
				writer.write("│\n");
			}

			writer.write("└");
			for (int i = 0; i < width.length; i++) {
				for (int j = 0; j < width[i] + 2; j++) writer.write("─");
				if (i != width.length - 1) writer.write("┴");
				if (i == fsm.getGrammar().getTerminals().size() + 1 || i == 0) writer.write("┴");
			}
			writer.write("┘");

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
			for (String token : tokens) {
				writer.write("," + token);
			}

			for (int i = 0; i < getTable().length; i++) {
				writer.write("\nI" + i);

				for (int j = 0; j < getTable()[i].length; j++) {
					writer.write("," + getTable()[i][j].toString());
				}
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



/*
	public void save(String filename) {
		File file = new File(filename);
		if (file.exists()) file.delete();

		try {
			file.createNewFile();
			FileWriter writer = new FileWriter(file);

			int[] width = new int[fsm.getGrammar().getTerminals().size() + fsm.getGrammar().getNonTerminals().size() + 1];

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
				if (i == fsm.getGrammar().getTerminals().size() || i == 0) writer.write("┬");
			}
			writer.write("┐\n");

			writer.write("│ State ");
			for (int i = 0; i < width[0] - 5; i++) writer.write(" ");

			writer.write("││");
			for (int i = 0; i < fsm.getGrammar().getTerminals().size(); i++) {
				writer.write(" " + fsm.getGrammar().getTerminals().get(i) + " ");
				for (int j = 0; j < width[i+1] - 1; j++) writer.write(" ");
				writer.write("│");
			}
			writer.write("│");
			for (int i = 0; i < fsm.getGrammar().getNonTerminals().size(); i++) {
				writer.write(" " + fsm.getGrammar().getNonTerminals().get(i) + " ");
				for (int j = 0; j < width[i + fsm.getGrammar().getTerminals().size() + 1] - 1; j++) writer.write(" ");
				writer.write("│");
			}
			writer.write("\n");

			writer.write("├");
			for (int i = 0; i < width.length; i++) {
				for (int j = 0; j < width[i] + 2; j++) writer.write("─");
				if (i != width.length - 1) writer.write("┼");
				if (i == fsm.getGrammar().getTerminals().size() || i == 0) writer.write("┼");
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
					if (j == fsm.getGrammar().getTerminals().size() - 1) writer.write("│");
				}
				writer.write("\n");
			}

			writer.write("└");
			for (int i = 0; i < width.length; i++) {
				for (int j = 0; j < width[i] + 2; j++) writer.write("─");
				if (i != width.length - 1) writer.write("┴");
				if (i == fsm.getGrammar().getTerminals().size() || i == 0) writer.write("┴");
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
			for (int i = 0; i < fsm.getGrammar().getTerminals().size(); i++) writer.write("," + fsm.getGrammar().getTerminals().get(i));
			for (int i = 0; i < fsm.getGrammar().getNonTerminals().size(); i++) writer.write("," + fsm.getGrammar().getNonTerminals().get(i));
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
*/
}