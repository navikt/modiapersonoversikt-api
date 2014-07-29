function apneMedAnimasjon(panelKlasse, fart) {
    $(panelKlasse).slideDown(fart);
}

function lukkMedAnimasjon(panelKlasse, fart, callback) {
    $(panelKlasse).slideUp(fart, callback);
}
