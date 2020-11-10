package com.ygy.java.collection;

import java.io.Serializable;
import java.util.*;

/**
 * @Description
 * @Author ygy
 * @Date 2020/11/10
 */
public class MyArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, Serializable {
    private static final long serialVersionUID = 8683452581122892189L;

    private static final int DEFAULT_CAPACITY = 10;

    private static final Object[] EMPTY_ELEMENTDATA = {};

    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    transient Object[] elementData;

    private int size;

    public MyArrayList(){
        this.elementData=DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    public MyArrayList(int capacity){
        if (capacity>0){
            this.elementData=new Object[capacity];
        } else if (capacity == 0){
            this.elementData=EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("非法参数:"+capacity);
        }
    }

    public MyArrayList(Collection<? extends E> collection){
        elementData=collection.toArray();
        if ((size=elementData.length)!=0){
            if (elementData.getClass()!=Object[].class){
                elementData=Arrays.copyOf(elementData,size,Object[].class);
            }
        } else {
            elementData=EMPTY_ELEMENTDATA;
        }
    }

    @Override
    public boolean add(E e){
        modCount++;
        add(e,elementData,size);
        return true;
    }

    public void add(int index,E e){
        checkRangeForAdd(index);
        modCount++;

    }

    private void checkRangeForAdd(int index){
        if (index<0 || index>size){
            throw new IndexOutOfBoundsException("index:"+index+",size:"+size);
        }
    }

    private void add(E e,Object[] elementData,int s){
        if (elementData.length==s){
            //扩容
        }
        elementData[s]=e;
        size=s+1;
    }

    @Override
    public E get(int index) {
        return (E) elementData[index];
    }

    @Override
    public int size() {
        return size;
    }
}
