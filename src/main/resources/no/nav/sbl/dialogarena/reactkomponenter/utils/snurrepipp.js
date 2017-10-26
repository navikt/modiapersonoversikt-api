import React from 'react';
import PT from 'prop-types';
import format from 'string-format';

class Snurrepipp extends React.Component {
    render() {
        const src = format(
            '/modiabrukerdialog/img/ajaxloader/{}/loader_{}_{}.gif',
            this.props.farge,
            this.props.farge,
            this.props.storrelse
        );
        return (
            <div className="snurrepipp">
                <img src={src} role="presentation" />
            </div>
        );
    }
}

Snurrepipp.propTypes = {
    storrelse: PT.number,
    farge: PT.string
};

Snurrepipp.defaultProps = {
    storrelse: 128,
    farge: 'graa'
};

export default Snurrepipp;
