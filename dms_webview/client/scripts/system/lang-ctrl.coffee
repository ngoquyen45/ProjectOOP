'use strict'

angular.module('app')
.controller('LangCtrl', [
    '$scope', '$translate', '$token'
    ($scope, $translate, $token) ->

        # Get current language used by angular-translate
        $scope.lang = $translate.use()

        $scope.$watch(
            () ->
                $translate.use()
            (newLang) ->
                $scope.lang = newLang
        )

        # When use choose language from header menu
        $scope.setLang = (lang) ->
            $translate.use(lang)
            $scope.lang = lang
            $token.setUserLanguage(lang)

        $scope.getFlag = () ->
            lang = $scope.lang
            switch lang
                when 'vi' then return 'flags-vietnam'
                when 'zh' then return 'flags-china'
                when 'km' then return 'flags-cambodia'
                else return 'flags-british'

        $scope.isSupportedLanguage = (lang) ->
            if $token?
                userInfo = $token.getUserInfo()
                if userInfo? and userInfo.languages?
                    return _.includes(userInfo.languages, lang)

            return false

        $scope.isShowLangInDropDown = (lang) ->
            if ($scope.isSupportedLanguage(lang))
                if $scope.lang isnt lang
                    return true
            return false
])
