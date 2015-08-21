heatSupply.mainModule = angular.module('mainModule', [
	'ngRoute',
	'headerControllers',
	'mainFactory',
	'mainControllers'
	]);

// heatSupply.mainModule.config(function ($routeProvider){
// 	$routeProvider.
// 		when('/menu1', {
// 			templateUrl: 'html/main/menu/12.html',
// 			controller: 'menu1Controller'
// 		}).
// 		when('/menu2', {
// 			templateUrl: 'html/main/menu/13.html'
// 		}).
// 		otherwise({
// 			redirectTo: '/'
// 		})
// })
// .run(function ($rootScope, $location, hsFactory){
// 	$rootScope.$on("$routeChangeStart", function (event, next, current){
// 		if($location.path().indexOf('/menu') != -1){
// 			hsFactory.getUserProfile(function(data){
// 				if(data.isLogin === 'false') {
// 					location.href = hsFactory.url;
// 				} else {
// 					// httpSession don't update last access time from first time
// 					hsFactory.getUserProfileInfo(null);
// 				}
// 			});
// 		}
// 	});
// });