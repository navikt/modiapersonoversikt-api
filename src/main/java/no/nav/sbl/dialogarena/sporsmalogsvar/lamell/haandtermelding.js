function animasjonSkliToggling(panelKlasse, fart) {
    $(panelKlasse).slideToggle(fart);
}

function animasjonSkliTogglingMedVent(panelKlasse, fart){
    var toggle = animasjonSkliToggling(panelKlasse, fart);
//    $.when(toggle()).done()( function () {
//        console.log('Animasjon complete');
//    });
}
