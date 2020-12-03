package springfox.javadoc.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Документация контроллера на русском
 *
 * <p>Несколько строк документации<p/>
 *
 * @author mitasov-ra
 */
@RequestMapping(path = "/russian", method = RequestMethod.PUT)
public class RussianDocsController {


    /**
     * Русская документация метода
     *
     * @param text Параметр с русскими символами в Regex
     * @return Вернёт что-то
     */
    @GetMapping(value = "/{text:[\\wа-яА-Я:._\\s]+}")
    public String blaRus(@PathVariable String text) {
        return "foo" + text;
    }
}
