package base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.daisy.dotify.api.tasks.TaskGroupFactoryMakerService;
import org.daisy.dotify.api.tasks.TaskGroupInformation;
import org.daisy.dotify.common.text.FilterLocale;
import org.junit.Test;

@SuppressWarnings("javadoc")
public abstract class TaskGroupFactoryMakerTestbase {
	
	public abstract TaskGroupFactoryMakerService getTaskGroupFMS();

	@Test
	public void testFactoryExists() {
		//Setup
		TaskGroupFactoryMakerService factory = getTaskGroupFMS();
		
		//Test
		assertNotNull("Factory exists.", factory);
	}
	
	@Test
	public void testSupportedSpecifications() {
		//Setup
		TaskGroupFactoryMakerService factory = getTaskGroupFMS();
		Set<TaskGroupInformation> specs = factory.listAll();

		//Test
		assertEquals(26, specs.size());
		//TODO: test more specs
		
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("text", "obfl").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("text", "obfl").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("txt", "obfl").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("txt", "obfl").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("epub", "html").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("epub", "html").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("obfl", "text").build()));
		
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("dtbook", "obfl").locale("sv-SE").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("xml", "obfl").locale("sv-SE").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("html", "obfl").locale("sv-SE").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("obfl", "pef").locale("sv-SE").build()));

		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("dtbook", "obfl").locale("en-US").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("xml", "obfl").locale("en-US").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("html", "obfl").locale("en-US").build()));
	}
	
	@Test
	public void testGetFactoryForSwedish() {
		//Setup
		TaskGroupFactoryMakerService factory = getTaskGroupFMS();
		FilterLocale locale = FilterLocale.parse("sv-SE");
		
		//Test
		assertNotNull(factory.getFactory(TaskGroupInformation.newConvertBuilder("xml", "obfl").locale(locale.toString()).build()));
		assertNotNull(factory.getFactory(TaskGroupInformation.newConvertBuilder("text", "obfl").build()));
		assertNotNull(factory.getFactory(TaskGroupInformation.newConvertBuilder("txt", "obfl").build()));
		assertNotNull(factory.getFactory(TaskGroupInformation.newConvertBuilder("dtbook", "obfl").locale(locale.toString()).build()));
	}
	
	@Test
	public void testGetFactoryForEnglish() {
		//Setup
		TaskGroupFactoryMakerService factory = getTaskGroupFMS();
		FilterLocale locale = FilterLocale.parse("en-US");
		
		//Test
		assertNotNull(factory.getFactory(TaskGroupInformation.newConvertBuilder("xml", "obfl").locale(locale.toString()).build()));
		assertNotNull(factory.getFactory(TaskGroupInformation.newConvertBuilder("text", "obfl").build()));
		assertNotNull(factory.getFactory(TaskGroupInformation.newConvertBuilder("txt", "obfl").build()));
		assertNotNull(factory.getFactory(TaskGroupInformation.newConvertBuilder("dtbook", "obfl").locale(locale.toString()).build()));
	}
}