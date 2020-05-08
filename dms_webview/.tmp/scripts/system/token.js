(function() {
  'use strict';
  angular.module('app.token', []).provider('$token', function() {
    var REQUIRED_AND_MISSING, defaultConfigs;
    REQUIRED_AND_MISSING = 'REQUIRED_AND_MISSING';
    defaultConfigs = {
      'client_id': REQUIRED_AND_MISSING,
      'authorizationEndpoint': REQUIRED_AND_MISSING,
      'scope': '',
      'revokeTokenEndpoint': '',
      'verifyTokenEndpoint': '',
      'changePasswordEndpoint': '',
      'logoutUrl': '',
      'redirect_url': '',
      'state_length': 5,
      'refresh': true,
      'min_live_time': 5 * 60 * 1000,
      'check_token_interval': 5 * 1000,
      'refresh_max_wait': 2 * 1000,
      'refresh_checklock_interval': 2 * 1000,
      'is_no_auth_page': function() {
        return false;
      }
    };
    return {
      extendConfig: function(configExtension) {
        defaultConfigs = angular.extend(defaultConfigs, configExtension);
        return defaultConfigs;
      },
      set: function() {
        var key, value;
        if (arguments.length !== 1 && typeof arguments[0] === 'object') {
          return this;
        }
        if (typeof arguments[0] !== 'string') {
          return this;
        }
        value = typeof arguments[1] === 'undefined' ? null : arguments[1];
        key = arguments[0];
        defaultConfigs[key] = value;
        return this;
      },
      $get: [
        '$http', '$rootScope', '$location', '$q', '$window', '$log', '$timeout', '$interval', '$state', function($http, $rootScope, $location, $q, $window, $log, $timeout, $interval, $state) {
          var addUrlParam, buildAuthorizationUrl, changePassword, clearToken, clearUserInfo, createRandomState, createRandomString, currentRenewTokenTaskTimeout, endsWith, getAccessToken, getRedirectUrl, getRoleCode, getState, getTokenExpiredIn, getUserInfo, getUserLanguage, isNoAuthPage, isOAuthFrame, key, login, logout, removeOAuthFrame, renewToken, renewTokenAfter, renewTokenTask, requiredAndMissing, revokeToken, setAccessToken, setTimeoutToRefresh, setTokenFromLocation, setUserInfo, setUserLanguage, updateProgressTask, verifyToken, verifyTokenPromise, wrap;
          requiredAndMissing = [];
          updateProgressTask = void 0;
          renewTokenTask = void 0;
          currentRenewTokenTaskTimeout = 0;
          setAccessToken = function(params) {
            var expiredIn, expiryDate, rawState, returnUrl, state;
            if (params['state'] == null) {
              $log.debug('State not exist, token rejected');
              return;
            }
            rawState = decodeURIComponent(params['state']);
            state = rawState.substring(0, defaultConfigs.state_length);
            if (state !== $window.localStorage['state']) {
              $log.debug('State not match, token rejected');
              return;
            }
            returnUrl = rawState.substring(defaultConfigs.state_length + 1);
            $window.localStorage['access_token'] = params['access_token'];
            $window.localStorage['token_type'] = params['token_type'];
            if (params['expires_in']) {
              expiryDate = new Date((new Date).getTime() + params['expires_in'] * 1000);
              $window.localStorage['expires_in'] = params['expires_in'];
              $window.localStorage['expiry_date'] = expiryDate;
            } else {
              $window.localStorage['expires_in'] = void 0;
              $window.localStorage['expiry_date'] = void 0;
            }
            if (!params['auto_refresh']) {
              if (params['lang']) {
                $window.localStorage['lang'] = params['lang'].toLowerCase();
              }
              if (defaultConfigs.refresh) {
                expiredIn = getTokenExpiredIn();
                if (expiredIn <= defaultConfigs.min_live_time) {
                  $log.error("Token validity must be > min_live_time");
                  defaultConfigs.min_live_time = parseInt(expiredIn / 3);
                  $log.error("The min_live_time was automatic set to " + defaultConfigs.min_live_time);
                }
              }
              setTimeoutToRefresh();
              if (returnUrl != null) {
                return $location.url(returnUrl);
              }
            }
          };
          getAccessToken = function() {
            if (getTokenExpiredIn() < defaultConfigs.min_live_time) {
              return void 0;
            }
            return $window.localStorage['access_token'];
          };
          getTokenExpiredIn = function() {
            var expiryDate;
            expiryDate = angular.isUndefined($window.localStorage['expiry_date']) ? null : Date.parse($window.localStorage['expiry_date']);
            if (expiryDate !== null) {
              return expiryDate - (new Date).getTime();
            } else {
              return 0;
            }
          };
          setTimeoutToRefresh = function() {
            if (!defaultConfigs.refresh) {
              $log.debug('Auto refresh token was turned off');
              return;
            }
            if (isOAuthFrame()) {
              $log.debug('Skip auto refresh token in OAuth iFrame');
              return;
            }
            return renewTokenAfter(0);
          };
          getRoleCode = function() {
            var userInfo;
            userInfo = getUserInfo();
            if (userInfo != null) {
              return userInfo['roleCode'];
            }
            return null;
          };
          endsWith = function(str, suffix) {
            return str.indexOf(suffix, str.length - suffix.length) !== -1;
          };
          logout = function() {
            var accessToken, doIt;
            accessToken = getAccessToken();
            doIt = function() {
              var lang;
              clearToken();
              clearUserInfo();
              if (defaultConfigs.logoutUrl && defaultConfigs.logoutUrl.length > 0) {
                lang = getUserLanguage();
                if (lang != null) {
                  return $window.location.href = addUrlParam(defaultConfigs.logoutUrl, 'lang', lang);
                } else {
                  return $window.location.href = defaultConfigs.logoutUrl;
                }
              }
            };
            if (accessToken && defaultConfigs.revokeTokenEndpoint && defaultConfigs.revokeTokenEndpoint.length > 0) {
              return revokeToken(accessToken).then(function() {
                return $log.info('Token revoked');
              }, function(e) {
                return $log.error('Token revoke error', e);
              })["finally"](function() {
                return doIt();
              });
            } else {
              return doIt();
            }
          };
          revokeToken = function(accessToken) {
            var configs, url;
            url = defaultConfigs.revokeTokenEndpoint;
            if (!endsWith(url, '/')) {
              url += '/';
            }
            url += '/' + accessToken;
            configs = {
              method: 'DELETE',
              url: url,
              headers: {
                'Authorization': 'Bearer ' + accessToken
              }
            };
            return $http(configs);
          };

          /**
           * Add a URL parameter (or changing it if it already exists)
           * @param {string} search  this is typically document.location.search
           * @param {string} key     the key to set
           * @param {string} val     value
           */
          addUrlParam = function(search, key, val) {
            var newParam, params;
            newParam = key + '=' + val;
            params = '?' + newParam;
            if (search != null) {
              params = search.replace(new RegExp('[?&]' + key + '[^&]*'), '$1' + newParam);
              if (params === search) {
                if (params.indexOf('?') === -1) {
                  params += '?';
                }
                params += '&' + newParam;
              }
            }
            return params;
          };
          clearToken = function() {
            return $window.localStorage.removeItem('access_token');
          };
          clearUserInfo = function() {
            return $window.localStorage.removeItem('userInfo');
          };
          getUserInfo = function() {
            var e, userInfo;
            userInfo = $window.localStorage['userInfo'];
            if (!userInfo) {
              return null;
            }
            try {
              return angular.fromJson(userInfo);
            } catch (_error) {
              e = _error;
              $window.localStorage['userInfo'] = null;
            }
            return null;
          };
          setUserInfo = function(userInfo) {
            return $window.localStorage['userInfo'] = angular.toJson(userInfo);
          };
          getUserLanguage = function() {
            return $window.localStorage['lang'];
          };
          setUserLanguage = function(lang) {
            return $window.localStorage['lang'] = lang;
          };
          verifyTokenPromise = null;
          verifyToken = function() {
            var defer;
            if (verifyTokenPromise == null) {
              if ((defaultConfigs.verifyTokenEndpoint == null) || defaultConfigs.verifyTokenEndpoint.length === 0) {
                throw new Error('Please config \'verifyTokenEndpoint\' before use this function');
              }
              defer = $q.defer();
              if (getAccessToken() === null) {
                login();
              }
              $http.get(defaultConfigs.verifyTokenEndpoint).success(function(userinfo) {
                setUserInfo(userinfo);
                $log.debug('Token was verified');
                $rootScope.$broadcast('TokenVerified');
                defer.resolve(userinfo);
                return verifyTokenPromise = null;
              }).error(function(error) {
                $log.debug('Cannot verify token: ' + error);
                defer.reject(error);
                return verifyTokenPromise = null;
              });
              verifyTokenPromise = defer.promise;
            }
            return verifyTokenPromise;
          };
          setTokenFromLocation = function() {
            var search;
            search = $location.search();
            setAccessToken(search);
            $location.search('access_token', null);
            $location.search('token_type', null);
            $location.search('expires_in', null);
            $location.search('flag', null);
            $location.search('lang', null);
            $location.search('auto_refresh', null);
            $location.search('state', null);
            if (isOAuthFrame()) {
              $log.debug('Token was renewed using iFrame');
              return removeOAuthFrame();
            } else {
              return $log.debug('Token was saved');
            }
          };
          isOAuthFrame = function() {
            return $window.parent && $window.parent.document.getElementById('oauth_frame');
          };
          removeOAuthFrame = function() {
            var iFrame;
            iFrame = $window.parent.document.getElementById('oauth_frame');
            if (iFrame) {
              return iFrame.parentNode.removeChild(iFrame);
            }
          };
          getRedirectUrl = function() {
            var redirect_uri;
            redirect_uri = $state.href('callback', {}, {
              absolute: true
            });
            return redirect_uri + '?';
          };
          getState = function() {
            var prefix, suffix;
            suffix = $location.url();
            prefix = createRandomState();
            return encodeURIComponent(prefix + '_' + suffix);
          };
          createRandomString = function(length) {
            var i, possible, text;
            text = '';
            possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
            i = 0;
            while (i < length) {
              text += possible.charAt(Math.floor(Math.random() * possible.length));
              i++;
            }
            return text;
          };
          createRandomState = function() {
            var state;
            state = createRandomString(defaultConfigs.state_length);
            $window.localStorage['state'] = state;
            return state;
          };
          buildAuthorizationUrl = function(extendParams) {
            var params, url;
            params = {
              response_type: 'token',
              client_id: defaultConfigs.client_id,
              redirect_uri: getRedirectUrl(),
              scope: defaultConfigs.scope,
              state: getState()
            };
            params = angular.extend(params, extendParams);
            url = defaultConfigs.authorizationEndpoint;
            angular.forEach(params, function(value, key) {
              return url = addUrlParam(url, encodeURIComponent(key), encodeURIComponent(value));
            });
            return url;
          };
          login = function() {
            var search;
            search = $location.search();
            if (search.access_token != null) {
              setTokenFromLocation();
              return;
            }
            return $window.location.href = buildAuthorizationUrl();
          };
          renewTokenAfter = function(timeout) {
            if (timeout === 0) {
              return renewToken();
            } else {
              if (renewTokenTask != null) {
                if (timeout !== currentRenewTokenTaskTimeout) {
                  $interval.cancel(renewTokenTask);
                  currentRenewTokenTaskTimeout = timeout;
                  return renewTokenTask = $interval((function() {
                    return renewToken();
                  }), timeout);
                }
              } else {
                currentRenewTokenTaskTimeout = timeout;
                return renewTokenTask = $interval((function() {
                  return renewToken();
                }), timeout);
              }
            }
          };
          renewToken = function() {
            var currentTime, diff, iFrame, lastUpdate, tokenExpiredIn;
            tokenExpiredIn = getTokenExpiredIn();
            if (tokenExpiredIn > defaultConfigs.min_live_time) {
              renewTokenAfter(defaultConfigs.check_token_interval);
              return;
            }
            if ($window.localStorage['refresh_locked']) {
              currentTime = (new Date).getTime();
              lastUpdate = $window.localStorage['refresh_last_update'] != null ? Date.parse($window.localStorage['refresh_last_update']) : null;
              if (lastUpdate > 0) {
                diff = currentTime - lastUpdate;
                if (diff > 0 && diff <= defaultConfigs.refresh_max_wait) {
                  renewTokenAfter(defaultConfigs.refresh_checklock_interval);
                  return;
                }
              }
            }
            $window.localStorage['refresh_locked'] = true;
            $window.localStorage['refresh_last_update'] = new Date();
            removeOAuthFrame();
            if (updateProgressTask != null) {
              $interval.cancel(updateProgressTask);
              updateProgressTask = void 0;
            }
            updateProgressTask = $interval((function() {
              return $window.localStorage['refresh_last_update'] = new Date();
            }), 1000);
            iFrame = document.createElement('iframe');
            iFrame.setAttribute('src', buildAuthorizationUrl({
              'auto_refresh': 'true'
            }));
            iFrame.setAttribute('id', 'oauth_frame');
            iFrame.style.display = 'none';
            document.body.appendChild(iFrame);
            $log.debug('Refreshing access token using iFrame');
            return renewTokenAfter(defaultConfigs.check_token_interval);
          };
          isNoAuthPage = function(toState, toParams) {
            return defaultConfigs.is_no_auth_page(toState, toParams);
          };
          changePassword = function(oldPassword, newPassword) {
            var defer, params;
            if ((defaultConfigs.changePasswordEndpoint == null) || defaultConfigs.changePasswordEndpoint.length === 0) {
              throw new Error('Please config \'changePasswordEndpoint\' before use this function');
            }
            defer = $q.defer();
            params = {
              newPassword: newPassword,
              oldPassword: oldPassword
            };
            $http.put(defaultConfigs.changePasswordEndpoint, params).success(function(data) {
              $rootScope.$broadcast('PasswordChanged');
              return defer.resolve(data);
            }).error(function(error) {
              return defer.reject(error);
            });
            return defer.promise;
          };
          for (key in defaultConfigs) {
            if (defaultConfigs[key] === REQUIRED_AND_MISSING) {
              requiredAndMissing.push(key);
            }
          }
          if (requiredAndMissing.length > 0) {
            throw new Error('TokenProvider is insufficiently configured. Please configure the following options using $tokenProvider.extendConfig: ' + requiredAndMissing.join(', '));
          }
          if (!isOAuthFrame()) {
            wrap = function(event) {
              var newToken, oldToken;
              if (event.key === 'access_token' && event.newValue !== event.oldValue) {
                oldToken = event.oldValue;
                newToken = event.newValue;
                if (updateProgressTask) {
                  $interval.cancel(updateProgressTask);
                  updateProgressTask = void 0;
                  $window.localStorage['refresh_locked'] = void 0;
                  $window.localStorage['refresh_last_update'] = void 0;
                }
                return $rootScope.$broadcast('TokenChanged', oldToken, newToken);
              }
            };
            if ($window.addEventListener) {
              $window.addEventListener('storage', wrap, false);
            } else {
              $window.attachEvent('onstorage', wrap);
            }
            if (getAccessToken()) {
              setTimeoutToRefresh();
            }
          }
          return {
            login: login,
            logout: logout,
            isNoAuthPage: isNoAuthPage,
            getAccessToken: getAccessToken,
            extendConfig: this.extendConfig,
            clearToken: clearToken,
            verifyToken: verifyToken,
            setTokenFromLocation: setTokenFromLocation,
            clearUserInfo: clearUserInfo,
            getUserInfo: getUserInfo,
            setUserInfo: setUserInfo,
            getUserLanguage: getUserLanguage,
            setUserLanguage: setUserLanguage,
            isOAuthFrame: isOAuthFrame,
            getRoleCode: getRoleCode,
            changePassword: changePassword
          };
        }
      ]
    };
  }).config([
    '$httpProvider', function($httpProvider) {
      return $httpProvider.interceptors.push([
        '$injector', function($injector) {
          return {
            request: function(config) {
              config.headers = config.headers || {};
              $injector.invoke([
                '$token', function($token) {
                  if ($token.getAccessToken() != null) {
                    return config.headers.Authorization = 'Bearer ' + $token.getAccessToken();
                  }
                }
              ]);
              return config;
            },
            responseError: function(resp) {
              if (resp.status === 401) {
                $injector.invoke([
                  '$token', function($token) {
                    $token.clearToken();
                    $token.clearUserInfo();
                    return $token.login();
                  }
                ]);
              }
              if (resp.status === -1) {
                $injector.invoke([
                  '$state', function($state) {
                    return $state.go('500');
                  }
                ]);
              }
              return $injector.get('$q').reject(resp);
            }
          };
        }
      ]);
    }
  ]).run([
    '$rootScope', '$log', '$location', '$state', '$token', function($rootScope, $log, $location, $state, $token) {
      $rootScope.$on('$locationChangeStart', function(event) {
        var search;
        search = $location.search();
        if ((search != null) && (search.access_token != null)) {
          $token.setTokenFromLocation();
          return event.preventDefault();
        }
      });
      return $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
        var access_token, promise, tokenVerified;
        console.log('$stateChangeStart in token.js');
        if ($token.isOAuthFrame()) {
          event.preventDefault();
          return;
        }
        if (!$token.isNoAuthPage(toState)) {
          access_token = $token.getAccessToken();
          tokenVerified = $rootScope.tokenVerified;
          if (access_token != null) {
            if (($token.getUserInfo() != null) && tokenVerified) {
              return $rootScope.$broadcast('$stateChangeStart:tokenVerified', toState, fromState, event);
            } else {
              promise = $token.verifyToken();
              return promise.then(function() {
                $rootScope.tokenVerified = true;
                return $rootScope.$broadcast('$stateChangeStart:tokenVerified', toState, fromState, event);
              }, function(error) {
                event.preventDefault();
                if ((error != null) && error.status === 401) {

                } else {
                  return $rootScope.$broadcast('$token:failToVerifyToken');
                }
              });
            }
          } else {
            event.preventDefault();
            if (($rootScope.preventOtherLogin != null) && $rootScope.preventOtherLogin) {
              return;
            }
            $token.login();
            return $rootScope.preventOtherLogin = true;
          }
        }
      });
    }
  ]);

}).call(this);

//# sourceMappingURL=token.js.map
