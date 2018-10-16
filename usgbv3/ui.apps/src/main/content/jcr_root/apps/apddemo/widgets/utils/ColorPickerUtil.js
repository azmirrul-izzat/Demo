"use strict";

use(function () {
    
	/**
	 * 
	 * @param {String} path
	 * @param {String} rgbaVal
	 */
    function getFontColor(path, rgbaVal){
    	var result = null;
    	
    	if( rgbaVal ){
    		log.debug("{rgba:{}}",  rgbaVal);
    		
    	    var hexCol = RGBAToHex(rgbaVal);
    	    
    	    if(hexCol){ 
    	    	log.debug("{hex:{}}",  hexCol);
    	    	    	    	
    	    	var resolverFactory = sling.getService(Packages.org.apache.sling.api.resource.ResourceResolverFactory);
    	    	var resolver = null;
                
                try{
        	    	// TOFIX use getServiceResourceResolver instead
        	    	resolver = resolverFactory.getAdministrativeResourceResolver(null);
        	    	
    	    	    var mappingRes = resolver.getResource(path); 
    	    	    
    	    	    
    	    	    if(resource){  
        	    		var colorList = mappingRes.getChildren();
        	    	    
        	    	    for(var i in colorList ){
        	    	    	var colorRes = colorList[i];
        	    	    	var vm = colorRes.adaptTo(Packages.org.apache.sling.api.resource.ValueMap);
        	    	    	var name = vm.get("name", "");
        	    	    	var value = vm.get("value", "");
        	    	    	if( name.length() && value.length() ){
        	    	    		if( hexCol.equalsIgnoreCase(value) ){
        	    	    			log.debug("fontColor={name:{}, value:{}}",  name, value);
        	    	    			result = name;
        	    	    			break;
        	    	    		}	    		
        	    	    	}	    	
        	    	    }
    	    	    }
    	    	    
	    	    }catch(e){
                    log.error(e);
                }finally{
                    if(resolver != null && resolver.isLive()) {
                        resolver.close();
                    }
                }
    	    }
        }
    	
    	return result;
    }
    
    /**
     * 
	 * @param {Resource} mappingRes
	 * @param {String} rgbaVal
     */
    function getColorName(mappingRes, rgbaVal){
    	var result = null;
    	
    	if( mappingRes && rgbaVal ){
    		log.debug("{rgba:{}}",  rgbaVal);
    		
    	    var hexCol = RGBAToHex(rgbaVal);
    	    
    	    if(hexCol){ 
    	    	log.debug("{hex:{}}",  hexCol);
    	    	
	    		var colorList = mappingRes.getChildren();
	    	    
	    	    for(var i in colorList ){
	    	    	var colorRes = colorList[i];
	    	    	var vm = colorRes.adaptTo(Packages.org.apache.sling.api.resource.ValueMap);
	    	    	var name = vm.get("name", "");
	    	    	var value = vm.get("value", "");
	    	    	if( name.length() && value.length() ){
	    	    		if( hexCol.equalsIgnoreCase(value) ){
	    	    			log.debug("fontColor={name:{}, value:{}}",  name, value);
	    	    			result = name;
	    	    			break;
	    	    		}	    		
	    	    	}	    	
	    	    }
    	    }
        }else{
            log.error("Missing Arguments");
        }
    	
    	return result;
    }
    
    function RGBAToHex(rgbaVal){
    	var start = rgbaVal.indexOf('(');
    	var end = rgbaVal.lastIndexOf(')');
    	var result = null;
    	if( start > 0 && end > 0 ){
    		var rgba = rgbaVal.substring(start + 1, end).split(",");
    		result = "#"+hex(rgba[0])+hex(rgba[1])+hex(rgba[2]);
    	}
    	
	    return result;
    }
    
    function hex(x) {
        return ("0" + parseInt(x, 10).toString(16)).slice(-2);
    }
    
    return { 
        getFontColor: getFontColor,
        getColorName: getColorName,
        RGBAToHex: RGBAToHex
    };
    
});
