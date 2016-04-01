///<reference path="../services/errorService.ts" />
///<reference path="../services/translateService.ts" />
///<reference path="../services/DataService.ts" />
'use strict';
var monitor;
(function (monitor) {
    var directives;
    (function (directives) {
        var LoginController = (function () {
            function LoginController($scope, errorService, authService, dataService, translate) {
                this.dataService = dataService;
                this.translate = translate;
                $scope.credentials = {};
                // $scope.credentials.password = 'qwe';
                $scope.progressIconClass = '';
                $scope.login = function () {
                    $scope.progressIconClass = 'fa fa-refresh fa-spin';
                    authService.authenticate($scope.credentials, function (authenticated) {
                        if (authenticated) {
                            dataService.setLogin($scope.credentials.username, true);
                            console.log("Login succeeded");
                        }
                        else {
                            console.log("Login failed");
                            $scope.progressIconClass = '';
                            errorService.setError('keyBadAuthentication');
                        }
                    });
                };
            }
            return LoginController;
        }());
        function Login() {
            return {
                restrict: 'E',
                templateUrl: 'html/directives/loginDirective.html',
                controller: LoginController,
                link: function (scope, elm, attrs, ctrl) {
                    scope.credentials.username = ctrl.dataService.login();
                    $('input[type="password"]')[0].focus();
                    ctrl.translate.translateAllByLocale(ctrl.dataService.language());
                }
            };
        }
        directives.Login = Login;
    })(directives = monitor.directives || (monitor.directives = {}));
})(monitor || (monitor = {}));
