// ----------------------------------------------------------------------
// Order Sample listing
// ----------------------------------------------------------------------
(function(){


    //Check if Element exist on DOM
    if($(".order-steps-wrapper").length>0){

    //Check if the Local storage for Sample Orders list is set
    var sampleOrderList = {};
    if(typeof(Storage) !== "undefined") {
        if (localStorage.sampleOrdersList) {
            sampleOrderList = JSON.parse(localStorage.getItem('sampleOrdersList'));
            console.log(sampleOrderList);
        }
    }

    $(document).ready(function () {

        var stepWrapper = $(".order-steps-wrapper .steps-inner-wrapper");
        var stepOrderSelectWrapper = $(".order-steps-wrapper .step-1-select-wrapper");
        var stepShippingDetailsWrapper = $(".order-steps-wrapper .step-2-shipping-wrapper");
        var stepOrderSummaryWrapper = $(".order-steps-wrapper .step-3-order-summary-wrapper");

        var stepHeaderWrapper = $(".steps-header .steps");
        var stepHeaderSelectOrder = $(".steps-header .step-1-select");
        var stepHeaderShippingDetails = $(".steps-header .step-2-select");
        var stepHeaderOrderSummary = $(".steps-header .step-3-select");

        var shippingDetailsForm = $("#sampleOrderShippingForm");

        var cartEmptyNotificationWrapper = $(".step-1-select-wrapper .cart-empty");

        var orderSubmitFailModal = $("#orderSubmitFailModal");

        var afterSubmitOrderSummaryData = {};


    
        //Check user SSO Login
        if(!$.ssoManager.isLogin){
            
            $('.pre-login-nav a').click();

        } else {

            if ( typeof $.ssoManager.sso.userInfo() !== 'undefined' &&
                 typeof $.ssoManager.sso.userInfo().responseJSON !== 'undefined' && 
                 ($.ssoManager.sso.userInfo().responseJSON).hasOwnProperty('id')) {

                    var userInfo = $.ssoManager.sso.userInfo().responseJSON;

                    //Order select step button
                    var orderSelectBtn = $(".order-steps-wrapper .order-sample-btn");

                    //Check local storage Object if empty
                    if(sampleOrderList.length <= 0){
                        cartEmptyNotificationWrapper.show();
                        orderSelectBtn.hide();
                    }

                    //Shipping Form Fields
                    var formName = shippingDetailsForm.find(".name");
                    var formAddress1 = shippingDetailsForm.find(".address-1");
                    var formAddress2 = shippingDetailsForm.find(".address-2");
                    var formPostalCode = shippingDetailsForm.find(".postal-code");
                    var formCity = shippingDetailsForm.find(".city");
                    var formState = shippingDetailsForm.find(".state");
                    var formCountry = shippingDetailsForm.find(".country");
                    var formRemark = shippingDetailsForm.find(".remark");
                    var formContactNo = shippingDetailsForm.find(".contact-no");
                    var formEmail = shippingDetailsForm.find(".email");

                    //Fill the shipping form with userinfo from sso object
                    formName.val(userInfo.profile.first_name+" "+userInfo.profile.last_name);
                    formAddress1.val(userInfo.profile.address1);
                    formAddress2.val(userInfo.profile.address2 +" "+ userInfo.profile.address3);
                    formPostalCode.val(userInfo.profile.postal_code);
                    formCity.val(userInfo.profile.city);
                    formState.val(userInfo.profile.state);
                    formContactNo.val(userInfo.profile.phone);
                    formEmail.val(userInfo.email.address);
                    

                    //Go to step 2 Shipping details Form
                    orderSelectBtn.on("click", function(event){
                        
                        //Hide all steps
                        stepWrapper.removeClass("step-active");
                        stepHeaderWrapper.removeClass("current-step");

                        //Show next step Shipping details
                        stepShippingDetailsWrapper.addClass("step-active");
                        stepHeaderShippingDetails.addClass("active");
                        stepHeaderShippingDetails.addClass("current-step");

                    });


                    //Back to Step 1 Order select Button
                    var backToOrderSelectBtn = $(".order-steps-wrapper .back-to-order-select");

                    backToOrderSelectBtn.on("click", function(event){
                        
                        //Hide all steps
                        stepWrapper.removeClass("step-active");
                        stepHeaderShippingDetails.removeClass("active");

                        //Show next step Shipping details
                        stepOrderSelectWrapper.addClass("step-active");
                    });


                    //Step 2 Shipping Details Form
                    shippingDetailsForm.validate({
                        ignore: ".ignore",
                        errorElement: "span",
                        errorPlacement: function (error, element) {
                            // Add the `help-block` class to the error element
                            error.addClass("help-block");
        
                            if (element.prop("type") === "checkbox") {
                                error.insertAfter(element.closest('.checkbox'));
                            } else if (element.prop("tagName").toLowerCase() === "select") {
                                error.insertAfter(element);
                            } else {
                                error.insertAfter(element.parent().find('label'));
                            }
                        },
                        highlight: function (element, errorClass, validClass) {
                            $(element).parents(".respond-msg").addClass("has-error").removeClass("has-success");
                        },
                        unhighlight: function (element, errorClass, validClass) {
                            $(element).parents(".respond-msg").addClass("has-success").removeClass("has-error");
                        }
                    });

                    $('#sampleOrderShippingFormSubmit').click(function (e) {
                        
                        e.preventDefault();

                        if (shippingDetailsForm.valid()) {
                            
                            var formData = {};
                            formData.user_info = {
                                "user_id":userInfo.id,
                                "display_name": userInfo.profile.display_name,
                                "country": userInfo.profile.country,
                                "first_name": userInfo.profile.first_name,
                                "last_name": userInfo.profile.last_name,
                                "email": userInfo.email.address
                            }

                            formData.shipping_detail = {
                                "name": formName.val(),
                                "phone_no": formContactNo.val(),
                                "email": formEmail.val(),
                                "address1": formAddress1.val(),
                                "address2": formAddress2.val(),
                                "address3": formAddress2.val(),
                                "city": formCity.val(),
                                "postcode": formPostalCode.val(),
                                "state": formState.val(),
                                "country": formCountry.val(),
                                "remark": formRemark.val()
                            }

                            //Function to setup the sample ordes form array objects
                            formData.order_sample = setFormDataOrderObject("sampleOrdersList");

                            $.ajax({
                                url: "/bin/sso/orderSample/submit",
                                data: JSON.stringify(formData),
                                dataType: 'json',
                                contentType: "application/json",
                                type: "POST",
                                cache: false,
                                success: function (response) {

                                    console.log(response);

                                    if(response.status === "success"){
                                        
                                        //Hide all steps
                                        stepWrapper.removeClass("step-active");
                                        stepHeaderWrapper.removeClass("current-step");

                                        //Show next step Shipping details
                                        stepOrderSummaryWrapper.addClass("step-active");
                                        stepHeaderOrderSummary.addClass("active");
                                        stepHeaderOrderSummary.addClass("current-step");

                                        //convert date string to long date
                                        afterSubmitOrderSummaryData.orderDate = convertDateLong(response.order_date);

                                        afterSubmitOrderSummaryData.orderId = response.order_id;
                                        

                                        afterSubmitOrderSummaryData.shipToInfo = {
                                            "name": formData.shipping_detail.name,
                                            "email": formData.shipping_detail.email,
                                            "contactno": formData.shipping_detail.phone_no,
                                            "address1": formData.shipping_detail.address1,
                                            "address2": formData.shipping_detail.address2,
                                            "postcode": formData.shipping_detail.postcode,
                                            "city": formData.shipping_detail.city,
                                            "state": formData.shipping_detail.state,
                                            "country": formData.shipping_detail.country,
                                            "remark": formData.shipping_detail.remark
                                        };

                                        console.log(afterSubmitOrderSummaryData);

                                        updateViewTemplate($('#submitOrderSummaryTemplate').html(), afterSubmitOrderSummaryData, $('.ship-to-summary') );

                                    } else {

                                        //Sample order Form Submission failed
                                        orderSubmitFailModal.modal('show');

                                    }
                                    
                                },
                                beforeSend: function () {
                                    //$('.loader').fadeIn("fast");
                                },
                                complete: function () {
                                    //$('.loader').fadeOut("slow");
                                }
                            }).fail(function() {

                                //Sample order Form Submission failed
                                orderSubmitFailModal.modal('show');

                            });
                            
                        }

                    });

                }
        }


       
        //Get all Sample orders product details and pass to Template
        getAllSampleOrdersData(sampleOrderList).done(function(results) {
            console.log(results);

            afterSubmitOrderSummaryData.ordersList = results;
            updateViewTemplate($('#sampleOrderTemplate').html(), results, $('.sample-orders-container') );

            var sampleQuantityCtrlSub = $(".sample-quantity .quantity-ctrl.dec-quantity");
            var sampleQuantityCtrlAdd = $(".sample-quantity .quantity-ctrl.inc-quantity");
            var sampleRemoveBtn = $(".sample-remove .remove-selected-sample");

            //Subtract Sample quantity
            sampleQuantityCtrlSub.on("click", function(event){

                event.preventDefault();
                var quantityObject = $(this).next();
                var quantityObjectValue = parseInt(quantityObject.val());
                var productUrl = $(this).parent().parent().data("producturl");

                if(quantityObjectValue > 1){

                    if(addSubtractSampleOrder(-1, productUrl, "sampleOrdersList")){
                        var orderIndex = afterSubmitOrderSummaryData.ordersList.findIndex(i => i.productUrl == productUrl);
                        if( orderIndex > -1){
                            afterSubmitOrderSummaryData.ordersList[orderIndex].quantity = parseInt(afterSubmitOrderSummaryData.ordersList[orderIndex].quantity) - 1;
                        }
                    }

                    quantityObjectValue = quantityObjectValue-1;
                    quantityObject.val(quantityObjectValue);
                }

            });

            //Add Sample quantity
            sampleQuantityCtrlAdd.on("click", function(event){

                event.preventDefault();
                var quantityObject = $(this).prev();
                var quantityObjectValue = parseInt(quantityObject.val());
                var productUrl = $(this).parent().parent().data("producturl");

                if(addSubtractSampleOrder(1, productUrl, "sampleOrdersList")){

                    var orderIndex = afterSubmitOrderSummaryData.ordersList.findIndex(i => i.productUrl == productUrl);
                    if( orderIndex > -1){
                        afterSubmitOrderSummaryData.ordersList[orderIndex].quantity = parseInt(afterSubmitOrderSummaryData.ordersList[orderIndex].quantity) + 1;
                    }

                }
                

                quantityObjectValue = quantityObjectValue+1;
                quantityObject.val(quantityObjectValue);

            });

            //Remove Order sample
            var orderToRemoveObject = {};
            var confirmRemoveOrderModal = $('#removeSampleOrderModal');
            var confirmRemoveOrderCta = confirmRemoveOrderModal.find(".cta-confirm-remove-order");
            
            sampleRemoveBtn.on("click", function(event){
                
                event.preventDefault();

                confirmRemoveOrderModal.modal('show');

                orderToRemoveObject.productUrlRemove = $(this).parent().parent().data("producturl");
                orderToRemoveObject.itemRowObject = $($(this).parent()).parent();

            });

            confirmRemoveOrderCta.on("click", function(event){

                removeSampleOrder(orderToRemoveObject.productUrlRemove, "sampleOrdersList", function(sampleOrderArrayLength){
                    
                    var orderIndex = afterSubmitOrderSummaryData.ordersList.findIndex(i => i.productUrl == orderToRemoveObject.productUrlRemove);
                    if( orderIndex > -1){
                        afterSubmitOrderSummaryData.ordersList.splice(orderIndex, 1);
                    }

                    orderToRemoveObject.itemRowObject.remove();
                    orderToRemoveObject = {};

                    confirmRemoveOrderModal.modal('hide');
                    
                    //Check local storage Object if empty
                    if(sampleOrderArrayLength <= 0){
                        cartEmptyNotificationWrapper.show();
                        orderSelectBtn.hide();
                    }


                });

            });

            confirmRemoveOrderModal.on('hidden.bs.modal', function (e) {
                orderToRemoveObject = {};
            })
            

        });
        

    });


    //To get all the sample order products list json data.
    //Function param is the local storage json parsed object
    function getAllSampleOrdersData(sampleOrdersList) {
        
        var sampleProductsDetails = $.Deferred();
        var tempSampleProductDetails = [];
        var arrayCount = 0;

        sampleOrderList.forEach(function(sampleProduct, index, array){

            $.get(sampleProduct.productUrl, function(){})
            .done(function(data){
                data.quantity = sampleProduct.quantity;
                data.productUrl = sampleProduct.productUrl;
                tempSampleProductDetails.push(data);
            }).always(function(){
                arrayCount++;
                if(arrayCount === array.length){
                    sampleProductsDetails.resolve(tempSampleProductDetails);
                }
            });

        });

        return sampleProductsDetails.promise();
        
    }


    //Function to Add/subtract quantity from Sample order
    function addSubtractSampleOrder(operation, productUrl, sampleOrderLocalStorageName){

        var sampleOrderLocalStorage = localStorage.getItem(sampleOrderLocalStorageName) 
        var sampleOrderLocalStorageJson = JSON.parse(sampleOrderLocalStorage) || [];

        var sampleOrderIndex = sampleOrderLocalStorageJson.findIndex(i => i.productUrl == productUrl);

        if(sampleOrderIndex > -1){

            var sampleOrderCurrentQuantity = parseInt(sampleOrderLocalStorageJson[sampleOrderIndex].quantity);
            
            if(operation === -1){
                sampleOrderLocalStorageJson[sampleOrderIndex].quantity = sampleOrderCurrentQuantity-1;
            }

            if(operation === 1){
                sampleOrderLocalStorageJson[sampleOrderIndex].quantity = sampleOrderCurrentQuantity+1;
            }

            localStorage.setItem(sampleOrderLocalStorageName, JSON.stringify(sampleOrderLocalStorageJson));

            return true;

        } else {
            return false;
        }

    }


    //Function to remove sample order from list
    function removeSampleOrder(productUrl, sampleOrderLocalStorageName, callback){

        var sampleOrderLocalStorage = localStorage.getItem(sampleOrderLocalStorageName); 
        var sampleOrderLocalStorageJson = JSON.parse(sampleOrderLocalStorage) || [];

        var sampleOrderIndex = sampleOrderLocalStorageJson.findIndex(i => i.productUrl == productUrl);

        if(sampleOrderIndex > -1){

            sampleOrderLocalStorageJson.splice(sampleOrderIndex, 1);

        }

        localStorage.setItem(sampleOrderLocalStorageName, JSON.stringify(sampleOrderLocalStorageJson));

        callback(sampleOrderLocalStorageJson.length);

    }

    
    //Function for Handlebar template update on changes
    function updateViewTemplate(html, data, container){

        var template = Handlebars.compile(html);
        container.html(template(data));

    }


    //Function to setup the sample ordes form array objects
    function setFormDataOrderObject(sampleOrderLocalStorageName){

        var sampleOrderLocalStorage = localStorage.getItem(sampleOrderLocalStorageName);
        var sampleOrderLocalStorageJson = JSON.parse(sampleOrderLocalStorage) || [];

        var formDataOrderArrayObject = [];

        sampleOrderLocalStorageJson.forEach(function(sampleProduct, index, array){

            formDataOrderArrayObject[index] = {
                "product": {
                    "product_name": sampleProduct.productName,
                    "product_url": sampleProduct.productUrl,
                    "product_path": "/content/demo/en_my/products/wall/technical/impactstop"
                },
                "quantity": sampleProduct.quantity
            }

        });


        return formDataOrderArrayObject;

    }


    //Function to convert to Long date format
    function convertDateLong(dateString){

        var orderDate = new Date(dateString);
        var options = { year: 'numeric', month: 'short', day: 'numeric' };

        return orderDate.toLocaleDateString("en-GB", options);

    }


    }

})();