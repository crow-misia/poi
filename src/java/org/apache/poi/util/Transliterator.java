/**
 * Copyright(C) 2011 Minori Solutions Co., Ltd.
 * All rights reserved.
 */
package org.apache.poi.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 文字種変換ユーティリティ<BR>
 * 変換ルールを限定することで速度向上を図る
 */
public final class Transliterator {
    /** 半角カタカナから全角カタカナへの変換フィルタ */
    public static final TransliterateFilter HANKAKU_KANA_TO_KATAKANA = new HankakuKanaToKatakana();
    /** 半角カタカナからひらがなへの変換フィルタ */
    public static final TransliterateFilter HANKAKU_KANA_TO_HIRAGANA = new HankakuKanaToHiragana();
    /** 全角カタカナから半角カタカナへの変換フィルタ */
    public static final TransliterateFilter KATAKANA_TO_HANKAKU_KANA = new KatakanaToHankakuKana();
    /** ひらがなから半角カタカナへの変換フィルタ */
    public static final TransliterateFilter HIRAGANA_TO_HANKAKU_KANA = new HiraganaToHankakuKana();
    /** カタカナからひらがなへの変換フィルタ */
    public static final TransliterateFilter KATAKANA_TO_HIRAGANA = new KatakanaToHiragana();
    /** ひらがなからカタカナへの変換フィルタ */
    public static final TransliterateFilter HIRAGANA_TO_KATAKANA = new HiraganaToKatakana();
    /** 半角英字から全角英字への変換フィルタ */
    public static final TransliterateFilter HANKAKU_TO_ZENKAKU_ALPHABET = new HankakuToZenkakuAlphabet();
    /** 半角数字から全角数字への変換フィルタ */
    public static final TransliterateFilter HANKAKU_TO_ZENKAKU_NUMERIC = new HankakuToZenkakuNumeric();
    /** 半角記号から全角記号への変換フィルタ */
    public static final TransliterateFilter HANKAKU_TO_ZENKAKU_SYMBOL = new HankakuToZenkakuSymbol();
    /** 半角記号から全角記号(一般的な記号へ変換)への変換フィルタ */
    public static final TransliterateFilter HANKAKU_TO_ZENKAKU_SYMBOL_EXPAND = new HankakuToZenkakuSymbolExpand();
    /** 半角英数字記号から全角英数字記号への変換フィルタ */
    public static final TransliterateFilter HANKAKU_TO_ZENKAKU_ALPHANUMSYMBOL = new HankakuToZenkakuAlphaNumSymbol();
    /** 半角英数字記号から全角英数字記号(一般的な記号へ変換)への変換フィルタ */
    public static final TransliterateFilter HANKAKU_TO_ZENKAKU_ALPHANUMSYMBOL_EXPAND = new HankakuToZenkakuAlphaNumSymbolExpand();
    /** 半角スペースから全角スペースへの変換フィルタ */
    public static final TransliterateFilter HANKAKU_TO_ZENKAKU_SPACE = new HankakuToZenkakuSpace();
    /** 全角英字から半角英字への変換フィルタ */
    public static final TransliterateFilter ZENKAKU_TO_HANKAKU_ALPHABET = new ZenkakuToHankakuAlphabet();
    /** 全角数字から半角数字への変換フィルタ */
    public static final TransliterateFilter ZENKAKU_TO_HANKAKU_NUMERIC = new ZenkakuToHankakuNumeric();
    /** 全角記号から半角記号への変換フィルタ */
    public static final TransliterateFilter ZENKAKU_TO_HANKAKU_SYMBOL = new ZenkakuToHankakuSymbol();
    /** 全角英数字記号から半角英数字記号への変換フィルタ */
    public static final TransliterateFilter ZENKAKU_TO_HANKAKU_ALPHANUMSYMBOL = new ZenkakuToHankakuAlphaNumSymbol();
    /** 全角スペースから半角スペースへの変換フィルタ */
    public static final TransliterateFilter ZENKAKU_TO_HANKAKU_SPACE = new ZenkakuToHankakuSpace();
    /** 正規化フィルタ */
    public static final TransliterateFilter NORMALIZE = new Normalize();

