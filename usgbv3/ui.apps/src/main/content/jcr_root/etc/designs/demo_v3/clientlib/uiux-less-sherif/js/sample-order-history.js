// ----------------------------------------------------------------------
// Order Sample History
// ----------------------------------------------------------------------
(function(){

    if($(".order-history-wrapper").length > 0){

        $(document).ready(function () { 

            //Check user SSO Login
            if(!$.ssoManager.isLogin){
                    
                $('.pre-login-nav a').click();
    
            } else {
    
                if ( typeof $.ssoManager.sso.userInfo() !== 'undefined' &&
                     typeof $.ssoManager.sso.userInfo().responseJSON !== 'undefined' && 
                     ($.ssoManager.sso.userInfo().responseJSON).hasOwnProperty('id')) {
    
                        var userInfo = $.ssoManager.sso.userInfo().responseJSON;
                        var userInfoCustom = {
                            "user_info": {
                                "user_id": userInfo.id,
                                "display_name": userInfo.profile.display_name,
                                "country": userInfo.profile.country,
                                "first_name": userInfo.profile.first_name,
                                "last_name": userInfo.profile.last_name,
                                "email": userInfo.email.address
                            }
                        }
    
                        var orderHistoryItemTemplate = $("#orderHistoryTemplate");
                        var orderHistoryItemTemplateContainer = $(".orders-history-wrapper");
    
                        var ordersHistoryResultArray = {};
                        var cancelOrderData = {};
    
                        getSampleOrdersHistory(userInfoCustom).done(function(result){
    
                            console.log(result);
                            ordersHistoryResultArray = result.orderSamplesList;
                            var template = Handlebars.compile(orderHistoryItemTemplate.html());
                            orderHistoryItemTemplateContainer.html(template(result.orderSamplesList));
    
                        });
    
    
                        var dropdownSortOrdersOptions = $(".sample-orders-cart-sort .dropdown-menu a");
    
                        dropdownSortOrdersOptions.on("click", function(event){
    
                            switch($(this).data("sortby")){
    
                                case "latest": 
                                    ordersHistoryResultArray.sort(function(a,b){
                                        return b.sort_date - a.sort_date;
                                    });
                                    break;
                                
                                case "oldest":
                                    ordersHistoryResultArray.sort(function(a,b){
                                        return a.sort_date - b.sort_date;
                                    });
                                    break;
    
                            }
    
                            var template = Handlebars.compile(orderHistoryItemTemplate.html());
                            orderHistoryItemTemplateContainer.html(template(ordersHistoryResultArray));
    
                        });
    
    
                        var confirmCancelOrderModal = $("#cancelSampleOrderModal");
                        var confirmCancelOrderCta = $(".cta-confirm-cancel-order");
                        
                        $(document).on("click", ".cancel-order-cta", function(event){
                            
                            var orderid =  $(this).data("orderid") + "";
                            cancelOrderData.user_info = userInfoCustom.user_info;
                            cancelOrderData.order_info = {
                                "order_id": orderid,
                                "status": "Cancelled"
                            }
    
                            confirmCancelOrderModal.modal('show');
    
                        });
    
                        confirmCancelOrderCta.on("click", function(event){
    
                            cancelActiveSampleOrder(cancelOrderData, function(data){
                                if(data.status == "success"){
                                    confirmCancelOrderModal.modal('hide');
                                    location.reload();
                                }
                            });
    
                        });
    
                     }
    
            }
    
        });
    
        //Function to get user Sample orders history
        function getSampleOrdersHistory(userInfoJson){
            
            var productsDetails = $.Deferred();
            var tempProductsDetails = [];
            var arrayCount = 0;
    
            $.ajax({
                url: "/bin/sso/orderSample/retrieve",
                data: JSON.stringify(userInfoJson),
                dataType: 'json',
                contentType: "application/json",
                type: "POST",
                cache: false,
                success: function (response) {
    
                    response.orderSamplesList.forEach(function(orderItem, index, array){
    
                        var orderDate = orderItem.created_date.replace(" ","T");
                        var display_date = new Date(orderDate);
                        var display_date_String = display_date.getDate()+"/"+(display_date.getMonth()+1)+"/"+display_date.getFullYear();
                        
                        orderItem.sort_date = display_date;
                        orderItem.display_date = display_date_String;
    
                        //Check order status if active to allow order cancel
                        if(orderItem.status == "Active"){
                            orderItem.orderStatusActive = true;
                        } else {
                            orderItem.orderStatusActive = false;
                        }
    
                    });
    
                    productsDetails.resolve(response);
                },
                beforeSend: function () {
                    //$('.loader').fadeIn("fast");
                },
                complete: function () {
                    //$('.loader').fadeOut("slow");
                }
            });
    
            return productsDetails.promise();
        }
    
        //Function to cancel the Active sample order request
        function cancelActiveSampleOrder(cancelRequestJson, callback){
    
            $.ajax({
                url: "/bin/sso/orderSample/cancel",
                data: JSON.stringify(cancelRequestJson),
                dataType: 'json',
                contentType: "application/json",
                type: "POST",
                cache: false,
                success: function (response) {
                    callback(response)
                },
                beforeSend: function () {
                    //$('.loader').fadeIn("fast");
                },
                complete: function () {
                    //$('.loader').fadeOut("slow");
                }
            });
    
        }

    }
     
})();