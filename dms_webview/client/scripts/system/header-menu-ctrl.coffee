'use strict'

angular.module('app')

.controller('HeaderMenuCtrl', [
    '$scope', '$location', '$token', 'MENUS', '$state', 'Idle', 'Keepalive', '$modal', '$http', 'ADDRESS_PING', '$log'
    ($scope, $location, $token, MENUS, $state, Idle, Keepalive, $modal, $http, ADDRESS_PING, $log) ->

        $scope.menus = MENUS

        checkActiveHref = (path, href) ->
            if path? and href?
                return ('#' + path) is (href)
            return false

        $scope.isMenuActive = (menu) ->
            if menu?
                if menu.children?
                    return true for child in menu.children when checkActiveHref($location.path(), child.href)
                else
                    return checkActiveHref($location.path(), menu.href)

            return false

        $scope.checkMenuByRole = (menu) ->
            roleCode = if $token? then $token.getRoleCode() else null
            if menu? and menu.roles? and _.indexOf(menu.roles, roleCode) > -1 then true else false

        $scope.isSpecificPage = ->
            path = $location.path()
            return _.contains( [
                '/404'
                '/500'
                '/550'
            ], path )

        $scope.main =
            brand: 'DMS PLUS'
            name: -> if $token.getUserInfo()? then $token.getUserInfo().fullname else ''

        $scope.logout = $token.logout

        $scope.changePassword = () ->
            $state.go('change-password')

        ################ Idle events ##############
        $scope.$on('IdleStart', () ->
            closeIdleWarningModals()
            $scope.warning = $modal.open({
                templateUrl: 'views/system/logout-warning-dialog.html'
                controller: 'TimeoutWarnCtrl'
                windowClass: 'modal-danger'
            })
        )

        $scope.$on('IdleEnd', () ->
            closeIdleWarningModals()
        )

        $scope.$on('IdleTimeout', () ->
            closeIdleWarningModals()
            $scope.timedout = $modal.open({
                templateUrl: 'views/system/logged-out-dialog.html'
                windowClass: 'modal-danger'
            })
            $token.logout()
        )

        $scope.$on('Keepalive', () ->
            iFrame = document.createElement('iframe')
            iFrame.onload = () ->
                iFrame.parentNode.removeChild iFrame

            iFrame.setAttribute 'src', ADDRESS_PING
            iFrame.setAttribute 'id', 'keep_alive_frame'
            iFrame.style.display = 'none'
            document.body.appendChild iFrame
        )

        closeIdleWarningModals = () ->
            if $scope.warning
                $scope.warning.close()
                $scope.warning = null
            if $scope.timedout
                $scope.timedout.close()
                $scope.timedout = null
            $scope.$apply()


        $scope.getUserLanguage = -> $token.getUserLanguage()
])

.controller('TimeoutWarnCtrl', ['$scope', 'NG_IDLE_TIMEOUT', ($scope, NG_IDLE_TIMEOUT) ->

    $scope.max = NG_IDLE_TIMEOUT
    $scope.countdown = NG_IDLE_TIMEOUT

    $scope.$on('IdleWarn', (e, countdown) ->
        $scope.countdown = countdown
        $scope.$apply()
    )

    $scope.$on('IdleTimeout', () ->
        $scope.countdown = 0
        $scope.$apply()
    )

])
