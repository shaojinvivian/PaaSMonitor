Ext.define('PaaSMonitor.model.Vim', {
	extend : 'Ext.data.Model',
	fields : [{
		name:'id',
		type: 'long'
	},{
		name: 'version',
		type: 'int'
	},{		
		name : 'name',
		type : 'string'
	}, {
		name: 'ip',
		type: 'string'
	}, {
		name : 'isMonitee',
		type : 'bool'
	},{
		name: 'phym',
		type: 'Phym'
	}],
	 proxy: {
        type: 'rest',
        url : 'vims',
         headers: {
        	'Accept': 'application/json'
        },  
        reader: {
            type: 'json',
            root: 'data',
            successProperty: 'success'
        }
    }
});
