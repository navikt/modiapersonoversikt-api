require('./../test-config.js');
var expect = require('chai').expect;
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;
var sinon = require('sinon');
var Ajax = require('./ajax');

describe('HTTP request', function () {

    sinon.spy(Ajax, 'toPromise');

    it("should use contentType in RequestModifier if set", function () {

        Ajax.post('http://localhost:9876/echo', {}, function (req) {
            return req.type('text/html');
        });

        expect(Ajax.toPromise.calledOnce).to.equal(true);

        expect(Ajax.toPromise.calledWith(sinon.match({header: {['Content-Type']: 'text/html'}}))).to.equal(true);
    });

    it("should use contentType 'application/json' if no contentType in RequestModifier", function () {

        Ajax.post('http://localhost:9876/echo', {});

        expect(Ajax.toPromise.calledTwice).to.equal(true);

        expect(Ajax.toPromise.calledWith(sinon.match({header: {['Content-Type']: 'application/json'}}))).to.equal(true);
    });


});