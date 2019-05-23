package proxy;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import proxy.InvocationHandler;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

//动态生成XXXProxy这代理类，并且动态编译，再通过反射创建对象并加载到内存中
//动态生成Java源文件并且排版是一个非常繁琐的工作，为了简化操作
//我们使用 JavaPoet 这个第三方库帮我们生成TimeProxy的源码.
//希望 JavaPoet  不要成为你的负担，不理解 JavaPoet 没有关系，你只要把它当成一个Java源码生成工具使用即可。
public class Proxy {

    //获取代理对象
    public static Object newProxyInstance(Class clazz, InvocationHandler invocationHandler) {


        //动态生成代理类，继承于原类型
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(clazz.getSimpleName().toLowerCase() + "$Proxy").addSuperinterface(clazz);

        //构建一个属性，用于保存执行对象
        FieldSpec fieldSpec = FieldSpec.builder(InvocationHandler.class, "handler", Modifier.PRIVATE).build();

        //添加到类中
        classBuilder.addField(fieldSpec);

        //构建一个构造器，初始化执行对象
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(InvocationHandler.class, "handler")
                .addStatement("this.handler = handler")
                .build();

        //把构造器添加到类中
        classBuilder.addMethod(constructor);

        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            MethodSpec.Builder methodSpec = MethodSpec.methodBuilder(method.getName())
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(method.getReturnType());
            //构造执行方法
            StringBuilder invokeString = new StringBuilder("this.handler.invoke(this, super.getClass().getMethod(\"" +
                    method.getName() + "\",");
            //遍历所有属性
            for (Parameter parameter : method.getParameters()) {
                methodSpec.addParameter(parameter.getType(), parameter.getName(), Modifier.PUBLIC);
                //添加到执行方法中
                invokeString.append(parameter.getName() + ",");
            }
            int i = invokeString.lastIndexOf(",");
            //删除最后一个逗号
            invokeString.deleteCharAt(i);
            invokeString.append(")");
            //填充占位符
            methodSpec.addStatement(invokeString.toString());

            //添加到类中
            classBuilder.addMethod(methodSpec.build());

            //生成java文件，第一个参数是包名
            JavaFile javaFile = JavaFile.builder("proxy", classBuilder.build()).build();

            try {
                javaFile.writeTo(new File("C:\\Users\\bai\\Desktop\\"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
