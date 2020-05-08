'use strict'

class CtrlCategoryInitiatorService

    constructor: (@$log
                  @$filter, @toast, @$modal
                  @$state, @$stateParams
                  @CtrlUtilsService, @LoadingUtilsService
                  @CATEGORY_POPUP_TEMPLATE, @CATEGORY_POPUP_CTRL
                  @$confirm
                  @CtrlInitiatorService) ->

# **********************************************************************************************************************
    initCategoryListViewCtrl: ($scope) ->
        _this = this

        throw "must set value $scope.title" if not $scope.title?
        throw "must set value $scope.categoryName" if not $scope.categoryName?
        throw "must set value $scope.usePopup" if not $scope.usePopup?
        throw "must implement $scope.isUseDistributorFilter" if not jQuery.isFunction($scope.isUseDistributorFilter)

        $scope.columns = $scope.columns ? [ { header: 'name', property: 'name' } ]

        if $scope.usePopup
            $scope.popupTemplateUrl = $scope.popupTemplateUrl ? _this.CATEGORY_POPUP_TEMPLATE
            $scope.popupCtrl = $scope.popupCtrl ? _this.CATEGORY_POPUP_CTRL
        else
            $scope.detailState = $scope.detailState ? (_this.$state.get('.').name + '-detail')

        $scope.useDeleteButton = $scope.useDeleteButton ? true
        $scope.useEnableButton = $scope.useEnableButton ? true
        $scope.useActivateButton = $scope.useActivateButton ? true

        $scope.isDisplayImportBtn = -> $scope.importState?
        $scope.goToImport = -> _this.$state.go($scope.importState, { filter: _this.$stateParams.filter })

        # FILTER
        $scope.distributorFilterChange = ->
            $scope.filter.searchText = null
            $scope.searchFilterChange()

        $scope.searchFilterChange = ->
            $scope.filter.currentPage = 1
            $scope.pageChange()

        # PROPERTY VALUE
        $scope.getPropertyValue = (column, record) ->
            if column? and column.property?
                if typeof column.property is 'function'
                    return column.property(record)
                else
                    return record[column.property]
            return ''

        # ACTION
        $scope.enableRecord = (record) ->
            action = 'enable'
            status = $scope.loadStatus.getStatusByDataName(action)
            params =
                who: $scope.who
                category: $scope.categoryName
                id: record.id
                action: action

            onSuccess = -> $scope.refresh()
            _this.CtrlUtilsService.doPut(params, status, null, onSuccess, null, action)

        $scope.changeActiveState = (record) ->
            action = if record.active then 'deactivate' else 'activate'
            status = $scope.loadStatus.getStatusByDataName(action)
            params =
                who: $scope.who
                category: $scope.categoryName
                id: record.id
                action: action

            onSuccess = -> $scope.refresh()
            _this.CtrlUtilsService.doPut(params, status, null, onSuccess, null, action)

        $scope.deleteRecord = (record) ->
            status = $scope.loadStatus.getStatusByDataName('data')

            params =
                who: $scope.who
                category: $scope.categoryName
                id: record.id

            onSuccess = -> $scope.refresh()
            _this.CtrlUtilsService.doDelete(params, status, onSuccess, null)

        # CREATE + EDIT
        openDetailPopup = (id) ->
            modalInstance = _this.$modal.open(
                templateUrl: $scope.popupTemplateUrl,
                controller: $scope.popupCtrl,
                resolve:
                    title: -> $scope.title
                    categoryName: -> $scope.categoryName
                    id: -> id
                    useDistributorFilter: -> $scope.isUseDistributorFilter()
                backdrop: true
            )
            modalInstance.result.then( -> $scope.refresh() )

        $scope.createRecord = ->
            if $scope.usePopup
                openDetailPopup(null)
            else
                _this.$state.go($scope.detailState)

        $scope.editRecord = (record) ->
            if $scope.usePopup
                openDetailPopup(record.id)
            else
                _this.$state.go($scope.detailState, { id: record.id, filter: $scope.getFilterAsString() })

        # INIT
        $scope.getReloadDataParams = (params) ->
            params = params ? {}
            params.category = $scope.categoryName

            if $scope.subCategory?
                params.subCategory = $scope.subCategory

            if $scope.isUseDistributorFilter() and $scope.filter.distributorId? and $scope.filter.distributorId != 'all'
                params.distributorId = $scope.filter.distributorId

            if not _.isEmpty($scope.filter.searchText)
                params.q = $scope.filter.searchText

            return params

        _this.CtrlInitiatorService.initPagingFilterViewCtrl($scope)

        $scope.addInitFunction((->
            if $scope.isUseDistributorFilter()
                _this.LoadingUtilsService.loadDistributors(
                    $scope.who
                    $scope.loadStatus.getStatusByDataName('distributors')
                    (list) ->
                        $scope.distributors = _.union(
                            [ { id: 'all', name: '-- ' + _this.$filter('translate')('all') + ' --' } ]
                            list
                        )

                        # CHECK DISTRIBUTOR
                        $scope.filter.distributorId = $scope.filter.distributorId ? 'all'

                        if $scope.filter.distributorId?
                            distributor = _.find($scope.distributors, (distributor) -> distributor.id is $scope.filter.distributorId)
                            if distributor?
                                $scope.reloadData()
                            else
                                _this.$state.go('404') if not distributor?
                        else
                            $scope.reloadData()
                )
            else
                $scope.reloadData()
        ), 'category-list-view')

