import React from 'react';

class PleiepengerettighetPanel extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const props = this.props;
        const tekst = props.tekst;
        return (
            <div>
                <h1>{ tekst['title'] }</h1>
                <div>
                    <div>
                        <span className="pleiepenger-etikett">{ tekst['barnetsDagkonto'] }</span>
                        <span className="pleiepenger-verdi">{ props.pleiepengedager }</span>
                    </div>
                    <div>
                        <span>{ props.forbrukteDagerTOMIDag }</span>
                        <span>{ tekst['forbrukteDagerPerIDag'] }</span>
                    </div>
                </div>
                <dl>
                    <dt>{ tekst['fraOgMedDato'] }</dt>
                    <dd>{ props.FOMDato }</dd>
                    <dt>{ tekst['tilOgMedDato'] }</dt>
                    <dd>{ props.TOMDato }</dd>
                    <dt style={{width: '100%'}}>{ tekst['forbruktEtterDennePerioden'] }</dt>
                    <dd>1234</dd>
                    <dt></dt><dd></dd>
                    <dt>{ tekst['kompensasjonsgrad'] }</dt>
                    <dd>{ props.kompensasjonsgrad }</dd>
                    <dt>{ tekst['pleiepengegrad'] }</dt>
                    <dd>{ props.graderingsgrad }</dd>
                    <dt>{ tekst['barnet'] }</dt>
                    <dd>{ props.barnet }</dd>
                    <dt>{ tekst['annenForelder'] }</dt>
                    <dd>{ props.andreOmsorgsperson }</dd>
                </dl>
            </div>
        );
    }
}

PleiepengerettighetPanel.propTypes = {
    tekst: React.PropTypes.object.isRequired,
    FOMDato: React.PropTypes.string.isRequired,
    TOMDato: React.PropTypes.string.isRequired,

};

export default PleiepengerettighetPanel;
