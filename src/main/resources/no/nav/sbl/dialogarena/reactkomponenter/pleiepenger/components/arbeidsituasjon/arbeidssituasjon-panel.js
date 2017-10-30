import React from 'react';

import { javaDatoType } from '../../typer';
import DLElement from '../dlelement';
import {
    formaterJavaDate, formaterBelop, emdash, formaterOptionalVerdi, konverterTilMomentDato
} from '../../utils';

const ArbeidsforholdKomponent = ({ arbeidsforhold, tekst }) => (
    <div className="arbeidsforhold">
        <dl className="pleiepenger-detaljer">
            <DLElement etikett={tekst.arbeidsgiver} className="halvbredde">
                {formaterOptionalVerdi(arbeidsforhold.arbeidsgiverNavn)}
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
                {arbeidsforhold.refusjonTom
                    ? formaterJavaDate(arbeidsforhold.refusjonTom)
                    : emdash}
            </DLElement>
        </dl>
    </div>
);

ArbeidsforholdKomponent.propTypes = {
    tekst: React.PropTypes.object.isRequired,
    arbeidsforhold: React.PropTypes.shape({
        arbeidsgiverNavn: React.PropTypes.string,
        arbeidsgiverKontonr: React.PropTypes.string.isRequired,
        inntektsperiode: React.PropTypes.string.isRequired,
        refusjonstype: React.PropTypes.string.isRequired,
        refusjonTom: javaDatoType,
        inntektForPerioden: React.PropTypes.number.isRequired
    }).isRequired
};

export const sorterArbeidsforhold = arbeidsforhold => {
    const gamleDager = konverterTilMomentDato('1900-01-01');
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

        if (!this.state.visFullListe) {
            arbeidsforholdKomponenter = arbeidsforholdKomponenter.slice(0, 1);
        }

        return (
            <div className="arbeidssituasjon">
                <h1 id="arbeidssituasjonTitle">{props.tekst.arbeidssituasjon}</h1>
                {arbeidsforholdKomponenter}
                <a className="toggle-arbeidsforhold" onClick={this.handleToggleListeClick}>
                    {this.state.visFullListe ? 'Skjul alle arbeidsforhold' : 'Vis alle arbeidsforhold'}
                </a>
            </div>
        );
    }
}

ArbeidssituasjonPanel.propTypes = {
    tekst: React.PropTypes.object.isRequired,
    arbeidsforhold: React.PropTypes.array.isRequired
};

export default ArbeidssituasjonPanel;
