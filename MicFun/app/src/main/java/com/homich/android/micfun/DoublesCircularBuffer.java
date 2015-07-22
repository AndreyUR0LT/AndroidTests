package com.homich.android.micfun;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;

/**
 * Created by root on 22.07.15.
 */
public class DoublesCircularBuffer {
    private int maxSize;
    private int front = 0;
    private int rear = 0;
    private int bufLen = 0;
    private double[] buf;

    public DoublesCircularBuffer(int size) {
        maxSize = size;
        front = rear = 0;
        rear = 0;
        bufLen = 0;
        buf = new double[maxSize];
    }

    public int getSize() {
        return bufLen;
    }

    public void clear() {
        front = rear = 0;
        rear = 0;
        bufLen = 0;
        buf = new double[maxSize];
    }

    public boolean isEmpty() {
        return bufLen == 0;
    }

    public boolean isFull() {
        return bufLen == maxSize;
    }

    public void add(double c) {
        if (!isFull()) {
            bufLen++;
            rear = (rear + 1) % maxSize;
            buf[rear] = c;
        } else
            throw new BufferOverflowException();
    }

    public double get() {
        if (!isEmpty()) {
            bufLen--;
            front = (front + 1) % maxSize;
            return buf[front];
        } else {
            throw new BufferUnderflowException();
        }
    }
}
