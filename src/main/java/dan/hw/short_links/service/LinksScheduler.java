package dan.hw.short_links.service;

import dan.hw.short_links.entity.Links;
import dan.hw.short_links.repository.LinksRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class LinksScheduler {

    private final LinksRepository linksRepository;

    @PostConstruct
    public void startScheduler() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::deactivateLinks, 0, 1, TimeUnit.MINUTES);
    }

    @Transactional
    public void deactivateLinks() {
        try {
            List<Links> links = linksRepository.findForDeactivate();
            links.stream().peek(link -> link.setActive(false)).forEach(this::sendNotifications);
            linksRepository.saveAll(links);
        } catch (Exception e) {
            log.error("Ошибка при выполнении deactivateLinks: ", e);
        }
    }

    private void sendNotifications(Links link) {
                //метод для отправки предупреждения
        log.info("link deactivated {}", link);
    }
}
