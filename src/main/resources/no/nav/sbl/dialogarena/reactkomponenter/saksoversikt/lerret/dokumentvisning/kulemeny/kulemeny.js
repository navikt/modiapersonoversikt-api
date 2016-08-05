import React from 'react';
import { Button } from 'react-scroll';

const isInside = (y, elemTopBound, elemBottomBound) => {
    const midtpunktish = y + 650;
    return (midtpunktish >= elemTopBound && midtpunktish <= elemBottomBound);
};

const Kulemeny = ({ dokref, tittel, initialState }) => (
    <li>
        <Button
            activeClass="active"
            to={dokref}
            spy smooth
            duration={500}
            offset={-80}
            title={tittel}
            isInside={isInside}
            initialState={initialState}
            type="button"
            containerId="js-kulemeny-scroll"
        />
    </li>
);

Kulemeny.propTypes = {
    dokref: React.PropTypes.string.isRequired,
    tittel: React.PropTypes.string.isRequired
};

export default Kulemeny;
