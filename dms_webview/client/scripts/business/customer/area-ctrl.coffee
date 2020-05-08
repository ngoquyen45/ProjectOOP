'use strict'

angular.module('app')

.controller('AreaCtrl', [
        '$scope', 'CtrlCategoryInitiatorService'
        ($scope, CtrlCategoryInitiatorService) ->
            $scope.title = 'area.title'
            $scope.categoryName = 'area'
            $scope.usePopup = true
            $scope.isUseDistributorFilter = -> true

            $scope.columns = [
                { header: 'name', property: 'name' }
                { header: 'distributor', property: (record) -> record.distributor.name }
            ]

            CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope)

            $scope.init()
])
