import "./../test-config.js";
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

describe('Store', function () {

    it('Tar vare på initial state', function () {

        var state = {myState: 1};
        var store = new Store(state);

        expect(store.getState()).to.equal(state);

    });

    it('kaller alle listeners ved fireUpdate', function () {
        var ts = new TestStore({});
        var resp1 = null;
        var resp2 = null;
        var listener1 = function () {
            resp1 = true
        };
        var listener2 = function () {
            resp2 = true
        };

        ts.addListener(listener1);
        ts.addListener(listener2);

        ts.setState({tull: 'ball'});

        expect(resp1).to.equal(true);
        expect(resp2).to.equal(true);

    });

    it('removeListener fjerner lytting', function () {
        var ts = new TestStore({});
        var resp1 = null;
        var resp2 = null;
        var listener1 = function () {
            resp1 = true
        };
        var listener2 = function () {
            resp2 = true
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