# **********************************************************************************************************************
    initCategoryDetailViewCtrl: ($scope) ->
        _this = this

        throw "must set value $scope.title" if not $scope.title?
        throw "must set value $scope.categoryName" if not $scope.categoryName?
        throw "must implement $scope.isIdRequire" if not jQuery.isFunction($scope.isIdRequire)

        $scope.useMap = $scope.useMap ? false
        if $scope.useMap
            throw "must implement $scope.getLocation" if not jQuery.isFunction($scope.getLocation)

            $scope.firstLoadDone = false
            $scope.$on('locationSelectorReady', ->
                # RE NOTIFY MAP TO REFRESH BECAUSE MAP READY AFTER THE FIRST BROADCAST
                if $scope.firstLoadDone
                    $scope.$broadcast('refreshLocationSelector', { id: null, location: $scope.getLocation() })
            )

        $scope.isNew = -> $scope.isIdRequire() and not _this.$stateParams.id?
        $scope.isEdit = -> not $scope.isNew()
        $scope.isDraft = -> not ($scope.record? && $scope.record.draft? && !$scope.record.draft)

        afterAction = (data) ->
            if $scope.isNew()
                _newStateParam = angular.copy(_this.$stateParams)
                _newStateParam.id = data.id
                _this.$state.go('.', _newStateParam)
            else
                $scope.refresh()

        # SAVE
        currentCheckBeforeSave = $scope.checkBeforeSave
        $scope.checkBeforeSave = ->
            if $scope.isFormValid('form')
                if not jQuery.isFunction(currentCheckBeforeSave) or currentCheckBeforeSave()
                    return true
                else
                    return false
            else
                _this.toast.logError(_this.$filter('translate')('error.data.input.not.valid'))
                return false

        $scope.getObjectToSave = $scope.getObjectToSave ? -> $scope.record
        $scope.getSaveParams = (params) ->
            params.category = $scope.categoryName
            if not $scope.isNew() then params.id = _this.$stateParams.id
            return params
        $scope.onSaveSuccess = (data) -> afterAction(data)
        $scope.onSaveFailure = ->
        $scope.isPost = -> $scope.isNew()

        # ENABLE
        $scope.enable = ->
            if not $scope.isChanged() or $scope.checkBeforeSave()
                loadStatusOfData = $scope.loadStatus.getStatusByDataName('enable')

                requestBody = null
                if $scope.isChanged()
                    requestBody = $scope.getObjectToSave()

                params = { who: $scope.who }
                params = $scope.getSaveParams(params)
                params.action = 'enable'

                onSuccess = (data) ->
                    $scope.changeStatus.reset()
                    $scope.onSaveSuccess(data)

                onFailure = (error) ->
                    $scope.onSaveFailure(error)

                if $scope.isPost()
                    _this.CtrlUtilsService.doPost(params, loadStatusOfData, requestBody, onSuccess, onFailure, 'enable')
                else
                    _this.CtrlUtilsService.doPut(params, loadStatusOfData, requestBody, onSuccess, onFailure, 'enable')

        _this.CtrlInitiatorService.initCanSaveViewCtrl($scope)

        if $scope.isIdRequire()
            _this.CtrlInitiatorService.initCanBackViewCtrl($scope)

        $scope.reloadData = ->
            params =
                who: $scope.who
                category: $scope.categoryName

            if $scope.isIdRequire() then params.id = _this.$stateParams.id

            _this.CtrlUtilsService.loadSingleData(params
                $scope.loadStatus.getStatusByDataName('data')
                (record) ->
                    $scope.record = record

                    if $scope.useMap
                        # NOTIFY MAP TO REFRESH
                        $scope.$broadcast('refreshLocationSelector', { id: null, location: $scope.getLocation() })
                        $scope.firstLoadDone = true

                    if jQuery.isFunction($scope.onLoadSuccess) then $scope.onLoadSuccess($scope.record)

                (error) ->
                    if jQuery.isFunction($scope.onLoadFailure) then $scope.onLoadFailure(error)
                    if (error.status is 400)
                        _this.$state.go('404')
                    else
                        _this.toast.logError(_this.$filter('translate')('loading.error'))
            )

        $scope.addInitFunction(( -> $scope.reloadData()), 'category-detail-view')

