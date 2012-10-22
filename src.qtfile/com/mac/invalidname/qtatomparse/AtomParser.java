/*
  "QT Atom Parse" (c) 2003, Chris Adamson, invalidname@mac.com
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.mac.invalidname.qtatomparse;

import java.io.*;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.ArrayList;
import javax.swing.JFileChooser;

public class AtomParser extends Object {
    /** QuickTime container atoms - these atoms contain only
        other atoms, so if one is encoutered, we recur to pick
        up the children
     */
    public static final String[] ATOM_CONTAINER_TYPE_STRINGS = {
        "moov", "trak", "udta", "tref", "imap",
        "mdia", "minf", "stbl", "edts", "mdra", 
        "rmra", "imag", "vnrp", "dinf"
    };
    /** A Set for fast lookup of possible atom-containers,
        includes all the members of ATOM_CONTAINER_TYPE_STRINGS
     */
    public static final HashSet ATOM_CONTAINER_TYPES =
        new HashSet();
    static {
        for (int i=0; i<ATOM_CONTAINER_TYPE_STRINGS.length; i++) {
            ATOM_CONTAINER_TYPES.add (ATOM_CONTAINER_TYPE_STRINGS[i]);
        } // for
    } 

    static byte[] atomSizeBuf = new byte[4];
    static byte[] atomTypeBuf = new byte[4];
    static byte[] extendedAtomSizeBuf = new byte[8];


    /** return top level atoms (and thus the entire 
        structure) parsed from the given file.
     */
    public static ParsedAtom[] parseAtoms (File f) 
        throws IOException {
        RandomAccessFile raf = new RandomAccessFile (f, "r");
        ParsedAtom[] atoms = parseAtoms (raf, 0, raf.length());
        raf.close();
        return atoms;
    }

    protected static ParsedAtom[] parseAtoms (RandomAccessFile raf,
                                              long firstOff,
                                              long stopAt) 
        throws IOException {

        // off is the atom's offset into the file (gets reset
        // for next sibling at bottom of loop, after preceding 
        // sibling's size is read)
        long off = firstOff;
        ArrayList parsedAtomList = new ArrayList();
        //        while (raf.getFilePointer() <= stopAt) {
        while (off < stopAt) {
            raf.seek (off);

            // 1. first 32 bits are atom size
            // use BigInteger to convert bytes to long
            // (instead of signed int)
            int bytesRead = raf.read (atomSizeBuf, 0, atomSizeBuf.length);
            if (bytesRead < atomSizeBuf.length)
                throw new IOException ("couldn't read atom length");
            BigInteger atomSizeBI = new BigInteger (atomSizeBuf);
            long atomSize = atomSizeBI.longValue();

            // this is kind of a hack to handle the udta problem
            // (see below) when the parent didn't have children,
            // meaning we've read 4 bytes of 0 and the atom is
            // already over
            if (raf.getFilePointer() == stopAt)
                break;

            // 2. next, the atom type
            bytesRead = raf.read (atomTypeBuf, 0, atomTypeBuf.length);
            if (bytesRead != atomTypeBuf.length)
            throw new IOException ("Couldn't read atom type");
            String atomType = new String (atomTypeBuf);

            // 3. if atomSize was 1, then there are 64 bits of extended size
            if (atomSize == 1) {
                bytesRead = raf.read (extendedAtomSizeBuf, 0,
                                      extendedAtomSizeBuf.length);
                if (bytesRead != extendedAtomSizeBuf.length)
                    throw new IOException ("Couldn't read extended atom size");
                BigInteger extendedSizeBI =
                    new BigInteger (extendedAtomSizeBuf);
                atomSize = extendedSizeBI.longValue();
            }

            // if this atom size is negative, or extends past end
            // of file, it's extremely suspicious (ie, we're not
            // really in a quicktime file)
            if ((atomSize < 0)  ||
                ((off + atomSize) > raf.length()))
                throw new IOException ("atom has invalid size: " +
                                       atomSize);
            
            // 4. if a container atom, then parse the children
            ParsedAtom parsedAtom = null;
            if (ATOM_CONTAINER_TYPES.contains (atomType)) {
                // children run from current point to the end of the atom
                ParsedAtom [] children =
                    parseAtoms (raf,
                                raf.getFilePointer(),
                                off + atomSize);
                parsedAtom =
                    new ParsedContainerAtom (atomSize,
                                             atomType,
                                             children);
            } else {
                parsedAtom =
                    AtomFactory.getInstance().createAtomFor (atomSize,
                                                             atomType,
                                                             raf);
            }

            // add atom to the list
            parsedAtomList.add (parsedAtom);
            
            // now set offset to next atom (or end-of-file
            // in special case (atomSize = 0 means atom goes
            // to EOF)
            if (atomSize == 0)
                off = raf.length();
            else 
                off += atomSize;

            // if a 'udta' container atom, then jump ahead 4 
            // to work around Apple's QT 1.0 workaround
            // (http://developer.apple.com/technotes/qt/qt_03.html )
            if (atomType.equals("udta"))
                off += 4;

        } // while not at stopAt

        // convert the array list into an array
        ParsedAtom[] atomArray =
            new ParsedAtom [parsedAtomList.size()];
        parsedAtomList.toArray (atomArray);
        return atomArray;
    } // parseAtoms


    /** debug - parse the atom chosen by user
     */
    public static void main (String[] args) {
        JFileChooser chooser = new JFileChooser ();
        int response = chooser.showOpenDialog(null);
        if (response == JFileChooser.CANCEL_OPTION)
            return;
        File f = chooser.getSelectedFile();
        try {
            ParsedAtom[] atomTree = parseAtoms (f);
            printAtomTree (atomTree, "");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            // awt will be hanging around because of the filechooser
            // so let's go away now
            System.exit(0);
        }
    } // main


    /** helper for main, recursively prints atoms and their
        children, adding further indent for each generation
     */
    protected static final void printAtomTree (ParsedAtom[] atomTree,
                                            String indent) {
        for (int i=0; i<atomTree.length; i++) {
            System.out.print (indent);
            ParsedAtom atom = atomTree[i];
            System.out.println (atom.toString());
            if (atom instanceof ParsedContainerAtom) {
                ParsedAtom[] children =
                    ((ParsedContainerAtom) atom).getChildren();
                printAtomTree (children, indent + "  ");
            }
        }
    }

}
