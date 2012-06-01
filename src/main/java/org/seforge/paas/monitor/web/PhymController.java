package org.seforge.paas.monitor.web;

import flexjson.JSONSerializer;
import flexjson.transformer.DateTransformer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.seforge.paas.monitor.domain.AppServer;
import org.seforge.paas.monitor.domain.Phym;
import org.seforge.paas.monitor.domain.Vim;
import org.seforge.paas.monitor.extjs.JsonObjectResponse;
import org.seforge.paas.monitor.extjs.TreeNode;
import org.seforge.paas.monitor.service.PhymService;
import org.springframework.beans.factory.annotation.Autowired;
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

@RequestMapping("/phyms")
@Controller
@RooWebJson(jsonObject = Phym.class)
public class PhymController {
	
	@Autowired
    private PhymService phymService;

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> deleteFromJson(@PathVariable("id") Long id) {
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
            List<Phym> records = Phym.findAllPhyms();
            returnStatus = HttpStatus.OK;
            response.setMessage("All Phyms retrieved.");
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
            Phym record = Phym.fromJsonToPhym(json);
            record.setId(null);
            record.setVersion(null);           
            record.persist();
            phymService.addVims(record);
            returnStatus = HttpStatus.CREATED;
            response.setMessage(record.getName());
            response.setSuccess(true);
            response.setTotal(1L);
            response.setData(record.getVims());
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
        }
        return new ResponseEntity<String>(new JSONSerializer().include("data.vims").include("data.vims.phym").exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }

    @RequestMapping(method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<java.lang.String> updateFromJson(@RequestBody String json) {
        HttpStatus returnStatus = HttpStatus.BAD_REQUEST;
        JsonObjectResponse response = new JsonObjectResponse();
        try {
            Phym record = Phym.fromJsonToPhym(json);
            Phym mergedRecord = (Phym) record.merge();
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
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setSuccess(false);
            response.setTotal(0L);
        }
        return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").transform(new DateTransformer("MM/dd/yy"), Date.class).serialize(response), returnStatus);
    }
}
