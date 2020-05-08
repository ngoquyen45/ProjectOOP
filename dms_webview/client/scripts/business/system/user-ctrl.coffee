'use strict'

angular.module('app')

.controller('UserCtrl', [
        '$scope', 'CtrlCategoryInitiatorService', '$filter'
        ($scope, CtrlCategoryInitiatorService, $filter) ->
            $scope.title = 'user.title'
            $scope.categoryName = 'user'
            $scope.usePopup = false

            $scope.roles = [
                { id: 'all', name: '-- ' + $filter('translate')('all') + ' --' }
                { id: 'AD', name: $filter('translate')('role.ad') }
                { id: 'OBS', name: $filter('translate')('role.obs') }
                { id: 'SUP', name: $filter('translate')('role.sup') }
                { id: 'DIS', name: $filter('translate')('role.dis') }
                { id: 'SM', name: $filter('translate')('role.sm') }
            ]

            # FILTER
            $scope.isUseDistributorFilter = ->
                $scope.filter.role? and ( $scope.filter.role == 'DIS' or $scope.filter.role == 'SM' )

            $scope.roleFilterChange = ->
                $scope.filter.distributorId = null
                $scope.distributorFilterChange()

            CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope)

            # OVERRIDE
            $scope.getReloadDataParams = (params) ->
                params = params ? {}
                params.category = $scope.categoryName

                if $scope.filter.role? and $scope.filter.role != 'all'
                    params.role = $scope.filter.role

                if $scope.isUseDistributorFilter() and $scope.filter.distributorId? and $scope.filter.distributorId != 'all'
                    params.distributorId = $scope.filter.distributorId

                if not _.isEmpty($scope.filter.searchText)
                    params.q = $scope.filter.searchText

                return params

            $scope.addInitFunction( ->
                $scope.filter.role = $scope.filter.role ? 'all'

                if $scope.isUseDistributorFilter() || $scope.filter.role == 'all'
                    $scope.columns = [
                        { header: 'fullname',    property: 'fullname' }
                        { header: 'username',    property: 'usernameFull' }
                        { header: 'role',        property: (record) -> $filter('translate')('role.' + record.role.toLowerCase()) }
                        { header: 'distributor', property: (record) -> if record.distributor? then record.distributor.name }
                    ]
                else
                    $scope.columns = [
                        { header: 'fullname',    property: 'fullname' }
                        { header: 'username',    property: 'usernameFull' }
                        { header: 'role',        property: (record) -> $filter('translate')('role.' + record.role.toLowerCase()) }
                    ]
            )

            $scope.init()
    ])

.controller('UserDetailCtrl', [
        '$scope', '$state', '$stateParams', '$filter', 'CtrlUtilsService', 'LoadingUtilsService', 'CtrlInitiatorService',
        'CtrlCategoryInitiatorService'
        ($scope, $state, $stateParams, $filter, CtrlUtilsService, LoadingUtilsService, CtrlInitiatorService,
         CtrlCategoryInitiatorService) ->
            $scope.title = 'user.title'
            $scope.categoryName  = 'user'
            $scope.isIdRequire = -> true
            $scope.defaultBackState = 'user'

            # AFTER LOAD
            $scope.onLoadSuccess = ->
                if $scope.record.distributor?
                    $scope.record.distributorId = $scope.record.distributor.id

                if $scope.record.salesmen? and $scope.record.salesman.length > 0
                    $scope.record.salesmanIds = (salesman.id for salesman in $scope.record.salesmen)

                if $scope.record.role?
                    $scope.roleName = $filter('translate')('role.' + $scope.record.role.toLowerCase())

            $scope.clientCode = CtrlUtilsService.getUserInfo().clientCode.toLowerCase() + '_'

            $scope.roles = [
                { id: 'AD', name: $filter('translate')('role.ad') }
                { id: 'OBS', name: $filter('translate')('role.obs') }
                { id: 'SUP', name: $filter('translate')('role.sup') }
                { id: 'DIS', name: $filter('translate')('role.dis') }
                { id: 'SM', name: $filter('translate')('role.sm') }
            ]

            $scope.useDistributor = ->
                if $scope.record? and $scope.record.role?
                    $scope.record.role is 'DIS' or $scope.record.role is 'SM'

            $scope.resetPassword = ->
                status = $scope.loadStatus.getStatusByDataName('reset.password')

                params =
                    who: $scope.who
                    category: $scope.categoryName
                    id: $scope.record.id
                    action: 'reset-password'

                CtrlUtilsService.doPut(params, status, null, null, null, 'reset.password')

            $scope.goToObserverDistributors = ->
                $state.go('user-observer-distributors', { id: $scope.record.id, filter: $stateParams.filter })

            CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope)

            $scope.addInitFunction( ->
                LoadingUtilsService.loadDistributors(
                    $scope.who
                    $scope.loadStatus.getStatusByDataName('distributor')
                    (list) -> $scope.distributors = list
                )
            )

            $scope.init()
    ])

.controller('UserObserverDistributorsCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams', '$confirm'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
         $filter, toast, $state, $stateParams, $confirm) ->
            $scope.title = 'user.observer.distributors.title'

            # LOADING
            $scope.reloadData = ->
                $scope.records = []

                params =
                    who: $scope.who
                    category: 'user'
                    id: $stateParams.id
                    param: 'distributors'

                CtrlUtilsService.loadListData(params
                    $scope.loadStatus.getStatusByDataName('data')
                    (records) -> $scope.records = records
                    -> toast.logError($filter('translate')('loading.error'))
                )

            $scope.isEmpty = -> _.isEmpty($scope.records)

            # SAVE
            $scope.checkBeforeSave = ->
                return true

            $scope.getObjectToSave = ->
                saveData = { list: [] }

                for record in $scope.records
                    saveData.list.push(record.id) if record.selected

                return saveData

            $scope.getSaveParams = (params) ->
                params.category = 'user'
                params.id = $stateParams.id
                params.param = 'distributors'
                return params

            $scope.moreThan20 = -> $scope.records? && $scope.records.length > 20

            $scope.onSaveSuccess = -> # DO NOTHING
            $scope.onSaveFailure = -> # DO NOTHING
            $scope.isPost = -> false

            # INIT
            # CtrlInitiatorService.initCanBackViewCtrl($scope)
            $scope.back = ->
                if $scope.isChanged()
                    $confirm( { text: $filter('translate')('confirm.dialog.are.you.sure.back') } ).then( ->
                        $state.go( 'user-detail', { id: $stateParams.id, filter: $stateParams.filter } )
                    )
                else
                    $state.go( 'user-detail', { id: $stateParams.id, filter: $stateParams.filter } )

            CtrlInitiatorService.initCanSaveViewCtrl($scope)

            $scope.addInitFunction(->
                $scope.reloadData()
            )

            $scope.init()

    ])

