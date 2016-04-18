///<reference path="../services/DataService.ts" />
///<reference path="../services/httpService.ts" />
///<reference path="../services/auth.ts" />
'use strict';
var monitor;
(function (monitor) {
    var controllers;
    (function (controllers) {
        var MainCtrl = (function () {
            function MainCtrl(dataService, $scope, $sce, $http, authService) {
                this.$sce = $sce;
                this.$http = $http;
                this.authService = authService;
                this.userName = dataService.login();
                $http.get('saferesources/profile')
                    .success(function (data) {
                    console.log(data);
                });
            }
            MainCtrl.prototype.report = function () {
                var _this = this;
                this.$http.get('secureresources/report', {
                    responseType: 'arraybuffer',
                    params: {
                        month: new Date().getMonth(),
                        year: new Date().getFullYear()
                    }
                })
                    .success(function (response) {
                    var file = new Blob([response], { type: 'application/pdf' });
                    var fileURL = URL.createObjectURL(file);
                    _this.src = _this.$sce.trustAsResourceUrl(fileURL);
                });
            };
            MainCtrl.prototype.logout = function () {
                this.authService.clear();
            };
            return MainCtrl;
        }());
        controllers.MainCtrl = MainCtrl;
    })(controllers = monitor.controllers || (monitor.controllers = {}));
})(monitor || (monitor = {}));
