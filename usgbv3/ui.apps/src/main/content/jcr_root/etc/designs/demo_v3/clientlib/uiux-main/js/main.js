// ----------------------------------------------------------------------
// att: mobile image
// ----------------------------------------------------------------------
(function () {
"use strict";
    $(document).ready(function () {
    
        var mobileWidth = window.matchMedia("(max-width: 768px)").matches;
        var target = $('.mobile-image-enabled');
        for( var i=0, len = target.length; i<len; i++ ){

            var src = $(target[i]).data('img-mobile');

            if( mobileWidth && src.replace(/\s/g,"") != "" ){
            	$(target[i]).attr('style', src);
            } 
        }

    });

})(); 



// ----------------------------------------------------------------------
// att: mobile image
// ----------------------------------------------------------------------
(function () {
"use strict";
    $(document).ready(function () {
        $('.same-height').matchHeight({ byRow: true });
        $('.match-height').matchHeight({ byRow: true });
    });

})(); 

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


// ----------------------------------------------------------------------
// Custom dropdown submission
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

        $(document).on("click", '.dp-form .dp-list a', function () {

            var dataVal = $(this).data("val");
            var dataHtml = $(this).html();
            var $dpText = $(this).parent().parent().prev().find($('.dp-text'));
            var $hiddenField = $dpText.next();

            $dpText.html(dataHtml);
            $hiddenField.val(dataVal);

            //console.log(dataVal);
            // console.log($(this).parent().parent().prev().find($('.dp-text')));

        });
    });
})();

/*imgliquid==========================*/
(function () {
    "use strict";
	$(document).ready(function () {

        $(".imgLiquidFill").imgLiquid();

	});

})();