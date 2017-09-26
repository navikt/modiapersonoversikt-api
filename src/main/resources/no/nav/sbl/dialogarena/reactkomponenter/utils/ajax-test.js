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

    it('should use contentType \'application/json\' if no contentType in RequestModifier', () => {
        Ajax.post('http://localhost:9876/echo', {});

        let request = Ajax.toPromise.getCall(0).args[0].req;
        let headers = request._headers;

        expect(Ajax.toPromise.calledOnce).to.equal(true);
        expect(headers['content-type']).to.equal('application/json');
    });
});
