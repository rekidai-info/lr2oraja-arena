package bms.player.beatoraja.arena.font;

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontUtils {
    public static String CHARACTERS;

    static {
        final StringBuilder builder = new StringBuilder(8192);

        builder.append(FreeTypeFontGenerator.DEFAULT_CHARS);
        builder.append("☆★");
        builder.append("あいうえお");
        builder.append("かきくけこ");
        builder.append("さしすせそ");
        builder.append("たちつてと");
        builder.append("なにぬねの");
        builder.append("はひふへほ");
        builder.append("まみむめも");
        builder.append("やゆよ");
        builder.append("らりるれろ");
        builder.append("わゐうゑを");
        builder.append("ん");
        builder.append("アイウエオ");
        builder.append("カキクケコ");
        builder.append("サシスセソ");
        builder.append("タチツテト");
        builder.append("ナニヌネノ");
        builder.append("ハヒフヘホ");
        builder.append("マミムメモ");
        builder.append("ヤユヨ");
        builder.append("ラリルレロ");
        builder.append("ワヰウヱヲ");
        builder.append("ン");
        builder.append("ゔ");
        builder.append("がぎぐげご");
        builder.append("ざじずぜぞ");
        builder.append("だぢづでど");
        builder.append("ばびぶべぼ");
        builder.append("ヴ");
        builder.append("ガギグゲゴ");
        builder.append("ザジズゼゾ");
        builder.append("ダヂヅデド");
        builder.append("バビブベボ");
        builder.append("ヷヸヹヺ");
        builder.append("ぱぴぷぺぽ");
        builder.append("パピプペポ");
        builder.append("ぁぃぅぇぉ");
        builder.append("っ");
        builder.append("ゃゅょ");
        builder.append("ゎ");
        builder.append("ァィゥェォ");
        builder.append("ヵㇰヶ");
        builder.append("ㇱㇲ");
        builder.append("ッㇳ");
        builder.append("ㇴ");
        builder.append("ㇵㇶㇷㇸㇹ");
        builder.append("ㇺ");
        builder.append("ャュョ");
        builder.append("ㇻㇼㇽㇾㇿ");
        builder.append("ヮ");
        builder.append("ゝ");
        builder.append("ゞ");
        builder.append("ヽ");
        builder.append("ヾ");
        builder.append("゛");
        builder.append("゜");
        builder.append("。");
        builder.append("、");
        builder.append("ー");
        builder.append("－");
        builder.append("―");
        builder.append("～");
        builder.append("１２３４５６７８９０");
        builder.append("ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ");
        builder.append("ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ");

        CHARACTERS = builder.toString();
    }

    private FontUtils() {
        throw new AssertionError();
    }
}