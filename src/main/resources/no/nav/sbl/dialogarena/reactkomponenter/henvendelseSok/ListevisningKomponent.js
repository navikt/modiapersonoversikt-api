var React = require('react');
var moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');

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
        var dato = traad.datoInMillis || new Date();
        dato = moment(dato).format('LLL');

        var datoString = dato;
        if (!melding.erInngaaende) {
            datoString += " - " + melding.fraBruker;
        }

        var meldingsStatus = this.props.traad.statusTekst + ", " + this.props.traad.temagruppe;

        return (
            <div className="sok-element" onClick={tekstChangedProxy(this.props.store, this.props.traad)}>
                <input id={"melding" + this.props.traad.key} name="tekstListeRadio" type="radio" readOnly checked={erValgt} />
                <label htmlFor={"melding" + this.props.traad.key} className={cls}>
                    <header>
                        <p>{datoString}</p>
                        <div className={this.props.traad.statusKlasse}></div>
                        <p className={'meldingstatus'} dangerouslySetInnerHTML={{__html: meldingsStatus}}></p>
                    </header>
                    <p className="fritekst" dangerouslySetInnerHTML={{__html: this.props.traad.innhold}}></p>
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