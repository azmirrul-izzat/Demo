function parseGACookie() {
    var values = {};
    var cookie = readCookie("__utmz");
    if (cookie) {
        var z = cookie.split('.');
        if (z.length >= 4) {
            var y = z[4].split('|');
            for (i = 0; i < y.length; i++) {
                var pair = y[i].split("=");
                values[pair[0]] = pair[1];
            }
        }
    }
    return values;
}

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
}

var ga = parseGACookie();
var currentURL = window.location.href;
//20170208 - PN - Mouseflow not firing: remove conditions - start
//20170210 - Mouseflow for AU sites - start
//if((ga['utmcmd'] == "organic" || ga['utmcmd'] == "referral" || ga['utmcmd'] == "email") && currentURL.indexOf("en_au") > 0 ) {
if(window.location.href.indexOf("en_au") > -1){
//20170210 - Mouseflow for AU sites - end
    var _mfq = _mfq || [];
    (function() {
        var mf = document.createElement("script");
        mf.type = "text/javascript";
        mf.async = true;
        mf.src = "//cdn.mouseflow.com/projects/b2bce8a7-3923-4c65-8189-fa6385bd0162.js";
        document.getElementsByTagName("head")[0].appendChild(mf);
    })();

}
//20170208 - PN - Mouseflow not firing: remove conditions - end