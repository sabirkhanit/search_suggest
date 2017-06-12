package suggest.spellchecker.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;



import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.ReflectionUtils;

import suggest.spellchecker.service.IndexerService;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doNothing;



@RunWith(SpringRunner.class)
@WebMvcTest(value=IndexController.class,secure=false)
public class IndexControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private IndexerService indexService;
	
	
	@Before
	public void init(){
		//ReflectionUtils.setField(arg0, IndexController.class, true);
	}
	
	@Test
	public void testBuildInitialIndex_true_success() throws Exception{
		
		doNothing().when(indexService).indexWordsFile();
		
		MvcResult result = mockMvc.perform(post("/index/indexfile").accept(MediaType.APPLICATION_JSON).content("").contentType(MediaType.APPLICATION_JSON)).andReturn();
		
		System.out.println("result.getResponse()="+result.getResponse());
	}
	
	
}
