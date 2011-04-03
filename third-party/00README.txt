




apache-ant-1.8.0-bin.tar.bz2 - Obtained at
http://mirrors.fe.up.pt/pub/apache/ant/binaries/apache-ant-1.8.0-bin.tar.bz2

junit4.8.2.zip - Obtained at
http://github.com/downloads/KentBeck/junit/junit4.8.2.zip
  Note for JUnit upgraders: In the toplevel build.xml file, there is an ant
  target java-test.config.create that extracts the bundled junit-4.8.2.jar
  into the devtools/lib/jars/junit-4.8.2.jar and creates the build.conf
  property file with the path to this jar. Please preserve this funtionality
  needed for the ant test targets to self-setup correctly (and independently of
  other JUnit libraries that may exist in the development environment).