///<reference path="../../typings/angularjs/angular.d.ts" />
'use strict'
module monitor.directives {
	export function EmbedSrc(): ng.IDirective {
		return {
			restrict: 'A',
			link: (scope:ng.IScope, element, attrs) => {
				var current = element;
				scope.$watch(() => {
					return attrs.embedSrc;
				}, () => {
					var clone = element
						.clone()
						.attr('src', attrs.embedSrc);
					current.replaceWith(clone);
					current = clone;
				});
			}
		};
	}
}