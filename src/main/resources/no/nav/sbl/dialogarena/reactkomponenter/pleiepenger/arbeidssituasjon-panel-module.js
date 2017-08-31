import React from 'react';
import moment from 'moment';
import DLElement from './dlelement';
import { formaterJavaDate, formaterBelop } from './formatering-utils';

const ArbeidssituasjonPanel = ({ tekst, arbeidsgiver, kontonummer, inntektsperiode,
                                   inntektForPerioden, refusjonstype, refusjonTilDato }) => (
        <div>
            <h1 id="arbeidssituasjonTitle">{ tekst['arbeidssituasjon'] }</h1>
            <dl className="pleiepenger-detaljer">
                <DLElement etikett={ tekst['arbeidsgiver'] } className="halvbredde">
                    { arbeidsgiver }
                </DLElement>
                <DLElement etikett={ tekst['kontonummer'] } className="halvbredde">
                    { kontonummer }
                </DLElement>
                <DLElement etikett={ tekst['inntektsperiode'] } className="halvbredde">
                    { inntektsperiode }
                </DLElement>
                <DLElement etikett={ tekst['inntektForPerioden'] } className="halvbredde">
                    { formaterBelop(inntektForPerioden) }
                </DLElement>
                <DLElement etikett={ tekst['refusjonstype'] } className="halvbredde">
                    { refusjonstype }
                </DLElement>
                <DLElement etikett={ tekst['refusjonTilDato'] } className="halvbredde">
                    { formaterJavaDate(refusjonTilDato) }
                </DLElement>
            </dl>
        </div>
);

ArbeidssituasjonPanel.propTypes = {
    tekst: React.PropTypes.object.isRequired,
    arbeidsgiver: React.PropTypes.string.isRequired,
    kontonummer: React.PropTypes.string,
    inntektsperiode: React.PropTypes.string,
    inntektForPerioden: React.PropTypes.number,
    refusjonstype: React.PropTypes.string,
    refusjonTilDato: React.PropTypes.object,
};

export default ArbeidssituasjonPanel;
