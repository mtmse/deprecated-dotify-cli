package spi;

import org.daisy.streamline.api.tasks.TaskSystemFactoryMaker;
import org.daisy.streamline.api.tasks.TaskSystemFactoryMakerService;

import base.TaskSystemFactoryMakerTestbase;

@SuppressWarnings("javadoc")
public class TaskSystemFactoryMakerTest extends TaskSystemFactoryMakerTestbase {

	@Override
	public TaskSystemFactoryMakerService getTaskSystemFMS() {
		return TaskSystemFactoryMaker.newInstance();
	}
}