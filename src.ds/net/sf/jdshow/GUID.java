package net.sf.jdshow;

/**
 *
 * @author Ken Larson
 *
 */
public class GUID
{
    public int Data1;
    public short Data2;
    public short Data3;
    public byte[] Data4 = new byte[8];

    public GUID()
    {
        super();
    }

    public GUID(int data1, int data2, int data3, int b1, int b2, int b3,
            int b4, int b5, int b6, int b7, int b8)
    {
        super();

        Data1 = data1;
        Data2 = (short) data2;
        Data3 = (short) data3;
        Data4[0] = (byte) b1;
        Data4[1] = (byte) b2;
        Data4[2] = (byte) b3;
        Data4[3] = (byte) b4;
        Data4[4] = (byte) b5;
        Data4[5] = (byte) b6;
        Data4[6] = (byte) b7;
        Data4[7] = (byte) b8;

    }

    public GUID(int data1, short data2, short data3, byte b1, byte b2, byte b3,
            byte b4, byte b5, byte b6, byte b7, byte b8)
    {
        super();

        Data1 = data1;
        Data2 = data2;
        Data3 = data3;
        Data4[0] = b1;
        Data4[1] = b2;
        Data4[2] = b3;
        Data4[3] = b4;
        Data4[4] = b5;
        Data4[5] = b6;
        Data4[6] = b7;
        Data4[7] = b8;

    }

    public int getData1()
    {
        return Data1;
    }

    public short getData2()
    {
        return Data2;
    }

    public short getData3()
    {
        return Data3;
    }

    public byte[] getData4()
    {
        return Data4;
    }
}
