package myproxy;

import java.lang.Override;

class Flyable$MyProxy implements Flyable {
  private InvocationHandler handler;

  public Flyable$MyProxy(InvocationHandler handler) {
    this.handler = handler;
  }

  @Override
  public void fly(long arg0) {
    try{
    	this.handler.invoke(this, myproxy.Flyable.class.getMethod("fly",long.class), arg0);
    } catch (NoSuchMethodException e) {
    	e.printStackTrace();
    }
  }
}
