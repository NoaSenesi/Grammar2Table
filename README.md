# Grammar2Table

Java program that takes a grammar as an input and gives a table as an output.

## Documentation

You will need Java to be installed in order to use the program.

To run this program on a grammar file, you first need to compile it with `build` for Windows or `./build` for Linux.
Then you can use the command `g2t <file> [options]` for Windows or `./g2t <file> [options]` for Linux.

Terminals and non-terminals are automatically determined. They can be any symbol apart from the end of file symbol `$`
Non-alphanumerical symbols are automatically determined to be terminals, and they are always separated.
Empty words (epsilon) are written as `^`

### Options

- `-s, --show-states`: Shows all states of the finite state machine
- `-n, --no-table`: Prevents the table from exporting
- `-q, --quiet`: Quiet mode, only show errors
- `-c, --compact`: Merge states to remove doubles when no ambiguity
- `-p[N], --optimize-csv[=N]`: Optimize CSV file with level N (default: 1)

#### Optimize CSV
0. No optimization
1. Remove ERROR actions
2. Remove leading commas and state number
3. Replace action type by a single character, replace arrow by equals and removes "I" in states
4. Replace REDUCE action rules by the non-terminal to reduce to and the number of stack pop to do
5. Remove first space after the first letter and replace non-terminals by their index at the top

## Syntax

```
// S, NonTerm and OtherNonTerm are automatically detected to be non-terminals
# a, b, c, d, e, f, \= and + are automatically detected to be terminals
/*
With an anti-slash,
you can escape the following characters: | ; = \
*/

S = a NonTerm b | c;
NonTerm = d NonTerm | e \= a OtherNonTerm;
OtherNonTerm = f NonTerm + e | ^;
```

***

<div style="text-align:center">
	<img src="resources/logo.png" width="200">
</div>