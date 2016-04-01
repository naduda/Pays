/// <reference path="../../typings/jquery/jquery.d.ts" />
/// <reference path="../../typings/angularjs/angular.d.ts" />
///<reference path="auth.ts" />
'use strict';
module monitor.services {
		interface IHTTPwrapper {
				getHTTP(url: string,
						config: ng.IRequestShortcutConfig,
						cbSuccess: ng.IHttpPromiseCallback<{}>,
						cbError: ng.IHttpPromiseCallback<{}>): void;
				postHTTP(url: string,
						data: any,
						config: ng.IRequestShortcutConfig,
						cbSuccess: ng.IHttpPromiseCallback<{}>,
						cbError: ng.IHttpPromiseCallback<{}>): void;
		}

		export class HTTPwrapper implements IHTTPwrapper {
				constructor(private $http: ng.IHttpService,
						private authService: monitor.services.IAuth) { }

				getHTTP(url: string,
						config: ng.IRequestShortcutConfig,
						cbSuccess: ng.IHttpPromiseCallback<{}>,
						cbError?: ng.IHttpPromiseCallback<{}>): void {
						this.$http.get(url, config)
								.success((data, status, headers, config) => {
										cbSuccess && cbSuccess(data, status, headers, config);
								})
								.error((data, status, headers, config) => {
									if(status === 401){
										console.log('401 in HTTPwrapper getHTTP')
										this.authService.clear();
									}
									cbError && cbError(data, status, headers, config);
								});
				}

				postHTTP(url: string,
						data: any,
						config: ng.IRequestShortcutConfig,
						cbSuccess: ng.IHttpPromiseCallback<{}>,
						cbError?: ng.IHttpPromiseCallback<{}>): void {
						this.$http.post(url, data, config)
								.success((data, status, headers, config) => {
										cbSuccess && cbSuccess(data, status, headers, config);
								})
								.error((data, status, headers, config) => {
										if (status === 401) {
												console.log('401 in HTTPwrapper postHTTP')
												this.authService.clear();
										}
										cbError && cbError(data, status, headers, config);
								});
				}
		}
}