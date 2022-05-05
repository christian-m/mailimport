package cloud.matzat.aws.mailimport.test.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Util class for test support.
 *
 * @author Christian Matzat (christian@matzat.net)
 */
public final class TestUtils {

    private TestUtils() {
    }

    public static String readFile(String filePath) throws IOException {
        final Resource resource = new ClassPathResource(filePath);
        final InputStream inputStream = resource.getInputStream();
        final StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
