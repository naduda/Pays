///<reference path="../services/httpService.ts" />
///<reference path="../services/translateService.ts" />
///<reference path="../services/DataService.ts" />
'use strict';
var monitor;
(function (monitor) {
    var directives;
    (function (directives) {
        var LangController = (function () {
            function LangController($scope, $location, httpService, translate, dataService, $timeout) {
                this.translate = translate;
                this.dataService = dataService;
                httpService.getLangs(function (response) {
                    var locales = [], index = 1;
                    response.forEach(function (localeName) {
                        var locale = {};
                        localeName = localeName.slice(localeName.indexOf('_') + 1, localeName.indexOf('.'));
                        locale.id = localeName;
                        locales.push(locale);
                        $scope.lang = locales.filter(function (f) {
                            return f.id === dataService.language();
                        })[0];
                        translate.translateValueByKey(localeName, ['kFlagLocale', 'kLangName'], function (value, k) {
                            if (value.indexOf('http') != -1) {
                                locale.img = value;
                            }
                            else {
                                locale.langName = value;
                                if ((index++) == response.length) {
                                    $scope.locales = locales;
                                    translate.translateAllByLocale(dataService.language());
                                    $timeout();
                                }
                            }
                        });
                    });
                }, function (data, dd) { console.log(data); });
                $scope.changeLanguage = function (id) {
                    translate.translateAllByLocale(id);
                    $scope.lang = $scope.locales.filter(function (f) {
                        return f.id === id;
                    })[0];
                    dataService.setLanguage(id, true);
                };
            }
            return LangController;
        }());
        function LangDirective() {
            return {
                restrict: 'E',
                templateUrl: 'html/directives/langDirective.html',
                controller: LangController,
                link: function (scope, elm, attrs, ctrl) {
                    scope.$on('$routeChangeStart', function (next, current) {
                        scope.changeLanguage(ctrl.dataService.language());
                    });
                }
            };
        }
        directives.LangDirective = LangDirective;
    })(directives = monitor.directives || (monitor.directives = {}));
})(monitor || (monitor = {}));
