///<reference path="../../typings/angularjs/angular.d.ts" />
///<reference path="HTTPWrapper.ts" />
'use strict';
var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
var monitor;
(function (monitor) {
    var services;
    (function (services) {
        var HTTPService = (function (_super) {
            __extends(HTTPService, _super);
            function HTTPService() {
                _super.apply(this, arguments);
            }
            HTTPService.prototype.getLangs = function (success, error) {
                this.getHTTP('resources/langs', {
                    cache: true
                }, success, error);
            };
            HTTPService.prototype.getUser = function (headers, success, error) {
                this.getHTTP('resources/user', headers, success, error);
            };
            HTTPService.prototype.resetAttempts = function (success, error) {
                this.postHTTP('secureresources/resetBlockTimeout', null, null, success, error);
            };
            HTTPService.prototype.isUserLock = function (username, success, error) {
                this.getHTTP('resources/isUserLock', username, success, error);
            };
            HTTPService.prototype.logout = function (url, success, error) {
                this.postHTTP(url, null, null, success, error);
            };
            HTTPService.prototype.detectDevice = function (success, error) {
                this.getHTTP('resources/detect-device', null, success, error);
            };
            HTTPService.prototype.registration = function (data, success, error) {
                this.postHTTP('resources/registration', data, null, success, error);
            };
            HTTPService.prototype.deactivate = function (data, success, error) {
                this.postHTTP('secureresources/removeProfile', data, null, success, error);
            };
            HTTPService.prototype.updateProfile = function (data, success, error) {
                this.postHTTP('secureresources/updateProfile', data, null, success, error);
            };
            HTTPService.prototype.getProfile = function (data, success, error) {
                this.getHTTP('secureresources/profileInfo', data, success, error);
            };
            HTTPService.prototype.recover = function (data, success, error) {
                this.postHTTP('resources/recover', data, null, success, error);
            };
            return HTTPService;
        }(services.HTTPwrapper));
        services.HTTPService = HTTPService;
    })(services = monitor.services || (monitor.services = {}));
})(monitor || (monitor = {}));
