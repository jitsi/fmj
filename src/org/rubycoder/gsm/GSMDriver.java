package org.rubycoder.gsm;

//    $Id: GSMDriver.java,v 1.6 2012/04/03 07:31:54 lyub0m1r Exp $

//    This file is part of the GSM 6.10 audio decoder library for Java
//    Copyright (C) 1998 Steven Pickles (pix@test.at)

//    This library is free software; you can redistribute it and/or
//    modify it under the terms of the GNU Library General Public
//    License as published by the Free Software Foundation; either
//    version 2 of the License, or (at your option) any later version.

//    This library is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//    Library General Public License for more details.

//    You should have received a copy of the GNU Library General Public
//    License along with this library; if not, write to the Free
//    Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

//  This software is a port of the GSM Library provided by
//  Jutta Degener (jutta@cs.tu-berlin.de) and
//  Carsten Bormann (cabo@cs.tu-berlin.de),
//  Technische Universitaet Berlin

import java.io.*;

public class GSMDriver
{
    private static void decode(String input, String output)
    {
        GSMDecoder myDecoder = new GSMDecoder();

        byte inputArray[] = new byte[33];
        int outputArray[] = new int[160];

        FileInputStream fis = null;
        FileOutputStream fos = null;

        byte outBytes[] = new byte[320];

        if (input.equalsIgnoreCase("") || output.equalsIgnoreCase(""))
        {
            System.err.print("Usage: GSMDriver inputfile outputfile\n");
            System.exit(1);
        }

        try
        {
            fis = new FileInputStream(input);
            fos = new FileOutputStream(output);
        } catch (Exception e)
        {
            System.err.println("file not found, or can't open.\n");
            System.exit(2);
        }

        while (true)
        {
            try
            {
                if (fis.read(inputArray) <= 0)
                {
                    break;
                }
            } catch (IOException e)
            {
                System.err.println("error reading inputArray");
                break;
            }

            try
            {
                myDecoder.decode(inputArray, outputArray);

                for (int i = 0; i < 160; i++)
                {
                    int index = i << 1;
                    outBytes[index] = (byte) (outputArray[i] & 0x00ff);
                    outBytes[++index] = (byte) ((outputArray[i] & 0xff00) >> 8);
                }
                System.out.println("-");
                try
                {
                    fos.write(outBytes);
                } catch (IOException e)
                {
                    System.err.println("error writing outputArray");
                    break;
                }

            } catch (InvalidGSMFrameException e)
            {
                System.err.println("bad frame");
                break;
            }

        }

        try
        {
            fis.close();
            fos.close();
        } catch (IOException e)
        {
            System.err.println("error closing files.");
        }
    }

    private static void encode(String input, String output)
    {
        GSMEncoder myEncoder = new GSMEncoder();

        byte inputArray[] = new byte[320];
        byte outputArray[] = new byte[33];

        FileInputStream fis = null;
        FileOutputStream fos = null;

        int[] inInts = new int[160];

        try
        {
            fis = new FileInputStream(input);
            fos = new FileOutputStream(output);
        } catch (Exception e)
        {
            System.err.println("file not found, or can't open.\n");
            System.exit(2);
        }

        while (true)
        {
            try
            {
                if (fis.read(inputArray) <= 0)
                {
                    break;
                }
            } catch (java.io.IOException e)
            {
                System.err.println("error reading inputArray");
                break;
            }

            for (int i = 0; i < 160; i++)
            {
                int index = i << 1;
                inInts[i] = inputArray[index + 1];
                inInts[i] <<= 8;
                inInts[i] |= inputArray[index++] & 0xFF;

            }
            myEncoder.encode(outputArray, inInts);
            try
            {
                fos.write(outputArray);
            } catch (IOException e)
            {
                System.err.println("error writing outputArray");
                break;
            }

        }

        try
        {
            fis.close();
            fos.close();
        } catch (IOException e)
        {
            System.err.println("error closing files.");
        }

    }

    public static void main(String argv[])
    {
        if (argv.length != 3)
        {
            System.err
                    .print("Usage: GSMDriver d inputfile outputfile   -  decode from gsm file");
            System.err
                    .print("       GSMDriver e inputfile outputfile   -  encode into gsm file");
            System.exit(2);
        }
        if (argv[0].equalsIgnoreCase("d"))
            decode(argv[1], argv[2]);
        else if (argv[0].equalsIgnoreCase("e"))
            encode(argv[1], argv[2]);

    }

}
