///<reference path="../../typings/angularjs/angular.d.ts" />
'use strict';
var monitor;
(function (monitor) {
    var directives;
    (function (directives) {
        function EmbedSrc() {
            return {
                restrict: 'A',
                link: function (scope, element, attrs) {
                    var current = element;
                    scope.$watch(function () {
                        return attrs.embedSrc;
                    }, function () {
                        var clone = element
                            .clone()
                            .attr('src', attrs.embedSrc);
                        current.replaceWith(clone);
                        current = clone;
                    });
                }
            };
        }
        directives.EmbedSrc = EmbedSrc;
    })(directives = monitor.directives || (monitor.directives = {}));
})(monitor || (monitor = {}));
