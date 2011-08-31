Ext.define('PaaSMonitor.model.Phym', {
	extend : 'Ext.data.Model',
	fields : [{
		name:'id',
		type: 'int'
	},{
		name: 'version',
		type: 'int'
	},'name', 'ip', 'username', 'password'],
	
	proxy : {
		type : 'rest',
		url : 'phyms',
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
