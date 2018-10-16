// ----------------------------------------------------------------------
// Custom Filter for System Content Component
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        $(document).on("click", '.system-content-filter .dp-form .dp-list a', function () {

            var dataVal = $(this).data("val");
            //console.log("dataVal : " + dataVal);
            //console.log("system-content-box : " + $('.system-content-box[data-system-category="'+dataVal+'"]').length);

            if($('.system-content-box[data-system-category="'+dataVal+'"]').length > 0) {
           	 	$('.system-content-box').fadeOut()
            	$('.system-content-box[data-system-category="'+dataVal+'"]').fadeIn()
            } else {
				$('.system-content-box').fadeIn();
            }

            var $dpText = $(this).parent().parent().prev().find($('.dp-text'));
			$dpText.html(dataVal);

        });

        $(document).on("change", '.system-content-filter .dp-form-mobile select', function () {

            var dataVal = $(this).find(":selected").text();

            if($('.system-content-box[data-system-category="'+dataVal+'"]').length > 0) {
           	 	$('.system-content-box').fadeOut()
            	$('.system-content-box[data-system-category="'+dataVal+'"]').fadeIn()
            } else {
				$('.system-content-box').fadeIn();
            }

        });

    });
})();



// ----------------------------------------------------------------------
// Custom Filter for Resources Component
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        $(document).on("click", '.resources-filter .dp-form .dp-list a', function () {

            var dataVal = $(this).data("val");
            console.log("dataVal : " + $(this).html());
            console.log("resources-content-box : " + $('.resources-content-box[data-resource-category="'+dataVal+'"]').length);

            if($('.resources-content-box[data-resource-category="'+dataVal+'"]').length > 0) {
           	 	$('.resources-content-box').fadeOut()
            	$('.resources-content-box[data-resource-category="'+dataVal+'"]').fadeIn()
            } else {
				$('.resources-content-box').fadeIn();
            }

            if($('.accordion-item[data-resource-category="'+dataVal+'"]').length > 0) {
           	 	$('.accordion-item').fadeOut()
            	$('.accordion-item[data-resource-category="'+dataVal+'"]').fadeIn()
            } else {
				$('.accordion-item').fadeIn();
            }

            var $dpText = $(this).parent().parent().prev().find($('.dp-text'));
			$dpText.html($(this).html());

        });

        $(document).on("change", '.system-content-filter .dp-form-mobile select', function () {

            var dataVal = $(this).find(":selected").text();

            if($('.system-content-box[data-system-category="'+dataVal+'"]').length > 0) {
           	 	$('.system-content-box').fadeOut()
            	$('.system-content-box[data-system-category="'+dataVal+'"]').fadeIn()
            } else {
				$('.system-content-box').fadeIn();
            }

        });

    });
})();


(function () {
    "use strict";
    $(document).ready(function () {
        // check login
        var dt = {};

        if($.ssoManager.isLogin){
            if (typeof $.ssoManager.sso.userInfo() !== 'undefined' &&
                typeof $.ssoManager.sso.userInfo().responseJSON !== 'undefined' &&
                ($.ssoManager.sso.userInfo().responseJSON).hasOwnProperty('id')) {
                var userInfo = $.ssoManager.sso.userInfo().responseJSON;

                //var dt = { user_info: {user_id: userInfo.id} };
                dt = {
                    user_info: {
                        user_id: userInfo.id,
                        display_name: userInfo.profile.display_name,
                        country: $.ssoManager.countryCode,
                        first_name: userInfo.profile.first_name,
                        last_name: userInfo.profile.last_name,
                        email: userInfo.email.address
                    }
                };
            }
            
            $('.cta-add-submittal').removeClass('disabled');
        }

        // login
        $(document).on('click', '.cta-login', function(e){
            e.preventDefault();
            $('.pre-login-nav a').click();
        });

        // add submittal
        $(document).on('click', '.cta-add-submittal', function(e){
            e.preventDefault();
            if(!$(this).hasClass('disabled')){
                var url = $(this).attr('href');
                var data = {
                    document_list: [
                        {
                            document_id: "",
                            document_name: $(this).closest('.c-download-add-submittal-list').find('.resource-title').html(),
                            document_path: $(this).attr('href'),
                            document_url: window.location.origin + $(this).attr('href')
                        }
                    ],
                    user_info: dt.user_info
                }
                $.ajax({
                    url: url + ".json",
                    type: "GET",
                    cache: false,
                    success: function(response) {
                        $('#resources-filter-add-submittal-modal').modal('show');
                        var uuid = response["jcr:uuid"];
                        data.document_list[0].document_id = uuid;
                        $.ajax({
                            url: "/bin/sso/mySubmittal/add",
                            data: JSON.stringify(data),
                            type: "POST",
                            dataType: 'json',
                            contentType: "application/json",
                            cache: false,
                            success: function(response) {
                                console.log('add submittal status', response);
                            }
                        });
                    }
                });
            } else {
                $('#resources-filter-signin-modal').modal('show');
            }
        });


    });
})();




