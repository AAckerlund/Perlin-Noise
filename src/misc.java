import java.awt.*;
import java.util.Random;

/**
 * This class contains miscellaneous functions I made while experimenting with my own noise generating algorithms
 */
public class misc
{
    Color[][] graphic;
    public misc(int x, int y)
    {
        graphic = new Color[x][y];
    }
    public void rain(int x, int y)
    {
        Random r = new Random();
        int color = r.nextInt() % 255;

        int upperBound = 256;
        try
        {
            upperBound = Math.max(graphic[x - 1][y].getBlue(), graphic[x][y - 1].getBlue());
        }catch(ArrayIndexOutOfBoundsException ignored){}

        int lowerBound = 0;
        try
        {
            lowerBound = Math.min(graphic[x - 1][y].getBlue(), graphic[x][y - 1].getBlue());
        }catch(ArrayIndexOutOfBoundsException ignored){}

        color = checkBounds(color, upperBound, lowerBound);

        graphic[x][y] = new Color(color, color, color);
    }
    public void norm2()
    {
        Random rand = new Random();
        int x, y;
        for(int num = 0; num < (graphic.length * graphic[0].length)/10; num++)
        {
            x = rand.nextInt(graphic.length);
            y = rand.nextInt(graphic[0].length);

            graphic[x][y] = normSpot(x, y);
        }
    }

    public void norm()
    {
        for(int x = 0; x < graphic.length; x++)
        {
            for(int y = 0; y < graphic[x].length; y++)
            {
                graphic[x][y] = normSpot(x, y);
            }
        }
    }

    public Color normSpot(int x, int y)
    {
        int edges = 1;
        int colorValue = graphic[x][y].getBlue();//doesn't matter what value we get between RGB cause they will all be the same
        Color newColor;
        try
        {
            colorValue += graphic[x+1][y].getBlue();
            edges++;
        }
        catch(ArrayIndexOutOfBoundsException ignored){}
        try
        {
            colorValue += graphic[x-1][y].getBlue();
            edges++;
        }
        catch(ArrayIndexOutOfBoundsException ignored){}
        try
        {
            colorValue += graphic[x][y+1].getBlue();
            edges++;
        }
        catch(ArrayIndexOutOfBoundsException ignored){}
        try
        {
            colorValue += graphic[x][y-1].getBlue();
            edges++;
        }
        catch(ArrayIndexOutOfBoundsException ignored){}

        colorValue = colorValue/edges;
        newColor = new Color(colorValue, colorValue, colorValue);
        return newColor;
    }

    /**
     * forces the color to be withing the set bounds
     * @param color the initial color value
     * @param upper the max value it can be
     * @param lower the min value it can be
     * @return the new value for the color that is bounded between upper and lower
     */
    public int checkBounds(int color, int upper, int lower)
    {
        int diff = upper - lower;
        if(diff == 0)
            return color;
        while(color > upper)
        {
            color -= diff;
        }
        while(color < lower)
        {
            color += diff;
        }
        return color;
    }
}