Ext.define('PaaSMonitor.model.MBeanAttribute', {
			extend : 'Ext.data.Model',
			fields : [{
				name : 'name',
				type : 'string'
			}, {
				name : 'objectName',
				type : 'string'
			}, {
				name : 'version',
				type : 'string'
			}, {
				name : 'type',
				type : 'string'
			}, {
				name : 'code',
				type : 'string'
			}]
		});