package org.seforge.paas.monitor.web;

import org.seforge.paas.monitor.domain.App;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RooWebScaffold(path = "apps", formBackingObject = App.class)
@RequestMapping("/apps")
@Controller
public class AppController {
}
