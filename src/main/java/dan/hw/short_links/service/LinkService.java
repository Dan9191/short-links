package dan.hw.short_links.service;

import dan.hw.short_links.configuration.AppProperties;
import dan.hw.short_links.entity.Link;
import dan.hw.short_links.entity.LinkMaster;
import dan.hw.short_links.exception.IncorrectDateException;
import dan.hw.short_links.exception.NotExistingLinkException;
import dan.hw.short_links.model.LinkEdit;
import dan.hw.short_links.model.LinkRequest;
import dan.hw.short_links.model.LinkResponse;
import dan.hw.short_links.repository.LinkMasterRepository;
import dan.hw.short_links.repository.LinkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkMasterRepository linkMasterRepository;

    private final LinkRepository linkRepository;

    private final AppProperties appProperties;

    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    @Transactional
    public LinkResponse generateShortLink(LinkRequest model) throws NoSuchAlgorithmException, IncorrectDateException {
        String linkMasterName = model.getUserName();
        String origLink = model.getOrigLink();
        String shortLink =
                "clck.ru/" + stringToHex(origLink + linkMasterName + System.currentTimeMillis()).substring(0, 8);
        LinkMaster user = linkMasterRepository.findByName(linkMasterName)
                .orElseGet(() -> {
                    LinkMaster newLinkMaster = new LinkMaster();
                    newLinkMaster.setName(linkMasterName);
                    return linkMasterRepository.save(newLinkMaster);
                });

        // Сравнивается дата, указанная пользователем и дата, сгенерированная согласно дефолтным настройкам.
        // Из полученных дат выбирается наименьшая
        LocalDateTime updatedToDate;
        ChronoUnit chronoUnit = ChronoUnit.valueOf(appProperties.getUnit());
        LocalDateTime automaticallyUpdatedToDate = LocalDateTime.now().plus(appProperties.getAmountToAdd(), chronoUnit);
        if (model.getToDate() == null || model.getToDate().equals("")) {
            updatedToDate = automaticallyUpdatedToDate;
        } else {
            updatedToDate = parseAndValidateDate(model.getToDate());
            if (updatedToDate.isAfter(automaticallyUpdatedToDate)) {
                updatedToDate = automaticallyUpdatedToDate;
            }
        }

        Link link = new Link();
        link.setLinkMaster(user);
        link.setOrigLink(origLink);
        link.setShortLink(shortLink);
        link.setRemainder(model.getRemainder());
        link.setToDate(updatedToDate);
        link.setActive(true);
        linkRepository.save(link);

        return new LinkResponse(user.getId(), shortLink);
    }

    private String stringToHex(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(string.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Transactional
    public String getOrigLink(LinkResponse linkResponse) {
        List<Link> existingLink =
                linkRepository.findActiveLinkByUserAndShortLink(linkResponse.getLinkMasterId(), linkResponse.getShortLink());
        if (existingLink.isEmpty()) {
            return "Не найдено ни одной ссылки";
        }
        List<Link> activeLinks = existingLink.stream()
                .filter(link -> link.getRemainder() > 0)
                .filter(link -> link.getToDate().isAfter(LocalDateTime.now()))
                .toList();
        if (activeLinks.isEmpty()) {
            return "Найденные ссылки просрочены";
        }

        Link link = activeLinks.get(0);
        long remainder = link.getRemainder();
        link.setRemainder(remainder - 1);
        linkRepository.save(link);
        return link.getOrigLink();
    }

    /**
     * Метод для изменения существующей ссылки.
     *
     * @param linkEdit
     * @return
     * @throws NotExistingLinkException
     * @throws IncorrectDateException
     */
    @Transactional
    public String editLink(LinkEdit linkEdit) throws NotExistingLinkException, IncorrectDateException {
        List<Link> existingLink =
                linkRepository.findAllByUserAndShortLink(linkEdit.getLinkMasterId(), linkEdit.getShortLink());
        if (existingLink.isEmpty()) {
            throw new NotExistingLinkException(linkEdit.getLinkMasterId(), linkEdit.getShortLink());
        }
        Link link = existingLink.get(0);
        if (linkEdit.getToDate() != null || !linkEdit.getToDate().equals("")) {
            LocalDateTime updatedToDate = parseAndValidateDate(linkEdit.getToDate());
            link.setToDate(updatedToDate);
        }

        link.setRemainder(linkEdit.getRemainder());
        link.setActive(linkEdit.getStatusStub().isStatus());
        linkRepository.save(link);
        return link.getOrigLink();
    }

    @Transactional
    public LinkEdit findLink(LinkResponse linkResponse) throws NotExistingLinkException {
        List<Link> existingLink =
                linkRepository.findAllByUserAndShortLink(linkResponse.getLinkMasterId(), linkResponse.getShortLink());
        if (existingLink.isEmpty()) {
            throw new NotExistingLinkException(linkResponse.getLinkMasterId(), linkResponse.getShortLink());
        }
        Link link = existingLink.get(0);
        return new LinkEdit(link);
    }

    private LocalDateTime parseAndValidateDate(String dateStr) throws IncorrectDateException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDateTime parsedDate;
        try {
            parsedDate = LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new IncorrectDateException("Неверный формат даты: " + dateStr);
        }
        if (parsedDate.isBefore(LocalDateTime.now())) {
            throw new IncorrectDateException("Дата должна быть больше текущей.");
        }

        return parsedDate;
    }

}
