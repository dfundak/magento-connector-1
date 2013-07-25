package org.mule.module.magento.automation.testcases;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mule.api.MuleEvent;
import org.mule.api.processor.MessageProcessor;

import com.magento.api.CatalogProductImageEntity;

public class GetProductAttributeMediaTestCases extends MagentoTestParent {

	@SuppressWarnings("unchecked")
	@Before
	// This test depends on there being an img.gif file in the classpath (when writing this test there was such a file in src/test/resources).
	public void setUp() {
		try {
			testObjects = (HashMap<String, Object>) context.getBean("getProductAttributeMedia");
			MessageProcessor createProductFlow = lookupFlowConstruct("create-product");
			MuleEvent response = createProductFlow.process(getTestEvent(testObjects));
			response.getMessage().getPayload();
			
			testObjects.put("productId", response.getMessage().getPayload());
			
			MessageProcessor flow = lookupFlowConstruct("create-product-attribute-media");
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("img.gif");
			MuleEvent event = getTestEvent(is);
			
			for(String key : testObjects.keySet()) {
				event.setFlowVariable(key, testObjects.get(key));
			}
			
			MuleEvent responseFileName = flow.process(event);
			String newImageFilename = (String) responseFileName.getMessage().getPayload();
			
			testObjects.put("fileName", newImageFilename);
			
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Category({RegressionTests.class})
	@Test
	public void testGetProductAttributeMedia() {
		try {
			MessageProcessor flow = lookupFlowConstruct("get-product-attribute-media");
			MuleEvent response = flow.process(getTestEvent(testObjects));
			CatalogProductImageEntity result = (CatalogProductImageEntity) response.getMessage().getPayload();
			assertNotNull(result);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@After
	public void tearDown() {
		try {
			int productId = (Integer) testObjects.get("productId");
			deleteProductById(productId);
		}
		catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
}
