///<reference path="../services/DataService.ts" />
///<reference path="../services/httpService.ts" />
///<reference path="../services/auth.ts" />
'use strict'
module monitor.controllers {
	import DataService = monitor.services.DataService;

	export class ChangeTarifCtrl {
		private services: any[] = [];
		private curService: any;
		private tarifs: any[];

		constructor(private $http: ng.IHttpService) {
			$http.get('/secureresources/services')
			.success((data) => {
				for(var k in data){
					this.services.push({
						name: k,
						id: data[k]
					});
				}
				this.curService = this.services[0];
				this.changeService(this.curService);
			});
		}

		changeService(service) {
			this.curService = service;
			this.tarifs = [];
			this.$http.get('/secureresources/tarifs')
			.success((data) => {
				for(var k in data) {
					var idService = k.slice(0, k.indexOf('_'));
					if(idService == service.id){
						this.tarifs.push(data[k]);
					}
				}
			});
		}

		save(){
			this.$http
			.post('/secureresources/changetarifs',this.tarifs)
			.success(data => {
				console.log(data);
			});
		}
	}
}