'use strict'

angular.module('app.directives.file.image', ['angularFileUpload'])

.value('fileImageChooserConfig', {})

.controller('SingleFileCtrl', [
    '$scope', '$element', '$attrs', 'FileUploader', 'fileImageChooserConfig'
    ($scope, $element, $attrs, FileUploader, fileImageChooserConfig) ->
        # nullModelCtrl
        ngModelCtrl = { $setViewValue: angular.noop }

        @init = (p_ngModelCtrl) ->
            ngModelCtrl = p_ngModelCtrl

        $scope.getFileName = ->
            if ngModelCtrl.$viewValue?
                ngModelCtrl.$viewValue.name
            else null

        inputs = $element.find('input')
        input = null;
        if inputs? and inputs.length > 0
            for _input in inputs
                if _input.type is 'file'
                    input = $(_input)
                    break

        $scope.getAccepts = ->
            if $attrs.accept?
                $attrs.accept
            else '*/*'

        url = fileImageChooserConfig.getBaseUrl() + 'file'
        uploader = $scope.uploader = new FileUploader(
            url: url
            headers: null
        )

        uploader.onAfterAddingAll = (addedItems) ->
            for item in addedItems
                item.headers = { Authorization: 'Bearer ' + fileImageChooserConfig.getAccessToken() }

            uploader.uploadAll()

        uploader.onSuccessItem = (fileItem, response) ->
            fileItem.id = response.id

            file =
                fileId: fileItem.id
                name: fileItem._file.name


            ngModelCtrl.$setViewValue(file)
            ngModelCtrl.$render()

            if input?
                input.val("")
])

.directive('singleFile', ->
    restrict: 'AE'
    require: ['singleFile', '?ngModel']
    replace: true
    scope: {}
    templateUrl: "views/directive/single-file.html"
    controller: "SingleFileCtrl"
    link: (scope, element, attrs, ctrls) ->
        SingleFileCtrl = ctrls[0]
        ngModelCtrl = ctrls[1]

        if not ngModelCtrl?
            # do nothing if no ng-model
            return

        SingleFileCtrl.init(ngModelCtrl)
)

.controller('SingleImageCtrl',
    ['$scope', '$element', '$attrs', 'FileUploader', 'fileImageChooserConfig', '$http'
    ($scope, $element, $attrs, FileUploader, fileImageChooserConfig, $http) ->
        # nullModelCtrl
        ngModelCtrl = { $setViewValue: angular.noop }

        @init = (p_ngModelCtrl) ->
            ngModelCtrl = p_ngModelCtrl

        $scope.getFile = ->
            ngModelCtrl.$viewValue

        $scope.getLink = ->
            id = $scope.getFile()
            if id?
                fileImageChooserConfig.getBaseUrl() + 'image/' + id
            else null

        sizeType = null
        if $attrs.sizeType?
            sizeType = $attrs.sizeType

        url = fileImageChooserConfig.getBaseUrl() + 'image'
        if sizeType? and ('thumb' is sizeType || 'medium' is sizeType || 'standard' is sizeType)
            url = url + "?sizetype=" + sizeType

        inputs = $element.find('input')
        input = null;
        if inputs? and inputs.length > 0
            for _input in inputs
                if _input.type is 'file'
                    input = $(_input)
                    break

        tempFileItem = null;

        uploader = $scope.uploader = new FileUploader(
            url: url
            headers: null
        )

        uploader.onAfterAddingAll = (addedItems) ->
            for item in addedItems
                item.headers = { Authorization: 'Bearer ' + fileImageChooserConfig.getAccessToken() }

            uploader.uploadAll()

        uploader.onSuccessItem = (fileItem, response) ->
            fileItem.id = response.id;

            ngModelCtrl.$setViewValue(fileItem.id)
            ngModelCtrl.$render()

            tempFileItem = fileItem

            if input?
                input.val("")

        $scope.removeFile = ->
            if ngModelCtrl.$viewValue?
                deleteURL = fileImageChooserConfig.getBaseUrl() + 'file'
                + '?id=' + ngModelCtrl.$viewValue
                + '&access_token=' + fileImageChooserConfig.getAccessToken();

            $http.delete(deleteURL).success()

            if tempFileItem?
                tempFileItem.remove()

            ngModelCtrl.$setViewValue(null);
            ngModelCtrl.$render();

            if input?
                input.val("")

])

.directive('singleImage', ->
    restrict: 'AE'
    require: ['singleImage', '?ngModel']
    replace: true
    scope: {}
    templateUrl: "views/directive/single-image.html"
    controller: "SingleImageCtrl"
    link: (scope, element, attrs, ctrls) ->
        SingleImageCtrl = ctrls[0]
        ngModelCtrl = ctrls[1]

        if not ngModelCtrl?
            return

        SingleImageCtrl.init(ngModelCtrl)
)
