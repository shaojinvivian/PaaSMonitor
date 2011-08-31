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
	}, {
		name : 'powerState',
		type : 'string'	
	}, {
		name : 'phymId',
		type : 'int',
		mapping: 'phym.id'	
	}, {
		name : 'phymName',
		type : 'string',
		mapping: 'phym.name'
	}, {
		name : 'phymIp',
		type : 'string',
		mapping: 'phym.ip'	
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
            successProperty: 'success',
            messageProperty: 'message'
        }
    }
});
