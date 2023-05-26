echo "Compiling G2T..."
if javac -d ./bin ./src/fr/senesi/g2t/*.java
then
echo "Done!"
else
echo "Error!"
fi