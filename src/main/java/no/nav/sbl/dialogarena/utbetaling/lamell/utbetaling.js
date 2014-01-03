var utbetalingslinjeSelector = '.utbetaling-ramme .utbetalingslinje';

$(document).on('click', utbetalingslinjeSelector, function (e) {
    if ($(e.target).is('a')) {
        e.preventDefault();
    } else {
        $(this).children('.detaljpanel').animate({height: 'toggle'}, 300);
    }
});

$(document).on('click', '.utbetaling-ramme .oppsummering-total', function() {
    $(this).children('.detaljpanel').animate({height: 'toggle'}, 300);
});

$(document).on('click', utbetalingslinjeSelector + ' .skriv-ut', function() {
    var utbetalingslinje = $(this).closest(utbetalingslinjeSelector).clone();
    utbetalingslinje.children('.detaljpanel').css('display', 'block');
    $('body > .print .content').html('<div class="kolonne-hoyre">' + utbetalingslinje.html() + '</div>');
    window.print();
});



var Utbetalinger = (function() {
    var haandterDetaljPanelVisning = function(detaljPanelID) {
        var $detaljPanel = $("#" + detaljPanelID);
        $detaljPanel.animate({height: 'toggle'}, 900);
        $('html,body').animate({scrollTop: $($detaljPanel).parent().offset().top - 76}, 'slow');
        $detaljPanel.focus();
    }
    return {
        haandterDetaljPanelVisning : haandterDetaljPanelVisning
    }
})();