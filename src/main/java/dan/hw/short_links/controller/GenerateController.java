package dan.hw.short_links.controller;

import dan.hw.short_links.configuration.AppProperties;
import dan.hw.short_links.exception.IncorrectDateException;
import dan.hw.short_links.model.LinkGenerate;
import dan.hw.short_links.model.LinkGet;
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
        model.addAttribute("linkGenerate", new LinkGenerate("",
                "",
                "",
                appProperties.getRemainder()));
        return "generate";
    }

    @PostMapping
    public String generateString(
            @ModelAttribute LinkGenerate linkGenerate, Model model) {
        try {
            LinkGet linkGet = linkService.generateShortLink(linkGenerate);
            model.addAttribute("linkMasterId", linkGet.getLinkMasterId());
            model.addAttribute("shortLink", linkGet.getShortLink());
        } catch (NoSuchAlgorithmException | IncorrectDateException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "generate";
    }


}
