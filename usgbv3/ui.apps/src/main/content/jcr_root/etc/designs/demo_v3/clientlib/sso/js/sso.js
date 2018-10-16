(function() {
  'use strict';
  // this function gets the query parameter of the url 
  function getQueryParam() {
      var query = location.search.substr(1);
      var result = {};
      query.split("&").forEach(function(part) {
          var item = part.split("=");
          result[item[0]] = decodeURIComponent(item[1]);
      });
      return result;
  }

  //generate random id  which we used as guid
  function guid() {
    function s4() {
      return Math.floor((1 + Math.random()) * 0x10000)
        .toString(16)
        .substring(1);
    }
    return s4() + s4() + s4() + s4() + s4() + s4() + s4();
  }

  // get the country part from the url
  function getCountry() {
	var currentURI = getReturnURI();
    var dt = currentURI.split('/');

    if(dt.indexOf('en_au') > 1 || dt.indexOf('en_au.html') > 1) {
		return 'AU';
    } else if(dt.indexOf('zh_cn') > 1 || dt.indexOf('zh_cn.html') > 1 || currentURI.indexOf('demochina') > 1) {
		return 'CN';
    } else if(dt.indexOf('en_in') > 1 || dt.indexOf('en_in.html') > 1) {
		return 'IN';
    } else if(dt.indexOf('ko_kr') > 1 || dt.indexOf('ko_kr.html') > 1) {
		return 'KR';
    } else if(dt.indexOf('en_nz') > 1 || dt.indexOf('en_nz.html') > 1) {
		return 'NZ';
    } else if(dt.indexOf('vi_vn') > 1 || dt.indexOf('vi_vn.html') > 1) {
		return 'VN';
    } else if(dt.indexOf('en_sg') > 1 || dt.indexOf('en_sg.html') > 1) {
		return 'SG';
    } else if(dt.indexOf('en_my') > 1 || dt.indexOf('en_my.html') > 1) {
		return 'MY';
    } else if(dt.indexOf('en_ph') > 1 || dt.indexOf('en_ph.html') > 1) {
		return 'AU';
    } else if(dt.indexOf('th_th') > 1 || dt.indexOf('th_th.html') > 1) {
		return 'TH';
    } else if(dt.indexOf('en_ex') > 1 || dt.indexOf('en_ex.html') > 1) {
		return 'TH';
    } else if(dt.indexOf('en_id') > 1 || dt.indexOf('en_id.html') > 1) {
		return 'ID';
    } else if(dt.indexOf('in_id') > 1 || dt.indexOf('in_id.html') > 1) {
		return 'ID';
    } else if(dt.indexOf('en_me') > 1 || dt.indexOf('en_me.html') > 1) {
		return 'ME';
    } else if(dt.indexOf('en_ip') > 1 || dt.indexOf('en_ip.html') > 1) {
		return 'IP';
    } else {
        return 'AU';
    }
  }
// get the locale code or country code from the url
  function getCountryCode() {
	var currentURI = getReturnURI();
    var dt = currentURI.split('/');

    if(dt.indexOf('en_au') > 1 || dt.indexOf('en_au.html') > 1) {
		return 'en_au';
    } else if(dt.indexOf('zh_cn') > 1 || dt.indexOf('zh_cn.html') > 1) {
		return 'zh_cn';
    } else if(dt.indexOf('en_in') > 1 || dt.indexOf('en_in.html') > 1) {
		return 'en_in';
    } else if(dt.indexOf('ko_kr') > 1 || dt.indexOf('ko_kr.html') > 1) {
		return 'ko_kr';
    } else if(dt.indexOf('en_nz') > 1 || dt.indexOf('en_nz.html') > 1) {
		return 'en_nz';
    } else if(dt.indexOf('vi_vn') > 1 || dt.indexOf('vi_vn.html') > 1) {
		return 'vi_vn';
    } else if(dt.indexOf('en_sg') > 1 || dt.indexOf('en_sg.html') > 1) {
		return 'en_sg';
    } else if(dt.indexOf('en_my') > 1 || dt.indexOf('en_my.html') > 1) {
		return 'en_my';
    } else if(dt.indexOf('en_ph') > 1 || dt.indexOf('en_ph.html') > 1) {
		return 'en_ph';
    }else if(dt.indexOf('en_ex') > 1 || dt.indexOf('en_ex.html') > 1) {
		return 'en_ex';
    } else if(dt.indexOf('th_th') > 1 || dt.indexOf('th_th.html') > 1) {
		return 'th_th';
    } else if(dt.indexOf('en_id') > 1 || dt.indexOf('en_id.html') > 1) {
		return 'en_id';
    } else if(dt.indexOf('in_id') > 1 || dt.indexOf('in_id.html') > 1) {
		return 'in_id';
    } else if(dt.indexOf('en_me') > 1 || dt.indexOf('en_me.html') > 1) {
		return 'en_me';
    } else if(dt.indexOf('en_ip') > 1 || dt.indexOf('en_ip.html') > 1) {
		return 'en_ip';
    } else {
        return 'en_au';
    }
  }

  // get the current language from the url
  function getLang() {
	var currentURI = getReturnURI();
    var dt = currentURI.split('/');

    if(dt.indexOf('zh_cn') > 1 || dt.indexOf('zh_cn.html') > 1) {
		return 'zh';
    } else if(dt.indexOf('ko_kr') > 1 || dt.indexOf('ko_kr.html') > 1) {
		return 'ko';
    } else if(dt.indexOf('vi_vn') > 1 || dt.indexOf('vi_vn.html') > 1) {
		return 'vi';
    } else if(dt.indexOf('th_th') > 1 || dt.indexOf('th_th.html') > 1) {
		return 'th';
    } else if(dt.indexOf('in_id') > 1 || dt.indexOf('in_id.html') > 1) {
		return 'in';
    } else {
        return 'en';
    }
  }

  // get the current url
  var getReturnURI = function() {
      if (location.href.indexOf('?') != -1) {
          return location.href.substring(0, location.href.indexOf('?'));
      } else {
          return location.href;
      }
  };

  $.ssoManager = (function() {
    const USGB_ST = 'usgb-st';
    const USGB_AT = 'usgb-at';
    const USGB_SA = 'usgb-sa';
    const USGB_RU = 'usgb-ru';

    var myclient = $('#myclient').html();
    var myclientd = $('#myclientd').html();
	  var currentDomain =window.location.hostname;

    var ssoDomain = "http://myaccount-stg.demo.com";

      if(myclientd){

		ssoDomain = myclientd;
      }

    // var stAPI = "http://localhost:9003/sso/sessionInfo";
    var stAPI = "/bin/sso/info/session";
    var uiAPI = "/bin/sso/info/user";
    var loginURL = ssoDomain +"/Login.aspx";
    var logoutURL = ssoDomain +"/Logout.aspx";
    var sessionURL = ssoDomain +"/GetSessionToken.aspx";
    var manageProfileURL = ssoDomain +"/MyAccount.aspx";
    var client_id = "wMFTKQEamclwZUsW8zZ2/g";
    var userSession = {};
    var userInfo = {};
    var countryCode = getCountryCode();
    //Granite.I18n.setLocale(countryCode);

    if(myclient){
		client_id = myclient;
      }

    var getState = function() {
      return guid();
    }

    function isRedirect() {
      return (!Cookies.get(USGB_ST) && !Cookies.get(USGB_AT));
    }

    var init = function() {
		  console.log("init sso!!!!");

		  $('.post-login-nav').hide();
		  $('.pre-login-nav').show();

      var errorSkipRedirect = ['not_logged_in', 'cookies_disabled'];
	  if(getQueryParam().error &&  errorSkipRedirect.indexOf(getQueryParam().error) != -1) {
			location.href=getReturnURI();
          	//window.close();
            return;
      }

      if (getQueryParam().access_token && getQueryParam().session_token) {
        Cookies.set(USGB_AT, getQueryParam().access_token, {path: '/'});
        Cookies.set(USGB_ST, getQueryParam().session_token, {path: '/'});
        Cookies.set(USGB_SA, getQueryParam().state, {path: '/'});
        //window.close();
      } else if (getQueryParam().session_token && getQueryParam().state && !getQueryParam().access_token) {
          if(getQueryParam().error &&  getQueryParam().error == 'not_logged_in') {
			location.href=getReturnURI();
            //window.close();
            return;
          }

          if (isRedirect()) {
            loginRedirect();
          }
      }

      if (Cookies.set(USGB_AT) && Cookies.set(USGB_ST) && Cookies.set(USGB_SA)) {
        if (getQueryParam().access_token || getQueryParam().session_token) {
          location.href=getReturnURI();
        }
      }

      getSession();
    };

    //encoded client_id
    client_id = encodeURIComponent(client_id);
    var loginRedirect = function() {
      var goto = loginURL + '?'
      goto += 'client_id=' + client_id;
      goto += (getCountry()) ? '&bu=' + getCountry() : '';
      goto += '&session_token=' + encodeURIComponent(getQueryParam().session_token);
      goto += '&state=' + getQueryParam().state;
      goto += '&scope=email profile';
      goto += (getLang()) ? '&lang=' + getLang() : '';
      goto += '&redirect_uri=' + getReturnURI();
console.log(goto);
      location.href = goto;
    };

    var sessionRedirect = function() {
      var goto = sessionURL + '?';
      goto += "client_id=" + client_id;
      goto += '&state='+getState();
      goto += '&redirect_uri=' + getReturnURI();
        console.log(goto);
      return location.href = goto;

      /*var e = window.location.origin + window.location.pathname;
        console.log(e);
      var f = window.open(goto, "", "toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=600, height=600, top=" + (screen.height / 2 - 600 / 2) + ", left=" + (screen.width / 2 - 600 / 2)),
	  j = setInterval(function() {
                        f.closed && (clearInterval(j), window.location.href = e)
                    }, 1E3)	;
        return;*/
    };

    var initPreLogin = function(newLogin) {

          if (getQueryParam().session_token && getQueryParam().state && !getQueryParam().error && isRedirect()) {
              loginRedirect();
          } else  {
            $('.pre-login-nav').click(function(e) {
                /*swal({
                  text: "SSO service is unavailable. Please try again later.",
                    title: "Service Unavailable!",
                     type: "warning",
                    timer: 5000
                });
                return;*/

              e.preventDefault();
              if (getQueryParam().session_token && getQueryParam().access_token && getQueryParam().state && getQueryParam().error != 'invalid_token') {
                if (isRedirect()) {
				 loginRedirect();
                }
              } else {
                sessionRedirect();
              }
            });
          }


    };

    var initPostLogin = function(sessionData) {
      var showNav = function(data) {
          console.log('post loginnn');
		  $('.post-login-nav').show();
		  $('.pre-login-nav').hide();
          console.log(data);
          $('.post-login-user').html((data.display_name ? data.display_name : data.first_name + ' ' + data.last_name));
          //$('.post-login-nav').removeClass('hidden');
          //$('.post-login-nav').find('div, .caret').removeClass('hidden');
          //$('.profile-user-name').html(Granite.I18n.get("Welcome") +', ' + (data.display_name ? data.display_name : data.first_name + ' ' + data.last_name));
          /*if ($.jStorage && $.jStorage.get('demo-order-sample') && $.jStorage.get('demo-order-sample').orderSample && $.jStorage.get('demo-order-sample').orderSample.length > 0) {
            //$('.order-sample a').append('<span class="fa fa-exclamation-circle txt-red"></span>');
            $('.order-sample').find('.caret').removeClass('caret');
            $('.order-sample').find('.icn-plc').addClass('fa fa-exclamation-circle txt-red');
            $('.order-sample').data('order-sample',true);
          }

          if($.jStorage && $.jStorage.get('demo-doc-collator')) {
            $('.doc-collator').find('.glyphicon-folder-close').removeClass('glyphicon-folder-close').addClass('glyphicon-folder-open');
            $('.doc-collator').find('.caret').removeClass('caret');
            $('.doc-collator').find('.icn-plc').addClass('fa fa-exclamation-circle txt-red');
            $('.doc-collator').data('doc-collator',true);
          }*/

          if(Cookies.set(USGB_RU)) {
              var ru = Cookies.get(USGB_RU);
              Cookies.remove(USGB_RU);

              setTimeout(function(){ location.href=ru; }, 3000);
              //location.href=ru;
          }
      };
      isLogin = true;
      showNav(sessionData);
    };

    var hideNav = function() {
        $('.post-login-nav').find('div, .caret').remove();
    };

    var session = function(obj, newLogin) {
      return $.ajax({
        url: stAPI,
        method: 'POST',
        data: JSON.stringify(obj),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        async: false
      }).done().responseJSON;
    };

  var manageProfile = function() {
    var goto = manageProfileURL + '?'
      goto += 'client_id=' + client_id;
      goto += '&session_token=' + encodeURIComponent(Cookies.get(USGB_ST));
      goto += '&state=' + Cookies.get(USGB_SA);
      goto += (getLang()) ? '&lang=' + getLang() : '';
      goto += '&redirect_uri=' + getReturnURI();
      location.href = goto;
  }

    var getUserInfo = function() {
      var obj = {};
      obj.access_token = Cookies.get(USGB_AT);
	  obj.domain = currentDomain;
        console.log("obj"+obj);
        console.log("uiAPI"+uiAPI);
      return $.ajax({
        url: uiAPI,
        method: 'POST',
        data: JSON.stringify(obj),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        async: false
      })

    };

    var getSessionToken = function() {
        var ep = sessionURL;
        ep += '?' + "client_id=" + client_id;
        ep += '&redirect_uri=' + getReturnURI();
        return location.href = ep;
    };

    var getSession = function() {
      var obj = {};
      var newLogin = false;
      if (Cookies.get(USGB_ST)) {
        obj.session_token = Cookies.get(USGB_ST);
        newLogin = false;
      } else if (getQueryParam().session_token) {
        obj.session_token = getQueryParam().session_token;
        newLogin = true;
      }

      

      if (Cookies.get(USGB_ST) || getQueryParam().session_token) {
		obj.domain = currentDomain;
        var us=session(obj, newLogin);
        if (us && us.status && us.status == 'logged_in') {
          if (newLogin && ((!getQueryParam().access_token && !getQueryParam().session_token)) && isRedirect()) {
                loginRedirect();
          }
          initPostLogin(us);
          return us;
        }
      }
      
      initPreLogin();
    }

    var logout = function() {
        var tmpSt = Cookies.get(USGB_ST);
        if (!Cookies.get(USGB_ST)) {
          var tmpSt = getQueryParam().session_token;
        }

        Cookies.remove(USGB_AT);
        Cookies.remove(USGB_ST);
        Cookies.remove(USGB_SA);
        /*if ($.jStorage) {
          if ($.jStorage.get('demo-order-sample')) {
            $.jStorage.deleteKey('demo-order-sample');
          }
          if ($.jStorage.get('demo-doc-collator')) {
            $.jStorage.deleteKey('demo-doc-collator');
          }
        }*/
        var goto = logoutURL;
        goto += '?client_id=' + client_id;
        goto += '&session_token=' + encodeURIComponent(tmpSt);
        goto += '&redirect_uri=' + getReturnURI();
        location.href = goto;
    };

    var sso = {
        login: sessionRedirect,
        logout: logout,
        userInfo: getUserInfo,
        sessionInfo: session,
        manageProfile: manageProfile
    };

    var doc = {
        // add: addDocument,
        // getDocument: getDocument,
        // removeDoc: removeDocument
    }

    var isLogin = false;
    init();
    return {
        sso: sso,
        doc: doc,
        userInfo: userInfo,
        sessionInfo: getSession,
        countryCode:countryCode,
        isLogin: isLogin
    };
  })();
})();