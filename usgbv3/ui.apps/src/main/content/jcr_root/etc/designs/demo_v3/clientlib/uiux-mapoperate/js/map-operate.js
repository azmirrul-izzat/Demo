function initializeMap() {

  $('.map-operate-container').each(function(k, v){

    var Map;

    // Icon parameters
    var iconURL = $(this).attr('data-icon-url');
    var iconAlt = $(this).attr('data-icon-alt');

    // Info Window parameters
    var infoWindowString = $(this).attr('data-info-window');

    // Map parameters
    var latitude = $(this).attr('data-latitude');
    var longitude = $(this).attr('data-longitude');
    var latitudeAndLongitude = {lat: parseFloat(latitude), lng: parseFloat(longitude)};
    var zoom = parseInt($(this).attr('data-zoom'));

    var canvasId = $(this).find('.map-canvas').attr('id');

    // Map
    Map = new google.maps.Map(document.getElementById(canvasId), {
      center: latitudeAndLongitude,
      zoom: zoom
    });

    // Icon (marker)
    if(iconURL != undefined && iconURL != "") {

      var Marker = new google.maps.Marker({
    		content: iconAlt,
    		position: latitudeAndLongitude,
    	    map: Map,
    	    icon: iconURL
    	});

      // Info window
      if(infoWindowString != undefined && infoWindowString != "") {

        var InfoWindow = new google.maps.InfoWindow({
          content: infoWindowString
        });
        
        InfoWindow.open(Map, Marker);
        
        // Click event
        Marker.addListener('click', function() {
          InfoWindow.open(Map, Marker);
        });

      }

    }

  });

};