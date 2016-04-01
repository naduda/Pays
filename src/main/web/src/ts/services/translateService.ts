///<reference path="../../typings/messageResources/messageResource.ts" />
'use strict';
module monitor.services {
	class Inner{
			static translateAll(locale: string): void {
					var all = document.querySelectorAll('[pr-lang]');
					Array.prototype.forEach.call(all, function(el) {
							var key = el.getAttribute('pr-lang');
							el.innerHTML = key.length > 0 ?
									messageResource.get(key, locale) : '';
					});
			}

			static translateValue(locale: string, key: any, cb: any): void {
					if (typeof key === 'string')
							cb(messageResource.get(key, locale));
					else {
							key.forEach(function(k) {
									cb(messageResource.get(k, locale), k);
							});
					}
			}
	}

	export class TranslateService {
			static filesLocale: string = '';
			constructor(){
				messageResource.init({filePath: 'lang/'});
			}

			translateAllByLocale(locale:string):void {
				locale = 'Language_' + locale;
				if (TranslateService.filesLocale.indexOf(locale) < 0) {
					messageResource.load(locale, function() {
						Inner.translateAll(locale);
						if (TranslateService.filesLocale.indexOf(locale) < 0)
								TranslateService.filesLocale += locale + ';';
					});
				} else {
					Inner.translateAll(locale);
				}
			}

			translateValueByKey(locale:string, key:any, cb:any):void {
				locale = 'Language_' + locale;
				if (TranslateService.filesLocale.indexOf(locale) < 0) {
						messageResource.load(locale, function() {
							Inner.translateValue(locale, key, cb);
							if (TranslateService.filesLocale.indexOf(locale) < 0)
									TranslateService.filesLocale += locale + ';';
						});
				} else {
					Inner.translateValue(locale, key, cb);
				}
			}
	}
}