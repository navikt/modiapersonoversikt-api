var React = require('react');
var Modal = require('modal');
var ListevisningKomponent = require('./ListevisningKomponent');
var ForhandsvisningKomponent = require('./ForhandsvisningKomponent');
var Utils = require('utils');
var MeldingerSokStore = require('./MeldingerSokStore');

var MeldingerSok = React.createClass({
    vis: function (props) {
        props = props || {};
        this.store.update(props);
        this.refs.modal.open();
    },
    skjul: function () {
        this.refs.modal.close();
    },

    getInitialState: function () {
        this.store = new MeldingerSokStore($.extend({}, {
            fritekst: "",
            traader: [],
            valgtTraad: {},
            traadMarkupIds: {},
            listePanelId: Utils.generateId('sok-liste-'),
            forhandsvisningsPanelId: Utils.generateId('sok-forhandsvisningsPanelId-')
        }, this.props));
        return this.store.getState();
    },
    componentDidMount: function () {
        this.store.addListener(this.storeChanged);
    },
    componentDidUnmount: function () {
        this.store.removeListener(this.storeChanged);
    },
    keyDownHandler: function (event) {
        if (event.keyCode === 13) {
            this.store.submit(this.skjul, event);
        }
    },
    render: function () {
        var tekstlistekomponenter = this.state.traader.map(function (traad) {
            return <ListevisningKomponent
                key={traad.traadId}
                traad={traad}
                valgtTraad={this.state.valgtTraad}
                store={this.store}
            />
        }.bind(this));

        return (
            <Modal ref="modal">
                <form className={"sok-layout meldinger-sok"} onSubmit={this.store.submit.bind(this.store, this.skjul)} onKeyDown={this.keyDownHandler} >
                    <div tabIndex="-1" className="sok-container">
                        <input
                            type="text"
                            placeholder="Søk"
                            value={this.state.fritekst}
                            onChange={this.store.onChange.bind(this.store)}
                            onKeyDown={this.store.onKeyDown.bind(this.store, this.refs.tablist)}
                            aria-controls={this.state.listePanelId}
                        />
                    </div>
                    <div className="sok-visning">
                        <div tabIndex="-1" className="sok-liste" role="tablist" ref="tablist" id={this.state.listePanelId} aria-live="assertive" aria-atomic="true" aria-controls={this.state.forhandsvisningsPanelId}>
                        {tekstlistekomponenter}
                        </div>
                        <div tabIndex="-1" className="sok-forhandsvisning" role="tabpanel" id={this.state.forhandsvisningsPanelId} aria-atomic="true" aria-live="polite">
                            <ForhandsvisningKomponent traad={this.state.valgtTraad} />
                        </div>
                    </div>
                    <input type="submit" value="submit" className="hidden" />
                </form>
            </Modal>
        );
    },
    storeChanged: function () {
        this.setState(this.store.getState());
    }
});

module.exports = MeldingerSok;