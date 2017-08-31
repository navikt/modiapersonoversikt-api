import React from 'react';
import moment from 'moment';
import DLElement from './dlelement';
import formaterJavaDate from './dato-utils';

const ArbeidssituasjonPanel = ({ tekst, arbeidsgiver, kontonummer, inntektsperiode,
                                   inntektForPerioden, refusjonstype, refusjonTilDato }) => {
    const formatertInntektForPerioden = inntektForPerioden
        .toLocaleString('nb-NO', {style: 'currency', currency: 'NOK', currencyDisplay: 'code'});
    return (
        <div>
            <h1 id="arbeidssituasjonTitle">{ tekst['title'] }</h1>
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
                    { formatertInntektForPerioden }
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
};

ArbeidssituasjonPanel.propTypes = {
    tekst: React.PropTypes.object.isRequired,
    arbeidsgiver: React.PropTypes.string.isRequired,
    kontonummer: React.PropTypes.string,
    inntektsperiode: React.PropTypes.string,
    inntektForPerioden: React.PropTypes.number,
    refusjonstype: React.PropTypes.string,
    refusjonTilDato: React.PropTypes.string,
};

export default ArbeidssituasjonPanel;
