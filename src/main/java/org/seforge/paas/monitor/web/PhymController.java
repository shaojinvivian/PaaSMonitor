package org.seforge.paas.monitor.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.seforge.paas.monitor.extjs.JsonObjectResponse;
import org.seforge.paas.monitor.extjs.TreeNode;
import org.seforge.paas.monitor.domain.Phym;
import org.seforge.paas.monitor.domain.Vim;
import org.seforge.paas.monitor.service.PhymService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.beans.factory.annotation.Autowired;


import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;

@RooWebScaffold(path = "phyms", formBackingObject = Phym.class)
@RequestMapping("/phyms")
@Controller
public class PhymController {
private PhymService phymService;
	
	@Autowired
	public void setPhymSerivce(PhymService phymService){
		this.phymService = phymService;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<String> deleteFromJson(@PathVariable("id") Long id) {
		HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
		
		JsonObjectResponse response = new JsonObjectResponse();
		try {
			Phym record = Phym.findPhym(id);
			record.remove();
            returnStatus = HttpStatus.OK;
			response.setMessage("Phym deleted.");
			response.setSuccess(true);
			response.setTotal(1L);
			response.setData(record);
		} catch(Exception e) {
			response.setMessage(e.getMessage());
			response.setSuccess(false);
			response.setTotal(0L);
		}
		
		// Return just the deleted id
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").include("data.id").exclude("data.*").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }

	@RequestMapping(headers = "Accept=application/json")
    public ResponseEntity<String> listJson() {
		HttpStatus returnStatus = HttpStatus.OK;
		JsonObjectResponse response = new JsonObjectResponse();

		try {
			List<Phym> records = Phym.findAllPhyms();
            returnStatus = HttpStatus.OK;
			response.setMessage("All Phyms retrieved.");
			response.setSuccess(true);
			response.setTotal(records.size());
			response.setData(records);
		} catch(Exception e) {
			response.setMessage(e.getMessage());
			response.setSuccess(false);
			response.setTotal(0L);
		}
		
		// Return list of retrieved performance areas
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
	
	}

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> createFromJson(@RequestBody String json) {
		HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
		
		JsonObjectResponse response = new JsonObjectResponse();
		try {
			Phym record = Phym.fromJsonToPhym(json);
			record.setId(null);
			record.setVersion(null);
			phymService.addVims(record);
			record.persist();
            returnStatus = HttpStatus.CREATED;
			response.setMessage("Phym created.");
			response.setSuccess(true);
			response.setTotal(1L);
			response.setData(record);
		} catch(Exception e) {
			response.setMessage(e.getMessage());
			response.setSuccess(false);
			response.setTotal(0L);
		}
		// return the created record with the new system generated id
        return new ResponseEntity<String>(new JSONSerializer().include("data.vims").exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }
	
	@RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
	public ResponseEntity<String> updateFromJson(@RequestBody String json) {
		HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
		
		JsonObjectResponse response = new JsonObjectResponse();
		try {
			Phym record = Phym.fromJsonToPhym(json);
			Phym mergedRecord = (Phym)record.merge();
	        if (mergedRecord == null) {
	            returnStatus = HttpStatus.NOT_FOUND;
				response.setMessage("Phym update failed.");
				response.setSuccess(false);
				response.setTotal(0L);
	        } else {
	            returnStatus = HttpStatus.OK;
				response.setMessage("Phym updated.");
				response.setSuccess(true);
				response.setTotal(1L);
				response.setData(mergedRecord);
	        }
		} catch(Exception e) {
			response.setMessage(e.getMessage());
			response.setSuccess(false);
			response.setTotal(0L);
		}
		// return the updated record
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }
	
	@RequestMapping(params = "node", method = RequestMethod.GET)
    public ResponseEntity<String> listJsonTree(@RequestParam("node") String nodeId) {		
		HttpStatus returnStatus = HttpStatus.BAD_REQUEST;		
		List<TreeNode> response = null;
		try {
			List<Phym> records = Phym.findAllPhyms();
			// If these is no record now
			if(records.size()<=0){
				response = new ArrayList<TreeNode>();
				TreeNode root = new TreeNode("There are no monitee now");
				root.setLeaf(true);			
				response.add(root);
				returnStatus = HttpStatus.OK;	
			}
			//There are phyms in the db
			else{
				if(nodeId.equals("root")){
					response = new ArrayList<TreeNode>();
					for(Phym phym : records){
						TreeNode phymNode = new TreeNode();
						phymNode.setText(phym.getName());
						phymNode.setId("phym" + phym.getId().toString());	
						phymNode.setLeaf(false);
						phymNode.setExpanded(false);
						response.add(phymNode);
					}
				}else if(nodeId.indexOf("phym")!=-1){
					Long id = Long.valueOf(nodeId.substring(nodeId.indexOf("phym")+4));
					Phym phym = Phym.findPhym(id);
					Set<Vim> vims = phym.getVims();
					if(vims.size()>0){
						response = new ArrayList<TreeNode>();						
						for (Vim vim : vims){
							TreeNode vimNode = new TreeNode();
							vimNode.setText(vim.getName());
							vimNode.setId("vim" + vim.getId().toString());
							vimNode.setLeaf(true);
							response.add(vimNode);
						}						
					}else{
						response = new ArrayList<TreeNode>();
						TreeNode vimNode = new TreeNode();
						vimNode.setText("There is no Virtual Machines");
						vimNode.setLeaf(true);
					}
					
				}
				 returnStatus = HttpStatus.OK;	
				
			}
			
			
			/*
			List<Phym> records = Phym.findAllPhyms();
			response = new ArrayList<TreeNode>();
			for(Phym phym : records){
				TreeNode phymNode = new TreeNode();
				phymNode.setText(phym.getName());
				phymNode.setId(phym.getId().toString());				
				Set<Vim> vims = phym.getVims();
				if(vims.size()>0){
					phymNode.setLeaf(false);
					phymNode.setExpanded(false);
					List<TreeNode> phymChildren = new ArrayList<TreeNode>();
					for (Vim vim : vims){
						TreeNode vimNode = new TreeNode();
						vimNode.setText(vim.getName());
						vimNode.setId(vim.getId().toString());
						phymChildren.add(vimNode);
					}
					phymNode.setChildren(phymChildren);
				}
				response.add(phymNode);
			}
            returnStatus = HttpStatus.OK;	
            */		
		} catch(Exception e) {			
		}
		
		// Return list of retrieved performance areas
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }
}
