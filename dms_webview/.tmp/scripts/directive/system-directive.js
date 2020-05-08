(function() {
  'use strict';
  angular.module('app.directives', []).directive('customBackground', function() {
    return {
      restrict: "A",
      controller: [
        '$scope', '$element', '$location', function($scope, $element, $location) {
          var addBg, path;
          path = function() {
            return $location.path();
          };
          addBg = function(path) {
            $element.removeClass('body-home body-special body-tasks body-lock');
            switch (path) {
              case '/':
                return $element.addClass('body-home');
              case '/404':
              case '/500':
              case '/550':
              case '/pages/signin':
              case '/pages/signup':
              case '/pages/forgot':
                return $element.addClass('body-special');
              case '/pages/lock-screen':
                return $element.addClass('body-special body-lock');
              case '/tasks':
                return $element.addClass('body-tasks');
            }
          };
          addBg($location.path());
          return $scope.$watch(path, function(newVal, oldVal) {
            if (newVal === oldVal) {
              return;
            }
            return addBg($location.path());
          });
        }
      ]
    };
  }).directive('uiColorSwitch', [
    function() {
      return {
        restrict: 'A',
        link: function(scope, ele) {
          return ele.find('.color-option').on('click', function(event) {
            var $this, hrefUrl, style;
            $this = $(this);
            hrefUrl = void 0;
            style = $this.data('style');
            if (style === 'loulou') {
              hrefUrl = 'styles/main.css';
              $('link[href^="styles/main"]').attr('href', hrefUrl);
            } else if (style) {
              style = '-' + style;
              hrefUrl = 'styles/main' + style + '.css';
              $('link[href^="styles/main"]').attr('href', hrefUrl);
            } else {
              return false;
            }
            return event.preventDefault();
          });
        }
      };
    }
  ]).directive('toggleMinNav', [
    '$rootScope', function($rootScope) {
      return {
        restrict: 'A',
        link: function(scope, ele) {
          var $window, app, updateClass;
          app = $('#app');
          $window = $(window);
          ele.on('click', function(e) {
            if (app.hasClass('nav-min')) {
              app.removeClass('nav-min');
            } else {
              app.addClass('nav-min');
              $rootScope.$broadcast('minNav:enabled');
            }
            return e.preventDefault();
          });
          updateClass = function() {
            var width;
            width = $window.width();
            if (width < 768) {
              return app.removeClass('nav-min');
            } else if (width < 992) {
              if (!app.hasClass('nav-min')) {
                app.addClass('nav-min');
                return $rootScope.$broadcast('minNav:enabled');
              }
            }
          };
          return $window.resize(function() {
            var t;
            clearTimeout(t);
            return t = setTimeout(updateClass, 300);
          });
        }
      };
    }
  ]).directive('collapseNav', [
    function() {
      return {
        restrict: 'A',
        link: function(scope, ele) {
          return scope.$on('minNav:enabled', function() {
            return ele.find('ul').parent('li').removeClass('open').find('ul').slideUp();
          });
        }
      };
    }
  ]).directive('navMenu', [
    function() {
      return {
        restrict: 'A',
        scope: {
          navMenu: '='
        },
        link: function(scope, ele) {
          var $a, $app, $ele, $ulParent;
          $app = $('#app');
          $ele = $(ele);
          $a = $ele.children('a');
          $ulParent = $ele.parent('ul');
          if ((scope.navMenu != null) && (scope.navMenu.children != null) && scope.navMenu.children.length > 0) {
            return $a.on('click', function(event) {
              var $parent, $this;
              if ($app.hasClass('nav-min')) {
                return false;
              }
              $this = $(this);
              $parent = $this.parent('li');
              $ulParent.find('ul').parent('li').not($parent).removeClass('open').find('ul').slideUp();
              $parent.toggleClass('open').find('ul').stop().slideToggle();
              return event.preventDefault();
            });
          } else {
            return $a.on('click', function() {
              return $ulParent.find('ul').parent('li').removeClass('open').find('ul').slideUp();
            });
          }
        }
      };
    }
  ]).directive('toggleOffCanvas', [
    function() {
      return {
        restrict: 'A',
        link: function(scope, ele) {
          return ele.on('click', function() {
            return $('#app').toggleClass('on-canvas');
          });
        }
      };
    }
  ]).directive('slimScroll', [
    function() {
      return {
        restrict: 'A',
        link: function(scope, ele, attrs) {
          return ele.slimScroll({
            height: attrs.scrollHeight || '100%'
          });
        }
      };
    }
  ]).directive('goBack', [
    function() {
      return {
        restrict: "A",
        controller: [
          '$scope', '$element', '$window', function($scope, $element, $window) {
            return $element.on('click', function() {
              return $window.history.back();
            });
          }
        ]
      };
    }
  ]);

}).call(this);

//# sourceMappingURL=system-directive.js.map
