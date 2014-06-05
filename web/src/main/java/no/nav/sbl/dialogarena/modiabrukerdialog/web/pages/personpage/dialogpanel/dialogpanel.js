var Dialogpanel = (function () {
    function init() {
        var kanalClass = '.kanalvalg';
        $(kanalClass + ' :radio').click(function () {
            var kanal = $(this).parent(kanalClass);
            if (!erValgt(kanal)) {
                fjernMerking($('.kanalvelger ' + kanalClass));
                merkSomValgt(kanal);
            }
        });

        function erValgt(kanal) {
            return kanal.css('background-image').indexOf('valgt.svg') !== -1;
        }

        function fjernMerking(kanaler) {
            kanaler.each(function() {
                $(this).css('background-image', $(this).css('background-image').replace('_valgt.svg', '.svg'));
            });
        }

        function merkSomValgt(kanal) {
            kanal.css('background-image', kanal.css('background-image').replace('.svg', '_valgt.svg'));
        }
    }

    return {
        init: init
    }
})();
