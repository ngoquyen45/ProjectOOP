'use strict'

class CtrlUtilsService

    constructor: (@Factory, @$log,
                  @$filter, @$token, @toast) ->

    getWho: ->
        roleCode = @$token.getRoleCode()
        if roleCode?
            switch roleCode
                when 'SUPER' then 'super-admin'
                when 'SUPPORTER' then 'supporter'
                when 'AD' then 'admin'
                when 'OBS' then 'observer'
                when 'SUP' then 'supervisor'
                when 'DIS' then 'distributor'
                else null
        else null

    getUserLanguage: -> @$token.getUserLanguage()

    # DO POST
    doPost: (params, loadStatusOfData, requestBody, callbackSuccess, callbackFailure, action) ->
        _this = this
        loadStatusOfData.processing = true

        action = action ? 'save'

        onSuccess = (data) ->
            loadStatusOfData.processing = false
            if jQuery.isFunction(callbackSuccess) then callbackSuccess(data)
            _this.toast.logSuccess(_this.$filter('translate')(action + '.success'))

        onFailure = (error) ->
            loadStatusOfData.processing = false
            _this.$log.log(error)
            if jQuery.isFunction(callbackFailure) then callbackFailure(error)
            if error.status is 400
                _this.toast.logError(_this.$filter('translate')(error.data.meta.error_message))
            else
                _this.toast.logError(_this.$filter('translate')(action + '.error'))

        _this.Factory.doPost(params, requestBody, onSuccess, onFailure)

    #  DO PUT
    doPut: (params, loadStatusOfData, requestBody, callbackSuccess, callbackFailure, action) ->
        _this = this
        loadStatusOfData.processing = true

        action = action ? 'save'

        onSuccess = (data) ->
            loadStatusOfData.processing = false
            if jQuery.isFunction(callbackSuccess) then callbackSuccess(data)
            _this.toast.logSuccess(_this.$filter('translate')(action + '.success'))

        onFailure = (error) ->
            loadStatusOfData.processing = false
            _this.$log.log(error)
            if jQuery.isFunction(callbackFailure) then callbackFailure(error)
            if error.status is 400
                _this.toast.logError(_this.$filter('translate')(error.data.meta.error_message))
            else
                _this.toast.logError(_this.$filter('translate')(action + '.error'))

        _this.Factory.doPut(params, requestBody, onSuccess, onFailure)

    #  DO DELETE
    doDelete: (params, loadStatusOfData, callbackSuccess, callbackFailure) ->
        _this = this
        loadStatusOfData.processing = true

        onSuccess = (data) ->
            loadStatusOfData.processing = false
            if jQuery.isFunction(callbackSuccess) then callbackSuccess(data)
            _this.toast.logSuccess(_this.$filter('translate')('delete.success'))

        onFailure = (error) ->
            loadStatusOfData.processing = false
            _this.$log.log(error)
            if jQuery.isFunction(callbackFailure) then callbackFailure(error)
            if error.status is 400
                _this.toast.logError(_this.$filter('translate')(error.data.meta.error_message))
            else
                _this.toast.logError(_this.$filter('translate')('delete.error'))

        _this.Factory.doDelete(params, onSuccess, onFailure)

    # GET SINGLE DATA AND LIST
    #  SINGLE DATA
    loadSingleData: (params, loadStatusOfData, callbackSuccess, callbackFailure) ->
        _this = this
        loadStatusOfData.processing = true
        loadStatusOfData.error = false

        onSuccess = (data) ->
            loadStatusOfData.processing = false
            if jQuery.isFunction(callbackSuccess) then callbackSuccess(data ? {})

        onFailure = (error) ->
            loadStatusOfData.processing = false
            loadStatusOfData.error = true
            _this.$log.log(error)
            if jQuery.isFunction(callbackFailure) then callbackFailure(error)

        _this.Factory.doGet(params, onSuccess, onFailure)

    #  LIST
    loadListData: (params, loadStatusOfData, callbackSuccess, callbackFailure) ->
        _this = this
        loadStatusOfData.processing = true
        loadStatusOfData.error = false

        onSuccess = (data) ->
            loadStatusOfData.processing = false
            list = if data? and data.list? then data.list else []
            count = if data? and data.list? then data.count else 0
            if jQuery.isFunction(callbackSuccess) then callbackSuccess(list, count)

        onFailure = (error) ->
            loadStatusOfData.processing = false
            loadStatusOfData.error = true
            _this.$log.log(error)
            if jQuery.isFunction(callbackFailure) then callbackFailure(error)

        _this.Factory.doGet(params, onSuccess, onFailure)

    # LOAD STATUS
    createLoadStatus: (dataNames...) -> new LoadStatus(dataNames)

    # CHANGE STATUS
    createChangeStatus: -> new ChangeStatus()

    # PRINT ORDER
    printOrder: (orderId) ->
        printContents = document.getElementById(orderId).innerHTML
        popupWin = window.open()
        popupWin.document.open()
        popupWin.document.write(
            '<html>' +
            '<head>' +
            '<link rel="stylesheet" type="text/css" href="styles/main.css"/>' +
            '</head>' +
            '<body onload="window.print()" class="print">' +
            printContents +
            '</body>' +
            '</html>'
        )
        popupWin.document.close()

    # GET USER INFO
    getUserInfo: -> @$token.getUserInfo()

    getDefaultZoom: -> 14

# **********************************************************************************************************************
class LoadStatus
    constructor: (dataNames) ->
        @status = {}
        for dataName in dataNames
            @status[dataName] = { processing: false, error: false }

    getStatusByDataName: (dataName) ->
        _status = @status[dataName]
        if not _status?
            _status = { processing: false, error: false }
            @status[dataName] = _status
        return _status

    isProcessing: ->
        if @status?
            for dataName of @status
                if @status[dataName].processing then return true
        return false

    isError: ->
        if @status?
            for dataName of @status
                if @status[dataName].error then return true
        return false

class ChangeStatus
    constructor: -> @isChanged = false
    markAsChanged: -> @isChanged = true
    reset: -> @isChanged = false

# **********************************************************************************************************************
angular.module('app')
.service('CtrlUtilsService', [
        'Factory', '$log',
        '$filter', '$token', 'toast'
        CtrlUtilsService
    ])
