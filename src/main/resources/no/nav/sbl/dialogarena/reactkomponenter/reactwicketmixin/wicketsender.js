module.exports = function (url, component, action, data) {
    Wicket.Ajax.ajax({
        "u": url,
        "c": component,
        "ep": [
            {"name": action, "value": JSON.stringify(data)}
        ]
    });
};