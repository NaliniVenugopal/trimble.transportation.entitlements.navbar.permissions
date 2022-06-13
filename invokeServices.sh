#!/bin/sh
export ENV=${APP_ENV}
export IS_CONTAINER="true"

export MONGODB_CONNECTION_STRING="$(cat /kvmnt/$ENV-mongodb-connection-string)"

REGION=$(env | grep NODE_NAME | cut -d"-" -f2)
echo $REGION

if [ "${REGION}" = "tcxloweast2" ] || [ "${REGION}" = "tcxstgeast2" ] || [ "${REGION}" = "tcxprodeast2" ] ; then
  export APP_REGION="east"
elif [ "${REGION}" = "tcxlowwest2" ] || [ "${REGION}" = "tcxstgwest2" ] || [ "${REGION}" = "tcxprodwest2" ] ; then
  export APP_REGION="west"
elif [ "${REGION}" = "tcxlowsindia" ]; then
  export APP_REGION="sindia"
fi


java -Djava.security.egd=file:/dev/./urandom -jar navbar-permissions-service.jar -P prod --server.port=80
