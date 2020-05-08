(function() {
  'use strict';
  angular.module('app').controller('HeaderMenuCtrl', [
    '$scope', '$location', '$token', 'MENUS', '$state', 'Idle', 'Keepalive', '$modal', '$http', 'ADDRESS_PING', '$log', function($scope, $location, $token, MENUS, $state, Idle, Keepalive, $modal, $http, ADDRESS_PING, $log) {
      var checkActiveHref, closeIdleWarningModals;
      $scope.menus = MENUS;
      checkActiveHref = function(path, href) {
        if ((path != null) && (href != null)) {
          return ('#' + path) === href;
        }
        return false;
      };
      $scope.isMenuActive = function(menu) {
        var child, _i, _len, _ref;
        if (menu != null) {
          if (menu.children != null) {
            _ref = menu.children;
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              child = _ref[_i];
              if (checkActiveHref($location.path(), child.href)) {
                return true;
              }
            }
          } else {
            return checkActiveHref($location.path(), menu.href);
          }
        }
        return false;
      };
      $scope.checkMenuByRole = function(menu) {
        var roleCode;
        roleCode = $token != null ? $token.getRoleCode() : null;
        if ((menu != null) && (menu.roles != null) && _.indexOf(menu.roles, roleCode) > -1) {
          return true;
        } else {
          return false;
        }
      };
      $scope.isSpecificPage = function() {
        var path;
        path = $location.path();
        return _.contains(['/404', '/500', '/550'], path);
      };
      $scope.main = {
        brand: 'DMS PLUS',
        name: function() {
          if ($token.getUserInfo() != null) {
            return $token.getUserInfo().fullname;
          } else {
            return '';
          }
        }
      };
      $scope.logout = $token.logout;
      $scope.changePassword = function() {
        return $state.go('change-password');
      };
      $scope.$on('IdleStart', function() {
        closeIdleWarningModals();
        return $scope.warning = $modal.open({
          templateUrl: 'views/system/logout-warning-dialog.html',
          controller: 'TimeoutWarnCtrl',
          windowClass: 'modal-danger'
        });
      });
      $scope.$on('IdleEnd', function() {
        return closeIdleWarningModals();
      });
      $scope.$on('IdleTimeout', function() {
        closeIdleWarningModals();
        $scope.timedout = $modal.open({
          templateUrl: 'views/system/logged-out-dialog.html',
          windowClass: 'modal-danger'
        });
        return $token.logout();
      });
      $scope.$on('Keepalive', function() {
        var iFrame;
        iFrame = document.createElement('iframe');
        iFrame.onload = function() {
          return iFrame.parentNode.removeChild(iFrame);
        };
        iFrame.setAttribute('src', ADDRESS_PING);
        iFrame.setAttribute('id', 'keep_alive_frame');
        iFrame.style.display = 'none';
        return document.body.appendChild(iFrame);
      });
      closeIdleWarningModals = function() {
        if ($scope.warning) {
          $scope.warning.close();
          $scope.warning = null;
        }
        if ($scope.timedout) {
          $scope.timedout.close();
          $scope.timedout = null;
        }
        return $scope.$apply();
      };
      return $scope.getUserLanguage = function() {
        return $token.getUserLanguage();
      };
    }
  ]).controller('TimeoutWarnCtrl', [
    '$scope', 'NG_IDLE_TIMEOUT', function($scope, NG_IDLE_TIMEOUT) {
      $scope.max = NG_IDLE_TIMEOUT;
      $scope.countdown = NG_IDLE_TIMEOUT;
      $scope.$on('IdleWarn', function(e, countdown) {
        $scope.countdown = countdown;
        return $scope.$apply();
      });
      return $scope.$on('IdleTimeout', function() {
        $scope.countdown = 0;
        return $scope.$apply();
      });
    }
  ]);

}).call(this);

//# sourceMappingURL=header-menu-ctrl.js.map
