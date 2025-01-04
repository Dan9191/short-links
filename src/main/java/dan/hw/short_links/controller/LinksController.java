package dan.hw.short_links.controller;

import dan.hw.short_links.exception.ExistingLinkException;
import dan.hw.short_links.exception.IncorrectDateException;
import dan.hw.short_links.model.LinkRequest;
import dan.hw.short_links.model.LinkResponse;
import dan.hw.short_links.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class LinksController {

    private final LinkService linkService;


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
        LinkRequest linkModel = new LinkRequest(userName, origLink, toDate, remainder);
        try {
            String shortLink = linkService.generateShortLink(linkModel);
            model.addAttribute("shortLink", shortLink);
        } catch (NoSuchAlgorithmException | IncorrectDateException e) {
            model.addAttribute("shortLink", e.getMessage());
        } catch (ExistingLinkException e) {
            model.addAttribute("shortLink", "ошибка преобразования ссылки");
        }
        return "generate";
    }

    @GetMapping("get-link")
    public String getLinkPage() {
        return "get-link";
    }

    @PostMapping("get-link")
    public String getLink(
            @RequestParam String linkMasterId,
            @RequestParam String shortLink,
            Model model) {

        LinkResponse linkResponse = new LinkResponse(linkMasterId, shortLink);
        String origLink = linkService.getOrigLink(linkResponse);
        model.addAttribute("origLink", origLink);

        return "get-link";
    }

}
