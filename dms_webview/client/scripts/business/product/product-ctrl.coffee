'use strict'

angular.module('app')

.controller('ProductCtrl', [
        '$scope', 'CtrlCategoryInitiatorService'
        ($scope, CtrlCategoryInitiatorService) ->
            $scope.title = 'product.title'
            $scope.categoryName = 'product'
            $scope.usePopup = false
            $scope.isUseDistributorFilter = -> false

            $scope.importState = 'import-product'

            $scope.columns = [
                { header: 'name', property: 'name' }
                { header: 'code', property: 'code' }
            ]

            CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope)

            $scope.init()
])

.controller('ProductDetailCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService) ->
            $scope.title = 'product.title'
            $scope.categoryName  = 'product'
            $scope.isIdRequire = -> true
            $scope.defaultBackState = 'product'

            # AFTER LOAD
            $scope.onLoadSuccess = ->
                if $scope.isNew() or $scope.isDraft()
                    LoadingUtilsService.loadProductCategories(
                        $scope.who
                        $scope.loadStatus.getStatusByDataName('productCategories')
                        (list) -> $scope.productCategories = list
                    )

                    LoadingUtilsService.loadUOMs(
                        $scope.who
                        $scope.loadStatus.getStatusByDataName('uom')
                        (list) -> $scope.uoms = list
                    )

                if $scope.isNew()
                    $scope.record.productivity = 1

                if $scope.record?
                    if $scope.record.productCategory?
                        $scope.record.productCategoryId = $scope.record.productCategory.id

                    if $scope.record.uom?
                        $scope.record.uomId = $scope.record.uom.id

            CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope)

            $scope.init()
])
