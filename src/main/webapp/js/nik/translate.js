return {
	Translator: function(){
		var returnInstance = Object.create(null), filesLocale = '';

		messageResource.init({filePath : 'lang/'});

		function translateARMByLocale(locale){
			var translateAll = function(){
				var all = document.body.getElementsByTagName("span");
				for (var i = 0; i < all.length; i++){
					var el = all[i],
							begInd = el.id.indexOf("${");
					if (begInd > -1) {
						var key = el.id.substring(begInd + 2);
						key = key.substring(0, key.indexOf('}'));
						if (el.hasAttribute("title")) {
							el.title = messageResource.get(key, locale);
						} else {
							el.innerHTML = messageResource.get(key, locale);
						}
					}
				}
			}

			if(filesLocale.indexOf(locale) < 0){
				messageResource.load(locale, function(){
					translateAll();
				});
				filesLocale += locale + ';';
			} else {
				translateAll();
			}
		}

		returnInstance.translateValueByKey = function(locale, key, callback){
			locale = 'Language_' + locale;
			if(filesLocale.indexOf(locale) < 0){
				messageResource.load(locale, function(){
					if(typeof key === 'string')
						callback(messageResource.get(key, locale));
					else {
						key.forEach(function(k){
							callback(messageResource.get(k, locale));
						});
					}
				});
				filesLocale += locale + ';';
			} else {
				if(typeof key === 'string')
					callback(messageResource.get(key, locale));
				else {
					key.forEach(function(k){
						callback(messageResource.get(k, locale));
					});
				}
			}
		}

		returnInstance.translateAll = function(){
			var btn = document.getElementById('curLangButton');
			var lang = btn ? btn.getAttribute('lang') : null;
			if(!lang) return;
			translateARMByLocale('Language_' + lang);
		}

		returnInstance.translateAllByLocaleName = function(localeName){
			translateARMByLocale('Language_' + localeName);
		}

		return returnInstance;
	}
}