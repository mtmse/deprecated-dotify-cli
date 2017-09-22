package spi;

import org.daisy.dotify.api.tasks.TaskGroupFactoryMakerService;
import org.daisy.dotify.consumer.tasks.TaskGroupFactoryMaker;

import base.TaskGroupFactoryMakerTestbase;

@SuppressWarnings("javadoc")
public class TaskGroupFactoryMakerTest extends TaskGroupFactoryMakerTestbase {

	@Override
	public TaskGroupFactoryMakerService getTaskGroupFMS() {
		return TaskGroupFactoryMaker.newInstance();
	}

}