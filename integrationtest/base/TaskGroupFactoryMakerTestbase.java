package base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.daisy.streamline.api.tasks.TaskGroupFactoryMakerService;
import org.daisy.streamline.api.tasks.TaskGroupInformation;
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
		assertEquals(10, specs.size());
		
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("text", "obfl").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("txt", "obfl").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("text", "html").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("txt", "html").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("epub", "html").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("obfl", "text").build()));
		
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("dtbook", "obfl").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("xml", "obfl").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("html", "obfl").build()));
		assertTrue(specs.contains(TaskGroupInformation.newConvertBuilder("obfl", "pef").build()));

	}
	
	@Test
	public void testGetFactory() {
		//Setup
		TaskGroupFactoryMakerService factory = getTaskGroupFMS();
		
		//Test
		assertNotNull(factory.getFactory(TaskGroupInformation.newConvertBuilder("xml", "obfl").build()));
		assertNotNull(factory.getFactory(TaskGroupInformation.newConvertBuilder("text", "obfl").build()));
		assertNotNull(factory.getFactory(TaskGroupInformation.newConvertBuilder("txt", "obfl").build()));
		assertNotNull(factory.getFactory(TaskGroupInformation.newConvertBuilder("dtbook", "obfl").build()));
	}

}