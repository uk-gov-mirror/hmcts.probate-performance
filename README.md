### Probate Gatling Performance Tests

Gatling performance tests for the Probate citizen application 

**Initial setup:**
- After cloning, run `git submodule update --init --recursive` to populate the common-performance submodule code
- Run `./gradlew build` to rebuild with the submodule code

**To keep the code up to date with the latest common-performance code:**
- `git submodule update --recursive --remote`
- `./gradlew build`

For further details on the submodule, refere to https://github.com/hmcts/common-performance/

**To run locally:**
- Run `az login` using Azure CLI, as authentication is required to retrieve secrets from the Key Vault
- Performance test against the perftest environment: `./gradlew gatlingRun`

**Flags:**
- Debug (single-user mode): `-Ddebug=on e.g. ./gradlew gatlingRun -Ddebug=on`
- Run against AAT: `Denv=aat e.g. ./gradlew gatlingRun -Denv=aat`

> 📢 **Notes:**
>- Ensure JAVA_HOME for the repo's directory is set to a Java 21 installation
>- Run `./gradlew -v` to confirm the version of Java being used by Gradle
>- The code must be compiled and executed against the same version of Java
>- Ensure your IDE is configured to use appropriate software versions (as specified in the build.gradle)
