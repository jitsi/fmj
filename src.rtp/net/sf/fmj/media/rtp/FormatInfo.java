package net.sf.fmj.media.rtp;

import javax.media.*;
import javax.media.format.*;

public class FormatInfo
{
    public static boolean isSupported(int i)
    {
        switch (i)
        {
        case 0: // '\0'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 8: // '\b'
        case 14: // '\016'
        case 15: // '\017'
        case 16: // '\020'
        case 17: // '\021'
        case 18: // '\022'
        case 26: // '\032'
        case 31: // '\037'
        case 32: // ' '
        case 34: // '"'
            return true;

        case 1: // '\001'
        case 2: // '\002'
        case 7: // '\007'
        case 9: // '\t'
        case 10: // '\n'
        case 11: // '\013'
        case 12: // '\f'
        case 13: // '\r'
        case 19: // '\023'
        case 20: // '\024'
        case 21: // '\025'
        case 22: // '\026'
        case 23: // '\027'
        case 24: // '\030'
        case 25: // '\031'
        case 27: // '\033'
        case 28: // '\034'
        case 29: // '\035'
        case 30: // '\036'
        case 33: // '!'
        case 35: // '#'
        case 36: // '$'
        case 37: // '%'
        case 38: // '&'
        case 39: // '\''
        case 40: // '('
        case 41: // ')'
        case 42: // '*'
        case 43: // '+'
        case 44: // ','
        case 45: // '-'
        case 46: // '.'
        case 47: // '/'
        case 48: // '0'
        case 49: // '1'
        case 50: // '2'
        case 51: // '3'
        case 52: // '4'
        case 53: // '5'
        case 54: // '6'
        case 55: // '7'
        case 56: // '8'
        case 57: // '9'
        case 58: // ':'
        case 59: // ';'
        case 60: // '<'
        case 61: // '='
        case 62: // '>'
        case 63: // '?'
        case 64: // '@'
        case 65: // 'A'
        case 66: // 'B'
        case 67: // 'C'
        case 68: // 'D'
        case 69: // 'E'
        case 70: // 'F'
        case 71: // 'G'
        case 72: // 'H'
        case 73: // 'I'
        case 74: // 'J'
        case 75: // 'K'
        case 76: // 'L'
        case 77: // 'M'
        case 78: // 'N'
        case 79: // 'O'
        case 80: // 'P'
        case 81: // 'Q'
        case 82: // 'R'
        case 83: // 'S'
        case 84: // 'T'
        case 85: // 'U'
        case 86: // 'V'
        case 87: // 'W'
        case 88: // 'X'
        case 89: // 'Y'
        case 90: // 'Z'
        case 91: // '['
        case 92: // '\\'
        case 93: // ']'
        case 94: // '^'
        case 95: // '_'
        case 96: // '`'
        case 97: // 'a'
        case 98: // 'b'
        case 99: // 'c'

        case 100: // 'd'
        case 101: // 'e'
        case 102: // 'f'
        case 103: // 'g'
        case 104: // 'h'
        case 105: // 'i'
        case 106: // 'j'
        case 107: // 'k'
        case 108: // 'l'
        case 109: // 'm'
        case 110: // 'n'
        default:
            return false;
        }
    }

    private SSRCCache cache;

    public static final int PAYLOAD_NOTFOUND = -1;

    Format formatList[];

    static AudioFormat mpegAudio = new AudioFormat("mpegaudio/rtp");

    public FormatInfo()
    {
        cache = null;
        formatList = new Format[111];
        initFormats();
    }

    public void add(int i, Format format)
    {
        if (i >= formatList.length)
            expandTable(i);

        Format existingFormat = formatList[i];

        if (existingFormat != null)
        {
            /*
             * XXX If the specified format is matches-equivalent to the
             * existingFormat, it shouldn't disturb the rest of the code/logic.
             * However, the specified format may contain additional and/or more
             * recent information (e.g. format parameters which are not
             * differentiating) which may be vital to the application.
             */
            if ((format == null)
                    || !existingFormat.matches(format)
                    || !format.matches(existingFormat))
                return;
        }

        formatList[i] = format;
        if (cache != null && (format instanceof VideoFormat))
            cache.clockrate[i] = 0x15f90;
        if (cache != null && (format instanceof AudioFormat))
        {
            if (mpegAudio.matches(format))
            {
                cache.clockrate[i] = 0x15f90;
            }
            else
            {
                cache.clockrate[i]
                    = (int) ((AudioFormat) format).getSampleRate();
            }
        }
    }

    private void expandTable(int i)
    {
        Format aformat[] = new Format[i + 1];
        for (int j = 0; j < formatList.length; j++)
            aformat[j] = formatList[j];

        formatList = aformat;
    }

    public Format get(int i)
    {
        return i >= formatList.length ? null : formatList[i];
    }

    public int getPayload(Format format)
    {
        if (format.getEncoding() != null
                && format.getEncoding().equals("g729a/rtp"))
            format = new AudioFormat("g729/rtp");
        for (int i = 0; i < formatList.length; i++)
            if (format.matches(formatList[i]))
                return i;

        return -1;
    }

    public void initFormats()
    {
        formatList[0] = new AudioFormat("ULAW/rtp", 8000D, 8, 1);
        formatList[3] = new AudioFormat("gsm/rtp", 8000D, -1, 1);
        formatList[4] = new AudioFormat("g723/rtp", 8000D, -1, 1);
        formatList[5] = new AudioFormat("dvi/rtp", 8000D, 4, 1);
        formatList[8] = new AudioFormat("ALAW/rtp", 8000D, 8, 1);
        formatList[14] = new AudioFormat("mpegaudio/rtp", -1D, -1, -1);
        formatList[15] = new AudioFormat("g728/rtp", 8000D, -1, 1);
        formatList[16] = new AudioFormat("dvi/rtp", 11025D, 4, 1);
        formatList[17] = new AudioFormat("dvi/rtp", 22050D, 4, 1);
        formatList[18] = new AudioFormat("g729/rtp", 8000D, -1, 1);
        formatList[26] = new VideoFormat("jpeg/rtp");
        formatList[31] = new VideoFormat("h261/rtp");
        formatList[32] = new VideoFormat("mpeg/rtp");
        formatList[34] = new VideoFormat("h263/rtp");
        // Seb: remove H263-1998
        // formatList[42] = new VideoFormat("h263-1998/rtp");
        // formatList[96] = new VideoFormat("h264/rtp");
        // formatList[97] = new AudioFormat("ilbc/rtp", 8000D, -1, 1);
        // formatList[98] = new AudioFormat("ilbc/rtp", 8000D, -1, 1);
        // formatList[99] = new VideoFormat("h264/rtp");
        // formatList[110] = new AudioFormat("speex/rtp", 8000D, 8, 1);
    }

    public void setCache(SSRCCache ssrccache)
    {
        cache = ssrccache;
    }

}
