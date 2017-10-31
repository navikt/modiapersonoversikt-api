import React from 'react';
import PT from 'prop-types';

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
    vedtak: PT.shape({
        periode: PT.shape({
            fraOgMed: javaDatoType.isRequired,
            tilOgMed: javaDatoType.isRequired
        }).isRequired,
        anvistUtbetaling: javaDatoType.isRequired,
        bruttoBelop: PT.number.isRequired,
        pleiepengegrad: PT.number.isRequired,
        kompensasjonsgrad: PT.number
    }),
    tekst: PT.object.isRequired
};

export default Vedtak;
