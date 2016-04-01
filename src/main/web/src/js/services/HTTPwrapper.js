/// <reference path="../../typings/jquery/jquery.d.ts" />
/// <reference path="../../typings/angularjs/angular.d.ts" />
///<reference path="auth.ts" />
'use strict';
var monitor;
(function (monitor) {
    var services;
    (function (services) {
        var HTTPwrapper = (function () {
            function HTTPwrapper($http, authService) {
                this.$http = $http;
                this.authService = authService;
            }
            HTTPwrapper.prototype.getHTTP = function (url, config, cbSuccess, cbError) {
                var _this = this;
                this.$http.get(url, config)
                    .success(function (data, status, headers, config) {
                    cbSuccess && cbSuccess(data, status, headers, config);
                })
                    .error(function (data, status, headers, config) {
                    if (status === 401) {
                        console.log('401 in HTTPwrapper getHTTP');
                        _this.authService.clear();
                    }
                    cbError && cbError(data, status, headers, config);
                });
            };
            HTTPwrapper.prototype.postHTTP = function (url, data, config, cbSuccess, cbError) {
                var _this = this;
                this.$http.post(url, data, config)
                    .success(function (data, status, headers, config) {
                    cbSuccess && cbSuccess(data, status, headers, config);
                })
                    .error(function (data, status, headers, config) {
                    if (status === 401) {
                        console.log('401 in HTTPwrapper postHTTP');
                        _this.authService.clear();
                    }
                    cbError && cbError(data, status, headers, config);
                });
            };
            return HTTPwrapper;
        }());
        services.HTTPwrapper = HTTPwrapper;
    })(services = monitor.services || (monitor.services = {}));
})(monitor || (monitor = {}));
