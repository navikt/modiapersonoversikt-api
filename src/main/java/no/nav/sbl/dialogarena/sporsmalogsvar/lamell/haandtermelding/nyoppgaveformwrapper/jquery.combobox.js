(function ($) {
    var ENTER = 13;

    //Brukes for å sendes oppdatering slik at wicket holder følge.
    function wicketEvent(element, event) {
        if (document.createEventObject) {
            //For IE
            var evt = document.createEventObject();
            return element.fireEvent('on' + event, evt);
        } else {
            var evt = document.createEvent("HTMLEvents");
            evt.initEvent(event, true, true);
            return !element.dispatchEvent(evt);
        }
    }

    //Overriding av autocomplete funksjonalitet
    $.widget("custom.autocomplete", $.ui.autocomplete, {
        _create: function () {
            this._super();

            //Kategory elementer skal ikke være selektbare
            this.widget().menu("option", "items", "> :not(.ui-autocomplete-category)");
        },
        _renderMenu: function (ul, items) {
            console.log('t', this);
            //Egen render metode for dropdownmenyen. NB bruk that._renderItemData for å underelementer
            var that = this;
            var currentCategory = undefined;
            $.each(items, function (index, item) {
                var li;
                if (item.category != currentCategory) {
                    $('<li>' + item.category + '</li>')
                        .addClass('ui-autocomplete-category')
                        .addClass(item.category)
                        .appendTo(ul);
                    currentCategory = item.category;
                }
                li = that._renderItemData(ul, item);
                if (item.category) {
                    li.attr('aria-label', item.category + " : " + item.label);
                }
            });
        }
    });

    //Custom komponent
    $.widget("custom.combobox", {
        _create: function () {
            this.wrapper = $("<span>")
                .addClass("jquery-combobox")
                .insertAfter(this.element);

            this.element.hide();
            this._createAutocomplete();
            this._createShowAllButton();
        },

        _createAutocomplete: function () {
            var selected = this.element.find("[selected]"),
                value = selected.val() ? selected.text() : "";

            this.input = $('<input type="text">')
                .appendTo(this.wrapper)
                .attr('placeholder', this.options.placeholder)
                .val(value)
                .autocomplete({
                    delay: 0,
                    minLength: 0,
                    source: $.proxy(this, "_source"),
                    appendTo: this.wrapper,
                    close: function () {
                        $(this)
                            .focus()
                            .parent()
                            .find('.ui-autocomplete-wrapper').hide();
                    },
                    open: function () {
                        $(this)
                            .parent()
                            .find('.ui-autocomplete-wrapper')
                            .show()
                            .find('.ui-autocomplete')
                            .css({top: 0, left: 0});
                    },
                    select: function (event, ui) {
                        wicketEvent($(ui.item.option).closest('select')[0], 'change');
                    }
                })
                .click(function () {
                    $(this).siblings('button').click();
                });
            var widget = this.input.autocomplete('widget').wrap('<div class="ui-autocomplete-wrapper" />');

            //Fjerning av ugyldig data når dropdown blir lukket
            this.input.on('keydown', function (e) {
                if (e.keyCode === ENTER) {
                    this._removeIfInvalid(e, {});
                    this.input.autocomplete('close');
                    e.stopPropagation();
                    e.preventDefault();
                }
            }.bind(this));
            this.wrapper.on('change', 'input[type=text]', function (e) {
                this._removeIfInvalid(e, {});
            }.bind(this));

            this._on(this.input, {
                autocompleteselect: function (event, ui) {
                    ui.item.option.selected = true;
                    this._trigger("select", event, {
                        item: ui.item.option
                    });
                },

                autocompletechange: "_removeIfInvalid"
            });
        },

        _createShowAllButton: function () {
            var input = this.input,
                wasOpen = false;

            var widget = this;
            $("<button>")
                .attr("tabIndex", -1)
                .attr("title", "Show All Items")
                .tooltip()
                .appendTo(widget.wrapper)
                .mousedown(function () {
                    wasOpen = input.autocomplete('widget').is(":visible");
                })
                .click(function (event) {
                    event.preventDefault();
                    input.focus();

                    // Close if already visible
                    if (wasOpen) {
                        return;
                    }

                    // Pass empty string as value to search for, displaying all results
                    input.autocomplete("search", "");
                })
                .append('<i class="ned">');
        },

        _source: function (request, response) {
            var matcher = new RegExp($.ui.autocomplete.escapeRegex(request.term), "i");

            response(this.element.find('option').map(function () {
                var $this = $(this);
                var text = $this.text();
                var hasCategory = $this.parent().is('optgroup');
                if (this.value && ( !request.term || matcher.test(text) )) {
                    var item = {
                        label: text,
                        value: text,
                        option: this
                    };
                    if (hasCategory) {
                        item.category = $this.parent().attr('label') || $this.parent().text();
                    }
                    return item;
                }
            }));
        },

        _removeIfInvalid: function (event, ui) {

            // Selected an item, nothing to do
            if (ui.item) {
                return;
            }

            // Search for a match (case-insensitive)
            var value = this.input.val(),
                valueLowerCase = value.toLowerCase(),
                valid = false;
            this.element.find("option").each(function () {
                if ($(this).text().toLowerCase() === valueLowerCase) {
                    this.selected = valid = true;
                    return false;
                }
            });

            // Found a match, nothing to do
            if (valid) {
                return;
            }

            // Remove invalid value
            this.input
                .val("")
                .attr("title", value + " didn't match any item");
            this.element.val("");
            this.input.autocomplete("instance").term = "";
        },

        _destroy: function () {
            this.wrapper.remove();
            this.element.show();
        }
    })
    ;
})
(jQuery);