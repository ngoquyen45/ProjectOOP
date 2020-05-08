"use strict"
module = angular.module("app.push", [ "app.token" ])
module.service("PushService", [ "$rootScope", "$q", "$token", "$log", "ADDRESS_WEBSOCKETS", "$timeout", ($rootScope, $q, $token, $log, ADDRESS_WEBSOCKETS, $timeout) ->

    MAX_RETRY_COUNT = 5
    RETRY_INTERVAL = 5 * 1000
    IDLE_RETRY_INTERVAL = 60 * 1000

    subscribes = []
    socket =
        sockJsClient: null
        stompClient: null
    restarting = false
    retryCount = 0

    registerSubscribe = (topic) ->
        defer = $q.defer()
        subscribes.push
            topic: topic
            defer: defer


        # If connection already open, subscribe topic
        subscribeStomp topic, defer  if socket.stompClient?
        defer.promise

    start = ->
        if $token.getUserInfo?
            startInternal()
        else
            $rootScope.$on('TokenVerified', () ->
                startInternal()
            )

    startInternal = ->
        if socket.stompClient? or socket.sockJsClient?
            try
                socket.stompClient.onclose = null
                socket.stompClient.disconnect()
            try
                socket.sockJsClient.onclose = null
                socket.sockJsClient.disconnect()

            socket.sockJsClient = null
            socket.stompClient = null

        sockJsClient = new SockJS(ADDRESS_WEBSOCKETS, {}, {})
        stompClient = Stomp.over(sockJsClient)

        accessToken = $token.getAccessToken()
        userId = if $token.getUserInfo()? then $token.getUserInfo().id else null
        # If not have user info, schedule to retry
        if not accessToken? or not userId?
            $log.debug("No user/token info")
            retryAfter(RETRY_INTERVAL)
            return

        stompClient.connect($token.getUserInfo().id, $token.getAccessToken(),
            ->
                retryCount = 0
                socket.sockJsClient = sockJsClient
                socket.stompClient = stompClient

                # Subscribe all topic
                i = 0
                while i < subscribes.length
                    subscribeStomp subscribes[i].topic, subscribes[i].defer
                    i++
                if restarting
                    restarting = false
                    $rootScope.$broadcast 'PushReconnected'
                else
                    $rootScope.$broadcast 'PushConnected'
                sockJsClient.onclose = reconnect
            , ->
                if retryCount < MAX_RETRY_COUNT then retryAfter(RETRY_INTERVAL) else retryAfter(IDLE_RETRY_INTERVAL)

        )

    retryAfter = (interval) ->
        retryCount++
        $log.debug("Fail to connect, schedule to retry after " + interval + " milis")
        $timeout (->
            $log.debug("Trying to connect[time=" + retryCount + "]")
            startInternal()
        ), interval

    restart = ->
        restarting = true
        start()

    send = (destination, message) ->
        socket.stompClient.send destination, {}, angular.toJson(message)

    subscribeStomp = (topic, defer) ->
        socket.stompClient.subscribe topic, (data) ->
            defer.notify angular.fromJson(data.body)


    reconnect = ->
        $log.debug("Connection closed unexpectedly, schedule to retry after " + RETRY_INTERVAL + " milis")
        $timeout (->
            $log.debug("Trying to reconnect...")
            restart()
        ), RETRY_INTERVAL

    $rootScope.$on("TokenChanged", () ->
        if socket.stompClient? then
        $log.debug("Token changed, reconnect to websockets endpoint")
        restart()
    )

    return {
        registerSubscribe: registerSubscribe
        start: start
        restart: restart
        send: send
    }
])
