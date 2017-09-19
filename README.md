# AnnotationDemo
注解与反射

[Annontation注解的应用及介绍](http://blog.csdn.net/awenyini/article/details/77478230)

# 注解

1.注解定义:

```java
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface UserParam {
    String name() default "";
    String phone() default "";
}
```
```java
@Target(METHOD)
@Retention(RUNTIME)
public @interface UserMethod {
    String title() default "";
}
```
2.注解使用
```java
public interface UserInterface {
    @UserMethod(title = "AwenZeng")
    String getUser(@UserParam(name = "刘峰",phone = "110") String a);
}
```
```java
public class User {
    private int id;
    private String name;
    private String phone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @UserMethod(title = "牛B啊！！！")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @UserMethod(title = "中国梦")
    public String getPhone() {
        return phone;
    }

    public void setPhone(@UserParam(name = "李小龙",phone = "999")String phone) {
        this.phone = phone;
    }

    public void print(@UserParam(name = "刘凤",phone = "110") String temp){
        Log.e("User", "User: "+id+","+name+","+phone+","+temp);
    }
}
```
3.注解获取

i.直接获取
```java
            Class c1 = Class.forName("com.awen.annotationdemo.User");
            Method method =  c1.getDeclaredMethod("getName");
            UserMethod userMethod = method.getAnnotation(UserMethod.class);
            if (userMethod != null) {
                Log.e(TAG, "注解方法：" + userMethod.title() + "\n");
            }

            Method method1 =  c1.getDeclaredMethod("print",String.class);//获取单个方法
            Annotation[][] parameterAnnotationsArray = method1.getParameterAnnotations();//拿到参数注解
            if (parameterAnnotationsArray != null) {
                for (int i = 0; i < parameterAnnotationsArray.length; i++) {
                    Annotation[] annotations = parameterAnnotationsArray[i];
                    if (annotations != null && annotations.length != 0) {
                        UserParam userParam = (UserParam) annotations[0];
                        Log.e(TAG, "注解参数2：" + userParam.name() + "," + userParam.phone() + "\n");
                    }
                }
            }
            Method[] methods = c1.getDeclaredMethods();
            for(Method method2:methods){

                UserMethod userMethod1 = method2.getAnnotation(UserMethod.class);
                if (userMethod1 != null) {
                    Log.e(TAG,method2.getName()+ "的注解方法：" + userMethod1.title() + "\n");
                }

                Annotation[][] temp = method2.getParameterAnnotations();//拿到参数注解
                if (temp != null) {
                    for (int i = 0; i < temp.length; i++) {
                        Annotation[] annotations = temp[i];
                        if (annotations != null && annotations.length != 0) {
                            UserParam userParam = (UserParam) annotations[0];
                            Log.e(TAG, method2.getName()+"的注解参数2：" + userParam.name() + "," + userParam.phone() + "\n");
                        }
                    }
                }
            }
```
ii.Java动态代理机制获取
```java
 public static  <T> T create(final Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object... args)
                            throws Throwable {
                        // Annotation[]  methodAnnotations = method.getAnnotations();//拿到函数注解数组
                        UserMethod userMethod = method.getAnnotation(UserMethod.class);
                        Log.e(TAG, "UserMethod---title->" + userMethod.title());
                        Annotation[][] parameterAnnotationsArray =                    method.getParameterAnnotations();//拿到参数注解
                        for (int i = 0; i < parameterAnnotationsArray.length; i++) {
                            Annotation[] annotations = parameterAnnotationsArray[i];
                            if (annotations != null) {
                                UserParam userParam = (UserParam) annotations[0];
                                Log.e(TAG, "UserParam---userParam->" + userParam.name()+ ","+userParam.phone()+ "," + args[i]);
                            }
                        }
                        return null;
                    }
                });
    }
```

# 反射

反射获取类的三种方法
```java
            //第一种方式：
            Class c = Class.forName("com.awen.annotationdemo.User");

            //第二种方式：
            Class c2 = User.class;

            //第三种方式：
            //java语言中任何一个java对象都有getClass 方法
            User user = new User();
            Class c3 = user.getClass(); //c3是运行时类
```
详细：
```java
    /**
     * 反射调用
     */
    private void invokeReflect() {
        try {

            StringBuffer sb = new StringBuffer();

            /********************************反射获取类的三种方法start****************/
            //第一种方式：
            Class c = Class.forName("com.awen.annotationdemo.User");

            //第二种方式：
            Class c2 = User.class;

            //第三种方式：
            //java语言中任何一个java对象都有getClass 方法
            User user = new User();
            Class c3 = user.getClass(); //c3是运行时类 (e的运行时类是Employee)
            /********************************反射获取类的三种方法end********************************/


            //c.getModifiers:类的权限类型（public或private)   c.getSimpleName():类的名字（User)
            sb.append(Modifier.toString(c.getModifiers()) + " class " + c.getSimpleName() + "{\n");


            Object o = c.newInstance();//对象初始化




            /************************属性变量start****************/
            Field idF = c.getDeclaredField("id");
            //打破封装  
            idF.setAccessible(true); //使用反射机制可以打破封装性，导致了java对象的属性不安全。
            idF.set(o, 1000000);

            Field nameF = c.getDeclaredField("name");
            nameF.setAccessible(true);
            nameF.set(o, "刘德华");

            Field phoneF = c.getDeclaredField("phone");
            phoneF.setAccessible(true);
            phoneF.set(o, "18665859331");

            Field[] fs = c.getDeclaredFields();//获取所有属性变量

            for (Field field : fs) {
                sb.append("\t");//空格
                sb.append(Modifier.toString(field.getModifiers()) + " ");//获得属性的修饰符，例如public，static等等
                sb.append(field.getType().getSimpleName() + " ");//属性的类型的名字
                sb.append(field.getName() + ";\n");//属性的名字+回车
            }
            /************************属性变量end****************/


            /************************类的方法start****************/
            Method print = c.getDeclaredMethod("print", String.class);//获取单个方法
            print.invoke(o, "中国人民解放军");//方法调用


            Method[] md = c.getDeclaredMethods();//获取类的所有方法
            for (Method method : md) {
                sb.append("\t");//空格
                sb.append(Modifier.toString(method.getModifiers()) + " ");//获得属性的修饰符，例如public，static等等
                sb.append(method.getReturnType().getSimpleName() + " ");//属性的类型的名字
                sb.append(method.getName() + "();\n");//属性的名字+回车
            }

            sb.append("}");
            Log.e(TAG, sb.toString());
            /************************类的方法end****************/

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
```
# 运行结果
```java
E/AnnotionProxy: UserMethod---title->AwenZeng
E/AnnotionProxy: UserParam---userParam->刘峰,110,我之存在，因为有你。
E/MainActivity: 注解方法：牛B啊！！！
E/MainActivity: 注解参数2：刘凤,110
E/MainActivity: getName的注解方法：牛B啊！！！
E/MainActivity: getPhone的注解方法：中国梦
E/MainActivity: print的注解参数2：刘凤,110
E/MainActivity: setPhone的注解参数2：李小龙,999
E/User: User: 1000000,刘德华,18665859331,中国人民解放军
E/MainActivity: public class User{
                private String name;
                private String phone;
                private int id;
                public static transient volatile IncrementalChange $change;
                public static final long serialVersionUID;
                public int getId();
                public String getName();
                public String getPhone();
                public void print();
                public void setId();
                public void setName();
                public void setPhone();
                public static transient Object access$super();
        }
```

# Lisence

```java
Copyright 2017 AwenZeng

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


