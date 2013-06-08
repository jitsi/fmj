package net.sf.fmj.registry;

/**
 * Factory singleton to create RegistryIO.
 *
 * @author Ken Larson
 *
 */
class RegistryIOFactory
{
    public static final int XML = 0;
    public static final int PROPERTIES = 1;

    public static final RegistryIO createRegistryIO(int type,
            RegistryContents contents)
    {
        switch (type)
        {
        case XML:
            return new XMLRegistryIO(contents);
        case PROPERTIES:
            return new PropertiesRegistryIO(contents);
        default:
            throw new IllegalArgumentException();
        }
    }
}
