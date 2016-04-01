/// <reference path="../../typings/angularjs/angular.d.ts" />
///<reference path="HTTPWrapper.ts" />
///<reference path="translateService.ts" />
'use strict';
var monitor;
(function (monitor) {
    var services;
    (function (services) {
        var ErrorService = (function () {
            function ErrorService($rootScope) {
                this.$rootScope = $rootScope;
            }
            ErrorService.prototype.setError = function (value, text) {
                this.$rootScope.$broadcast('errorChange', {
                    error: value,
                    text: text
                });
            };
            return ErrorService;
        }());
        services.ErrorService = ErrorService;
    })(services = monitor.services || (monitor.services = {}));
})(monitor || (monitor = {}));
