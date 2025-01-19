package de.uoc.dh.idh.autodone.services;

import org.springframework.stereotype.Component;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class MediaDownloadQueue {
    private final PriorityBlockingQueue<MediaDownloadTask> queue = new PriorityBlockingQueue<>();

    public void addTask(MediaDownloadTask task) {
        queue.add(task);
        System.out.println("Added task: " + task.getDescription());
    }

    public MediaDownloadTask takeTask() throws InterruptedException {
        return queue.take();
    }

    public void clear() {
        queue.clear();
    }
}