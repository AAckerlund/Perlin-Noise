package java;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class Driver extends JPanel implements ActionListener
{
    JFrame mainFrame, seedFrame;
    JTextField seedInput, octaveInput, scaleInput;
    JLabel seedLabel, octaveLabel, scaleLabel;
    JButton redraw, toggleCutoffs;
    int seed, octaveCount, width, height;
    Color[] perlin;
    float[] noise, perlinNoise;
    float scaleBias;
    Random r;
    boolean cutoffs;

    public Driver()
    {
        seed = (int) (Math.random()*Integer.MAX_VALUE);//start with a random seed
        octaveCount = 4;
        scaleBias = 2;
        cutoffs = true;

        initFrameComponents();

        int arraySize = mainFrame.getWidth() * mainFrame.getHeight();

        perlin = new Color[arraySize];
        noise = new float[arraySize];
        perlinNoise = new float[arraySize];

        width = mainFrame.getWidth();
        height = mainFrame.getHeight();
        r = new Random();
        r.setSeed(seed);

        for(int i = 0; i < perlin.length; i++)
        {
            perlin[i] = new Color(0,0,0);
            noise[i] = r.nextFloat() % 1;
        }

        seedFrame.setVisible(true);
        mainFrame.setVisible(true);
    }

    /**
     * Initializes most of the frame components
     */
    public void initFrameComponents()
    {
        mainFrame = new JFrame("Perlin Noise");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.add(this);
        mainFrame.setSize(400, 400);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);

        seedFrame = new JFrame();
        seedFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        seedFrame.setLayout(new GridLayout(4,2));
        seedFrame.setResizable(false);

        seedLabel = new JLabel("Seed:");
        seedInput = new JTextField();
        seedInput.setText(String.valueOf(seed));

        octaveLabel = new JLabel("Octave:");
        octaveInput = new JTextField();
        octaveInput.setText(String.valueOf(octaveCount));

        scaleLabel = new JLabel("Scale Bias:");
        scaleInput = new JTextField();
        scaleInput.setText(String.valueOf(scaleBias));

        redraw = new JButton("Redraw");
        redraw.addActionListener(this);
        toggleCutoffs = new JButton("Toggle Cutoffs");
        toggleCutoffs.addActionListener(this);

        seedFrame.add(seedLabel);       //element (0, 0)
        seedFrame.add(seedInput);       //element (1, 0)
        seedFrame.add(octaveLabel);     //element (0, 1)
        seedFrame.add(octaveInput);     //element (1, 1)
        seedFrame.add(scaleLabel);      //element (2, 0)
        seedFrame.add(scaleInput);      //element (2, 1)
        seedFrame.add(redraw);          //element (3, 0)
        seedFrame.add(toggleCutoffs);   //element (3, 1)
        seedFrame.pack();
    }

    /**
     * The built-in paint function, used to visualize the perlin noise
     * @param g the graphics tool used for drawing to the screen
     */
    public void paintComponent(Graphics g)
    {
        generateNoise(octaveCount);
        for(int i = 0; i < perlinNoise.length; i++)
        {
            int tmpCol = (int)(perlinNoise[i] * 255);
            perlin[i] = new Color(tmpCol, tmpCol, tmpCol);
        }

        super.paintComponent(g);
        for(int x = 0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {
                int color;
                if(cutoffs)
                {
                    color = switchColor(perlin[y * width + x].getBlue());
                }
                else
                {
                    color = perlin[y * width + x].getBlue();
                }
                g.setColor(new Color(color, color, color));
                g.drawLine(x, y, x, y);
            }
        }
    }

    /**
     * generates 2D perlin noise
     * @param octaves The level of detail to render
     */
    public void generateNoise(int octaves)
    {
        //Code was gotten while watching this (https://www.youtube.com/watch?v=6-0UaeJBumA) video
        for(int x = 0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {

                float noiseFloat = 0;
                float scaleAccumulate = 0;
                float scale = 1.0f;

                for(int j = 0; j < octaves; j++)
                {
                    int pitch = width >> j;//divides array size by 2 j number of times
                    if(pitch == 0)
                        return;
                    int sample1x = (x / pitch) * pitch;
                    int sample1y = (y / pitch) * pitch;

                    int sample2x = (sample1x + pitch) % width;
                    int sample2y = (sample1y + pitch) % width;

                    float blendx = (float) (x - sample1x) / (float) pitch;
                    float blendy = (float) (y - sample1y) / (float) pitch;

                    int noisePoint1 = sample1y * width + sample1x;
                    int noisePoint2 = sample1y * width + sample2x;
                    float sampleBlend1 = (1.0f - blendx) * noise[noisePoint1] + blendx * noise[noisePoint2];

                    noisePoint1 = sample2y * width + sample1x;
                    noisePoint2 = sample2y * width + sample2x;
                    float sampleBlend2 = (1.0f - blendx) * noise[noisePoint1] + blendx * noise[noisePoint2];

                    noiseFloat += (blendy * (sampleBlend2 - sampleBlend1) + sampleBlend1) * scale;
                    scaleAccumulate += scale;
                    scale = scale / scaleBias;
                }
                perlinNoise[y * mainFrame.getWidth() + x] = noiseFloat / scaleAccumulate;
            }
        }
    }

    /**
     * the built-in actionPerformed, used to detect when the redraw button is pressed
     * @param e the action being performed
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(e.getSource() == redraw)
        {
            seed = checkSeed();
            octaveCount = checkOctaveCount();
            scaleBias = checkScaleBias();

            r.setSeed(seed);

            for(int i = 0; i < perlin.length; i++)
            {
                perlin[i] = new Color(0,0,0);
                noise[i] = r.nextFloat() % 1;
            }

            //draws to the screen
            repaint();
            invalidate();
            validate();
        }
        else if(e.getSource() == toggleCutoffs)
        {
            cutoffs = !cutoffs;
            //draws to the screen
            repaint();
            invalidate();
            validate();
        }
    }

    /**
     * checks that the entered seed value is valid
     * @return the new valid seed value, or the old seed value if the new is not valid
     */
    public int checkOctaveCount()
    {
        int newOctaveCount;
        try
        {
            newOctaveCount = Integer.parseInt(octaveInput.getText());
            return newOctaveCount;
        }
        catch(NumberFormatException ex)
        {
            JOptionPane.showMessageDialog(null, "The octave count must be an integer.", "Bad Octave Count Value", JOptionPane.ERROR_MESSAGE);
            octaveInput.setText(String.valueOf(octaveCount));
        }
        return octaveCount;
    }

    /**
     * checks that the entered scaleBias value is valid
     * @return the new valid seed value, or the old seed value if the new is not valid
     */
    public float checkScaleBias()
    {
        float newScaleBias;
        try
        {
            newScaleBias = Float.parseFloat(scaleInput.getText());
            return newScaleBias;
        }
        catch(NumberFormatException ex)
        {
            JOptionPane.showMessageDialog(null, "The scale bias must be a float.", "Bad Scale Bias Value", JOptionPane.ERROR_MESSAGE);
            scaleInput.setText(String.valueOf(scaleBias));
        }
        return scaleBias;
    }

    /**
     * checks that the entered seed value is valid
     * @return the new valid seed value, or the old seed value if the new is not valid
     */
    public int checkSeed()
    {
        int newSeed;
        try
        {
            newSeed = Integer.parseInt(seedInput.getText());
            return newSeed;
        }
        catch(NumberFormatException ex)
        {
            JOptionPane.showMessageDialog(null, "The seed must be an integer.", "Bad Seed Value", JOptionPane.ERROR_MESSAGE);
            seedInput.setText(String.valueOf(seed));
        }
        return seed;
    }

    /**
     * makes the generated perlin noise easier to view
     * @param color the value determined by the perlin noise generator
     * @return a new color value based on the value of color
     */
    public int switchColor(int color)
    {
        if(color < 25) return 25;

        if(color < 50) return 50;

        if(color < 75) return 75;

        if(color < 100) return 100;

        if(color < 125) return 125;

        if(color < 150) return 150;

        if(color < 175) return 175;

        if(color < 200) return 200;

        if(color < 225) return 225;

        if(color < 250) return 250;

        else return 255;
    }

    /**
     * generates 1D perlin noise
     * @param octaves The level of detail to render
     */
    public void generate1DNoise(int octaves)
    {
        for(int i = 0; i < noise.length; i++)
        {
            float noiseFloat = 0;
            float scale = 1.0f;
            float scaleAccumulate = 0;

            for(int j = 0; j < octaves; j++)
            {
                int pitch = noise.length >> j;//divides array size by 2 j number of times
                if(pitch == 0)
                    return;
                int sample1 = (i / pitch) * pitch;
                int sample2 = (sample1 + pitch) % noise.length;

                float blend = (float)(i - sample1)/ (float)pitch;
                float sampleBlend = (1.0f - blend) * noise[sample1] + blend * noise[sample2];

                noiseFloat += sampleBlend * scale;
                scaleAccumulate += scale;
                scale = scale / scaleBias;
            }
            perlinNoise[i] = noiseFloat / scaleAccumulate;
        }
    }

    public static void main(String[] args)
    {
        new Driver();
    }
}