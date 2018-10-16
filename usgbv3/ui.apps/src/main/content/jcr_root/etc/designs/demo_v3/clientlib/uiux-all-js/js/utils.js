// ----------------------------------------------------------------------
// utils
// ----------------------------------------------------------------------


// serialize form data to object
$.fn.serializeObject = function(){
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

// unique array
var uniqueArray = function (arrArg) {
    return arrArg.filter(function (elem, pos, arr) {
        return arr.indexOf(elem) == pos;
    });
};



function escapeString(s) {
    return ('' + s) /* Forces the conversion to string. */
        .replace(/\\/g, '\\\\') /* This MUST be the 1st replacement. */
        .replace(/\t/g, '\\t') /* These 2 replacements protect whitespaces. */
        .replace(/\n/g, '\\n')
        .replace(/%/g, '\\pp')
        .replace(/\u00A0/g, '\\u00A0') /* Useful but not absolutely necessary. */
        .replace(/&/g, '\\x26') /* These 5 replacements protect from HTML/XML. */
        .replace(/'/g, '\\x27')
        .replace(/"/g, '\\x22')
        .replace(/</g, '\\x3C')
        .replace(/>/g, '\\x3E');
}

function unescapeString(s) {
    /*
    Note: this can be implemented more efficiently by a loop searching for
    backslashes, from start to end of source string, and parsing and
    dispatching the character found immediately after the backslash, if it
    must be followed by additional characters such as an octal or
    hexadecimal 7-bit ASCII-only encoded character, or an hexadecimal Unicode
    encoded valid code point, or a valid pair of hexadecimal UTF-16-encoded
    code units representing a single Unicode code point.

    8-bit encoded code units for non-ASCII characters should not be used, but
    if they are, they should be decoded into a 16-bit code units keeping their
    numeric value, i.e. like the numeric value of an equivalent Unicode
    code point (which means ISO 8859-1, not Windows 1252, including C1 controls).

    Note that Javascript or JSON does NOT require code units to be paired when
    they encode surrogates; and Javascript/JSON will also accept any Unicode
    code point in the valid range representable as UTF-16 pairs, including
    NULL, all controls, and code units assigned to non-characters.
    This means that all code points in \U00000000..\U0010FFFF are valid,
    as well as all 16-bit code units in \u0000..\uFFFF, in any order.
    It's up to your application to restrict these valid ranges if needed.
    */
    s = ('' + s) /* Forces the conversion to string. */
        /* Decode by reversing the initial order of replacements */
        .replace(/\\x3E/g, '>')
        .replace(/\\x3C/g, '<')
        .replace(/\\x22/g, '\"')
        .replace(/\\x27/g, "\'")
        .replace(/\\pp/g, "%")
        .replace(/\\x26/g, '&') /* These 5 replacements protect from HTML/XML. */
        .replace(/\\u00A0/g, '\u00A0') /* Useful but not absolutely necessary. */
        .replace(/\\n/g, '\n')
        .replace(/\\t/g, '\t') /* These 2 replacements protect whitespaces. */
        .replace(/\\/g, "") /*remove backslash */ ;
    /*
    You may optionally add here support for other numerical or symbolic
    character escapes.
    But you can add these replacements only if these entities will *not* 
    be replaced by a string value containing *any* backslash character.
    Do not decode to any doubled backslashes here !
    */
    /* Required check for security! */
    // var found = (/\\[^\\]))?/.match(s);
    // if (found.length > 0 && found[0] != '\\\\')
    //     throw 'Unsafe or unsupported escape found in the literal string content';     
    /* This MUST be the last replacement. */
    return s.replace(/\\\\/g, '\\');

}

Handlebars.registerHelper('decodeString', function (str) {
    return new Handlebars.SafeString(unescape(str));
});