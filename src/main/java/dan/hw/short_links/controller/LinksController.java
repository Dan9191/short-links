package dan.hw.short_links.controller;

import dan.hw.short_links.configuration.AppProperties;
import dan.hw.short_links.exception.ExistingLinkException;
import dan.hw.short_links.model.LinkRequest;
import dan.hw.short_links.model.LinkResponse;
import dan.hw.short_links.service.LinksService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class LinksController {

    private final LinksService linksService;

    private final AppProperties appProperties;

    @GetMapping("generate")
    public String generatePage() {
        return "generate";
    }

    @PostMapping("generate")
    public String generateString(
            @RequestParam String userName,
            @RequestParam String toDate,
            @RequestParam Long remainder,
            @RequestParam String origLink,
            Model model) {
        if (toDate == null) {
            ChronoUnit temporalUnit;
            LocalDateTime updatedToDate = LocalDateTime.now().plus(appProperties.getAmountToAdd(), ChronoUnit.DAYS);
        }
        LinkRequest linkModel = new LinkRequest(userName, origLink, toDate, remainder);
        try {
            String shortLink = linksService.generateShortLink(linkModel);
            model.addAttribute("shortLink", shortLink);
        } catch (NoSuchAlgorithmException e) {
            model.addAttribute("shortLink", e.getMessage());
        } catch (ExistingLinkException e) {
            model.addAttribute("shortLink", "ошибка преоразования ссылки");
        }
        return "generate";
    }

    @GetMapping("get-link")
    public String getLinkPage() {
        return "get-link";
    }

    @PostMapping("get-link")
    public String getLink(
            @RequestParam String userName,
            @RequestParam String shortLink,
            Model model) {

        LinkResponse linkResponse = new LinkResponse(userName, shortLink);
        String origLink = linksService.getOrigLink(linkResponse);
        model.addAttribute("origLink", origLink);

        return "get-link";
    }
}
