'use strict';

angular.module('app', [
    # Angular modules
    'ui.router'
    'ngAnimate'
    'ngResource'

    # 3rd Party Modules
    'ui.bootstrap'
    'easypiechart'
    'mgo-angular-wizard'
    'textAngular'
    'ui.tree'
    'ngMap'
    'ngTagsInput'
    'pascalprecht.translate'
    'fcsa-number'
    'ui.select'
    'angular-confirm'
    'ngIdle'

    # System
    'app.state'
    'app.token'
    'app.toast'
    'app.filter'
    'app.push'

    # Directive
    'app.directives'
    'app.directives.file.image'
])

.config [
    '$tokenProvider', '$translateProvider', '$provide', 'ADDRESS_BACKEND', 'ADDRESS_OAUTH'
    ($tokenProvider, $translateProvider, $provide, ADDRESS_BACKEND, ADDRESS_OAUTH, DEFAULT_LANGUAGE) ->
        # Configure $token service for login management
        defaultParams = {
            'client_id': 'dmsplus'
            'authorizationEndpoint': ADDRESS_OAUTH + "oauth/authorize"
            'scope': "read"
            'revokeTokenEndpoint': ADDRESS_OAUTH + "oauth/revoke"
            'verifyTokenEndpoint': ADDRESS_OAUTH + 'oauth/userinfo'
            'changePasswordEndpoint': ADDRESS_OAUTH + 'oauth/password'
            'logoutUrl': ADDRESS_OAUTH + 'account/logout'
            'refresh': true
            'is_no_auth_page': (toState) ->
                toState is 500
        };
        $tokenProvider.extendConfig defaultParams

        # Configure multi-language support
        $translateProvider.preferredLanguage DEFAULT_LANGUAGE
        $translateProvider.useStaticFilesLoader { prefix: 'i18n/', suffix: '.json' }

        # Configure file image chooser
        $provide.decorator('fileImageChooserConfig', [
            '$token', 'ADDRESS_BACKEND'
            ($token, ADDRESS_BACKEND) ->
                getBaseUrl: -> ADDRESS_BACKEND
                getAccessToken: -> $token.getAccessToken()
        ])
]

# Configure Idle detector and Auto-ping service to keep session alive
.config [
    'KeepaliveProvider', 'IdleProvider', 'NG_IDLE_MAX_IDLE_TIME', 'NG_IDLE_TIMEOUT', 'NG_IDLE_PING_INTEVAL'
    (KeepaliveProvider, IdleProvider, NG_IDLE_MAX_IDLE_TIME, NG_IDLE_TIMEOUT, NG_IDLE_PING_INTEVAL) ->
        # Time out
        IdleProvider.idle(NG_IDLE_MAX_IDLE_TIME);

        # Count down time before logout
        IdleProvider.timeout(NG_IDLE_TIMEOUT);

        # Auto-ping interval
        KeepaliveProvider.interval(NG_IDLE_PING_INTEVAL);
]

.run ['$rootScope', '$token', '$translate', 'DEFAULT_LANGUAGE', 'Idle'
    ($rootScope, $token, $translate, DEFAULT_LANGUAGE, Idle) ->
        $rootScope.tokenVerified = false

        # Load saved language
        lang = $token.getUserLanguage()
        if lang? then $translate.use(lang) else $translate.use(DEFAULT_LANGUAGE)

        $rootScope.$on('TokenVerified', ->
            $translate.use($token.getUserLanguage())
        )

        # Watch Idle events
        Idle.watch()
]
