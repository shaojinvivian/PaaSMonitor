Ext.define('PaaSMonitor.model.AppInstance', {
	extend : 'Ext.data.Model',
	fields : [{
		name:'id',
		type: 'int'
	},{
		name: 'version',
		type: 'int'	
	},{
		name: 'appServerName',
		type: 'string',
		mapping: 'appServer.name'
	},{
		name: 'appServerId',
		type: 'int',
		mapping: 'appServer.id'
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
			successProperty : 'success',
			messageProperty: 'message'
		}
	}

});
