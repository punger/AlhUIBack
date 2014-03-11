package xxx.pju.alhambra.uibe;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class AlhController {
    protected final Log logger = LogFactory.getLog(getClass());

        @RequestMapping(method = RequestMethod.GET)
        public String hello(ModelMap model) {

                model.addAttribute("message", "Hey there. How's it going?");
                return "welcome";

        }
}
