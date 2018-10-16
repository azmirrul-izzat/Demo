(function() {
  'use strict';
		var userDt = $.ssoManager.sso.userInfo().responseJSON;
        $.each($('input[sso-default]'), function(a, b, c) {
            var inp = $(b);
            var key = $(b).attr('sso-default');
        
            if(key.indexOf('+') < 0) {
                if(key != 'email') {
                    inp.val(userDt.profile[key]);
                } else {
                    inp.val(userDt.email.address);
                }
            } else {
                var keys = key.split('+');		
                var dt = "";
                for(var i=0; i<keys.length; i++) {
                    var ky = keys[i];
                    if(ky != 'email') {
                        dt = dt + userDt.profile[ky] + " ";
                    } else {
                        dt = dt + userDt.email.address + " ";
                    }
                }
                if(dt) inp.val(dt.trim());	
            }
        })


})();