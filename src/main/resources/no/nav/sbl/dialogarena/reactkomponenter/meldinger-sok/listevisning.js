import React from "react";
import sanitize from "sanitize-html";
import format from "string-format";

function tekstChangedProxy() {
    this.props.store.traadChanged(this.props.traad, React.findDOMNode(this).parentNode);
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
        const { traad, valgtTraad } = this.props;
        const erValgt = erValgtTekst(traad, valgtTraad);
        const cls = erValgt ? 'meldingsforhandsvisning valgt' : 'meldingsforhandsvisning';
        const dato = sanitize(traad.opprettetDato, { allowedTags: ['em'] });

        let meldingsStatus = traad.statusTekst + (!traad.temagruppe ? '' : `, ${traad.temagruppe}`);
        meldingsStatus = sanitize(meldingsStatus, { allowedTags: ['em'] });
        const innhold = sanitize(traad.innhold, { allowedTags: ['em'] });

        const statusIkonTekst = format('{0}, {1} {2}',
                traad.statusKlasse.match(/ubesvart$/) ? 'Ubesvart' : 'Besvart',
                traad.antallMeldingerIOpprinneligTraad,
                traad.antallMeldingerIOpprinneligTraad === 1 ? 'melding' : 'meldinger'
        );

        return (
            <div className="sok-element" onClick={tekstChangedProxy.bind(this)}>
                <input id={`melding ${traad.key}`} name="tekstListeRadio" type="radio" readOnly checked={erValgt} />
                <label htmlFor={`melding ${traad.key}`} className={cls}>
                    <div className={traad.statusKlasse} aria-hidden="true"></div>
                    <p className="vekk">{statusIkonTekst}</p>
                    <p>{dato}></p>
                    <p className={'meldingstatus'}>{meldingsStatus}</p>
                    <p className="fritekst" dangerouslySetInnerHTML={{ __html: innhold }}></p>
                </label>
            </div>
        );
    }
});

module.exports = ListevisningKomponent;
