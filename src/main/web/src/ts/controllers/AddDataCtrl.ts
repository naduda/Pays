///<reference path="../services/DataService.ts" />
///<reference path="../services/httpService.ts" />
///<reference path="../services/auth.ts" />
'use strict'
module monitor.controllers {
	import DataService = monitor.services.DataService;

	export class AddDataCtrl {
		private services: any[] = [];
		private curService: any;
		private lastData: any;
		private lastValue: any;
		private currentValue: any = {
			DATE: new Date().getTime(),
			VALUE: 0
		};
		private dataPeriod: any[];

		constructor(private $http: ng.IHttpService) {
			$http.get('/secureresources/services')
			.success((data) => {
				for(var k in data){
					this.services.push({
						name: k,
						id: data[k]
					});
				}
				$http.get('/secureresources/lastdata')
				.success(data => {
					this.lastData = data;
					this.curService = this.services[0];
					this.changeService(this.curService);
				});
			});
		}

		changeService(service) {
			this.curService = service;
			this.currentValue.idservice = service.id;
			for(var k in this.lastData){
				if(k == service.id){
					this.lastValue = this.lastData[k];
				}
			}
			this.getDataPeriod(service.id);
		}

		getDataPeriod(idservice){
			this.dataPeriod = [];
			this.$http.get('/secureresources/dataperiod', {
				params: {
					idservice: idservice,
					dtBeg: new Date().getTime() - 2592000000,
					dtEnd: new Date().getTime()
				}
			}).success(data => {
				for(var k in data){
					this.dataPeriod.push({
						date: k,
						value: data[k]
					});
				}
			});
		}

		addData(){
			if (isNaN(this.currentValue.VALUE)) {
				alert('It\'s not a number');
				return;
			}
			var n = +this.currentValue.VALUE;
			if (n < 0) {
				alert('Put positive number');
				return;
			}
			console.log('http')
			this.$http
				.post('/secureresources/adddata', this.currentValue)
			.success(data => {
				console.log(data);
			});
		}
	}
}