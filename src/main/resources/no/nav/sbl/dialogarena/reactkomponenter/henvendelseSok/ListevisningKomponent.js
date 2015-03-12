var React = require('react');
var sanitize = require('sanitize-html');

module.exports = React.createClass({
    statics: {
        lagAriaLabel: function (traad) {
            return traad.temagruppe;
        }
    },
    render: function () {
        var erValgt = erValgtTekst(this.props.traad, this.props.valgtTraad);
        var cls = erValgt ? "meldingsforhandsvisning valgt" : "meldingsforhandsvisning";
        var traad = this.props.traad;
        var melding = traad.meldinger[0];
        var dato = traad.opprettetDato;

        var datoString = dato;
        if (!melding.erInngaaende) {
            datoString += " - " + melding.fraBruker;
        }
        console.log('melding', melding);
        datoString = sanitize(datoString, {allowedTags: ['em']});

        var meldingsStatus = this.props.traad.statusTekst + ", " + this.props.traad.temagruppe;
        meldingsStatus = sanitize(meldingsStatus, {allowedTags: ['em']});
        var innhold = sanitize(this.props.traad.innhold, {allowedTags: ['em']});

        return (
            <div className="sok-element" onClick={tekstChangedProxy(this.props.store, this.props.traad)}>
                <input id={"melding" + this.props.traad.key} name="tekstListeRadio" type="radio" readOnly checked={erValgt} />
                <label htmlFor={"melding" + this.props.traad.key} className={cls}>
                    <header>
                        <p dangerouslySetInnerHTML={{__html: datoString}}></p>
                        <div className={this.props.traad.statusKlasse}></div>
                        <p className={'meldingstatus'} dangerouslySetInnerHTML={{__html: meldingsStatus}}></p>
                    </header>
                    <p className="fritekst" dangerouslySetInnerHTML={{__html: innhold}}></p>
                </label>
            </div>
        );
    }
});

function tekstChangedProxy(store, traad) {
    return function () {
        store.traadChanged(traad);
    };
}

function erValgtTekst(traad, valgtTraad) {
    return traad === valgtTraad;
}