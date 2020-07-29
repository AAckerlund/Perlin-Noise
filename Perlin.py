import turtle
import random


def drawNoise(noise, width):
    t = turtle.Turtle()
    turtle.tracer(0)

    t.penup()
    t.begin_fill()
    t.goto(0 - width, 0)
    t.pendown()

    for i in range(len(noise)):
        t.goto(i - width, noise[i] * 255)

    t.goto(len(noise) - width - 1, 0)
    t.goto(0 - width, 0)
    t.end_fill()

    turtle.mainloop()


def makeFrame():
    frame = turtle.Screen()
    frame.screensize(300, 300)
    # print(frame.canvheight, frame.canvwidth)
    return frame


def genNoise(frame):
    print("Noise Generation")
    noise = []
    for i in range(frame.canvwidth):  # * frame.canvheight):
        noise.append(random.random())
    return noise


def PerlinNoise1D(octaves, noise, scaleBias):
    print("1D Perlin Noise")
    perlinNoise = []
    for i in range(len(noise)):
        noiseFloat = 0.0
        scale = 1.0
        scaleAccumulate = 0.0

        for j in range(octaves):
            pitch = len(noise) >> j
            if pitch == 0:
                return
            sample1 = int(i / pitch) * int(pitch)
            sample2 = int(sample1 + pitch) % len(noise)

            blend = float(i - sample1) / float(pitch)
            sampleBlend = (1.0 - blend) * noise[sample1] + blend * noise[sample2]

            noiseFloat += sampleBlend * scale
            scaleAccumulate += scale
            scale = scale / scaleBias
        perlinNoise.append(noiseFloat / scaleAccumulate)
    return perlinNoise


def main():
    frame = makeFrame()
    noise = genNoise(frame)
    perlinNoise = PerlinNoise1D(4, noise, 2.0)
    drawNoise(perlinNoise, frame.canvwidth)


main()
