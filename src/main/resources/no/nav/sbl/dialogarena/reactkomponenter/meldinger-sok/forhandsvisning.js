import React from 'react';
import PT from 'prop-types';
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
    const antallInformasjon = (<span>
        Viser <b>{meldinger.length}</b> av <b>{traad.antallMeldingerIOpprinneligTraad}</b> {meldingBenevnelse} i dialogen
    </span>);

    return (
        <div>
            <ScrollPortal className="traadPanel" innerClassName="traad-panel-wrapper">
                <div className="traadinfo">
                    {antallInformasjon}
                </div>
                <div>{meldingElementer}</div>
            </ScrollPortal>
            <div className="velgPanel">
                <input type="submit"
                       value={props.store.state.mode.submitButtonValue}
                       className="knapp-hoved-liten"
                />
                {props.store.state.submitError}
            </div>
        </div>
    );
}

Forhandsvisning.propTypes = {
    traad: PT.shape({
        meldinger: PT.array,
        antallMeldingerIOpprinneligTraad: PT.number
    }).isRequired,
    store: PT.object.isRequired
};

export default Forhandsvisning;
