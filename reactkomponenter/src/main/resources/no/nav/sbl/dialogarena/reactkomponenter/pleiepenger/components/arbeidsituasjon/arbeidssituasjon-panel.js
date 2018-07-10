import React from 'react';
import PT from 'prop-types';

import { javaDatoType } from '../../typer';
import DLElement from '../dlelement';
import {
    formaterJavaDate, formaterBelop, formaterOptionalVerdi, konverterTilMomentDato
} from '../../utils';

const arbeidssituasjonTekstShape = PT.shape({
    arbeidssituasjon: PT.string.isRequired,
    arbeidskategori: PT.string.isRequired,
    arbeidsgiver: PT.string.isRequired,
    kontonummer: PT.string.isRequired,
    inntektsperiode: PT.string.isRequired,
    inntektForPerioden: PT.string.isRequired,
    refusjonstype: PT.string.isRequired,
    refusjonTilDato: PT.string.isRequired
});

const ArbeidsforholdKomponent = ({ arbeidsforhold, tekst }) => (
    <div className="arbeidsforhold">
        <dl className="pleiepenger-detaljer">
            <DLElement etikett={tekst.arbeidsgiver} className="halvbredde">
                {formaterOptionalVerdi(arbeidsforhold.arbeidsgiverNavn)}
            </DLElement>
            <DLElement etikett={tekst.arbeidskategori} className="halvbredde">
                {arbeidsforhold.arbeidskategori}
            </DLElement>
            <DLElement etikett={tekst.kontonummer} className="halvbredde">
                {formaterOptionalVerdi(arbeidsforhold.arbeidsgiverKontonr)}
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

ArbeidsforholdKomponent.propTypes = {
    tekst: arbeidssituasjonTekstShape.isRequired,
    arbeidsforhold: PT.shape({
        arbeidskategori: PT.string.isRequired,
        arbeidsgiverNavn: PT.string,
        arbeidsgiverKontonr: PT.string.isRequired,
        inntektsperiode: PT.string.isRequired,
        refusjonstype: PT.string.isRequired,
        refusjonTom: javaDatoType,
        inntektForPerioden: PT.number.isRequired
    }).isRequired
};

export const sorterArbeidsforhold = arbeidsforhold => {
    const gamleDager = konverterTilMomentDato({ year: 1900, month: 1, day: 1 });
    return arbeidsforhold.sort((a, b) => (
        konverterTilMomentDato(b.refusjonTom || gamleDager).diff(konverterTilMomentDato(a.refusjonTom || gamleDager))
    ));
};

class ArbeidssituasjonPanel extends React.Component {
    constructor(props) {
        super(props);
        this.state = { visFullListe: false };
        this.handleToggleListeClick = this.handleToggleListeClick.bind(this);
    }

    handleToggleListeClick() {
        this.setState(prevState => ({
            visFullListe: !prevState.visFullListe
        }));
    }
    render() {
        const props = this.props;

        let arbeidsforholdKomponenter = sorterArbeidsforhold(props.arbeidsforhold).map((forhold, index) =>
            (<ArbeidsforholdKomponent key={index} arbeidsforhold={forhold} tekst={props.tekst} />));
        const antallArbeidsforhold = arbeidsforholdKomponenter.length;

        if (!this.state.visFullListe) {
            arbeidsforholdKomponenter = arbeidsforholdKomponenter.slice(0, 1);
        }

        const toggleAlleArbeidsforhold = (
            <a className="toggle-arbeidsforhold" onClick={this.handleToggleListeClick}>
                {this.state.visFullListe ? 'Skjul' : 'Vis alle arbeidsforhold'}
            </a>
        );

        return (
            <div className="arbeidssituasjon">
                <h1 id="arbeidssituasjonTitle">{props.tekst.arbeidssituasjon}</h1>
                {arbeidsforholdKomponenter}
                {antallArbeidsforhold > 1 && toggleAlleArbeidsforhold}
            </div>
        );
    }
}

ArbeidssituasjonPanel.propTypes = {
    tekst: arbeidssituasjonTekstShape.isRequired,
    arbeidsforhold: PT.array.isRequired
};

export default ArbeidssituasjonPanel;
