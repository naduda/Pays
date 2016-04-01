/// <reference path="../../typings/angularjs/angular.d.ts" />
///<reference path="HTTPWrapper.ts" />
///<reference path="translateService.ts" />
'use strict';
module monitor.services {
	export class ErrorService {
		constructor(private $rootScope: ng.IRootScopeService) {}

		setError(value: string, text?: string): void {
			this.$rootScope.$broadcast('errorChange', {
					error: value,
					text: text
			});
		}
	}
}