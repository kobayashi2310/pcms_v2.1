package njb.pcms.util;

import org.springframework.stereotype.Component;

@Component
public class KanaConverter {

    public String hiraganaToKatakana(String hiragana) {
        if (hiragana == null) {
            return null;
        }
        StringBuilder katakana = new StringBuilder(hiragana.length());
        for (int i = 0; i < hiragana.length(); i++) {
            char c = hiragana.charAt(i);
            if (c >= 'ぁ' && c <= 'ん') {
                katakana.append((char) (c - 'ぁ' + 'ァ'));
            } else {
                katakana.append(c);
            }
        }
        return katakana.toString();
    }

}
