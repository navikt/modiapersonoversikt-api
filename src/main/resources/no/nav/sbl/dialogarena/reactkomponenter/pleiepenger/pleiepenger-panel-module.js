import React from 'react';
import moment from 'moment';

const Personnummer = ({ ident }) => (
    <span>
        <span style={{marginRight: '0.3rem'}}>{ ident.slice(0, 6) }</span>
        <span>{ ident.slice(6) }</span>
    </span>
);

const ProgressBar = ({ percent }) => (
    <div className="progress-bar-bg">
        <div style={{ width: percent + '%' }} className="progress-bar" />
    </div>
);

class PleiepengerettighetPanel extends React.Component {
    render() {
        const props = this.props;
        const tekst = props.tekst;
        return (
            <div>
                <h1>{ tekst['title'] }</h1>
                <div>
                    <div>
                        <span className="pleiepenger-etikett">{ tekst['barnetsDagkonto'] }</span>
                        <span className="pleiepenger-verdi">{ props.pleiepengedager }&nbsp;{ tekst['dagerEnhet'] }</span>
                    </div>
                    <ProgressBar percent={ 100 * props.forbrukteDagerTOMIDag / props.pleiepengedager } />
                    <div className="forbrukte-dager">
                        <span className="forbrukte-dager-verdi">{ props.forbrukteDagerTOMIDag }</span>
                        <span className="forbrukte-dager-etikett">{ tekst['forbrukteDagerPerIDag'] }</span>
                    </div>
                </div>
                <dl>
                    <dt>{ tekst['fraOgMedDato'] }</dt>
                    <dd>{ moment(props.FOMDato).format('DD.MM.YYYY') }</dd>
                    <dt>{ tekst['tilOgMedDato'] }</dt>
                    <dd>{ moment(props.TOMDato).format('DD.MM.YYYY') }</dd>
                    <dt style={{ width: '100%' }}>{ tekst['forbruktEtterDennePerioden'] }</dt>
                    <dd>{ props.forbrukteDagerEtterDennePerioden }</dd>
                    <dt style={{ height: '1.2rem' }} />
                    <dd />
                    <dt>{ tekst['kompensasjonsgrad'] }</dt>
                    <dd>{ props.kompensasjonsgrad  || '' }&nbsp;%</dd>
                    <dt>{ tekst['pleiepengegrad'] }</dt>
                    <dd>{ props.graderingsgrad || '' }&nbsp;%</dd>
                    <dt className="barnet-etikett">{ tekst['barnet'] }</dt>
                    <dd className="barnet-verdi">
                        <Personnummer ident={ props.barnet } />
                    </dd>
                    <dt>{ tekst['annenForelder'] }</dt>
                    <dd>
                        <Personnummer ident={ props.andreOmsorgsperson } />
                    </dd>
                </dl>
            </div>
        );
    }
}

PleiepengerettighetPanel.propTypes = {
    tekst: React.PropTypes.object.isRequired,
    FOMDato: React.PropTypes.string,
    TOMDato: React.PropTypes.string,
    pleiepengedager: React.PropTypes.number.isRequired,
    forbrukteDagerTOMIDag: React.PropTypes.number.isRequired,
    forbrukteDagerEtterDennePerioden: React.PropTypes.number.isRequired,
    kompensasjonsgrad: React.PropTypes.number,
    graderingsgrad: React.PropTypes.number,
    barnet: React.PropTypes.string.isRequired,
    andreOmsorgsperson: React.PropTypes.string,
};

export default PleiepengerettighetPanel;
