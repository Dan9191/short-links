package dan.hw.short_links.service;

import dan.hw.short_links.configuration.AppProperties;
import dan.hw.short_links.entity.Links;
import dan.hw.short_links.entity.LinkMaster;
import dan.hw.short_links.exception.ExistingLinkException;
import dan.hw.short_links.exception.IncorrectDateException;
import dan.hw.short_links.model.LinkRequest;
import dan.hw.short_links.model.LinkResponse;
import dan.hw.short_links.repository.LinkMasterRepository;
import dan.hw.short_links.repository.LinksRepository;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LinksService {

    private final LinkMasterRepository linkMasterRepository;

    private final LinksRepository linksRepository;

    private final AppProperties appProperties;

    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    @Transactional
    public String generateShortLink(LinkRequest model) throws NoSuchAlgorithmException, ExistingLinkException, IncorrectDateException {
        String linkMasterName = model.getUserName();
        String origLink = model.getOrigLink();
        String shortLink =  "clck.ru/" + stringToHex(origLink + linkMasterName).substring(0, 8);;
        LinkMaster user = linkMasterRepository.findByName(linkMasterName)
                .orElseGet(() -> {
                    LinkMaster newLinkMaster = new LinkMaster();
                    newLinkMaster.setName(linkMasterName);
                    return linkMasterRepository.save(newLinkMaster);
                });
        Optional<Links> existingLink = linksRepository.findActiveLinkByUserAndOrigLink(linkMasterName, origLink);

        if (existingLink.isPresent()) {
            throw new ExistingLinkException(linkMasterName, origLink);
        }

        LocalDateTime updatedToDate;
        if (model.getToDate() == null || model.getToDate().equals("")) {
            ChronoUnit chronoUnit = ChronoUnit.valueOf(appProperties.getUnit());
            updatedToDate = LocalDateTime.now().plus(appProperties.getAmountToAdd(), chronoUnit);
        } else {
            updatedToDate = parseAndValidateDate(model.getToDate());
        }

        Links link = new Links();
        link.setLinkMaster(user);
        link.setOrigLink(origLink);
        link.setShortLink(shortLink);
        link.setRemainder(model.getRemainder());
        link.setToDate(updatedToDate);
        linksRepository.save(link);

        return shortLink;
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

    public String getOrigLink(LinkResponse linkResponse) {
        Optional<Links> existingLink =
                linksRepository.findActiveLinkByUserAndShortLink(linkResponse.getUserName(), linkResponse.getShortLink());
        return "sdfsdf";
    }

    public static LocalDateTime parseAndValidateDate(String dateStr) throws IncorrectDateException {
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
