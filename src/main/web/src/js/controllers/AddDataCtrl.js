///<reference path="../services/DataService.ts" />
///<reference path="../services/httpService.ts" />
///<reference path="../services/auth.ts" />
'use strict';
var monitor;
(function (monitor) {
    var controllers;
    (function (controllers) {
        var AddDataCtrl = (function () {
            function AddDataCtrl($http) {
                var _this = this;
                this.$http = $http;
                this.services = [];
                this.currentValue = {
                    DATE: new Date().getTime(),
                    VALUE: 0
                };
                $http.get('/secureresources/services')
                    .success(function (data) {
                    for (var k in data) {
                        _this.services.push({
                            name: k,
                            id: data[k]
                        });
                    }
                    $http.get('/secureresources/lastdata')
                        .success(function (data) {
                        _this.lastData = data;
                        _this.curService = _this.services[0];
                        _this.changeService(_this.curService);
                    });
                });
            }
            AddDataCtrl.prototype.changeService = function (service) {
                this.curService = service;
                this.currentValue.idservice = service.id;
                for (var k in this.lastData) {
                    if (k == service.id) {
                        this.lastValue = this.lastData[k];
                    }
                }
                this.getDataPeriod(service.id);
            };
            AddDataCtrl.prototype.getDataPeriod = function (idservice) {
                var _this = this;
                this.dataPeriod = [];
                this.$http.get('/secureresources/dataperiod', {
                    params: {
                        idservice: idservice,
                        dtBeg: new Date().getTime() - 2592000000,
                        dtEnd: new Date().getTime()
                    }
                }).success(function (data) {
                    for (var k in data) {
                        _this.dataPeriod.push({
                            date: k,
                            value: data[k]
                        });
                    }
                });
            };
            AddDataCtrl.prototype.addData = function () {
                if (isNaN(this.currentValue.VALUE)) {
                    alert('It\'s not a number');
                    return;
                }
                var n = +this.currentValue.VALUE;
                if (n < 0) {
                    alert('Put positive number');
                    return;
                }
                console.log('http');
                this.$http
                    .post('/secureresources/adddata', this.currentValue)
                    .success(function (data) {
                    console.log(data);
                });
            };
            return AddDataCtrl;
        }());
        controllers.AddDataCtrl = AddDataCtrl;
    })(controllers = monitor.controllers || (monitor.controllers = {}));
})(monitor || (monitor = {}));
