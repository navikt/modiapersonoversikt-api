import React from 'react';

export const BarnetIkon = ({ kjonn }) => (
    <div className="pleiepenger-barnet-ikon">
        <svg height="100%" viewBox="0 0 18 24">
            <g fill={{ K: '#C86151', M: '#3385D1', U: '#38a161' }[kjonn]}>
                <path
                    d="M3.581,20 L18,20 L18,23.5 C18,23.776 17.776,24 17.5,24 L0.5,24 C0.225,24 0,23.776
                       0,23.5 L0,20 L3.069,20 L3.069,20.001 L3.581,20 Z M11,22 L15,22 L15,21 L11,21 L11,22 Z"
                />
                <path
                    d="M18,4.5 L18,19 L14.994,19 L14.992,18.943 C15.003,18.176 14.462,17.485 13.657,17.255
                       L11.039,16.622 L11.039,15.83 C11.913,15.152 12.486,14.054 12.499,12.81 C12.098,12.941
                       11.623,13.023 11.154,13.023 C10.908,13.023 10.664,13.001 10.433,12.951 C9.982,12.853
                       9.62,12.661 9.364,12.385 C8.533,12.841 7.328,12.919 6.415,12.771 C6.168,12.731 5.836,12.655
                       5.513,12.519 C5.508,12.603 5.5,12.686 5.5,12.771 C5.5,14.069 6.111,15.215 7.039,15.894
                       L7.039,16.623 L4.402,17.262 C3.639,17.48 3.086,18.185 3.086,18.93 L3.085,19 L0,19 L0,4.5
                       C0,4.225 0.225,4 0.5,4 L6,4 L6,3 C6,1.346 7.346,0 9,0 C10.654,0 12,1.346 12,3 L12,4 L17.5,4
                       C17.776,4 18,4.225 18,4.5 Z M8,3 C8,3.552 8.447,4 9,4 C9.553,4 10,3.552 10,3 C10,2.448
                       9.553,2 9,2 C8.447,2 8,2.448 8,3 Z M12.377,11.796 C11.977,10.188 10.617,9 9,9 C7.484,9
                       6.194,10.046 5.709,11.502 C5.901,11.613 6.184,11.721 6.575,11.784 C7.591,11.949 8.721,11.744
                       9.146,11.317 C9.267,11.197 9.439,11.145 9.606,11.181 C9.772,11.217 9.908,11.335 9.968,11.494
                       C10.001,11.584 10.105,11.857 10.644,11.973 C11.248,12.104 11.978,11.957 12.377,11.796 Z"
                />
            </g>
        </svg>
    </div>
);

BarnetIkon.propTypes = {
    kjonn: React.PropTypes.string.isRequired
};
