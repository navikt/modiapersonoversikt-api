/* eslint-env mocha */
import './../test-config';
import { expect } from 'chai';
import sinon from 'sinon';
import Ajax from './ajax';

describe('HTTP request', () => {
    beforeEach(() => {
        sinon.spy(Ajax, 'toPromise');
    });

    afterEach(() => {
        Ajax.toPromise.restore();
    });


    it('should use contentType in RequestModifier if set', () => {
        Ajax.post('http://localhost:9876/echo', {}, (req) => {
            return req.type('text/html');
        });

        expect(Ajax.toPromise.calledOnce).to.equal(true);

        expect(Ajax.toPromise.calledWith(sinon.match({header: {['Content-Type']: 'text/html'}}))).to.equal(true);
    });

    it('should use contentType \'application/json\' if no contentType in RequestModifier', () => {
        Ajax.post('http://localhost:9876/echo', {});

        expect(Ajax.toPromise.calledOnce).to.equal(true);

        expect(Ajax.toPromise.calledWith(sinon.match({header: {['Content-Type']: 'application/json'}}))).to.equal(true);
    });
});
