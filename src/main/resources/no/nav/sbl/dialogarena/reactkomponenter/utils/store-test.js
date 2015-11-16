require('./../test-config.js');
var Store = require('./store');
var assert = require('chai').assert;

var TestStore = function () {
    Store.apply(this, arguments);
};
TestStore.prototype = $.extend({}, Store.prototype, TestStore.prototype);
TestStore.prototype.setState = function (nState) {
    this.state = nState;
    this.fireUpdate();
};

describe('Store', function () {

    it('Tar vare på initial state', function () {
        var state = {myState: 1};
        var store = new Store(state);

        assert.equal(store.getState(), state);
    });

    it('kaller alle listeners ved fireUpdate', function () {
        var ts = new TestStore({});
        var resp1 = null;
        var resp2 = null;
        var listener1 = function () {
            resp1 = true;
        };
        var listener2 = function () {
            resp2 = true;
        };

        ts.addListener(listener1);
        ts.addListener(listener2);

        ts.setState({tull: 'ball'});

        assert.equal(resp1, true);
        assert.equal(resp2, true);
    });

    it('removeListener fjerner lytting', function () {
        var ts = new TestStore({});
        var resp1 = null;
        var resp2 = null;
        var listener1 = function () {
            resp1 = true;
        };
        var listener2 = function () {
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