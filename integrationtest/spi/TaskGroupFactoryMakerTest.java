package spi;

import org.daisy.streamline.api.tasks.TaskGroupFactoryMaker;
import org.daisy.streamline.api.tasks.TaskGroupFactoryMakerService;

import base.TaskGroupFactoryMakerTestbase;

@SuppressWarnings("javadoc")
public class TaskGroupFactoryMakerTest extends TaskGroupFactoryMakerTestbase {

	@Override
	public TaskGroupFactoryMakerService getTaskGroupFMS() {
		return TaskGroupFactoryMaker.newInstance();
	}

}