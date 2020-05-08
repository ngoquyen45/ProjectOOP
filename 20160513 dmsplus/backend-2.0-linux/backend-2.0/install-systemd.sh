#!/bin/bash

FS_USER=dmsplus

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
    BASE_PATH="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
    SOURCE="$(readlink "$SOURCE")"
    [[ $SOURCE != /* ]] && SOURCE="$BASE_PATH/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
BASE_PATH="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

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

SERVICE_FILE="/etc/systemd/system/dmsplus.service"
echo "Creating service file at: $SERVICE_FILE"

CONTENT="[Unit]"
CONTENT="$CONTENT\nDescription=DMSPlus backend service"
CONTENT="$CONTENT\nAfter=syslog.target"
CONTENT="$CONTENT\n"

CONTENT="$CONTENT\n[Service]"
CONTENT="$CONTENT\nExecStart=/bin/sh -c 'java $JAVA_OPTS -jar \"$BASE_PATH/bin/backend.war\"'"
CONTENT="$CONTENT\nSuccessExitStatus=143"

CONTENT="$CONTENT\n"
CONTENT="$CONTENT\n[Install]"
CONTENT="$CONTENT\nWantedBy=multi-user.target"
echo -e "$CONTENT" > $SERVICE_FILE
chmod 664 $SERVICE_FILE

echo "Reloading deamon"
systemctl daemon-reload

echo "Enable dmsplus service on reboot"
systemctl enable dmsplus

echo -e "Service installed at $SERVICE_FILE"
echo -e "JAVA_OPS can be configure at \"$SERVICE_FILE\""
echo -e "After update service file: Please run: 'systemctl daemon-reload'"
echo -e "Usage:service dmsplus start | stop | restart | status"
