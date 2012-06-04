package org.seforge.paas.monitor.web;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.seforge.paas.monitor.domain.Phym;
import org.seforge.paas.monitor.domain.Vim;
import org.seforge.paas.monitor.extjs.JsonObjectResponse;
import org.seforge.paas.monitor.reference.MoniteeState;
import org.seforge.paas.monitor.service.PhymService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.json.RooWebJson;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/vims")
@Controller
@RooWebJson(jsonObject = Vim.class)
public class VimController {
	
	@Autowired
    private PhymService phymService;

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> deleteFromJson(@PathVariable("id") Long id) {
        HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            Vim record = Vim.findVim(id);
            record.remove();
            returnStatus = HttpStatus.OK;
            response.setMessage("Vim deleted.");
            response.setSuccess(true);
            response.setTotal(1L);
            response.setData(record);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
        }
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").include("data.id").exclude("data.*").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }

    @RequestMapping(headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> listJson() {
        HttpStatus returnStatus = HttpStatus.OK;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            List<Vim> records = Vim.findAllVims();
            returnStatus = HttpStatus.OK;
            response.setMessage("All Vims retrieved.");
            response.setSuccess(true);
            response.setTotal(records.size());
            response.setData(records);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
        }
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }

    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> createFromJson(@RequestBody String json) {
        HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            Vim record = Vim.fromJsonToVim(json);
            Phym phym = Phym.findPhym(record.getPhym().getId());
            record.setPhym(phym);
            record.persist();
            returnStatus = HttpStatus.CREATED;
            response.setMessage("Vim created.");
            response.setSuccess(true);
            response.setTotal(1L);
            response.setData(record);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
        }
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> updateFromJson(@RequestBody String json) {
        HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            Vim record = Vim.fromJsonToVim(json);
            Vim mergedRecord = (Vim) record.merge();
            if (mergedRecord == null) {
                returnStatus = HttpStatus.NOT_FOUND;
                response.setMessage("Vim update failed.");
                response.setSuccess(false);
                response.setTotal(0L);
            } else {
                returnStatus = HttpStatus.OK;
                response.setMessage("Vim updated.");
                response.setSuccess(true);
                response.setTotal(1L);
                response.setData(mergedRecord);
            }
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
        }
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }

    @RequestMapping(params = "list=ByPhym", headers = "Accept=application/json")
    @ResponseBody
    //通过接口获取指定phym上的vim列表
    public ResponseEntity<String> listVimsOnPhym(@RequestParam("phymId") Long phymId) {
    	HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
    	HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
        JsonObjectResponse response = new JsonObjectResponse();
    	try {	    	
	        Phym phym = Phym.findPhym(phymId);
	        phymService.addVims(phym);
	        Set vims = phym.getVims();
	        returnStatus = HttpStatus.OK;
            response.setMessage("All Vims retrieved.");
            response.setSuccess(true);
            response.setTotal(vims.size());
            response.setData(vims);
    	}catch(Exception e){
    		response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
    	}
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), headers, returnStatus);
    }
    
    
    @RequestMapping(params = "find=ByPhym", method = RequestMethod.GET)
    public ResponseEntity<java.lang.String> findVimsByPhymJson(@RequestParam("phymId") Long phymId) {
        HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            Phym phym = Phym.findPhym(phymId);
            Set vims = phym.getVims();            
            returnStatus = HttpStatus.OK;
            response.setMessage("All Vims retrieved.");
            response.setSuccess(true);
            response.setTotal(vims.size());
            response.setData(vims);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
        }
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }

    @RequestMapping(params = "find=ByPhyms", method = RequestMethod.GET)
    public ResponseEntity<java.lang.String> findVimsByPhymsJson(@RequestParam("phymIdList") List<java.lang.String> phymIdList) {
        HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            List<Vim> data = new ArrayList<Vim>();
            for (String phymId : phymIdList) {
                Phym phym = Phym.findPhym(Long.valueOf(phymId));
                Set vims = phym.getVims();
                data.addAll(vims);
            }
            returnStatus = HttpStatus.OK;
            response.setMessage("All Vims retrieved.");
            response.setSuccess(true);
            response.setTotal(data.size());
            response.setData(data);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
        }
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }
}
