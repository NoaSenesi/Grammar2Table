echo "Compiling G2T..."
find -name "*.java" > sources.txt
if javac -d ./bin @sources.txt;
then
rm sources.txt
echo "Done!"
else
rm sources.txt
echo "Error!"
fi