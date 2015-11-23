/* eslint-env mocha */
import './../test-config';
import Store from './store';
import { assert } from 'chai';

const TestStore = function TestStore() {
    Store.apply(this, arguments);
};
TestStore.prototype = $.extend({}, Store.prototype, TestStore.prototype);
TestStore.prototype.setState = function setState(nState) {
    this.state = nState;
    this.fireUpdate();
};

describe('Store', () => {
    it('Tar vare på initial state', () => {
        const state = {myState: 1};
        const store = new Store(state);

        assert.equal(store.getState(), state);
    });

    it('kaller alle listeners ved fireUpdate', () => {
        const ts = new TestStore({});
        let resp1 = null;
        let resp2 = null;
        const listener1 = () => {
            resp1 = true;
        };
        const listener2 = () => {
            resp2 = true;
        };

        ts.addListener(listener1);
        ts.addListener(listener2);

        ts.setState({tull: 'ball'});

        assert.equal(resp1, true);
        assert.equal(resp2, true);
    });

    it('removeListener fjerner lytting', () => {
        const ts = new TestStore({});
        let resp1 = null;
        let resp2 = null;
        const listener1 = () => {
            resp1 = true;
        };
        const listener2 = () => {
            resp2 = true;
        };

        ts.addListener(listener1);
        ts.addListener(listener2);

        ts.setState({tull: 'ball'});

        assert.equal(resp1, true);
        assert.equal(resp2, true);

        resp1 = null;
        resp2 = null;
        ts.removeListener(listener1);

        ts.setState({mer: 'tullball'});

        assert.equal(resp1, null);
        assert.equal(resp2, true);
    });
});
