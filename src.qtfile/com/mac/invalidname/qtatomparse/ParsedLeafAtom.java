/*
  "QT Atom Parse" (c) 2003, Chris Adamson, invalidname@mac.com
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.mac.invalidname.qtatomparse;

import java.io.RandomAccessFile;
import java.io.IOException;

public class ParsedLeafAtom extends ParsedAtom {

    /** Constructor should only be called by AtomFactory and 
        assumes that the RandomAccessFile is on the first
        byte after the type (or extended size, if present)
        so that the contents of the leaf atom can be read.
        <p>
        All subclasses must have a constructor with this signature,
        so that the factory can instantiate them.
     */
    public ParsedLeafAtom (long size,
                           String type,
                           RandomAccessFile raf) throws IOException {
        super (size, type);
        init(raf);
    }

    /** Called by the constructor, the init method assumes
        that the RandomAccessFile is on the first byte after
        the type (or extended size, if present) and reads
        in the contents of the atom.  That means that there are
        size-8 bytes left to be read, unless size > 0xffffffff (unsigned)
        in which case there was an extended size and there are thus
        size-16 bytes left to be read
        <p>
        The default does nothing.  Atom-specific subclasses
        can override this method to handle the specific
        structures of their atoms.
     */
    public void init (RandomAccessFile raf) throws IOException {
        // does nothing
    }

    /** By default, leaf atoms return "type (size bytes)" */
    public String toString () {
        return  type + " (" + size + " bytes) "; 
    }

}
