/* eslint-env mocha */
/* eslint no-unused-expressions:0 */
import './../test-config';
import {expect} from 'chai';
import VarselStore from './varsel-store';
import Ajax from '../utils/ajax';
import sinon from 'sinon';
import chai from 'chai';
import sinonChai from 'sinon-chai';
chai.use(sinonChai);

describe('VarselStore', () => {
    it('henter varsler basert på fnr', () => {
        sinon.spy(Ajax, 'get');
        const fnr = '123456789';
        const store = new VarselStore(fnr);
        const args = Ajax.get.args[0];
        const url = args[0];

        expect(Ajax.get).to.have.been.calledTwice;
        expect(url).to.contains(fnr).and.to.contain('varsler');
        expect(store.getResources()).to.eql({ resources: {}});

        Ajax.get.restore();
    });

    it('sorterer varsler på mottatt tidspunkt, descending order', () => {
        const fnr = '123456789';
        const v1 = {mottattTidspunkt: '1997-11-17T08:39:39.444Z'};
        const v2 = {mottattTidspunkt: '1995-12-19T08:40:42.444Z'};
        const v3 = {mottattTidspunkt: '1997-11-20T08:40:39.444Z'};

        const store = new VarselStore(fnr);
        store._sortVarsler([v1, v2, v3]);

        expect(store.state.varsler).to.eql([v3, v1, v2]);
    });
});
