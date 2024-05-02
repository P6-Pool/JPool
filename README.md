- Fetch submodule
  - `git submodule update --init`
- Generate and install dependencies
  - `mvn clean install --settings ./settings.xml`
- Build jar
  - `mvn package --settings ./settings.xml`
- Run jar
  - `java -jar {path-to-target}/CueCraft-1.0-SNAPSHOT-jar-with-dependencies.jar`

