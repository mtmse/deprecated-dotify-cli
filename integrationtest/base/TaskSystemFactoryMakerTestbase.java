package base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.daisy.streamline.api.tasks.InternalTask;
import org.daisy.streamline.api.tasks.TaskSystem;
import org.daisy.streamline.api.tasks.TaskSystemException;
import org.daisy.streamline.api.tasks.TaskSystemFactoryException;
import org.daisy.streamline.api.tasks.TaskSystemFactoryMakerService;
import org.daisy.streamline.engine.TaskRunnerCore;
import org.junit.Test;

@SuppressWarnings("javadoc")
public abstract class TaskSystemFactoryMakerTestbase {
	
	public abstract TaskSystemFactoryMakerService getTaskSystemFMS();

	@Test
	public void testFactoryExists() {
		//Setup
		TaskSystemFactoryMakerService factory = getTaskSystemFMS();
		
		//Test
		assertNotNull("Factory exists.", factory);
	}

	@Test
	public void testGetFactoryForSwedish() throws TaskSystemFactoryException {
		//Setup
		TaskSystemFactoryMakerService factory = getTaskSystemFMS();
		
		//Test
		assertNotNull(factory.getFactory("xml", "pef", "sv-SE"));
	}
	
	@Test
	public void testFactoryForSwedish() throws TaskSystemFactoryException, TaskSystemException {
		//Setup
		TaskSystemFactoryMakerService factory = getTaskSystemFMS();
		TaskSystem tf = factory.newTaskSystem("xml", "pef", "sv-SE");
		assertNotNull(tf);
		Map<String, Object> options = Collections.emptyMap();
		List<InternalTask> tasks = tf.compile(options);
		
		//Test
		assertEquals(2, tasks.size());
		assertEquals("XML Tasks Bundle", tasks.get(0).getName());
		assertEquals("OBFL to PEF converter", tasks.get(1).getName());
	}
	
	@Test
	public void runFactoryForSwedish() throws TaskSystemFactoryException, TaskSystemException, IOException {
		//Setup
		TaskSystemFactoryMakerService factory = getTaskSystemFMS();
		TaskSystem tf = factory.newTaskSystem("xml", "pef", "sv-SE");
		assertNotNull(tf);
		Map<String, Object> options = Collections.emptyMap();
		List<InternalTask> tasks = tf.compile(options);

		File out = File.createTempFile(this.getClass().getName(), ".tmp");
		File f = new File("integrationtest/base/resource-files/dtbook.xml");

		try (TaskRunnerCore core = new TaskRunnerCore(f, out)) {
			for (InternalTask task : tasks) {
				core.runTask(task);
			}
		} finally {
			if (!out.delete()) {
				out.deleteOnExit();
			}
		}
	}
	
	@Test
	public void runFactoryForSwedishEpub() throws TaskSystemFactoryException, TaskSystemException, IOException {
		//Setup
		TaskSystemFactoryMakerService factory = getTaskSystemFMS();
		TaskSystem tf = factory.newTaskSystem("epub", "pef", "sv-SE");
		assertNotNull(tf);
		Map<String, Object> options = Collections.emptyMap();
		List<InternalTask> tasks = tf.compile(options);

		File out = File.createTempFile(this.getClass().getName(), ".tmp");
		File f = new File("integrationtest/base/resource-files/epub.epub");

		try (TaskRunnerCore core = new TaskRunnerCore(f, out)) {
			for (InternalTask task : tasks) {
				core.runTask(task);
			}
		} finally {
			if (!out.delete()) {
				out.deleteOnExit();
			}
		}
	}
	
	@Test
	public void testGetFactoryForEnglish() throws TaskSystemFactoryException {
		//Setup
		TaskSystemFactoryMakerService factory = getTaskSystemFMS();
		
		//Test
		assertNotNull(factory.getFactory("xml", "text", "en-US"));
	}
}