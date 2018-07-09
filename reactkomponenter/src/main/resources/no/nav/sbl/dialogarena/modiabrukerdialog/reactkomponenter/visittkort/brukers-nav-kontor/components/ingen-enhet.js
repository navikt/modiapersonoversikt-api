import React from 'react';
import { organisasjonType } from '../types';

function IngenEnhet() {
    return (
        <div className="nav-kontor-panel">
            <div className="nav-ikon" />
            <div className="content">
                <div className="nav-kontor-info">
                    <h3 className="overskrift">Brukers nav-kontor</h3>
                    <p>Ingen enhet</p>
                </div>
            </div>
        </div>
    );
}

export default IngenEnhet;
