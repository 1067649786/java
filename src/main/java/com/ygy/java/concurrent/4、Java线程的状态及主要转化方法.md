# 4 Java线程的状态及主要转化方法

## 4.1 操作系统中的线程状态转换

首先我们来看看操作系统中的线程状态转换。<br>

>在现在的操作系统中，线程是被视为轻量级进程的，所以`操作系统线程的状态其实和操作系统进程的状态是一致的`。

<br>

操作系统线程主要有以下三个状态：

- 就绪状态(ready)：线程正在等待使用CPU，经调度程序调用之后可进入running状态。
- 执行状态(running)：线程正在使用CPU。
- 等待状态(waiting)：线程经过等待事件的调用或者正在等待其他资源(如I/O)。

<br>

## 4.2 Java线程的6个状态

```java
// Thread.State 源码
public enum State {
    NEW,
    RUNNABLE,
    BLOCKED,
    WAITING,
    TIMED_WAITING,
    TERMINATED;
}
```

### 4.2.1 NEW

处于NEW状态的线程此时尚未启动。这里的尚未启动指的是还没调用Thread实例的start()方法。

```java
private void testStateNew() {
    Thread thread = new Thread(() -> {});
    System.out.println(thread.getState()); // 输出 NEW 
}
```

从上面可以看出，只是创建了线程而并没有调用start()方法，此时线程处于NEW状态。<br>

**对于start()的两个引申问题**<br>

1.反复调用同一个线程的start()方法是否可行？
2.假如一个线程执行完毕(此时处于TERMINATED状态)，再次调用这个线程的start()方法是否可行？<br>

要分析这两个问题，我们先来看看start()的源码：

```java
public synchronized void start() {
    if (threadStatus != 0)
        throw new IllegalThreadStateException();

    group.add(this);

    boolean started = false;
    try {
        start0();
        started = true;
    } finally {
        try {
            if (!started) {
                group.threadStartFailed(this);
            }
        } catch (Throwable ignore) {

        }
    }
}
```

我们可以看到，在start()内部，这里有一个threadStatus的变量。如果它不等于0，调用start()是会直接抛出异常的。<br>

我们接着往下看，有一个native的`start0()`方法。这个方法里并没有对`threadStatus`的处理。到了这里我们仿佛就拿这个
threadStatus没辙了，我们通过debug的方式再看一下：

```java
@Test
public void testStartMethod() {
    Thread thread = new Thread(() -> {});
    thread.start(); // 第一次调用
    thread.start(); // 第二次调用
}
```

我们是在start()方法内部的最开始打的断点，叙述下在我这里打断点看到的结果：

- 第一次调用时，threadStatus的值是0。
- 第二次调用时，threadStatus的值不为0。
<br>

查看当前线程状态的源码：

```java
// Thread.getState方法源码：
public State getState() {
    // get current thread state
    return sun.misc.VM.toThreadState(threadStatus);
}

// sun.misc.VM 源码：
public static State toThreadState(int var0) {
    if ((var0 & 4) != 0) {
        return State.RUNNABLE;
    } else if ((var0 & 1024) != 0) {
        return State.BLOCKED;
    } else if ((var0 & 16) != 0) {
        return State.WAITING;
    } else if ((var0 & 32) != 0) {
        return State.TIMED_WAITING;
    } else if ((var0 & 2) != 0) {
        return State.TERMINATED;
    } else {
        return (var0 & 1) == 0 ? State.NEW : State.RUNNABLE;
    }
}
```

所以，我们结合上面的源码可以得到引申的两个问题的结果：<br>

>两个问题的答案都是不可行，在调用一次start()之后，threadStatus的值会改变(threadStatus!=0),
>此时再次调用start()方法会抛出IllegalThreadStateException异常。<br>
>比如threadStatus为2代表当前线程状态为TERMINATED。

<br>

### 4.2.2 RUNNABLE

表示当前线程正在运行中。处于RUNNABLE状态的线程在Java虚拟机中运行，也有可能在等待其他系统资源(比如I/O)。<br>

**Java中线程的RUNNABLE状态**<br>

看了操作系统线程的几个状态之后我们来看看Thread源码里对RUNNABLE状态的定义：

```java
/**
 * Thread state for a runnable thread.  A thread in the runnable
 * state is executing in the Java virtual machine but it may
 * be waiting for other resources from the operating system
 * such as processor.
 */
```

>Java线程的RUNNABLE状态其实是包括了传统操作系统的ready和running两个状态的。<br>

### 4.2.3 BLOCKED

阻塞状态。处于BLOCKED状态的线程正等待锁的释放以进入同步区。<br>

我们用BLOCKED状态举个生活中的例子：

>假如今天你下班后准备去食堂吃饭。你来到食堂仅有的一个窗口，发现前面已经有个人在窗口前了，此时
>你必须得等前面的人从窗口离开才行。
>假设你是线程t2，你前面的那个人是线程t1。此时t1占有了锁(食堂唯一的窗口)，t2正在等待锁的释放，
>所以此时t2就处于BLOCKED状态。

<br>

### 4.2.4 WAITING

