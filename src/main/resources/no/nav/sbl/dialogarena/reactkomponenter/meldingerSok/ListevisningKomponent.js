var React = require('react');
var sanitize = require('sanitize-html');

var ListevisningKomponent = React.createClass({
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
        var dato = sanitize(traad.opprettetDato, {allowedTags: ['em']});

        var meldingsStatus = this.props.traad.statusTekst + ", " + this.props.traad.temagruppe;
        meldingsStatus = sanitize(meldingsStatus, {allowedTags: ['em']});
        var innhold = sanitize(this.props.traad.innhold, {allowedTags: ['em']});

        return (
            <div className="sok-element" onClick={tekstChangedProxy.bind(this)}>
                <input id={"melding" + this.props.traad.key} name="tekstListeRadio" type="radio" readOnly checked={erValgt} />
                <label htmlFor={"melding" + this.props.traad.key} className={cls}>
                    <header>
                        <p dangerouslySetInnerHTML={{__html: dato}}></p>
                        <div className={this.props.traad.statusKlasse}></div>
                        <p className={'meldingstatus'} dangerouslySetInnerHTML={{__html: meldingsStatus}}></p>
                    </header>
                    <p className="fritekst" dangerouslySetInnerHTML={{__html: innhold}}></p>
                </label>
            </div>
        );
    }
});

function tekstChangedProxy() {
    this.props.store.traadChanged(this.props.traad, this.getDOMNode().parentNode);
}

function erValgtTekst(traad, valgtTraad) {
    return traad === valgtTraad;
}

module.exports = ListevisningKomponent;