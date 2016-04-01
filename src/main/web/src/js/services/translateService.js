///<reference path="../../typings/messageResources/messageResource.ts" />
'use strict';
var monitor;
(function (monitor) {
    var services;
    (function (services) {
        var Inner = (function () {
            function Inner() {
            }
            Inner.translateAll = function (locale) {
                var all = document.querySelectorAll('[pr-lang]');
                Array.prototype.forEach.call(all, function (el) {
                    var key = el.getAttribute('pr-lang');
                    el.innerHTML = key.length > 0 ?
                        messageResource.get(key, locale) : '';
                });
            };
            Inner.translateValue = function (locale, key, cb) {
                if (typeof key === 'string')
                    cb(messageResource.get(key, locale));
                else {
                    key.forEach(function (k) {
                        cb(messageResource.get(k, locale), k);
                    });
                }
            };
            return Inner;
        }());
        var TranslateService = (function () {
            function TranslateService() {
                messageResource.init({ filePath: 'lang/' });
            }
            TranslateService.prototype.translateAllByLocale = function (locale) {
                locale = 'Language_' + locale;
                if (TranslateService.filesLocale.indexOf(locale) < 0) {
                    messageResource.load(locale, function () {
                        Inner.translateAll(locale);
                        if (TranslateService.filesLocale.indexOf(locale) < 0)
                            TranslateService.filesLocale += locale + ';';
                    });
                }
                else {
                    Inner.translateAll(locale);
                }
            };
            TranslateService.prototype.translateValueByKey = function (locale, key, cb) {
                locale = 'Language_' + locale;
                if (TranslateService.filesLocale.indexOf(locale) < 0) {
                    messageResource.load(locale, function () {
                        Inner.translateValue(locale, key, cb);
                        if (TranslateService.filesLocale.indexOf(locale) < 0)
                            TranslateService.filesLocale += locale + ';';
                    });
                }
                else {
                    Inner.translateValue(locale, key, cb);
                }
            };
            TranslateService.filesLocale = '';
            return TranslateService;
        }());
        services.TranslateService = TranslateService;
    })(services = monitor.services || (monitor.services = {}));
})(monitor || (monitor = {}));
