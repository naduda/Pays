describe('describe', function(){
	it('it first', function(){
		browser.get('http://localhost:8088/index.html');
		element(by.model('credentials.password')).sendKeys('qwe');
		element(by.css('button[type="submit"]')).click();
	});
});