package de.vstange;

import org.junit.Assert;
import org.junit.Test;

public class MainTest {

    @Test
    public void getArticleType() {
        String articleType = Main.getArticleType("archivearticle1.dtd\">\n" +
                "<article xmlns:mml=\"http://www.w3.org/1998/Math/MathML\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" article-type=\"research-article\"><?properties open_access?><front><journal-meta><journal-id journal-id-type=\"nlm-ta\">3 Biotech</journal-id><journal-id journal-id-type=\"iso-ab");
        Assert.assertEquals("research-article", articleType);
    }

}
