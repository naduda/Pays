///<reference path="../../typings/angularjs/angular.d.ts" />
'use strict'
module monitor.directives {
		import TranslateService = monitor.services.TranslateService;

		export function Enter(): ng.IDirective {
			return (scope, element, attrs) => {
				element.bind('keydown keypress', (event) => {
					if (event.which === 13) {
						scope.$apply(() => {
							scope.$eval(attrs.ngEnter);
						});
						event.preventDefault();
					}
				});
			}
		}
}