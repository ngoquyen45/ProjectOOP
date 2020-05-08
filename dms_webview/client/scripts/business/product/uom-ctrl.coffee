'use strict'

angular.module('app')

.controller('UomCtrl', [
        '$scope', 'CtrlCategoryInitiatorService'
        ($scope, CtrlCategoryInitiatorService) ->
            $scope.title = 'uom.title'
            $scope.categoryName = 'uom'
            $scope.usePopup = true
            $scope.isUseDistributorFilter = -> false

            $scope.popupCtrl = 'NameCodeCategoryPopupCtrl'

            $scope.columns = [
                { header: 'name', property: 'name' }
                { header: 'code', property: 'code' }
            ]

            CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope)

            $scope.init()
])
