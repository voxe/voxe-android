package org.voxe.android.data;

public class TaskResult<T> {
	
	public static <T> TaskResult<T> fromResult(T result) {
		return new TaskResult<T>(result);
	}
	
	public static <T> TaskResult<T> fromException(Exception e) {
		return new TaskResult<T>(e);
	}
	
	private final Object resultOrException;
	
	private final boolean isResult;
	
	private TaskResult(T result) {
		resultOrException = result;
		isResult = true;
	}
	
	private TaskResult(Exception exception) {
		resultOrException = exception;
		isResult = false;
	}
	
	public boolean isException() {
		return !isResult;
	}
	
	public boolean isResult() {
		return isResult;
	}
	
	@SuppressWarnings("unchecked")
	public T asResult() {
		return (T) resultOrException;
	}
	
	public Exception asException() {
		return (Exception) resultOrException;
	}
	
	
	
	
}
