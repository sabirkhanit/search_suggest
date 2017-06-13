package suggest.spellchecker.controller;



import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;




import org.springframework.test.web.servlet.MvcResult;



import suggest.spellchecker.service.IndexerService;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;





@RunWith(SpringRunner.class)
@WebMvcTest(value=IndexController.class,secure=false)
public class IndexControllerTest {

	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private IndexerService indexService;
	
	@Autowired private IndexController controller;
	
	@Before
	public void setUp()  {
		
	}
	
	@Test
	public void testBuildBaselineIndex_success_isFileIndex_false() throws Exception{
		
		MvcResult result = mockMvc.perform(post("/index/indexfile").accept(MediaType.APPLICATION_JSON).content("").contentType(MediaType.APPLICATION_JSON)).andReturn();
		
		assertNotNull(result.getResponse());
	}
	
	@Test
	public void testBuildBaselineIndex_success_isFileIndex_true() throws Exception{
		
		when(indexService.indexWordsFile()).thenReturn(true);
		

		ReflectionTestUtils.setField(controller, "isFileIndex", Boolean.TRUE);
		
		MvcResult result = mockMvc.perform(post("/index/indexfile").accept(MediaType.APPLICATION_JSON).content("").contentType(MediaType.APPLICATION_JSON)).andReturn();
		
		assertNotNull(result.getResponse());
		
		ReflectionTestUtils.setField(controller, "isFileIndex", Boolean.FALSE);
	}
	
	@Test
	public void testBuildBaselineIndex_fail_isFileIndex_true() throws Exception{
		
		//This will force exception handler method be called 
		// IOException -> SuggestAPIRuntimeException -> handler method 
		when(indexService.indexWordsFile()).thenThrow(IOException.class);
		
		ReflectionTestUtils.setField(controller, "isFileIndex", Boolean.TRUE);
		
		MvcResult result = mockMvc.perform(post("/index/indexfile").accept(MediaType.APPLICATION_JSON).content("").contentType(MediaType.APPLICATION_JSON)).andReturn();
		
		ReflectionTestUtils.setField(controller, "isFileIndex", Boolean.FALSE);
	}

}
