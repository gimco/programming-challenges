import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class TuentiChallenge9 {
	public static void main(String[] args) throws Throwable {
		BufferedImage img = ImageIO.read(new File("scrambled-photo-submit-ag45z6b4ce3s5.png"));
		float[][][] image = new float[img.getWidth()][img.getHeight()][3];
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				Color c = new Color(img.getRGB(x, y));
				float[] hsv = new float[3];
				Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), image[x][y]);
			}
		}

		BufferedImage salida = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);

		for (int i = 0; i < image.length - 1; i++) {
			if (i % 100 == 0)
				System.out.println("i");
			int minx = -1;
			double mindis = Double.MAX_VALUE;
			for (int x = i + 1; x < image.length; x++) {
				double dis = distance(image[i], image[x]);
				if (dis < mindis) {
					mindis = dis;
					minx = x;
				}
			}
			swap(image, img, i + 1, minx);
		}

		ImageIO.write(img, "png", new File("submit.png"));
	}

	private static void swap(float[][][] image, BufferedImage img, int a, int b) {
		if (a == b)
			return;

		float[][] tmp = image[a];
		image[a] = image[b];
		image[b] = tmp;

		for (int y = 0; y < img.getHeight(); y++) {
			int aa = img.getRGB(a, y);
			img.setRGB(a, y, img.getRGB(b, y));
			img.setRGB(b, y, aa);
		}
	}

	// https://stackoverflow.com/questions/34622466/euclidean-distance-between-2-vectors-implementation
	private static double distance(float[][] a, float[][] b) {
		double diff_square_sum = 0.0;
		// Seleccionar solo una franja de la imagen
		for (int i = 320; i < 800; i++) {
			diff_square_sum += diff(a[i], b[i]) * diff(a[i], b[i]);
		}
		return Math.sqrt(diff_square_sum);
	}

	// https://stackoverflow.com/questions/35113979/calculate-distance-between-colors-in-hsv-space
	private static double diff(float[] a, float[] b) {
		float h0 = a[0];
		float s0 = a[1];
		float v0 = a[2];

		float h1 = b[0];
		float s1 = b[1];
		float v1 = b[2];

		double dh = Math.min(Math.abs(h1 - h0), 360 - Math.abs(h1 - h0)) / 180.0;
		double ds = Math.abs(s1 - s0);
		double dv = Math.abs(v1 - v0) / 255.0;

		double distance = Math.sqrt(dh * dh + ds * ds + dv * dv);

		return distance;
	}
}