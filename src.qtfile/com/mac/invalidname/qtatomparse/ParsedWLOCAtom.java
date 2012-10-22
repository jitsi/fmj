/*
  "QT Atom Parse" (c) 2003, Chris Adamson, invalidname@mac.com
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.mac.invalidname.qtatomparse;

import java.io.RandomAccessFile;
import java.io.IOException;

public class ParsedWLOCAtom extends ParsedLeafAtom {

    int x;
    int y;

    public ParsedWLOCAtom (long size,
                           String type,
                           RandomAccessFile raf) throws IOException {
        super (size, type, raf);
    }

    public void init (RandomAccessFile raf) throws IOException {
        // WLOC contains 16-bit x,y values
        byte[] value = new byte[4];
        raf.read (value, 0, value.length);
        x = (value[0] << 8) | value[1];
        y = (value[2] << 8) | value[3]; 
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String toString() {
        return super.toString() +
            " (x,y) == (" + 
            x + "," + y + ")";

    }


}
