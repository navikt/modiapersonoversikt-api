var Utbetalinger = (function () {
    var utbetalingslinjeSelector = '.utbetaling-ramme .utbetalingslinje';

    var init = function() {
        addKeyNavigation();

        // Event listeners
        $(document).on('keypress', '.utbetaling-ramme-innhold .utbetalingslinje', function(event){
            if (event.which === 13) {
                Utbetalinger.toggleDetaljPanel($(this));
            }
        });

        $(document).on('click', utbetalingslinjeSelector, function () {
            Utbetalinger.toggleDetaljPanel($(this));
        });

        $(document).on('click', utbetalingslinjeSelector + ' .skriv-ut', function (event) {
            var $utbetalingslinje = $(this).closest(utbetalingslinjeSelector);
            Utbetalinger.skrivUt($utbetalingslinje);
            event.stopPropagation();
        });

    };

    var addKeyNavigation = function() {
        $('.utbetaling-ramme').addKeyNavigation({itemsSelector:'.utbetalingslinje'});
    };

    var haandterDetaljPanelVisning = function (detaljPanelID) {
        var $detaljPanel = $("#" + detaljPanelID);
        $detaljPanel.animate({height: 'toggle'}, 900);
        $detaljPanel.parent().toggleClass('ekspandert');
        $('html,body').animate({scrollTop: $($detaljPanel).parent().offset().top - 76}, 'slow');
        $detaljPanel.parent().focus();
    };

    var toggleDetaljPanel = function ($element) {
        $element.children('.detaljpanel').animate({height: 'toggle'}, 200);
        $element.toggleClass('ekspandert');
    };

    var skrivUt = function ($element) {
        var $printCopy = $element.clone();
        $printCopy.children('.detaljpanel').css('display', 'block');
        $printCopy.css('padding', '0');
        kopierOgSkrivUt($('<div>').append($printCopy).html());
    };

    function kopierOgSkrivUt(html) {
        $('body > .print .content').html('<div class="utbetalinger">' + html + '</div>');
        var date = new Date();
        var day = date.getDate();
        var month = date.getMonth() + 1;
        var year = date.getFullYear();
        var ddmmyyyy = ((day < 10 ? '0' : '') + day) + '.' + ((month < 10 ? '0' : '') + month) + '.' + year;
        $('body > .print .dato-utskrift #dato').text(ddmmyyyy);
        window.print();
    }

    return {
        init: init,
        addKeyNavigation: addKeyNavigation,
        haandterDetaljPanelVisning: haandterDetaljPanelVisning,
        toggleDetaljPanel: toggleDetaljPanel,
        skrivUt: skrivUt
    }
})();

$(document).ready(function() {
    Utbetalinger.init();
});