import React from 'react';
import format from 'string-format';

class Snurrepipp extends React.Component {
    render() {
        const src = format('/modiabrukerdialog/img/ajaxloader/{}/loader_{}_{}.gif', this.props.farge, this.props.farge, this.props.storrelse);
        return (
            <div className="snurrepipp">
                <img src={src} />
            </div>
        );
    }
}

Snurrepipp.propTypes = {
    'storrelse': React.PropTypes.number,
    'farge': React.PropTypes.string
};

Snurrepipp.defaultProps = {
    'storrelse': 128,
    'farge': 'graa'
};

export default Snurrepipp;
