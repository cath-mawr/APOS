@ECHO OFF
javac -Xlint:deprecation -cp bot.jar;lib/rsclassic.jar Scripts/*.java
echo Compilation completed.
pause
