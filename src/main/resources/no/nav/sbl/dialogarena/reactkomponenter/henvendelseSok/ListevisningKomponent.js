var React = require('react');
var moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');

module.exports = React.createClass({
    statics: {
        lagAriaLabel: function (henvendelse) {
            return henvendelse.temagruppe;
        }
    },
    render: function () {
        var erValgt = erValgtTekst(this.props.henvendelse, this.props.valgtHenvendelse);
        var cls = erValgt ? "meldingsforhandsvisning valgt" : "meldingsforhandsvisning";
        var henvendelse = this.props.henvendelse;
        var melding = henvendelse.meldinger[0];
        var dato = henvendelse.datoInMillis || new Date();
        dato = moment(dato).format('LLL');

        var datoString = dato;
        if (!melding.erInngaaende) {
            datoString += " - " + melding.fraBruker;
        }

        return (
            <div {...this.props} className="sok-element" onClick={tekstChangedProxy(this.props.store, this.props.henvendelse)}>
                <input id={"melding" + this.props.henvendelse.key} name="tekstListeRadio" type="radio" readOnly checked={erValgt} />
                <label htmlFor={"melding" + this.props.henvendelse.key} className={cls}>
                    <header>
                        <p>{datoString}</p>
                        <div className={this.props.henvendelse.statusKlasse}></div>
                        <p className={'meldingstatus'}>{this.props.henvendelse.statusTekst}, {this.props.henvendelse.temagruppe}</p>
                    </header>
                    <p className="fritekst" dangerouslySetInnerHTML={{__html: this.props.henvendelse.innhold}}></p>
                </label>
            </div>
        );
    }
});

function tekstChangedProxy(store, henvendelse) {
    return function () {
        store.henvendelseChanged(henvendelse);
    };
}

function erValgtTekst(henvendelse, valgtHenvendelse) {
    return henvendelse === valgtHenvendelse;
}