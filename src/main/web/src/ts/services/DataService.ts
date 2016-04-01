/// <reference path="../../typings/angularjs/angular.d.ts" />
///<reference path="HTTPWrapper.ts" />
'use strict';
module monitor.services {
	export class DataService {
		private _language: string = 'en';
		private _login: string;
		private _localStorageName:string = 'monitor';

		constructor(){
			var cache: any = localStorage.getItem(this._localStorageName);
			if (cache) {
				cache = JSON.parse(cache);
				this.setLogin(cache.login);
				this.setLanguage(cache.language);
			}
		}

		localStorageName(): string { return this._localStorageName; }

		login(): string {return this._login;}
		setLogin(value: string, isSave?: boolean): void {
			this._login = value;
			isSave && this.save();
		}
		language(): string {return this._language;}
		setLanguage(value: string, isSave?: boolean): void {
			this._language = value;
			isSave && this.save();
		}

		save(): void {
			localStorage.setItem(this._localStorageName, JSON.stringify({
				login: this._login,
				language: this._language
			}));
		}
	}
}