import random
from PIL import Image, ImageDraw


def drawNoise(noise, width, height, image):
    draw = ImageDraw.Draw(image)

    for x in range(width):
        for y in range(height):
            colorValue = int(noise[y * width + x] * 255)
            point = (x, y)
            draw.point(point, colorValue)
    image.show()


def makeFrame(width, height):
    image = Image.new('L', (width, height))# creates an image of size widthXheight that can display any rgb values
    return image


def genNoise(frame, seed):
    noise = []
    random.seed(seed)
    for i in range(frame.width * frame.height):
        noise.append(random.random())
    return noise


def PerlinNoise2D(octaves, noise, scaleBias, width, height):
    perlinNoise = []
    for x in range(width):
        for y in range(height):
            noiseFloat = 0.0
            scale = 1.0
            scaleAccumulate = 0.0

            for j in range(octaves):
                pitch = width >> j
                if pitch == 0:
                    return
                sample1x = int(x / pitch) * int(pitch)
                sample1y = int(y / pitch) * int(pitch)

                sample2x = int((sample1x + pitch) % width)
                sample2y = int((sample1y + pitch) % width)

                blendx = float(x - sample1x) / float(pitch)
                blendy = float(y - sample1y) / float(pitch)

                noisePoint1 = sample1y * width + sample1x
                noisePoint2 = sample1y * width + sample2x
                sampleBlend1 = (1.0 - blendx) * noise[noisePoint1] + blendx * noise[noisePoint2]

                noisePoint1 = sample2y * width + sample1x
                noisePoint2 = sample2y * width + sample2x
                sampleBlend2 = (1.0 - blendx) * noise[noisePoint1] + blendx * noise[noisePoint2]

                noiseFloat += (blendy * (sampleBlend2 - sampleBlend1) + sampleBlend1) * scale
                scaleAccumulate += scale
                scale = scale / scaleBias

            perlinNoise.append(noiseFloat / scaleAccumulate)

    return perlinNoise


def PerlinNoise1D(octaves, noise, scaleBias):
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
    frame = makeFrame(900, 900)
    seed = random.randint(0, 2147483647)

    print("Generating Seed Noise with seed", seed)
    noise = genNoise(frame, seed)

    print("Generating Perlin Noise")
    perlinNoise = PerlinNoise2D(4, noise, 2.0, frame.width, frame.height)

    # perlinNoise = PerlinNoise1D(4, noise, 2.0)

    print("Drawing Perlin Noise")
    drawNoise(perlinNoise, frame.width, frame.height, frame)


main()
