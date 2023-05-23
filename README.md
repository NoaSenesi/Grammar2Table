# Grammar2Table

Java program that takes a grammar as an input and gives a table as an output.

## Documentation

To run this program on a grammar file, you shall use the command `java g2t.Grammar2Table <file>`

Terminals and non-terminals are automatically determined. They can be any symbol apart from these: `; | = $`

Empty words (epsilon) are written as `^`

## Syntax

```
// S, X and Y are automatically detected to be non-terminals
# a, b, c, d, e and f are automatically detected to be terminals

S = aXb | c;
X = dX | eY;
Y = fX | ^;
```