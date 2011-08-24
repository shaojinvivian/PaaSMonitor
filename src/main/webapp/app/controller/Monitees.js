Ext.define('PaaSMonitor.controller.Monitees', {
	extend : 'Ext.app.Controller',
	stores : ['Phyms', 'Vims', 'MoniteeTree', 'AppServers'],
	models : ['Phym', 'Vim', 'AppServer'],
	views : [
		'monitee.AddPhym',
		'monitee.AddVims',
		'monitee.ListVimsByPhym', 
		'monitee.ViewMonitees',
		'monitee.AddAppServer'
	],

	init : function() {
		this.control({
			'addPhym button[action=save]' : {
				click : this.savePhym
			},
			'addAppServer button[action=save]' : {
				click : this.saveAppServer
			},
			'addVims button' : {
				click : this.saveVims
			}
		});
	},
	
	savePhym : function(button) {
		var form = button.up('form'), values = form.getValues(), panel = form.up('panel');
		uppanel = panel.up('panel');
		var phym = Ext.ModelManager.create(values, 'PaaSMonitor.model.Phym');
		var store = this.getVimsStore();
		var treeStore = this.getMoniteeTreeStore();
		phym.save({
			success : function(phym, operation) {
				form.getForm().reset();
				store.getProxy().extraParams = {
					findVims : "ByPhym",
					phymId : phym.internalId
				};
				store.load();
				Ext.ComponentManager.get('add_vims-panel').setTitle('Phym ' + phym.get('ip'));
				uppanel.layout.setActiveItem('add_vims-panel');
				store.getProxy().extraParams = {};
				treeStore.load();
			}
		});
	},
	
	saveAppServer : function(button) {
		var form = button.up('form'), values = form.getValues(), panel = form.up('panel');
		uppanel = panel.up('panel');
		var appServer = Ext.ModelManager.create(values, 'PaaSMonitor.model.AppServer');
		var store = this.getVimsStore();
		var treeStore = this.getMoniteeTreeStore();
		appServer.save({
			success : function(appServer, operation) {
				form.getForm().reset();
				/*
				store.getProxy().extraParams = {
					findVims : "ByPhym",
					phymId : phym.internalId
				};
				store.load();
				Ext.ComponentManager.get('add_vims-panel').setTitle('Phym ' + phym.get('ip'));
				uppanel.layout.setActiveItem('add_vims-panel');
				store.getProxy().extraParams = {};
				treeStore.load();
				*/
			}
		});
	},
	
	saveVims : function(button) {
		this.getVimsStore().sync();
		var panel = button.up('panel').up('panel');
		Ext.MessageBox.alert('Success', 'You have successfully added monitees', function() {
			panel.layout.setActiveItem('start-panel');
		});
	}
});
