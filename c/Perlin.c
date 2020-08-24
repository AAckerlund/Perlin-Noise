#include <stdio.h>
#include <stdlib.h>

float perlinNoise[10];


void PerlinNoise1D(int octaves, float noise[], int scaleBias)
{
	int i;
	for(i = 0; i < sizeof(noise)/sizeof(noise[0]); i++)
	{
		float noiseFloat = 0;
		float scale = 1.0f;
		float scaleAccumulate = 0;

		int j;
		for(j = 0; j < octaves; j++)
		{
			int pitch = (sizeof(noise)/sizeof(noise[0])) >> j;
			if(pitch == 0)
				return;
			int sample1 = (i/pitch) * pitch;
			int sample2 = (sample1 + pitch) % (sizeof(noise)/sizeof(noise[0]));

			float blend = (float)(i - sample1) / (float)pitch;
			float sampleBlend = (1.0f - blend) * noise[sample1] + blend * noise[sample2];

			noiseFloat += sampleBlend * scale;
			scaleAccumulate += scale;
			scale = scale / scaleBias;
		}
		perlinNoise[i] = noiseFloat / scaleAccumulate;
	}
}

int main()
{
	int i;
	time_t t;
	srand((unsigned) time(&t));
	float noise[10];
	for(i = 0; i < sizeof(perlinNoise)/sizeof(perlinNoise[0]); i++)
	{
		perlinNoise[i] = (float)rand() / (float)(RAND_MAX/1);//generate a random value between 0 and 1
		noise[i] = (float)rand() / (float)(RAND_MAX/1);
	}
	
	PerlinNoise1D(4, noise, 1);
       for(i = 0; i < sizeof(perlinNoise)/sizeof(perlinNoise[0]); i++)
        {
		printf("%f\t%f\n", noise[i], perlinNoise[i]);
	}	
}