    /** 半角カタカナ -> 全角カタカナマッピング */
    static final char[] KATAKANA_MAPPING = new char[] { '。', '「', '」',
            '、', '・', 'ヲ', 'ァ', 'ィ', 'ゥ', 'ェ', 'ォ', 'ャ', 'ュ', 'ョ', 'ッ', 'ー',
            'ア', 'イ', 'ウ', 'エ', 'オ', 'カ', 'キ', 'ク', 'ケ', 'コ', 'サ', 'シ', 'ス',
            'セ', 'ソ', 'タ', 'チ', 'ツ', 'テ', 'ト', 'ナ', 'ニ', 'ヌ', 'ネ', 'ノ', 'ハ',
            'ヒ', 'フ', 'ヘ', 'ホ', 'マ', 'ミ', 'ム', 'メ', 'モ', 'ヤ', 'ユ', 'ヨ', 'ラ',
            'リ', 'ル', 'レ', 'ロ', 'ワ', 'ン', '゛', '゜', };

    /** 半角カタカナ -> 全角ひらがなマッピング */
    static final char[] HIRAGANA_MAPPING = new char[] { '。', '「', '」',
            '、', '・', 'を', 'ぁ', 'ぃ', 'ぅ', 'ぇ', 'ぉ', 'ゃ', 'ゅ', 'ょ', 'っ', 'ー',
            'あ', 'い', 'う', 'え', 'お', 'か', 'き', 'く', 'け', 'こ', 'さ', 'し', 'す',
            'せ', 'そ', 'た', 'ち', 'つ', 'て', 'と', 'な', 'に', 'ぬ', 'ね', 'の', 'は',
            'ひ', 'ふ', 'へ', 'ほ', 'ま', 'み', 'む', 'め', 'も', 'や', 'ゆ', 'よ', 'ら',
            'り', 'る', 'れ', 'ろ', 'わ', 'ん', '゛', '゜', };

    /** 全角カタカナ -> 半角カタカナマッピング */
    static final String[] HANKAKU_KANA_MAPPING = new String[] {
            "ｧ", "ｱ", "ｨ", "ｲ", "ｩ", "ｳ", "ｪ", "ｴ", "ｫ", "ｵ",
            "ｶ", "ｶﾞ", "ｷ", "ｷﾞ", "ｸ", "ｸﾞ", "ｹ", "ｹﾞ", "ｺ", "ｺﾞ",
            "ｻ", "ｻﾞ", "ｼ", "ｼﾞ", "ｽ", "ｽﾞ", "ｾ", "ｾﾞ", "ｿ", "ｿﾞ",
            "ﾀ", "ﾀﾞ", "ﾁ", "ﾁﾞ", "ｯ", "ﾂ", "ﾂﾞ", "ﾃ", "ﾃﾞ", "ﾄ", "ﾄﾞ",
            "ﾅ", "ﾆ", "ﾇ", "ﾈ", "ﾉ",
            "ﾊ", "ﾊﾞ", "ﾊﾟ", "ﾋ", "ﾋﾞ", "ﾋﾟ", "ﾌ", "ﾌﾞ", "ﾌﾟ", "ﾍ", "ﾍﾞ", "ﾍﾟ", "ﾎ", "ﾎﾞ", "ﾎﾟ",
            "ﾏ", "ﾐ", "ﾑ", "ﾒ", "ﾓ", "ｬ", "ﾔ", "ｭ", "ﾕ", "ｮ", "ﾖ",
            "ﾗ", "ﾘ", "ﾙ", "ﾚ", "ﾛ", };

    /** Filter */
    private final List<TransliterateFilter> filters = new ArrayList<>();

    /**
     * フィルタを追加
     * @param filter フィルタ
     * @return Transliterator
     */
    public Transliterator addFilter(final TransliterateFilter filter) {
        this.filters.add(filter);
        return this;
    }

    /**
     * 文字種変換
     * @param str 対象文字列
     * @return 変換後文字列
     */
    public String transform(final String str) {
        if (str == null) {
            return null;
        }
        final int length = str.length();
        if (length <= 0) {
            return str;
        }

        final StringBuilder sb = new StringBuilder(length + 16);
        char ch;
        char nextCh = str.charAt(0);
        int result;
        int i = 0;
        while (i < length) {
            ch = nextCh;
            i++;
            if (i >= length) {
                nextCh = (char) -1;
            } else {
                nextCh = str.charAt(i);
            }

            result = 0;
            for (final TransliterateFilter filter : filters) {
                result = filter.transform(sb, ch, nextCh);
                if (result != 0) {
                    break;
                }
            }

            switch (result) {
            case 0:
                sb.append(ch);
                break;
            case 1:
                break;
            case 2:
                i++;
                if (i < length) {
                    nextCh = str.charAt(i);
                }
                break;
            default:
                break;
            }
        }

        return sb.toString();
    }

