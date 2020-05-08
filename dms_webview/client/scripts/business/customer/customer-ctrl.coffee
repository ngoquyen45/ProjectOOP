'use strict'

angular.module('app')

.controller('CustomerCtrl', [
        '$scope', 'CtrlCategoryInitiatorService'
        ($scope, CtrlCategoryInitiatorService) ->
            $scope.title = 'customer.title'
            $scope.categoryName = 'customer'
            $scope.usePopup = false
            $scope.isUseDistributorFilter = -> $scope.who isnt 'distributor'

            $scope.importState = 'import-customer'

            if $scope.isUseDistributorFilter()
                $scope.columns = [
                    { header: 'name', property: 'name' }
                    { header: 'code', property: 'code' }
                    { header: 'distributor', property: (record) -> record.distributor.name }
                    { header: 'area', property: (record) -> record.area.name }
                    { header: 'customer.type', property: (record) -> record.customerType.name }
                ]
            else
                $scope.columns = [
                    { header: 'name', property: 'name' }
                    { header: 'code', property: 'code' }
                    { header: 'area', property: (record) -> record.area.name }
                    { header: 'customer.type', property: (record) -> record.customerType.name }
                ]

            CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope)

            $scope.init()
    ])

.controller('CustomerDetailCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService) ->
            $scope.title = 'customer.title'
            $scope.categoryName  = 'customer'
            $scope.isIdRequire = -> true
            $scope.defaultBackState = 'customer'

            $scope.useMap = true
            $scope.getLocation = -> $scope.record.location

            $scope.onLoadSuccess = (record)  ->
                if $scope.isNew() or $scope.isDraft()
                    LoadingUtilsService.loadDistributors(
                        $scope.who
                        $scope.loadStatus.getStatusByDataName('distributors')
                        (list) -> $scope.distributors = list
                    )

                    LoadingUtilsService.loadCustomerTypes(
                        $scope.who
                        $scope.loadStatus.getStatusByDataName('customerTypes')
                        (list) -> $scope.customerTypes = list
                    )

                if record?
                    if record.distributor? and record.distributor.id?
                        $scope.record.distributorId = $scope.record.distributor.id
                        reloadArea()

                    if record.customerType? and record.customerType.id?
                        $scope.record.customerTypeId = $scope.record.customerType.id

                    if record.area? and record.area.id?
                        $scope.record.areaId = $scope.record.area.id

            reloadArea = ->
                if $scope.record? and $scope.record.distributorId?
                    LoadingUtilsService.loadAreasByDistributor(
                        $scope.who
                        $scope.record.distributorId
                        $scope.loadStatus.getStatusByDataName('areas')
                        (list) -> $scope.areas = list
                    )
                else
                    $scope.areas = []

            $scope.changeDistributor = ->
                $scope.record.areaId = null
                $scope.changeStatus.markAsChanged()
                reloadArea()

            CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope)
            CtrlInitiatorService.initUseDatePickerCtrl($scope)

            $scope.init()
    ])
