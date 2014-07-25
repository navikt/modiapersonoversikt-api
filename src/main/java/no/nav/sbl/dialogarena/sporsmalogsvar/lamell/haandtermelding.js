function animasjonSkliToggling(panelKlasse, fart) {
    $(panelKlasse).slideToggle(fart);
}

function animasjonSkliMedVent(){
    var toggle = animasjonSkliToggling();
    $.when(toggle()).done()( function () {
        console.log('Animasjon complete');
    });
}
