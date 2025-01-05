package dan.hw.short_links.controller;

import dan.hw.short_links.model.LinkResponse;
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
        model.addAttribute("linkResponse", new LinkResponse("", ""));
        return "get-link";
    }

    @PostMapping
    public String getLink(@ModelAttribute LinkResponse linkResponse, Model model) {
        model.addAttribute("origLink", linkService.getOrigLink(linkResponse));
        return "get-link";
    }

}
