This is the README file for my Scatterplot.java class/program.

In order to compile this program, you must have JavaFX, which comes
included in all JDK's after java 6. 

THIS IS IMPORTANT:

================================COMPILATION============================================

To compile the program, you must use this command

javac Scatterplot.java -Xlint

Without the -Xlint flag, you will not be able to compile and run the program.
This is because java (even though they include this by default in the JDK now)
does not recognize JavaFX as part of the public API. SO you NEED to use that flag.

When it compiles, you will see a lot of warnings come up. This is because I didn't
use the parameterized types like I should have. The program is COMPLETELY FUNCTIONAL,
however, so do not worry about it.

To run, simply use the command

java Scatterplot

Voila! 

One more thing, it is very important that the "pointstyles.css" file is contained
within whatever directory in which you execute the program. Without it, the default
styles will be used and the data will not make any sense, or at least it will be very hard
to interpret.

=======================================USAGE===========================================

In order to use the program, first click "initialize."

Then, click "next" to generate the specified k-number of means.

Keep clicking next to see the points be clustered, and then moved as the means move.

In order to restart this process, simply click "initialize" again and repeat. The number
"k" is ONLY taken into account after you click "next" for the first time after "initialize."
That number will have no bearing on the following steps until you click "initialize" again.

