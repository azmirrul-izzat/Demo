// ----------------------------------------------------------------------
// Compare Product Page 
// ----------------------------------------------------------------------
// ----------------------------------------------------------------------
(function () {
    "use strict";
    $(document).ready(function () {

			//console.log("aaa");
            var url_string = window.location.href;
            var url = new URL(url_string);
            var c = url.searchParams.get("category");
            var cc = url.searchParams.get("countrycode");
            var ccc = url.searchParams.get("key");
            
            var data;
            var getJSONarr = [];
            var currOnStageMainResultData = [];
            var currCategoryKey = c+"_"+cc ;


            $(".default-col").hide();

            if(ccc){
				getShareCompare();
            }else{
                getLocalCompare();
            }


            function getLocalCompare() {
                if (localStorage.getItem(currCategoryKey) === null) {
                    //console.log("localempty")
                    $(".default-col").show();
                } else {
                    $(".default-col").hide();


                    //get from local storage
                    data = JSON.parse(localStorage.getItem(currCategoryKey));
                    //console.log("data1-", data);

                    getJSONarr = data.map(function (getJSONarr) {
                        return getJSONarr.url.split(".").reverse()[1] + ".properties_v3.json";
                    });


                    if (data.length < 1) {
                        //console.log("nomore");
                        localStorage.removeItem(currCategoryKey);
                        $(".compare-product-col").remove();
                        $(".default-col").show();

                    }

                    loadLocalCompare(getJSONarr);
                }
            }

        	function getShareCompare() {

                $.ajax({
                    url: "/bin/usgb/v3/shareService",
                    type: "GET",
                    data: 'rType=get&key=' + ccc,
                    cache: false,
                    success: function(response) {
                        console.log(response.path);
                        data = response.path;
                        if(data == null || data.length == 0){
                            $(".default-col").show();
                        } else {
                            $(".default-col").hide();

                            //get from local storage
                            //data = JSON.parse(localStorage.getItem(currCategoryKey));
                            console.log("path = ", data);
        

        
        
                            if (data.length < 1) {
                                //console.log("nomore");
                                localStorage.removeItem(currCategoryKey);
                                $(".compare-product-col").remove();
                                $(".default-col").show();
        
                            }
        
                            loadLocalCompare(data);

                			$( document ).ajaxComplete(function() {
                              $(".close-btn-box").addClass("hidden");
                              $(".product-compare-content a").removeAttr("href");
                              $(".product-compare-content a").removeAttr("onclick");
                            });

                			$(".emailCompare").addClass("hidden");


                        }

                    }
                });
            }


            function loadLocalCompare(compareData) {
                //data = JSON.parse(localStorage.getItem('ceiling')) || [];
                // getJSONarr = data.map(function (getJSONarr) {
                // return getJSONarr.url.split(".").reverse()[1] + ".json";
                //  });

                //after ajax get all data in array, run pagination
                getAll(compareData).done(function (results) {

                    currOnStageMainResultData = results;

                    if (currOnStageMainResultData.length > 0) {
                        //load the compare
                        var compareContent = $('#compare-product-col').html();
                        var temptcompareContent = Handlebars.compile(compareContent);
                        $('.compare-product-col').html(temptcompareContent(results));

                    }

                    switch (currOnStageMainResultData.length) {
                        case 1:
                            $(".compare-one").hide();
                            break;
                        case 2:
                            $(".compare-one , .compare-two").hide();
                            break;
                        case 3:
                            $(".compare-one , .compare-two, .compare-three").hide();
                            break;
                    }


                    $('[data-div-height="physicalProperties"]').matchHeight();
                    $('[data-div-height="featureAndBenefits"]').matchHeight();
                    $('[data-div-height="applications"]').matchHeight();
                    $('[data-div-height="resourceList"]').matchHeight();
                    $('[data-div-height="sustainability"]').matchHeight();
                    $('[data-div-height="wrapper"]').matchHeight();



                });


                // iterate thru all  json list
                function getAll(requests) {
                    var count = requests.length;
                    var results = [];
                    var deferreds = [];
                    var all_done = $.Deferred();

                    for (var i = 0; i < requests.length; i++) {
                        var deferred = $.ajax({
                                url: requests[i],
                                type: "GET",
                                cache: false,
                            })
                            .done(function (data) {
                                results.push(data);

                            })
                            .always(function () {
                                count--;

                                if (count === 0) {
                                    all_done.resolve(results);
                                }
                            });
                        deferreds.push(deferred);
                    }
                    return all_done.promise();
                }

            }


            // close btn on popup to remove content
            $("body").on("click", ".close-btn-box", function () {
                event.preventDefault();

                var btnURL = $(this).closest("button").attr("href");

                // var getIndex1 = data.findIndex(x => x.url == btnURL);
                var getIndex1 = data.findIndex(function (x) {
                    return x.url == btnURL;
                });
                data.splice(getIndex1, 1);
                //console.log("btn-", btnURL);

                localStorage.setItem(currCategoryKey, JSON.stringify(data));
                getLocalCompare();

            });

        	$("body").on("click", ".emailCompare", function () {
                event.preventDefault();

                var jsonpath = new Object();
                jsonpath.paths = getJSONarr;

                var paths = JSON.stringify(jsonpath);

                    $.ajax({
                        url: "/bin/usgb/v3/shareService",
                        type: "GET",
                        data: 'rType=write&paths='+paths,
                        cache: false,
                        success: function(response) {
                            var compareUrl = window.location.href.split('?')[0] + "?key=" + response.key;
                        	window.location = 'mailto:?subject=My USG Boral Compare&body='+compareUrl;
                        	//window.open('mailto:?subject=My USG Boral Compare&body='+compareUrl);

                        }
                    });

                console.log("jsonpath : " + JSON.stringify(jsonpath));
            });





    });
})();