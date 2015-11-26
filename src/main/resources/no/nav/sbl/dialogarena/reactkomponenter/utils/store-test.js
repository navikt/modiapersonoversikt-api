/* eslint-env mocha */
import './../test-config';
import Store from './store';
import {expect} from 'chai';

class TestStore extends Store {
    constructor(props) {
        super(props);
    }

    setState(nState) {
        this.state = nState;
        this.fireUpdate();
    }
}

describe('Store', () => {
    it('Tar vare på initial state', () => {
        const state = {myState: 1};
        const store = new Store(state);

        expect(store.getState()).to.equal(state);
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

        expect(resp1).to.equal(true);
        expect(resp2).to.equal(true);
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

        expect(resp1).to.equal(true);
        expect(resp2).to.equal(true);

        resp1 = null;
        resp2 = null;
        ts.removeListener(listener1);

        ts.setState({mer: 'tullball'});


        expect(resp1).to.equal(null);
        expect(resp2).to.equal(true);
    });
});
