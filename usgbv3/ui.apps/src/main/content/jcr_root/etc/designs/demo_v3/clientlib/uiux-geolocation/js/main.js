// ----------------------------------------------------------------------
// Component:  Global Landing 
// ----------------------------------------------------------------------

(function () {
    "use strict";
    $(document).ready(function () {

        function globalLandingCard() {
            function ipLookUp() {
                $.ajax('https://geoip.nekudo.com/api')
                    .then(
                        function success(response) {
                            //console.log('User\'s Location Data is ', response);
                            //console.log('User\'s Country', response.country.code);
                            var countryCode = response.country.code;
                            var countryName = response.country.name;
                            //console.log("cc", countryCode, "cn", countryName);

                            $.ajax({
                                url: "/bin/usgb/v3/getGeoCountryContent",
                                data: "countrycode=" + countryCode + "&country=" + countryName + "",
                                type: "GET",
                                cache: false,
                                success: function (response) {

                                    var globalLanding = $('#global-landing').html();
                                    var TemptglobalLanding = Handlebars.compile(globalLanding);
                                    $('.global-landing').html(TemptglobalLanding(response));

                                },
                                beforeSend: function () {},
                                complete: function () {}
                            });

                        },

                        function fail(data, status) {
                            console.log('Request failed.  Returned status of', status);
                        }
                    );
            }
            ipLookUp();


        }


        if ($('.global-location-card').length) globalLandingCard();

    });
})();