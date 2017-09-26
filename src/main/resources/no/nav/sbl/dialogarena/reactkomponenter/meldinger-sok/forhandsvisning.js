import React from 'react';
import Melding from './melding';
import DokumentMelding from './dokument-melding';
import OppgaveMelding from './oppgave-melding';
import ScrollPortal from './../utils/scroll-portal';

function Forhandsvisning(props) {
    if (!props.traad.hasOwnProperty('meldinger')) {
        return <noscript />;
    }

    const { traad, traad: { meldinger } } = props;

    const meldingElementer = meldinger.map((melding) => {
        if (melding.erDokumentMelding) {
            return <DokumentMelding key={melding.id} melding={melding} />;
        }
        if (melding.erOppgaveMelding) {
            return <OppgaveMelding key={melding.id} melding={melding} />;
        }
        return <Melding key={melding.id} melding={melding} />;
    });

    const meldingBenevnelse = traad.antallMeldingerIOpprinneligTraad === 1 ? 'melding' : 'meldinger';
    const antallInformasjon = `Viser <b>${meldinger.length}</b> av 
                <b>${traad.antallMeldingerIOpprinneligTraad}</b> ${meldingBenevnelse} i dialogen`;

    return (
        <div>
            <ScrollPortal className="traadPanel" innerClassName="traad-panel-wrapper">
                <div className="traadinfo">
                    <span dangerouslySetInnerHTML={{ __html: antallInformasjon }}></span>
                </div>
                <div>{meldingElementer}</div>
            </ScrollPortal>
            <div className="velgPanel">
                <input type="submit" value="Vis dialog" className="knapp-hoved-liten" />
            </div>
        </div>
    );
}

Forhandsvisning.propTypes = {
    traad: React.PropTypes.shape({
        meldinger: React.PropTypes.array,
        antallMeldingerIOpprinneligTraad: React.PropTypes.number
    }).isRequired
};

export default Forhandsvisning;
