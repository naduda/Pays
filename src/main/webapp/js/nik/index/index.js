heatSupply.indexModule = angular.module('indexModule', [
	'ngRoute',
	'headerControllers',
	'indexControllers']);

heatSupply.indexModule.config(function ($routeProvider){
	$routeProvider.
		when('/', {
			templateUrl: 'html/index/mainForm.html',
			controller:'mainIndexController'
		}).
		when('/login', {
			templateUrl: 'html/login/loginForm.html'
		}).
		when('/registration', {
			templateUrl: 'html/login/loginRegistration.html'
		}).
		otherwise({
			redirectTo: '/'
		})
});

heatSupply.indexModule
	.directive('loginForm', function (translate){
		return {
			templateUrl:"./html/templates/loginTemplate.html",
			link: function(scope, elm, attrs, ctrl){
				var currentUser = $('#currentUser');
				if(currentUser) currentUser.parent().addClass('isHide');
				translate.run(function(t){t.translateAll();});
			}
		}
	})
	.directive('registrationForm', function (translate, hsFactory){
		return {
			templateUrl:"./html/templates/registrationTemplate.html",
			link: function(scope, elm, attrs, ctrl){
				var currentUser = $('#currentUser');
				if(currentUser) currentUser.parent().addClass('isHide');
				translate.run(function(t){t.translateAll();});

				scope.submit = function(){
					var isValid = true;
					$('form input').each(function(){
						if(!this.checkValidity()){
							$('#btnHide').click();
							isValid = false;
						}
					});
					if(isValid){
						hsFactory.registration(function(data){
							if(data.messageId != 3)
								$('#error').html(data.message);
							else
								window.location.href = hsFactory.url + '#login';
						});
					}
				}
			}
		}
	});