/** @jsx React.DOM */
var React = require('react');
var Modal = require('modal');
var Utils = require('utils');
var TekstForhandsvisning = require('./TekstForhandsvisning');
var TekstListeKomponent = require('./TekstListeKomponent');
var KnaggInput = require('knagginput');
var SkrivestotteStore = require('./SkrivestotteStore');

var Skrivestotte = React.createClass({
    vis: function(){
        this.refs.modal.open();
    },
    skjul: function(){
        this.refs.modal.close();
    },
    getInitialState : function () {
        this.store = new SkrivestotteStore($.extend({},{
            knagger : [],
            fritekst: "",
            tekster: [],
            valgtTekst: {},
            valgtLocale: Utils.Constants.LOCALE_DEFAULT
        }, this.props));
        return this.store.getState();
    },
    componentDidMount : function() {
        this.store.addListener(this.storeChanged);
        this.store.onChange({fritekst: this.state.fritekst, knagger: this.state.knagger});
    },
    componentDidUnmount : function() {
        this.store.removeListener(this.storeChanged);
    },
    keyDownHandler: function(event){
        if (event.keyCode === 13) {
            this.store.submit(this.skjul, event);
        }
    },
    render: function () {
        var listePanelId = Utils.generateId('sok-layout-');
        var forhandsvisningsPanelId = Utils.generateId('sok-layout-');
        var tekstlistekomponenter = this.state.tekster.map(function(tekst) {
            return <TekstListeKomponent key={tekst.key} tekst={tekst} valgtTekst={this.state.valgtTekst} store={this.store}/>
        }.bind(this));

        return (
            <Modal ref="modal" skipFocus={['div', '.knagg > button']}>
                <form className={"sok-layout tekstforslag"} onSubmit={this.store.submit.bind(this.store, this.skjul)} onKeyDown={this.keyDownHandler} >
                    <div tabIndex="-1" className="sok-container">
                        <KnaggInput knagger={this.state.knagger} fritekst={this.state.fritekst} store={this.store} tabliste={this.refs.tablist} />
                    </div>
                    <div className="sok-visning">
                        <div tabIndex="-1" className="sok-liste" role="tablist" ref="tablist" id={listePanelId} aria-live="assertive" aria-atomic="true">
                        {tekstlistekomponenter}
                        </div>
                        <div tabIndex="-1" className="sok-forhandsvisning" role="tabpanel" id={forhandsvisningsPanelId} aria-atomic="true" aria-live="polite">
                            <TekstForhandsvisning tekst={this.state.valgtTekst} locale={this.state.valgtLocale} store={this.store}/>
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

module.exports = Skrivestotte;