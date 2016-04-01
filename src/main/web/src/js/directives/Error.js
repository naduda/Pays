///<reference path="../../typings/angularjs/angular.d.ts" />
///<reference path="../services/ErrorService.ts" />
'use strict';
var monitor;
(function (monitor) {
    var directives;
    (function (directives) {
        var ErrorController = (function () {
            function ErrorController($scope, $sce, errorService, translate, dataService) {
                var _this = this;
                _this.close = function () {
                    _this.langKey = '';
                    _this.message = '';
                    _this.show = false;
                };
                $scope.$on('errorChange', function (e, args) {
                    if (args.error) {
                        _this.show = true;
                        translate.translateValueByKey(dataService.language(), args.error, function (value) {
                            _this.message = $sce.trustAsHtml(value +
                                (args.text ? ' ' + args.text : ''));
                            _this.langKey = args.error;
                            setTimeout(function () { return $scope.$apply(); }, 100);
                        });
                    }
                    else {
                        _this.close();
                    }
                });
            }
            return ErrorController;
        }());
        function Error() {
            return {
                templateUrl: 'html/directives/errorDirective.html',
                controller: ErrorController,
                controllerAs: 'errCtrl'
            };
        }
        directives.Error = Error;
    })(directives = monitor.directives || (monitor.directives = {}));
})(monitor || (monitor = {}));
