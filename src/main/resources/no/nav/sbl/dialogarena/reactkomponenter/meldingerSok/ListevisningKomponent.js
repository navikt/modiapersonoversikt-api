var React = require('react');
var sanitize = require('sanitize-html');
var format = require('string-format');

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
        var statusIkonTekst = format('{0}, {1}, {2}',
            this.props.traad.statusKlasse.match(/ubesvart$/) ? 'Ubesvart' : 'Besvart',
            dato,
            meldingsStatus
        );

        return (
            <div className="sok-element" onClick={tekstChangedProxy.bind(this)}>
                <input id={"melding" + this.props.traad.key} name="tekstListeRadio" type="radio" readOnly checked={erValgt} />
                <label htmlFor={"melding" + this.props.traad.key} className={cls}>
                    <header>
                        <p dangerouslySetInnerHTML={{__html: dato}} aria-hidden="true"></p>
                        <span className="vekk">{statusIkonTekst}</span>
                        <div className={this.props.traad.statusKlasse} aria-hidden="true"></div>
                        <p className={'meldingstatus'} dangerouslySetInnerHTML={{__html: meldingsStatus}} aria-hidden="true"></p>
                    </header>
                    <p className="fritekst" dangerouslySetInnerHTML={{__html: innhold}} aria-hidden="true"></p>
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