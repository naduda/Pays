var heatSupply = Object.create(null);
heatSupply.headerControllers = angular.module('headerControllers', [
	'ngRoute',
	'headerFactory']);

heatSupply.headerControllers.config(function ($routeProvider){
	$routeProvider.
		when('/profile', {
			templateUrl: function(){
				return 'html/templates/profileTemplate.html';
			},
			controller: 'profileController'
		})
})
.run(function ($rootScope, $location, hsFactory){
	$rootScope.$on("$routeChangeStart", function (event, next, current){
		if($location.path() === '/profile'){
			hsFactory.getUserProfile(function(data){
				if(data.isLogin === 'false'){
					location.href = hsFactory.url;
				}
			});
		}
	});
});

heatSupply.headerControllers.controller('headerController', 
	function ($scope, translate, hsFactory){
		hsFactory.getUserProfile(function(){
			checkIsLogin();
		});
		translate.langFiles(function(files){
			var locales = [], index = 1;
			files.forEach(function(file){
				var locale = Object.create(null);
				locale.id = file;
				locales.push(locale);
				translate.run(function(t){
					t.translateValueByKey(file, ['kFlagLocale','kLangName'],
						function(value){
							if(value.indexOf('http') != -1)
								locale.img = value;
							else{
								locale.langName = value;
								if((index++) == files.length){
									$scope.$apply();
									changeLocale(hsFactory.language);
								}
							}
						});
				});
			});
			$scope.locales = locales;
		});

		$scope.click = function($event){
			var btn = document.getElementById('curLangButton'),
					el = $event.target;

			if(el.tagName.toLowerCase() !== 'li') el = el.parentNode;
			changeLocale(el.id);
		}

		function changeLocale(langId){
			var btn = document.getElementById('curLangButton'),
					lis, li, img, span;

			if(btn){
				lis = btn.parentNode.getElementsByTagName('ul')[0]
									.getElementsByTagName('li');

				li = Array.prototype.filter.call(lis, function(li){
					return li.id === langId;
				})[0];
				if(!li) {
					console.log('null'); 
					return;
				}

				img = li.getElementsByTagName('img')[0];
				span = li.getElementsByTagName('span')[0];

				$scope.langId = langId;
				$scope.langImg = img.src;
				$scope.langDesc = span.innerHTML;
				// $scope.$apply();

				hsFactory.language = langId;
				translate.run(function(t){
					t.translateAllByLocaleName(langId);
					localStorage.setItem('heatSupply', JSON.stringify(hsFactory));
				});
			}
		}

		function checkIsLogin(){
			var isLogin = hsFactory.isLogin === 'true',
					aLogin = $('#aLogin');

			if(isLogin){
				$('#currentUser').html(hsFactory.user);
				$('#currentUserIcon').removeClass('isHide');
				aLogin[0].href = 'LogoutServlet';
				aLogin[0].getElementsByTagName('span')[0].id = '${kLogout}';
				aLogin.removeClass('fa-sign-in');
				aLogin.addClass('fa-sign-out');
				$('#mainMenu').removeClass('isHide');
			}
		}
	})
	.controller('profileController', 
		function ($scope, translate, hsFactory){
			translate.run(function(t){t.translateAll();});

			hsFactory.getUserProfileInfo(function(data){
				if(data.loginBad == undefined){
					$('input[name="login"').val(data.login);
					$('input[name="user"').val(data.name);
					$('input[name="email"').val(data.email);
				}
			});

			$scope.submitProfile = function(){
				var isValid = true;
				$('form input').each(function(){
					if(!this.checkValidity()){
						$('#btnHide').click();
						isValid = false;
					}
				});
				if(isValid){
					hsFactory.updateProfile(function(data){
						document.test = data.messageId;
						if(data.messageId != 3)
							$('#error').html(data.message);
						else
							window.location.href = hsFactory.url + 'main.html';
					});
				}
			}
		});