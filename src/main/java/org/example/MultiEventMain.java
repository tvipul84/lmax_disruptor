package org.example;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import org.example.lmax.*;

import java.util.concurrent.ThreadFactory;

public class MultiEventMain {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Multi Event Main");
        Disruptor<ValueEvent> disruptor;
        WaitStrategy waitStrategy = new BusySpinWaitStrategy();;
        final ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
        ProducerType producerType = ProducerType.MULTI;
        disruptor = new Disruptor<>(ValueEvent.EVENT_FACTORY, 16, threadFactory, producerType, waitStrategy);
        final EventConsumer eventConsumer = new MultiEventPrintConsumer();
        disruptor.handleEventsWith(eventConsumer.getEventHandler());
        final EventProducer eventProducer = new DelayedMultiEventProducer();
        final RingBuffer<ValueEvent> ringBuffer = disruptor.start();
        eventProducer.startProducing(ringBuffer, 32);
        Thread.sleep(30000);
        disruptor.halt();
        disruptor.shutdown();
    }
}
