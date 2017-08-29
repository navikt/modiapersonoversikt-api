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
                </dl>
            </div>
        );
    }
}

PleiepengerettighetPanel.propTypes = {
};

export default PleiepengerettighetPanel;