等待状态。处于等待状态的线程变成RUNNABLE状态需要其他线程唤醒。<br>

调用如下3个方法会使线程进入等待状态：

- Object.wait()：使当前线程处于等待状态直到另一个线程唤醒它；
- Thread.join()：等待线程执行完毕，底层调用的是Object实例的wait方法；
- LockSupport.park()：除非获得调用许可，否则禁用当前线程进行线程调度。

<br>

我们延续上面的例子继续解释一下WAITING状态：

>你等了好几分钟现在终于轮到你了，突然你有一个不懂事的经理突然来了。你看到他你就有一种不祥的预感，
>果然，他就是来找你的。<br>
>
>他把你拉到一旁叫你待会再吃饭，说他下午要去作报告，赶紧来找你了解一下项目的情况。你心里虽然有一万个不愿意，
>但是你还是从食堂窗口走开了。<br>
>
>此时，假设你还是线程t2，你的经理是线程t1。虽然你此时都占有锁(窗口)了，不速之客来了你还是得释放掉锁。
>此时你t2的状态就是WAITING。然后经理t1获得锁，进入RUNNABLE状态。<br>
>
>要是经理t1不主动唤醒你t2(notify、notifyAll...)，可以说你t2只能一直等待了。

<br>

### 4.2.5 TIMED_WAITING

超时等待状态。线程等待一个具体的时间，时间到后会被自动唤醒。<br>

调用如下方法会使线程进入超时等待状态：

- Thread.sleep(long mills)：使当前线程睡眠指定时间；
- Object.wait(long timeout)：线程休眠指定时间，等待期间可以通过notify()/notifyAll()唤醒；
- Thread.join(long mills)：等待当前线程最多执行mills毫秒，如果mills为0，则会一直执行；
- LockSupport.parkNanos(long nanos)：除非获得调用许可，否则禁用当前线程进行线程调度指定时间；
- LockSupport.parkUntil(long deadline)：同上，也是禁止线程进行调度指定时间；

<br>

我们继续延续上面的例子来解释一下TIMED_WAITING状态：

>到了第二天中午，又到了饭点，你还是到了窗口前。<br>
>
>突然间想起你的同事叫你等他一起，他说让你等他十分钟他改个bug。<br>
>
>好吧，你说那你就等等吧，你就离开了窗口。很快十分钟过去了，你见他还没来，你想都等了这么久了还不来，
>那你还是先去吃饭好了。<br>
>
>这时你还是线程t1，你改bug的同事是线程t2。t2让t1等待了指定时间，t1先主动释放了锁。
>此时t1等待期间就属于TIMED_WAITING状态。<br>
>
>t1等待十分钟后，就自动唤醒，拥有了去争夺锁的资格。

<br>

### 4.2.6 TERMINATED

终止状态。此时线程已执行完毕。

## 4.3 线程状态的转换

### 4.3.1 BLOCKED与RUNNABLE状态的转换

我们上面说到：处于BLOCKED状态的线程是因为在等待锁的释放。假如这里有两个线程a和b，a线程提前获得了锁并且
暂未释放锁，此时b就处于BLOCKED状态。我们先来看一个例子：

