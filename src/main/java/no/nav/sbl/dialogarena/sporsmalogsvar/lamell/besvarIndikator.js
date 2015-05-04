(function () {
    $(document).ready(function () {
        var $lamell = $('.main-content');

        $lamell.on('click', '.besvarBoble > a', function () {
            var $link = $(this);
            var $nyMeldingContainer = $link.closest('.nyMelding');

            var panelId = $link.closest('.traadDetaljer').attr('id');
            var $alleMeldingerListe = $lamell.find('.meldingsforhandsvisning');

            //Legger til indikator for nyMeldingContainer
            $nyMeldingContainer.addClass('besvares');

            //Skjuler alle indikatorer i listen
            $alleMeldingerListe
                .find('[id^=besvarIndikator]')
                .hide();

            //Legger til indikator for valgt melding
            $alleMeldingerListe
                .filter('.valgt')
                .find('[id^=besvarIndikator]')
                .addClass('besvarIndikator')
                .show()
                .attr('aria-hidden', true);
        });
    });
})();