(function() {
  'use strict';
  angular.module('app').factory('Factory', [
    '$resource', 'ADDRESS_BACKEND', function($resource, ADDRESS_BACKEND) {
      var actions, paramDefaults, url;
      url = ADDRESS_BACKEND + ':who/:category/:subCategory/:id/:param/:action/:subAction';
      paramDefaults = null;
      actions = {
        doPost: {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          }
        },
        doPut: {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          }
        },
        doGet: {
          method: 'GET',
          isArray: false
        },
        doDelete: {
          method: 'DELETE'
        }
      };
      return $resource(url, paramDefaults, actions);
    }
  ]);

}).call(this);

//# sourceMappingURL=factory.js.map