    /**
     * 文字変換フィルタ　インタフェース
     */
    public interface TransliterateFilter {
        /**
         * 文字を変換する
         * @param sb 出力バッファ
         * @param ch 対象文字
         * @param nextCh 対象文字の次の文字
         * @return 処理文字数
         */
        int transform(final StringBuilder sb, final char ch, final char nextCh);
    }

    /**
     * 半角カタカナ -> 全角カタカナ フィルタ
     */
    protected static class HankakuKanaToKatakana implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {
            int offset;
            char extend = 0;

            switch (nextCh) {
            case 'ﾞ':
                // 次の文字が ﾞ の場合、濁音にする
                if ((ch >= 'ｶ' && ch <= 'ﾄ') || (ch >= 'ﾊ' && ch <= 'ﾎ')) {
                    extend = 1;
                } else if (ch == 'ｳ') {
                    // ヴの場合は、例外
                    sb.append('ヴ');
                    return 2;
                }
                break;
            case 'ﾟ':
                // 次の文字が ゜ の場合、半濁音にする
                if (ch >= 'ﾊ' && ch <= 'ﾎ') {
                    extend = 2;
                }
                break;
            default:
                break;
            }

            // 。～ソ
            if (ch >= '｡' && ch <= 'ｿ') {
                offset = ch - '｡';
            } else
            // タ～゜
            if (ch >= 'ﾀ' && ch <= 'ﾟ') {
                offset = ch - ('ﾀ' - 31);
            } else {
                // その他の文字
                return 0;
            }