# **********************************************************************************************************************
    initCategoryDetailPopupCtrl: ($scope, $modalInstance) ->
        _this = this

        throw "must set value $scope.title" if not $scope.title?
        throw "must set value $scope.categoryName" if not $scope.categoryName?
        throw "must implement $scope.isIdRequire" if not jQuery.isFunction($scope.isIdRequire)
        throw "must implement $scope.isUseDistributorFilter" if not jQuery.isFunction($scope.isUseDistributorFilter)

        $scope.isNew = -> $scope.isIdRequire() and not $scope.id?
        $scope.isEdit = -> not $scope.isNew()
        $scope.isDraft = -> not ($scope.record? && $scope.record.draft? && !$scope.record.draft)

        $scope.cancel = -> $modalInstance.dismiss('cancel')

        afterAction = ->  $modalInstance.close()

        # SAVE
        currentCheckBeforeSave = $scope.checkBeforeSave
        $scope.checkBeforeSave = ->
            if $scope.isFormValid('form')
                if not jQuery.isFunction(currentCheckBeforeSave) or currentCheckBeforeSave()
                    return true
                else
                    return false
            else
                _this.toast.logError(_this.$filter('translate')('error.data.input.not.valid'))
                return false

        $scope.getObjectToSave = $scope.getObjectToSave ? -> $scope.record
        $scope.getSaveParams = (params) ->
            params.category = $scope.categoryName
            if not $scope.isNew() then params.id = $scope.id
            return params
        $scope.onSaveSuccess = -> afterAction()
        $scope.onSaveFailure = -> #DO NOTHING
        $scope.isPost = -> $scope.isNew()

        # ENABLE
        $scope.enable = ->
            if not $scope.isChanged() or $scope.checkBeforeSave()
                loadStatusOfData = $scope.loadStatus.getStatusByDataName('enable')

                requestBody = null
                if $scope.isChanged()
                    requestBody = $scope.getObjectToSave()

                params = { who: $scope.who }
                params = $scope.getSaveParams(params)
                params.action = 'enable'

                onSuccess = (data) ->
                    $scope.changeStatus.reset()
                    $scope.onSaveSuccess(data)

                onFailure = (error) ->
                    $scope.onSaveFailure(error)

                if $scope.isPost()
                    _this.CtrlUtilsService.doPost(params, loadStatusOfData, requestBody, onSuccess, onFailure, 'enable')
                else
                    _this.CtrlUtilsService.doPut(params, loadStatusOfData, requestBody, onSuccess, onFailure, 'enable')

        _this.CtrlInitiatorService.initCanSaveViewCtrl($scope)

        $scope.reloadData = ->
            params =
                who: $scope.who
                category: $scope.categoryName

            if $scope.isIdRequire() then params.id = $scope.id

            _this.CtrlUtilsService.loadSingleData(params
                $scope.loadStatus.getStatusByDataName('data')
                (record) ->
                    $scope.record = record
                    if $scope.isUseDistributorFilter()
                        if $scope.isNew() or $scope.isDraft()
                            _this.LoadingUtilsService.loadDistributors(
                                $scope.who
                                $scope.loadStatus.getStatusByDataName('distributors')
                                (list) ->
                                    $scope.distributors = list
                            )

                        if $scope.record.distributor?
                            $scope.record.distributorId = $scope.record.distributor.id

                (error) ->
                    if (error.status is 400)
                        _this.$state.go('404')
                    else
                        _this.toast.logError(_this.$filter('translate')('loading.error'))
            )

        $scope.addInitFunction(( -> $scope.reloadData()), 'category-detail-popup')

# **********************************************************************************************************************
angular.module('app')

.service('CtrlCategoryInitiatorService', [
        '$log'
        '$filter', 'toast', '$modal'
        '$state', '$stateParams'
        'CtrlUtilsService', 'LoadingUtilsService'
        'CATEGORY_POPUP_TEMPLATE', 'CATEGORY_POPUP_CTRL'
        '$confirm'
        'CtrlInitiatorService'
        CtrlCategoryInitiatorService
    ])


