function getRequestParam(paras) {
	var url = location.href;
	var paraString = url.substring(url.indexOf("?") + 1, url.length).split("&");
	var paraObj = {}
	for( i = 0; j = paraString[i]; i++) {
		paraObj[j.substring(0, j.indexOf("=")).toLowerCase()] = j.substring(j.indexOf("=") + 1, j.length);
	}
	var returnValue = paraObj[paras.toLowerCase()];
	if( typeof (returnValue) == "undefined") {
		return "";
	} else {
		return returnValue;
	}
}


function convertToMB(bytes){
	var result = bytes/1024/1024;
	return result;
}


//将字符串s的首字母变成大写
function upperFirstLetter(str) {
	first = str.substring(0, 1).toUpperCase();
	rest = str.substring(1, str.length);
	return first + rest;
}

function lowerFirstLetter(str) {
	first = str.substring(0, 1).toLowerCase();
	rest = str.substring(1, str.length);
	return first + rest;
}
