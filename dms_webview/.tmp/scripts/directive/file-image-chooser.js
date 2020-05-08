(function() {
  'use strict';
  angular.module('app.directives.file.image', ['angularFileUpload']).value('fileImageChooserConfig', {}).controller('SingleFileCtrl', [
    '$scope', '$element', '$attrs', 'FileUploader', 'fileImageChooserConfig', function($scope, $element, $attrs, FileUploader, fileImageChooserConfig) {
      var input, inputs, ngModelCtrl, uploader, url, _i, _input, _len;
      ngModelCtrl = {
        $setViewValue: angular.noop
      };
      this.init = function(p_ngModelCtrl) {
        return ngModelCtrl = p_ngModelCtrl;
      };
      $scope.getFileName = function() {
        if (ngModelCtrl.$viewValue != null) {
          return ngModelCtrl.$viewValue.name;
        } else {
          return null;
        }
      };
      inputs = $element.find('input');
      input = null;
      if ((inputs != null) && inputs.length > 0) {
        for (_i = 0, _len = inputs.length; _i < _len; _i++) {
          _input = inputs[_i];
          if (_input.type === 'file') {
            input = $(_input);
            break;
          }
        }
      }
      $scope.getAccepts = function() {
        if ($attrs.accept != null) {
          return $attrs.accept;
        } else {
          return '*/*';
        }
      };
      url = fileImageChooserConfig.getBaseUrl() + 'file';
      uploader = $scope.uploader = new FileUploader({
        url: url,
        headers: null
      });
      uploader.onAfterAddingAll = function(addedItems) {
        var item, _j, _len1;
        for (_j = 0, _len1 = addedItems.length; _j < _len1; _j++) {
          item = addedItems[_j];
          item.headers = {
            Authorization: 'Bearer ' + fileImageChooserConfig.getAccessToken()
          };
        }
        return uploader.uploadAll();
      };
      return uploader.onSuccessItem = function(fileItem, response) {
        var file;
        fileItem.id = response.id;
        file = {
          fileId: fileItem.id,
          name: fileItem._file.name
        };
        ngModelCtrl.$setViewValue(file);
        ngModelCtrl.$render();
        if (input != null) {
          return input.val("");
        }
      };
    }
  ]).directive('singleFile', function() {
    return {
      restrict: 'AE',
      require: ['singleFile', '?ngModel'],
      replace: true,
      scope: {},
      templateUrl: "views/directive/single-file.html",
      controller: "SingleFileCtrl",
      link: function(scope, element, attrs, ctrls) {
        var SingleFileCtrl, ngModelCtrl;
        SingleFileCtrl = ctrls[0];
        ngModelCtrl = ctrls[1];
        if (ngModelCtrl == null) {
          return;
        }
        return SingleFileCtrl.init(ngModelCtrl);
      }
    };
  }).controller('SingleImageCtrl', [
    '$scope', '$element', '$attrs', 'FileUploader', 'fileImageChooserConfig', '$http', function($scope, $element, $attrs, FileUploader, fileImageChooserConfig, $http) {
      var input, inputs, ngModelCtrl, sizeType, tempFileItem, uploader, url, _i, _input, _len;
      ngModelCtrl = {
        $setViewValue: angular.noop
      };
      this.init = function(p_ngModelCtrl) {
        return ngModelCtrl = p_ngModelCtrl;
      };
      $scope.getFile = function() {
        return ngModelCtrl.$viewValue;
      };
      $scope.getLink = function() {
        var id;
        id = $scope.getFile();
        if (id != null) {
          return fileImageChooserConfig.getBaseUrl() + 'image/' + id;
        } else {
          return null;
        }
      };
      sizeType = null;
      if ($attrs.sizeType != null) {
        sizeType = $attrs.sizeType;
      }
      url = fileImageChooserConfig.getBaseUrl() + 'image';
      if ((sizeType != null) && ('thumb' === sizeType || 'medium' === sizeType || 'standard' === sizeType)) {
        url = url + "?sizetype=" + sizeType;
      }
      inputs = $element.find('input');
      input = null;
      if ((inputs != null) && inputs.length > 0) {
        for (_i = 0, _len = inputs.length; _i < _len; _i++) {
          _input = inputs[_i];
          if (_input.type === 'file') {
            input = $(_input);
            break;
          }
        }
      }
      tempFileItem = null;
      uploader = $scope.uploader = new FileUploader({
        url: url,
        headers: null
      });
      uploader.onAfterAddingAll = function(addedItems) {
        var item, _j, _len1;
        for (_j = 0, _len1 = addedItems.length; _j < _len1; _j++) {
          item = addedItems[_j];
          item.headers = {
            Authorization: 'Bearer ' + fileImageChooserConfig.getAccessToken()
          };
        }
        return uploader.uploadAll();
      };
      uploader.onSuccessItem = function(fileItem, response) {
        fileItem.id = response.id;
        ngModelCtrl.$setViewValue(fileItem.id);
        ngModelCtrl.$render();
        tempFileItem = fileItem;
        if (input != null) {
          return input.val("");
        }
      };
      return $scope.removeFile = function() {
        var deleteURL;
        if (ngModelCtrl.$viewValue != null) {
          deleteURL = fileImageChooserConfig.getBaseUrl() + 'file';
          +'?id=' + ngModelCtrl.$viewValue;
          +'&access_token=' + fileImageChooserConfig.getAccessToken();
        }
        $http["delete"](deleteURL).success();
        if (tempFileItem != null) {
          tempFileItem.remove();
        }
        ngModelCtrl.$setViewValue(null);
        ngModelCtrl.$render();
        if (input != null) {
          return input.val("");
        }
      };
    }
  ]).directive('singleImage', function() {
    return {
      restrict: 'AE',
      require: ['singleImage', '?ngModel'],
      replace: true,
      scope: {},
      templateUrl: "views/directive/single-image.html",
      controller: "SingleImageCtrl",
      link: function(scope, element, attrs, ctrls) {
        var SingleImageCtrl, ngModelCtrl;
        SingleImageCtrl = ctrls[0];
        ngModelCtrl = ctrls[1];
        if (ngModelCtrl == null) {
          return;
        }
        return SingleImageCtrl.init(ngModelCtrl);
      }
    };
  });

}).call(this);

//# sourceMappingURL=file-image-chooser.js.map
