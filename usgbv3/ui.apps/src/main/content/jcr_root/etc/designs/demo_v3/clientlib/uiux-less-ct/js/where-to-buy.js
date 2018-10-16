// ----------------------------------------------------------------------
// where to buy
// ----------------------------------------------------------------------
(function () {
    "use strict";

    if($('#filter-list-controller').length > 0){
        var map;
        var infoWindow;
        var markersData = [];
        var filterListingData = [];
        var markers = [];
        var filterForm = $('#filter-list-controller');
        var jsonDataToCall = {};
        var filterData = {};
        var currLocation = null;
        var executedGeo = false;
        var currLocationMarker;
        var proximityLocationMarker;
        var circleOnMap = [];
        var markerCluster;
        var currSenario = "";
        var choosenState = "";
        var choosenProximity = "";
        var searchedText = "";
        var autocompleteData = {};

        var urlString = window.location.href;
        var url = new URL(urlString);
        var countryCode = window.location.href.indexOf("/demo/") > -1? "en_my" : "en_my";
        var locationPath = function() {
            var pathArray = window.location.pathname.split('/').filter(Boolean).slice(0, -1);
            var newPathname = "";
            for (var i = 0; i < pathArray.length; i++) {
                newPathname += "/";
                newPathname += pathArray[i];
            }
            return newPathname;
        }();

        $(document).ready(function(){

            initialize();

            $(document).on('click','.wtb-search-bar .option', function(e){
                e.preventDefault();
                
                $('.wtb-search-bar').addClass('add-bg');
                $('.search-bar-toggle-button').addClass('open');
                $('#input-search-location').val(this.innerHTML); 
                $(this).closest('.dropdown').remove('open');
                currSenario = "searchState";
                choosenState = $(this).html();
                
                resetFilter();
                loadStoreListResult();
            });
        
            $(document).on('change keyup','#input-search-location', function(e){
                e.preventDefault();
                //var num = Number($(this).attr('data-length-autocomplete-start'));
                var target = $(this);
                if ($(this).val()) {
                    $('.wtb-search-bar').addClass('add-bg');

                    AutoComplete({
                        EmptyMessage: "No item found",
                        QueryArg: "text",
                    }, "#input-search-location");

                } else {  
                    $('.wtb-search-bar').removeClass('add-bg').removeClass('open');
                    $('.state-wrap').removeClass('open');
                    $('.search-bar-toggle-button').removeClass('open');
                    $('.filter-list-item').removeClass('open');
                    
                }
            });

            $(document).on('change keyup','#filter-list-controller .checkbox, #filter-list-controller .radio', function(e){
                e.preventDefault();
                var dataFiltered = [];
                var radius = null;

                //clear circle
                console.log('markersData', markersData);

                filterData = $(filterForm).serializeObject();
                console.log('dataSelectedFilter', filterData);
                dataFiltered = multiFilter(markersData, filterData);
                console.log('dataFiltered', dataFiltered);
                displayMarkers(dataFiltered);
                if(dataFiltered.length > 0){
                    var templateSource = $("#infoTemplate").html();
                    var template = Handlebars.compile(templateSource);
                    $('.search-detail-group').html(template(dataFiltered));
                } else {
                    $('.search-detail-group').html('<div class="p-3xl"><h5>Sorry</h5><p><i>No result has been found.</i></p></div>');
                }

                switch (Number($(this).val().replace('KM', ''))) {
                    case 15:
                        radius = 15000;
                        break; 
                    case 25:
                        radius = 25000;
                        break;
                    case 35:
                        radius = 35000;
                }
                if($(this).attr('name') == "distance"){
                    if(circleOnMap.length>0){
                        circleOnMap[0].setMap(null);
                        circleOnMap = [];
                    }

                    if($(this).val().toLowerCase() !== "all"){
                        var circle = new google.maps.Circle({
                            map: map,
                            radius: radius,    // 15km
                            fillColor: '#009b3a',
                            strokeOpacity: 0.35,
                            strokeWeight: 0,
                        });
                        circleOnMap.push(circle);
                        circle.bindTo('center', proximityLocationMarker, 'position');
                    }
                    
                }
                
                
            });
        
            $(document).on('click','.search-bar-toggle-button', function(e){
                e.preventDefault();
                $('.search-detail-wrap').toggleClass('search-detail-wrap-collapse');
                $('.fa-angle-left').toggleClass('arrow-on-off');
                $('.wtb-search-bar').toggleClass('open');
                if($('.filter-back-btn').hasClass('open') || $('.filter-list-item').hasClass('open')){
                    $('.filter-back-btn').removeClass('open');
                    $('.filter-list-item').removeClass('open');
                }
            });
            
            $(document).on('click','.btn-filter', function(e){
                e.preventDefault();
                if(!$(this).hasClass('disabled')){
                    $('.filter-list-item').addClass('open');
                    $('.wtb-search-bar').addClass('add-bg');
                    $('.search-bar-toggle-button').addClass('open');
                    $('.filter-back-btn').addClass('open');
                }
            });
        
            $(document).on('click','.filter-back-btn', function(e){
                e.preventDefault();
                $('.filter-back-btn').removeClass('open');
                $('.filter-list-item').removeClass('open');
            })
        
            $(document).on('click', '#map-search-controller .btn-search', function(e){
                var value = $('#input-search-location').val();
                console.log(wtbAutocompleteData,value);
                //wtbAutocompleteData, variable created at the autocomplte.js
                for(var i=0; i<wtbAutocompleteData.Items.length; i++){
                    var key = findKey(wtbAutocompleteData.Items[i], value);
                    if(key !== null && key.indexOf('proximity') >= 0){
                        currSenario = key;
                        choosenProximity = value;
                    } else if(key !== null){
                        currSenario = key;
                        searchedText = value;
                    }
                }
                resetFilter();
                loadStoreListResult();
            });
            
            $(document).on('click', '.accordion-wrap .btn-accordion', function(e){
                $(this).next().slideToggle('fast').toggleClass('a-collapse');
                $(this).parent().toggleClass('a-collapse');
        
                //Hide the other panels
                $(".content-accordion").not($(this).next()).slideUp('fast').removeClass('a-collapse');
            });

            $('#input-search-location').on('keyup', function(event) {
                var target = $(event.currentTarget);
                var currentValue = target.val();
                var placeholderValue = $('#input-search-location').attr('placeholder');
                
                if (event.keyCode === 13) {
                    // enter pressed
                    if (!currentValue || currentValue === placeholderValue) return;
                
                } else {
                    if (!currentValue || currentValue === placeholderValue || currentValue.length === 1) {
                        $('.inputSearch-myLocation').removeClass('hidden');
                    } else {
                        $('.inputSearch-myLocation').addClass('hidden');
                    }
                }
            });
            
            $('#input-search-location').on('focus', function(event) {
                var target = $(event.currentTarget);
                var currentValue = target.val();
                var placeholderValue = $('#input-search-location').attr('placeholder');
                
                if (!currentValue || currentValue === placeholderValue || currentValue.length === 1) {
                    $('.inputSearch-myLocation').removeClass('hidden');
                } else {
                    $('.inputSearch-myLocation').addClass('hidden');
                }
            });
            
            $('#input-search-location').on('blur', function(event) {
                var to = setTimeout(function() {
                    clearTimeout(to);
                    $('.inputSearch-myLocation').addClass('hidden');
                }, 200);
            });
            
            $('.inputSearch-myLocation').on('click', function(event) {
                $('.wtb-search-bar').addClass('add-bg');
                $('.inputSearch-myLocation').addClass('hidden');
                $('#input-search-location').val('Use My Location');
                currSenario = "searchUserLocation";
                loadStoreListResult();
            });

            $(document).on('click', '.cta-search-detail-off', function(event) {
                event.preventDefault();
                $(this).closest('.search-detail-wrap').find('.state-wrap').removeClass('open');
            });

            $(document).on('click', '.cta-search-detail-on', function(event) {
                event.preventDefault();
                $('.state-wrap').addClass('open');
            });

            if($("#input-search-location").length){
                $.ajax({
                    url: "/bin/usgb/v3/getAllStates",
                    data: "country="+ countryCode,
                    type: "GET",
                    cache: false,
                    success: function (response) {
                        console.log(response);
                        for(var i=0; i<response.length; i++){
                            $('#map-search-controller .dropdown-menu').append('<div class="option">'+ response[i] +'</div>');
                        }
                    },
                    beforeSend: function () {
                        //$('.loader').fadeIn("fast");
                    },
                    complete: function () {
                        //$('.loader').fadeOut("slow");
                    }
                });
            }
        });

        function resetFilter(){
            $('#filter-list-controller .checkbox, #filter-list-controller .radio').prop('checked', false);
        }



        function multiFilter(array, filters) {
            const filterKeys = Object.keys(filters);
            // filters all elements passing the criteria
            return array.filter((item) => {
                // dynamically validate all filter criteria
                return filterKeys.every(function(key){
                    if( Array.isArray(item[key]) && item[key].length > 0 ) {
                        // if data is tagging array, one matches in the array, return the object true
                        for(var i=0; i<item[key].length; i++){
                            if(!!~filters[key].indexOf(item[key][i])){
                                return filters[key];
                            };
                        }
                    } else if(key == "distance"){
                        if(filters[key].toLowerCase() == "all" || filters[key] == ""){
                            return filters[key];
                        } else {
                            return item[key] <= Number(filters[key].replace('KM', ''));
                        }
                    }else {
                        return !!~filters[key].indexOf(item[key]);
                    }
                });
            });
        }


        function initialize() {
            var mapOptions = {
                center: new google.maps.LatLng(3.283038, 108.0),
                zoom: 4,
                mapTypeId: 'roadmap',
            };
            

            infoWindow = new google.maps.InfoWindow;
        
            map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
        
            // a new Info Window is created
            infoWindow = new google.maps.InfoWindow();
        
            // Event that closes the Info Window with a click on the map
            // google.maps.event.addListener(map, 'click', function() {
            // infoWindow.close();
            // });
            // Finally displayMarkers() function is called to begin the markers creation
            if(markersData.length > 0){
                displayMarkers(markersData);
            }
        }

        
        google.maps.event.addDomListener(window, 'load', initialize);
        
        
        // This function will iterate over markersData array
        // creating markers with createMarker function
        function displayMarkers(data){
            clearOverlays();
            //markerCluster.clearMarkers();
            console.log('displayMarkers', data);
            // this variable sets the map bounds according to markers position
            var bounds = new google.maps.LatLngBounds();
            
            // for loop traverses data array calling createMarker function for each marker 
            for (var i = 0; i < data.length; i++){
                var latlng = new google.maps.LatLng(data[i].latitude, data[i].longitude);
                var companyName = data[i].companyName;
                var address1 = data[i].address1;
                var address2 = data[i].address2;
                var phoneNumber = data[i].phoneNumber;
                var email = data[i].email;
                var directionUrl = data[i].directionUrl;
            
                createMarker(latlng, companyName, address1, address2, phoneNumber, email, directionUrl);
            
                // marker position is added to bounds variable
                bounds.extend(latlng);  
            }

            if(markerCluster) markerCluster.clearMarkers();

            markerCluster = new MarkerClusterer(map, markers,
                {imagePath: 'https://developers.google.com/maps/documentation/javascript/examples/markerclusterer/m'});
            
        
            // Finally the bounds variable is used to set the map bounds
            // with fitBounds() function
            map.fitBounds(bounds);
            map.setZoom(8);
        }
        
        // This function creates each marker and it sets their Info Window content
        function createMarker(latlng, companyName, address1, address2, phoneNumber, email, directionUrl){
            var var_pin = '/content/dam/demo/Global/Website/Images/component/allsample/marker.png';
            var marker = new google.maps.Marker({
                map: map,
                position: latlng,
                icon: var_pin,
                title: name
            });

            markers.push(marker);

            google.maps.event.addListener(marker, 'click', function () {
                // Creating the content to be inserted in the infowindow
                var iwContent =
                    '<div id="iw_container">' +
                    '<div class="iw_title">' + companyName + '</div>' +
                    '<div class="iw_content">' +
                    address1 + '<br />' +
                    address2 + '<br /><br />' +
                    '<div class="fa fa-phone icon-size-xs p-right-m"></div>' + phoneNumber + '<br /><br />' +
                    '<div class="fa fa-envelope icon-size-xs p-right-m"></div>' + email + '<br /><br />' +
                    '<a href="' + directionUrl + '" target="_blank"> <div class="rounded-corners bg-primary-green p-s text-center color-white bold"> Get Direction </div></a>' + '</div></div>';

                // pan to clicked marker
                var divHeightOfTheMap = document.getElementById('map-canvas').clientHeight;
                var offSetFromBottom = 100;
                map.setCenter(marker.getPosition());
                if (window.matchMedia("(min-width: 768px)").matches) {
                    map.panBy(-200, -(divHeightOfTheMap / 2 - offSetFromBottom));
                } else {
                    map.panBy(0, -(divHeightOfTheMap / 2 - offSetFromBottom));
                }
                

                // including content to the Info Window.
                infoWindow.setContent(iwContent);
                
                // opening the Info Window in the current map and at the current marker location.
                infoWindow.open(map, marker);
                marker.setIcon('/content/dam/demo/Global/Website/Images/component/allsample/marker-active.png');
            });

            google.maps.event.addListener(map, 'click', function () {
                infoWindow.close();
                //Change the marker icon
                marker.setIcon('/content/dam/demo/Global/Website/Images/component/allsample/marker.png');
            });

            google.maps.event.addListener(infoWindow, 'closeclick', function () {
                infoWindow.close();
                // then, remove the infowindows name from the array
                marker.setIcon('/content/dam/demo/Global/Website/Images/component/allsample/marker.png');
            });
            
        }

        function clearOverlays() {
            for (var i = 0; i < markers.length; i++) {
                markers[i].setMap(null);
            }
            markers.length = 0;
        }

        function handleLocationError(browserHasGeolocation, infoWindow, pos) {
            infoWindow.setPosition(pos);
            infoWindow.setContent(browserHasGeolocation ?
                'Error: The Geolocation service failed.' :
                'Error: Your browser doesn\'t support geolocation.');
            infoWindow.open(map);
        }

        function loadStoreListResult(){
            console.log("jsonDataToCall", jsonDataToCall);

            $.ajax({
                url: "/bin/usgb/v3/storeSearch",
                    
                data:   function(){
                            if(currSenario.indexOf('proximity') >= 0){
                                return "key="+currSenario+"&country=" + countryCode + "&text=" + choosenProximity;
                            } else if(currSenario == "searchState"){
                                return "key=statename&text=" + choosenState + "&country=" + countryCode;
                            } else if(currSenario == "searchUserLocation"){
                                return "country=" + countryCode + "&currentlocation=true";
                            } else {
                                return "key="+currSenario+"&country=" + countryCode + "&text=" + searchedText;
                            }
                        }()


                        ,
                type: "GET",
                cache: false,
                success: function (response) {
                    // 1. data come in
                    markersData = response.storeResults;
                    filterListingData = response.filterListing;

                    if( currSenario.indexOf('proximity') < 0 || currSenario !== "searchUserLocation" ){
                        currLocation = null;
                    }

                    geolocation(function (pos) {
                        if(pos.lat !== null){
                            checkDuoLocation();
                            addDistanceToData();
                            var templateSource = $("#infoTemplate").html();
                            var template = Handlebars.compile(templateSource);
                            $('.search-detail-group').html(template(markersData));
                        }
                    });

                    markers = [];

                    if(markersData.length > 0){
                        // 1. load filter listing with getted data
                        var templateFilter = $("#templWtbFilter").html();
                        var htmlTemplateFilter = Handlebars.compile(templateFilter);
                        $('.filter-list-item').html(htmlTemplateFilter(response.filterListing));
                        if(response.hasOwnProperty('proximityResult')){
                            var pos = {
                                lat: Number(response.proximityResult[0].latitude),
                                lng: Number(response.proximityResult[0].longitude),
                            };
    
                            currLocation = pos;
                        }

                        checkDuoLocation();
                        addDistanceToData();

                        // 2. load map with getted data
                        initialize();
                        console.log(markersData);
                        // 3. load location info with getted data
                        var templateSource = $("#infoTemplate").html();
                        var template = Handlebars.compile(templateSource);
                        $('.search-detail-group').html(template(markersData));
                        $('.cta-search-detail-on').addClass('active');
                        $('.btn-filter').removeClass('disabled');

                        if (window.matchMedia("(min-width: 768px)").matches) {
                            $('.state-wrap').addClass('open');
                        }

                        // bind external cta event
                        var extLocationCta = $('#filter-list-controller .cta-location');
                        $(extLocationCta).off('click').on( "click", function() {
                            var currentMarker = $(extLocationCta).index(this);
                            triggerClick(currentMarker);
                        });

                        if(response.hasOwnProperty('proximityResult')){
                            proximityLocationMarker = new google.maps.Marker({
                                position: currLocation,
                                icon: '/content/dam/demo/Global/Website/Images/component/allsample/icon_marker_yellow.png',
                                map: map
                            });
                            map.setCenter(pos);
                            map.setZoom(8);
                            proximityLocationMarker.setPosition(pos);
                            proximityLocationMarker.setMap(map);
                        }

                    } else {
                        $('.cta-search-detail-on').removeClass('active');
                        $('.btn-filter').addClass('disabled');
                    }
                },
                beforeSend: function () {
                    $('.loader').fadeIn("fast");
                },
                complete: function () {
                    $('.loader').fadeOut("slow");
                }
            });
        }

        

        function triggerClick(index) {
            console.log(markers[index]);
            google.maps.event.trigger(markers[index], 'click');
        }

        function geolocation(callback){
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function(position) {
                    var var_pin = '/content/dam/demo/Global/Website/Images/component/allsample/icon_marker_red.png';
                    currLocationMarker = new google.maps.Marker({
                        map: map,
                        icon: var_pin
                    });
                    var pos = {
                        long: null,
                        lat: null
                    };

                    pos = {
                        lat: position.coords.latitude,
                        lng: position.coords.longitude,
                    };
                    
                    currLocation = pos;

                    currLocationMarker.setPosition(pos);
                    map.setCenter(pos);

                    return callback(pos);
                }, function() {
                    if(!executedGeo){
                        //handleLocationError(true, infoWindow, map.getCenter());
                        executedGeo = true;
                    }
                    
                });
            } else {
                // Browser doesn't support Geolocation
                if(!executedGeo){
                    //handleLocationError(false, infoWindow, map.getCenter());
                    executedGeo = true;
                }
            }
        }
    }

    function addDistanceToData(){
        if(currLocation !== null){
            $('.state-detail--distance').removeClass('hidden');
            markersData.sort(function(a, b) {
                var distanceA = calculateDistance(currLocation.lat, currLocation.lng, Number(a.latitude), Number(a.longitude) , "K");
                var distanceB = calculateDistance(currLocation.lat, currLocation.lng, Number(b.latitude), Number(b.longitude) , "K");
                return distanceA - distanceB;
            });

            // Add distance to all object
            for ( var i = 0; i < markersData.length; i++) {
                markersData[i]["distance"] = Number(calculateDistance(currLocation.lat, currLocation.lng, markersData[i]["latitude"], markersData[i]["longitude"],"K").toFixed(3));
            }

            console.log(markersData);
        }
    }

    function checkDuoLocation() {
        // if have the first location for calculate distance
        if(currLocation !== null){
            $('.state-detail--distance').removeClass('hidden');
        } else {
            $('.state-detail--distance').addClass('hidden');
        }
    }

    function calculateDistance(lat1, lon1, lat2, lon2, unit) {
        var radlat1 = Math.PI * lat1/180;
        var radlat2 = Math.PI * lat2/180;
        var radlon1 = Math.PI * lon1/180;
        var radlon2 = Math.PI * lon2/180;
        var theta = lon1-lon2;
        var radtheta = Math.PI * theta/180;
        var dist = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
        dist = Math.acos(dist);
        dist = dist * 180/Math.PI;
        dist = dist * 60 * 1.1515;
        if (unit=="K") { dist = dist * 1.609344; }
        if (unit=="N") { dist = dist * 0.8684; }
        return dist;
    }

    var findKey = function (obj, value) {
        var key = null;
        for (var prop in obj) {
            if (obj.hasOwnProperty(prop)) {
                if (obj[prop] === value) {
                    key = prop;
                }
            }
        }
        return key;
    };
    
    Handlebars.registerHelper('ifEquals', function(arg1, arg2, options) {
        return (arg1 == arg2) ? options.fn(this) : options.inverse(this);
    });
    
})();

