///<reference path="../typings/angularjs/angular-route.d.ts" />
'use strict'
module monitor{
	export class RouteConfig{
		constructor($routeProvider: ng.route.IRouteProvider,
								$httpProvider,
								$sceDelegateProvider) {
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
	}
}