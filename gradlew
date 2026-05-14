#!/usr/bin/env sh
# Gradle start-up script for UN*X

# Attempt to set APP_HOME
PRG="$0"
while [ -h "$PRG" ] ; do
    ls_output=$(ls -ld "$PRG")
    link=$(expr "$ls_output" : '.*-> \(.*\)$')
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG="$(dirname "$PRG")/$link"
    fi
done
APP_HOME="$(cd "$(dirname "$PRG")"/ && pwd -P)"
export APP_HOME

# Add default JVM options
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or set to a maximum specified by the user
JAVA_MAX_MEM="${JAVA_MAX_MEM:-}"
JAVA_OPTS="${JAVA_OPTS} ${JAVA_MAX_MEM}"

# Use the Java home set by the user, or use the system Java home
JAVA_CMD="java"
if [ -n "$JAVA_HOME" ] ; then
    JAVA_CMD="$JAVA_HOME/bin/java"
fi

# Escape application args
save() {
    for i do printf %s\\n "$i" | sed "s/'/'\\\\''/g;1s/^/'/;\$s/\$/' \\\\/" ; done
    echo " "
}
APP_ARGS=$(save "$@")

# Collect all arguments for the java command
exec "$JAVA_CMD" \
    -Dorg.gradle.appname="$APP_BASE_NAME" \
    -Dorg.gradle.java.home="$JAVA_HOME" \
    $JAVA_OPTS \
    -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" \
    org.gradle.wrapper.GradleWrapperMain "$APP_ARGS"
