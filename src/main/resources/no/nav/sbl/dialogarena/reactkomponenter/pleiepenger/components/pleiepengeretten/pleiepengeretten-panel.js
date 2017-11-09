import React from 'react';
import PT from 'prop-types';
import DLElement from '../dlelement';
import { BarnetIkon } from './barnet-ikon';
import { formaterJavaDate, formaterOptionalProsentVerdi, emdash, kjonnFraIdent } from '../../utils';
import { javaDatoType } from '../../typer';

const Personnummer = ({ ident }) => {
    if (!ident || ident.length < 6) {
        return <span>{emdash}</span>;
    }

    return (
        <span>
            <span className="personnummer-margin">{ident.slice(0, 6)}</span>
            <span>{ident.slice(6)}</span>
        </span>
    );
};

Personnummer.propTypes = {
    ident: PT.string
};

const ProgressBar = ({ percent }) => (
    <div className="progress-bar-bg">
        <div style={{ width: percent + '%' }} className="progress-bar" />
    </div>
);

ProgressBar.propTypes = {
    percent: PT.number
};

const PleiepengerettighetPanel = props => {
    const tekst = props.tekst;
    const forbrukteDagerProsent = (100 * props.forbrukteDagerTOMIDag) / props.pleiepengedager;
    return (
        <div className="om-pleiepenger">
            <h1 id="pleiepengerettenTitle">{tekst.omPleiepengeretten}</h1>
            <div>
                <div>
                    <span className="pleiepenger-etikett">{tekst.barnetsDagkonto}</span>
                    <span className="pleiepenger-antall">{props.pleiepengedager}&nbsp;{tekst.dagerEnhet}</span>
                </div>
                <ProgressBar percent={forbrukteDagerProsent} />
                <div className="forbrukte-dager">
                    <span className="forbrukte-dager-verdi">{props.forbrukteDagerTOMIDag}</span>
                    <span className="forbrukte-dager-etikett">{tekst.forbrukteDagerPerIDag}</span>
                </div>
            </div>
            <dl className="pleiepenger-detaljer">
                <DLElement etikett={tekst.fraOgMedDato} className="halvbredde">
                    {formaterJavaDate(props.fomDato)}
                </DLElement>
                <DLElement etikett={tekst.tilOgMedDato} className="halvbredde">
                    {formaterJavaDate(props.tomDato)}
                </DLElement>
                <DLElement etikett={tekst.kompensasjonsgrad} className="halvbredde">
                    {formaterOptionalProsentVerdi(props.kompensasjonsgrad)}
                </DLElement>
                <DLElement etikett={tekst.pleiepengegrad} className="halvbredde">
                    {formaterOptionalProsentVerdi(props.pleiepengegrad)}
                </DLElement>
                <DLElement etikett={tekst.totaltInnvilget} className="fullbredde">
                    {props.totaltInnvilget}&nbsp;{tekst.dagerEnhet}
                </DLElement>
                <div className="blokk-s halvbredde">
                    <BarnetIkon kjonn={kjonnFraIdent(props.barnet)} />
                    <dt className="pleiepenger-etikett">{ tekst.barnet }</dt>
                    <dd className="pleiepenger-verdi">
                        <Personnummer ident={props.barnet} />
                    </dd>
                </div>
                <DLElement etikett={tekst.annenForelder} className="halvbredde">
                    <Personnummer ident={props.andreOmsorgsperson} />
                </DLElement>
            </dl>
        </div>
    );
};

PleiepengerettighetPanel.propTypes = {
    tekst: PT.shape({
        omPleiepengeretten: PT.string.isRequired,
        barnetsDagkonto: PT.string.isRequired,
        dagerEnhet: PT.string.isRequired,
        forbrukteDagerPerIDag: PT.string.isRequired,
        fraOgMedDato: PT.string.isRequired,
        tilOgMedDato: PT.string.isRequired,
        totaltInnvilget: PT.string.isRequired,
        kompensasjonsgrad: PT.string.isRequired,
        pleiepengegrad: PT.string.isRequired,
        barnet: PT.string.isRequired,
        annenForelder: PT.string.isRequired
    }).isRequired,
    fomDato: javaDatoType,
    tomDato: javaDatoType,
    pleiepengedager: PT.number.isRequired,
    forbrukteDagerTOMIDag: PT.number.isRequired,
    totaltInnvilget: PT.number.isRequired,
    kompensasjonsgrad: PT.number,
    pleiepengegrad: PT.number,
    barnet: PT.string.isRequired,
    andreOmsorgsperson: PT.string
};

export default PleiepengerettighetPanel;
