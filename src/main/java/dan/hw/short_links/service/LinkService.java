package dan.hw.short_links.service;

import dan.hw.short_links.configuration.AppProperties;
import dan.hw.short_links.entity.Link;
import dan.hw.short_links.entity.LinkMaster;
import dan.hw.short_links.exception.IncorrectDateException;
import dan.hw.short_links.exception.NotActiveLinkException;
import dan.hw.short_links.exception.NotExistingLinkException;
import dan.hw.short_links.model.LinkEdit;
import dan.hw.short_links.model.LinkGenerate;
import dan.hw.short_links.model.LinkGet;
import dan.hw.short_links.model.LinkGetResponse;
import dan.hw.short_links.repository.LinkMasterRepository;
import dan.hw.short_links.repository.LinkRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkService {

    private final LinkMasterRepository linkMasterRepository;

    private final LinkRepository linkRepository;

    private final AppProperties appProperties;

    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * Генерация короткой ссылки.
     *
     * @param model Данные для генерации ссылки.
     * @return Короткая ссылка + uuid пользователя.
     * @throws NoSuchAlgorithmException ошибка при вычислении хэша.
     * @throws IncorrectDateException некорректно переданная дата.
     */
    @Transactional
    public LinkGet generateShortLink(LinkGenerate model) throws NoSuchAlgorithmException, IncorrectDateException {
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
        // согласно ТЗ, из введенных значений количества переходов выбирается наибольшее
        if (model.getRemainder() >= appProperties.getRemainder()) {
            link.setRemainder(model.getRemainder());
        } else {
            link.setRemainder(appProperties.getRemainder());
        }
        link.setToDate(updatedToDate);
        link.setActive(true);
        linkRepository.save(link);

        return new LinkGet(user.getId(), shortLink);
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

    /**
     * Получение оригинальной ссылки.
     *
     * @param linkGet Uuid пользователя + короткая ссылка.
     * @return оригинальная ссылка
     * @throws NotExistingLinkException такой ссылки нет в БД.
     * @throws NotActiveLinkException найденная ссылка не активна.
     */
    @Transactional
    public LinkGetResponse getOrigLink(LinkGet linkGet) throws NotExistingLinkException, NotActiveLinkException {
        List<Link> existingLink =
                linkRepository.findAllByUserAndShortLink(linkGet.getLinkMasterId(), linkGet.getShortLink());
        if (existingLink.isEmpty()) {
            throw new NotExistingLinkException(linkGet.getLinkMasterId(), linkGet.getShortLink());
        }
        List<Link> activeLinks = existingLink.stream()
                .filter(link -> link.getRemainder() > 0)
                .filter(link -> link.getToDate().isAfter(LocalDateTime.now()))
                .filter(Link::isActive)
                .toList();
        if (activeLinks.isEmpty()) {
            throw new NotActiveLinkException(linkGet.getLinkMasterId(), linkGet.getShortLink());
        }

        Link link = activeLinks.get(0);
        long remainder = link.getRemainder() - 1;
        link.setRemainder(remainder);
        LinkGetResponse result;
        if (remainder == 0) {
            link.setActive(false);
            result = new LinkGetResponse("Текущее количество действий ссылки  0", link.getOrigLink());
        } else {
            result = new LinkGetResponse(null, link.getOrigLink());
        }
        linkRepository.save(link);
        return result;
    }

    /**
     * Метод для изменения существующей ссылки.
     *
     * @param linkEdit Данные для изменения ссылки.
     * @throws NotExistingLinkException такой ссылки нет.
     * @throws IncorrectDateException передана некорректная дата.
     */
    @Transactional
    public void editLink(LinkEdit linkEdit) throws NotExistingLinkException, IncorrectDateException {
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
    }

    /**
     * Поиск ссылки, для внесения изменений
     *
     * @param linkGet Uuid пользователя + короткая ссылка.
     * @return Данные о ссылке
     * @throws NotExistingLinkException указанная ссылка не найдена
     */
    @Transactional
    public LinkEdit findLink(LinkGet linkGet) throws NotExistingLinkException {
        List<Link> existingLink =
                linkRepository.findAllByUserAndShortLink(linkGet.getLinkMasterId(), linkGet.getShortLink());
        if (existingLink.isEmpty()) {
            throw new NotExistingLinkException(linkGet.getLinkMasterId(), linkGet.getShortLink());
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
