- Fetch submodule
  - `git submodule update --init`
- Compile protobuffers
  - `mvn --settings ./settings.xml clean install`
- Download fastfiz and make a java bundle named `libfastfiz.so`
  - Put this lib in `src/main/resources/lib`

