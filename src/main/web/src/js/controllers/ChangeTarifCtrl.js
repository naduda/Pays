///<reference path="../services/DataService.ts" />
///<reference path="../services/httpService.ts" />
///<reference path="../services/auth.ts" />
'use strict';
var monitor;
(function (monitor) {
    var controllers;
    (function (controllers) {
        var ChangeTarifCtrl = (function () {
            function ChangeTarifCtrl($http) {
                var _this = this;
                this.$http = $http;
                this.services = [];
                $http.get('/secureresources/services')
                    .success(function (data) {
                    for (var k in data) {
                        _this.services.push({
                            name: k,
                            id: data[k]
                        });
                    }
                    _this.curService = _this.services[0];
                    _this.changeService(_this.curService);
                });
            }
            ChangeTarifCtrl.prototype.changeService = function (service) {
                var _this = this;
                this.curService = service;
                this.tarifs = [];
                this.$http.get('/secureresources/tarifs')
                    .success(function (data) {
                    for (var k in data) {
                        var idService = k.slice(0, k.indexOf('_'));
                        if (idService == service.id) {
                            _this.tarifs.push(data[k]);
                        }
                    }
                });
            };
            ChangeTarifCtrl.prototype.save = function () {
                this.$http
                    .post('/secureresources/changetarifs', this.tarifs)
                    .success(function (data) {
                    console.log(data);
                });
            };
            return ChangeTarifCtrl;
        }());
        controllers.ChangeTarifCtrl = ChangeTarifCtrl;
    })(controllers = monitor.controllers || (monitor.controllers = {}));
})(monitor || (monitor = {}));
