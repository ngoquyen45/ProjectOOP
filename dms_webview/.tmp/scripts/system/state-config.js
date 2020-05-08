(function() {
  'use strict';
  angular.module('app.state', []).constant('CATEGORY_LIST_TEMPLATE', 'views/common/category-list.html').constant('CATEGORY_POPUP_TEMPLATE', 'views/common/category-popup.html').constant('CATEGORY_POPUP_CTRL', 'NameCategoryPopupCtrl').controller('HomeCtrl', [
    '$scope', '$state', '$token', 'HOME_STATE', function($scope, $state, $token, HOME_STATE) {
      var home, roleCode;
      if ($token != null) {
        roleCode = $token.getRoleCode();
        if (roleCode != null) {
          home = HOME_STATE[roleCode];
          if (home != null) {
            return $state.go(home);
          } else {
            return $state.go('550');
          }
        } else {
          return console.log('roleCode is null');
        }
      } else {
        return console.log('$token is null');
      }
    }
  ]).config([
    '$stateProvider', '$urlRouterProvider', 'STATES', 'CATEGORY_LIST_TEMPLATE', function($stateProvider, $urlRouterProvider, STATES, CATEGORY_LIST_TEMPLATE) {
      var addState, getTemplateUrl, state, _i, _len, _results;
      $urlRouterProvider.when('', '/home');
      $urlRouterProvider.otherwise(function($injector, $location) {
        var $state;
        $state = $injector.get('$state');
        if ($location.url() === '/') {
          return $state.go('home');
        } else {
          return $state.go('404');
        }
      });
      $stateProvider.state('home', {
        url: '/home',
        controller: 'HomeCtrl'
      });
      $stateProvider.state('callback', {
        url: '/callback'
      });
      $stateProvider.state('404', {
        url: '/404',
        templateUrl: 'views/pages/404.html'
      });
      $stateProvider.state('500', {
        url: '/500',
        templateUrl: 'views/pages/500.html'
      });
      $stateProvider.state('550', {
        url: '/550',
        templateUrl: 'views/pages/550.html'
      });
      getTemplateUrl = function(url) {
        switch (url) {
          case 'CATEGORY_LIST_TEMPLATE':
            return CATEGORY_LIST_TEMPLATE;
          default:
            return url;
        }
      };
      addState = function(state) {
        var url;
        if ((state != null) && (state.name != null)) {
          url = (state.url != null ? state.url : '/' + state.name);
          return $stateProvider.state(state.name, {
            url: url + '?filter&id&parent&parentparams',
            templateUrl: getTemplateUrl(state.templateUrl),
            controller: state.controller
          });
        }
      };
      if ((STATES != null) && STATES.length > 0) {
        _results = [];
        for (_i = 0, _len = STATES.length; _i < _len; _i++) {
          state = STATES[_i];
          _results.push(addState(state));
        }
        return _results;
      }
    }
  ]).run([
    '$rootScope', '$location', '$state', '$token', 'STATES', 'MENUS', 'HOME_STATE', function($rootScope, $location, $state, $token, STATES, MENUS, HOME_STATE) {
      $rootScope.$on('$stateChangeStart:tokenVerified', function(event, toState, fromState, parentEvent) {
        var roleCode, state, stateName, _i, _len;
        console.log('$stateChangeStart:tokenVerified in state-config.js');
        roleCode = $token != null ? $token.getRoleCode() : null;
        if ((toState != null) && (toState.name != null)) {
          if (toState.name === 'home') {
            return;
          }
          stateName = toState.name;
          if (stateName === '404' || stateName === '500' || stateName === '550') {
            return;
          }
          for (_i = 0, _len = STATES.length; _i < _len; _i++) {
            state = STATES[_i];
            if (state.name === stateName) {
              if (_.indexOf(state.roles, roleCode) > -1) {
                return;
              } else {
                event.preventDefault();
                parentEvent.preventDefault();
                $state.go('404');
              }
            }
          }
          event.preventDefault();
          parentEvent.preventDefault();
          return $state.go('404');
        }
      });
      return $rootScope.$on('$token:failToVerifyToken', function() {
        return $state.go('500');
      });
    }
  ]);

}).call(this);

//# sourceMappingURL=state-config.js.map
