import PT from 'prop-types';
import React, { Component } from 'react';
import 'nav-frontend-ekspanderbartpanel-style'; // eslint-disable-line import/extensions
import EkspanderbartpanelBasePure from './ekspanderbartpanel-base-pure';

class EkspanderbartpanelBase extends Component {
    constructor(props) {
        super(props);

        this.state = {
            apen: this.props.apen
        };

        this.handleClick = this.handleClick.bind(this);
    }

    handleClick(event) {
        event.preventDefault();
        this.setState({ apen: !this.state.apen });
        this.props.onClick(event);
    }

    render() {
        const { apen: _apen, ...renderProps } = this.props;
        return (
            <EkspanderbartpanelBasePure {...renderProps} apen={this.state.apen} onClick={this.handleClick} />
        );
    }
}

EkspanderbartpanelBase.propTypes = {
    apen: PT.bool,
    onClick: PT.func
};
EkspanderbartpanelBase.defaultProps = {
    apen: false,
    onClick: () => {}
};

export default EkspanderbartpanelBase;
