package levelgenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class TemplateReader {

	private static String TEMPLATE = "/res/levelTemplate.xml";

	public String getTemplateTemplateReader() {

		try {
			InputStream is = getClass().getResourceAsStream(TEMPLATE);

			try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
					BufferedReader reader = new BufferedReader(streamReader)) {

				String line;
				String content = "";
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
					content = content + line + System.lineSeparator();
				}
				
				return content;

			} catch (IOException e) {
				e.printStackTrace();
			}

			is.close();
		} catch (Exception ex) {
			return null;

		}
		return null;
	}

}
