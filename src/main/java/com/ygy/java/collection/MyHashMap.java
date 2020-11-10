package com.ygy.java.collection;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @Description
 * @Author ygy
 * @Date 2020/11/10
 */
public class MyHashMap<K,V> extends AbstractMap<K,V>
        implements Map<K,V>, Cloneable, Serializable {

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    static class Node<K,V> implements Entry<K,V> {
        final int hash; //用于计算索引
        final K key;
        V value;
        Node<K,V> next; //后继节点，下一个Node

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public final K getKey() {
            return key;
        }

        @Override
        public final V getValue() {
            return value;
        }

        @Override
        public final V setValue(V value) {
            return null;
        }
    }

}
