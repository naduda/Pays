/// <reference path="../../typings/angularjs/angular-route.d.ts" />
'use strict'
module monitor.services {
	declare var unescape: any;
	import DataService = monitor.services.DataService;
	import ErrorService = monitor.services.ErrorService;

	export interface IAuth {
			authenticated: boolean;
			loginPath: string;
			registrationPath: string;
			logoutPath: string;
			homePath: string;
			path: string;
			authenticate(credentials, callback): void;
			clear(): void;
			init(homePath, loginPath, logoutPath, registrationPath): void;
	}

	export class AuthInterceptorService {
		constructor($cookies, $log) {
			var interceptor: any = {};
			interceptor.response = (rejection) => {
				if (rejection.status === 401) {
					console.log('401 in interceptor');
					// $http.post('logout', null)
					// 		.success(() => console.log('Logout succeeded'))
					// 		.error((response, status) =>
					// 				console.log(status !== 401 ?
					// 						'Logout failed' : 'Logout succeeded')
					// 		);
				}
				return rejection;
			}
			return interceptor;
		}
	}

	export class Auth {
		constructor($rootScope, $location: ng.ILocationService,
								$http: ng.IHttpService,
								errorService: ErrorService, dataService: DataService) {
			var lsItem = localStorage.getItem(dataService.localStorageName());
			var cache = lsItem ? lsItem : null;
			var curLang: string = cache ? cache.language : 'en';
			var auth: IAuth | any = {
					loginPath: '/login',
					registrationPath: '/registration',
					logoutPath: '/logout',
					homePath: '/',
					path: $location.path(),

					authenticate: (credentials, callback) => {
						var headers = credentials && credentials.username ?
							{authorization: "Basic " + 
								btoa(unescape(encodeURIComponent(credentials.username +
									':' + credentials.password)))
							} : {};
						var config: any = {headers: headers};
						var getUserURL = 'resources/user';
						$http.get(getUserURL, config)
							.success((response: any, status: any) => {
								auth.authenticated = response.name ? true : false;
								callback && callback(auth.authenticated);
								if (auth.authenticated)
									$location.path(auth.path == auth.loginPath ?
										auth.homePath : auth.path);
								else $location.path(auth.loginPath);
							})
							.error((response: any, status: any) => {
								auth.authenticated = false;
								if (status === 401 && credentials.username) {
									var userLockURL = 'resources/isUserLock/';
									$http.get(userLockURL + credentials.username)
										.success((response: any) => {
											response.result && (() => {
												errorService.setError(response.message, response.wait);
											})();
										})
										.error((response) => console.log(response));
								}
								callback && callback();
							});
					},

					clear: () => {
						$location.path(auth.loginPath);
						auth.authenticated = false;
						$http.post(auth.logoutPath, null)
								.success(() => console.log('Logout succeeded'))
								.error((response, status) => 
										console.log(status !== 401 ?
										'Logout failed' : 'Logout succeeded')
								);
					},

					init: (homePath, loginPath, logoutPath, registrationPath) => {
						auth.homePath = homePath;
						auth.loginPath = loginPath;
						auth.logoutPath = logoutPath;
						auth.registrationPath = registrationPath;

						auth.authenticate({}, (authenticated) => {
							if (authenticated) {
								$location.path(auth.path);
							}
						});

						$rootScope.$on('$routeChangeStart', () => enter());
					}
			}

			var enter = () => {
					if ($location.path() != auth.loginPath &&
							// $location.path() != auth.homePath &&
							$location.path().indexOf(auth.registrationPath) < 0 &&
							$location.path().indexOf('unsafe') < 0) {
							auth.path = $location.path();

							if (!auth.authenticated) {
									$location.path(auth.loginPath);
							}
					}
			}

			return auth;
		}
	}
}