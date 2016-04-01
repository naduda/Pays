///<reference path="../../typings/angularjs/angular.d.ts" />
///<reference path="../services/ErrorService.ts" />
'use strict';
module monitor.directives {
	import ErrorService = monitor.services.ErrorService;
	import TranslateService = monitor.services.TranslateService;
	import DataService = monitor.services.DataService;

	interface IError {
			error: string;
			text: string;
	}

	class ErrorController {
			constructor($scope: any, $sce: ng.ISCEService,
									errorService: ErrorService,
									translate: TranslateService,
									dataService: DataService) {
				var _this: any = this;

				_this.close = () => {
						_this.langKey = '';
						_this.message = '';
						_this.show = false;
				};

				$scope.$on('errorChange', (e, args: IError) => {
					if (args.error) {
							_this.show = true;
							translate.translateValueByKey(
								dataService.language(), args.error, (value) => {
										_this.message = $sce.trustAsHtml(value +
												(args.text ? ' ' + args.text : ''));
										_this.langKey = args.error;
										setTimeout(() => $scope.$apply(), 100);
								});
					} else {
						_this.close();
					}
				});
			}
	}

	export function Error(): ng.IDirective {
			return {
					templateUrl: 'html/directives/errorDirective.html',
					controller: ErrorController,
					controllerAs: 'errCtrl'
			}
	}
}