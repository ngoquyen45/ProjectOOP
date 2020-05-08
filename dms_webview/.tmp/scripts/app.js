(function() {
  'use strict';
  angular.module('app', ['ui.router', 'ngAnimate', 'ngResource', 'ui.bootstrap', 'easypiechart', 'mgo-angular-wizard', 'textAngular', 'ui.tree', 'ngMap', 'ngTagsInput', 'pascalprecht.translate', 'fcsa-number', 'ui.select', 'angular-confirm', 'ngIdle', 'app.state', 'app.token', 'app.toast', 'app.filter', 'app.push', 'app.directives', 'app.directives.file.image']).config([
    '$tokenProvider', '$translateProvider', '$provide', 'ADDRESS_BACKEND', 'ADDRESS_OAUTH', function($tokenProvider, $translateProvider, $provide, ADDRESS_BACKEND, ADDRESS_OAUTH, DEFAULT_LANGUAGE) {
      var defaultParams;
      defaultParams = {
        'client_id': 'dmsplus',
        'authorizationEndpoint': ADDRESS_OAUTH + "oauth/authorize",
        'scope': "read",
        'revokeTokenEndpoint': ADDRESS_OAUTH + "oauth/revoke",
        'verifyTokenEndpoint': ADDRESS_OAUTH + 'oauth/userinfo',
        'changePasswordEndpoint': ADDRESS_OAUTH + 'oauth/password',
        'logoutUrl': ADDRESS_OAUTH + 'account/logout',
        'refresh': true,
        'is_no_auth_page': function(toState) {
          return toState === 500;
        }
      };
      $tokenProvider.extendConfig(defaultParams);
      $translateProvider.preferredLanguage(DEFAULT_LANGUAGE);
      $translateProvider.useStaticFilesLoader({
        prefix: 'i18n/',
        suffix: '.json'
      });
      return $provide.decorator('fileImageChooserConfig', [
        '$token', 'ADDRESS_BACKEND', function($token, ADDRESS_BACKEND) {
          return {
            getBaseUrl: function() {
              return ADDRESS_BACKEND;
            },
            getAccessToken: function() {
              return $token.getAccessToken();
            }
          };
        }
      ]);
    }
  ]).config([
    'KeepaliveProvider', 'IdleProvider', 'NG_IDLE_MAX_IDLE_TIME', 'NG_IDLE_TIMEOUT', 'NG_IDLE_PING_INTEVAL', function(KeepaliveProvider, IdleProvider, NG_IDLE_MAX_IDLE_TIME, NG_IDLE_TIMEOUT, NG_IDLE_PING_INTEVAL) {
      IdleProvider.idle(NG_IDLE_MAX_IDLE_TIME);
      IdleProvider.timeout(NG_IDLE_TIMEOUT);
      return KeepaliveProvider.interval(NG_IDLE_PING_INTEVAL);
    }
  ]).run([
    '$rootScope', '$token', '$translate', 'DEFAULT_LANGUAGE', 'Idle', function($rootScope, $token, $translate, DEFAULT_LANGUAGE, Idle) {
      var lang;
      $rootScope.tokenVerified = false;
      lang = $token.getUserLanguage();
      if (lang != null) {
        $translate.use(lang);
      } else {
        $translate.use(DEFAULT_LANGUAGE);
      }
      $rootScope.$on('TokenVerified', function() {
        return $translate.use($token.getUserLanguage());
      });
      return Idle.watch();
    }
  ]);

}).call(this);

//# sourceMappingURL=app.js.map