            sb.append((char) (KATAKANA_MAPPING[offset] + extend));
            return extend == 0 ? 1 : 2;
        }

    }

    /**
     * 半角カタカナ -> 全角ひらがな フィルタ
     */
    protected static class HankakuKanaToHiragana implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {
            int offset;
            char extend = 0;

            switch (nextCh) {
            case 'ﾞ':
                // 次の文字が ﾞ の場合、濁音にする
                if ((ch >= 'ｶ' && ch <= 'ﾄ') || (ch >= 'ﾊ' && ch <= 'ﾎ')) {
                    extend = 1;
                }
                break;
            case 'ﾟ':
                // 次の文字が ゜ の場合、半濁音にする
                if (ch >= 'ﾊ' && ch <= 'ﾎ') {
                    extend = 2;
                }
                break;
            default:
                break;
            }

            // 。～ソ
            if (ch >= '｡' && ch <= 'ｿ') {
                offset = ch - '｡';
            } else
            // タ～゜
            if (ch >= 'ﾀ' && ch <= 'ﾟ') {
                offset = ch - ('ﾀ' - 31);
            } else {
                // その他の文字
                return 0;
            }

            sb.append((char) (HIRAGANA_MAPPING[offset] + extend));
            return extend == 0 ? 1 : 2;
        }

    }

    /**
     * 全角カタカナ -> 半角カタカナ フィルタ
     */
    protected static class KatakanaToHankakuKana implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {
            int offset;

            switch (ch) {
            case '。':
                sb.append('｡');
                return 1;
            case '、':
                sb.append('､');
                return 1;
            case 'ー':
                sb.append('ｰ');
                return 1;
            case '・':
                sb.append('･');
                return 1;
            case 'ヲ':
                sb.append('ｦ');
                return 1;
            case 'ワ':
                sb.append('ﾜ');
                return 1;
            case 'ン':
                sb.append('ﾝ');
                return 1;
            case 'ヴ':
                sb.append("ｳﾞ");
                return 1;
            case '゛':
                sb.append('ﾞ');
                return 1;
            case '゜':
                sb.append('ﾟ');
                return 1;
            default:
                if (ch >= 'ァ' && ch <= 'ロ') {
                    offset = ch - 'ァ';
                    sb.append(HANKAKU_KANA_MAPPING[offset]);
                    return 1;
                }
            }

            // その他の文字
            return 0;
        }
    }

    /**
     * 全角ひらがな -> 半角カタカナ フィルタ
     */
    protected static class HiraganaToHankakuKana implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {
            int offset;

            switch (ch) {
            case '。':
                sb.append('｡');
                return 1;
            case '、':
                sb.append('､');
                return 1;
            case 'ー':
                sb.append('ｰ');
                return 1;
            case '・':
                sb.append('･');
                return 1;
            case 'を':
                sb.append('ｦ');
                return 1;
            case 'わ':
                sb.append('ﾜ');
                return 1;
            case 'ん':
                sb.append('ﾝ');
                return 1;
            case '゛':
                sb.append('ﾞ');
                return 1;
            case '゜':
                sb.append('ﾟ');
                return 1;
            default:
                if (ch >= 'ぁ' && ch <= 'ろ') {
                    offset = ch - 'ぁ';
                    sb.append(HANKAKU_KANA_MAPPING[offset]);
                    return 1;
                }
            }

            // その他の文字
            return 0;
        }
    }

    /**
     * 全角カタカナ -> 全角ひらがな フィルタ
     */
    protected static class KatakanaToHiragana implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {

            // ァ～ン
            if (ch >= 'ァ' && ch <= 'ン') {
                sb.append((char) (ch - 0x60)); // 'ぁ' - 'ァ'
                return 1;
            }

            if (ch == 'ヴ') {
                sb.append("う゛");
                return 1;
            }

            // その他の文字
            return 0;
        }
    }

    /**
     * 全角ひらがな -> 全角かたかな フィルタ
     */
    protected static class HiraganaToKatakana implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {

            if (ch == 'う' && nextCh == '゛') {
                // う゛
                sb.append("ヴ");
                return 2;
            }

            if (ch >= 'ぁ' && ch <= 'ん') {
                // ぁ～ん
                sb.append((char) (ch + 0x60)); // 'ァ' - 'ぁ'
                return 1;
            }

            // その他の文字
            return 0;
        }
    }

    /**
     * 半角アルファベット -> 全角アルファベット フィルタ
     */
    protected static class HankakuToZenkakuAlphabet implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {

            if ((ch >= 0x41 && ch <= 0x5a) || (ch >= 0x61 && ch <= 0x7a)) {
                // A-Z a-z
                sb.append((char) (ch + 0xfee0)); // 'Ａ' - 'A'
                return 1;
            }

            // その他の文字
            return 0;
        }
    }

    /**
     * 半角数字 -> 全角数字 フィルタ
     */
    protected static class HankakuToZenkakuNumeric implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {

            if (ch >= 0x30 && ch <= 0x39) {
                // 0 - 9
                sb.append((char) (ch + 0xfee0)); // '０' - '0'
                return 1;
            }

            // その他の文字
            return 0;
        }
    }

    /**
     * 半角記号 -> 全角記号 フィルタ
     */
    protected static class HankakuToZenkakuSymbol implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {

            if ((ch >= 0x21 && ch <= 0x2f) ||
                    (ch >= 0x3a && ch <= 0x40) ||
                    (ch >= 0x5b && ch <= 0x60) ||
                    (ch >= 0x7b && ch <= 0x7e)) {
                // ! - /
                // : - @
                // [ - `
                // { - ~
                sb.append((char) (ch + 0xfee0));
                return 1;
            }

            // その他の文字
            return 0;
        }
    }

    /**
     * 半角記号 -> 全角記号(一般的な記号に変換) フィルタ
     */
    protected static class HankakuToZenkakuSymbolExpand extends HankakuToZenkakuSymbol {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {

            // 例外的な変換を行う
            switch (ch) {
            case 0x22:
                sb.append('”');
                return 1;
            case 0x27:
                sb.append('’');
                return 1;
            default:
                return super.transform(sb, ch, nextCh);
            }

        }
    }

    /**
     * 半角英数字記号 -> 全角英数字記号 フィルタ
     */
    protected static class HankakuToZenkakuAlphaNumSymbol implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {

            if (ch >= 0x21 && ch <= 0x7e) {
                // ! - ~
                sb.append((char) (ch + 0xfee0)); // '!' - '~'
                return 1;
            }
            if (ch == 0x20) {
                // space
                sb.append((char) 0x3000);
                return 1;
            }

            // その他の文字
            return 0;
        }
    }

    /**
     * 半角英数字記号 -> 全角英数字記号(一般的な記号に変換) フィルタ
     */
    protected static class HankakuToZenkakuAlphaNumSymbolExpand extends HankakuToZenkakuAlphaNumSymbol {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {
            // 例外的な変換を行う
            switch (ch) {
            case 0x22:
                sb.append('”');
                return 1;
            case 0x27:
                sb.append('’');
                return 1;
            case '`':
                sb.append('‘');
                return 1;
            case '\\':
                sb.append('￥');
                return 1;
            default:
                return super.transform(sb, ch, nextCh);
            }

        }
    }

    /**
     * 半角スペース -> 全角スペース フィルタ
     */
    protected static class HankakuToZenkakuSpace implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {
            if (ch == 0x20) {
                sb.append((char) 0x3000);
                return 1;
            }

            return 0;
        }
    }

    /**
     * 全角アルファベット -> 半角アルファベット フィルタ
     */
    protected static class ZenkakuToHankakuAlphabet implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {
            char newCh;

            if ((ch >= 'Ａ' && ch <= 'Ｚ') || (ch >= 'ａ' && ch <= 'ｚ')) {
                // A-Z a-z
                newCh = (char) (ch - 0xfee0); // 'A' - 'Ａ'
            } else {
                // その他の文字
                return 0;
            }

            sb.append(newCh);
            return 1;
        }
    }

    /**
     * 全角数字 -> 半角数字 フィルタ
     */
    protected static class ZenkakuToHankakuNumeric implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {
            char newCh;

            if (ch >= '０' && ch <= '９') {
                // 0 - 9
                newCh = (char) (ch - 0xfee0); // '0' - '０'
            } else {
                // その他の文字
                return 0;
            }

            sb.append(newCh);
            return 1;
        }
    }

    /**
     * 全角記号 -> 半角記号 フィルタ
     */
    protected static class ZenkakuToHankakuSymbol implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {

            if ((ch >= '！' && ch <= '／') ||
                    (ch >= '：' && ch <= '＠') ||
                    (ch >= '［' && ch <= '｀') ||
                    (ch >= '｛' && ch <= '～')) {
                // ! - /
                sb.append((char) (ch - 0xfee0)); // '!' - '！'
                return 1;
            }
            switch (ch) {
            case '。':
                sb.append('｡');
                return 1;
            case '、':
                sb.append('､');
                return 1;
            case '・':
                sb.append('･');
                return 1;
            case '「':
            case '」':
                // FF62-FF63
                sb.append(ch - 0xcf56);
                return 1;
            case '”':
                sb.append('"');
                return 1;
            case '’':
                sb.append('\'');
                return 1;
            default:
                // その他の文字
                return 0;
            }
        }
    }

    /**
     * 全角英数字記号 -> 半角英数字記号 フィルタ
     */
    protected static class ZenkakuToHankakuAlphaNumSymbol implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {

            if (ch >= '！' && ch <= '～') {
                // ! - ~
                sb.append((char) (ch - 0xfee0)); // '！' - '!'
                return 1;
            }
            switch (ch) {
            case '。':
                sb.append('｡');
                return 1;
            case '、':
                sb.append('､');
                return 1;
            case '・':
                sb.append('･');
                return 1;
            case '￥':
                sb.append('\\');
                return 1;
            case '「':
            case '」':
                // 300C -> FF62-FF63
                sb.append((char) (ch + 0xcf56));
                return 1;
            case '”':
                sb.append('"');
                return 1;
            case '’':
                sb.append('\'');
                return 1;
            case 0x3000: // space
                sb.append((char) 0x20);
                return 1;
            default:
                // その他の文字
                return 0;
            }
        }
    }

    /**
     * 全角スペース -> 半角スペース フィルタ
     */
    protected static class ZenkakuToHankakuSpace implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {
            if (ch == 0x3000) {
                sb.append((char) 0x20);
                return 1;
            }

            return 0;
        }
    }

    /**
     * 正規化 フィルタ
     */
    protected static class Normalize implements TransliterateFilter {

        @Override
        public int transform(final StringBuilder sb, final char ch, final char nextCh) {
            switch (ch) {
            case 0xff02:
                // ＂ -> ”
                sb.append('”');
                return 1;
            case 0xff07:
                // ＇ -> ’
                sb.append('’');
                return 1;
            case 0x301c:
            case 0x3030:
                // 〜 -> ～
                // 〰 -> ～
                sb.append((char) 0xff5e);
                return 1;
            case 0xff5f:
                // ｟ -> 《
                sb.append('《');
                return 1;
            case 0xff60:
                // ｠ -> 》
                sb.append('》');
                return 1;
            case 0x2015:
                // ― -> －
                sb.append((char) 0xff0d);
                return 1;
            case 0x2016:
                // ‖ -> ∥
                sb.append((char) 0x2225);
                return 1;
            default:
                // その他の文字
                return 0;
            }

        }
    }

}
