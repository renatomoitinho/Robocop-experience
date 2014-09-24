
1) git clone https://github.com/renatomoitinho/robotscop-test.git

2) download jar file for your platform
https://code.google.com/p/jav8/

jav8-jsr223-Mac OS X-x86_64-0.6.jar
jav8-jsr223-linux-amd64-0.6.jar
jav8-jsr223-win-amd64-0.6.jar

3) force dependency in maven

mvn install:install-file \
  -DgroupId=engine.transaction \
  -DartifactId=jv8 \
  -Dpackaging=jar \
  -Dversion=1.0 \
  -Dfile=<path you jar> \
  -DgeneratePom=true

run maven

4) mvn clean install

5) go to folder target and extract robocop-package.zip and execute jar file

java -jar robocop-jar-with-dependencies.jar

check in browser http://localhost:9000


:)


