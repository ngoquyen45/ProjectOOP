(function() {
  'use strict';
  angular.module('app').controller('LangCtrl', [
    '$scope', '$translate', '$token', function($scope, $translate, $token) {
      $scope.lang = $translate.use();
      $scope.$watch(function() {
        return $translate.use();
      }, function(newLang) {
        return $scope.lang = newLang;
      });
      $scope.setLang = function(lang) {
        $translate.use(lang);
        $scope.lang = lang;
        return $token.setUserLanguage(lang);
      };
      $scope.getFlag = function() {
        var lang;
        lang = $scope.lang;
        switch (lang) {
          case 'vi':
            return 'flags-vietnam';
          case 'zh':
            return 'flags-china';
          case 'km':
            return 'flags-cambodia';
          default:
            return 'flags-british';
        }
      };
      $scope.isSupportedLanguage = function(lang) {
        var userInfo;
        if ($token != null) {
          userInfo = $token.getUserInfo();
          if ((userInfo != null) && (userInfo.languages != null)) {
            return _.includes(userInfo.languages, lang);
          }
        }
        return false;
      };
      return $scope.isShowLangInDropDown = function(lang) {
        if ($scope.isSupportedLanguage(lang)) {
          if ($scope.lang !== lang) {
            return true;
          }
        }
        return false;
      };
    }
  ]);

}).call(this);

//# sourceMappingURL=lang-ctrl.js.map
