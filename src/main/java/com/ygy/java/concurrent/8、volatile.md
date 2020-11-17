# 8 volatile

## 8.1 几个基本概念

在介绍volatile之前，我们先回顾及介绍几个基本概念。

### 8.1.1 内存可见性

在Java内存模型那一章我们介绍了JMM有一个主内存，每个线程有自己私有的工作内存，工作内存
中保存了一些变量在主内存的拷贝。<br>

**内存可见性，指的是线程之间的可见性，当一个线程修改了共享变量时，另一个线程可以读取到这个修改后的值**。<br>

### 8.1.2 重排序

为优化程序性能，对原有的指令执行顺序进行优化重新排序。重排序可能发生在多个阶段，比如编译重排序，CPU重排序等。

### 8.1.3 happens-before规则

是一个给程序员使用的规则，只要程序员在写代码的时候遵循happens-before规则，JVM就能
保证指令在多线程之间的顺序符合程序员的预期。

## 8.2 volatile的内存语义

在Java中，volatile关键字有特殊的内存语义。volatile主要有以下两个功能：

- 保证变量的**内存可见性**
- 禁止volatile变量与普通变量**重排序**

## 8.2.1 内存可见性

以一段示例代码开始：

```java
public class VolatileExample {
    int a = 0;
    volatile boolean flag = false;

    public void writer() {
        a = 1; // step 1
        flag = true; // step 2
    }

    public void reader() {
        if (flag) { // step 3
            System.out.println(a); // step 4
        }
    }
}
```

在这段代码里，我们使用volatile关键字修饰了一个boolean类型的变量flag。<br>

所谓内存可见性，指的是当一个线程对volatile修饰的变量进行写操作(比如step2)时，JMM会立即把该线程对应的
本地内存中的共享变量的值刷新到主内存；当一个线程对volatile修饰的变量进行读操作(比如step3)时，JMM会立即
把该线程对应的本地内存置为无效，从主内存中读取共享变量的值。<br>

> 在这一点上，volatile与锁具有相同的内存效果，volatile变量的写和锁的释放具有相同的内存语义，
>volatile变量的读和锁的获取具有相同的内存语义。

<br>
