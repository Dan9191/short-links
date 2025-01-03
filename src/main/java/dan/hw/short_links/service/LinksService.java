package dan.hw.short_links.service;

import dan.hw.short_links.entity.Links;
import dan.hw.short_links.entity.User;
import dan.hw.short_links.exception.ExistingLinkException;
import dan.hw.short_links.model.LinkRequest;
import dan.hw.short_links.model.LinkResponse;
import dan.hw.short_links.repository.LinksRepository;
import dan.hw.short_links.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LinksService {

    private final UserRepository userRepository;

    private final LinksRepository linksRepository;

    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    public String generateShortLink(LinkRequest model) throws NoSuchAlgorithmException, ExistingLinkException {
        String userName = model.getUserName();
        String origLink = model.getOrigLink();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(origLink.getBytes(StandardCharsets.UTF_8));
        String shortLink =  bytesToHex(hashBytes).substring(0, 8);
        User user = userRepository.findByName(userName)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setName(userName);
                    return userRepository.save(newUser);
                });
        Optional<Links> existingLink = linksRepository.findActiveLinkByUserAndOrigLink(userName, origLink);

        if (existingLink.isPresent()) {
            throw new ExistingLinkException(userName, origLink);
        }

        Links link = new Links();
        link.setUser(user);
        link.setOrigLink(origLink);
        link.setShortLink(shortLink);
        link.setRemainder(model.getRemainder());
        link.setToDate(parseAndValidateDate(model.getToDate()));
        linksRepository.save(link);

        return shortLink;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String getOrigLink(LinkResponse linkResponse) {
        return "sdfsdf";
    }

    public static LocalDateTime parseAndValidateDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        LocalDateTime parsedDate;
        try {
            // Преобразование строки в LocalDateTime
            parsedDate = LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Неверный формат даты: " + dateStr);
        }

        // Проверка, что дата больше текущей
        if (parsedDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Дата должна быть больше текущей.");
        }

        return parsedDate;
    }
}
