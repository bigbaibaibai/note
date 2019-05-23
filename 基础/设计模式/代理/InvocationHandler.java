package proxy;

import java.lang.reflect.Method;

//执行方法
public interface InvocationHandler {

    Object invoke(Object proxy, Method method, Object... args);

}
