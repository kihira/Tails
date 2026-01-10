package uk.kihira.tails.client.outfit;

import uk.kihira.tails.client.Colour;

import java.util.HexFormat;

// TODO store as float between 0-1 or as int between 0-255?
// Or just use Colour class/store as int?
public record Tint(float red, float green, float blue)
{
    public static Tint fromARGB(int argb)
    {
        var r = ((argb >> 16) & 0xFF) / 255f;
        var g = ((argb >> 8) & 0xFF) / 255f;
        var b = (argb & 0xFF) / 255f;
        return new Tint(r, g, b);
    }

    public int toARGB()
    {
        int col = Colour.BLACK;
        col |= (int)(red * 255f) << 16;
        col |= (int)(green * 255f) << 8;
        col |= (int)(blue * 255f);
        return col;
    }

    public String toHexString()
    {
        return HexFormat.of().toHexDigits(toARGB(), 6);
    }

    public int redInt()
    {
        return (int)(red * 255f);
    }

    public int greenInt()
    {
        return (int)(green * 255f);
    }

    public int blueInt()
    {
        return (int)(blue * 255f);
    }
}
