package dan.hw.short_links.controller;

import dan.hw.short_links.configuration.AppProperties;
import dan.hw.short_links.model.LinkEdit;
import dan.hw.short_links.model.StatusStub;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/edit-link")
@RequiredArgsConstructor
public class EditController {

    private final AppProperties appProperties;

    @GetMapping
    public String editLinkPage(Model model) {
        model.addAttribute("linkEdit", new LinkEdit("",
                "",
                "",
                appProperties.getRemainder(),
                StatusStub.STATUS_ON));
        return "edit-link";
    }

    @PostMapping
    public String editLink(@ModelAttribute LinkEdit linkEdit, Model model) {

//        LinkResponse linkResponse = new LinkResponse(linkMasterId, shortLink);
//        String origLink = linkService.getOrigLink(linkResponse);
        model.addAttribute("origLink", "asdasd");

        return "edit-link";
    }
}
