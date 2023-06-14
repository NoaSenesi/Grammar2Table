# Grammar2Table

Java program that takes a grammar as an input and gives a table as an output.

## Documentation

You will need Java to be installed in order to use the program.

To run this program on a grammar file, you first need to compile it with `build` for Windows or `sh build.sh` for Linux.
Then you can use the command `g2t <file> [options]` for Windows or `sh g2t.sh <file> [options]` for Linux.

Terminals and non-terminals are automatically determined. They can be any symbol apart from the end of file symbol `$`
Non-alphanumerical symbols are automatically determined to be terminals, and they are always separated.
Empty words (epsilon) are written as `^`

### Options

- `-s, --show-states`: Shows all states of the finite state machine
- `-p[N], --optimize-csv[=N]`: Optimize CSV file with level N (default: 1)

#### Optimize CSV
0. No optimization
1. Remove ERROR actions
2. Remove leading commas and state number
3. Replace action type by a single character

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
