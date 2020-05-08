(function() {
  'use strict';
  var utils;

  utils = {};

  utils.parseIsoDate = function(isoDate) {
    if (isoDate != null) {
      if (isoDate.length === 19) {
        return moment(isoDate, 'YYYY-MM-DDTHH:mm:ss').toDate();
      } else if (isoDate.length === 10) {
        return moment(isoDate, 'YYYY-MM-DD').toDate();
      }
    }
  };

  utils.parseIsoTime = function(isoTime) {
    if (isoTime != null) {
      if (isoTime.length === 10) {
        return moment(isoTime, 'YYYY-MM-DD').toDate();
      } else if (isoTime.length === 19) {
        return moment(isoTime, 'YYYY-MM-DDTHH:mm:ss').toDate();
      }
    }
  };

  utils.createIsoDate = function(date) {
    if (date != null) {
      return moment(date).format('YYYY-MM-DD');
    }
  };

  utils.escapeHTML = function(html) {
    html = _.escape(html);
    return html;
  };

  utils.getChartLabel = function(label) {
    var length;
    if (label.length > 23) {
      length = label.length;
      label = label.substring(0, 10) + '...' + label.substring(length - 10);
    }
    label = _.escape(label);
    return label;
  };

  window.globalUtils = utils;

}).call(this);

//# sourceMappingURL=global-utils.js.map
