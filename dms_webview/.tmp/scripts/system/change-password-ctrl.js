(function() {
  'use strict';
  angular.module('app').controller('ChangePasswordCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'toast', '$filter', '$token', function($scope, CtrlUtilsService, CtrlInitiatorService, toast, $filter, $token) {
      $scope.title = '';
      CtrlInitiatorService.initBasicViewCtrl($scope);
      $scope.save = function() {
        var status;
        if (!$scope.isFormValid('form')) {
          return toast.logError($filter('translate')('error.data.input.not.valid'));
        } else if ($scope.record.newPassword !== $scope.record.newPassword2) {
          return toast.logError($filter('translate')('error.new.password.not.match'));
        } else {
          status = $scope.loadStatus.getStatusByDataName('data');
          status.processing = true;
          return $token.changePassword($scope.record.oldPassword, $scope.record.newPassword).then(function() {
            status.processing = false;
            toast.logSuccess($filter('translate')('save.success'));
            return $scope.refresh();
          }, function(error) {
            status.processing = false;
            if (error.status === 400) {
              return toast.logError($filter('translate')(error.data.meta.error_message));
            } else {
              return toast.logError($filter('translate')('save.error'));
            }
          });
        }
      };
      $scope.addInitFunction(function() {
        return $scope.record = {};
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=change-password-ctrl.js.map
