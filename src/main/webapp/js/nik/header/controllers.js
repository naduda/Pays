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

		$scope.changeLocaleClick = function($event){
			var btn = document.getElementById('curLangButton'),
					el = $event.target;

			if(el.tagName.toLowerCase() !== 'li') el = el.parentNode;
			changeLocale(el.id);
		}

		$scope.changeTarif = function(){
			var dt, val, content, inDT, inVal;

			content = $('<table width="100%" cellspacing="5"></table>');
			inTarif = $('<select>' +
				'<option>Water</option>' +
				'<option>Gas</option>' +
			'</select>');
			inTarif.change(function(){
				var idTarif = inTarif.find('option:selected').index() + 1;
				hsFactory.getLastTarif(idTarif, function(data){
					inVal.val(data.t1);
					lastValue.html(
						'Last val (date) = ' + data.t1 + ' (' + data.dt + ')'
					);
				});
			});
			lastValue = $('<span></span>');
			inDT = $('<input type="text" value="' +
				'" size="10" class="inlineContent" readonly>');
			inVal = $('<input type="text" value="' +
				'" size="10" class="inlineContent">');
			content.append('<tr>' +
				'<td></td><td></td><td></td><td></td>' +
			'</tr>');
			content.find('td').eq(0).append(inTarif);
			content.find('td').eq(1).append(lastValue);
			content.find('td').eq(2).append(inDT);
			content.find('td').eq(3).append(inVal);

			hsFactory.getLastTarif(1, function(data){
				inVal.val(data.t1);
				lastValue.html(
					'Last val (date) = ' + data.t1 + ' (' + data.dt + ')'
				);
			});
			BootstrapDialog.show({
				size: BootstrapDialog.SIZE_NORMAL,
				title: 'Change Tarif',
				message: content,
				onshown: function(dialog){
					inDT.datepicker({
						showOn: 'button',
						dateFormat: 'dd.mm.yy',
						buttonText: "<i class='fa fa-calendar'></i>"
					});
					var date = new Date(),
							firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
					inDT.datepicker('setDate', firstDay);
					inVal.focus();
				},
				buttons: [{
					icon: 'glyphicon glyphicon-send',
					label: '  Send',
					cssClass: 'menubutton',
					autospin: true,
					action: function(dialog){
						var idTarif = inTarif.find('option:selected').index() + 1;
						hsFactory.setTarif(inDT.val(), idTarif, inVal.val(), 0,
							function(data){
								console.log(data);
								if(data.message === 'success'){
									alert('Tarif was added');
								} else {
									alert('Error');
								}
							});
						dialog.close();
					}
				}],
				draggable: true,
				closable: true
			});
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
					$('input[name="address"').val(data.address);
					$('input[name="ownerAccount1"').val(data.ownerAccount1);
					$('input[name="ownerAccount2"').val(data.ownerAccount2);
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