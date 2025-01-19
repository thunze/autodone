package de.uoc.dh.idh.autodone.services;

import de.uoc.dh.idh.autodone.entities.StatusEntity;

public class MediaDownloadTask implements Comparable<MediaDownloadTask> {
    private final String media;
    private final StatusEntity status;
    private final String description;

    public MediaDownloadTask(String media, StatusEntity status, String description) {
        this.media = media;
        this.status = status;
        this.description = description;
    }

    public MediaDownloadTask(String media, StatusEntity status) {
        this(media, status, null);
    }

    public String getMedia() {
        return media;
    }

    public StatusEntity getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(MediaDownloadTask other) {
        return this.status.getDate().compareTo(other.getStatus().getDate());
    }
}