Overview
--------
Folder Analyzer does a directory/folder walk, counts number of files in each
sub-directory and counts the total number of files in the given directory.

It is run from a java command shell library. Accepts folder name as mandatory and date filter as optional. On each execution it will produce csv output file containing list of all of the files in the directory, creation date of the file, number of files in each folder and number of total files.


Prerequisites
-------------
Java 8 installed on the OS

Build
-----
	mvn clean install

Run
---
On the command line execute

	java -jar target/folder-analyzer-1.0-SNAPSHOT.jar
	
This will start a spring shell interface for user to enter commands

Examples
--------
1. Analyze folder
	
	spring-shell>anlz /some-folder

	
This will produce following output
	
	Output saved into: Output-2017-11-25T14:09:20.284.csv

Content of the output file:

/some-folder/a/1,2017-10-29
/some-folder/a/2,2017-10-30
/some-folder/a/3,2017-10-30
/some-folder/a/4,2017-10-30
/some-folder/b/1,2017-10-30
/some-folder/b/2,2017-10-29
SUMMARY	
Folder /some-folder/b has 2 files	
Folder /some-folder/a has 4 files	
Folder /some-folder has 0 files	
Total # of files: 6	

In this example some-folder has two sub folders 'a' and 'b'. 'a' subfolder 
has 4 files: 1,2,3 and 4

1. Analyze folder for files created on a specific date
	
	spring-shell>anlz /some-folder --date 2017-10-30

	
This will produce following output
	
	Output saved into: Output-2017-11-25T14:23:58.362.csv

Content of the output file:

/some-folder/a/2,2017-10-30
/some-folder/a/3,2017-10-30
/some-folder/a/4,2017-10-30
/some-folder/b/1,2017-10-30
SUMMARY	
Folder /some-folder/b has 1 files	
Folder /some-folder/a has 3 files	
Folder /some-folder has 0 files	
Total # of files: 4	

In this example output produced contains only files created on 2017-10-30
