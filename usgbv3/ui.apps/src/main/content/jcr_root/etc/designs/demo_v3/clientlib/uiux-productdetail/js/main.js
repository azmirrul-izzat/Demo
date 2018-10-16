// ----------------------------------------------------------------------
// Component: CTA Video Popup
// ----------------------------------------------------------------------
(function () {
  "use strict";
  $(document).ready(function () {
      if ($('.cta-video-popup').length > 0) {
          $('.cta-video-popup').on('click', function () {
              switch ($(this).data('video-type')) {
                  case "youtube":
                      //console.log('click youtube video-id : ' + $(this).data('video-id'));
                      togglePopupYoutubeVideo($(this).data('video-id'),'');
                      $(this).parent().find('.modal').attr('data-video', 'youtube');
                      break;

                  case "assetVideo":
                      //console.log('click assetVideo video-id : ' + $(this).data('video-id'));
                      togglePopupVideo($(this).data('video-id'),'');
                      $(this).parent().find('.modal').attr('data-video', 'assetVideo');
                      break;
              }
          });


          $('.modal-video-popup .close').on('click', function () {
              switch ($(this).closest('.modal').data('video')) {
                  case "youtube":
                      //console.log('close youtube video-id : ' + $(this).closest('.modal').data('video-id'));
                      togglePopupYoutubeVideo($(this).closest('.modal').data('video-id'),'hide');
                      break;

                  case "assetVideo":
                      //console.log('close assetVideo video-id : ' + $(this).closest('.modal').data('video-id'));
                      togglePopupVideo($(this).closest('.modal').data('video-id'),'hide');
                      break;
              }
          });
      }
  });


  function togglePopupVideo(id,state) {
      // if state == 'hide', hide. Else: show video
      if (state == "hide") {
          $('video.modal-video-elem.' + id)[0].pause();
      } else {
          $('video.modal-video-elem.' + id)[0].play();
      }
  }

  function togglePopupYoutubeVideo(id,state) {
      // if state == 'hide', hide. Else: show video
      var func;
      var div = document.getElementById(id);
      var iframe = div.getElementsByTagName("iframe")[0].contentWindow;
      div.style.display = state == 'hide' ? 'none' : '';
      func = state == 'hide' ? 'pauseVideo' : 'playVideo';
      iframe.postMessage('{"event":"command","func":"' + func + '","args":""}', '*');
  }

})();


// ----------------------------------------------------------------------
// Inline videos
// ----------------------------------------------------------------------
(function () {
  "use strict";


  $(document).ready(function () {



      $('.inline-video').on("click", function () {

          var $this = $(this);
          var thumbs = $this.children('.cta-video-inline');
          var video = $this.find('video');

          if ($this.data('video') === "html5") {

              //retain image for video responsiveness
              thumbs.css({
                  "z-index": -1
              });
              video.css({
                  "z-index": 1
              });
              video[0].play();

          } else if ($this.data('video') === "youtube") {

              var videoWrapper = $this.find('.video');
              var video = $this.find('.YouTubeVideoPlayer');
              //YT.Player name parameter must be an ID not class
              var videoIdName = video.attr('id');
              var videoId = video.attr('data-youtubeId');

              //retain image for video responsiveness
              thumbs.css({
                  "z-index": -1
              });
              video.css({
                  "z-index": 1
              });


          }
      });




  })

})();

/*load script before body====================*/
function hookHeadScript(url, async, defer, callback) {

  if (document.getElementsByTagName('html')[0].innerHTML.indexOf(url) === -1) {
      var script = document.createElement("script")
      script.type = "text/javascript";
      script.onload = callback;
      script.src = url;
      if (async === true) {
          script.setAttribute("async", "");
      }
      if (defer === true) {
          script.setAttribute("defer", "");
      }
      document.getElementsByTagName("head")[0].appendChild(script);
  }
}
/*end load script before body====================*/




// ----------------------------------------------------------------------
// Load inline youtube video(s) in background
// ----------------------------------------------------------------------
(function () {
  "use strict";
  $(document).ready(function () {
      $(document).on('click','.youtube-inline-video',function(e){
        $(this).hide();
        $(this).closest('.inline-video').find('.video').html('<iframe width="560" height="316" id="ytvideo" title="YouTube video player" class="video" frameborder="0" allowfullscreen src="http://www.youtube.com/embed/'+$(this).attr("data-youtubeid")+'?autoplay=1"></iframe>').show();
    });
  });
})();


