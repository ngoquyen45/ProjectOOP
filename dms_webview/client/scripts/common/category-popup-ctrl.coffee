'use strict'

angular.module('app')

.controller('NameCategoryPopupCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', '$modalInstance', 'title', 'categoryName', 'id', 'useDistributorFilter'
    ($scope, CtrlCategoryInitiatorService, $modalInstance, title, categoryName, id, useDistributorFilter) ->

        $scope.title = title
        $scope.categoryName = categoryName
        $scope.id = id
        $scope.isIdRequire = -> true
        $scope.isUseDistributorFilter = -> useDistributorFilter ? false

        CtrlCategoryInitiatorService.initCategoryDetailPopupCtrl($scope, $modalInstance)

        $scope.init()
])

.controller('NameCodeCategoryPopupCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', '$modalInstance', 'title', 'categoryName', 'id', 'useDistributorFilter'
    ($scope, CtrlCategoryInitiatorService, $modalInstance, title, categoryName, id, useDistributorFilter) ->

        $scope.title = title
        $scope.categoryName = categoryName
        $scope.id = id
        $scope.isIdRequire = -> true
        $scope.isUseDistributorFilter = -> useDistributorFilter ? false

        $scope.useCode = true

        CtrlCategoryInitiatorService.initCategoryDetailPopupCtrl($scope, $modalInstance)

        $scope.init()
])

