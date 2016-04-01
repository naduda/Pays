///<reference path="../services/ErrorService.ts" />
///<reference path="../services/httpService.ts" />
'use strict'
module monitor.controllers {
	import HTTPService = monitor.services.HTTPService;
	import ErrorService = monitor.services.ErrorService;
	import DataService = monitor.services.DataService;
	import TranslateService = monitor.services.TranslateService;

	export class RegistrationCtrl {
		private btnText: string;
		private required: boolean;
		private login: string;
		private email: string;
		private password: string;
		private password1: string;
		private password2: string;
		private name: string;
		private middlename: string;
		private surname: string;
		private phone: string;

		constructor(private $location: ng.ILocationService,
								private errorService: ErrorService,
								private dataService: DataService,
								translate: TranslateService,
								private httpService: HTTPService) {
			console.log('RegistrationCtrl');
			var path = $location.path();
			this.btnText = path == '/profile' ?
					'kApply' : 'kRegisterButton';
			this.required = path == '/registration';
			if(path == '/profile'){
				httpService.getProfile(null,
					(response: any) => {
						this.login = response.login;
						this.email = response.email;
						this.name = response.name;
						this.middlename = response.middlename;
						this.surname = response.surname;
						this.phone = response.phone;

						translate.translateAllByLocale(dataService.language());
					});
			}
		}

		registration = () => {
			this.errorService.setError(null);
			var psw = this.$location.path() == '/profile' ?
					this.password1 : this.password,
					psw2 = this.password2;

			psw = psw ? psw : '';
			psw2 = psw2 ? psw2 : '';
			if (psw != psw2) {
				this.errorService.setError('keyNewPasswordsWrong');
			} else {
				switch (this.$location.path()) {
					case '/registration':
							this.registerNew();
							break;
					case '/profile':
							this.update();
							break;
				}
			}
		}

		private registerNew = () => {
			console.log('registerNew')
		}

		private update = () => {
			console.log('update');
			console.log(this);
			var data = {
				email: this.email,
				phone: this.phone,
				langId: this.dataService.language(),
				password: this.password,
				password1: this.password1,
				name: this.name,
				middlename: this.middlename,
				surname: this.surname
			};
			this.httpService.updateProfile(data, (response: any) => {
				if (response.result === 'success') {
					this.$location.path('/main');
				} else {
					this.errorService.setError(response.result);
				}
			});
		}
	}
}