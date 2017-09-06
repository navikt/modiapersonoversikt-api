import React from 'react';

import DLElement from '../dlelement';
import { formaterJavaDate, formaterBelop } from '../formatering-utils';

const ArbeidsforholdKomponent = ({arbeidsforhold, tekst}) => (
    <div className="arbeidsforhold">
        <dl className="pleiepenger-detaljer">
            <DLElement etikett={tekst.arbeidsgiver} className="halvbredde">
                {arbeidsforhold.arbeidsgiverNavn}
            </DLElement>
            <DLElement etikett={tekst.kontonummer} className="halvbredde">
                {arbeidsforhold.arbeidsgiverKontonr}
            </DLElement>
            <DLElement etikett={tekst.inntektsperiode} className="halvbredde">
                {arbeidsforhold.inntektsperiode}
            </DLElement>
            <DLElement etikett={tekst.inntektForPerioden} className="halvbredde">
                {formaterBelop(arbeidsforhold.inntektForPerioden)}
            </DLElement>
            <DLElement etikett={tekst.refusjonstype} className="halvbredde">
                {arbeidsforhold.refusjonstype}
            </DLElement>
            <DLElement etikett={tekst.refusjonTilDato} className="halvbredde">
                {formaterJavaDate(arbeidsforhold.refusjonTom)}
            </DLElement>
        </dl>
    </div>
);

const ArbeidssituasjonPanel = ({tekst, arbeidsforhold}) => {
    const arbeidsforholdKomponenter = arbeidsforhold.map((forhold, index) =>
        (<ArbeidsforholdKomponent key={index} arbeidsforhold={forhold} tekst={tekst}/>));

    return (
        <div className="arbeidssituasjon">
            <h1 id="arbeidssituasjonTitle">{tekst.arbeidssituasjon}</h1>
            {arbeidsforholdKomponenter}
        </div>
    );
};

ArbeidssituasjonPanel.propTypes = {
    tekst: React.PropTypes.object.isRequired,
    arbeidsforhold: React.PropTypes.arrayOf(React.PropTypes.shape({
        arbeidsgiverNavn: React.PropTypes.string.isRequired,
        arbeidsgiverKontonr: React.PropTypes.string.isRequired,
        inntektsperiode: React.PropTypes.string.isRequired,
        refusjonstype: React.PropTypes.string.isRequired,
        refusjonTom: React.PropTypes.object.isRequired,
        inntektForPerioden: React.PropTypes.number.isRequired
    })).isRequired
};

export default ArbeidssituasjonPanel;
