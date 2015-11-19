import './../test-config';
import { expect } from 'chai';
import React from 'react/addons';
import sinon from 'sinon';
import Ajax from './ajax';

describe('HTTP request', function () {

    beforeEach(function () {
        sinon.spy(Ajax, 'toPromise');
    });

    afterEach(function () {
        Ajax.toPromise.restore();
    });


    it("should use contentType in RequestModifier if set", function () {

        Ajax.post('http://localhost:9876/echo', {}, function (req) {
            return req.type('text/html');
        });

        expect(Ajax.toPromise.calledOnce).to.equal(true);

        expect(Ajax.toPromise.calledWith(sinon.match({header: {['Content-Type']: 'text/html'}}))).to.equal(true);
    });

    it("should use contentType 'application/json' if no contentType in RequestModifier", function () {

        Ajax.post('http://localhost:9876/echo', {});

        expect(Ajax.toPromise.calledOnce).to.equal(true);

        expect(Ajax.toPromise.calledWith(sinon.match({header: {['Content-Type']: 'application/json'}}))).to.equal(true);
    });


});