'use strict'

class CtrlInitiatorService

    constructor: (@$log
                  @$filter, @toast, @$modal
                  @$state, @$stateParams
                  @CtrlUtilsService, @LoadingUtilsService
                  @$confirm) ->

# **********************************************************************************************************************
    initBasicViewCtrl: ($scope) ->
        _this = this

        #*** UTILS ***
        $scope.isEmpty = $scope.isEmpty ? -> false
        $scope.isOnePage = $scope.isOnePage ? -> false

        $scope.initFunctions = $scope.initFunctions ? []
        $scope.addInitFunction = (initFunction, name) ->
            if not jQuery.isFunction(initFunction)
                throw "initFunction cannot null and must be a function"

            if name?
                if _.findIndex($scope.initFunctions, (o) -> o.name? and o.name is name) < 0
                    $scope.initFunctions.push( { function: initFunction, name: name } )
            else
                $scope.initFunctions.push( { function: initFunction, name: null } )
        $scope.init = ->
            for container in $scope.initFunctions
                container.function()

        $scope.refresh = $scope.refresh ? (-> $scope.init())

        $scope.isFormValid = (formName) ->
            _scope = $scope
            while _scope?
                if _scope[formName]?
                    return _scope[formName].$valid
                else
                    _scope = _scope.$$childHead
            return false
        #***********

        # LOADING STATUS
        $scope.who = $scope.who ? _this.CtrlUtilsService.getWho()

        $scope.isLoading = -> $scope.loadStatus.isProcessing() and not $scope.loadStatus.isError()
        $scope.isError = -> $scope.loadStatus.isError()
        $scope.isDisplayContent = -> not ($scope.isLoading() or $scope.isError())

        # MY INIT
        $scope.addInitFunction((-> $scope.loadStatus = _this.CtrlUtilsService.createLoadStatus('data')), 'basic-view')

