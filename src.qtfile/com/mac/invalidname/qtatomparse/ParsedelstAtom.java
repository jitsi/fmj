/*
  "QT Atom Parse" (c) 2003, Chris Adamson, invalidname@mac.com
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.mac.invalidname.qtatomparse;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.math.BigInteger;

public class ParsedelstAtom extends ParsedLeafAtom {

    int version;
    Edit[] edits;

    public ParsedelstAtom (long size,
                           String type,
                           RandomAccessFile raf) throws IOException {
        super (size, type, raf);
    }

    public void init (RandomAccessFile raf) throws IOException {
        BigInteger longConverter = null;
        // elst contains a 1-byte version, 3 bytes of (unused) flags,
        // a byte of table count, then 12-byte table entries
        // (4 each of trackDuration, mediaTime, and mediaRate)
        byte[] buffy = new byte[4];
        raf.read (buffy, 0, 1);
        version = buffy [0];
        // flags are defined as 3 bytes of 0, I just read & forget
        raf.read (buffy, 0, 3);
        // how many table entries are there?
        raf.read (buffy, 0, 4);
        longConverter = new BigInteger (buffy);
        int tableCount = longConverter.intValue();
        edits = new Edit[tableCount];
        for (int i=0; i<tableCount; i++) {
            // TODO: also bounds-check that we don't go past size
            // track duration
            raf.read (buffy, 0, 4);
            longConverter = new BigInteger (buffy);
            long trackDuration = longConverter.longValue();
            // media time
            raf.read (buffy, 0, 4);
            longConverter = new BigInteger (buffy);
            long mediaTime = longConverter.longValue();
            // media rate
            // TODO: wrong, these 4 bytes are a fixed-point
            // float, 16-bytes left of decimal, 16 right
            // I don't get how apple does this, so I'm just reading
            // the integer part
            raf.read (buffy, 0, 2);
            longConverter = new BigInteger (buffy);
            float mediaRate = longConverter.floatValue();
            raf.read (buffy, 0, 2);
            // make an Edit object
            Edit edit = new Edit (trackDuration, mediaTime, mediaRate);
            edits[i] = edit;
        }
    }

    public int getVersion() {
        return version;
    }

    public Edit[] getEdits() {
        return edits;
    }

    public String toString() {
        return super.toString() +  "[" +
            edits.length + 
            ((edits.length != 1) ? " edits]" : " edit]");
    }


    public class Edit extends Object {
        long trackDuration;
        long mediaTime;
        float mediaRate;
        public Edit (long d, long t, float r) {
            trackDuration = d;
            mediaTime = t;
            mediaRate = r;
        }
        public long getTrackDuration() { return trackDuration; }
        public long getMediaTime() { return mediaTime; }
        public float getMediaRate() { return mediaRate; }
    }

}
