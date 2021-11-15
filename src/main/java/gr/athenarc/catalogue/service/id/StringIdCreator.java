package gr.athenarc.catalogue.service.id;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Random;

@Service
public class StringIdCreator implements IdCreator<String> {

    public static final int DEFAULT_LENGTH = 8;

    @Override
    public String createId() {
        return createId("", DEFAULT_LENGTH);
    }

    @Override
    public String createId(String prefix) {
        return createId(prefix, DEFAULT_LENGTH);
    }

    @Override
    public String createId(String prefix, int length) {
        if (prefix == null) {
            prefix = "";
        }
        return prefix + RandomStringUtils.randomAlphabetic(length);
    }
}
