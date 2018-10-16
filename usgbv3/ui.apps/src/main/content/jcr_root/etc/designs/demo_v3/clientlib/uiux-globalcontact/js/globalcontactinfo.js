(function () {
    "use strict";
    $(document).ready(function () {


		var countryList = [];
        var minHeight = 0;
        var MIN_TABLET = 480;
        var MIN_DESKTOP = 768;
        init(); 

        $(window).on("resize", function() {
        	minHeight = 0;
        	init(); 
        });

		function init() {
            var ctList = [];
            $.each($('.contactDetail'), function(index, value){ 
                if(minHeight < $(this).height()){ 
                    minHeight = $(this).height(); 
                }

                ctList.push($(this).data('country'));
            });

            /* Start - Calculate Height for Row have more than 1 item */
            var cls = $('.contactDetail').attr("class");
            var col_xs = (cls.match(/col-xs-\d+/g)[0].split("-"))[2] == 12;
            var col_sm = (cls.match(/col-sm-\d+/g)[0].split("-"))[2] == 12;
            var col_md = (cls.match(/col-md-\d+/g)[0].split("-"))[2] == 12;

            if((col_md && $(window).width() >= MIN_DESKTOP) || (col_sm && $(window).width() >= MIN_TABLET) || (col_xs && $(window).width() < MIN_TABLET)) {
                console.log("IN");
				$('.contactDetail').css('height','auto');
    		} else {
                console.log("ELSE " + minHeight);
           	 	$('.contactDetail').height(minHeight);
            }
			/* End */

            /* Start - Populate filtering based on existing data */
            if(countryList.length < 1) {
				countryList = $.grep(ctList, function(el, index) {
                    return index == $.inArray(el, ctList);
                });

                $.each( $.unique(countryList).sort(), function(index, value){ 
                    $('.countryDropdown').append('<option value="'+value+'">'+value+'</option>');
                });
            }
            /* End */
        }

        $( ".countryDropdown" ).change(function() {
        	if($('.contactDetail[data-country="'+$(this).val()+'"]').length > 0) {
           	 	$('.contactDetail').fadeOut()
            	$('.contactDetail[data-country="'+$(this).val()+'"]').fadeIn()
            } else {
				$('.contactDetail').fadeIn();
            }
        });

    });
})();