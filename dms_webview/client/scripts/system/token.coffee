'use strict'

angular.module('app.token', [])

.provider '$token', ->
    REQUIRED_AND_MISSING = 'REQUIRED_AND_MISSING'
    defaultConfigs =
        'client_id': REQUIRED_AND_MISSING
        'authorizationEndpoint': REQUIRED_AND_MISSING
        'scope': ''
        'revokeTokenEndpoint': ''
        'verifyTokenEndpoint': ''
        'changePasswordEndpoint': ''
        'logoutUrl': ''
        'redirect_url': ''
        'state_length': 5
        'refresh': true
        'min_live_time': 5 * 60 * 1000
        'check_token_interval': 5 * 1000
        'refresh_max_wait': 2 * 1000
        'refresh_checklock_interval': 2 * 1000
        'is_no_auth_page': -> false

    extendConfig: (configExtension) ->
        defaultConfigs = angular.extend(defaultConfigs, configExtension)
        defaultConfigs

    set: ->
        if arguments.length != 1 and typeof arguments[0] == 'object'
            return this
        if typeof arguments[0] != 'string'
            return this
        value = if typeof arguments[1] == 'undefined' then null else arguments[1]
        key = arguments[0]
        defaultConfigs[key] = value
        this

    $get: ['$http', '$rootScope', '$location', '$q', '$window', '$log', '$timeout', '$interval', '$state', ($http, $rootScope, $location, $q, $window, $log, $timeout, $interval, $state) ->
        requiredAndMissing = []
        updateProgressTask = undefined
        renewTokenTask = undefined
        currentRenewTokenTaskTimeout = 0

        setAccessToken = (params) ->
            # Check if same state
            if not params['state']?
                $log.debug 'State not exist, token rejected'
                return

            rawState = decodeURIComponent(params['state'])
            state = rawState.substring(0, defaultConfigs.state_length)
            if state isnt $window.localStorage['state']
                $log.debug 'State not match, token rejected'
                return

            # Get returning url
            returnUrl = rawState.substring(defaultConfigs.state_length + 1)

            $window.localStorage['access_token'] = params['access_token']
            $window.localStorage['token_type'] = params['token_type']
            if params['expires_in']
                expiryDate = new Date((new Date).getTime() + params['expires_in'] * 1000)
                $window.localStorage['expires_in'] = params['expires_in']
                $window.localStorage['expiry_date'] = expiryDate
            else
                $window.localStorage['expires_in'] = undefined
                $window.localStorage['expiry_date'] = undefined

            if !params['auto_refresh']
                if params['lang'] then $window.localStorage['lang'] = params['lang'].toLowerCase()

                # Check token validity with cofigured parameter
                if defaultConfigs.refresh
                    expiredIn = getTokenExpiredIn()
                    if expiredIn <= defaultConfigs.min_live_time
                        $log.error("Token validity must be > min_live_time")
                        defaultConfigs.min_live_time = parseInt(expiredIn / 3)
                        $log.error("The min_live_time was automatic set to " + defaultConfigs.min_live_time)

                # Schedule to refresh
                setTimeoutToRefresh()
                # Redirect
                if returnUrl?
                    $location.url(returnUrl)


        getAccessToken = ->
            if getTokenExpiredIn() < defaultConfigs.min_live_time
                return undefined
            $window.localStorage['access_token']

        getTokenExpiredIn = ->
            expiryDate = if angular.isUndefined($window.localStorage['expiry_date']) then null else Date.parse($window.localStorage['expiry_date'])
            if expiryDate != null then expiryDate - (new Date).getTime() else 0

        setTimeoutToRefresh = ->
            if !defaultConfigs.refresh
                $log.debug 'Auto refresh token was turned off'
                return
            # OAuth iframe will be remove immediately after token was saved
            # So we no need set a schedule to renew token
            if isOAuthFrame()
                $log.debug 'Skip auto refresh token in OAuth iFrame'
                return
            # Continuous check token expire time
            renewTokenAfter(0)

        getRoleCode = ->
            userInfo = getUserInfo()
            if userInfo?
                return userInfo['roleCode']
            return null

        endsWith = (str, suffix) ->
            str.indexOf(suffix, str.length - (suffix.length)) != -1

        logout = ->
            accessToken = getAccessToken()

            doIt = ->
                clearToken()
                clearUserInfo()
                # Redirect to logout url
                if defaultConfigs.logoutUrl and defaultConfigs.logoutUrl.length > 0
                    lang = getUserLanguage()
                    # Passing current user's language to logout url, so the auth server can decide to switch the
                    # language of login page to current user's language
                    if lang?
                        $window.location.href = addUrlParam(defaultConfigs.logoutUrl, 'lang', lang)
                    else
                        $window.location.href = defaultConfigs.logoutUrl

            if accessToken and defaultConfigs.revokeTokenEndpoint and defaultConfigs.revokeTokenEndpoint.length > 0
                revokeToken(accessToken).then(
                    () ->
                        $log.info 'Token revoked'
                    (e) ->
                        $log.error('Token revoke error', e)
                ).finally(
                    () ->
                        doIt()
                )
            else
                doIt()


        revokeToken = (accessToken) ->
            url = defaultConfigs.revokeTokenEndpoint
            if !endsWith(url, '/')
                url += '/'
            url += '/' + accessToken
            configs =
                method: 'DELETE'
                url: url
                headers:
                    'Authorization': 'Bearer ' + accessToken

            $http(configs)

        ###*
        # Add a URL parameter (or changing it if it already exists)
        # @param {string} search  this is typically document.location.search
        # @param {string} key     the key to set
        # @param {string} val     value
        ###
        addUrlParam = (search, key, val) ->
            newParam = key + '=' + val
            params = '?' + newParam
            # If the "search" string exists, then build params from it
            if search?
                # Try to replace an existance instance
                params = search.replace(new RegExp('[?&]' + key + '[^&]*'), '$1' + newParam)
                # If nothing was replaced, then add the new param to the end
                if params is search
                    if params.indexOf('?') == -1
                        params += '?'
                    params += '&' + newParam
            return params

        clearToken = ->
            $window.localStorage.removeItem 'access_token'

        clearUserInfo = ->
            $window.localStorage.removeItem 'userInfo'

        getUserInfo = ->
            userInfo = $window.localStorage['userInfo']
            if !userInfo
                return null
            try
                return angular.fromJson(userInfo)
            catch e
                $window.localStorage['userInfo'] = null
            return null

        setUserInfo = (userInfo) ->
            $window.localStorage['userInfo'] = angular.toJson userInfo

        getUserLanguage = ->
            $window.localStorage['lang']

        setUserLanguage = (lang) ->
            $window.localStorage['lang'] = lang


        verifyTokenPromise = null
        verifyToken = ->
            if not verifyTokenPromise?
                if (not defaultConfigs.verifyTokenEndpoint?) or defaultConfigs.verifyTokenEndpoint.length == 0
                    throw new Error('Please config \'verifyTokenEndpoint\' before use this function')

                defer = $q.defer()
                if getAccessToken() is null then login()

                $http.get(defaultConfigs.verifyTokenEndpoint)
                .success((userinfo) ->
                    setUserInfo userinfo
                    $log.debug('Token was verified')
                    # Broadcasting events
                    $rootScope.$broadcast 'TokenVerified'
                    defer.resolve userinfo
                    verifyTokenPromise = null
                ).error (error) ->
                    $log.debug 'Cannot verify token: ' + error
                    defer.reject error
                    verifyTokenPromise = null
                verifyTokenPromise = defer.promise

            verifyTokenPromise

        setTokenFromLocation = ->
            search = $location.search()
            setAccessToken(search)

            #remove access_token from url
            $location.search 'access_token', null
            $location.search 'token_type', null
            $location.search 'expires_in', null
            $location.search 'flag', null
            $location.search 'lang', null
            $location.search 'auto_refresh', null
            $location.search 'state', null

            # Self close if is an iframe
            if isOAuthFrame()
                $log.debug 'Token was renewed using iFrame'
                removeOAuthFrame()
            else
                $log.debug 'Token was saved'


        isOAuthFrame = ->
            $window.parent and $window.parent.document.getElementById('oauth_frame')

        removeOAuthFrame = ->
            iFrame = $window.parent.document.getElementById('oauth_frame')
            if iFrame
                iFrame.parentNode.removeChild iFrame

        getRedirectUrl = ->
            redirect_uri = $state.href('callback', {}, {absolute: true})
            redirect_uri + '?'

        getState = ->
            suffix = $location.url()
            prefix = createRandomState()
            encodeURIComponent(prefix + '_' + suffix)

        createRandomString = (length) ->
            text = ''
            possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
            i = 0
            while i < length
                text += possible.charAt(Math.floor(Math.random() * possible.length))
                i++
            return text

        createRandomState = ->
            # Create random state
            state = createRandomString(defaultConfigs.state_length)
            # Save to storage for further process
            $window.localStorage['state'] = state
            return state

        buildAuthorizationUrl = (extendParams) ->
            params =
                response_type: 'token'
                client_id: defaultConfigs.client_id
                redirect_uri: getRedirectUrl()
                scope: defaultConfigs.scope
                state: getState()

            params = angular.extend(params, extendParams)
            url = defaultConfigs.authorizationEndpoint

            angular.forEach params, (value, key) ->
                url = addUrlParam(url, encodeURIComponent(key), encodeURIComponent(value))

            return url

        login = ->
            search = $location.search()
            if search.access_token?
                setTokenFromLocation()
                return
            $window.location.href = buildAuthorizationUrl()

        renewTokenAfter = (timeout) ->
            if timeout is 0
                renewToken()
            else
                if renewTokenTask?
                    if timeout isnt currentRenewTokenTaskTimeout
                        $interval.cancel(renewTokenTask)
                        currentRenewTokenTaskTimeout = timeout
                        renewTokenTask = $interval (() ->
                            renewToken()
                        ), timeout
                else
                    currentRenewTokenTaskTimeout = timeout
                    renewTokenTask = $interval (() ->
                        renewToken()
                    ), timeout

        renewToken = ->
            tokenExpiredIn = getTokenExpiredIn()
            # Check if token already renewed or not expire yet.
            # If token not expire yet, schedule to check after check_token_interval.
            if tokenExpiredIn > defaultConfigs.min_live_time
                renewTokenAfter(defaultConfigs.check_token_interval)
                return

            # Check lock, if locked, check last update time
            if $window.localStorage['refresh_locked']
                currentTime = (new Date).getTime()
                lastUpdate = if $window.localStorage['refresh_last_update']? then Date.parse($window.localStorage['refresh_last_update']) else null
                if lastUpdate > 0
                    diff = currentTime - lastUpdate
                    # Another tab is holding lock and still working, schedule to check again
                    if diff > 0 and diff <= defaultConfigs.refresh_max_wait
                        renewTokenAfter(defaultConfigs.refresh_checklock_interval)
                        return

            # Not locked by anyone (only one tab)
            # Or last update time is too old (the tab holding the lock may be closed)
            # So we acquire new lock for current tab
            # Acquired new lock and remove oauth iframe if exist
            $window.localStorage['refresh_locked'] = true
            $window.localStorage['refresh_last_update'] = new Date()
            removeOAuthFrame()
            if updateProgressTask?
                $interval.cancel updateProgressTask
                updateProgressTask = undefined

            # Update the last update continuously to keep the lock
            updateProgressTask = $interval((->
                $window.localStorage['refresh_last_update'] = new Date()
            ), 1000)

            iFrame = document.createElement('iframe')
            iFrame.setAttribute 'src', buildAuthorizationUrl('auto_refresh': 'true')
            iFrame.setAttribute 'id', 'oauth_frame'
            iFrame.style.display = 'none'
            document.body.appendChild iFrame

            $log.debug 'Refreshing access token using iFrame'

            # Continuous check if dead lock or timeout
            renewTokenAfter(defaultConfigs.check_token_interval)

        isNoAuthPage = (toState, toParams) ->
            defaultConfigs.is_no_auth_page(toState, toParams)

        changePassword = (oldPassword, newPassword) ->
            if (not defaultConfigs.changePasswordEndpoint?) or defaultConfigs.changePasswordEndpoint.length == 0
                throw new Error('Please config \'changePasswordEndpoint\' before use this function')

            defer = $q.defer()
            params = {
                newPassword: newPassword
                oldPassword: oldPassword
            }

            $http.put(defaultConfigs.changePasswordEndpoint, params)
            .success((data) ->
                # Broadcasting events
                $rootScope.$broadcast 'PasswordChanged'
                defer.resolve data
            ).error (error) ->
                defer.reject error
            defer.promise

        for key of defaultConfigs
            if defaultConfigs[key] is REQUIRED_AND_MISSING then requiredAndMissing.push key

        if requiredAndMissing.length > 0
            throw new Error('TokenProvider is insufficiently configured. Please configure the following options using $tokenProvider.extendConfig: ' + requiredAndMissing.join(', '))

        if not isOAuthFrame()
            wrap = (event) ->
                if event.key == 'access_token' and event.newValue != event.oldValue
                    oldToken = event.oldValue
                    newToken = event.newValue
                    # Token was renewed, cancel the lock
                    if updateProgressTask
                        $interval.cancel updateProgressTask
                        updateProgressTask = undefined
                        $window.localStorage['refresh_locked'] = undefined
                        $window.localStorage['refresh_last_update'] = undefined
                    # Broadcasting events
                    $rootScope.$broadcast 'TokenChanged', oldToken, newToken

            # Listening for token change
            if $window.addEventListener
                $window.addEventListener 'storage', wrap, false
            else
                $window.attachEvent 'onstorage', wrap
            # If access token already stored in localStorage, setup an schedule to refresh
            if getAccessToken()
                setTimeoutToRefresh()

        return {
            login: login
            logout: logout
            isNoAuthPage: isNoAuthPage
            getAccessToken: getAccessToken
            extendConfig: @extendConfig
            clearToken: clearToken
            verifyToken: verifyToken
            setTokenFromLocation: setTokenFromLocation
            clearUserInfo: clearUserInfo
            getUserInfo: getUserInfo
            setUserInfo: setUserInfo
            getUserLanguage: getUserLanguage
            setUserLanguage: setUserLanguage
            isOAuthFrame: isOAuthFrame
            getRoleCode: getRoleCode
            changePassword: changePassword
        }
    ]

