package com.taurus.memory;

import org.junit.Test;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;


/**
 * Created by ynfeng on 16/8/19.
 */
public class PhantomReferenceTest {

    @Test
    public void phantomReferenceTest() {
        String s = new String("abcdef");
        ReferenceQueue<Object> queue = new ReferenceQueue<Object>();
        PhantomReference<Object> phantomReferenceTest = new PhantomReference<Object>(s, queue);
        s = null;
        System.gc();
        System.out.println(queue.poll());
    }

}
