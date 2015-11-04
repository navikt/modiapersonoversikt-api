window.Modig = window.Modig || {};

Modig.scrollOptions = {
    theme: '3d',
    scrollbarPosition: 'outside',
    alwaysShowScrollbar: 0,
    live: 'true',
    scrollInertia: 100,
    keyboard: false,
    advanced: {
        autoScrollOnFocus: "input,textarea,select,button,datalist,keygen,a[tabindex],area,object,[contenteditable='true'],article[tabindex], ul[tabindex], div[tabindex]"
    }
};

Modig.lagScrollbars = function () {
    $('.sidebar-venstre').mCustomScrollbar(Modig.scrollOptions);
    $('#personsok').mCustomScrollbar(Modig.scrollOptions);
    $('.sidebar-hoyre').mCustomScrollbar($.extend({}, Modig.scrollOptions, {
        advanced: {
            autoScrollOnFocus: "input,select,button,datalist,keygen,a[tabindex],area,object,[contenteditable='true'],article[tabindex]"
        }
    }));

    //Alle lerret med unntak av meldinger og saksoversikt
    $('.lamell:not(.meldinger):not(.saksoversikt) .lerret').mCustomScrollbar(Modig.scrollOptions);

    //For meldinger lamell
    $('.lamell.meldinger .traad-liste-visning').mCustomScrollbar(Modig.scrollOptions);
    $('.lamell.meldinger .traadvisning').mCustomScrollbar($.extend({}, Modig.scrollOptions, {
        advanced: {
            autoScrollOnFocus: "input:not([type=submit]),select,button,a[role=button],datalist,keygen,a[tabindex],area,object,[contenteditable='true'],article[tabindex]"
        }
    }));

    //For saksoversikt lamell
    $('.lamell.saksoversikt .sak-navigering').mCustomScrollbar(Modig.scrollOptions);
    $('.lamell.saksoversikt .sak-informasjon').mCustomScrollbar(Modig.scrollOptions);
};