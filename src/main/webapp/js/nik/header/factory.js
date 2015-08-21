angular.module('headerFactory', [])
	.factory('translate', function ($http){
		function getTranslate(callback){
			$http({
				method: 'GET',
				url: '/Pays/js/nik/translate.js',
				cache: true
			})
			.success(function(data){
				var func = new Function('', data);
				try{callback(func);}catch(e){console.log(e);console.log(file);}
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		function getListOfFiles(callback){
			$http({
				method: 'GET',
				url: '/Pays/dataServer/db/filesInDir',
				cache: true
			})
			.success(function(data){
				var files = [];
				data.forEach(function(file){
					file = file.name;
					file = file.slice(file.indexOf('_') + 1, file.indexOf('.'));
					files.push(file);
				});
				callback(files);
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		return {
			langFiles: getListOfFiles,
			run: function(callback){
				getTranslate(function(func){
					var translator = func().Translator();
					callback(translator);
				});
			}
		}
	})
	.factory('hsFactory', function ($http){
		var main;

		function getUserProfile(callback){
			$http({
				method: 'GET',
				url: '/Pays/StartServlet',
				cache: false
			})
			.success(function(data){
				callback(data);
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		function getUserProfileInfo(callback){
			$http({
				method: 'POST',
				url: '/Pays/ProfileInfoServlet',
				cache: false
			})
			.success(function(data){
				if(callback != null) callback(data);
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		function registration(callback){
			$http({
				method: 'POST',
				url: '/Pays/RegisterServlet',
				params: {
					name: $('#rfI_2').val(),
					login: $('#rfI_1').val(),
					password: $('#rfI_3').val(),
					password2: $('#rfI_4').val(),
					email: $('#rfI_5').val(),
				},
				cache: false
			})
			.success(function(data){
				if(callback != null) callback(data);
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		function getData(idTarif, callback){
			$http({
				method: 'GET',
				url: '/Pays/dataServer/db/getData?' + 
							'params=' + main.userId + ';' + idTarif,
				cache: false
			})
			.success(function(data){
				if(callback) callback(data);
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		function setData(idTarif, dt, value1, value2, callback){
			$http({
				method: 'GET',
				url: '/Pays/dataServer/db/setData?' + 
							'params=' + main.userId + ';' + idTarif +
							';' + dt + ';' + value1 + ';' + value2,
				cache: false
			})
			.success(function(data){
				if(callback) callback(data);
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		function updateProfile(callback){
			$http({
				method: 'POST',
				url: '/Pays/ProfileServlet',
				params: {
					name: $('input[name="user"').val(),
					login: $('input[name="login"').val(),
					email: $('input[name="email"').val(),
					pwd: $('input[name="pwd"').val(),
					pwd1: $('input[name="pwd1"').val(),
					pwd2: $('input[name="pwd2"').val(),
				},
				cache: false
			})
			.success(function(data){
				if(callback != null) callback(data);
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		function Pays(){
			var hs = Object.create(null),
					url = document.URL,
					cache = localStorage.getItem('heatSupply');

			hs.language = cache ? JSON.parse(cache).language : 'uk';
			getUserProfile(function(data){
				hs.userId = data.userId;
				hs.user = data.user;
				hs.isLogin = data.isLogin;
			});
			hs.getUserProfile = getUserProfile;
			hs.registration = registration;
			hs.updateProfile = updateProfile;
			hs.getUserProfileInfo = getUserProfileInfo;
			hs.url = url.slice(0, url.indexOf('Pays') + 5);
			hs.setData = setData;
			hs.getData = getData;
			return hs;
		}

		function getMainFactory(){
			if(!main) {
				main = new Pays();
			}
			return main;
		}
		return getMainFactory();
	});