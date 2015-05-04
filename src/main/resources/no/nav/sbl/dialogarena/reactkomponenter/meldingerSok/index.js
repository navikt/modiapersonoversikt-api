var React = require('react/addons');
var Modal = require('./../modal');
var ListevisningKomponent = require('./ListevisningKomponent');
var ForhandsvisningKomponent = require('./ForhandsvisningKomponent');
var Utils = require('./../utils');
var MeldingerSokStore = require('./MeldingerSokStore');

var modalConfig = {
    title: {
        text: 'Meldingersøk modal',
        show: false,
        tag: 'h1.vekk'
    },
    description: {
        text: '',
        show: false,
        tag: 'div.vekk'
    },
    closeButton: {
        text: 'Lukk meldingersøk modal',
        show: true,
        tag: 'span.vekk'
    }
};

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
        var erTom = this.state.traader.length === 0;
        var sokVisning = (
            <div className={"sok-visning " + (erTom ? 'hidden' : '')}>
                <div tabIndex="-1" className="sok-liste" role="tablist" id={this.state.listePanelId}
                     aria-live="assertive" aria-atomic="true" aria-controls={this.state.forhandsvisningsPanelId}>
                    {tekstlistekomponenter}
                </div>
                <div tabIndex="-1" className="sok-forhandsvisning" role="tabpanel"
                     id={this.state.forhandsvisningsPanelId} aria-atomic="true" aria-live="polite">
                    <ForhandsvisningKomponent traad={this.state.valgtTraad}/>
                </div>
            </div>
        );
        var tomInnhold;
        if (this.state.feilet) {
            tomInnhold = <h1 className="tom">Noe feilet</h1>
        } else if (this.state.initialisert) {
            tomInnhold = <h1 className="tom">Ingen treff</h1>;
        } else {
            tomInnhold =
                <div className="tom">
                    <img src="../img/ajaxloader/hvit/loader_hvit_128.gif" alt="Henter meldinger"></img>
                </div>
        }

        var tomVisning = (
            <div className={"sok-visning " + (erTom ? '' : 'hidden')}>
                {tomInnhold}
            </div>
        );

        return (
            <Modal ref="modal" title={modalConfig.title} description={modalConfig.description}
                   closeButton={modalConfig.closeButton}>
                <form className={"sok-layout meldinger-sok"} onSubmit={this.store.submit.bind(this.store, this.skjul)}
                      onKeyDown={this.keyDownHandler}>
                    <div tabIndex="-1" className="sok-container">
                        <div>
                            <input
                                type="text"
                                placeholder="Søk"
                                value={this.state.fritekst}
                                title="Søk"
                                onChange={this.store.onChange.bind(this.store)}
                                onKeyDown={this.store.onKeyDown.bind(this.store, $('#'+this.state.listePanelId))}
                                aria-controls={this.state.listePanelId}
                                />
                            <img src="../img/sok.svg" alt="Forstørrelseglass-ikon" aria-hidden="true"/>
                        </div>
                    </div>
                    {sokVisning}
                    {tomVisning}
                    <input type="submit" value="submit" className="hidden"/>
                </form>
            </Modal>
        );
    },
    storeChanged: function () {
        this.setState(this.store.getState());
    }
});

module.exports = MeldingerSok;