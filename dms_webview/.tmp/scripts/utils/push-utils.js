(function() {
  "use strict";
  var module;

  module = angular.module("app.push", ["app.token"]);

  module.service("PushService", [
    "$rootScope", "$q", "$token", "$log", "ADDRESS_WEBSOCKETS", "$timeout", function($rootScope, $q, $token, $log, ADDRESS_WEBSOCKETS, $timeout) {
      var IDLE_RETRY_INTERVAL, MAX_RETRY_COUNT, RETRY_INTERVAL, reconnect, registerSubscribe, restart, restarting, retryAfter, retryCount, send, socket, start, startInternal, subscribeStomp, subscribes;
      MAX_RETRY_COUNT = 5;
      RETRY_INTERVAL = 5 * 1000;
      IDLE_RETRY_INTERVAL = 60 * 1000;
      subscribes = [];
      socket = {
        sockJsClient: null,
        stompClient: null
      };
      restarting = false;
      retryCount = 0;
      registerSubscribe = function(topic) {
        var defer;
        defer = $q.defer();
        subscribes.push({
          topic: topic,
          defer: defer
        });
        if (socket.stompClient != null) {
          subscribeStomp(topic, defer);
        }
        return defer.promise;
      };
      start = function() {
        if ($token.getUserInfo != null) {
          return startInternal();
        } else {
          return $rootScope.$on('TokenVerified', function() {
            return startInternal();
          });
        }
      };
      startInternal = function() {
        var accessToken, sockJsClient, stompClient, userId;
        if ((socket.stompClient != null) || (socket.sockJsClient != null)) {
          try {
            socket.stompClient.onclose = null;
            socket.stompClient.disconnect();
          } catch (_error) {}
          try {
            socket.sockJsClient.onclose = null;
            socket.sockJsClient.disconnect();
          } catch (_error) {}
          socket.sockJsClient = null;
          socket.stompClient = null;
        }
        sockJsClient = new SockJS(ADDRESS_WEBSOCKETS, {}, {});
        stompClient = Stomp.over(sockJsClient);
        accessToken = $token.getAccessToken();
        userId = $token.getUserInfo() != null ? $token.getUserInfo().id : null;
        if ((accessToken == null) || (userId == null)) {
          $log.debug("No user/token info");
          retryAfter(RETRY_INTERVAL);
          return;
        }
        return stompClient.connect($token.getUserInfo().id, $token.getAccessToken(), function() {
          var i;
          retryCount = 0;
          socket.sockJsClient = sockJsClient;
          socket.stompClient = stompClient;
          i = 0;
          while (i < subscribes.length) {
            subscribeStomp(subscribes[i].topic, subscribes[i].defer);
            i++;
          }
          if (restarting) {
            restarting = false;
            $rootScope.$broadcast('PushReconnected');
          } else {
            $rootScope.$broadcast('PushConnected');
          }
          return sockJsClient.onclose = reconnect;
        }, function() {
          if (retryCount < MAX_RETRY_COUNT) {
            return retryAfter(RETRY_INTERVAL);
          } else {
            return retryAfter(IDLE_RETRY_INTERVAL);
          }
        });
      };
      retryAfter = function(interval) {
        retryCount++;
        $log.debug("Fail to connect, schedule to retry after " + interval + " milis");
        return $timeout((function() {
          $log.debug("Trying to connect[time=" + retryCount + "]");
          return startInternal();
        }), interval);
      };
      restart = function() {
        restarting = true;
        return start();
      };
      send = function(destination, message) {
        return socket.stompClient.send(destination, {}, angular.toJson(message));
      };
      subscribeStomp = function(topic, defer) {
        return socket.stompClient.subscribe(topic, function(data) {
          return defer.notify(angular.fromJson(data.body));
        });
      };
      reconnect = function() {
        $log.debug("Connection closed unexpectedly, schedule to retry after " + RETRY_INTERVAL + " milis");
        return $timeout((function() {
          $log.debug("Trying to reconnect...");
          return restart();
        }), RETRY_INTERVAL);
      };
      $rootScope.$on("TokenChanged", function() {
        if (socket.stompClient != null) {

        }
        $log.debug("Token changed, reconnect to websockets endpoint");
        return restart();
      });
      return {
        registerSubscribe: registerSubscribe,
        start: start,
        restart: restart,
        send: send
      };
    }
  ]);

}).call(this);

//# sourceMappingURL=push-utils.js.map
