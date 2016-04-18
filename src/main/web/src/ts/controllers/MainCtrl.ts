///<reference path="../services/DataService.ts" />
///<reference path="../services/httpService.ts" />
///<reference path="../services/auth.ts" />
'use strict'
module monitor.controllers {
	import DataService = monitor.services.DataService;
	import AuthService = monitor.services.IAuth;

	export class MainCtrl {
		private userName: string;
		private src: any;

		constructor(dataService: DataService,
								$scope,
								private $sce: ng.ISCEService,
								private $http: ng.IHttpService,
								private authService: AuthService) {
			this.userName = dataService.login();

			$http.get('saferesources/profile')
			.success((data) => {
				console.log(data);
			});
		}

		report(){
			this.$http.get('secureresources/report', {
				responseType: 'arraybuffer',
				params: {
					month: new Date().getMonth(),
					year: new Date().getFullYear()
				}
			})
				.success((response) => {
					var file = new Blob([response], { type: 'application/pdf' });
					var fileURL = URL.createObjectURL(file);
					this.src = this.$sce.trustAsResourceUrl(fileURL);
				});
		}

		logout(){
			this.authService.clear();
		}
	}
}