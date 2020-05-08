'use strict'

angular.module('app')

.controller('ProductCategoryCtrl', [
        '$scope', 'CtrlCategoryInitiatorService'
        ($scope, CtrlCategoryInitiatorService) ->
            $scope.title = 'product.category.title'
            $scope.categoryName = 'product-category'
            $scope.usePopup = true
            $scope.isUseDistributorFilter = -> false

            CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope)

            $scope.init()
])
