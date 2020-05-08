(function() {
  'use strict';
  angular.module('app.filter', []).filter('propsFilter', function() {
    return function(items, props) {
      var item, itemMatches, key, keys, out, text, value, _i, _j, _len, _len1;
      out = [];
      if (angular.isArray(items)) {
        for (_i = 0, _len = items.length; _i < _len; _i++) {
          item = items[_i];
          itemMatches = false;
          keys = Object.keys(props);
          for (_j = 0, _len1 = keys.length; _j < _len1; _j++) {
            key = keys[_j];
            text = props[key] == null ? '' : props[key].toLowerCase();
            value = '';
            if ((item != null) && (item[key] != null)) {
              value = item[key].toString().toLowerCase();
            }
            if (value.indexOf(text) !== -1) {
              itemMatches = true;
              break;
            }
          }
          if (itemMatches) {
            out.push(item);
          }
        }
      } else {
        out = items;
      }
      return out;
    };
  }).filter('isoDate', [
    'CtrlUtilsService', function(CtrlUtilsService) {
      return function(str) {
        var date;
        if (str != null) {
          date = globalUtils.parseIsoDate(str);
          if (date != null) {
            return moment(date).format(CtrlUtilsService.getUserInfo().dateFormat.toUpperCase());
          }
        }
        return '';
      };
    }
  ]).filter('isoDateTime', [
    'CtrlUtilsService', function(CtrlUtilsService) {
      return function(str) {
        var date;
        if (str != null) {
          date = globalUtils.parseIsoTime(str);
          if (date != null) {
            return moment(date).format(CtrlUtilsService.getUserInfo().dateFormat.toUpperCase() + ' HH:mm:ss');
          }
        }
        return '';
      };
    }
  ]).filter('isoTime', function() {
    return function(str) {
      var date;
      if (str != null) {
        date = globalUtils.parseIsoTime(str);
        if (date != null) {
          return moment(date).format('HH:mm:ss');
        }
      }
      return '';
    };
  }).filter('bigNumber', [
    '$filter', function($filter) {
      return function(value) {
        var unit;
        if (value != null) {
          if (isNaN(value)) {
            return value;
          } else {
            unit = '';
            if (value > 10000000000) {
              value = value / 1000000000;
              unit = ' B';
            } else if (value > 10000000) {
              value = value / 1000000;
              unit = ' M';
            } else if (value > 10000) {
              value = value / 1000;
              unit = ' k';
            }
            if (value > 99) {
              return $filter('number')(value, 0) + unit;
            } else if (value > 1) {
              return $filter('number')(value, 1) + unit;
            } else {
              return $filter('number')(value, 2) + unit;
            }
          }
        }
      };
    }
  ]).filter('bigInteger', [
    '$filter', function($filter) {
      return function(value) {
        if (value != null) {
          if (isNaN(value)) {
            return value;
          } else {
            return $filter('bigNumber')($filter('number')(value, 0));
          }
        }
      };
    }
  ]);

}).call(this);

//# sourceMappingURL=filter.js.map
