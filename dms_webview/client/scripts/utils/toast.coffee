'use strict'

angular.module('app.toast', [])

.service('toast', ->
    toastr.options =
        closeButton: true
        positionClass: 'toast-bottom-right'
        timeOut: '3000'

    logIt = (message, type) -> toastr[type] message

    this.log = (message) -> logIt message, 'info'
    this.logWarning = (message) -> logIt message, 'warning'
    this.logSuccess = (message) -> logIt message, 'success'
    this.logError = (message) -> logIt message, 'error'

    return

)
