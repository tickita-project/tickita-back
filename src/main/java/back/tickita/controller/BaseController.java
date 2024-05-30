package back.tickita.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.ResolvableType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class BaseController {

    private static final String authorizationRequestBaseUri = "oauth2/authorization";
    private final ClientRegistrationRepository clientRegistrationRepository;

    public BaseController(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/account")
    public String account() { return "account"; }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

        Iterable<ClientRegistration> clientRegistrations = getClientRegistrations(clientRegistrationRepository);
        if (clientRegistrations != null) {
            clientRegistrations.forEach(registration ->
                    oauth2AuthenticationUrls.put(registration.getClientName(),
                            authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
        }

        model.addAttribute("urls", oauth2AuthenticationUrls);
        return "login";
    }

    @SuppressWarnings("unchecked")
    private Iterable<ClientRegistration> getClientRegistrations(ClientRegistrationRepository repository) {
        ResolvableType type = ResolvableType.forInstance(repository).as(Iterable.class);
        if (type != ResolvableType.NONE &&
                ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            return (Iterable<ClientRegistration>) repository;
        }
        return null;
    }

//    @GetMapping("/login/{oauth2}")
//    public String loginGoogle(@PathVariable String oauth2, HttpServletResponse httpServletResponse) {
//        return "redirect:/oauth2/authorization/" + oauth2;
//    }

    @RequestMapping("/accessDenied")
    public String accessDenied() {
        return "accessDenied";
    }
}
