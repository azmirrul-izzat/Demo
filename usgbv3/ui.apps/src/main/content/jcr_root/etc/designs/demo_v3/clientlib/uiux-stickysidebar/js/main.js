// ----------------------------------------------------------------------
// Sticky tool
// ----------------------------------------------------------------------

$(window).on('load', function() {

    function makeSticky(){

        var stickySidebar = new StickySidebar('.make-sticky', {
            topSpacing: 105,
            bottomSpacing: 0,
            containerSelector: '.make-sticky_limit',
            innerWrapperSelector: '.make-sticky_inner',
            resizeSensor: true,
            stickyClass: 'is-affixed',
            minWidth: 0
        });

        //console.log('onload');

    }


    if ($('.make-sticky').length){
        
        setTimeout(function(){ makeSticky(); }, 100); //for stupid cacat IE browser
        
    }
});