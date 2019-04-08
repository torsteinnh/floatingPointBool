package com.booleanbyte.worldsynth.module;

public class ClassNotModuleExeption extends Exception{
	private static final long serialVersionUID = 2959873093248591632L;

	/**
     * Constructs an {@code ClassNotModuleExeption} with {@code null}
     * as its error detail message.
     */
    public ClassNotModuleExeption() {
        super();
    }

    /**
     * Constructs an {@code ClassNotModuleExeption} with the specified detail message.
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     */
    public ClassNotModuleExeption(String message) {
        super(message);
    }

    /**
     * Constructs an {@code ClassNotModuleExeption} with the specified detail message
     * and cause.
     *
     * <p> Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated into this exception's detail
     * message.
     *
     * @param message
     *        The detail message (which is saved for later retrieval
     *        by the {@link #getMessage()} method)
     *
     * @param cause
     *        The cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A null value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     */
    public ClassNotModuleExeption(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an {@code ClassNotModuleExeption} with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of {@code cause}).
     *
     * @param cause
     *        The cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A null value is permitted,
     *        and indicates that the cause is nonexistent or unknown.)
     */
    public ClassNotModuleExeption(Throwable cause) {
        super(cause);
    }
}
