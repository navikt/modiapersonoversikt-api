$(document).on('click', function () {
    $('.temagruppe-liste').hide();
});

$(document).on('click', '.temagruppe-liste', function (e) {
    e.stopPropagation();
});

$(document).on('click', '.velg-temagruppe', function (e) {
    var $temagruppeliste = $('.temagruppe-liste');
    $temagruppeliste.toggle();

    var $valgtTemagruppe = $temagruppeliste.find('.radiogroup input:checked');

    if ($valgtTemagruppe.length > 0) {
        $valgtTemagruppe.focus();
    } else {
        $temagruppeliste.find('.radiogroup input:first').focus();
    }

    e.stopPropagation();
});

$(document).on('click', '.plukk-knapp', function (e) {
    $('.velg-temagruppe').addClass('plukket');
    var valgTemagruppe = $('.oppgavepanel .temagruppe-liste').find(':checked');

    if (valgTemagruppe.length == 0) {
        var radiogroup = $('.temagruppe-liste').show()
            .find('[role=radiogroup]');

        radiogroup
            .attr('aria-invalid', true)
            .attr('aria-describedBy', 'aria-error-' + radiogroup.attr('id'));

    } else {
        $('.temagruppe-liste')
            .find('[role=radiogroup]')
            .removeAttr('aria-invalid')
            .removeAttr('aria-describedBy');
    }
    e.stopPropagation();
});
window.fokusPlukkOppgaveTemagruppe = function () {
    var $temagruppeliste = $('.temagruppe-liste');
    $temagruppeliste.show();

    var $valgtTemagruppe = $temagruppeliste.find('.radiogroup input:checked');
    if ($valgtTemagruppe.length > 0) {
        $valgtTemagruppe.focus();
    } else {
        $temagruppeliste.find('.radiogroup input:first').focus();
    }
};
$(document).on('keydown', '.plukk-knapp, .velg-temagruppe', function (e) {
    var $target = $(e.currentTarget);
    if (e.keyCode == 13 || e.keyCode == 32) {
        $target.click();
        e.preventDefault();
    }
});

/*
 Detta er et hack for IE11. Uten dette så kan man ikke bla med piltasterna. Allt her under kan slettes om man hitter en måte at få browsern at løse dette.
 */

$(document).on('keydown', '.temagruppe-liste', function (e) {
    if (e.keyCode === 38 || e.keyCode === 40) {

        var radio = $('.temagruppe-liste').find('[type=radio]');

        RadioGroupHandler.removeFocusFromPreviousChoice(radio);

        if (e.keyCode === 38) {
            RadioGroupHandler.setForrigeTemagruppeTilValgt(radio);
        } else if (e.keyCode === 40) {
            RadioGroupHandler.setNesteTemagruppeTilValgt(radio);
        }

        RadioGroupHandler.setFocusOnNewChoice(radio);
        e.preventDefault();
        e.stopPropagation();
        return false;
    }
});

var RadioGroupHandler = function () {
    var indexValgtTemagruppe = 0;
    return {
        removeFocusFromPreviousChoice: function (radio) {
            radio[indexValgtTemagruppe].checked = false;
        },
        setNesteTemagruppeTilValgt: function (radio) {
            indexValgtTemagruppe = indexValgtTemagruppe === radio.length - 1 ? 0 : indexValgtTemagruppe + 1;
        },
        setForrigeTemagruppeTilValgt: function (radio) {
            indexValgtTemagruppe = indexValgtTemagruppe === 0 ? radio.length - 1 : indexValgtTemagruppe - 1;
        },
        setFocusOnNewChoice: function (radio) {
            radio[indexValgtTemagruppe].checked = true;
            radio[indexValgtTemagruppe].focus();
        }
    }
}();
