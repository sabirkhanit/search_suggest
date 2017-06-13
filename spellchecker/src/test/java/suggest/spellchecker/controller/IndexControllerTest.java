package suggest.spellchecker.controller;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.BeforeClass;
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
import static org.junit.Assert.*;
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
	
	private static Field isFileIndexField;
	
	
	@BeforeClass
	public void globalSetUp() throws NoSuchFieldException, SecurityException{
		isFileIndexField = IndexController.class.getDeclaredField("isFileIndex");
		isFileIndexField.setAccessible(true);
	}
	
	@Before
	public void init() {
		
	}
	
	@Test
	public void testBuildBaselineIndex_success_isFileIndex_false() throws Exception{
		
		//doNothing().when(indexService).indexWordsFile();
		
		//isFileIndex is false since its not explicitly test and will not be read from properties file
		
		//isFileIndexField.setBoolean(obj, false);
		
		MvcResult result = mockMvc.perform(post("/index/indexfile").accept(MediaType.APPLICATION_JSON).content("").contentType(MediaType.APPLICATION_JSON)).andReturn();
		
		assertNotNull(result.getResponse());
	}
	
	@Test
	public void testBuildBaselineIndex_success_isFileIndex_true() throws Exception{
		
		//doNothing().when(indexService).indexWordsFile();
		
		//isFileIndex is false since its not explicitly test and will not be read from properties file
		
		MvcResult result = mockMvc.perform(post("/index/indexfile").accept(MediaType.APPLICATION_JSON).content("").contentType(MediaType.APPLICATION_JSON)).andReturn();
		
		assertNotNull(result.getResponse());
	}
}
