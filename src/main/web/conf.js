exports.config = {
	seleniumAddress: 'http://localhost:4444/wd/hub',
	capabilities: {
		'browserName': 'chrome'
	},
	specs: [
		'./test/*.spec.js'
	],
	jasmineNodeOpts: {
		showColors: true,
		defaultTimeoutInterval: 10000
	}
}