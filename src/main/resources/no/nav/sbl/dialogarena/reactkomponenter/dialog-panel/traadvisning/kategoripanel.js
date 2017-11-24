import React from 'react';
import EkspanderbartpanelBase from '../../ekspanderbartpanelBase/ekspanderbartpanel-base'
import PT from 'prop-types';

function Kategoripanel(props) {
    const tittel = <h1 className="medium dialogpanel-header">{props.tittel}</h1>;

    return (
        <EkspanderbartpanelBase
            collapseProps={{ hasNestedCollapse: true }}
            className="kategoripanel kanaloverskrift"
            ariaTittel={`Ekspander ${props.tittel}`}
            heading={tittel}
            apen={props.apen}
        >
            {props.children}
        </EkspanderbartpanelBase>
    );
}

Kategoripanel.propTypes = {
    apen: PT.bool,
    tittel: PT.string.isRequired,
    children: PT.node.isRequired
};
Kategoripanel.defaultProps = {
    apen: false
};

export default Kategoripanel;
