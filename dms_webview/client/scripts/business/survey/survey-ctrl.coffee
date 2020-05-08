'use strict'

angular.module('app')

.controller('SurveyCtrl', [
        '$scope', 'CtrlCategoryInitiatorService', '$filter'
        ($scope, CtrlCategoryInitiatorService, $filter) ->
            $scope.title = 'survey.title'
            $scope.categoryName = 'survey'
            $scope.usePopup = false
            $scope.isUseDistributorFilter = -> false

            $scope.useActivateButton = false

            $scope.columns = [
                { header: 'name', property: 'name' }
                { header: 'start.date', property: (record) -> $filter('isoDate')(record.startDate) }
                { header: 'end.date', property: (record) -> $filter('isoDate')(record.endDate) }
            ]

            CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope)

            $scope.init()
    ])

.controller('SurveyDetailCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', '$filter', 'LoadingUtilsService', 'ADDRESS_BACKEND', 'toast'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, $filter, LoadingUtilsService, ADDRESS_BACKEND, toast) ->
            $scope.title = 'survey.title'
            $scope.categoryName  = 'survey'
            $scope.isIdRequire = -> true
            $scope.defaultBackState = 'survey'

            # AFTER LOAD
            $scope.onLoadSuccess = ->
                $scope.startDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.record.startDate))
                $scope.endDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.record.endDate))

            # CHECK BEFORE SAVE
            $scope.checkBeforeSave = ->
                if $scope.startDate.date.getTime() > $scope.endDate.date.getTime()
                    toast.logError($filter('translate')('error.start.date.after.end.date'))
                    return false

                if not $scope.hasQuestion()
                    toast.logError($filter('translate')('error.data.input.not.valid'))
                    return false

                return true

            # GET OBJECT TO SAVE
            $scope.getObjectToSave = ->
                record = angular.copy($scope.record)
                record.startDate = globalUtils.createIsoDate($scope.startDate.date)
                record.endDate = globalUtils.createIsoDate($scope.endDate.date)
                return record

            $scope.hasQuestion = ->
                $scope.record? and $scope.record.questions? and $scope.record.questions.length > 0

            # QUESTION EDIT
            $scope.addNewQuestion = ->
                $scope.changeStatus.markAsChanged()
                $scope.currentEdit = {
                    index: null
                    question: {}
                }

            $scope.removeQuestion = (index) ->
                $scope.changeStatus.markAsChanged()
                $scope.record.questions.splice(index, 1)

            $scope.editQuestion = (index) ->
                $scope.changeStatus.markAsChanged()
                question = $scope.record.questions[index];
                questionEdit = angular.copy(question)
                if question.options?
                    questionEdit.options = []
                    questionEdit.options.push(angular.copy(option)) for option in question.options

                $scope.currentEdit = {
                    index: index
                    question: questionEdit
                }

            $scope.isDetailMode = -> $scope.currentEdit?
            $scope.exitDetailMode = -> $scope.currentEdit = null

            # DETAIL MODE
            $scope.applyEdit = ->
                if not $scope.isFormValid('form2')
                    toast.logError($filter('translate')('error.data.input.not.valid'))
                else
                    if not $scope.currentEdit.question.options? or $scope.currentEdit.question.options.length < 2
                        toast.logError($filter('translate')('survey.at.least.2.option'))
                    else
                        if $scope.currentEdit.index?
                            $scope.record.questions.splice($scope.currentEdit.index, 1, $scope.currentEdit.question)
                        else
                            if not $scope.record.questions?
                                $scope.record.questions = []
                            $scope.record.questions.push($scope.currentEdit.question)

                        $scope.exitDetailMode()
            $scope.hasOption = ->
                $scope.currentEdit? and $scope.currentEdit.question? and $scope.currentEdit.question.options? and $scope.currentEdit.question.options.length > 0
            $scope.addNewOption = ->
                if not $scope.currentEdit.question.options?
                    $scope.currentEdit.question.options = []
                $scope.currentEdit.question.options.push({})

            # INIT
            CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope)
            CtrlInitiatorService.initUseDatePickerCtrl($scope)

            $scope.init()
    ])
