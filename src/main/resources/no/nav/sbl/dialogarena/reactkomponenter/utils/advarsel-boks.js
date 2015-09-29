import React from 'react';

class AdvarselBoks extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="advarsel-boks warn">
                <p>{this.props.tekst}</p>
            </div>
        );
    }
}

export default AdvarselBoks;