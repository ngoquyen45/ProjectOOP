#!/bin/bash

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
    BASE_PATH="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
    SOURCE="$(readlink "$SOURCE")"
    [[ $SOURCE != /* ]] && SOURCE="$BASE_PATH/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
BASE_PATH="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

# Create config file
JAVA_OPTS="-server"
JAVA_OPTS="$JAVA_OPTS -Xms256m -Xmx1024m -XX:MaxPermSize=256m -Xss1024k"
JAVA_OPTS="$JAVA_OPTS -XX:+UseParallelGC -XX:MaxGCPauseMillis=1500 -XX:GCTimeRatio=9 -XX:+DisableExplicitGC"
JAVA_OPTS="$JAVA_OPTS -Djava.io.tmpdir=$BASE_PATH/temp"
JAVA_OPTS="$JAVA_OPTS -Dlogging.path=$BASE_PATH/logs"
JAVA_OPTS="$JAVA_OPTS -Dspring.config.location=$BASE_PATH/conf/application.properties"

JAVA_OPTS="$JAVA_OPTS -Dspring.application.admin.enabled=true"
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.port=50201"
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.authenticate=false"
JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote.ssl=false"

echo "Base path: $BASE_PATH"

CONFIG_FILE="$BASE_PATH/bin/backend.conf"
echo "Creating service config file at: $CONFIG_FILE"
CONFIG="JAVA_OPTS=\"$JAVA_OPTS\""

echo -e "$CONFIG" > $CONFIG_FILE

# Remove existing
rm -rf /etc/init.d/dmsplus

ln -s "$BASE_PATH/bin/backend.war" /etc/init.d/dmsplus

chmod +x /etc/init.d/dmsplus

echo "Enable dmsplus service on reboot"
chkconfig --add dmsplus
chkconfig dmsplus on

echo -e "Service install at \"/etc/init.d/dmsplus\""
echo -e "JAVA_OPS can be configure at \"$CONFIG_FILE\""
echo -e "Usage:service dmsplus start | stop | restart | status"