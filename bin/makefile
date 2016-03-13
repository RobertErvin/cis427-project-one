#
# A simple makefile for compiling three java classes
#

# define a makefile variable for the java compiler
#
JC = javac
JVM = java 
# define a makefile variable for compilation flags
# the -g flag compiles with debugging information
#
JFLAGS = -g

.SUFFIXES: .java .class

.java.class: ; $(JC) $(JFLAGS) $*.java

CLASSES = \
	projectPackage/Constants.java \
	projectPackage/User.java \
	projectPackage/MotdService.java \
	projectPackage/UserService.java \
	projectPackage/MultithreadedSocketServer.java \
	projectPackage/ChildThread.java

MAIN = projectPackage/MultithreadedSocketServer

# typing 'make' will invoke the first target entry in the makefile 
# (the default one in this case)
default: classes

run: classes
	$(JVM) $(MAIN)

classes: $(CLASSES:.java=.class)

# To start over from scratch, type 'make clean'.  
# Removes all .class files, so that the next make rebuilds them
#
clean: ; $(RM) projectPackage/*.class
