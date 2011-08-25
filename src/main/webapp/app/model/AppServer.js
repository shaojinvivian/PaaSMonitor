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
		type: 'int'
	},{
		name: 'isMonitee',
		type: 'bool'	
	},'name', 'ip'],
	proxy : {
		type : 'rest',
		url : 'appservers',
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
