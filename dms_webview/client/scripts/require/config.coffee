'use strict'

angular.module('app')

.constant('ADDRESS_BACKEND',    'https://dms.techqila.com:8443/api/')
.constant('ADDRESS_OAUTH',      'https://dms.techqila.com:8443/')
.constant('ADDRESS_WEBSOCKETS', 'https://dms.techqila.com:8443/websocket')
.constant('ADDRESS_PING',       'https://dms.techqila.com:8443/account/ping')

# Default language
.constant("DEFAULT_LANGUAGE", "en")

# Time out: 30 mins
.constant("NG_IDLE_MAX_IDLE_TIME", 60 * 30)
# Count down time before logout: 8 secs
.constant("NG_IDLE_TIMEOUT", 8)
# Auto-ping interval: 10 mins
.constant("NG_IDLE_PING_INTEVAL", 60 * 10)
