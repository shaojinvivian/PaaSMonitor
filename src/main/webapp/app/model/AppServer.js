Ext.define('PaaSMonitor.model.AppServer', {
	extend : 'Ext.data.Model',
	fields : [{
		name:'id',
		type: 'int'
	},{
		name: 'version',
		type: 'int'
	},{
		name: 'httpPort',
		type: 'string'
	},{
		name: 'jmxPort',
		type: 'string'
	},{
		name: 'type',
		type: 'string'	
	},{
		name: 'vim',
		type: 'Vim'	
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
