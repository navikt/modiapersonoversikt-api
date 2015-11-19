import './../test-config';
import {expect} from 'chai';
import VarselStore from './varsel-store';
import Ajax from '../utils/ajax';
import sinon from 'sinon';
import chai from 'chai';
import sinonChai from 'sinon-chai';
chai.use(sinonChai);

describe('VarselStore', function () {

    it('henter varsler basert på fnr', function () {
        sinon.spy(Ajax, 'get');
        const fnr = "123456789";
        var store = new VarselStore(fnr);
        var args = Ajax.get.args[0];
        var url = args[0];

        expect(Ajax.get).to.have.been.calledTwice;

        expect(url).to.contains(fnr).and.to.contain('varsler');

        Ajax.get.restore();
    });

    it('sorterer varsler på mottatt tidspunkt, descending order', function () {
        const fnr = "123456789";
        var v1 = {mottattTidspunkt: "1997-11-17T08:39:39.444Z"};
        var v2 = {mottattTidspunkt: "1995-12-19T08:40:42.444Z"};
        var v3 = {mottattTidspunkt: "1997-11-20T08:40:39.444Z"};

        var store = new VarselStore(fnr);
        store._sortVarsler([v1, v2, v3]);

        expect(store.state.varsler).to.eql([v3, v1, v2]);

    });

});