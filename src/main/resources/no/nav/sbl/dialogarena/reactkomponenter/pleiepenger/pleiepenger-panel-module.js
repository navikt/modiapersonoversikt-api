import React from 'react';

class PleiepengerPanel extends React.Component {
    constructor(props) {
        super(props);
        console.log(props)
        this.state = {
            saker: []
        };

    }

    render() {
        return (
            <h3>Hallo!!!!!</h3>
        );
    }
}

PleiepengerPanel.propTypes = {
};

export default PleiepengerPanel;
