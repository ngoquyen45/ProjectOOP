'use strict'

angular.module('app.state', [])

.constant('CATEGORY_LIST_TEMPLATE', 'views/common/category-list.html')
.constant('CATEGORY_POPUP_TEMPLATE', 'views/common/category-popup.html')
.constant('CATEGORY_POPUP_CTRL', 'NameCategoryPopupCtrl')

.controller('HomeCtrl', [
        '$scope', '$state' ,'$token', 'HOME_STATE'
        ($scope, $state, $token, HOME_STATE) ->
            if $token?
                roleCode = $token.getRoleCode()
                if roleCode?
                    home = HOME_STATE[roleCode]
                    if home?
                        $state.go(home)
                    else
                        $state.go('550')
                else
                    console.log('roleCode is null')
            else
                console.log('$token is null')
])

.config [
    '$stateProvider', '$urlRouterProvider', 'STATES', 'CATEGORY_LIST_TEMPLATE'
    ($stateProvider, $urlRouterProvider, STATES, CATEGORY_LIST_TEMPLATE) ->
        $urlRouterProvider.when '', '/home'
        $urlRouterProvider.otherwise(($injector, $location) ->
            $state = $injector.get('$state')
            if $location.url() is '/' then $state.go('home') else $state.go('404')
        )

        $stateProvider.state('home', { url: '/home', controller: 'HomeCtrl' })
        $stateProvider.state('callback', { url: '/callback' })
        $stateProvider.state '404', { url: '/404', templateUrl: 'views/pages/404.html' }
        $stateProvider.state '500', { url: '/500', templateUrl: 'views/pages/500.html' }
        $stateProvider.state '550', { url: '/550', templateUrl: 'views/pages/550.html' }

        getTemplateUrl = (url) ->
            switch url
                when 'CATEGORY_LIST_TEMPLATE' then CATEGORY_LIST_TEMPLATE
                else url

        addState = (state) ->
            if state? and state.name?
                url = (if state.url? then state.url else ('/' + state.name))
                $stateProvider.state state.name, {
                    url: url + '?filter&id&parent&parentparams'
                    templateUrl: getTemplateUrl(state.templateUrl)
                    controller: state.controller
                }

        if STATES? and STATES.length > 0
            addState(state) for state in STATES
]

.run [
    '$rootScope', '$location', '$state', '$token', 'STATES', 'MENUS', 'HOME_STATE'
    ($rootScope, $location, $state, $token, STATES, MENUS, HOME_STATE) ->
        $rootScope.$on '$stateChangeStart:tokenVerified', (event, toState, fromState, parentEvent) ->
            console.log('$stateChangeStart:tokenVerified in state-config.js')

            roleCode = if $token? then $token.getRoleCode() else null

#                # if home state go to the first link in menu
#                for menu in MENUS
#                    if menu.roles? and _.indexOf(menu.roles, roleCode) > -1
#                        if menu.children?
#                            for child in menu.children
#                                if child.roles? and _.indexOf(child.roles, roleCode) > -1 and child.href?
#                                    event.preventDefault()
#                                    $location.url child.href.substring(1, child.href.length)
#                                    return
#                        else
#                            if menu.href?
#                                event.preventDefault()
#                                $location.url menu.href.substring(1, menu.href.length)
#                                return
#                $state.go('550')

#            if toState? and toState.name is 'home'
#                if not roleCode?
#                    console.log('roleCode is null')
#                    $state.go('500')
#                else
#                    home = HOME_STATE[roleCode]
#                    if home?
#                        $state.go(home)
#                    else
#                        $state.go('550')
#            else
            if toState? and toState.name?
                if toState.name is 'home'
                    return

                stateName = toState.name
                if stateName is '404' or stateName is '500' or stateName is '550'
                    return

                for state in STATES
                    if state.name is stateName
                        if _.indexOf(state.roles, roleCode) > -1
                            return
                        else
                            event.preventDefault()
                            parentEvent.preventDefault()
                            $state.go '404'

                event.preventDefault()
                parentEvent.preventDefault()
                $state.go '404'

        $rootScope.$on '$token:failToVerifyToken', () ->
            $state.go '500'
]
