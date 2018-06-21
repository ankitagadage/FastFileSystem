### Project Title ###
JNachos v1.0 - Implementing Unix Fast File System

### Getting Started ###
Download the JNachos source code from Prof. Patrick McSweeney's page.
http://www.cis.syr.edu/~pjmcswee/cis657/

### Prerequisites ###
Install Java and set the JAVA_HOME environment variable.
You can use a Java IDE like Netbeans, Eclipse, IntelliJ

### Installing ###
1) Install Java
2) Install Java IDE
3) Download JNachos zip file from Prof. Patrick McSweeney's page.
4) Unzip and import the Java project in the IDE.

### Unix Fast File System ###
1) Implemented file growth algorithm in Jnachos according to the Unix fast file system research paper.
2) Divided a block into fragements and implemented read and write at sector level.
3) Divided every block into two fragments.
4) Whenever the file goes out of space, the expandFile() method runs and allocates new fragments/blocks for the new content as mentioned in the paper.

### Running and Testing ###
1) Simply run the jnachos project with command line argument as "-f<space>true".
2) This invokes a kernel process which runs the demo of file content being added to file in stages and retrieves the original content at the end.

### Contribution ###
Prof. Patrick McSweeney (EECS Department, Syracuse University)
Ankita Gadage (EECS, Masters in Computer Science, 2017-2019)
Swapnil Borse (EECS, Masters in Computer Science, 2017-2019)

### Versioning ###
Used Bitbucket for version control. Project developed on master branch.

### Acknowledgements ###
Prof. Patrick McSweeney
Wikipedia
