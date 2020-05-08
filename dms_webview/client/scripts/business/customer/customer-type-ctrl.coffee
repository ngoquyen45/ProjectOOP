'use strict'

angular.module('app')

.controller('CustomerTypeCtrl', [
        '$scope', 'CtrlCategoryInitiatorService'
        ($scope, CtrlCategoryInitiatorService) ->
            $scope.title = 'customer.type.title'
            $scope.categoryName = 'customer-type'
            $scope.usePopup = true
            $scope.isUseDistributorFilter = -> false

            CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope)

            $scope.init()
])
