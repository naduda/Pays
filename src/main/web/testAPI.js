var fs = require('fs');
var path = require('path');
var express = require('express');
var bodyParser = require('body-parser');
var app = express();

app.set('port', 8088);
app.use(require('connect-livereload')());

app.use('/', express.static(path.join(__dirname, './src')));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
app.use(function(req, res, next) {
		res.setHeader('Access-Control-Allow-Origin', '*');
		res.setHeader('Cache-Control', 'no-cache');
		next();
});

app.get('/resources/user', function(req, res) {
	if(!req.headers.authorization) {
		res.json({});
		return;
	}
	fs.readFile('./api/resources/users_web', function(err, data) {
		if (err) {
			console.error(err);
			console.error('/resources/user')
			process.exit(1);
		}

		var users = JSON.parse(data);
		var atob = require('atob');
		var obj = req.headers.authorization, user, name, pwd;
		obj = obj.slice(obj.indexOf(' ') + 1);
		obj = atob(obj);
		obj = obj.split(':');
		name = obj[0];
		pwd = obj[1];

		user = users.filter(function(u){
			return u.login === name;
		});
		user = user.length > 0 ? user[0] : null;
		if(user && (user.password === pwd)) {
			fs.writeFile('./api/resources/currentUser',
				JSON.stringify({login: name}));
			res.json(user);
			return;
		}
		console.log('Name = ' + name + '; password = ' + pwd);
		res.json({});
	});
});

app.get('/resources/langs', function(req, res) {
	fs.readFile('./api/resources/langs', function(err, data) {
		if (err) {
			console.error(err);
			console.error('/resources/langs')
			process.exit(1);
		}
		res.json(JSON.parse(data));
	});
});

app.get('/secureresources/report', (req, res) => {
	fs.readFile('./src/reports/Water.pdf', (err, data) => {
		if(!err) {
			res.writeHead(200, {'Content-Type': 'application/pdf'});
			res.write(data);
			res.end();
		} else {
			res.writeHead(400, {'Content-Type': 'text/html'});
			res.end("index.html not found");
		}
	});
});

app.get('/secureresources/profileInfo', function(req, res) {
	fs.readFile('./api/resources/users_web', function(err, data) {
		if (err) {
			console.error(err);
			console.error('/secureresources/resetBlockTimeout');
			process.exit(1);
		}
		var users = JSON.parse(data);
		fs.readFile('./api/resources/currentUser', function(err, cu) {
			var cu = JSON.parse(cu);
			var user = users
				.filter(u => {
					return u.login === cu.login;
				});
			user = user ? user[0] : null;
			var ct = new Date();
			if(user){
				res.json(user);
			}
		});
	});
});

app.post('/logout', function(req, res) {
	res.json('');
});

app.get('/secureresources/services', function(req, res) {
	fs.readFile('./api/secureresources/services', function(err, data) {
		res.json(JSON.parse(data));
	});
});

app.get('/secureresources/tarifs', function(req, res) {
	fs.readFile('./api/secureresources/tarifs', function(err, data) {
		res.json(JSON.parse(data));
	});
});

app.get('/secureresources/lastdata', function(req, res) {
	fs.readFile('./api/secureresources/lastdata', function(err, data) {
		res.json(JSON.parse(data));
	});
});

app.get('/secureresources/dataperiod', function(req, res) {
	fs.readFile('./api/secureresources/dataperiod', function(err, data) {
		res.json(JSON.parse(data));
	});
});

app.listen(app.get('port'), function() {
	console.log('Server started: http://localhost:' + app.get('port') + '/');
});

function timestamp2date(ts){
	var a = new Date(ts);
	var year = a.getFullYear();
	var month = fullNumber(a.getMonth() + 1);
	var date = fullNumber(a.getDate());
	var hour = fullNumber(a.getHours());
	var min = fullNumber(a.getMinutes());
	var sec = fullNumber(a.getSeconds());
	var time = year + '-' + month + '-' + date + ' ' + hour + ':' + min + ':' + sec;
	return time;
}

function fullNumber(n){return n < 10 ? '0' + n : n;}