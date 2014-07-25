function animasjonSkliToggling() {
    $('.journalforing').slideToggle(2000);
}

function animasjonSkliMedVent(){
    var toggle = animasjonSkliToggling()
    $.when(toggle()).done()( function () {
        console.log('Animasjon complete');
    });
}
