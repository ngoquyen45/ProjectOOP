'use strict'

angular.module('app.directives', [])

#.directive('imgHolder', [ ->
#        return {
#        restrict: 'A'
#        link: (scope, ele) ->
#            Holder.run(
#                images: ele[0]
#            )
#        }
#    ])

# add background and some style just for specific page
.directive('customBackground', () ->
    return {
    restrict: "A"
    controller: [
        '$scope', '$element', '$location'
        ($scope, $element, $location) ->
            path = ->
                return $location.path()

            addBg = (path) ->
                # remove all the classes
                $element.removeClass('body-home body-special body-tasks body-lock')

                # add certain class based on path
                switch path
                    when '/' then $element.addClass('body-home')
                    when '/404', '/500', '/550', '/pages/signin', '/pages/signup', '/pages/forgot' then $element.addClass('body-special')
                    when '/pages/lock-screen' then $element.addClass('body-special body-lock')
                    when '/tasks' then $element.addClass('body-tasks')

            addBg( $location.path() )

            $scope.$watch(path, (newVal, oldVal) ->
                if newVal is oldVal
                    return
                addBg($location.path())
            )
    ]
    }
)

# switch stylesheet file
.directive('uiColorSwitch', [ ->
        return {
        restrict: 'A'
        link: (scope, ele) ->
            ele.find('.color-option').on('click', (event)->
                $this = $(this)
                hrefUrl = undefined

                style = $this.data('style')
                if style is 'loulou'
                    hrefUrl = 'styles/main.css'
                    $('link[href^="styles/main"]').attr('href',hrefUrl)
                else if style
                    style = '-' + style
                    hrefUrl = 'styles/main' + style + '.css'
                    $('link[href^="styles/main"]').attr('href',hrefUrl)
                else
                    return false

                event.preventDefault()
            )
        }
    ])

# for mini style NAV
.directive('toggleMinNav', [
        '$rootScope'
        ($rootScope) ->
            return {
            restrict: 'A'
            link: (scope, ele) ->
                app = $('#app')
                $window = $(window)

                ele.on('click', (e) ->
                    if app.hasClass('nav-min')
                        app.removeClass('nav-min')
                    else
                        app.addClass('nav-min')
                        $rootScope.$broadcast('minNav:enabled')

                    e.preventDefault()
                )

                updateClass = ->
                    width = $window.width()
                    if width < 768
                        app.removeClass('nav-min')
                    else if width < 992
                        if !app.hasClass('nav-min')
                            app.addClass('nav-min')
                            $rootScope.$broadcast('minNav:enabled')

                $window.resize( () ->
                    clearTimeout(t)
                    t = setTimeout(updateClass, 300)
                )
            }
    ])
# for accordion/collapse style NAV
.directive('collapseNav', [ ->
        return {
        restrict: 'A'
        link: (scope, ele) ->
            # reset collapse NAV, sub Ul should slideUp
            scope.$on('minNav:enabled', ->
                ele.find('ul').parent('li').removeClass('open').find('ul').slideUp()
            )

        }
    ])
# MENU NAV
.directive('navMenu', [ ->
        return {
        restrict: 'A'
        scope: { navMenu: '=' }
        link: (scope, ele) ->
            $app = $('#app')
            $ele = $(ele)

            $a = $ele.children('a')

            $ulParent = $ele.parent('ul')

            if scope.navMenu? and scope.navMenu.children? and scope.navMenu.children.length > 0
                # has children
                $a.on('click', (event) ->

                    # disable click event when Nav is in mini style
                    if ( $app.hasClass('nav-min') ) then return false

                    $this = $(this)
                    $parent = $this.parent('li')
                    $ulParent.find('ul').parent('li').not( $parent ).removeClass('open').find('ul').slideUp()
                    $parent.toggleClass('open').find('ul').stop().slideToggle()

                    event.preventDefault()

                )
            else
                $a.on('click', ->
                    $ulParent.find('ul').parent('li').removeClass('open').find('ul').slideUp()
                )
        }
    ])
# toggle on-canvas for small screen, with CSS
.directive('toggleOffCanvas', [ ->
        return {
        restrict: 'A'
        link: (scope, ele) ->
            ele.on('click', ->
                $('#app').toggleClass('on-canvas')
            )
        }
    ])
.directive('slimScroll', [ ->
        return {
        restrict: 'A'
        link: (scope, ele, attrs) ->
            ele.slimScroll({
                height: attrs.scrollHeight || '100%'
            })
        }
    ])

# history back button
.directive('goBack', [ ->
        return {
        restrict: "A"
        controller: [
            '$scope', '$element', '$window'
            ($scope, $element, $window) ->
                $element.on('click', ->
                    $window.history.back()
                )
        ]
        }
    ])

