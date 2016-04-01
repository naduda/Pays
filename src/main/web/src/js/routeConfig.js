///<reference path="../typings/angularjs/angular-route.d.ts" />
'use strict';
var monitor;
(function (monitor) {
    var RouteConfig = (function () {
        function RouteConfig($routeProvider, $httpProvider, $sceDelegateProvider) {
            $routeProvider
                .when('/', {
                templateUrl: 'main.html'
            })
                .when('/login', {
                templateUrl: 'html/login.html'
            })
                .when('/registration', {
                templateUrl: 'html/registration.html'
            })
                .when('/changeTarif', {
                templateUrl: 'html/changeTarif.html',
            })
                .when('/adddata', {
                templateUrl: 'html/addData.html',
            })
                .otherwise('/main');
            $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
            // $httpProvider.defaults.withCredentials = true;
            $httpProvider.interceptors.push('authInterceptorService');
        }
        return RouteConfig;
    }());
    monitor.RouteConfig = RouteConfig;
})(monitor || (monitor = {}));