# **********************************************************************************************************************
    initFilterViewCtrl: ($scope, defaultFilter) ->
        _this = this

        throw "must implement $scope.afterFilterLoad" if not jQuery.isFunction($scope.afterFilterLoad)

        _this.initBasicViewCtrl($scope)

        utf8_to_b64 = (str) ->
            # window.btoa(unescape(encodeURIComponent(str)))
            str = window.btoa(unescape(encodeURIComponent(str)))
            str = str.replace(/\+/g, '-').replace(/\//g, '_').replace(/\=+$/, '')
            return str

        b64_to_utf8 = (str) ->
            # decodeURIComponent(escape(window.atob(str)))
            if (str.length % 4) is 3
                str = str + '='
            else if (str.length % 4) is 2
                str = str + '=='
            else if (str.length % 4) is 1
                str = str + '==='

            str = str.replace(/-/g, '+').replace(/_/g, '/')

            return decodeURIComponent(escape(window.atob(str)))

        object_to_b64 = (object) ->
            if object?
                return utf8_to_b64(JSON.stringify(object))
            else
                return null
        b64_to_object = (str, onError) ->
            if str?
                try
                    return JSON.parse(b64_to_utf8(str))
                catch error
                    _this.$log.log(error)
                    onError() if jQuery.isFunction(onError)

            return null

        # GET FILTER FROM PARAMS
        $scope.getFilterAsString = -> object_to_b64($scope.filter)
        $scope.reloadForNewFilter = -> _this.$state.go('.', { filter: $scope.getFilterAsString() }, { reload: true })

        $scope.addInitFunction((->
            $scope.filter = b64_to_object(_this.$stateParams.filter, -> _this.$state.go('404'))

            if $scope.filter?
                if defaultFilter?
                    for key of defaultFilter
                        if not $scope.filter[key]?
                            $scope.filter[key] = angular.copy(defaultFilter[key])
            else
                $scope.filter = defaultFilter ? {}

            $scope.afterFilterLoad()

        ), 'filter-view')

# **********************************************************************************************************************
    initPagingFilterViewCtrl: ($scope) ->
        _this = this

        throw "must implement $scope.getReloadDataParams" if not jQuery.isFunction($scope.getReloadDataParams)

        defaultFilter =
            currentPage: 1
            itemsPerPage: 10

        $scope.afterFilterLoad = ->
            if $scope.filter.currentPage < 1
                $scope.filter.currentPage = defaultFilter.currentPage
            if $scope.filter.itemsPerPage <= 0
                $scope.filter.itemsPerPage = defaultFilter.itemsPerPage

        _this.initFilterViewCtrl($scope, defaultFilter)

        # PAGE CHANGE
        $scope.pageChange = ->
            if $scope.loaded
                $scope.filter.currentPage = $scope.pagingData.currentPage
                $scope.reloadForNewFilter()

        # LOAD DATA
        $scope.afterLoad = $scope.afterLoad ? ( -> )
        $scope.reloadData = ->
            $scope.pagingData = { pagingMaxSize: 5 }

            params =
                who: $scope.who
                page: $scope.filter.currentPage
                size: $scope.filter.itemsPerPage

            params = $scope.getReloadDataParams(params)

            if not params?
                $scope.loaded = true
            else
                $scope.loaded = false

                _this.CtrlUtilsService.loadListData(params
                    $scope.loadStatus.getStatusByDataName('data')
                    (records, count) ->
                        if _.isEmpty(records) and count > 0 and params.page > 1
                            newPage = Math.floor(count/$scope.filter.itemsPerPage)
                            newPage += (if count % $scope.filter.itemsPerPage > 0 then 1 else 0)
                            $scope.filter.currentPage = newPage
                            $scope.loaded = true
                            $scope.reloadForNewFilter()
                        else
                            $scope.records = records
                            $scope.pagingData.count = count
                            $scope.pagingData.currentPage = $scope.filter.currentPage
                            $scope.pagingData.itemsPerPage = $scope.filter.itemsPerPage

                            $scope.afterLoad(records, count)

                            $scope.loaded = true
                    (error) ->
                        if (error.status is 400)
                            _this.$state.go('404')
                        else
                            _this.toast.logError(_this.$filter('translate')('loading.error'))
                )

        $scope.isEmpty = -> _.isEmpty($scope.records)
        $scope.isOnePage = -> ($scope.pagingData? and $scope.pagingData.count? and $scope.pagingData.itemsPerPage? and $scope.pagingData.count <= $scope.pagingData.itemsPerPage)

        $scope.addInitFunction(( ->
            $scope.loaded = false
            $scope.pagingData = { pagingMaxSize: 5 }

        ), 'paging-filter-view')

# **********************************************************************************************************************
    initCanSaveViewCtrl: ($scope) ->
        _this = this

        throw "must implement $scope.checkBeforeSave" if not jQuery.isFunction($scope.checkBeforeSave)
        throw "must implement $scope.getObjectToSave" if not jQuery.isFunction($scope.getObjectToSave)
        throw "must implement $scope.getSaveParams" if not jQuery.isFunction($scope.getSaveParams)
        throw "must implement $scope.onSaveSuccess" if not jQuery.isFunction($scope.onSaveSuccess)
        throw "must implement $scope.onSaveFailure" if not jQuery.isFunction($scope.onSaveFailure)
        throw "must implement $scope.isPost" if not jQuery.isFunction($scope.isPost)

        _this.initBasicViewCtrl($scope)

        $scope.confirmOnSave = $scope.confirmOnSave ? (-> null)

        $scope.isChanged = -> $scope.changeStatus? and $scope.changeStatus.isChanged
        $scope.isDisplaySaveBtn = -> $scope.isChanged()
        $scope.refresh = ->
            if $scope.isDisplaySaveBtn()
                _this.$confirm( { text: _this.$filter('translate')('confirm.dialog.are.you.sure.refresh') } ).then( ->
                    $scope.init()
                )
            else
                $scope.init()

        _save = ->
            loadStatusOfData = $scope.loadStatus.getStatusByDataName('save')

            requestBody = $scope.getObjectToSave()

            params = { who: $scope.who }
            params = $scope.getSaveParams(params)

            onSuccess = (data) ->
                $scope.changeStatus.reset()
                $scope.onSaveSuccess(data)

            onFailure = (error) ->
                $scope.onSaveFailure(error)

            if $scope.isPost()
                _this.CtrlUtilsService.doPost(params, loadStatusOfData, requestBody, onSuccess, onFailure, 'save')
            else
                _this.CtrlUtilsService.doPut(params, loadStatusOfData, requestBody, onSuccess, onFailure, 'save')

        $scope.save = ->
            if $scope.checkBeforeSave()
                confirmOnSave = $scope.confirmOnSave()
                if confirmOnSave?
                    _this.$confirm( confirmOnSave.data, confirmOnSave.settings ).then( -> _save() )
                else
                    _save()

        $scope.addInitFunction((->
            $scope.changeStatus = _this.CtrlUtilsService.createChangeStatus()
        ), 'can-save-view')

# **********************************************************************************************************************
    initCanBackViewCtrl: ($scope) ->
        _this = this

        throw "must set value $scope.defaultBackState" if not $scope.defaultBackState?

        _this.initBasicViewCtrl($scope)

        $scope.isDisplayBackBtn = -> true

        $scope.back = ->
            if jQuery.isFunction($scope.isChanged) and $scope.isChanged()
                _this.$confirm( { text: _this.$filter('translate')('confirm.dialog.are.you.sure.back') } ).then( ->
                    backState = _this.$stateParams.parent ? $scope.defaultBackState
                    _this.$state.go( backState, { filter: _this.$stateParams.filter } )
                )
            else
                backState = _this.$stateParams.parent ? $scope.defaultBackState
                _this.$state.go( backState, { filter: _this.$stateParams.filter } )

# **********************************************************************************************************************
    initUseDatePickerCtrl: ($scope) ->
        _this = this

        _this.initBasicViewCtrl($scope)

        $scope.createDatePickerModel = (date) ->
            model = { date: date, opened: false }
            $scope.datePickerModels.push(model)
            return model

        $scope.removeDatePickerModel = (model) ->
            if model?
                $scope.datePickerModels.splice(_.indexOf($scope.datePickerModels, model), 1)

        $scope.open = ($event, model) ->
            if jQuery.isFunction($scope.onOpenDatePicker) then $scope.onOpenDatePicker(model)
            $event.preventDefault()
            $event.stopPropagation()
            if model?
                model.opened = true
                otherModel.opened = false for otherModel in $scope.datePickerModels when otherModel? and otherModel isnt model

        $scope.dateOptions =
            'year-format': 'yy'
            'starting-day': 1

        $scope.dateFormat = _this.CtrlUtilsService.getUserInfo().dateFormat

        $scope.addInitFunction((->
            $scope.datePickerModels = []
        ), 'use-date-view')

# **********************************************************************************************************************
angular.module('app')

.service('CtrlInitiatorService', [
        '$log'
        '$filter', 'toast', '$modal'
        '$state', '$stateParams'
        'CtrlUtilsService', 'LoadingUtilsService'
        '$confirm'
        CtrlInitiatorService
    ])

class window.ListenerContainer

    constructor: ->
        @container= {}

    addListener: (name, callback) ->
        listeners = @container[name]
        if not listeners?
            listeners = []
            @container[name] = listeners

        listeners.push(callback)

    executeCallback: (listenerName, args...) ->
        listeners = @container[listenerName]
        if listeners?
            for listener in listeners
                value = listener.apply(this, args)
                if value isnt null and value is false
                    return false

        return true

    getContainer: -> @container

    merge: (listenerContainer) ->
        if listenerContainer?
            _container = listenerContainer.getContainer()
            if _container?
                for listenerName of _container
                    listeners = _container[listenerName]
                    @addListener(listenerName, listener) for listener in listeners
