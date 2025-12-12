package njb.pcms.util;

import org.springframework.stereotype.Component;

/**
 * ひらがなをカタカナに変換するユーティリティ
 */
@Component
public class KanaConverter {

    /**
     * ひらがなをカタカナに変換します。
     * 
     * @param hiragana 変換するひらがな
     * @return 変換後のカタカナ
     */
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
