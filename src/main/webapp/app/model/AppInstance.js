Ext.define('PaaSMonitor.model.AppInstance', {
	extend : 'Ext.data.Model',
	fields : [{
		name:'id',
		type: 'int'
	},{
		name: 'version',
		type: 'int'	
	},{
		name: 'appServer',
		type: 'AppServer'
	},{
		name: 'isMonitee',
		type: 'bool'	
	},'name', 'docBase', 'displayName'],
	proxy : {
		type : 'rest',
		url : 'appintances',
		headers : {
			'Accept' : 'application/json'
		},
		reader : {
			type : 'json',
			root : 'data',
			successProperty : 'success'
		}
	}

});
