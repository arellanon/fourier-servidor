package fourier.server;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fourier.util.Fourier;
import fourier.vo.CancionVO;
import fourier.vo.HuellaDigitalVO;

/**
 * REST service provider
 * 
 * Only GET and POST will return values PUT and DELETE will not.
 */
@Controller
public class FourierController {

	protected static Logger logger = Logger.getLogger("controller");
		
	@SuppressWarnings("serial")
	static class ListHUellaDigital extends ArrayList<HuellaDigitalVO> { }
	
	@RequestMapping(value = "/buscar", method = RequestMethod.POST )
	public @ResponseBody String buscar(@RequestBody ListHUellaDigital ListHD) {
		Fourier f = new Fourier();
		CancionVO c = new CancionVO();
		c = f.buscar(ListHD);
		return c.toString();
	}
	
}
