package ru.otus.broker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.otus.message.Message;
import ru.otus.service.Receiver;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.String.format;

@Slf4j
@Component
public class MessageSystemImpl implements MessageSystem, Closeable {

    private static final String THREAD_NAME = "MS-worker-%s";

    private final Map<String, Receiver> receivers;
    private final Map<String, BlockingQueue<Message>> messageQueues;
    private final List<Thread> workers;

    public MessageSystemImpl() {
        workers = new ArrayList<>();
        receivers = new HashMap<>();
        messageQueues = new HashMap<>();
    }

    @Override
    public void start() {
        for (final Map.Entry<String, Receiver> entry : receivers.entrySet()) {
            final String destination = entry.getKey();
            final String threadName = format(THREAD_NAME, destination);
            final Thread thread = new Thread(() -> {
                while (true) {
                    final BlockingQueue<Message> queue = messageQueues.get(destination);
                    while (true) {
                        try {
                            final Message message = queue.take();
                            entry.getValue().receive(message);
                        } catch (InterruptedException e) {
                            log.warn("Worker {} terminated", threadName);
                            return;
                        } catch (Exception e) {
                            log.warn("Worker {} threw exception during message handling: {}", threadName, e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.setName(threadName);
            thread.start();
            workers.add(thread);
        }
        log.info("Message system started with {} workers", workers.size());
    }

    @Override
    public void send(Message message) {
        messageQueues.get(message.getDestination()).add(message);
    }

    @Override
    public void registerReceiver(String destination, Receiver receiver) {
        receivers.put(destination, receiver);
        messageQueues.put(destination, new LinkedBlockingQueue<>());
        log.info("Registered destination {} in MessageSystem", destination);
    }

    @Override
    public void close() {
        workers.forEach(Thread::interrupt);
        log.info("Message system terminated");
    }
}
