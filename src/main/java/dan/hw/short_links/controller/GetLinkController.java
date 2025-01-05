package dan.hw.short_links.controller;

import dan.hw.short_links.exception.NotActiveLinkException;
import dan.hw.short_links.exception.NotExistingLinkException;
import dan.hw.short_links.model.LinkGet;
import dan.hw.short_links.model.LinkGetResponse;
import dan.hw.short_links.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/get-link")
@RequiredArgsConstructor
public class GetLinkController {

    private final LinkService linkService;

    @GetMapping
    public String getLinkPage(Model model) {
        model.addAttribute("linkGet", new LinkGet("", ""));
        return "get-link";
    }

    @PostMapping
    public String getLink(@ModelAttribute LinkGet linkGet, Model model) {
        try {
            LinkGetResponse result = linkService.getOrigLink(linkGet);
            model.addAttribute("origLink", result.getOrigLink());
            if (result.getMessage() != null && !result.getMessage().equals("")) {
                model.addAttribute("message", result.getMessage());
            }
        } catch (NotExistingLinkException | NotActiveLinkException e) {
            model.addAttribute("message", e.getMessage());
        }
        return "get-link";
    }

}
