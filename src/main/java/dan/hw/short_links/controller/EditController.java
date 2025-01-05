package dan.hw.short_links.controller;

import dan.hw.short_links.configuration.AppProperties;
import dan.hw.short_links.exception.IncorrectDateException;
import dan.hw.short_links.exception.NotExistingLinkException;
import dan.hw.short_links.model.LinkEdit;
import dan.hw.short_links.model.LinkGet;
import dan.hw.short_links.model.StatusStub;
import dan.hw.short_links.service.LinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/edit-link")
@RequiredArgsConstructor
public class EditController {

    private final LinkService linkService;

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
    public String editLink(@ModelAttribute LinkEdit linkEdit, @RequestParam("action") String action, Model model) {
        if ("edit".equals(action)) {
            try {
                linkService.editLink(linkEdit);
            } catch (NotExistingLinkException | IncorrectDateException e) {
                model.addAttribute("message", e.getMessage());
            }
            model.addAttribute("message", "Редактирование прошло успешно");
        } else if ("fill".equals(action)) {
            try {
                LinkEdit foundLink =
                        linkService.findLink(new LinkGet(linkEdit.getLinkMasterId(), linkEdit.getShortLink()));
                model.addAttribute("linkEdit", foundLink);
                model.addAttribute("message", "Форма была заполнена.");
            } catch (NotExistingLinkException e) {
                model.addAttribute("message", e.getMessage());
            }

        }
        return "edit-link";
    }
}
