/** @jsx React.DOM */
var React = require('react/addons');
var Modal = require('./../modal');
var Utils = require('./../utils');
var TekstForhandsvisning = require('./TekstForhandsvisning');
var TekstListeKomponent = require('./TekstListeKomponent');
var KnaggInput = require('./../knagginput');
var SkrivestotteStore = require('./SkrivestotteStore');

var modalConfig = {
    title: {
        text: 'Skrivestøtte modal',
        show: false,
        tag: 'h1.vekk'
    },
    description: {
        text: '',
        show: false,
        tag: 'div.vekk'
    },
    closeButton: {
        text: 'Lukk skrivestøtte modal',
        show: true,
        tag: 'span.vekk'
    }
};

var Skrivestotte = React.createClass({
    vis: function () {
        this.refs.modal.open();
    },
    skjul: function () {
        this.refs.modal.close();
    },
    getInitialState: function () {
        this.store = new SkrivestotteStore($.extend({}, {
            knagger: [],
            fritekst: "",
            tekster: [],
            valgtTekst: {},
            valgtLocale: Utils.Constants.LOCALE_DEFAULT,
            listePanelId: Utils.generateId('sok-layout-'),
            forhandsvisningsPanelId: Utils.generateId('sok-layout-')
        }, this.props));
        return this.store.getState();
    },
    componentDidMount: function () {
        this.store.addListener(this.storeChanged);
        this.store.onChange({fritekst: this.state.fritekst, knagger: this.state.knagger});
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
        var tekstlistekomponenter = this.state.tekster.map(function (tekst) {
            return <TekstListeKomponent key={tekst.key} tekst={tekst} valgtTekst={this.state.valgtTekst} store={this.store}/>
        }.bind(this));

        var erTom = this.state.tekster.length === 0;
        var sokVisning = (
            <div className={"sok-visning " + (erTom ? 'hidden' : '')}>
                <div tabIndex="-1" className="sok-liste" role="tablist" id={this.state.listePanelId} aria-live="assertive" aria-atomic="true" aria-controls={this.state.forhandsvisningsPanelId}>
                    {tekstlistekomponenter}
                </div>
                <div tabIndex="-1" className="sok-forhandsvisning" role="tabpanel" id={this.state.forhandsvisningsPanelId} aria-atomic="true" aria-live="polite">
                    <TekstForhandsvisning tekst={this.state.valgtTekst} locale={this.state.valgtLocale} store={this.store}/>
                </div>
            </div>
        );

        var tomVisning = (
            <div className={"sok-visning " + (erTom ? '' : 'hidden')}>
                <h1 className="tom">Ingen treff</h1>
            </div>
        );

        return (
            <Modal ref="modal" skipFocus={['.knagg > button']} title={modalConfig.title} description={modalConfig.description} closeButton={modalConfig.closeButton}
                width={904} height={600}>
                <form className={"sok-layout tekstforslag"} onSubmit={this.store.submit.bind(this.store, this.skjul)} onKeyDown={this.keyDownHandler} >
                    <div tabIndex="-1" className="sok-container">
                        <KnaggInput knagger={this.state.knagger} fritekst={this.state.fritekst} store={this.store} tablisteId={this.state.listePanelId} placeholder={'Søk'}/>
                    </div>
                    <fieldset className="wcag">
                        <legend>Tekst liste</legend>
                    {sokVisning}
                    </fieldset>
                    {tomVisning}
                    <input type="submit" value="submit" className="hidden" />
                </form>
            </Modal>
        );
    },
    storeChanged: function () {
        this.setState(this.store.getState());
    }
});

module.exports = Skrivestotte;