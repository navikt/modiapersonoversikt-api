import React from 'react';

class PleiepengerettighetPanel extends React.Component {
    constructor(props) {
        super(props);
        console.log(props)
        this.state = {};

    }

    render() {
        return (
            <div>
                <h1>{this.props.tekst['title']}</h1>
                <dl>
                    <dt>{this.props.tekst['fraOgMedDato']}</dt>
                    <dd>{this.props.FOMDato}</dd>
                    <dt>{this.props.tekst['tilOgMedDato']}</dt>
                    <dd>{this.props.TOMDato}</dd>
                    <dt style={{width: '100%'}}>{this.props.tekst['forbruktEtterDennePerioden']}</dt>
                    <dd>1234</dd>
                    <dt></dt><dd></dd>
                    <dt>{this.props.tekst['kompensasjonsgrad']}</dt>
                    <dd>{this.props.kompensasjonsgrad}</dd>
                    <dt>{this.props.tekst['pleiepengegrad']}</dt>
                    <dd>{this.props.graderingsgrad}</dd>
                </dl>
            </div>
        );
    }
}

PleiepengerettighetPanel.propTypes = {
};

export default PleiepengerettighetPanel;
