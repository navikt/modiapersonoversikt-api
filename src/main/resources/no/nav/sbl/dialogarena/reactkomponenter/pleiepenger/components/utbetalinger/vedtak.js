import React from 'react';

import DLElement from '../dlelement';
import { formaterJavaDate, formaterBelop, formaterOptionalProsentVerdi } from '../../utils';
import { javaDatoType } from '../../typer';

const Vedtak = ({ vedtak, tekst }) => {
    const fraOgMed = formaterJavaDate(vedtak.periode.fraOgMed);
    const tilOgMed = formaterJavaDate(vedtak.periode.tilOgMed);
    const anvistUtbetaling = formaterJavaDate(vedtak.anvistUtbetaling);
    return (
        <li>
            <dl className="pleiepenger-detaljer">
                <DLElement etikett={tekst.fraOgMedDato} className="halvbredde">
                    {fraOgMed}
                </DLElement>
                <DLElement etikett={tekst.tilOgMedDato} className="halvbredde">
                    {tilOgMed}
                </DLElement>
                <DLElement etikett={tekst.bruttoBelop} className="halvbredde">
                    {formaterBelop(vedtak.bruttoBelop)}
                </DLElement>
                <DLElement etikett={tekst.anvistUtbetaling} className="halvbredde">
                    {anvistUtbetaling}
                </DLElement>
                <DLElement etikett={tekst.dagsats} className="halvbredde">
                    {formaterBelop(vedtak.dagsats)}
                </DLElement>
                <DLElement etikett={tekst.kompensasjonsgrad} className="halvbredde">
                    {formaterOptionalProsentVerdi(vedtak.kompensasjonsgrad)}
                </DLElement>
                <DLElement etikett={tekst.pleiepengegrad} className="halvbredde">
                    {formaterOptionalProsentVerdi(vedtak.pleiepengegrad)}
                </DLElement>
            </dl>
        </li>
    );
};

Vedtak.propTypes = {
    vedtak: React.PropTypes.shape({
        periode: React.PropTypes.shape({
            fraOgMed: javaDatoType.isRequired,
            tilOgMed: javaDatoType.isRequired
        }).isRequired,
        anvistUtbetaling: javaDatoType.isRequired,
        bruttoBelop: React.PropTypes.number.isRequired,
        pleiepengegrad: React.PropTypes.number.isRequired,
        kompensasjonsgrad: React.PropTypes.number
    }),
    tekst: React.PropTypes.object.isRequired
};

export default Vedtak;
