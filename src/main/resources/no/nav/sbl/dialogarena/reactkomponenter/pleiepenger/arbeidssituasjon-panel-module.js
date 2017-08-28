import React from 'react';

class ArbeidssituasjonPanel extends React.Component {
    constructor(props) {
        super(props);
        console.log(props)
        this.state = {
        };

    }

    render() {
        return (
            <div>
                <h1>{this.props.tekst['title']}</h1>
                <dl>
                    <dt>{this.props.tekst['arbeidsgiver']}</dt>
                    <dd>{this.props.arbeidsgiver}</dd>
                </dl>
            </div>
        );
    }
}

ArbeidssituasjonPanel.propTypes = {
};

export default ArbeidssituasjonPanel;
