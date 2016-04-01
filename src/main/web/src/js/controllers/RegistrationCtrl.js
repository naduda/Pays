///<reference path="../services/ErrorService.ts" />
///<reference path="../services/httpService.ts" />
'use strict';
var monitor;
(function (monitor) {
    var controllers;
    (function (controllers) {
        var RegistrationCtrl = (function () {
            function RegistrationCtrl($location, errorService, dataService, translate, httpService) {
                var _this = this;
                this.$location = $location;
                this.errorService = errorService;
                this.dataService = dataService;
                this.httpService = httpService;
                this.registration = function () {
                    _this.errorService.setError(null);
                    var psw = _this.$location.path() == '/profile' ?
                        _this.password1 : _this.password, psw2 = _this.password2;
                    psw = psw ? psw : '';
                    psw2 = psw2 ? psw2 : '';
                    if (psw != psw2) {
                        _this.errorService.setError('keyNewPasswordsWrong');
                    }
                    else {
                        switch (_this.$location.path()) {
                            case '/registration':
                                _this.registerNew();
                                break;
                            case '/profile':
                                _this.update();
                                break;
                        }
                    }
                };
                this.registerNew = function () {
                    console.log('registerNew');
                };
                this.update = function () {
                    console.log('update');
                    console.log(_this);
                    var data = {
                        email: _this.email,
                        phone: _this.phone,
                        langId: _this.dataService.language(),
                        password: _this.password,
                        password1: _this.password1,
                        name: _this.name,
                        middlename: _this.middlename,
                        surname: _this.surname
                    };
                    _this.httpService.updateProfile(data, function (response) {
                        if (response.result === 'success') {
                            _this.$location.path('/main');
                        }
                        else {
                            _this.errorService.setError(response.result);
                        }
                    });
                };
                console.log('RegistrationCtrl');
                var path = $location.path();
                this.btnText = path == '/profile' ?
                    'kApply' : 'kRegisterButton';
                this.required = path == '/registration';
                if (path == '/profile') {
                    httpService.getProfile(null, function (response) {
                        _this.login = response.login;
                        _this.email = response.email;
                        _this.name = response.name;
                        _this.middlename = response.middlename;
                        _this.surname = response.surname;
                        _this.phone = response.phone;
                        translate.translateAllByLocale(dataService.language());
                    });
                }
            }
            return RegistrationCtrl;
        }());
        controllers.RegistrationCtrl = RegistrationCtrl;
    })(controllers = monitor.controllers || (monitor.controllers = {}));
})(monitor || (monitor = {}));
