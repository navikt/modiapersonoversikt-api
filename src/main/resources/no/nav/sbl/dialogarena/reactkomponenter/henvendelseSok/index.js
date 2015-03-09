var React = require('react');
var Modal = require('modal');
var ListevisningKomponent = require('./ListevisningKomponent');
var ForhandsvisningKomponent = require('./ForhandsvisningKomponent');
var Utils = require('utils');
var HenvendelseSokStore = require('./HenvendelseSokStore');

module.exports = React.createClass({
    vis: function () {
        this.refs.modal.open();
    },
    skjul: function () {
        this.refs.modal.close();
    },
    oppdaterTraadRefs: function (traadRefs) {
        this.store.oppdaterTraadRefs(traadRefs);
    },

    getInitialState: function () {
        this.store = new HenvendelseSokStore($.extend({}, {
            fritekst: "",
            traader: [],
            valgtTraad: {},
            traadMarkupIds: {}
        }, this.props));
        return this.store.getState();
    },
    componentDidMount: function () {
        this.store.addListener(this.storeChanged);
        this.store.onChange({target: {value: ''}});
    },
    componentDidUnmount: function () {
        this.store.removeListener(this.storeChanged);
    },
    keyDownHandler: function (event) {
        if (event.keyCode === 13) {
            this.store.submit(this.skjul, event);
        }
    },
    componentWillMount: function () {
        $.get('/modiabrukerdialog/rest/meldinger/' + this.props.fnr + '/indekser');
    },
    render: function () {
        var listePanelId = Utils.generateId('sok-liste-');
        var forhandsvisningsPanelId = Utils.generateId('sok-forhandsvisningsPanelId-');
        var tekstlistekomponenter = this.state.traader.map(function (traad) {
            return <ListevisningKomponent
                traad={traad}
                valgtTraad={this.state.valgtTraad}
                store={this.store}
            />
        }.bind(this));

        return (
            <Modal ref="modal">
                <form className={"sok-layout henvendelse-sok"} onSubmit={this.store.submit.bind(this.store, this.skjul)} onKeyDown={this.keyDownHandler} >
                    <div tabIndex="-1" className="sok-container">
                        <input
                            type="text"
                            placeholder="Søk"
                            value={this.state.fritekst}
                            onChange={this.store.onChange.bind(this.store)}
                            onKeyDown={this.store.onKeyDown.bind(this.store)}
                            aria-controls={listePanelId}
                        />
                    </div>
                    <div className="sok-visning">
                        <div tabIndex="-1" className="sok-liste" role="tablist" ref="tablist" id={listePanelId} aria-live="assertive" aria-atomic="true">
                        {tekstlistekomponenter}
                        </div>
                        <div tabIndex="-1" className="sok-forhandsvisning" role="tabpanel" id={forhandsvisningsPanelId} aria-atomic="true" aria-live="polite">
                            <ForhandsvisningKomponent traad={this.state.valgtTraad} />
                        </div>
                    </div>
                    <input type="submit" value="submit" className="hidden" />
                </form>
            </Modal>
        );
    },
    storeChanged: function(){
        this.setState(this.store.getState());
    }
});