.config [
    '$httpProvider'
    ($httpProvider) ->
        $httpProvider.interceptors.push ['$injector', ($injector) ->
            request: (config) ->
                config.headers = config.headers or {}
                $injector.invoke [
                    '$token'
                    ($token) ->
                        if $token.getAccessToken()?
                            config.headers.Authorization = 'Bearer ' + $token.getAccessToken()
                ]
                return config
            responseError: (resp) ->
                if resp.status == 401
                    $injector.invoke [
                        '$token'
                        ($token) ->
                            $token.clearToken()
                            $token.clearUserInfo()
                            $token.login()
                    ]

                if resp.status is -1
                    $injector.invoke [
                        '$state'
                        ($state) ->
                            $state.go('500')
                    ]

                return $injector.get('$q').reject(resp)
        ]
]

.run [
    '$rootScope', '$log', '$location', '$state', '$token',
    ($rootScope, $log, $location, $state, $token) ->

        $rootScope.$on '$locationChangeStart', (event) ->
            search = $location.search()
            if search? and search.access_token?
                $token.setTokenFromLocation()
                event.preventDefault()

        $rootScope.$on '$stateChangeStart', (event, toState, toParams, fromState, fromParams) ->

            console.log('$stateChangeStart in token.js')

            # OAuth iframe will be remove immediately after token was saved
            # So we navigate to dummy state and check nothing
            if $token.isOAuthFrame()
                event.preventDefault()
                return

            if not $token.isNoAuthPage(toState)
                access_token = $token.getAccessToken()
                tokenVerified = $rootScope.tokenVerified
                if access_token?
                    if $token.getUserInfo()? and tokenVerified
                        # It okay - do nothing
                        $rootScope.$broadcast('$stateChangeStart:tokenVerified', toState, fromState, event)
                    else
                        promise = $token.verifyToken()
                        promise.then(
                            ->
                                $rootScope.tokenVerified = true
                                $rootScope.$broadcast('$stateChangeStart:tokenVerified', toState, fromState, event)
                            (error) ->
                                event.preventDefault()
                                # If error is undefined, this usual because server is offline
                                if error? and error.status == 401
                                # Do nothing - this case will be handled in interceptor
                                else
                                    $rootScope.$broadcast('$token:failToVerifyToken')
                        )

                else
                    event.preventDefault()
                    # If already in login process, prevent to call login() again (so OAuth state not generate again,
                    # sometime cause bug on safari)
                    if $rootScope.preventOtherLogin? and $rootScope.preventOtherLogin
                        return
                    $token.login()
                    $rootScope.preventOtherLogin = true;
]


