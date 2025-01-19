package de.uoc.dh.idh.autodone.services;

import org.springframework.stereotype.Component;

import java.util.concurrent.PriorityBlockingQueue;

@Component
public class MediaDownloadQueue {
    private final PriorityBlockingQueue<MediaDownloadTask> queue = new PriorityBlockingQueue<>();

    public void addTask(MediaDownloadTask task) {
        queue.add(task);
        System.out.println("Added task: " + task.getDescription());
        System.out.println("Queue size after adding task: " + queue.size());
        System.out.println("MediaDownloadQueue instance: " + System.identityHashCode(this));

    }

    public MediaDownloadTask takeTask() throws InterruptedException {
        System.out.println("MediaDownloadQueue instance: " + System.identityHashCode(this));
        MediaDownloadTask task = queue.take();
        System.out.println("Queue size after taking task: " + queue.size());
        return task;
    }

    public void clear() {
        queue.clear();
    }

    public PriorityBlockingQueue<MediaDownloadTask> getQueue() {
        return queue;
    }
}