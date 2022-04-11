README file for the 2020-2021 Concurrency and Multi-threading assignment

File structure

bin/run_data_structures
    script to start the application.
bin/run_data_structures.bat
    script to start the application for windows.
bin/run_data_structures2
    script to start the application with a different element type (String)
bin/run_data_structures.bat
    script to start the application for windows with a different element type (String)
bin/test_data_structures
    script to exercise the application a bit.
bin/test_data_structures.bat
    script to exercise the application a bit for windows.
build.gradle
gradle
gradlew
gradlew.bat
    gradle build scripts and directory.
report
    directory where you should put the PDF of your report.
README.txt
    this file.
src
    the source code for this project.

Building the application

Run gradle from the root directory (DataStructures)
$ ./gradlew

Testing the application
From the root directory run 
$ bin/test_data_structures

This will give the usage. An example of a correct run of the application is:
$ bin/test_data_structures cgl
(which would test your course-grained list implementation).

Running the application

From the root directory run 
$ bin/run_data_structures

This will give the usage. An example of a correct run of the application is:
$ bin/run_data_structures cgl 2 10000 0
The first argument indicates the version to use, the second argument is the number of worker threads,
the third argument is the number of items to add/remove, and the fourth argument is the number of
microseconds that the program should do work, between adding or removing items (think Amdahl's law).

Creating a submission
First put the pdf-file containing your report in the "report" directory. Then call
$ ./gradlew submit
This should create a "submit.zip" file that you can submit on Canvas.
