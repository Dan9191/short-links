package dan.hw.short_links.controller;

import dan.hw.short_links.configuration.AppProperties;
import dan.hw.short_links.exception.IncorrectDateException;
import dan.hw.short_links.model.LinkRequest;
import dan.hw.short_links.model.LinkResponse;
import dan.hw.short_links.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/generate")
@RequiredArgsConstructor
public class GenerateController {

    private final LinkService linkService;

    private final AppProperties appProperties;

    @GetMapping
    public String generatePage(Model model) {
        model.addAttribute("linkRequest", new LinkRequest("",
                "",
                "",
                appProperties.getRemainder()));
        return "generate";
    }

    @PostMapping
    public String generateString(
            @ModelAttribute LinkRequest linkRequest, Model model) {
        try {
            LinkResponse linkResponse = linkService.generateShortLink(linkRequest);
            model.addAttribute("linkMasterId", linkResponse.getLinkMasterId());
            model.addAttribute("shortLink", linkResponse.getShortLink());
        } catch (NoSuchAlgorithmException | IncorrectDateException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "generate";
    }


}
