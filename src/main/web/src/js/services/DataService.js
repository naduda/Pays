/// <reference path="../../typings/angularjs/angular.d.ts" />
///<reference path="HTTPWrapper.ts" />
'use strict';
var monitor;
(function (monitor) {
    var services;
    (function (services) {
        var DataService = (function () {
            function DataService() {
                this._language = 'en';
                this._localStorageName = 'monitor';
                var cache = localStorage.getItem(this._localStorageName);
                if (cache) {
                    cache = JSON.parse(cache);
                    this.setLogin(cache.login);
                    this.setLanguage(cache.language);
                }
            }
            DataService.prototype.localStorageName = function () { return this._localStorageName; };
            DataService.prototype.login = function () { return this._login; };
            DataService.prototype.setLogin = function (value, isSave) {
                this._login = value;
                isSave && this.save();
            };
            DataService.prototype.language = function () { return this._language; };
            DataService.prototype.setLanguage = function (value, isSave) {
                this._language = value;
                isSave && this.save();
            };
            DataService.prototype.save = function () {
                localStorage.setItem(this._localStorageName, JSON.stringify({
                    login: this._login,
                    language: this._language
                }));
            };
            return DataService;
        }());
        services.DataService = DataService;
    })(services = monitor.services || (monitor.services = {}));
})(monitor || (monitor = {}));
