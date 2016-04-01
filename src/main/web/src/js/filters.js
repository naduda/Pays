///<reference path="../typings/angularjs/angular.d.ts" />
'use strict';
var monitor;
(function (monitor) {
    var filters;
    (function (filters) {
        var CustomFilters = (function () {
            function CustomFilters(app) {
                this.app = app;
            }
            CustomFilters.prototype.setFilters = function () {
                this.app
                    .filter('profilechan', function () {
                    return function (input) {
                        switch (input) {
                            case 1: return 'A+';
                            case 2: return 'A-';
                            case 3: return 'R+';
                            case 4: return 'R-';
                            default: return 'No chan => ' + input;
                        }
                    };
                });
            };
            return CustomFilters;
        }());
        filters.CustomFilters = CustomFilters;
    })(filters = monitor.filters || (monitor.filters = {}));
})(monitor || (monitor = {}));
