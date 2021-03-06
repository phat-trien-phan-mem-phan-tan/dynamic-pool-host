package vn.edu.hust.student.dynamicpool.exeption;

public class DALException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 264604355194982107L;

	private String message;
	private Exception innerException;
	
	public DALException(String message, Exception inner){
		setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Exception getInnerException() {
		return innerException;
	}

	public void setInnerException(Exception innerException) {
		this.innerException = innerException;
	}
}
