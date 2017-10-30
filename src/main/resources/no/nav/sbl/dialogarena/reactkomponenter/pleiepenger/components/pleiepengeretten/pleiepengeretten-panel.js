import React from 'react';
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
    ident: React.PropTypes.string
};

const ProgressBar = ({ percent }) => (
    <div className="progress-bar-bg">
        <div style={{ width: percent + '%' }} className="progress-bar" />
    </div>
);

ProgressBar.propTypes = {
    percent: React.PropTypes.number
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
                <DLElement etikett={tekst.forbruktEtterDennePerioden} className="fullbredde">
                    {props.forbrukteDagerEtterDennePerioden }&nbsp;{tekst.dagerEnhet}
                </DLElement>
                <DLElement etikett={tekst.kompensasjonsgrad} className="halvbredde">
                    {formaterOptionalProsentVerdi(props.kompensasjonsgrad)}
                </DLElement>
                <DLElement etikett={tekst.pleiepengegrad} className="halvbredde">
                    {props.pleiepengegrad || ''}&nbsp;%
                </DLElement>
                <div className="blokk-s halvbredde">
                    <BarnetIkon kjonn={kjonnFraIdent(props.barnet)} />
                    <dt className="pleiepenger-barnet-etikett">{ tekst.barnet }</dt>
                    <dd className="pleiepenger-verdi pleiepenger-barnet-ident">
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
    tekst: React.PropTypes.object.isRequired,
    fomDato: javaDatoType.isRequired,
    tomDato: javaDatoType.isRequired,
    pleiepengedager: React.PropTypes.number.isRequired,
    forbrukteDagerTOMIDag: React.PropTypes.number.isRequired,
    forbrukteDagerEtterDennePerioden: React.PropTypes.number.isRequired,
    kompensasjonsgrad: React.PropTypes.number,
    pleiepengegrad: React.PropTypes.number.isRequired,
    barnet: React.PropTypes.string.isRequired,
    andreOmsorgsperson: React.PropTypes.string
};

export default PleiepengerettighetPanel;
