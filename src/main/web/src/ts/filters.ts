///<reference path="../typings/angularjs/angular.d.ts" />
'use strict'
module monitor.filters {
	export class CustomFilters {
		constructor(private app: any) { }

		setFilters(): void {
			this.app
			.filter('profilechan', () => {
				return (input) => {
					switch(input){
						case 1: return 'A+';
						case 2: return 'A-';
						case 3: return 'R+';
						case 4: return 'R-';
						default: return 'No chan => ' + input;
					}
				}
			});
		}
	}
}