Ext.define('PaaSMonitor.model.AppServer', {
	extend : 'Ext.data.Model',
	fields : [{
		name:'id',
		type: 'int'
	},{
		name: 'version',
		type: 'int'
	},{
		name: 'jmxPort',
		type: 'string'	
	},{
		name: 'isMonitee',
		type: 'bool'	
	},'name', 'ip', 'status'],
	proxy : {
		type : 'rest',
		url : 'appservers',
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
