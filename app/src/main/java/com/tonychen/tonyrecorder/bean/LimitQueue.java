package com.tonychen.tonyrecorder.bean;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by tony on 2018/5/9;
 * Email : chenchenyanrong@163.com
 * Blog : http://blog.csdn.net/weixin_37484990
 * Description :
 */
public class LimitQueue<E> {
    private int limit; // 队列长度

    private LinkedList<E> queue = new LinkedList<E>();

    public Queue<E> getQueue() {
        return queue;
    }

    public LimitQueue(int limit) {
        this.limit = limit;
    }

    /**
     * 入列：当队列大小已满时，把队头的元素poll掉
     */
    public void offer(E e) {
        if (queue.size() >= limit-1) {
            queue.poll();
        }
        queue.offer(e);
    }

    public E get(int position) {
        return queue.get(position);
    }

    public E getLast() {
        return queue.getLast();
    }

    public E getFirst() {
        return queue.getFirst();
    }

    public int getLimit() {
        return limit;
    }

    public int size() {
        return queue.size();
    }
}
