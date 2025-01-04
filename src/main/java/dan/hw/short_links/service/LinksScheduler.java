package dan.hw.short_links.service;

import dan.hw.short_links.repository.LinksRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class LinksScheduler {

    private final LinksRepository linksRepository;

    @PostConstruct
    public void startScheduler() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::deactivateLinks, 0, 1, TimeUnit.MINUTES);
    }

    public void deactivateLinks() {
        int updatedRows = linksRepository.deactivateExpiredLinks();
    }
}
