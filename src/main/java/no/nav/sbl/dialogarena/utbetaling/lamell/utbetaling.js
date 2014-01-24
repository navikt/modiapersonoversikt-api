var utbetalingslinjeSelector = '.utbetaling-ramme .utbetalingslinje';

$(document).on('click', utbetalingslinjeSelector, function () {
    $(this).children('.detaljpanel').animate({height: 'toggle'}, 300);
    $(this).toggleClass('ekspandert');
});

$(document).on('click', utbetalingslinjeSelector + ' .skriv-ut', function(event) {
    var $utbetalingslinje = $(this).closest(utbetalingslinjeSelector);
    Utbetalinger.skrivUt($utbetalingslinje);
    event.stopPropagation();
});


var Utbetalinger = (function () {

    var haandterDetaljPanelVisning = function (detaljPanelID) {
        var $detaljPanel = $("#" + detaljPanelID);
        $detaljPanel.animate({height: 'toggle'}, 900);
        $detaljPanel.parent().toggleClass('ekspandert');
        $('html,body').animate({scrollTop: $($detaljPanel).parent().offset().top - 76}, 'slow');
        $detaljPanel.focus();
    };

    var skrivUt = function ($element) {
        var $printCopy = $element.clone();
        $printCopy.children('.detaljpanel').css('display', 'block');
        kopierOgSkrivUt($printCopy.html());
    };

    function kopierOgSkrivUt(html) {
        $('body > .print .content').html(html);
        var date = new Date();
        var day = date.getDate();
        var month = date.getMonth() + 1;
        var year = date.getFullYear();
        var ddmmyyyy = ((day < 10 ? '0' : '') + day) + '.' + ((month < 10 ? '0' : '') + month) + '.' + year;
        $('body > .print .dato-utskrift #dato').text(ddmmyyyy);
        var $body = $('body');
        var currentWindowHeight = $body.height();
        $body.height('100%'); // hack så det ikke kommer en tom side til slutt
        //window.print();
        var onPrintFinished = function(printed, currentWindowHeight) {
            $body.height(currentWindowHeight);
        };
        onPrintFinished(window.print(), currentWindowHeight);
    }



    return {
        haandterDetaljPanelVisning: haandterDetaljPanelVisning,
        skrivUt: skrivUt
    }
})();