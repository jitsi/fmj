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

public class ParsedhdlrAtom extends ParsedLeafAtom {

    int version;
    String componentType;
    String componentSubType;
    String componentManufacturer;
    String componentName;

    public ParsedhdlrAtom (long size,
                           String type,
                           RandomAccessFile raf) throws IOException {
        super (size, type, raf);
    }

    public void init (RandomAccessFile raf) throws IOException {
        BigInteger longConverter = null;
        // hdlr contains a 1-byte version, 3 bytes of (unused) flags,
        // 4-char component type, 4-char component subtype,
        // 4-byte fields for comp mfgr, comp flags, and flag mask
        // then a pascal string for component name

        byte[] buffy = new byte[4];
        raf.read (buffy, 0, 1);
        version = buffy [0];
        // flags are defined as 3 bytes of 0, I just read & forget
        raf.read (buffy, 0, 3);
        // component type and subtype (4-byte strings)
        raf.read (buffy, 0, 4);
        componentType = new String (buffy);
        raf.read (buffy, 0, 4);
        componentSubType = new String (buffy);
        // component mfgr (4 bytes, apple says "reserved- set to 0")
        raf.read (buffy, 0, 4);
        componentManufacturer = new String (buffy);
        // component flags & flag mask 
        // (4 bytes each, apple says "reserved- set to 0", skip for now)
        raf.read (buffy, 0, 4);
        raf.read (buffy, 0, 4);
        // length of pascal string
        raf.read (buffy, 0, 1);
        int compNameLen = buffy[0];
        /* undocumented hack:
           in .mp4 files (as opposed to .mov's), the component name
           seems to be a C-style (null-terminated) string rather
           than Pascal-style (length-byte then run of characters).
           However, the name is the last thing in this atom, so
           if the String size is wrong, assume we're in MPEG-4
           and just read to end of the atom.  In other words, the
           string length *must* always be atomSize - 33, since there
           are 33 bytes prior to the string, and it's the last thing
           in the atom.
        */
        if (compNameLen != (size - 33)) {
            // MPEG-4 case
            compNameLen = (int) size - 33;
            // back up one byte (since what we thought was
            // length was actually first char of string)
            raf.seek (raf.getFilePointer() - 1);
        }
        byte compNameBuf[] = new byte[compNameLen];
        raf.read (compNameBuf, 0, compNameLen);
        componentName = new String (compNameBuf);
    }

    public int getVersion() {
        return version;
    }

    public String getComponentType() {
        return componentType;
    }
    public String getComponentSubType() {
        return componentSubType;
    }
    public String getComponentManufacturer() {
        return componentManufacturer;
    }
    public String getComponentName() {
        return componentName;
    }

    public String toString() {
        return super.toString() +  "[" +
            componentType + "/" + componentSubType + 
            " - " + componentName + "]";
    }


}
