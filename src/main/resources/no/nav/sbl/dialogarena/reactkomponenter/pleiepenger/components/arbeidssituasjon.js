import React from 'react';

import DLElement from '../dlelement';
import { formaterJavaDate, formaterBelop } from '../formatering-utils';

const ArbeidssituasjonPanel = ({ tekst, arbeidsgiver }) => (
    <div className="arbeidssituasjon">
        <h1 id="arbeidssituasjonTitle">{ tekst.arbeidssituasjon }</h1>
        <dl className="pleiepenger-detaljer">
            <DLElement etikett={tekst.arbeidsgiver} className="halvbredde">
                {arbeidsgiver.arbeidsgiverNavn}
            </DLElement>
            <DLElement etikett={tekst.kontonummer} className="halvbredde">
                {arbeidsgiver.arbeidsgiverKontonr}
            </DLElement>
            <DLElement etikett={tekst.inntektsperiode} className="halvbredde">
                {arbeidsgiver.inntektsperiode}
            </DLElement>
            <DLElement etikett={tekst.inntektForPerioden} className="halvbredde">
                {formaterBelop(arbeidsgiver.inntektForPerioden)}
            </DLElement>
            <DLElement etikett={tekst.refusjonstype} className="halvbredde">
                {arbeidsgiver.refusjonstype}
            </DLElement>
            <DLElement etikett={tekst.refusjonTilDato} className="halvbredde">
                {formaterJavaDate(arbeidsgiver.refusjonTom)}
            </DLElement>
        </dl>
    </div>
);

ArbeidssituasjonPanel.propTypes = {
    tekst: React.PropTypes.object.isRequired,
    arbeidsgiver: React.PropTypes.shape({
        arbeidsgiverNavn: React.PropTypes.string.isRequired,
        arbeidsgiverKontonr: React.PropTypes.string.isRequired,
        inntektsperiode: React.PropTypes.string.isRequired,
        refusjonstype: React.PropTypes.string.isRequired,
        refusjonTom: React.PropTypes.object.isRequired,
        inntektForPerioden: React.PropTypes.number.isRequired
    })
};

export default ArbeidssituasjonPanel;