```java
@Test
public void blockedTest() {

    Thread a = new Thread(new Runnable() {
        @Override
        public void run() {
            testMethod();
        }
    }, "a");
    Thread b = new Thread(new Runnable() {
        @Override
        public void run() {
            testMethod();
        }
    }, "b");

    a.start();
    b.start();
    System.out.println(a.getName() + ":" + a.getState()); // 输出？
    System.out.println(b.getName() + ":" + b.getState()); // 输出？
}

// 同步方法争夺锁
private synchronized void testMethod() {
    try {
        Thread.sleep(2000L);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

初看之下，大家可能会觉得线程a会先调用同步方法，同步方法内又调用了Thread.sleep()方法，必然会输出TIMED_WAITING，
而线程b因为等待线程a释放锁所以必然会输出BLOCKED。<br>

其实不然，有两点需要值得大家注意，一是`在测试方法blockedTest()内还有一个main线程`，二是`启动线程后
执行run()方法还是需要消耗一定时间的`。不打断点的情况下，上面代码都应该输出`RUNNABLE`。<br>

>测试方法的main线程只保证了a，b两个线程调用start()方法(转化为RUNNABLE状态)，还没等两个线程真正开始争抢锁，
>就已经打印此时两个线程的状态(RUNNABLE)了。

<br>

这时你可能又会问了，要是我想要打印出BLOCKED状态我该怎么处理呢？其实处理下测试方法里的main线程就可以了，你让它休息一会，
打断点或者调用Thread.sleep()方法就行。<br>

这里需要注意的是main线程休息的时间，要保证在线程争夺锁的时间内，不要等到前一个线程锁都释放了你再去争夺锁，
此时还是得不到BLOCKED状态的。<br>

我们把上面的测试方法blockedTest()改动一下：

```java
public void blockedTest() throws InterruptedException {
    ······
    a.start();
    Thread.sleep(1000L); // 需要注意这里main线程休眠了1000毫秒，而testMethod()里休眠了2000毫秒
    b.start();
    System.out.println(a.getName() + ":" + a.getState()); // 输出？
    System.out.println(b.getName() + ":" + b.getState()); // 输出？
}
```

在这个例子中，由于main线程休眠，所以线程a的run()方法跟着执行，线程b再接着执行。<br>

在线程a执行run()调用testMethod()之后，线程a休眠了2000ms(注意这里是没有释放锁的)，main线程休眠完毕后，
接着b线程执行的时候是争夺不到锁的，所以这里输出:

```
a:TIMED_WAITING
b:BLOCKED
```

### 4.3.2 WAITING状态与RUNNABLE状态的转换

根据转换图我们知道有3个方法可以使RUNNABLE()状态转了WAITING。我们主要介绍下Object.wait()
和Thread.join()。<br>

**Object.wait()**<br>

>调用wait()方法前线程必须持有对象的锁。<br>
>
>线程调用wait()方法时，会释放当前的锁，直到有其他线程调用notify()/notifyALL()方法唤醒等待锁的线程。<br>
>
>需要注意的是，其他线程调用notify()方法只会唤醒单个等待锁的线程，如果有多个线程都在等待这个锁的话，
>不一定会唤醒到之前调用wait()方法的线程。<br>
>
>同样，调用notifyAll()方法唤醒所有等待锁的线程之后，也不一定会马上把时间片分给刚才放弃锁的那个线程，
>具体要看系统的调度。

<br>

**Thread.join()**<br>

>调用join()方法不会释放锁，会一直等待当前线程执行完毕(转换为TERMINATED状态)。

<br>

我们再把上面的例子线程启动那里改变一下：

```java
public void blockedTest() {
    ······
    a.start();
    a.join();
    b.start();
    System.out.println(a.getName() + ":" + a.getState()); // 输出 TERMINATED
    System.out.println(b.getName() + ":" + b.getState());
}
```

要是没有调用join方法，main线程不管a线程是否执行完毕都会继续往下走。<br>

a线程启动之后马上调用了join方法，这里main线程就会等到a线程执行完毕，所以这里a线程打印的状态固定是TERMINATED。<br>

至于b线程的状态，有可能打印RUNNABLE(尚未进入同步方法)，也有可能打印TIMED_WAITING(进入了同步方法)。
<br>

### 4.3.3 TIMED_WAITING与RUNNABLE状态转换

TIMED_WAITING与WAITING状态类似，只是TIMED_WAITING状态等待的时间是指定的。<br>

**Thread.sleep(long)**<br>

>使当前线程睡眠指定时间。需要注意这里的睡眠只是暂时使线程停止执行，并不会释放锁。时间到后，线程会重新进入
>RUNNABLE状态。

<br>

**Object.wait(long)**<br>

>wait(long)方法使线程进入TIMED_WAITING状态。这里的wait(long)方法与无参方法wait()相同的地方是，
>都可以通过其他线程调用notify()或notifyAll()方法来唤醒。<br>
>
>不同的地方是，有参方法wait(long)就算其他线程不来唤醒它，经过指定时间long之后它会自动唤醒，拥有去争夺锁的资格。

<br>

**Thread.join(long)**<br>

>join(long)使当前线程执行指定时间，并且使线程进入TIMED_WAITING状态。<br>
>
>我们再来改一改刚才的代码:
>
```java
public void blockedTest() {
     ······
     a.start();
     a.join(1000L);
     b.start();
     System.out.println(a.getName() + ":" + a.getState()); // 输出 TIEMD_WAITING
     System.out.println(b.getName() + ":" + b.getState());
 }
```
>这里调用a.join(1000L)，因为是指定了具体a线程执行的时间的，并且执行时间是小于a线程sleep的时间，
>所以a线程状态输出TIMED_WAITING。

<br>

b线程状态仍然不固定(RUNNABLE或BLOCKED)。

### 4.3.4 线程中断

>在某些情况下，我们在线程启动后发现并不需要它继续执行下去，需要中断线程。目前在Java中还没有安全直接
>的方法来停止线程，但是Java提供了线程中断机制来处理需要中断线程的情况。<br>
>
>线程中断机制是一种协作机制。需要注意，通过中断操作并不能直接终止一个线程，而是通知需要被中断的线程自行处理。

<br>

简单介绍下Thread类里提供的关于线程中断的几个方法：

- Thread.interrupt()：中断线程。这里的中断线程并不会立即停止线程，而是设置线程的中断状态为true(默认是false)；
- Thread.interrupted()：测试当前线程是否被中断。线程的中断状态受这个方法的影响，意思是调用一次使线程中断状态设置为true，
连续调用两次会使得这个线程的中断重新转为false；
- Thread.isInterrupt()：测试当前线程是否被中断。与上面方法不同的是调用这个方法并不会影响线程的中断状态。

>在线程中断机制里，当其他线程通知需要被中断的线程后，线程中断的状态被设置为true，但是具体被要求中断的线程要怎么处理，
>完全由被中断线程自己而定，可以在合适的实际处理中断请求，也可以完全不处理继续执行下去。