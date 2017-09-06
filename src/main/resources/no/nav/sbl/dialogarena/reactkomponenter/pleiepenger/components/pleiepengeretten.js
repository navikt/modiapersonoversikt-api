import React from 'react';
import DLElement from '../dlelement';
import { formaterJavaDate } from '../formatering-utils';

const Personnummer = ({ ident }) =>{
    if (!ident || ident.length <= 6) {
        return <span/>;
    }

    return (
        <span>
        <span className="personnummer-margin">{ ident.slice(0, 6) }</span>
        <span>{ ident.slice(6) }</span>
    </span>
    );
};

const ProgressBar = ({ percent }) => (
    <div className="progress-bar-bg">
        <div style={{ width: percent + '%' }} className="progress-bar" />
    </div>
);

const PleiepengerettighetPanel = props => {
    const tekst = props.tekst;
    const forbrukteDagerProsent = 100 * props.forbrukteDagerTOMIDag / props.pleiepengedager;
    console.log(props);
    return (
        <div className="om-pleiepenger">
            <h1 id="pleiepengerettenTitle">{ tekst.omPleiepengeretten }</h1>
            <div>
                <div>
                    <span className="pleiepenger-etikett">{ tekst.barnetsDagkonto }</span>
                    <span className="pleiepenger-antall">{ props.pleiepengedager }&nbsp;{ tekst.dagerEnhet }</span>
                </div>
                <ProgressBar percent={ forbrukteDagerProsent } />
                <div className="forbrukte-dager">
                    <span className="forbrukte-dager-verdi">{ props.forbrukteDagerTOMIDag }</span>
                    <span className="forbrukte-dager-etikett">{ tekst.forbrukteDagerPerIDag }</span>
                </div>
            </div>
            <dl className="pleiepenger-detaljer">
                <DLElement etikett={ tekst.fraOgMedDato } className="halvbredde">
                    {formaterJavaDate(props.fomDato)}
                </DLElement>
                <DLElement etikett={ tekst.tilOgMedDato } className="halvbredde">
                    {formaterJavaDate(props.tomDato)}
                </DLElement>
                <DLElement etikett={ tekst.forbruktEtterDennePerioden } className="fullbredde">
                    {props.forbrukteDagerEtterDennePerioden }&nbsp;{ tekst.dagerEnhet }
                </DLElement>
                <DLElement etikett={ tekst.kompensasjonsgrad } className="halvbredde">
                    { props.kompensasjonsgrad || '' }&nbsp;%
                </DLElement>
                <DLElement etikett={ tekst.pleiepengegrad } className="halvbredde">
                    { props.graderingsgrad || '' }&nbsp;%
                </DLElement>
                <DLElement etikett={ tekst.barnet } className="halvbredde">
                    <Personnummer ident={ props.barnet } />
                </DLElement>
                <DLElement etikett={ tekst.annenForelder } className="halvbredde">
                    <Personnummer ident={props.andreOmsorgsperson} />
                </DLElement>
            </dl>
        </div>
    );
};

PleiepengerettighetPanel.propTypes = {
    tekst: React.PropTypes.object.isRequired,
    fomDato: React.PropTypes.object.isRequired,
    tomDato: React.PropTypes.object.isRequired,
    pleiepengedager: React.PropTypes.number.isRequired,
    forbrukteDagerTOMIDag: React.PropTypes.number.isRequired,
    forbrukteDagerEtterDennePerioden: React.PropTypes.number.isRequired,
    kompensasjonsgrad: React.PropTypes.number,
    graderingsgrad: React.PropTypes.number.isRequired,
    barnet: React.PropTypes.string.isRequired,
    andreOmsorgsperson: React.PropTypes.string,
};

export default PleiepengerettighetPanel;
