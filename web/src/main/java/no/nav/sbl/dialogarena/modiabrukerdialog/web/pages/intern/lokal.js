jQuery(document).ready(function ($) {
    "use strict";

    Modig.shortcutListener.on({alt:true, shift:false, key:'V'}, toggleKjerneinfo);

    $("#toggle-kjerneinfo").on("click", toggleKjerneinfo);

    function toggleKjerneinfo () {
        var kjerneInfoElement = $(".main > .kjerneinfo"), toggleElement = $("#toggle-kjerneinfo"), visittkort = $("aside.sidebar > .visittkort");
        if (kjerneInfoElement.is(":visible")) {
            kjerneInfoElement.hide();
            toggleElement.html(toggleElement.attr("data-show-text"));
            visittkort.removeClass("active");
        } else {
            kjerneInfoElement.show();
            toggleElement.html(toggleElement.attr("data-hide-text"));
            visittkort.addClass("active");

            if ($("#personsokPanel").is(":visible")) {
            	toggleAvansertSok();
            }
        }
    }

    $(".sidebar").on("click", function () {
        var sidebar = $(this);
        if (sidebar.css("position") == "absolute") {
            if (sidebar.css("right") == "-380px") {
                sidebar.animate({right: "0"}, "25");
            } else {
                sidebar.animate({right: "-380px"}, "25");
            }
        }
    });

    $(".sidebar > *").on("click", function (e) {
        e.stopPropagation();
    });

    $("#toggle-personsok").on("click", toggleAvansertSok);
    Modig.shortcutListener.on({alt:true, shift:true, key:'A'}, toggleAvansertSok);
    
    $("body").on("click", ".lamell .lamellhode > a", function() {
    	if ($(".main > .personsok").is(":visible")) {
    		toggleAvansertSok();
    	}
    });

    function toggleAvansertSok() {
        var personsokElement = $("#personsok"), toppmeny = $(".main > .menu-intern"),
            fornavnInput = $("#utvidetPersonsokForm input[name='fornavn']"), sokKnapp = $("#toggle-personsok"),
            dataApne = sokKnapp.attr("data-apne"),
            dataLukke = sokKnapp.attr("data-lukke");
        if (personsokElement.is(":visible")) {
            personsokElement.hide();
            toppmeny.removeClass("active");
            sokKnapp.removeClass("active");
            sokKnapp.attr("title", dataApne);
            sokKnapp.attr("aria-label", dataApne);
        } else {
            personsokElement.show();
            toppmeny.addClass("active");
            sokKnapp.addClass("active");
            sokKnapp.attr("title", dataLukke);
            sokKnapp.attr("aria-label", dataLukke);

            if (fornavnInput.length != 0) {
                fornavnInput.focus();
            }

            if ($(".main > .kjerneinfo").is(":visible")) {
                toggleKjerneinfo();
            }
        }
    }

    Modig.shortcutListener.on({alt:false, shift:true, keyCode:13}, gjennomfoerAvansertSok);

    function gjennomfoerAvansertSok() {
        var personsokElement = $(".main > .personsok");
        if (personsokElement.is(":visible")) {
            $("#utvidetPersonsokForm:visible").submit;
        }
    }
    
    // IE9 fiks til søk-knapp i personsøk pga bugg med :hover på anet en A-element
    $(".search-intern input[type=button]").hover(function() {
    	$(this).addClass("hover");
    }, function() {
    	$(this).removeClass("hover");
    });
});

