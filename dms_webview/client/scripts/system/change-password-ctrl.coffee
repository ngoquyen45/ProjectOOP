'use strict'

angular.module('app')

.controller('ChangePasswordCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'toast', '$filter', '$token',
        ($scope, CtrlUtilsService, CtrlInitiatorService, toast, $filter, $token) ->
            $scope.title = ''

            CtrlInitiatorService.initBasicViewCtrl($scope)

            $scope.save = ->
                if not $scope.isFormValid('form')
                    toast.logError($filter('translate')('error.data.input.not.valid'))
                else if ($scope.record.newPassword isnt $scope.record.newPassword2)
                    toast.logError($filter('translate')('error.new.password.not.match'))
                else
                    status = $scope.loadStatus.getStatusByDataName('data')
                    status.processing = true

                    $token.changePassword($scope.record.oldPassword, $scope.record.newPassword).then(
                        ->
                            status.processing = false
                            toast.logSuccess($filter('translate')('save.success'))
                            $scope.refresh()

                        (error) ->
                            status.processing = false
                            if (error.status is 400)
                                toast.logError($filter('translate')(error.data.meta.error_message))
                            else
                                toast.logError($filter('translate')('save.error'))
                    )

            $scope.addInitFunction( ->
                $scope.record = {}
            )

            $scope.init()
    ])
