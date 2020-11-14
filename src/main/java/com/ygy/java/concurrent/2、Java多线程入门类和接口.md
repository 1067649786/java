# 2 Java多线程入门类和接口

## 2.1 Thread类和Runnable接口

上一章我们了解了操作系统中多线程的基本概念。那么在Java中，我们是如何使用多线程的呢？<br>

首先，我们需要有一个"线程"类。JDK提供了Thread类和Runnable接口来让我们实现自己的"线程"类。<br>

- 继承`Thread`类，并重写`run`方法；
- 实现`Runnable`接口的`run`方法；
<br>

### 2.1.1 继承Thread类

先学会怎么用，再学原理。首先我们来看看怎么用`Thread`和`Runnable`来写一个Java多线程程序。<br>

首先继承`Thread`类：
```java
public class Demo {
    public static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("MyThread");
        }
    }

    public static void main(String[] args) {
        Thread myThread = new MyThread();
        myThread.start();
    }
}
```

注意要调用`start()`方法后，线程才算启动！<br>

>我们在程序里面调用了start()方法后，虚拟机会先为我们创建一个线程，然后等到这个线程第一次
>得到时间片时再调用run()方法。<br>
>注意不可以多次调用start()方法。在第一次调用start()方法后，再次调用start()方法会抛出异常。

<br>

### 2.1.2 实现Runnable接口

接着我们来看一下`Runnable`接口(JDK 1.8+):

```java
@FunctionalInterface
public interface Runnable {
    public abstract void run();
}
```

可以看到`Runnable`是一个函数式接口，这意味着我们可以使用**Java 8的函数式编程**来简化代码。
<br>

示例代码:
```java
public class Demo {
    public static class MyThread implements Runnable {
        @Override
        public void run() {
            System.out.println("MyThread");
        }
    }

    public static void main(String[] args) {
        new MyThread().start();

        // Java 8 函数式编程，可以省略MyThread类
        new Thread(() -> {
            System.out.println("Java 8 匿名内部类");
        }).start();
    }
}
```

### 2.1.3 Thread类构造方法

`Thread`类是一个`Runnable`接口的实现类，我们来看看`Thread`类的源码。<br>

查看`Thread`类的构造方法，发现其实是简单调用一个私有的`init`方法来实现初始化。
`init`的方法签名:
```java
// Thread类源码 

// 片段1 - init方法
private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc,
                      boolean inheritThreadLocals)

// 片段2 - 构造函数调用init方法
public Thread(Runnable target) {
    init(null, target, "Thread-" + nextThreadNum(), 0);
}

// 片段3 - 使用在init方法里初始化AccessControlContext类型的私有属性
this.inheritedAccessControlContext = 
    acc != null ? acc : AccessController.getContext();

// 片段4 - 两个对用于支持ThreadLocal的私有属性
ThreadLocal.ThreadLocalMap threadLocals = null;
ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;
```

我们挨个来解释以下`init`方法的参数：

- g：线程组，指定这个线程是在哪个线程组下；
- target：指定要执行的任务；
- name：线程的名字，多个线程的名字是可以重复的。如果不指定名字，见片段2；
- acc：见片段3，用于初始化私有变量`inheritedAccessControlContext`。
> 这个变量有点神奇。它是一个私有变量，但是在Thread类里只有init方法对它进行初始化，
>在`exit`方法把它设为null。其它没有任何地方使用它，一般我们是不会使用它的。
- inheritThreadLocals：可继承的`Thread`,见片段4，`Thread`类里面有两个私有属性来支持`ThreadLocal`。
<br>

实际情况下，我们大多是直接调用下面两个构造方法：
```java
Thread(Runnable target)
Thread(Runnable target, String name)
```