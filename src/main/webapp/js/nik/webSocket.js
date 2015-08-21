heatSupply.initWebSocket = function(url){
	console.log(url);
	var ws = new WebSocket('ws' + url.slice(4) + 'socketServer');
	ws.onmessage = function (message){
		console.log(message);
		if(message.data instanceof Blob){
			saveTextAsFile(message.data,
				heatSupply.currentReport, heatSupply.currentReportExt);
			return;
		}
		var jsonData = JSON.parse(message.data);
		console.log(jsonData)
		if(jsonData.type === 'CommandMessage') {
			if(jsonData.command === 'reportHTML'){
				var param = jsonData.parameters[0],
						reportContent = $('#reportContent');
				if(reportContent) {
					reportContent.html(param.content);
					$('#reportContent table:first').css({
						'zoom': ($('#reportContent').width() - 20)/595
					});
				}
			}
		}
	}
	ws.onerror = function (e){
		console.log(e);
	}
	ws.onclose = function (){
		console.log('session close ');
	}
	ws.onopen = function(){
		console.log('session open');
	}

	function saveTextAsFile(textToWrite, fileNameToSaveAs, ext){
		var typeBlob = ext === 'pdf' ? 
					'application/pdf' : 
							ext === 'xls' ? 'application/csv' :'text/html';
				textFileAsBlob = new Blob([textToWrite], {type: typeBlob}),
				downloadLink = document.createElement("a");

		downloadLink.download = fileNameToSaveAs + '.' + ext;
		downloadLink.href = window.URL.createObjectURL(textFileAsBlob);
		downloadLink.click();
	}

	heatSupply.socket = ws;
}