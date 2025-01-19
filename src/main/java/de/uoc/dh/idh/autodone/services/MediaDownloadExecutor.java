package de.uoc.dh.idh.autodone.services;

import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMG_FORMAT;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMG_SIZE_X;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_IMG_SIZE_Y;
import static de.uoc.dh.idh.autodone.config.AutodoneConfig.AUTODONE_DOWNLOADRATELIMIT;
import static de.uoc.dh.idh.autodone.utils.ObjectUtils.mapFields;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static java.awt.Image.SCALE_FAST;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.util.Map.of;
import static javax.imageio.ImageIO.read;
import static javax.imageio.ImageIO.write;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.text.ParseException;
import de.uoc.dh.idh.autodone.entities.MediaEntity;
import de.uoc.dh.idh.autodone.entities.StatusEntity;




@Component
public class MediaDownloadExecutor {
    private final MediaDownloadQueue queue;
    private final ExecutorService executorService;
    @Autowired()
	private StatusService statusService;

    @Autowired
    public MediaDownloadExecutor(MediaDownloadQueue queue) {
        this.queue = queue;
        this.executorService = Executors.newFixedThreadPool(5); // Adjust thread count as needed
        System.out.println("Created executor service with 5 threads");
    }

    @PostConstruct
    public void start() {
        for (int i = 0; i < ((ThreadPoolExecutor) executorService).getCorePoolSize(); i++) {
            executorService.submit(this::processTasks);
        }
    }

    private void processTasks() {
        try {
            System.out.println("Thread started");
            while (true) {
                MediaDownloadTask task = queue.takeTask();
                System.out.println("Status UUID " + task.getStatus().getUuid().toString());
                StatusEntity status = task.getStatus();

                System.out.println(status.getUuid().toString());

                // Implement the download logic here
                try {
                    
                status.media.add(mapFields(of("status", status), importMedia(task.getMedia())));

					if (status.media.get(0).description != null) {
						// status.exceptions.add(new ParseException("Image scaled down (4th column)"));
						status.media.get(0).description = null;
					}

					if (task.getDescription() != null) {
						if (task.getDescription().length() > 1500) {
							// status.exceptions.add(new ParseException("Image caption too long (5th column)", number));
						} else {
							status.media.get(0).description = task.getDescription();
						}
					}
                
                statusService.save(status);

                System.out.println("Added media to download for status " + status.getUuid());
                
                } catch (Exception exception) {
                    // TODO: change this number thing here (errorOffset)
                    status.exceptions.add(new ParseException("Image not usable", 1));
                }
                
                TimeUnit.MILLISECONDS.sleep(AUTODONE_DOWNLOADRATELIMIT);

            }
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted");
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        executorService.shutdownNow();
    }

    public MediaEntity importMedia(String url) throws Exception {
		var media = new MediaEntity();
		var request = new URL(url).openConnection();

		media.contentType = request.getContentType();

		if (request.getContentLength() < 1024000) {
			media.file = request.getInputStream().readAllBytes();
		} else {
			var buffer = new ByteArrayOutputStream();
			var source = read(request.getInputStream());

			var scaleX = source.getWidth();
			var scaleY = source.getHeight();

			if (scaleX > AUTODONE_IMG_SIZE_X) {
				scaleX = AUTODONE_IMG_SIZE_X;
				scaleY = (scaleX * source.getHeight()) / source.getWidth();
			}

			if (scaleY > AUTODONE_IMG_SIZE_Y) {
				scaleY = AUTODONE_IMG_SIZE_Y;
				scaleX = (scaleY * source.getWidth()) / source.getHeight();
			}

			var target = new BufferedImage(scaleX, scaleY, TYPE_INT_ARGB);
			var scaled = source.getScaledInstance(scaleX, scaleY, SCALE_FAST);
			target.getGraphics().drawImage(scaled, 0, 0, null, null);
			write(target, AUTODONE_IMG_FORMAT, buffer);

			media.description = "Scaled down from original";
			media.file = buffer.toByteArray();
		}

		return media;
	}
}