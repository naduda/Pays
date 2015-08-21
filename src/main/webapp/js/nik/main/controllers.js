heatSupply.mainControllers = angular.module('mainControllers', [
	'headerFactory']);

heatSupply.mainControllers
	.controller('mainController', 
		function ($scope, translate, hsFactory, $location, $http, $compile){
		$scope.$on('$viewContentLoaded', function(){
			translate.run(function(t){
				t.translateAllByLocaleName(hsFactory.language);
			});
		});
		$location.path('/');

		$scope.menuClick = function($event){
			var li = $event.target, fileUrl;
			while(li.tagName !== 'LI' && li.tagName !== 'BUTTON'){
				li = li.parentNode;
			}
			switch(li.id){
				case 'waterBillings': $scope.idTarif = 1; break;
				case 'gasBillings': $scope.idTarif = 2; break;
				default: $scope.idTarif = 0; break;
			}

			fileUrl = 'html/templates/' + li.id + '.html';
			$('#reportContent').load(fileUrl, function(){
				$('#reportContent').html(
					$compile($('#reportContent').html())($scope)
				);
				translate.run(function(t){
					t.translateAllByLocaleName(hsFactory.language);

					$('#dtEnd').datepicker({
						showOn: 'button',
						dateFormat: 'dd.mm.yy',
						buttonText: "<i class='fa fa-calendar'></i>"
					});
					$('#dtEnd').datepicker('setDate', new Date());

					hsFactory.getData($scope.idTarif, function(data){
						$scope.dataDTbeg = data.dt;
						$scope.dataValBeg = data.value1;
						if(data.value1 === '-')
							$scope.dataValEnd = 0;
						else
							$scope.dataValEnd = Number(data.value1) + 100;
					})
				});
			});
		}

		$scope.editData = function (event){
			var row = event.target.parentNode.parentNode,
					dt, val, content, inDT, inVal;

			dt = $(row).children('td').eq(0).html();
			val = $(row).children('td').eq(1).html();
			console.log('EDIT');
			content = $('<table width="100%" cellspacing="5"></table>');
			inDT = $('<input type="text" value="' + dt +
				'" size="10" class="inlineContent" readonly>');
			inDT.datepicker({dateFormat: 'dd.mm.yy'});
			inVal = '<input type="text" value="' + val +
				'" size="10" class="inlineContent">';
			content.append('<tr>' +
				'<td></td><td></td>' +
			'</tr>');
			content.find('td').eq(0).append(inDT);
			content.find('td').eq(1).append(inVal);
			BootstrapDialog.show({
				size: BootstrapDialog.SIZE_SMALL,
				title: 'Edit data',
				message: content,
				onshown: function(dialog) {
					dialog.getModalBody().find('input').focus();
				},
				buttons: [{
					icon: 'glyphicon glyphicon-send',
					label: '  Send',
					cssClass: 'menubutton',
					autospin: true,
					action: function(dialog){
						// par.text = dialog.getModalBody().find('textarea').val();
						// cm.parameters.push(par);

						// dialog.enableButtons(false);
						// model.webSocket.send(JSON.stringify(cm));
						// $('.selectedAlarm').each(function() {
						// 	$(this).removeClass('selectedAlarm');
						// });
						console.log('SENDED ' + dt + ' - ' + inDT.val());
						dialog.close();
					}
				}],
				draggable: true,
				closable: true
			});

		}

		$scope.deleteData = function (event){
			var row = event.target.parentNode.parentNode, dt;

			dt = $(row).children().first().html();
			deleteDataFromDB($scope.idTarif, dt, function(data){
				var el, delIndex;
				el = $scope.dataList.filter(function(ce){
					return ce.dt === data.remove;
				})[0];
				delIndex = $scope.dataList.indexOf(el);
				$scope.dataList.splice(delIndex, 1);
			});
		}

		function deleteDataFromDB(idTarif, dt, callback){
			if(!$('#showHistory')[0].checked) return false;
			$http({
				method: 'GET',
				url: '/Pays/dataServer/db/deleteData?' + 
							'params=' + hsFactory.userId + ';' + idTarif + ';' + dt,
				cache: false
			})
			.success(function(data){
				if(callback) callback(data);
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		$scope.getDataHistory = function(){
			console.log('CHANGE');
			$('#tHistory table tr').each(function(r){
				if(r > 0){
					this.parentNode.removeChild(this);
				}
			});
			if(!$scope.isShowHistory) return false;
			$http({
				method: 'GET',
				url: '/Pays/dataServer/db/getAllData?' + 
							'params=' + hsFactory.userId + ';' + $scope.idTarif,
				cache: false
			})
			.success(function(data){
				$scope.dataList = data;
			})
			.error(function(data, status, headers, config){
				console.log(status)
			});
		};

		function addWaterBillings(){
			console.log('addWaterBillings');
		}

		$scope.addGasBilling = function(){
			var idTarif = $scope.idTarif,
					dt = $('#dtEnd').val();
					val1 = $('#valEnd').val();

			hsFactory.setData(idTarif, dt, val1, 0, function(data){
				$('#dtBeg').html(data.dt);
				$('#valBeg').html(data.value1);
				$('#dtEnd').val(data.dtEnd);
				$('#valEnd').val(Number(data.value1) + 100);
				if($scope.isShowHistory){
					var d = new Object(null);
					d.dt = data.dt;
					d.idTarif = $scope.idTarif;
					d.idUser = hsFactory.userId;
					d.value1 = data.value1;
					d.value2 = data.value2;
					$scope.dataList.push(d);
				}
			});
		}

		$scope.createReport = function($event){
			var li = $event.target;
			while(li.tagName !== 'LI' && li.tagName !== 'BUTTON'){
				li = li.parentNode;
			}

			$('#reportContent').css({
				'max-height': ($(window).height() - 115) + 'px',
				'overflow': 'auto'
			});
			heatSupply.currentReport = li.id;
			heatSupply.socket.send(JSON.stringify({
				'type': 'CommandMessage', 'command': 'getReport',
				'parameters': [{'reportName' : li.id}]
			}));
		}

		$scope.saveAs = function($event){
			var btn = $event.target;
			if(btn){
				heatSupply.currentReportExt = btn.id.slice(1);
				heatSupply.socket.send(JSON.stringify({
					'type': 'CommandMessage', 'command': 'saveReport',
					'parameters': [
						{'reportName': heatSupply.currentReport},
						{'ext': btn.id.slice(1)}]
				}));
			}
		}

		heatSupply.initWebSocket(hsFactory.url);
	});