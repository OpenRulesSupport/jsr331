package com.exigen.ie.tools;




/**
 * This class should be used in methods which may cause exceptions as a result of
 * programming error or system call fails. You don't want to include an exception in a signature
 * of your method but Java will require it from you. Write a try-catch block and
 * wrap throwed exception in this Wrapper
 */

public class RTExceptionWrapper extends RuntimeException
  implements ExceptionWrapper
{

  Throwable _t;

  static public RuntimeException wrap(String s, Throwable t)
  {
    return wrap(s, t, false);
  }

  static public RuntimeException wrap(String s, Throwable t, boolean always)
  {
    if (t instanceof RuntimeException)
      if ((s == null || s.length() == 0) && !always)
        return (RuntimeException)t;
    return new RTExceptionWrapper(s, t);
  }

  public RTExceptionWrapper(String s, Throwable t)
  {
    super(s);
    _t = t;
  }

  public Throwable getTargetException()
  {
    return _t;
  }

  public String getMessage()
  {
    String errMsg = super.getMessage();
    if(_t !=null && (errMsg==null || errMsg.equals("")))
    {
      String subMess = _t.getMessage();
      if(subMess!=null && subMess.length()>0)
        return subMess;
      else
        return _t.getClass().getName();
    }
    return errMsg;
  }

  public void printStackTrace()
  {
   	Log.error(getMessage(),_t);
  }

  public void printStackTrace(java.io.PrintStream s)
  {
    synchronized (s)
    {
      s.println(getMessage());
      _t.printStackTrace(s);
    }
  }

  public void printStackTrace(java.io.PrintWriter s)
  {
    synchronized (s)
    {
      s.println(getMessage());
      _t.printStackTrace(s);
    }
  }

}
