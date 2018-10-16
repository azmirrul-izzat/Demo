var SlingSettingsService = Packages.org.apache.sling.settings.SlingSettingsService;
var Jsoup = Packages.org.jsoup.Jsoup;

"use strict";
use(function(){

    var 
    	svgPath = '/content/dam/hlb/global/images/icons/icons.svg',
        getSVGId = function(pngPath) {
			
			if(!pngPath) return '';
			
			var startPos = pngPath.lastIndexOf('/') + 1,
            	endPos = pngPath.indexOf('.');

            return (startPos<endPos?pngPath.substring(startPos,endPos):'');
        },
        getSVG = function(pngPath,size,title,linkURL,desc,cssClass,alt,target,tabindex,fill){
	            var
	                thisId = getSVGId(pngPath),
	                thisSVG = svgPath + '#' + thisId,
	                iconSize = 'icon-l';

	            if(size!=null) {
	            	iconSize = 'icon-' + size;
	            }
                if(!cssClass) {
                    cssClass='';
                }

                if(!fill) {
    				fill = 'currentColor';
                }

                var titleAttribute = "";
                if(title) titleAttribute = "title='"+title+"'";
                else if(alt) titleAttribute = "title='"+alt+"'";
                var result = '<svg ' + titleAttribute+ ' class="icon '+ iconSize +' hide-on-fallback ' + cssClass + '" role="img"';
                if(alt) result += ' title="' + alt + '"';
            	if(tabindex) result += ' tabindex="' + tabindex + '"';
                result += '><g>';
                if(title) result += '<title>' + title + '</title>';
                if(desc) result += '<desc>' + desc + '</desc>';
                result += '<use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="'+thisSVG+'" fill="'+fill+'" ></use>';
	            result += '<image class="icon '+ iconSize +' ' + cssClass + '" src="'+ pngPath +'" alt="' + alt + '" xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href=""';
                result += '></image>';
	            result += '</g></svg>';

                if(linkURL) {
                    if(linkURL.indexOf("/") == 0 && linkURL.indexOf(".html") != (linkURL.length - 5)) linkURL += ".html";
                    result = '<a href="' + linkURL + '"' + (target == '_blank' ? ' target="_blank"':'') + '>' + result;
                    result += '</a>';
                }

	            return result;
        },
        hashCode = function(s){
          return s.split("").reduce(function(a,b){a=((a<<5)-a)+b.charCodeAt(0);return a&a},0).toString().replace('-','');
        },

        findCookie = function(_key) {
            var key = "aiaform_"+_key;
            var cookies = request.getCookies();
            for(var i=0;i<cookies.length;i++) {
                var cookie = cookies[i];
                if(cookie.getName() == key)
                    return cookie;
            }
            return null;
        },
        getPaddingClasses = function(d,t,m) {
            var padding = [];
            if(d) {
				padding.push('p-no-md-only');
            }
            if(t) {
				padding.push('p-no-sm-only');
            }
            if(m) {
				padding.push('p-no-xs-only'); 
            }
            return padding.join(' ');
        },
        getRowPaddingClasses = function(d1,t1,m1,d2,t2,m2,d3,t3,m3) {
            var padding = [];
            if(d1 || d2 || d3) {
				padding.push('m-side-no-md-only');
            }
            if(t1 || t2 || t3) {
				padding.push('m-side-no-sm-only');
            }
            if(m1 || m2 || m3) {
				//padding.push('m-side-no-xs-only');
            }
            return padding.join(' ');
        },
        decorateSVGClassPath = function(class_name)
        {
        	var svgIconPath = 'ico-' + class_name
        	return svgIconPath;
        },
        decorateDatasourceWithSVGPath = function(path)
        {
        	var datasourceWithSVGPath = path + '/jcr:content/full-content/datasourcewithsvg/dropdownValue'
        	return datasourceWithSVGPath;
        },
        decorateLink = function(path)
    	{
    		if(!path.startsWith("http") && !path.endsWith("pdf") && !path.includes("#"))
    		{
    			return path + ".html";
    		}
    		return path;
    	}
    return {
        getSVG: getSVG,
        getHashCode: hashCode,
        findCookie: findCookie,
		getPaddingClasses: getPaddingClasses,
		decorateSVGClassPath: decorateSVGClassPath,
		decorateDatasourceWithSVGPath: decorateDatasourceWithSVGPath,
		decorateLink: decorateLink,
        getRowPaddingClasses: getRowPaddingClasses
    };
});