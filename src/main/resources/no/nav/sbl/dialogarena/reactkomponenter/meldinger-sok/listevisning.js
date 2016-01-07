import React from 'react/addons';
import sanitize from 'sanitize-html';
import format from 'string-format';

function tekstChangedProxy() {
    this.props.store.traadChanged(this.props.traad, this.getDOMNode().parentNode);
}

function erValgtTekst(traad, valgtTraad) {
    return traad === valgtTraad;
}

const ListevisningKomponent = React.createClass({
    propTypes: {
        traad: React.PropTypes.object.isRequired,
        valgtTraad: React.PropTypes.object.isRequired
    },
    statics: {
        lagAriaLabel: function lagAriaLabel(traad) {
            return traad.temagruppe;
        }
    },
    render: function render() {
        const erValgt = erValgtTekst(this.props.traad, this.props.valgtTraad);
        const cls = erValgt ? 'meldingsforhandsvisning valgt' : 'meldingsforhandsvisning';
        const traad = this.props.traad;
        const dato = sanitize(traad.opprettetDato, {allowedTags: ['em']});

        let meldingsStatus = this.props.traad.statusTekst + ', ' + this.props.traad.temagruppe;
        meldingsStatus = sanitize(meldingsStatus, {allowedTags: ['em']});
        const innhold = sanitize(this.props.traad.innhold, {allowedTags: ['em']});

        const statusIkonTekst = format('{0}, {1} {2}',
                this.props.traad.statusKlasse.match(/ubesvart$/) ? 'Ubesvart' : 'Besvart',
                this.props.traad.antallMeldingerIOpprinneligTraad,
                this.props.traad.antallMeldingerIOpprinneligTraad === 1 ? 'melding' : 'meldinger'
            );

        return (
            <div className="sok-element" onClick={tekstChangedProxy.bind(this)}>
                <input id={'melding' + this.props.traad.key} name="tekstListeRadio" type="radio" readOnly checked={erValgt} />
                <label htmlFor={'melding' + this.props.traad.key} className={cls}>
                    <div className={this.props.traad.statusKlasse} aria-hidden="true"></div>
                    <p className="vekk">{statusIkonTekst}</p>
                    <p dangerouslySetInnerHTML={{__html: dato}}></p>
                    <p className={'meldingstatus'} dangerouslySetInnerHTML={{__html: meldingsStatus}}></p>
                    <p className="fritekst" dangerouslySetInnerHTML={{__html: innhold}}></p>
                </label>
            </div>
        );
    }
});

module.exports = ListevisningKomponent;
