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
	private boolean quiet = false;

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
					ActionType type = fsm.getGrammar().getNonTerminals().contains(token) ? ActionType.GOTO : ActionType.SHIFT;
					table[i][j] = new Action(type, s.shift(token));
				}
			}
		}
	}

	public Action[][] getTable() {
		if (table == null) buildTable();

		return table;
	}

	private boolean areLinesConflictual(Action[] line1, Action[] line2, List<List<State>> duplicates) {
		for (int i = 0; i < line1.length; i++) {
			Action action1 = line1[i], action2 = line2[i];

			if (action1.getType() == ActionType.ERROR || action2.getType() == ActionType.ERROR) continue;
			if (action1.getType() == ActionType.ACCEPT || action2.getType() == ActionType.ACCEPT) return true;
			if (action1.getType() != action2.getType()) return true;

			if (action1.getType() == ActionType.REDUCE && !action1.getRule().coreEquals(action2.getRule())) return true;

			if (action1.getType() == ActionType.SHIFT || action1.getType() == ActionType.GOTO) {
				for (List<State> duplicate : duplicates) {
					if (!duplicate.contains(action1.getState()) || !duplicate.contains(action2.getState())) return true;
				}
			}
		}

		return false;
	}

	public void setQuiet() {
		quiet = true;
	}

	private void separateConflicts(List<List<State>> duplicates) {
		Action[][] table = getTable();
		List<List<State>> last = new ArrayList<>();

		while (!duplicates.equals(last)) {
			last = new ArrayList<>(duplicates);

			for (int i = 0; i < duplicates.size(); i++) {
				List<State> group = duplicates.get(i);
				List<List<State>> newGroups = new ArrayList<>();

				for (int a = 0; a < group.size() - 1; a++) {
					boolean aIsConflictual = false;

					for (int b = a + 1; b < group.size(); b++) {
						if (areLinesConflictual(table[group.get(a).getId()], table[group.get(b).getId()], duplicates)) {
							aIsConflictual = true;
							break;
						}
					}

					if (!aIsConflictual) continue;

					group.remove(a);

					for (List<State> newGroup : newGroups) {
						if (!areLinesConflictual(table[a], table[newGroup.get(0).getId()], duplicates)) {
							newGroup.add(group.get(a));
							aIsConflictual = false;
							break;
						}
					}

					if (aIsConflictual) {
						List<State> newGroup = new ArrayList<>();
						newGroup.add(group.get(a));
						newGroups.add(newGroup);
					}
				}

				if (newGroups.size() > 0) {
					duplicates.addAll(newGroups);
				}

				if (group.size() == 1) duplicates.remove(i--);
			}
		}
	}

	private void mergeLines(Action[] line1, Action[] line2) {
		// Lines are supposedly non-conflictual

		for (int i = 0; i < line1.length; i++) {
			Action action1 = line1[i], action2 = line2[i];

			if (action1.getType() == ActionType.ERROR) line1[i] = action2;
		}
	}

	private void mergeTable(List<List<State>> duplicates) {
		int count = 0;
		for (List<State> duplicate : duplicates) count += duplicate.size() - 1;

		Action[][] table = getTable(), newTable = new Action[table.length - count][table[0].length];

		for (int i = 0, newTableIndex = 0; i < table.length; i++) {
			boolean isDuplicate = false;

			for (List<State> group : duplicates) {
				for (State s : group) {
					if (s.getId() == i) {
						isDuplicate = true;
						break;
					}
				}

				if (isDuplicate && group.get(0).getId() == i) {
					newTable[newTableIndex] = table[i];

					for (int j = 1; j < group.size(); j++) {
						mergeLines(newTable[newTableIndex], table[group.get(j).getId()]);
					}

					newTableIndex++;
					break;
				}
			}

			if (!isDuplicate) newTable[newTableIndex++] = table[i];
		}

		this.table = newTable;

		if (!quiet) System.out.println(count + " state" + (count > 1 ? "s" : "") + " merged, " + newTable.length + " state" + (newTable.length > 1 ? "s" : "") + " left");
	}

	private void updateStateCount(List<List<State>> duplicates) {
		int index = 0;

		for (State s : fsm.getStates()) {
			boolean inDuplicate = false;

			for (List<State> group : duplicates) {
				if (group.contains(s)) {
					inDuplicate = true;

					if (group.get(0).getId() == s.getId()) s.setId(index++);
					else s.setId(group.get(0).getId());

					break;
				}
			}

			if (!inDuplicate) s.setId(index++);
		}
	}

	public void compact() {
		if (!quiet) System.out.println("Compacting table...");

		List<List<State>> duplicates = fsm.findDuplicates();
		separateConflicts(duplicates);
		mergeTable(duplicates);
		updateStateCount(duplicates);
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
		saveCSV(filename, 0);
	}

	public void saveCSV(String filename, int optimizeCSVLevel) {
		File file = new File(filename);
		if (file.exists()) file.delete();

		try {
			file.createNewFile();
			FileWriter writer = new FileWriter(file);

			if (optimizeCSVLevel < 2) writer.write("State");
			for (int i = 0; i < tokens.size(); i++) {
				writer.write((i == 0 && optimizeCSVLevel >= 2 ? "" : ",") + tokens.get(i));
			}

			for (int i = 0; i < getTable().length; i++) {
				if (optimizeCSVLevel <= 1) writer.write("\nI" + i);
				else writer.write("\n");

				String line = "";

				for (int j = 0; j < getTable()[i].length; j++) {
					Action action = getTable()[i][j];

					if (optimizeCSVLevel <= 1 || j != 0) line += ",";
					if (optimizeCSVLevel == 0 || action.getType() != ActionType.ERROR) {
						String part = action.toString();

						if (optimizeCSVLevel >= 3) {
							String[] parts = part.split(" ");
							parts[0] = String.valueOf(parts[0].charAt(0));

							if (action.getType() == ActionType.SHIFT || action.getType() == ActionType.GOTO) parts[1] = parts[1].substring(1, parts[1].length());
							else if (action.getType() == ActionType.REDUCE) {
								if (optimizeCSVLevel >= 4) {
									parts[2] = parts[3].equals("^") ? "0" : String.valueOf(parts.length - 3);

									for (int n = 3; n < parts.length; n++) parts[n] = "";
								}

								else parts[2] = "=";

								if (optimizeCSVLevel >= 5) parts[1] = tokens.indexOf(parts[1]) + " ";
							}

							part = String.join(optimizeCSVLevel >= 5 ? "" : " ", parts).trim();
						}

						line += part;
					}
				}

				if (optimizeCSVLevel >= 2) line = line.replaceAll(",*$", "");

				writer.write(line);
			}

			writer.close();

			if (fsm.getGrammar().getTerminals().contains(",")) System.out.println("Warning: CSV file contains comma in terminals. It may cause problems when using it as input.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}