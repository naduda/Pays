///<reference path="../../typings/angularjs/angular.d.ts" />
///<reference path="HTTPWrapper.ts" />
'use strict';
module monitor.services {
	interface IHTTPService{
			getLangs(success: ng.IHttpPromiseCallback<{}>,
							 error: ng.IHttpPromiseCallback<{}>): void;
			getUser(headers: any,
							success: ng.IHttpPromiseCallback<{}>,
							error?: ng.IHttpPromiseCallback<{}>): void;
			resetAttempts(success: ng.IHttpPromiseCallback<{}>,
										error: ng.IHttpPromiseCallback<{}>): void;
			isUserLock(username: string,
								success: ng.IHttpPromiseCallback<{}>,
								error: ng.IHttpPromiseCallback<{}>): void;
			logout(url: string,
						 success: ng.IHttpPromiseCallback<{}>,
						 error: ng.IHttpPromiseCallback<{}>): void;
			detectDevice(success: ng.IHttpPromiseCallback<{}>,
									 error: ng.IHttpPromiseCallback<{}>): void;
			registration(data: any,
									 success: ng.IHttpPromiseCallback<{}>,
									 error: ng.IHttpPromiseCallback<{}>): void;
			deactivate(data: any,
								 success: ng.IHttpPromiseCallback<{}>,
								 error: ng.IHttpPromiseCallback<{}>): void;
			updateProfile(data: any,
										success: ng.IHttpPromiseCallback<{}>,
										error: ng.IHttpPromiseCallback<{}>): void;
			getProfile(data: any,
								 success: ng.IHttpPromiseCallback<{}>,
								 error: ng.IHttpPromiseCallback<{}>): void;
			recover(data: any,
							success: ng.IHttpPromiseCallback<{}>,
							error: ng.IHttpPromiseCallback<{}>): void;
	}

	export class HTTPService extends HTTPwrapper implements IHTTPService {
			getLangs(success: ng.IHttpPromiseCallback<{}>,
							 error: ng.IHttpPromiseCallback<{}>): void {
					this.getHTTP('resources/langs', {
						cache: true
					}, success, error);
			}

			getUser(headers: any,
					success: ng.IHttpPromiseCallback<{}>,
					error?: ng.IHttpPromiseCallback<{}>): void {
					this.getHTTP('resources/user', headers, success, error);
			}

			resetAttempts(success: ng.IHttpPromiseCallback<{}>,
										error: ng.IHttpPromiseCallback<{}>): void {
					this.postHTTP('secureresources/resetBlockTimeout', 
							null, null, success, error);
			}

			isUserLock(username: any,
								 success: ng.IHttpPromiseCallback<{}>,
								 error: ng.IHttpPromiseCallback<{}>): void {
					this.getHTTP('resources/isUserLock', username,
							success, error);
			}

			logout(url: string,
						 success: ng.IHttpPromiseCallback<{}>,
						 error: ng.IHttpPromiseCallback<{}>): void {
					this.postHTTP(url, null, null, success, error);
			}

			detectDevice(success: ng.IHttpPromiseCallback<{}>,
									 error?: ng.IHttpPromiseCallback<{}>): void {
				this.getHTTP('resources/detect-device',
						null, success, error);
			}

			registration(data: any,
									 success: ng.IHttpPromiseCallback<{}>,
									 error?: ng.IHttpPromiseCallback<{}>): void {
				this.postHTTP('resources/registration', data, null, success, error);
			}

			deactivate(data: any,
					success: ng.IHttpPromiseCallback<{}>,
					error?: ng.IHttpPromiseCallback<{}>): void {
				this.postHTTP('secureresources/removeProfile', data, null, success, error);
			}

			updateProfile(data: any,
										success: ng.IHttpPromiseCallback<{}>,
										error?: ng.IHttpPromiseCallback<{}>): void {
				this.postHTTP('secureresources/updateProfile',
						data, null, success, error);
			}

			getProfile(data: any,
					success: ng.IHttpPromiseCallback<{}>,
					error?: ng.IHttpPromiseCallback<{}>): void {
				this.getHTTP('secureresources/profileInfo', data, success, error);
			}

			recover(data: any,
							success: ng.IHttpPromiseCallback<{}>,
							error?: ng.IHttpPromiseCallback<{}>): void {
				this.postHTTP('resources/recover', data, null, success, error);
			}
	}
}