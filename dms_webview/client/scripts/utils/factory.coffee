'use strict'

angular.module('app')

.factory('Factory', [
    '$resource', 'ADDRESS_BACKEND'
    ($resource, ADDRESS_BACKEND) ->
        url = ADDRESS_BACKEND + ':who/:category/:subCategory/:id/:param/:action/:subAction'
        paramDefaults = null
        actions =
            doPost:
                method: 'POST'
                headers: {'Content-Type':'application/json'}
            doPut:
                method: 'PUT'
                headers: {'Content-Type':'application/json'}
            doGet:
                method: 'GET'
                isArray: false
            doDelete:
                method: 'DELETE'

        $resource(url, paramDefaults, actions)
])
