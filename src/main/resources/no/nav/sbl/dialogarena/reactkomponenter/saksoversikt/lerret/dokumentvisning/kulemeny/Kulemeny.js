import React from 'react';
import { Button } from 'react-scroll';

const isInside = (y, elemTopBound, elemBottomBound, containerCoords, elementCoords) => {
    const midtpunktish = y + 650;
    return (midtpunktish >= elemTopBound && midtpunktish <= elemBottomBound);
};

const Kulemeny = ({ dokref, tittel, initalState }) => (
    <li>
        <Button
            activeClass="active"
            to={dokref}
            spy smooth
            duration={500}
            offset={-80}
            title={tittel}
            isInside={isInside}
            initalState={initalState}
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
