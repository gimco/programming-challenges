import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Tuenti7Challenge12 {
	
	public static final String COINS_DIR = "coins";
	public static int MAX_IMAGE_SIZE = 200 * 1024;

	public static void main(String[] args) throws Exception {
		// Cargar libreria
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// Cargar monedas
		List<Coin> coins = new ArrayList<>();
		for (int i : new int[] { 1, 2, 5, 10, 20, 50, 100, 200 }) {
			coins.add(new Coin(i, String.format("%s/coin%d.jpg", COINS_DIR, i)));
		}

		// Conectar al socket
		Socket socket = new Socket("52.49.91.111", 3456);
		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();
		
		int readBytes = 0;
		byte[] buffer = new byte[MAX_IMAGE_SIZE];

		while (socket.isConnected()) {

			// Leemos un bloque de datos del socket
			readBytes = 0;
			do {
				readBytes += in.read(buffer, readBytes, MAX_IMAGE_SIZE - readBytes);
				Thread.sleep(200);
			} while (in.available() != 0);
			System.out.println("Leido " + readBytes);
			
			// Guardar fichero de la imagen
			File tempFile = File.createTempFile("tuenti", ".jpg");
			FileOutputStream fos = new FileOutputStream(tempFile);
			fos.write(buffer, 0, readBytes);
			System.out.println(tempFile.getName());
			fos.close();

			// Reordenar columnas
			BufferedImage image = ImageIO.read(tempFile);
			for (int c = 40; c < image.getWidth(); c += 80) {
				int limit = Math.min(40, image.getWidth() - c);
				for (int y = 0; y < image.getHeight(); y++) {
					for (int x = 0; x < (limit / 2); x++) {
						int rgb1 = image.getRGB(c + x, y);
						int rgb2 = image.getRGB(c + (limit - x - 1), y);
						image.setRGB(c + x, y, rgb2);
						image.setRGB(c + (limit - x - 1), y, rgb1);
					}
				}
			}
			ImageIO.write(image, "jpg", new File("/tmp/a_rearranged.jpg"));

			// Detectar círculos
			Mat dst = Highgui.imread("/tmp/a_rearranged.jpg");
			Mat imageGrayScale = Highgui.imread("/tmp/a_rearranged.jpg", Highgui.CV_LOAD_IMAGE_GRAYSCALE);

			Mat circles = new Mat();
			int cannyThreshold = 1;
			int minRadius = 25;
			int maxRadius = 50;
			int accumulator = 40;

			Imgproc.HoughCircles(imageGrayScale, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 2 * minRadius, cannyThreshold,
					accumulator, minRadius, maxRadius);

			int money = 0;
			for (int i = 0; i < circles.cols(); i++) {
				double vCircle[] = circles.get(0, i);

				if (vCircle == null)
					break;

				int x = (int) Math.round(vCircle[0]);
				int y = (int) Math.round(vCircle[1]);
				int radius = (int) Math.round(vCircle[2]);

				// Point center = new Point(vCircle[0], vCircle[1]);
				// Core.circle(dst, center, radius, new Scalar(255, 0, 0), 1);

				// De cada circulo detectado cortamos el rectángulo de 60x60
				int rx = Math.max(0, x - 30);
				int ry = Math.max(0, y - 30);
				int sw = Math.min(60, dst.width() - rx);
				int sh = Math.min(60, dst.height() - ry);
				
				Mat m = new Mat(dst, new Rect(rx, ry, sw, sh));
				Highgui.imwrite("/tmp/c" + i + ".jpg", m);

				// Buscar la moneda que mas se le parece. Los patrones a buscar son de 40x40
				int value = -1;
				double similarity = 0;
				for (Coin coin : coins) {
					Mat templ = coin.image;
					int result_cols =  m.cols() - templ.cols() + 1;
					int result_rows = m.rows() - templ.rows() + 1;
					
					Mat result = new Mat(result_cols, result_rows, m.type());
					Imgproc.matchTemplate(m, templ, result, Imgproc.TM_CCOEFF_NORMED);
					MinMaxLocResult minMaxLoc = Core.minMaxLoc(result);
					double s = minMaxLoc.maxVal;
					if (s > similarity) {
						similarity = s;
						value = coin.value;
					}
				}
				System.out.println(String.format("%d: (%d,%d) -> %d  - %f %%", i, x, y, value, similarity));
				money += value;
			}
			// Highgui.imwrite("/tmp/detected-circles.jpg", dst);
			
			// Enviar el dinero contado por el socket
			System.out.println("Total: " + money);
			out.write((money + "\n").getBytes());
			
			// Leer respuesta
			readBytes = in.read(buffer);
			System.out.println(new String(buffer, 0, readBytes));
		}
	}

	
	public static class Coin {
		int value;
		Mat image;
		
		public Coin(int value, String filename) {
			this.value = value;
			this.image = Highgui.imread(filename);
		}
	}
}
